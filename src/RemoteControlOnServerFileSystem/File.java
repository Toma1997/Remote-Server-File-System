package RemoteControlOnServerFileSystem;

public class File extends Entry {
    private String content = "dffdfdfdfdfd,fdfddfdfddffd,gfgfgfgfg,";
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
        this.lastAccessed = System.currentTimeMillis();
        this.content = content;
        this.lastUpdated = System.currentTimeMillis();
        this.size = content.length();
    }

    public void appendContent(String content){
        this.lastAccessed = System.currentTimeMillis();
        this.content += content;
        this.lastUpdated = System.currentTimeMillis();
        this.size += content.length();
    }
}
