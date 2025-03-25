import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.LineUnavailableException;

import de.maxhenkel.opus4j.UnknownPlatformException;

import java.io.InputStream;
import java.io.OutputStream;



public class ServerRelay extends Thread{
    int port1;
    int port2;

    public static void main(String[] args) throws IOException, UnknownPlatformException, LineUnavailableException {
        ServerRelay serverRelay = new ServerRelay(9000,9001);
        
    }
    

    public ServerRelay(int port1, int port2) throws IOException, UnknownPlatformException, LineUnavailableException{
        this.port1 = port1;
        this.port2 = port2;

        ServerSocket socket1 = new ServerSocket(port1);
        Socket client1 = socket1.accept();
        OutputStream out1 = client1.getOutputStream();
        InputStream in1 = client1.getInputStream();


        ServerSocket socket2 = new ServerSocket(port2);
        Socket client2 = socket2.accept();
        OutputStream out2 = client2.getOutputStream();
        InputStream in2 = client1.getInputStream();

        try {

            while(true) {
                SocketThread socketThread = new SocketThread(in1, out2);
                SocketThread socketThread2 = new SocketThread(in2, out1);
                while(socketThread.isAlive() && socketThread2.isAlive()){}
            }
        }catch (Exception e){
            System.out.println("UH OH");
        }
    }


    public class SocketThread extends Thread{
        InputStream in;
        OutputStream out;

        public SocketThread(InputStream in, OutputStream out){
            this.in = in;
            this.out = out;
        }

        public void run(){
            try{
                clientPassthrough(in, out);
            }catch(Exception e){e.printStackTrace();}
        }

        public void clientPassthrough(InputStream in, OutputStream out) throws IOException, UnknownPlatformException, LineUnavailableException{
            while(true) {
                byte[] buf = in.readNBytes(4);
                int psize = Util.readInt(buf);
                byte[] packet = new byte[psize];
                in.read(packet);
    
                //Future Audio Magic
    
                out.write(buf);
                out.write(packet,0,packet.length);
                out.flush();
            }
        }
    }

    
}