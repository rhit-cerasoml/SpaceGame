package example;

import java.io.*;
import java.net.Socket;

import javax.sound.sampled.LineUnavailableException;

import voip.constants.AudioConstants;
import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import voip.endpoints.AudioPlayer;
import voip.util.Util;


public class Client {
    //static String serverIP = "25.3.220.57";
    static String serverIP = "127.0.0.1";
    static int port = 9000;
    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {

        //byte[] encoded = encoder.encode(rawAudio);


        OpusDecoder decoder = new OpusDecoder(AudioConstants.SAMPLE_RATE, 1);
        decoder.setFrameSize(AudioConstants.FRAME_SIZE);
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
                    player.sendAudio(decompressed);
                }catch (Exception e){
                    System.out.println(e);
                    System.out.println(packet.length);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("UH OH");
        }
    }



}
