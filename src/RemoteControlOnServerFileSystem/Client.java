package RemoteControlOnServerFileSystem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {

        // kreiranje soketa (ip adresa + port)
        try (Socket socket = new Socket("localhost", 50000)) {

            BufferedReader serverOutput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverInput = new PrintWriter(socket.getOutputStream(),true);


            Scanner clientInput = new Scanner(System.in);
            String command;
            String response;

            // kolekcija vrednosti korisnika severskog fajl sistema
            HashMap<String, String> mapaKorisnika = new HashMap<>();
            mapaKorisnika.put("Toma97", "toma1997+");
            mapaKorisnika.put("Markoni", "mare+123!");
            mapaKorisnika.put("Nidza12", "tacrqwe+_12");
            mapaKorisnika.put("AnaM", "anci4568");
            mapaKorisnika.put("Roberto95", "robi_king23=");

            String userName = "";
            String password = "";
            do{
                System.out.println("Enter your username:");
                userName = clientInput.nextLine().trim();

                System.out.println("Enter your password:");
                password = clientInput.nextLine().trim();

                if(!mapaKorisnika.containsKey(userName) || !mapaKorisnika.get(userName).equals(password)){
                    System.out.println("Wrong login. Try again!");
                } else if (mapaKorisnika.containsKey(userName) && mapaKorisnika.get(userName).equals(password)){
                    break;
                }

            } while (true);

            System.out.println("You are logged in!");
            serverInput.println(userName); // slanje imena za kreiranje root foldera

            do{
                System.out.println("Enter command to be executed on your File System: ");
                command = clientInput.nextLine();
                serverInput.println(command);

                if(command.equals("ls")){
                    response = serverOutput.readLine();
                    String [] responseList = response.split(",");
                    for(String elem: responseList){
                        System.out.println(elem);
                    }

                } else if(command.equals("cat")){ // ispis sadrzaja iz fajla
                    response = serverOutput.readLine();
                    String [] responseList = response.split(",");
                    for(String elem: responseList){
                        System.out.println(elem);
                    }

                } else if(command.equals("write")){ // upis sadzraja u fajl (prepisuje postojeci)
                    response = serverOutput.readLine();
                    System.out.println(response);
                    String command2;
                    do{
                        command2 = clientInput.nextLine();
                    } while(command2 != "exit_file");

                } else if(!command.equals("exit")){
                    response = serverOutput.readLine();
                    System.out.println(response);

                }
            }

            while (!command.equals("exit"));

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}