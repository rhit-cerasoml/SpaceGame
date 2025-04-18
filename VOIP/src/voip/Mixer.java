package voip;

import voip.constants.ChannelClosedException;
import voip.interfaces.AudioSource;

import java.util.ArrayList;

public class Mixer implements AudioSource {
    private ArrayList<AudioSource> sources = new ArrayList<>();
    public void addSource(AudioSource source){
        sources.add(source);
    }


    @Override
    public short[] getAudioSample(int bufferSize) throws ChannelClosedException {
        short[] res = new short[bufferSize];
        for(AudioSource source : sources){
            short[] sample = source.getAudioSample(bufferSize);
            for(int i = 0; i < bufferSize; i++){
                res[i] += sample[i];
            }
        }
        return res;
    }
}
