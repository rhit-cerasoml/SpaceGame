package example;

import voip.AudioConnection;
import voip.endpoints.AudioPlayer;
import voip.endpoints.MicrophoneHandler;

public class EndpointsExample {
    public static void main(String[] args) {
        try {
            MicrophoneHandler mic = new MicrophoneHandler();

            AudioPlayer player = new AudioPlayer();

            AudioConnection connection = new AudioConnection(mic, player);

            while(true){}

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
