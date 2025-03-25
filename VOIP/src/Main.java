import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.opus4j.UnknownPlatformException;

import javax.sound.sampled.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import java.io.InputStream;
import java.io.OutputStream;


public class Main {

    static int port = 9000;


    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {
        int streamLen = 960;
        OpusEncoder encoder = new OpusEncoder(48000, 1, OpusEncoder.Application.VOIP);
        encoder.setMaxPayloadSize(960);
        //encoder.resetState();
        //encoder.close();

        OpusDecoder decoder = new OpusDecoder(48000, 1);
        decoder.setFrameSize(960);

        ServerSocket socket = new ServerSocket(port);
        Socket client = socket.accept();
        OutputStream out = null;
        out = client.getOutputStream();



        MicrophoneHandler mic = new MicrophoneHandler();
        AudioPlayer player = new AudioPlayer();
        mic.open();
        int drop_count = 0;
        short[] decompressed;

        try {

            while(true) {
                short[] data = mic.getAudioSample(960);
                byte[] compressed = encoder.encode(data);
                short[] decoded = decoder.decode(compressed);
                //player.playAudio(decoded);
                byte[] buf = Util.writeInt(compressed.length);

                out.write(buf);
                out.write(compressed,0,compressed.length);
                out.flush();
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
