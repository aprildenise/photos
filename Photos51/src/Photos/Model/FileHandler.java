package Photos.Model;

import java.io.File;
import java.io.IOException;

/**
 * Loads and finds files.
 * @author Krysti Leong
 * @author April Dizon
 */
public class FileHandler {

    /**
     * Find a file with the given file name.
     * @param fileName File name.
     * @return File, if found.
     */
    public static File findPath(String fileName){
        File file = new File(".."); // Go up to the Photos package.
        //System.out.println("Trying to find:" + fileName);
        return find(fileName, file);
    }

//    public static String convertToAbsolute(File path){
//        return path.getAbsolutePath();
//    }
//
//    public static String convertToCanonical(File path){
//        try {
//            return path.getCanonicalPath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * Utility to recurse through directories to find the file.
     * @param fileName File name of the file.
     * @param currentDirectory Current directory the search is in.
     * @return File found, if any.
     */
    private static File find(String fileName, File currentDirectory){
        if (currentDirectory.isDirectory()){
            File[] list = currentDirectory.listFiles();
            for (File f : list){
                File found = find(fileName, f);
                if (found != null){
                    return found;
                }
            }
        }
        else{
            if (currentDirectory.getName().equalsIgnoreCase(fileName)){
                return currentDirectory;
            }
        }
        return null;
    }
}
