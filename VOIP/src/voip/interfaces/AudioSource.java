package voip.interfaces;

import voip.constants.ChannelClosedException;

public interface AudioSource {
    short[] getAudioSample(int bufferSize) throws ChannelClosedException;
}
