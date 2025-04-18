package example;

import java.net.ServerSocket;
import java.net.Socket;

import voip.AudioConnection;
import voip.Mixer;
import voip.endpoints.AudioPlayer;
import voip.endpoints.AudioReceiver;
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

            System.out.println("Connected");


            //AudioTransmitter audioTransmitter = new AudioTransmitter(client);
            AudioReceiver audioReceiver = new AudioReceiver(client);


            //AudioConnection connectionOut = new AudioConnection(mic, audioTransmitter);
            //AudioConnection connectionIn = new AudioConnection(audioReceiver, player);

            Mixer mixer = new Mixer();
            mixer.addSource(mic);
            mixer.addSource(audioReceiver);
            AudioConnection connectionIn = new AudioConnection(mixer, player);



            while(connectionIn.isAlive()){}
            socket.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
