package voip.endpoints;

import voip.constants.AudioConstants;
import voip.constants.ChannelClosedException;
import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import voip.interfaces.AudioSource;
import voip.util.Util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class AudioReceiver implements AudioSource {
    private final InputStream in;
    private final OpusDecoder decoder;
    public AudioReceiver(Socket s) throws IOException, UnknownPlatformException {
        decoder = new OpusDecoder(AudioConstants.SAMPLE_RATE, 1);
        decoder.setFrameSize(AudioConstants.FRAME_SIZE);
        in = s.getInputStream();
    }
    @Override
    public short[] getAudioSample(int bufferSize) throws ChannelClosedException {
        try {
            byte[] buf = in.readNBytes(4);
            int psize = Util.readInt(buf);

            byte[] packet = new byte[psize];
            in.read(packet);

            return decoder.decode(packet);
        }catch (Exception e){
            throw new ChannelClosedException();
        }
    }
}
