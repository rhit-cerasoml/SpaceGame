package voip;

import voip.constants.AudioConstants;
import voip.constants.ChannelClosedException;
import voip.interfaces.AudioSink;
import voip.interfaces.AudioSource;

public class AudioConnection extends Thread {
    private AudioSource source;
    private AudioSink sink;
    public AudioConnection(AudioSource source, AudioSink sink){
        this.source = source;
        this.sink = sink;
        this.start();
    }

    @Override
    public void run() {
        while(true){
            try {
                this.sink.sendAudio(this.source.getAudioSample(AudioConstants.FRAME_SIZE));
            } catch (ChannelClosedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
