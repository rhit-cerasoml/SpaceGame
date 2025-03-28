import de.maxhenkel.opus4j.OpusEncoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;


public class Main {

    public static final int SAMPLE_RATE = 48000; // 48000
    public static final int FRAME_SIZE = 960;
    static int port = 9000;
    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {
        int streamLen = FRAME_SIZE;
        OpusEncoder encoder = new OpusEncoder(SAMPLE_RATE, 1, OpusEncoder.Application.VOIP);
        encoder.setMaxPayloadSize(streamLen);

        ServerSocket socket = new ServerSocket(port);
        Socket client = socket.accept();
        OutputStream out = null;
        out = client.getOutputStream();

        MicrophoneHandler mic = new MicrophoneHandler();
        mic.open();

        try {

            while(true) {
                short[] data = mic.getAudioSample(streamLen);
                byte[] compressed = encoder.encode(data);
                //player.playAudio(decoded);
                byte[] buf = Util.writeInt(compressed.length);

                out.write(buf);
                out.write(compressed,0,compressed.length);
                out.flush();
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("UH OH");
        }
    }

    public static class AudioPlayer {

        private SourceDataLine line;
        private AudioFormat format;

        public AudioPlayer() throws LineUnavailableException {
            // Set up audio format (should match the recording format)
            format = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
        }

        public void playAudio(short[] data) {
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
}