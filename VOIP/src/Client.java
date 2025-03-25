import java.io.*;
import java.lang.constant.DynamicCallSiteDesc;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.text.Style;

import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;



public class Client {
    //static String serverIP = "25.3.220.57";
    static String serverIP = "127.0.0.1";
    static int port = 9000;

    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {

        //byte[] encoded = encoder.encode(rawAudio);


        OpusDecoder decoder = new OpusDecoder(48000, 1);
        decoder.setFrameSize(960);
        //short[] decoded = decoder.decode(encoded);



        AudioPlayer player = new AudioPlayer();
        


        int drop_count = 0;
        short[] decompressed;
        Socket socket = new Socket(serverIP, port);
        System.out.println("connected");
        InputStream in = socket.getInputStream();

        try {
            while(true) {
                byte[] buf = in.readNBytes(4);
                int psize = Util.readInt(buf);

                byte[] packet = new byte[psize];
                in.read(packet);

                try {
                    decompressed = decoder.decode(packet);
                    player.playAudio(decompressed);
                }catch (Exception e){
                    System.out.println(e);
                    System.out.println(packet.length);
                }
            }
        }catch (Exception e){
            System.out.println("UH OH");
        }
    }







    public static class AudioPlayer {

        private SourceDataLine line;
        private AudioFormat format;

        public AudioPlayer() throws LineUnavailableException {
            // Set up audio format (should match the recording format)
            format = new AudioFormat(48000, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
        }

        public void playAudio(short[] data) {
            byte[] audioBytes = new byte[data.length * 2]; // 2 bytes per short (16-bit)

            // Convert short[] to byte[] for playback
            for (int i = 0; i < data.length; i++) {
                audioBytes[2 * i] = (byte) (data[i] & 0xFF);
                audioBytes[2 * i + 1] = (byte) ((data[i] >> 8) & 0xFF);
            }

            // Play the audio data
            line.write(audioBytes, 0, audioBytes.length);
        }

        public void close() {
            if (line != null) {
                line.stop();
                line.close();
            }
        }
    }

}
