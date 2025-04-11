import de.maxhenkel.opus4j.OpusDecoder;
import de.maxhenkel.opus4j.UnknownPlatformException;
import voip.endpoints.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.FileInputStream;
import java.io.IOException;

public class Playback {
    public static AudioPlayer player;
    public static void main(String[] args) throws IOException, LineUnavailableException, UnknownPlatformException {
        player = new AudioPlayer();
        OpusDecoder decoder = new OpusDecoder(48000, 1);
        decoder.setFrameSize(960);
        try(FileInputStream fis = new FileInputStream("src.bin")) {
            try (FileInputStream fis2 = new FileInputStream("res.bin")) {
                int pnum = 0;
                while (true) {
                    byte[] packet = new byte[960];
                    int r = fis.read(packet);
                    if(r <= 0){ System.out.println("src eof"); System.exit(-1);}

                    byte[] p2 = new byte[960];
                    r = fis2.read(p2);
                    if(r <= 0){ System.out.println("res eof"); System.exit(-1);}

                    for (int i = 0; i < 960; i++) {
                        if (packet[i] != p2[i]) System.out.println(pnum + ": " + i + " - " + packet[i] + "/" + p2[i]);
                    }
                    System.out.println("------end of p" + pnum + "--------");
                    player.sendAudio(decoder.decode(packet));
                    pnum++;
                }
            }
        }
    }
}
