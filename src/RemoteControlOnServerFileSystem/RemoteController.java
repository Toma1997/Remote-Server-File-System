package RemoteControlOnServerFileSystem;

// ovo je jedna nit za jednog klijenta na serveru

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class RemoteController extends  Thread{

    private Socket socket;
    private Directory fileSystemRoot;
    private HashMap<String, String> mapaKorisnika;

    public RemoteController(Socket socket, HashMap mapa){
        this.socket=socket;
        this.mapaKorisnika = mapa;
    }

    @Override
    public void run(){

        try{

            System.out.println("Client connected...");
            // kreiran tok za unos kome se podaci salju na server
            BufferedReader serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(),true); // tok za izlaz servera prema klijentu za odogovore

            String [] loginList; // login podaci sto se salju od klijenta
            do{
                loginList = serverInput.readLine().trim().split(",");
                if(!this.mapaKorisnika.containsKey(loginList[0]) || !this.mapaKorisnika.get(loginList[0]).equals(loginList[1])){
                    serverOutput.println("Wrong login. Try again!");
                } else if (this.mapaKorisnika.containsKey(loginList[0]) && this.mapaKorisnika.get(loginList[0]).equals(loginList[1])){
                    serverOutput.println("Correct!");
                    break;
                }
            } while (true);

            // svaki korsinik ima svoj root folder fajl sistema
            fileSystemRoot = new Directory(loginList[0], null);
            Directory currentDirectory = fileSystemRoot;
            while (true){

                String command = serverInput.readLine(); // pristigla komanda od klijenta

                if(command.equals("exit")){
                    serverOutput.println("You are disconnected from the server!");
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
                            serverOutput.println("Number of files: " + numOfFilesInDir);
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
                            if(resp.equals("")){
                                resp = "No files and directories!";
                            }
                            serverOutput.println(resp);
                            break;

                        case "touch":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the file!");
                            } else {
                                if(totalCommands[1].matches("[A-z]+[A-z0-9]*([.-]?[A-z0-9])*")){ // regex za naziv fajla
                                    File file = new File(totalCommands[1], currentDirectory, 200);
                                    if(currentDirectory.addEntry(file)){
                                        serverOutput.println("File " + totalCommands[1] + " is successfully created!");
                                    } else {
                                        serverOutput.println("File " + totalCommands[1] + " already exists!");
                                    }
                                } else {
                                    serverOutput.println("Name of the file should begin with alphabet chars and can only have . and - wildchars!");
                                }

                            }
                            break;

                        case "rm":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the file!");
                            } else {
                                File file1 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                if (file1 != null) {
                                    if (currentDirectory.deleteEntry(file1)) {
                                        serverOutput.println("File " + totalCommands[1] + " is successfully deleted!");
                                    } else {
                                        serverOutput.println("File " + totalCommands[1] + " is not found!");
                                    }
                                } else {
                                    serverOutput.println("File " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "mkdir":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the directory!");
                            } else {
                                if(totalCommands[1].matches("[A-z]+[A-z0-9]*([.-]?[A-z0-9])*")){
                                    Directory dir = new Directory(totalCommands[1], currentDirectory);
                                    if (currentDirectory.addEntry(dir)) {
                                        serverOutput.println("Directory " + totalCommands[1] + " is successfully created!");
                                    } else {
                                        serverOutput.println("Directory " + totalCommands[1] + " already exists!");
                                    }
                                } else {
                                    serverOutput.println("Name of the directory should begin with alphabet chars and can only have . and - wildchars!");

                                }

                            }
                            break;

                        case "rmdir":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the directory!");
                            } else {
                                // ako zelimo obrisati folder u kom se nalazimo
                                if(totalCommands[1].equals(currentDirectory.getName())){
                                    if(currentDirectory.delete()){
                                        currentDirectory = currentDirectory.parent; // vrati se u roditeljski folder
                                        serverOutput.println("Directory " + totalCommands[1] + " is successfully deleted! You are moved in parent directory!");
                                    } else { // ako pokusavamo obrisati root folder
                                        serverOutput.println("Directory " + totalCommands[1] + " is root directory, and you can't delete it!");
                                    }
                                } else {
                                    Directory dir1 = (Directory) currentDirectory.getEntryByName(totalCommands[1], "Directory");
                                    if (dir1 != null) {
                                        if (currentDirectory.deleteEntry(dir1)) {
                                            serverOutput.println("Directory " + totalCommands[1] + " is successfully deleted!");
                                        } else {
                                            serverOutput.println("Directory " + totalCommands[1] + " is not found!");
                                        }
                                    } else {
                                        serverOutput.println("Directory " + totalCommands[1] + " is not found!");
                                    }
                                }
                            }
                            break;

                        case "cat":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the file!");
                            } else {
                                File file2 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                if (file2 != null) {
                                    String content = file2.getContents();
                                    serverOutput.println(content);
                                } else {
                                    serverOutput.println("File " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "write":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the file!");
                            } else {
                                File file3 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                if (file3 != null) {
                                    String content1 = "";
                                    serverOutput.println("File is opened for writing! to save enter: save_file, to exit enter: exit_file.");
                                    String command2;
                                    do {
                                        command2 = serverInput.readLine();
                                        if (command2.equals("save_file")) {
                                            file3.setContent(content1);
                                            serverOutput.println("File is overwritten with new content!");
                                        } else if (!command2.equals("exit_file")) {
                                            content1 += command2 + "/;";
                                        }

                                    } while (!command2.equals("exit_file"));
                                    serverOutput.println("Exited the file!");

                                } else {
                                    serverOutput.println("File " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "append":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the file!");
                            } else {
                                File file4 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                if (file4 != null) {
                                    String content2 = "";
                                    serverOutput.println("File is opened for appending! to save enter: save_file, to exit enter: exit_file.");
                                    String command3;
                                    do {
                                        command3 = serverInput.readLine();
                                        if (command3.equals("save_file")) {
                                            file4.appendContent(content2);
                                            serverOutput.println("Appended new content!");
                                        } else if (!command3.equals("exit_file")) {
                                            content2 += command3 + "/;";
                                        }

                                    } while (!command3.equals("exit_file"));
                                    serverOutput.println("Exited the file!");

                                } else {
                                    serverOutput.println("File " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "rename":
                            if(totalCommands.length < 3){
                                serverOutput.println("Specify the current file name and new!");
                            } else {
                                File file5 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                if (file5 != null) {
                                    file5.changeName(totalCommands[2]);
                                    serverOutput.println("File " + totalCommands[1] + " changed name to " + totalCommands[2]);
                                } else {
                                    serverOutput.println("File " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "cd":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the new path to change location!");
                            } else {
                                if (totalCommands[1].charAt(totalCommands[1].length() - 1) != '/') {
                                    serverOutput.println("Put / at the end of new path");
                                } else {
                                    currentDirectory = currentDirectory.changeDirectory(currentDirectory, totalCommands[1]);
                                    serverOutput.println("Your current location has changed!");
                                }
                            }
                            break;

                        case "details":
                            if(totalCommands.length < 2){
                                serverOutput.println("Specify the file!");
                            } else {
                                File file6 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                if (file6 != null) {
                                    serverOutput.println("File created: "+file6.getCreationTime()+",File last accessed: "+file6.getLastAccessedTime()+",File last updated: "+file6.getLastUpdatedTime()+",");
                                } else {
                                    serverOutput.println("File " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "mv":
                            if(totalCommands.length < 3){
                                serverOutput.println("Specify the name and new location of the entry!");
                            } else {
                                File file7 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                Directory dir2 = (Directory) currentDirectory.getEntryByName(totalCommands[1], "Directory");
                                if (file7 != null) {
                                    if(currentDirectory.moveEntry(currentDirectory, file7, totalCommands[2])){
                                        serverOutput.println("File " + totalCommands[1] + " is moved to another location!");
                                    } else {
                                        serverOutput.println("File " + totalCommands[1] + " is not moved to another location!");
                                    }

                                } else if (dir2 != null) {
                                    if(currentDirectory.moveEntry(currentDirectory, dir2, totalCommands[2])){
                                        serverOutput.println("Directory " + totalCommands[1] + " is moved to another location!");
                                    } else {
                                        serverOutput.println("Directory " + totalCommands[1] + " is not moved to another location!");
                                    }

                                } else {
                                    serverOutput.println("Entry " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "cp":
                            if(totalCommands.length < 3){
                                serverOutput.println("Specify the name and new location of the entry!");
                            } else {
                                File file8 = (File) currentDirectory.getEntryByName(totalCommands[1], "File");
                                Directory dir3 = (Directory) currentDirectory.getEntryByName(totalCommands[1], "Directory");
                                if (file8 != null) {
                                    if(currentDirectory.copyEntry(currentDirectory, file8, totalCommands[2])){
                                        serverOutput.println("File " + totalCommands[1] + " is copied to another location!");
                                    } else {
                                        serverOutput.println("File " + totalCommands[1] + " is not copied to another location!");
                                    }

                                } else if (dir3 != null) {
                                    if(currentDirectory.copyEntry(currentDirectory, dir3, totalCommands[2])){
                                        serverOutput.println("Directory " + totalCommands[1] + " is copied to another location!");
                                    } else {
                                        serverOutput.println("Directory " + totalCommands[1] + " is not copied to another location!");
                                    }

                                } else {
                                    serverOutput.println("Entry " + totalCommands[1] + " is not found!");
                                }
                            }
                            break;

                        case "help":
                            String helpString = "- pwd : Print current directory location /;";
                            helpString += "- num_files : Number of files in current directory, recursevly /;";
                            helpString += "- ls : List all files and directories in current directory /;";
                            helpString += "- touch <file_name> : create empty file /;";
                            helpString += "- rm <file_name> : remove file /;";
                            helpString += "- mkdir <directory_name> : create directory /;";
                            helpString += "- rmdir <directory_name> : remove directory, and everything in it /;";
                            helpString += "- cat <file_name> : Print file content /;";
                            helpString += "- write <file_name : Write or overwrite file content /;";
                            helpString += "- append <file_name> : Append new content to file /;";
                            helpString += "- rename <old_file_name> <new_file_name : Rename file with new name /;";
                            helpString += "- cd <new_relative_path> : Change current location by giving relative path /;";
                            helpString += "- details <file_name> : File details /;";
                            helpString += "- mv <entry_name> <new_relative_path> : Move file or directory to a new location /;";
                            helpString += "- cp <entry_name> <another_relative_path> : Copy file or directory to another location /;";
                            helpString += "- exit : Disconnect from the server /;";

                            serverOutput.println(helpString);


                        default:
                            serverOutput.println("There is no such command in this file system! Type 'help' to see commands");
                            break;
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
