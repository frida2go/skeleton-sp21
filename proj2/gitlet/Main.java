package gitlet;

import java.util.Arrays;
import java.util.HashMap;

import static gitlet.Repository.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author TODO
 */
public class Main {

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     * If a user doesn’t input any arguments,
     * print the message Please enter a command. and exit.
     * <p>
     * If a user inputs a command that doesn’t exist,
     * print the message No command with that name exists. and exit.
     * <p>
     * If a user inputs a command with the wrong number or format of operands,
     * print the message Incorrect operands. and exit.
     * <p>
     * If a user inputs a command that requires being
     * in an initialized Gitlet working directory
     * (i.e., one containing a .gitlet subdirectory), but is not in such a directory,
     * print the message Not in an initialized Gitlet directory.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];

        if (!firstArg.equals("init") && !isInitialized()) {
            Utils.message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }

        switch (firstArg) {
            case "init" -> {
                isValidArgument(args, 1);
                initCommand();
            }
            case "add" -> {
                isValidArgument(args, 2);
                addFiles(args[1]);
            }
            case "commit" -> {
                isValidArgument(args, 2);
                createCommit(args[1]);
            }
            case "rm" -> {
                isValidArgument(args, 2);
                rmFile(args[1]);
            }
            case "log" -> {
                isValidArgument(args, 1);
                log();
            }
            case "global-log" -> {
                isValidArgument(args, 1);
                globLog();
            }
            case "find" -> {
                isValidArgument(args, 2);
                find(args[1]);
            }
            case "status" -> {
                isValidArgument(args, 1);
                status();
            }
            case "checkout" -> {
                String[] checkoutArgs = Arrays.copyOfRange(args, 1, args.length);
                checkout(checkoutArgs);
            }
            case "branch" -> {
                isValidArgument(args, 2);
                createBranch(args[1]);
            }
            case "rm-branch" -> {
                isValidArgument(args, 2);
                rmBranch(args[1]);
            }
            case "reset" -> {
                isValidArgument(args, 2);
                reset(args[1]);
            }
            case "merge" -> {
                isValidArgument(args, 2);
                merge(args[1]);
            }
            default -> {
                Utils.message("No command with that name exists.");
            }
        }


    }

    public static void isValidArgument(String[] args, int length) {
        if (args.length != length) {
            System.out.println("Invalid argument Length");
            System.exit(0);
        }
    }
}
