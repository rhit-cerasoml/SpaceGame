package example;

import java.net.Socket;

import voip.AudioConnection;
import voip.endpoints.AudioPlayer;
import voip.endpoints.AudioReceiver;
import voip.endpoints.MicrophoneHandler;

public class EndpointsClient {
    static int port = 9000;
    static String serverIP = "127.0.0.1";

    public static void main(String[] args) {
        try {


            MicrophoneHandler mic = new MicrophoneHandler();

            AudioPlayer player = new AudioPlayer();

            Socket socket = new Socket(serverIP, port);

            AudioReceiver audioReceiver = new AudioReceiver(socket);
            
            AudioConnection connection = new AudioConnection(audioReceiver, player);

            while(true){}

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
