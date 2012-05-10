package fsart.diffTools.gui;

import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Vector;

import fsart.diffTools.consoleApp.*;
import fsart.helper.Loader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;

// to work with it must add some intellij libs.
//     <pathelement location="${idea.lib}/javac2.jar"/>
//    <pathelement location="${idea.lib}/jdom.jar"/>
//    <pathelement location="${idea.lib}/asm.jar"/>
//    <pathelement location="${idea.lib}/asm-commons.jar"/>
//    <pathelement location="${idea.lib}/jgoodies-forms.jar"/>


/**
 * User: fabien
 * Date: 24/04/12
 * Time: 20:17
 */
public class DiffToolsMainPanel {
    private JPanel panel1;
    private JButton browseButton;
    private JButton browseButton1;
    private JButton compareButton;
    private JLabel comparedFileLabel;
    private JLabel baseFileLabel;
    private JTextField baseFileTxt;
    private JTextField comparedFileTxt;
    private JList listfic1;
    private JList listfic2;

    private JFileChooser fc = new JFileChooser();

    private Log log = LogFactory.getLog(this.getClass());

    public DiffToolsMainPanel() {
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String res = getFileGui();
                if(Helper.isGoodFile(res)) {
                    baseFileTxt.setText(res);
                    appendExcelSheet(res, listfic1);
                }
            }
        });

        browseButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String res = getFileGui();
                if(Helper.isGoodFile(res)) {
                    comparedFileTxt.setText(res);
                    appendExcelSheet(res, listfic2);
                }
            }

        });
        compareButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                StringBuilder args = new StringBuilder();
                args.append("-o;output.xls;" );
                args.append(baseFileTxt.getText() );
                if(!listfic1.isSelectionEmpty()) {
                    args.append(":");
                    String sheetName = (String)listfic1.getSelectedValue();
                    args.append(sheetName);
                }
                args.append(";");
                args.append(comparedFileTxt.getText() );
                if(!listfic2.isSelectionEmpty()) {
                    args.append(":");
                    String sheetName = (String)listfic2.getSelectedValue();
                    args.append(sheetName);
                }
                log.debug("launching DiffTools console with args : " +args.toString() );
                launchDiffTools(args.toString().split(";"));
            }
        });
    }

    private void launchDiffTools(String[] args) {
        try {

            DiffToolsCalc.main(args);
            JOptionPane.showMessageDialog((Component) this.getPanel(), "Ok, output is generated"); //, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            log.error(e.toString() + "\n" + strackTraceToString(e.getStackTrace()));
            JOptionPane.showMessageDialog((Component) this.getPanel(), e.toString()); //, JOptionPane.ERROR_MESSAGE);
        }
    }

    private String strackTraceToString(StackTraceElement[] strs) {
        StringBuilder res = new StringBuilder();
        for(int i=0; i<strs.length; i++) {
            res.append(strs[i].toString());
            res.append("\n");
        }
        return res.toString();
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JPanel getPanel() {
        return panel1;
    }

    private String getFileGui() {

        int returnVal = fc.showOpenDialog(panel1);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                return fc.getSelectedFile().getCanonicalPath();
            } catch (Exception e) {
                log.error("Can't get file with FileChooser \n" + e.toString() + "\n" + e.getStackTrace());
                return "";
            }
        }
        return "";
    }

    private void appendExcelSheet(String excelFile, JList listfic)  {
        System.out.println("Entre append excel shhe with " + excelFile);
        String type = Helper.getTypeOfFile(excelFile);
        Vector<String> emptyList = new Vector<String>();
        listfic.setListData(emptyList);
        System.out.println("type of file : " + type);
        if(type.equals("xls")) {
            System.out.println("ok1");
            InputStream inp = null;
            try {
                inp = new FileInputStream(excelFile);
                HSSFWorkbook wb = new HSSFWorkbook(inp);
                int sheetNb = wb.getNumberOfSheets();
                if(sheetNb > 0 ) {
                    System.out.println("ok2");
                    Vector<String> sheetnames = new Vector<String>();
                    for(int i = 0; i< sheetNb; i++) {
                        Sheet sheet = wb.getSheetAt(i);
                        sheetnames.addElement(sheet.getSheetName());
                        System.out.println("J'ai trouvé : " + sheet.getSheetName());
                    }
                    listfic.setListData(sheetnames);
                }
            } catch (Exception e) {}

        }

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, BorderLayout.WEST);
        final Spacer spacer2 = new Spacer();
        panel1.add(spacer2, BorderLayout.EAST);
        final Spacer spacer3 = new Spacer();
        panel1.add(spacer3, BorderLayout.SOUTH);
        final Spacer spacer4 = new Spacer();
        panel1.add(spacer4, BorderLayout.NORTH);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("left:max(d;4px):noGrow,left:4dlu:noGrow,center:max(d;50px):grow,left:4dlu:noGrow,fill:max(d;4px):noGrow,left:5dlu:noGrow,fill:max(d;4px):noGrow", "top:d:grow,top:4dlu:noGrow,top:d:noGrow,top:4dlu:noGrow,center:max(d;25px):noGrow,top:6dlu:noGrow,top:max(d;4px):noGrow,top:4dlu:noGrow,top:max(d;25px):noGrow,top:7dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,bottom:d:grow"));
        panel2.putClientProperty("html.disable", Boolean.FALSE);
        panel1.add(panel2, BorderLayout.CENTER);
        final Spacer spacer5 = new Spacer();
        CellConstraints cc = new CellConstraints();
        panel2.add(spacer5, cc.xy(3, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        browseButton = new JButton();
        browseButton.setText("Browse");
        panel2.add(browseButton, cc.xy(5, 5));
        comparedFileLabel = new JLabel();
        comparedFileLabel.setText("Compared file :");
        panel2.add(comparedFileLabel, cc.xy(3, 7, CellConstraints.LEFT, CellConstraints.TOP));
        browseButton1 = new JButton();
        browseButton1.setText("Browse");
        panel2.add(browseButton1, cc.xy(5, 9, CellConstraints.DEFAULT, CellConstraints.TOP));
        final Spacer spacer6 = new Spacer();
        panel2.add(spacer6, cc.xy(3, 13, CellConstraints.DEFAULT, CellConstraints.FILL));
        final Spacer spacer7 = new Spacer();
        panel2.add(spacer7, cc.xy(1, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
        final Spacer spacer8 = new Spacer();
        panel2.add(spacer8, cc.xy(7, 7, CellConstraints.FILL, CellConstraints.DEFAULT));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        panel2.add(panel3, cc.xyw(3, 11, 3, CellConstraints.FILL, CellConstraints.FILL));
        compareButton = new JButton();
        compareButton.setPreferredSize(new Dimension(100, 29));
        compareButton.setText("Compare");
        panel3.add(compareButton, cc.xy(1, 1, CellConstraints.CENTER, CellConstraints.CENTER));
        baseFileLabel = new JLabel();
        baseFileLabel.setText("Base file :");
        panel2.add(baseFileLabel, cc.xy(3, 3, CellConstraints.FILL, CellConstraints.FILL));
        baseFileTxt = new JTextField();
        baseFileTxt.setMinimumSize(new Dimension(50, 27));
        baseFileTxt.setPreferredSize(new Dimension(50, 27));
        panel2.add(baseFileTxt, cc.xy(3, 5, CellConstraints.FILL, CellConstraints.DEFAULT));
        comparedFileTxt = new JTextField();
        comparedFileTxt.setMinimumSize(new Dimension(50, 27));
        comparedFileTxt.setPreferredSize(new Dimension(100, 27));
        panel2.add(comparedFileTxt, cc.xy(3, 9, CellConstraints.FILL, CellConstraints.DEFAULT));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
