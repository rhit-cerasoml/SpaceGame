package voip.interfaces;

import voip.constants.ChannelClosedException;

public interface AudioSink {
    void sendAudio(short[] data) throws ChannelClosedException;
}
