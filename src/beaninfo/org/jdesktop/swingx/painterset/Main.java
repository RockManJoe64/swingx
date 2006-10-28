/*
 * Main.java
 *
 * Created on July 12, 2006, 2:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painterset;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.jdesktop.swingx.editors.PaintPicker;


/**
 *
 * @author jm158417
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
                JFrame frame = new JFrame("Painter Editor");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                PainterEditorPanel pep = new PainterEditorPanel();
                frame.add(pep);
                frame.setJMenuBar(pep.menuBar);
                frame.pack();
                frame.setSize(600,750);
                frame.setVisible(true);
            }
        });
    }
    
}
