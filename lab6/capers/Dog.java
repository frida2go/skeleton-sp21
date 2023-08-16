package capers;

import java.io.File;
import java.io.Serializable;
import static capers.Utils.*;

/** Represents a dog that can be serialized.
 * @author TODO
*/
public class Dog implements Serializable { // TODO

    /** Folder that dogs live in. */
    static final File CWD = new File(System.getProperty("user.dir"));
    static final File DOG_FOLDER = Utils.join(CWD,".capers/dogs");

    /** Age of dog. */
    private int age;
    /** Breed of dog. */
    private String breed;
    /** Name of dog. */
    private String name;

    /**
     * Creates a dog object with the specified parameters.
     * @param name Name of dog
     * @param breed Breed of dog
     * @param age Age of dog
     */
    public Dog(String name, String breed, int age) {
        this.age = age;
        this.breed = breed;
        this.name = name;
    }

    /**
     * Reads in and deserializes a dog from a file with name NAME in DOG_FOLDER.
     *
     * @param name Name of dog to load
     * @return Dog read from file
     */
    public static Dog fromFile(String name) {
        // TODO (hint: look at the Utils file)
        File readerFile = join(DOG_FOLDER,name);
        try{
            Dog dogFromFile = readObject(readerFile, Dog.class);
            return dogFromFile;
        } catch (Exception e){
            return null;
        }
    }

    /**
     * Increases a dog's age and celebrates!
     */
    public void haveBirthday() {
        age += 1;
        System.out.println(toString());
        System.out.println("Happy birthday! Woof! Woof!");
    }

    /**
     * Saves a dog to a file for future use.
     */
    public void saveDog() {
        // TODO (hint: don't forget dog names are unique)
        File[] dogFiles = DOG_FOLDER.listFiles();
        String outFileName = this.name.toLowerCase();
        boolean hasEqualName = false;

        if (dogFiles != null) {
            for (File f: dogFiles) {
                String fileName = f.getName();
                if (fileName.equals(outFileName)) {
                    hasEqualName = true;
                }
            }
        }
        if (!hasEqualName) {
            File outFile = join(DOG_FOLDER,outFileName);
            System.out.println(toString());
            writeObject(outFile,this);
        }

    }

    @Override
    public String toString() {
        return String.format(
            "Woof! My name is %s and I am a %s! I am %d years old! Woof!",
            name, breed, age);
    }

}
