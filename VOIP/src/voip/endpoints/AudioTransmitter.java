package voip.endpoints;

import voip.constants.AudioConstants;
import voip.constants.ChannelClosedException;
import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import voip.interfaces.AudioSink;
import voip.util.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class AudioTransmitter implements AudioSink {
    private final OutputStream out;
    private final OpusEncoder encoder;
    public AudioTransmitter(Socket s) throws IOException, UnknownPlatformException {
        encoder = new OpusEncoder(AudioConstants.SAMPLE_RATE, 1, OpusEncoder.Application.VOIP);
        encoder.setMaxPayloadSize(AudioConstants.FRAME_SIZE);
        out = s.getOutputStream();
    }

    @Override
    public void sendAudio(short[] data) throws ChannelClosedException {
        byte[] compressed = encoder.encode(data);
        byte[] sizeBuffer = Util.writeInt(compressed.length);
        try {
            out.write(sizeBuffer);
            out.write(compressed, 0, compressed.length);
            out.flush();
        }catch (Exception e){
            throw new ChannelClosedException();
        }
    }
}
