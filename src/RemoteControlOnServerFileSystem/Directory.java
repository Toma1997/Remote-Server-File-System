package RemoteControlOnServerFileSystem;

import java.util.ArrayList;

public class Directory extends Entry {
    protected ArrayList<Entry> filesAndSubDirectories;

    public Directory(String name, Directory parent){
        super(name, parent);
        this.filesAndSubDirectories = new ArrayList<>();
    }

    @Override
    public int size() {
        int size = 0;
        for(Entry e: this.filesAndSubDirectories){
            if(e instanceof Directory) {
                Directory dir = (Directory) e;
                size += dir.size();
            } else if (e instanceof File){
                size += e.size();
            }
        }
        return size;
    }

    // prikazuje broj fajlova u folderu
    public int numberOfFiles(){
        int count = 0;
        for(Entry e: this.filesAndSubDirectories){
            if(e instanceof Directory){
                Directory dir = (Directory) e;
                count += dir.numberOfFiles();
            } else if (e instanceof File){
                count++;
            }
        }
        return count;
    }

    // prema imenu izvlaci folder ili fajl
    public Entry getEntryByName(String name, String type){
        for(Entry e: this.filesAndSubDirectories){
            if(e.getName().equals(name)){
                if((type.equals("File") && e instanceof File) || (type.equals("Directory") && e instanceof Directory)){
                    return e;
                }
            }
        }
        return null;
    }

    // brise folder ili fajl - IMA BUG, nece nista da brise sad
    public boolean deleteEntry(Entry entry){
        if(!this.entryExists(entry)){
            return false;
        }
        this.filesAndSubDirectories.remove(entry);
        return true;

    }

    // dodaje fajl ili folder
    public boolean addEntry(Entry entry){
        if(this.entryExists(entry)){
            return false;
        }
        this.filesAndSubDirectories.add(entry);
        return true;

    }

    // ako vec psotoji isti fajl ili folder
    public boolean entryExists(Entry entry){
        for(Entry e: this.filesAndSubDirectories){
            if(e.getName().equals(entry.getName())){
                if((e instanceof File && entry instanceof File) || (e instanceof Directory && entry instanceof Directory)){
                    return true;
                }
            }
        }
        return false;
    }

    // promeni trenutni direktorijum
    public Directory changeDirectory(Directory currentDir, String newPath){
        if(newPath.length() == 0){
            return currentDir;
        } else {
            if(newPath.substring(0, 3).equals("../")) { // ako se treba preci nazad na roditeljski folder
                if(currentDir.parent == null){ // ako je koreni folder trenutni vrati njega
                    return currentDir;
                }
                return changeDirectory(currentDir.parent, newPath.substring(3));
            } else {
                // izvuci samo ime sledeceg foldera
                Directory newDir = (Directory) currentDir.getEntryByName(newPath.substring(0, newPath.indexOf("/")), "Directory");
                if(newDir == null){
                    return currentDir;
                }
                return changeDirectory(newDir, newPath.substring(newPath.indexOf("/")+1));
            }
        }
    }

    public boolean moveEntry(Directory currentDir, Entry movingEntry, String newPath){
        if(!currentDir.deleteEntry(movingEntry)){
            return false;
        }
        Directory newParentDirectory = changeDirectory(currentDir, newPath);
        if(!newParentDirectory.addEntry(movingEntry)){
            return false;
        }
        return true;
    }

    public boolean copyEntry(Directory currentDir, Entry copyingEntry, String newPath){
        Directory newParentDirectory = changeDirectory(currentDir, newPath);
        if(copyingEntry instanceof File){
            File copyFile = new File(copyingEntry.getName(), newParentDirectory, 200);
            if(!newParentDirectory.addEntry(copyFile)){
                return false;
            }
        } else {
            Directory copyDir = new Directory(copyingEntry.getName(), newParentDirectory);
            copyingEntry = (Directory) copyingEntry;
            copyDir.filesAndSubDirectories = ((Directory) copyingEntry).getFilesAndSubDirectories();
            if(!newParentDirectory.addEntry(copyDir)){
                return false;
            }
        }
        return true;
    }

    protected ArrayList<Entry> getFilesAndSubDirectories(){
        return this.filesAndSubDirectories;
    }

}
