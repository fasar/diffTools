package fsart.diffTools.gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;

/**
 * User: fabien
 * Date: 24/04/12
 * Time: 21:39
 */
public class DiffToolsGui {

    static private Log log = LogFactory.getLog(DiffToolsGui.class);


    public static void main(String[] args) {
        //Create and set up the window.
        JFrame frame = new JFrame("FileChooserDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        log.debug("Demarrage de l'application");

        DiffToolsMainPanel gui = new DiffToolsMainPanel();

        frame.add(gui.getPanel());
        frame.pack();
        frame.setMinimumSize(gui.getPanel().getMinimumSize());
        frame.setLocationRelativeTo(frame.getParent());
        frame.setVisible(true);
        //System.exit(0);
    }
}
