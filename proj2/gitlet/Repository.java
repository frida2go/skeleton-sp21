package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    private String HEAD = "master";
    private static StagingArea stage;

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File BLOBS_DIR = join(GITLET_DIR, "blobs");
    public static final File BRANCHES_DIR = join(GITLET_DIR, "branches");
    public static final File COMMITS_DIR = join(GITLET_DIR, "commits");
    public static final File STAGING_DIR = join(GITLET_DIR, "staging");
    public static final File LOG_DIR = join(GITLET_DIR, "log");

    /* TODO: fill in the rest of this class. */
    public static void initCommand(){
        if (GITLET_DIR.exists()){
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
        File initCommitFile = join(COMMITS_DIR,initCommit.getSelfHash());
        Utils.writeObject(initCommitFile,initCommit);

        //将init commit HASH以String形式写入branches / master 文件
        File MasterFile = join(BRANCHES_DIR,"master");
        Utils.writeContents(MasterFile,initCommit.getSelfHash());

        File HeadFile = join(BRANCHES_DIR, "head");
        Utils.writeContents(HeadFile,"master");

    }
    public static void addFiles(String filename){
        File filePath = join(CWD,filename);
        //检查想add的文件是否存在
        if (!filePath.exists()){
            System.out.println("File not exist.");
            return;
        }

        //将文件写入写入，并生成Hash值
        byte[] fileContents = readContents(filePath);
        String fileHash = sha1(fileContents);

        // 将文件的byte 写入Blob文件夹
        File blobFile = join(BLOBS_DIR, fileHash);
        if (!blobFile.exists()) {
            writeContents(blobFile,fileContents);
        }


        // 检测Staging Area 是否存在，不存在则创建一个
        File stagingFile = join(STAGING_DIR, "stage");
        if (stagingFile.exists()) {
            stage = Utils.readObject(stagingFile, StagingArea.class);
        } else {
            stage = new StagingArea();
        }

        Commit currentCommit = getCurrentCommit();

        //如果现在的Commit的文件列表已经有了要添加的文件（名字），并且内容也完全相同，则将
        //文件从Stage 删除。
        if (currentCommit.getFile().containsKey(filename) &&
                currentCommit.getFile().get(filename).equals(fileHash)) {
            stage.addToRemovedFiles(filename);
        } else {
            stage.add(filename, fileHash);
        }

        writeObject(stagingFile,stage);
    }

    public static void createCommit(String message) {
        File stagingFile = join(STAGING_DIR, "stage");

        stage = Utils.readObject(stagingFile, StagingArea.class);
        if (stage.getAddedFiles().isEmpty()
                && stage.getRemovedFiles().isEmpty()) {
            System.out.println("No changes added to commit");
            return;
        }

        if (message == "") {
            System.out.println("Please enter a commit message.");
        }

        // 获取目前commit，以及其parentList
        Commit currentCommit = getCurrentCommit();
        List<String> parentList = new ArrayList<>();

        // 将current commit 加到即将创建的commit的 parent list
        if(currentCommit != null) {
            parentList.add(currentCommit.getSelfHash());
        }

        //获取现在有的File list，并将stage add的文件加入这个File list
        HashMap<String,String> currentFileList =
                new HashMap<>(currentCommit.getFile());
        currentFileList.putAll(stage.getAddedFiles());

        // 删除掉在stage remove区域的files
        for (String removeFile : stage.getRemovedFiles()) {
            currentFileList.remove(removeFile);
        }

        //将commit写入文件夹
        Commit newCommit = new Commit(message,parentList,currentFileList);
        writeCommit(newCommit);

        updateBranch(newCommit.getSelfHash());

        //清空stage现有文件；
        stage.clear();
        writeObject(stagingFile,stage);



    }

    //更新Branch，将head指向newCommit
    private static void updateBranch(String newCommitHash) {
        File headFile = join(BRANCHES_DIR, "head");
        String activeBranch = Utils.readContentsAsString(headFile);

        File branchFile = join(BRANCHES_DIR, activeBranch);
        Utils.writeContents(branchFile,newCommitHash);
    }

    //将commit object写入文件
    private static void writeCommit(Commit newCommit) {
        File commitFile = join(COMMITS_DIR,newCommit.getSelfHash());
        Utils.writeObject(commitFile,newCommit);
    }

    public static Commit getCurrentCommit() {
        File HeadFile = join(BRANCHES_DIR, "head");
        String currentBranch = Utils.readContentsAsString(HeadFile);

        File branchFile = join(BRANCHES_DIR, currentBranch);
        String commitHash = readContentsAsString(branchFile);

        File commitFile = join(COMMITS_DIR, commitHash);
        return readObject(commitFile, Commit.class);
    }


}
