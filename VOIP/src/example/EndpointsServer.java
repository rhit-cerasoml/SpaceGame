package example;

import java.net.ServerSocket;
import java.net.Socket;

import voip.AudioConnection;
import voip.endpoints.AudioPlayer;
import voip.endpoints.AudioTransmitter;
import voip.endpoints.MicrophoneHandler;

public class EndpointsServer {
    public static int port = 9000;
    
    public static void main(String[] args) {
        try {

            MicrophoneHandler mic = new MicrophoneHandler();

            AudioPlayer player = new AudioPlayer();

            ServerSocket socket = new ServerSocket(port);
            Socket client = socket.accept();
            AudioTransmitter audioTransmitter = new AudioTransmitter(client);
            
            AudioConnection connection = new AudioConnection(mic, audioTransmitter);



            while(true){}

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
