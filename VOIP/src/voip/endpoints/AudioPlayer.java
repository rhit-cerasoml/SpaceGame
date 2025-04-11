package voip.endpoints;

import voip.constants.AudioConstants;
import voip.interfaces.AudioSink;

import javax.sound.sampled.*;

public class AudioPlayer implements AudioSink {

    private SourceDataLine line;
    private AudioFormat format;

    public AudioPlayer() throws LineUnavailableException {
        // Set up audio format (should match the recording format)
        format = new AudioFormat(AudioConstants.SAMPLE_RATE, 16, 1, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
    }

    public void sendAudio(short[] data) {
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

