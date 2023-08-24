package gitlet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;
import static gitlet.Utils.readContents;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author TODO
 */
public class Repository {
    private String HEAD = "master";
    private static StagingArea stage;

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File LOG_DIR = join(GITLET_DIR, "log");

    /* TODO: fill in the rest of this class. */
    public static void initCommand() {
        if (GITLET_DIR.exists()) {
            message("A Gitlet version-control system already exists in the current directory.");
            return;
        }

        GITLET_DIR.mkdir();
        BLOBS_DIR.mkdir();
        BRANCHES_DIR.mkdir();
        COMMITS_DIR.mkdir();
        STAGING_DIR.mkdir();
        LOG_DIR.mkdir();

        //将init的commit以object形式写入commit文件夹
        Commit initCommit = new Commit();
        File initCommitFile = join(COMMITS_DIR, initCommit.getSelfHash());
        Utils.writeObject(initCommitFile, initCommit);

        //将init commit HASH以String形式写入branches / master 文件
        File MasterFile = join(BRANCHES_DIR, "master");
        Utils.writeContents(MasterFile, initCommit.getSelfHash());

        File HeadFile = join(BRANCHES_DIR, "head");
        Utils.writeContents(HeadFile, "master");

    }

    public static void addFiles(String fileName) {
        File filePath = join(CWD, fileName);
        //检查想add的文件是否存在
        if (!filePath.exists()) {
            System.out.println("File not exist.");
            return;
        }

        //将文件写入写入，并生成Hash值
        byte[] fileContents = readContents(filePath);
        String fileHash = sha1(fileContents);

        // 将文件的byte 写入Blob文件夹
        File blobFile = join(BLOBS_DIR, fileHash);
        if (!blobFile.exists()) {
            writeContents(blobFile, fileContents);
        }

        // 检测Staging Area 是否存在，不存在则创建一个
        StagingArea stage = getStage();
        Commit currentCommit = getCurrentCommit();

        
        if (currentCommit.getFile().containsKey(fileName) &&
                currentCommit.getFile().get(fileName).equals(fileHash)) {
            return;

        }
        if (stage.getRemovedFiles().contains(fileName)) {
            stage.removeRemovedFile(fileName);

        }
        if (!stage.getAddedFiles().containsKey(fileName)) {
            stage.add(fileName, fileHash);
        }

        writeStage(stage);
    }

    public static void createCommit(String message) {
        StagingArea stage = getStage();

        if (stage.getAddedFiles().isEmpty()
                && stage.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to commit");
            return;
        }

        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
        }

        // 获取目前commit，以及其parentList
        Commit currentCommit = getCurrentCommit();
        List<String> parentList = new ArrayList<>();

        // 将current commit 加到即将创建的commit的 parent list
        if (currentCommit != null) {
            parentList.add(currentCommit.getSelfHash());

            //获取现在有的File list，并将stage add的文件加入这个File list
            if (currentCommit.getFile() != null) {
                HashMap<String, String> currentFileList =
                        new HashMap<>(currentCommit.getFile());
                currentFileList.putAll(stage.getAddedFiles());

                // 删除掉在stage remove区域的files
                for (String removeFile : stage.getRemovedFiles()) {
                    currentFileList.remove(removeFile);
                }

                //将commit写入文件夹
                Commit newCommit = new Commit(message, parentList, currentFileList);
                writeCommit(newCommit);
                updateBranch(newCommit.getSelfHash());
            }
        }

        //清空stage现有文件；
        stage.clear();
        writeStage(stage);

    }

    //更新Branch，将head指向newCommit
    private static void updateBranch(String newCommitHash) {
        File headFile = join(BRANCHES_DIR, "head");
        String activeBranch = Utils.readContentsAsString(headFile);

        File branchFile = join(BRANCHES_DIR, activeBranch);
        Utils.writeContents(branchFile, newCommitHash);
    }

    //将commit object写入文件
    private static void writeCommit(Commit newCommit) {
        File commitFile = join(COMMITS_DIR, newCommit.getSelfHash());
        Utils.writeObject(commitFile, newCommit);
    }

    public static void rmFile(String filename) {
        StagingArea stage = getStage();
        boolean isStaged = stage.getAddedFiles().containsKey(filename);

        Commit current = getCurrentCommit();
        boolean isTracked = current.getFile().containsKey(filename);

        if (!isTracked && !isStaged) {
            System.out.println("No reason to remove the file.");
            return;
        }

        if (isStaged) {
            stage.removeAddedFile(filename);
        }

        if (isTracked) {
            stage.addToRemovedFiles(filename);
            File fileToRemove = join(CWD, filename);

            if (fileToRemove.exists()) {
                Utils.restrictedDelete(fileToRemove);
            }
        }

        writeStage(stage);

    }

    public static void log() {
        Commit current = getCurrentCommit();

        while (current != null) {
            printCommit(current);

            if (!current.getParentList().isEmpty()) {
                current = getCommitFromHash(current.getParentList().get(0));
            } else {
                current = null;
            }

        }
    }

    public static void globLog() {
        List<String> filenames = Utils.plainFilenamesIn(COMMITS_DIR);
        if (filenames != null) {
            for (String file : filenames) {
                File commitFile = join(COMMITS_DIR, file);
                Commit commit = readObject(commitFile, Commit.class);

                printCommit(commit);
            }
        }
    }

    public static void printCommit(Commit commit) {
        System.out.println("===");
        System.out.println("commit " + commit.getSelfHash());

        List<String> parent = commit.getParentList();
        if (parent.size() > 1) {
            String firstParent = parent.get(0).substring(0, 7);
            String secondParent = parent.get(1).substring(0, 7);
            System.out.println("Merge: " + firstParent + " " + secondParent);
        }

        System.out.println("Date: " + commit.getTimestamp());
        System.out.println(commit.getMessage());
        System.out.println();

    }

    public static void find(String message) {
        boolean found = false;
        List<String> filename = Utils.plainFilenamesIn(COMMITS_DIR);

        if (filename != null) {
            for (String file : filename) {
                File commitFile = join(COMMITS_DIR, file);
                Commit commit = readObject(commitFile, Commit.class);

                if (commit.getMessage().equals(message)) {
                    found = true;
                    System.out.println(commit.getSelfHash());
                }
            }
        }

        if (!found) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void status() {
        List<String> branches = getBranches();
        List<String> filesInCWD = plainFilenamesIn(CWD);
        String currentBranches = getCurrentBranches();
        StagingArea stage = getStage();

        Commit currentCommit = getCurrentCommit();
        HashMap<String, String> currentFiles = currentCommit.getFile();

        System.out.println("=== Branches ===");
        for (String branch : branches) {
            if (branch.equals(currentBranches)) {
                System.out.println("*" + currentBranches);
            } else {
                System.out.println(branch);
            }
        }

        System.out.println();

        ArrayList<String> addedFiles = stage.getStagedFiles();
        Collections.sort(addedFiles);

        System.out.println("=== Staged Files ===");
        for (String filename : addedFiles) {
            System.out.println(filename);
        }

        System.out.println();

        // Display Removed Files
        ArrayList<String> removedFiles = stage.getRemovedFiles();
        Collections.sort(removedFiles);

        System.out.println("=== Removed Files ===");
        for (String file : removedFiles) {
            System.out.println(file);
        }

        System.out.println();

        List<String> modifiedNotStaged = new ArrayList<>();
        List<String> untrackedFiles = new ArrayList<>();

        for (String file : filesInCWD) {

            // 是否在缓存区？ 是否被commit追踪？ 是否修改过？
            boolean isStaged = stage.getAddedFiles().containsKey(file);
            boolean isTracked = currentFiles.containsKey(file);
            boolean isModified = isFileModified(file, currentFiles.get(file));

            //如果在缓存区，但是文件已经不存在了（删除）
            if (isStaged && !join(CWD, file).exists()) {
                modifiedNotStaged.add(file + " (deleted)");
            }
            // 如果在现在commit，修改过了，但不在缓存区
            else if (isTracked && isModified && !isStaged) {
                modifiedNotStaged.add(file + " (modified)");
            }
            //既不在缓存区又没有被现在commit tracked
            else if (!isStaged && !isTracked) {
                untrackedFiles.add(file);
            }

        }

        System.out.println("=== Modifications Not Staged For Commit ===");
        for (String file : modifiedNotStaged) {
            System.out.println(file);
        }
        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String file : untrackedFiles) {
            System.out.println(file);
        }
        System.out.println();


    }

    public static void checkout(String[] args) {
        if (args.length == 2 && args[0].equals("--")) {
            checkoutFile(args[1], null);
        } else if (args.length == 3 && args[1].equals("--")) {
            checkoutFile(args[2], args[0]);
        } else if (args.length == 1) {
            checkoutBranch(args[0]);
        } else {
            System.out.println("Incorrect Operands");
        }
    }


    private static void checkoutFile(String filename, String commitID) {
        Commit commitToFind;

        if (commitID == null) {
            commitToFind = getCurrentCommit();
        } else {
            commitToFind = getCommitFromHash(commitID);

            if (commitToFind == null) {
                System.out.println("No commit with that id exists.");
                return;
            }
        }

        String fileHash = commitToFind.getFile().get(filename);

        if (fileHash == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        File blobFile = join(BLOBS_DIR, fileHash);
        byte[] fileContent = readContents(blobFile);
        File fileTo = join(CWD, filename);
        writeContents(fileTo, fileContent);

    }

    private static void checkoutBranch(String branchName) {
        String currentBranch = getCurrentBranches();
        List<String> allBranches = getBranches();

        if (branchName.equals(currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        if (!allBranches.contains(branchName)) {
            System.out.println("No such branch exists");
            return;
        }

        Set<String> currentBranchFiles = getFilesFromBranchHead(currentBranch);
        Set<String> checkBranchFiles = getFilesFromBranchHead(branchName);
        List<String> CWDFiles = plainFilenamesIn(CWD);

        if (CWDFiles != null) {
            for (String file : CWDFiles) {
                if (!currentBranchFiles.contains(file)
                        && checkBranchFiles.contains(file)) {
                    System.out.println("There is an untracked file in the way; " +
                            "delete it, or add and commit it first.");
                    return;
                }
            }
        }

        for (String file : checkBranchFiles) {
            checkoutFile(file, null);
        }

        for (String file : currentBranchFiles) {
            if (!checkBranchFiles.contains(file)) {
                File fileToDelete = join(CWD, file);
                fileToDelete.delete();
            }
        }

        File headFile = join(GITLET_DIR, "HEAD");
        writeContents(headFile, branchName);

        StagingArea stage = getStage();
        stage.clear();
        writeStage(stage);

    }

    private static Commit getCommitFromHash(String hash) {
        String commitHash = hash;

        if (hash.length() == 6) {
            List<String> allCommits = plainFilenamesIn(COMMITS_DIR);
            List<String> matchCommits;

            if (allCommits != null) {
                matchCommits = allCommits.stream()
                        .filter(name -> name.startsWith(hash))
                        .collect(Collectors.toList());
            } else {
                matchCommits = new ArrayList<>();
            }

            if (matchCommits.size() == 1) {
                commitHash = matchCommits.get(0);
            }

        }

        File commitFile = join(COMMITS_DIR, commitHash);
        if (commitFile.exists()) {
            return readObject(commitFile, Commit.class);
        }

        return null;
    }

    private static Commit getCurrentCommit() {
        File HeadFile = join(BRANCHES_DIR, "head");
        String currentBranch = Utils.readContentsAsString(HeadFile);

        File branchFile = join(BRANCHES_DIR, currentBranch);
        String commitHash = readContentsAsString(branchFile);

        File commitFile = join(COMMITS_DIR, commitHash);
        return readObject(commitFile, Commit.class);
    }

    private static String getCurrentBranches() {
        File headFile = join(BRANCHES_DIR, "head");
        if (headFile.exists()) {
            return Utils.readContentsAsString(headFile);
        }

        return null;
    }

    private static List<String> getBranches() {
        List<String> branches = Utils.plainFilenamesIn(BRANCHES_DIR);
        if (branches == null) {
            return new ArrayList<>();
        }

        List<String> mutableBranches = new ArrayList<>(branches);
        mutableBranches.remove("head");
        return mutableBranches;
    }

    private static Commit getBranchHead(String branchName) {
        File brancheFile = join(BRANCHES_DIR, branchName);

        if (brancheFile.exists()) {
            String commitHash = readContentsAsString(brancheFile);
            return getCommitFromHash(commitHash);
        }

        return null;
    }

    private static Set<String> getFilesFromBranchHead(String branchName) {
        Commit commit = getBranchHead(branchName);

        if (commit == null) {
            return new HashSet<>();
        }

        return commit.getFile().keySet();
    }

    private static StagingArea getStage() {
        File stagingFile = join(STAGING_DIR, "stage");
        if (stagingFile.exists()) {
            return Utils.readObject(stagingFile, StagingArea.class);
        } else {
            return new StagingArea();
        }
    }

    private static void writeStage(StagingArea stage) {
        File stagingFile = join(STAGING_DIR, "stage");
        Utils.writeObject(stagingFile, stage);
    }

    private static boolean isFileModified(String filename, String fileHash) {
        File file = new File(CWD, filename);
        if (file.exists()) {
            String currentHash = Utils.sha1(Utils.readContents(file));
            return !currentHash.equals(fileHash);
        }
        return false;
    }


}
