/*
 * ImagePainterTest.java
 * JUnit based test
 *
 * Created on December 18, 2006, 12:51 PM
 */

package org.jdesktop.swingx.painter;

import com.jhlabs.image.BlurFilter;
import junit.framework.TestCase;
import junit.framework.*;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.effects.AreaEffect;

/**
 *
 * @author joshy
 */
public class ImagePainterTest extends TestCase {
    
    public ImagePainterTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testPaintBackground() {
        System.out.println("paintBackground");
        
        BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImagePainter painter = new ImagePainter(image);
        painter.setFilters(new BlurFilter());

        BufferedImage canvas = new BufferedImage(100,100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();
        Object component = null;
        int width = 100;
        int height = 100;
        
        //paint normally
        painter.doPaint(g, component, width, height);
        
        //painte with horizontal repeat
        painter.setHorizontalRepeat(true);
        painter.doPaint(g, component, width, height);
    }

}
