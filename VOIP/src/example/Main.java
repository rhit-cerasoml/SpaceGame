package example;

import voip.constants.AudioConstants;
import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import voip.endpoints.MicrophoneHandler;
import voip.util.Util;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;


public class Main {

    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {
        int streamLen = AudioConstants.FRAME_SIZE;
        OpusEncoder encoder = new OpusEncoder(AudioConstants.SAMPLE_RATE, 1, OpusEncoder.Application.VOIP);
        encoder.setMaxPayloadSize(streamLen);

        ServerSocket socket = new ServerSocket(9000);
        Socket client = socket.accept();
        OutputStream out = null;
        out = client.getOutputStream();

        MicrophoneHandler mic = new MicrophoneHandler();

        try {

            while(true) {
                short[] data = mic.getAudioSample(streamLen);
                byte[] compressed = encoder.encode(data);
                //player.playAudio(decoded);
                byte[] buf = Util.writeInt(compressed.length);

                out.write(buf);
                out.write(compressed,0,compressed.length);
                out.flush();
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("UH OH");
        }
    }


}