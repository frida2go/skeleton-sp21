package gitlet;

import java.io.File;
import java.net.CookieHandler;
import java.util.*;
import java.util.stream.Collectors;

import static gitlet.Utils.*;
import static gitlet.Utils.readContents;
import static java.lang.System.*;


/**
 * Represents a gitlet repository.
 * does at a high level.
 *
 * @author Frida
 */
public class Repository {
    private String HEAD = "master";

    /**
     * The current working directory.
     */
    public static final File CWD = new File(getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File LOG_DIR = join(GITLET_DIR, "log");

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
        File masterFile = join(BRANCHES_DIR, "master");
        Utils.writeContents(masterFile, initCommit.getSelfHash());

        File headFile = join(BRANCHES_DIR, "head");
        Utils.writeContents(headFile, "master");

    }

    public static void addFiles(String fileName) {
        File filePath = join(CWD, fileName);
        //检查想add的文件是否存在
        if (!filePath.exists()) {
            out.println("File does not exist.");
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


        if (currentCommit.getFile().containsKey(fileName)
                && currentCommit.getFile().get(fileName).equals(fileHash)) {
            if (stage.getRemovedFiles().contains(fileName)) {
                stage.removeRemovedFile(fileName);
            }
            writeStage(stage);
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
            out.println("No changes added to the commit.");
            return;
        }

        if (message.equals("")) {
            out.println("Please enter a commit message.");
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
                updateBranch(newCommit);
            }
        }

        //清空stage现有文件；
        stage.clear();
        writeStage(stage);

    }

    //更新Branch，将head指向newCommit
    private static void updateBranch(Commit newCommit) {
        File headFile = join(BRANCHES_DIR, "head");
        String activeBranch = Utils.readContentsAsString(headFile);

        File branchFile = join(BRANCHES_DIR, activeBranch);
        Utils.writeContents(branchFile, newCommit.getSelfHash());
    }

    //将commit object写入文件
    private static void writeCommit(Commit newCommit) {
        File commitFile = join(COMMITS_DIR, newCommit.getSelfHash());
        Utils.writeObject(commitFile, newCommit);
    }

    public static void rmFile(String filename) {
        StagingArea stage = getStage();
        boolean isStaged = isStaged(stage, filename);

        Commit current = getCurrentCommit();
        boolean isTracked = current.getFile().containsKey(filename);

        if (!isTracked && !isStaged) {
            out.println("No reason to remove the file.");
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
        out.println("===");
        out.println("commit " + commit.getSelfHash());

        List<String> parent = commit.getParentList();
        if (parent.size() > 1) {
            String firstParent = parent.get(0).substring(0, 7);
            String secondParent = parent.get(1).substring(0, 7);
            out.println("Merge: " + firstParent + " " + secondParent);
        }

        out.println("Date: " + commit.getTimestamp());
        out.println(commit.getMessage());
        out.println();

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
                    out.println(commit.getSelfHash());
                }
            }
        }

        if (!found) {
            out.println("Found no commit with that message.");
        }
    }

    public static void status() {

        branchStatus();
        stageStatus();

        Commit currentCommit = getCurrentCommit();
        StagingArea stage = getStage();

        HashMap<String, String> currentCommitFiles = currentCommit.getFile();


        List<String> modifiedNotStaged = new ArrayList<>();
        List<String> untrackedFiles = new ArrayList<>();
        List<String> cwdFiles = plainFilenamesIn(CWD);

        for (String file : cwdFiles) {

            // 是否在缓存区？ 是否被commit追踪？ 是否修改过？
            boolean staged = isStaged(stage, file);
            boolean tracked = currentCommitFiles.containsKey(file);
            boolean isModified = isFileModified(file, currentCommitFiles.get(file));

            //如果在缓存区，但是文件已经不存在了（删除）
            if (tracked && isModified && !staged) {
                modifiedNotStaged.add(file + " (modified)");
            }
            if (!staged && !tracked) {
                untrackedFiles.add(file);
            }
        }

        for (String file : stage.getStagedFiles()) {
            if (!join(CWD, file).exists()) {
                modifiedNotStaged.add(file + " (deleted)");
            }
        }

        for (String file : currentCommitFiles.keySet()) {
            if (!stage.getRemovedFiles().contains(file) && !join(CWD, file).exists()) {
                modifiedNotStaged.add(file + " (deleted)");
            }
        }

        out.println("=== Modifications Not Staged For Commit ===");
        //modifiedNotStaged.forEach(out::println);
        out.println();

        out.println("=== Untracked Files ===");
        //untrackedFiles.forEach(out::println);
        out.println();

    }

    private static void branchStatus() {
        List<String> branches = getBranches();
        String currentBranches = getCurrentBranch();
        branches.remove(currentBranches);
        out.println("=== Branches ===");
        out.println("*" + currentBranches);
        branches.forEach(out::println);
        out.println();
    }

    private static void stageStatus() {
        StagingArea stage = getStage();
        ArrayList<String> addedFiles = stage.getStagedFiles();
        Collections.sort(addedFiles);

        ArrayList<String> removedFiles = stage.getRemovedFiles();
        Collections.sort(removedFiles);

        out.println("=== Staged Files ===");
        for (String filename : addedFiles) out.println(filename);
        out.println();

        out.println("=== Removed Files ===");
        removedFiles.forEach(out::println);
        out.println();
    }

    private static boolean isStaged(StagingArea stage, String file) {
        return stage.getAddedFiles().containsKey(file);
    }

    public static void checkout(String[] args) {
        if (args.length == 2 && args[0].equals("--")) {
            checkoutFile(args[1], null);
        } else if (args.length == 3 && args[1].equals("--")) {
            checkoutFile(args[2], args[0]);
        } else if (args.length == 1) {
            checkoutBranch(args[0]);
        } else {
            out.println("Incorrect Operands");
        }
    }


    private static void checkoutFile(String filename, String commitID) {
        Commit commitToFind;

        if (commitID == null) {
            commitToFind = getCurrentCommit();
        } else {
            commitToFind = getCommitFromHash(commitID);

            if (commitToFind == null) {
                out.println("No commit with that id exists.");
                return;
            }
        }

        String fileHash = commitToFind.getFile().get(filename);

        if (fileHash == null) {
            out.println("File does not exist in that commit.");
            return;
        }

        File blobFile = join(BLOBS_DIR, fileHash);
        byte[] fileContent = readContents(blobFile);
        File fileTo = join(CWD, filename);
        writeContents(fileTo, fileContent);

    }

    private static void checkoutBranch(String branchName) {
        String currentBranch = getCurrentBranch();
        List<String> allBranches = getBranches();

        if (branchName.equals(currentBranch)) {
            out.println("No need to checkout the current branch.");
            return;
        }

        if (!allBranches.contains(branchName)) {
            out.println("No such branch exists");
            return;
        }

        Set<String> currentBranchFiles = getFilesFromBranchHead(currentBranch);
        Set<String> checkBranchFiles = getFilesFromBranchHead(branchName);
        List<String> cwdFiles = plainFilenamesIn(CWD);

        if (cwdFiles != null) {
            for (String file : cwdFiles) {
                if (!currentBranchFiles.contains(file)
                        && checkBranchFiles.contains(file)) {
                    out.println("There is an untracked file in the way; "
                            + "delete it, or add and commit it first.");
                    return;
                }
            }
        }

        for (String file : checkBranchFiles) {
            checkoutFile(file, getBranchHead(branchName).getSelfHash());
        }

        for (String file : currentBranchFiles) {
            if (!checkBranchFiles.contains(file)) {
                File fileToDelete = join(CWD, file);
                fileToDelete.delete();
            }
        }

        File headFile = join(BRANCHES_DIR, "head");
        writeContents(headFile, branchName);

        StagingArea stage = getStage();
        stage.clear();
        writeStage(stage);

    }

    public static void createBranch(String branchName) {
        String currentBranchCommitHash = getBranchHead(getCurrentBranch()).getSelfHash();
        List<String> branchList = getBranches();

        if (branchList.contains(branchName)) {
            out.println("A branch with that name already exists.");
            return;
        }

        File branchFile = join(BRANCHES_DIR, branchName);
        writeContents(branchFile, currentBranchCommitHash);
    }

    public static void rmBranch(String branchName) {
        String currentBranch = getCurrentBranch();
        List<String> branchList = getBranches();

        if (branchName.equals(currentBranch)) {
            out.println("Cannot remove the current branch.");
            return;
        }

        if (!branchList.contains(branchName)) {
            out.println("A branch with that name does not exist.");
            return;
        }

        File branchToRemove = join(BRANCHES_DIR, branchName);
        branchToRemove.delete();
    }

    public static void reset(String commitID) {

        Commit targetCommit = getCommitFromHash(commitID);

        if (targetCommit == null) {
            out.println("No commit with that id exists.");
            return;
        }

        List<String> cwdFiles = plainFilenamesIn(CWD);
        Set<String> targetCommitFiles = targetCommit.getFile().keySet();
        Set<String> currentCommitFiles = getCurrentCommit().getFile().keySet();

        for (String file : cwdFiles) {
            if (!currentCommitFiles.contains(file) && targetCommitFiles.contains(file)) {
                out.println("There is an untracked file in the way; "
                        + "delete it, or add and commit it first.");
                return;
            }
        }

        // Update files in the working directory to match the target commit
        for (String file : targetCommitFiles) {
            checkoutFile(file, commitID);
        }

        // Remove files that are in the current commit but not in the target commit
        for (String file : currentCommitFiles) {
            if (!targetCommitFiles.contains(file)) {
                File fileToRemove = join(CWD, file);
                fileToRemove.delete();
            }
        }

        // Set the current branch's pointer to the target commit
        String currentBranch = getCurrentBranch();
        File branchPointer = join(BRANCHES_DIR, currentBranch);
        writeContents(branchPointer, commitID);

        StagingArea stage = new StagingArea();
        writeStage(stage);
    }

    public static void merge(String branch) {
        if (!canMerge(branch)) {
            return;
        }

        StagingArea stage = getStage();

        String currentBranch = getCurrentBranch();
        Commit currentBranchHead = getBranchHead(currentBranch);

        Commit givenBranchHead = getBranchHead(branch);
        String splitPointHash = findLatestCommonAncestor
                (currentBranchHead, givenBranchHead);
        Commit splitPoint = getCommitFromHash(splitPointHash);

        if (!canMerge2(givenBranchHead,currentBranchHead,splitPointHash,branch)){
            return;
        }

        Set<String> allFiles = new HashSet<>();
        allFiles.addAll(splitPoint.getFile().keySet());
        allFiles.addAll(currentBranchHead.getFile().keySet());
        allFiles.addAll(givenBranchHead.getFile().keySet());

        for (String filename: allFiles) {
            String splitVersion = splitPoint.getFile().getOrDefault(filename, null);
            String currentVersion = currentBranchHead.getFile().getOrDefault(filename, null);
            String givenVersion = givenBranchHead.getFile().getOrDefault(filename, null);



            // modified in current but not in given => current
            // modified in given but not in current => given
            // modified in given and in current
                // same => remain
                // not same => conflict
            // not present in split
                // not in given but in current => current
                // not in current but in given => given
            // unmodified in current but not present in given => Remove
            // unmodified in given but not present in current => Remain remove

            // if files only modified(delete) in given, change to given.
            boolean conflict = false;

            boolean onlyPresentInGiven = splitVersion == null
                    && givenVersion != null && currentVersion == null;

            boolean onlyModifiedInGiven = splitVersion != null
                    && givenVersion != null
                    && Objects.equals(splitVersion,currentVersion)
                    && !Objects.equals(splitVersion,givenVersion);

            boolean onlyDeletedInGiven = splitVersion != null
                    && Objects.equals(splitVersion,currentVersion)
                    && givenVersion == null;

            if (onlyPresentInGiven || onlyModifiedInGiven) {
                checkoutFile(filename,givenBranchHead.getSelfHash());
                //stage.add(filename,givenVersion);
            }

            if (onlyDeletedInGiven) {
                currentBranchHead.removeFiles(filename);
                File toRemove = join(CWD,filename);
                Utils.restrictedDelete(toRemove);
            }

            // conflict
            if (splitVersion != null
                    && givenVersion != null && currentVersion != null
                    && !Objects.equals(splitVersion,givenVersion)
                    && !Objects.equals(splitVersion,currentVersion)) {
                conflict = true;
            }
            // split不为null，given中被删了，curr跟split不相等；
            if (splitVersion != null
                    && givenVersion == null && currentVersion != null
                    && !Objects.equals(splitVersion,currentVersion)) {
                conflict = true;
            }

            // split不为null，curr中被删了，given跟split不相等；
            if (splitVersion != null
                    && givenVersion != null && currentVersion == null
                    && !Objects.equals(splitVersion,givenVersion)) {
                conflict = true;
            }
            if (conflict) {
                generateConflictContent(currentVersion,givenVersion,
                        currentBranchHead,givenBranchHead,filename);
                out.println("Encountered a merge conflict.");
            }
        }
        
        mergeCommit(currentBranchHead,givenBranchHead,branch,currentBranch);


    }



    private static boolean canMerge(String branch){
        StagingArea stage = getStage();
        if (!stage.isEmptyStage()) {
            out.println("You have uncommitted changes.");
            return false;
        }

        if (!getBranches().contains(branch)) {
            out.println("A branch with that name does not exist.");
            return false;
        }

        String currentBranch = getCurrentBranch();
        if (currentBranch.equals(branch)) {
            out.println("Cannot merge a branch with itself.");
            return false;
        }

        return true;
    }

    private static boolean canMerge2(Commit givenBranchHead,
                                     Commit currentBranchHead, String splitPointHash,
                                     String branch) {
        if (splitPointHash == null) {
            throw new IllegalStateException("No common ancestor found.");
        }

        if (splitPointHash.equals(givenBranchHead.getSelfHash())) {
            out.println("Given branch is an ancestor of the current branch.");
            return false;
        }

        if (splitPointHash.equals(currentBranchHead.getSelfHash())) {
            out.println("Current branch fast-forwarded.");
            checkoutBranch(branch);
            return false;
        }
        return true;
    }


    private static Commit getCommitFromHash(String hash) {
        List<String> allCommits = plainFilenamesIn(COMMITS_DIR);

        if (allCommits == null) {
            return null;
        }

        List<String> matchCommits = allCommits.stream()
                .filter(name -> name.startsWith(hash))
                .collect(Collectors.toList());

        if (matchCommits.size() == 1) {
            File commitFile = join(COMMITS_DIR, matchCommits.get(0));
            if (commitFile.exists()) {
                return readObject(commitFile, Commit.class);
            }
        }
        return null;
    }

    private static Commit getCurrentCommit() {
        File headFile = join(BRANCHES_DIR, "head");
        String currentBranch = Utils.readContentsAsString(headFile);

        File branchFile = join(BRANCHES_DIR, currentBranch);
        String commitHash = readContentsAsString(branchFile);

        File commitFile = join(COMMITS_DIR, commitHash);
        return readObject(commitFile, Commit.class);
    }

    private static String getCurrentBranch() {
        File headFile = join(BRANCHES_DIR, "head");
        return Utils.readContentsAsString(headFile);
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

        throw new IllegalStateException("No branch found.");
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
        File file = join(CWD, filename);
        if (file.exists()) {
            String currentHash = sha1(readContents(file));
            return !currentHash.equals(fileHash);
        }
        return false;
    }

    public static boolean isInitialized() {
        File gitletDir = join(CWD, ".gitlet");
        return gitletDir.exists() && gitletDir.isDirectory();
    }

    private static List<String> getAllAncestors(Commit commit) {
        List<String> ancestors = new ArrayList<>();
        while (commit != null) {
            ancestors.add(commit.getSelfHash());
            String parentHash = commit.getFirstParent();
            if (parentHash == null) {
                commit = null;
            } else {
                commit = getCommitFromHash(parentHash);
            }
        }
        return ancestors;
    }

    private static String findLatestCommonAncestor(Commit commit1, Commit commit2) {
        List<String> ancestorsCommit1 = getAllAncestors(commit1);
        List<String> ancestorsCommit2 = getAllAncestors(commit2);

        for (String commitHash : ancestorsCommit1) {
            if (ancestorsCommit2.contains(commitHash)) {
                return commitHash;
            }
        }
        return null;
    }

    private static void generateConflictContent(String currentVersion, String givenVersion,
                                                  Commit curr, Commit given,String filename) {

        String content = "<<<<<<< HEAD\n" +
                (currentVersion == null ? "" : readFileFromCommit(curr,currentVersion)) +
                "=======\n" +
                (givenVersion == null ? "" : readFileFromCommit(given,givenVersion)) +
                ">>>>>>>";

        File newFile = join(CWD,filename);
        writeContents(newFile,content);

        String fileHash = sha1(content);
        File blob = join(BLOBS_DIR,fileHash);
        writeContents(blob,content);
        curr.addFiles(filename,fileHash);

    }

    private static void mergeCommit (Commit currentHead, Commit otherHead,String branch,String currentBranch) {
        List<String> parents = new ArrayList<>();
        String message = "Merged " + branch + " into " + currentBranch +".";

        parents.add(currentHead.getSelfHash());
        parents.add(otherHead.getSelfHash());
        HashMap<String,String> blob = currentHead.getFile();

        Commit newCommit = new Commit(message, parents, blob);
        writeCommit(newCommit);
        updateBranch(newCommit);
    }

    private static String readFileFromCommit(Commit commit, String fileHash) {
        File file = join(BLOBS_DIR,fileHash);
        return readContentsAsString(file);


    }


}
