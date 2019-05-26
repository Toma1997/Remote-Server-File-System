package RemoteControlOnServerFileSystem;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Entry {
    protected Directory parent;
    protected String created;
    protected String lastUpdated;
    protected String lastAccessed;
    protected String name;

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Entry(String name, Directory parent){
        this.name = name;
        this.parent = parent;
        this.created = format.format(new Date());
        this.lastUpdated = format.format(new Date());
        this.lastAccessed = format.format(new Date());
    }

    public boolean delete(){
        if(this.parent == null){
            return false;
        }

        return parent.deleteEntry(this);
    }

    public abstract int size();

    public String getFullPath(){
        if(this.parent == null){
            return this.name + "/";
        } else {
            String path = parent.getFullPath() + this.name;
            if(this instanceof Directory){
                path += "/";
            }
            return path;

        }
    }

    // Geteri i seteri
    public String getCreationTime(){
        return this.created;
    }

    public String getLastUpdatedTime(){
        return this.lastUpdated;
    }

    public String getLastAccessedTime(){
        return this.lastAccessed;
    }

    public void changeName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
