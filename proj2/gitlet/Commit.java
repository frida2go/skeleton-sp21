package gitlet;

// TODO: any imports you need here

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
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
    public Date timestamp;
    private final List<String> parentList;
    public HashMap<String, String> fileToBlobMap;


    /* TODO: fill in the rest of this class. */
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
}

