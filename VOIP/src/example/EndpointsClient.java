package example;

import java.net.Socket;

import voip.AudioConnection;
import voip.endpoints.AudioPlayer;
import voip.endpoints.AudioReceiver;
import voip.endpoints.AudioTransmitter;
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
            AudioTransmitter audioTransmitter = new AudioTransmitter(socket);


            AudioConnection connectionOut = new AudioConnection(mic, audioTransmitter);
            AudioConnection connectionIn = new AudioConnection(audioReceiver, player);

            while(connectionIn.isAlive() || connectionOut.isAlive()){}
            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
