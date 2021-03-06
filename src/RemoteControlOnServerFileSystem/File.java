package RemoteControlOnServerFileSystem;

import java.util.Date;

public class File extends Entry {
    private String content = "";
    private int size;

    public File(String name, Directory parent, int size){
        super(name, parent);
        this.size = size;
    }

    public int size(){
        return this.size;
    }

    public String getContents(){
        return this.content;
    }

    public void setContent(String content){
        this.lastAccessed = format.format(new Date());
        this.content = content;
        this.lastUpdated = format.format(new Date());
        this.size = content.length() + 200;
    }

    public void appendContent(String content){
        this.lastAccessed = format.format(new Date());
        this.content += content;
        this.lastUpdated = format.format(new Date());
        this.size += content.length();
    }
}
