package RemoteControlOnServerFileSystem;

public abstract class Entry {
    protected Directory parent;
    protected long created;
    protected long lastUpdated;
    protected long lastAccessed;
    protected String name;

    public Entry(String name, Directory parent){
        this.name = name;
        this.parent = parent;
        this.created = System.currentTimeMillis();
        this.lastUpdated = System.currentTimeMillis();
        this.lastAccessed = System.currentTimeMillis();
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
            String path = parent.getFullPath() + "/" + this.name;
            if(this instanceof Directory){
                path += "/";
            }
            return path;

        }
    }

    // Geteri i seteri
    public long getCreationTime(){
        return this.created;
    }

    public long getLastUpdatedTime(){
        return this.lastUpdated;
    }

    public long getLastAccessedTime(){
        return this.lastAccessed;
    }

    public void changeName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
