import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import voip.endpoints.MicrophoneHandler;
import voip.util.Util;

public class TwoWayClient {
    static String serverIP ;
    static int port;

    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {
        TwoWayClient client = new TwoWayClient();
        
    }
    public TwoWayClient() throws IOException, UnknownPlatformException, LineUnavailableException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("IP:");
        serverIP = scanner.nextLine();
        System.out.println("Port:");
        port = scanner.nextInt();
                
        OpusEncoder encoder = new OpusEncoder(48000, 1, OpusEncoder.Application.VOIP);
        encoder.setMaxPayloadSize(960);

        OpusDecoder decoder = new OpusDecoder(48000, 1);
        decoder.setFrameSize(960);


        AudioPlayer player = new AudioPlayer();
        MicrophoneHandler mic = new MicrophoneHandler();


        int drop_count = 0;
        short[] decompressed;
        Socket socket = new Socket(serverIP, port);
        System.out.println("connected");
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();
        
        
        

        try {
            

                
            MicReader micReader = new MicReader(mic, encoder, out);
            
            SocketReader socReader = new SocketReader(player, decoder, in);

            micReader.run();
            socReader.run();

                
            
        }catch (Exception e){
            System.out.println("UH OH");
        }
    }

    private class MicReader extends Thread{
        MicrophoneHandler mic;
        OpusEncoder encoder;
        OutputStream out;

        public MicReader(MicrophoneHandler mic, OpusEncoder encoder, OutputStream out){
            this.mic = mic;
            this.encoder = encoder;
            this.out = out;
        
        }

        public void run(){
            try{
                short[] data = mic.getAudioSample(960);
                byte[] compressed = encoder.encode(data);
                byte[] buf = Util.writeInt(compressed.length);

                out.write(buf);
                out.write(compressed,0,compressed.length);
                out.flush();
            }catch(Exception e){e.printStackTrace();}
        }
    }

    private class SocketReader extends Thread{
        AudioPlayer player;
        OpusDecoder decoder;
        InputStream in;

        public SocketReader(AudioPlayer player, OpusDecoder decoder, InputStream in){
            this.player = player;
            this.decoder = decoder;
            this.in = in;
        
        }

        public void run(){
            try{
                byte[] buf = in.readNBytes(4);
                int psize = Util.readInt(buf);

                byte[] packet = new byte[psize];
                in.read(packet);

                try {
                    short[] decompressed = decoder.decode(packet);
                    player.playAudio(decompressed);
                }catch (Exception e){
                    System.out.println(e);
                    System.out.println(packet.length);
                }
            }catch(Exception e){e.printStackTrace();}
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
