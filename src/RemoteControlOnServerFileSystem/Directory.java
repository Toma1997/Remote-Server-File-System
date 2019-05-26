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
        for(Entry e: filesAndSubDirectories){
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
        for(Entry e: filesAndSubDirectories){
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
    public Entry getEntryByName(String name){
        for(Entry e: filesAndSubDirectories){
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }

    // brise folder ili fajl - IMA BUG, nece nista da brise sad
    public boolean deleteEntry(Entry entry){
        if(!this.entryExists(entry)){
            return false;
        }
        filesAndSubDirectories.remove(entry);
        return true;

    }

    // dodaje fajl ili folder
    public boolean addEntry(Entry entry){
        if(this.entryExists(entry)){
            return false;
        }
        filesAndSubDirectories.add(entry);
        return true;

    }

    // ako vec psotoji isti fajl ili folder
    public boolean entryExists(Entry entry){
        for(Entry e: filesAndSubDirectories){
            if(e.getName().equals(entry.getName())){
                if(e instanceof File && entry instanceof File || e instanceof Directory && entry instanceof Directory){
                    return true;
                }
            }
        }
        return false;
    }

    protected ArrayList<Entry> getFilesAndSubDirectories(){
        return this.filesAndSubDirectories;
    }

}
