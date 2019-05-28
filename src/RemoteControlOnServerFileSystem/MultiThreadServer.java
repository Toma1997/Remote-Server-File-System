package RemoteControlOnServerFileSystem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class MultiThreadServer {

    public static void main(String[] args) {

        // kolekcija vrednosti korisnika severskog fajl sistema
        HashMap<String, String> mapaKorisnika = new HashMap<>();
        mapaKorisnika.put("Toma97", "toma1997+");
        mapaKorisnika.put("Markoni", "mare+123!");
        mapaKorisnika.put("Nidza12", "tacrqwe+_12");
        mapaKorisnika.put("AnaM", "anci4568");
        mapaKorisnika.put("Roberto95", "robi_king23=");

        // server osluskuje zahteve na portu 5000
        try(ServerSocket serverSocket = new ServerSocket(50000)){

            while (true){
                Socket socket = serverSocket.accept(); // server prihvata zahtev
                RemoteController remoteController = new RemoteController(socket, mapaKorisnika); // server kreira kontroler dolazecih zahteva
                remoteController.start(); // pokrece se nit kontrolera za odredjenog klijenta
            }

        } catch (IOException e){
            System.out.println("Server exception " + e.getMessage());
        }
    }
}
