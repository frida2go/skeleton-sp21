package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class StagingArea implements Serializable {
    private HashMap<String, String> addedFiles;
    private ArrayList<String> removedFiles;

    public StagingArea() {
        this.addedFiles = new HashMap<>();
        this.removedFiles = new ArrayList<>();
    }

    public void add(String filename, String sha1) {
        if (removedFiles.contains(filename)) {
            removedFiles.remove(filename);
        }
        addedFiles.put(sha1, filename);
    }

    public void addToRemovedFiles(String fileName) {
        if (addedFiles.containsKey(fileName)) {
            addedFiles.remove(fileName);
        }
        removedFiles.add(fileName);
    }

    public void clear() {
        addedFiles.clear();
        removedFiles.clear();
    }

    public HashMap<String, String> getAddedFiles() {
        return new HashMap<>(addedFiles);
    }

    public ArrayList<String> getRemovedFiles() {
        return new ArrayList<>(removedFiles);
    }


}
