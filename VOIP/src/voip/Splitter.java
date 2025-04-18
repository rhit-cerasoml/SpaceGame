package voip;

import voip.constants.ChannelClosedException;
import voip.interfaces.AudioSink;
import voip.interfaces.AudioSource;

public class Splitter implements AudioSink {
    @Override
    public void sendAudio(short[] data) throws ChannelClosedException {
        
    }
}
