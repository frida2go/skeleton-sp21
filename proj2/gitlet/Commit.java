package gitlet;



import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Represents a gitlet commit object.
 *  does at a high level.
 *
 * @author frida
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private final String message;
    private final String selfHash;
    private final Date timestamp;
    private final List<String> parentList;
    private final HashMap<String, String> fileToBlobMap;



    public Commit(String message, List<String> parents, HashMap<String, String> blob) {
        this.message = message;
        this.parentList = parents;
        this.fileToBlobMap = blob;
        this.timestamp = new Date();
        this.selfHash = generateID();
    }

    // Constructor for the initial commit
    public Commit() {
        this.message = "initial commit";
        this.parentList = new ArrayList<>();
        this.fileToBlobMap = new HashMap<>();
        this.timestamp = new Date(0);
        this.selfHash = generateID();
    }

    private String generateID() {
        byte[] commitObject = Utils.serialize(this);
        return Utils.sha1(commitObject);
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        // Thu Jan 1 00:00:00 1970 +0000
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(timestamp);
    }

    public String getSelfHash() {
        return this.selfHash;
    }

    public List<String> getParentList() {
        return Collections.unmodifiableList(parentList);
    }

    public HashMap<String, String> getFile() {
        return new HashMap<>(fileToBlobMap);
    }

    public String getFirstParent() {
        if (!parentList.isEmpty()) {
            return parentList.get(0);
        }
        return null;
    }

    public void removeFiles(String filename) {
        fileToBlobMap.remove(filename);
    }

    public void addFiles(String filename,String fileHash) {
        fileToBlobMap.put(filename,fileHash);
    }
}

