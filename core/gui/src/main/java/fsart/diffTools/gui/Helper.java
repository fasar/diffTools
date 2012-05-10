package fsart.diffTools.gui;

import java.io.File;

/**
 * User: fabien
 * Date: 10/05/12
 * Time: 19:28
 */
public class Helper {

    public static String getTypeOfFile(String file) {
        String[] filetb = file.split("\\.");
        if(filetb.length > 1) {
            return filetb[filetb.length - 1];
        } else {
            return "";
        }
    }

    public static Boolean isGoodFile(String file) {
        File fic = new File(file);
        if(fic!=null && fic.exists() && fic.isFile() && fic.canRead()) {
            return true;
        }
        return false;
    }
}
