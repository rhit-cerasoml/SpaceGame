package voip.endpoints;

import voip.constants.AudioConstants;
import voip.interfaces.AudioSource;

import javax.sound.sampled.*;

public class MicrophoneHandler implements AudioSource {

    private TargetDataLine line;
    private AudioFormat format;
    private DataLine.Info info;
    private byte[] audioBuffer;

    public MicrophoneHandler() throws LineUnavailableException {
        // Set up audio format (e.g., 16-bit mono at 44.1 kHz)
        format = new AudioFormat(AudioConstants.SAMPLE_RATE, 16, 1, true, false);
        info = new DataLine.Info(TargetDataLine.class, format);
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
    }

    public short[] getAudioSample(int bufferSize){
        audioBuffer = new byte[bufferSize * 2];

        // Continuously read from the microphone into the audio buffer
        int bytesRead = line.read(audioBuffer, 0, audioBuffer.length);

        // Return the portion of the buffer that was filled with data

        short[] shortData = new short[bytesRead / 2];
        for (int i = 0; i < bytesRead / 2; i++) {
            shortData[i] = (short) ((audioBuffer[2 * i + 1] << 8) | (audioBuffer[2 * i] & 0xFF));
        }

        return shortData;
    }

    public void close() {
        if (line != null) {
            line.stop();
            line.close();
        }
    }
}