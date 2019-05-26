package RemoteControlOnServerFileSystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadServer {

    public static void main(String[] args) {

        // server osluskuje zahteve na portu 5000
        try(ServerSocket serverSocket = new ServerSocket(50000)){

            while (true){
                Socket socket = serverSocket.accept(); // server prihvata zahtev
                RemoteController remoteController = new RemoteController(socket); // server kreira kontroler dolazecih zahteva koje obradjuje
                remoteController.start(); // pokrece se nit kontrolera za odredjenog klijenta
            }

        } catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }
    }
}
