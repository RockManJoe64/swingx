/*
 * FilePainter.java
 *
 * Created on August 2, 2006, 11:39 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.painter;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JComponent;
import org.jdesktop.swingx.editors.PainterUtil;
import org.joshy.util.u;

/**
 *
 * @author joshy
 */
public class FilePainter extends CompoundPainter{
    URL file;
    /** Creates a new instance of FilePainter */
    public FilePainter(File file) {
        try {
        this.file = file.toURL();
        } catch (MalformedURLException exception) {
            u.p(exception);
            this.file = null;
        }
    }
    
    public FilePainter() {
        
    }
    
    public FilePainter(String url) {
        try {
            this.file = new URL(url);
        } catch (MalformedURLException ex) {
            u.p(ex);
            this.file = null;
        }
    }
    public FilePainter(Class baseClass, String resource) {
        file = baseClass.getResource(resource);
    }
    
    public void setFile(URL file) {
        URL old = this.file;
        this.file = file;
        firePropertyChange("file",old,this.file);
    }
    public URL getFile() {
        return this.file;
    }
    
    private boolean loaded = false;
    private void load() {
        try {
            Painter painter;
            painter = PainterUtil.loadPainter(file);
            this.setPainters(new Painter[] { painter } );
            loaded = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        if(!loaded) {
            load();
        }
        super.paintBackground(g, component, width, height);
    }
   
    
}
