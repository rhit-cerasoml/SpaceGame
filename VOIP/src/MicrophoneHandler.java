import javax.sound.sampled.*;

public class MicrophoneHandler {

    private TargetDataLine line;
    private AudioFormat format;
    private DataLine.Info info;
    private byte[] audioBuffer;

    public MicrophoneHandler() {
        // Set up audio format (e.g., 16-bit mono at 44.1 kHz)
        format = new AudioFormat(48000, 16, 1, true, false);
        info = new DataLine.Info(TargetDataLine.class, format);
    }

    public void open() throws LineUnavailableException {
        // Open the line to capture the audio
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
    }

    public short[] getAudioSample(int bufferSize){
        audioBuffer = new byte[bufferSize];

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