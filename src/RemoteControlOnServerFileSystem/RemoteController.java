package RemoteControlOnServerFileSystem;

// ovo je jedna nit za jednog klijenta na serveru

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class RemoteController extends  Thread{

    private Socket socket;
    private Directory fileSystemRoot;

    public RemoteController(Socket socket){
        this.socket=socket;
    }

    @Override
    public void run(){

        try{

            System.out.println("Client connected...");
            // kreiran tok za unos kome se podaci salju na server
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(),true); // tok za izlaz servera prema klijentu za odogovore

            String userName = serverInput.readLine();

            // svaki korsinik ima svoj root folder fajl sistema
            fileSystemRoot = new Directory(userName + "/", null);
            Directory currentDirectory = fileSystemRoot;
            while (true){

                String command = serverInput.readLine(); // pristigla komanda od klijenta

                if(command.equals("exit")){
                    serverOutput.println("You are disconnected from server!");
                    break; // prekini konekciju na udaljeni fajl sistem

                } else {
                    String [] totalCommands = command.split(" ");
                    switch(totalCommands[0]){
                        case "pwd":
                            String responseOnCommand = currentDirectory.getFullPath();
                            serverOutput.println(responseOnCommand); // salje se odgovor servera
                            break;

                        case "num_files":
                            int numOfFilesInDir = currentDirectory.numberOfFiles();
                            serverOutput.println("Broj fajlova u trenutnom folderu: " + numOfFilesInDir);
                            break;

                        case "ls":
                            ArrayList<Entry> filesAndSubDirectories = currentDirectory.getFilesAndSubDirectories();
                            String resp = "";
                            for(Entry e : filesAndSubDirectories){
                                if(e instanceof Directory){
                                    resp += "./" + e.name + " " + e.size() + "B,";
                                } else if (e instanceof File){
                                    resp += e.name + " " + e.size() + "B,";
                                }
                            }
                            serverOutput.println(resp);
                            break;

                        case "touch":
                            File file = new File(totalCommands[1], currentDirectory, 200);
                            if(currentDirectory.addEntry(file)){
                                serverOutput.println("File " + totalCommands[1] + " is successfully created!");
                            } else {
                                serverOutput.println("File " + totalCommands[1] + " already exists!");
                            }

                            break;

                        case "rm":
                            File file1 = (File) currentDirectory.getEntryByName(totalCommands[1]);
                            if(currentDirectory.deleteEntry(file1)){
                                serverOutput.println("File " + totalCommands[1] + " is successfully deleted!");
                            } else {
                                serverOutput.println("File " + totalCommands[1] + " is not found!");
                            }

                            break;

                        case "mkdir":
                            Directory dir = new Directory(totalCommands[1], currentDirectory);
                            if(currentDirectory.addEntry(dir)){
                                serverOutput.println("Directory " + totalCommands[1] + " is successfully created!");
                            } else {
                                serverOutput.println("Directory " + totalCommands[1] + " already exists!");
                            }
                            break;

                        case "rmdir":
                            Directory dir1 = (Directory) currentDirectory.getEntryByName(totalCommands[1]);
                            if(currentDirectory.deleteEntry(dir1)){
                                serverOutput.println("Directory " + totalCommands[1] + " is successfully deleted!");
                            } else {
                                serverOutput.println("Directory " + totalCommands[1] + " is not found!");
                            }
                            break;

                        case "cat":
                            File file2 = (File) currentDirectory.getEntryByName(totalCommands[1]);
                            if(file2 != null){
                                String content = file2.getContents();
                                serverOutput.println(content);
                            } else {
                                serverOutput.println("File " + totalCommands[1] + " is not found!");
                            }
                            break;

                        case "write":
                            File file3 = (File) currentDirectory.getEntryByName(totalCommands[1]);
                            if(file3 != null){
                                String content = file3.getContents();
                                serverOutput.println("File is opened for writing!\n to save enter: save_file\n to exit enter: exit_file\n\n" + content);
                                String command2;
                                do{
                                    command2 = serverInput.readLine();
                                    if(command2.equals("save_file")){
                                        file3.setContent(content);
                                    } else if(!command2.equals("exit_file")) {
                                        content += command2;
                                    }

                                } while(!command2.equals("exit_file"));

                            } else {
                                serverOutput.println("File " + totalCommands[1] + " is not found!");
                            }
                            break;

                        case "append":
                            File file4 = (File) currentDirectory.getEntryByName(totalCommands[1]);
                            if(file4 != null){
                                String content1 = file4.getContents();
                                serverOutput.println("File is opened for appending!\n to save enter: save_file\n to exit enter: exit_file\n\n" + content1);
                                String command3;
                                do{
                                    command3 = serverInput.readLine();
                                    if(command3.equals("save_file")){
                                        file4.appendContent(content1);
                                    } else if(!command3.equals("exit_file")) {
                                        content1 += command3;
                                    }

                                } while(!command3.equals("exit_file"));

                            } else {
                                serverOutput.println("File " + totalCommands[1] + " is not found!");
                            }
                            break;

                        default:
                            serverOutput.println("There is no such command in this file system!");
                    }
                }
            }

        } catch (IOException e){
            System.out.println("Error in input/output: " + e.getMessage());
        }

        finally {

            try{
                socket.close(); // zatvori konekciju

            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
