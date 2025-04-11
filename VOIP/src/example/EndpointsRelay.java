package example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import voip.AudioConnection;
import voip.constants.AudioConstants;
import voip.constants.ChannelClosedException;
import voip.endpoints.AudioPlayer;
import voip.endpoints.AudioReceiver;
import voip.endpoints.AudioTransmitter;
import voip.endpoints.MicrophoneHandler;

public class EndpointsRelay {
    static int port = 9000;
    static String serverIP = "127.0.0.1";

    public static void main(String[] args) {
        try {
            int clientCount=0;
            ArrayList<Client> clients = new ArrayList<>();
            ServerSocket socket = new ServerSocket(port);
            
            while(true){
                Socket clientSocket = socket.accept();
                System.out.println("Client accepted.");
                clientCount++;
                Client client = new Client(clientCount, clientSocket);
                for(int i = 0; i < clients.size(); i++){
                    Client otherClient = clients.get(i);
                    client.addConnection(otherClient);
                    otherClient.addConnection(client);
                    System.out.println("Clients " + (clientCount) + " and" + otherClient.id + " connected." );
                }
                clients.add(client);
                client.start();
                System.out.println("Client " + clientCount + " added.");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    static class Client extends Thread{
        int id;
        Socket socket;
        AudioReceiver audioReceiver;
        AudioTransmitter audioTransmitter;
        ArrayList<Client> clients;

        public Client(int id, Socket socket){
            this.id = id;
            try {
                audioTransmitter = new AudioTransmitter(socket);
                audioReceiver = new AudioReceiver(socket);
                clients = new ArrayList<Client>();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void addConnection(Client client){
            if(client.id!=this.id){
                clients.add(client);
            }
        }

        @Override
        public void run() {
            while(true){
                for(int i = 0; i < clients.size(); i ++){
                    try {
                        this.clients.get(i).audioTransmitter.sendAudio(this.audioReceiver.getAudioSample(AudioConstants.FRAME_SIZE));
                    } catch (ChannelClosedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }


    }
}
