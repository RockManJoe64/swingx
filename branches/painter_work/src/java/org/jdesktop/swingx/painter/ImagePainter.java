/*
 * $Id$
 *
 * Copyright 2004 Sun Microsystems, Inc., 4150 Network Circle,
 * Santa Clara, California 95054, U.S.A. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jdesktop.swingx.painter;

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
import org.jdesktop.swingx.painter.effects.PathEffect;

/**
 * <p>A Painter instance that paints an image. Any Image is acceptable. This
 * Painter also allows the developer to specify a "Style" -- CENTERED, TILED,
 * SCALED, POSITIONED, and CSS_POSITIONED; with the following meanings:</p>
 *
 * <ul>
 *  <li><b>CENTERED</b>: draws the image unscaled and positioned in the center of
 * the component</li>
 *  <li><b>TILED</b>: draws the image repeatedly across the component, filling the
 * entire background.</li>
 *  <li><b>SCALED</b>: draws the image stretched large enough (or small enough) to
 * cover the entire component. The stretch may not preserve the aspect ratio of the
 * original image.</li>
 *  <li><b>POSITIONED</b>: draws the image at the location specified by the imageLocation
 * property. This style of drawing will respect the imageScale property.</li>
 *  <li><b>CSS_POSITIONED</b>: draws the image using CSS style background positioning.
 *It will use the location specified by the imageLocation property. This property should
 *contain a point with the x and y values between 0 and 1. 0,0 will put the image in the
 *upper left hand corner, 1,1 in the lower right, and 0.5,0.5 in the center. All other values
 *will be interpolated accordingly. For a more
 * complete defintion of the positioning algorithm see the
 * <a href="http://www.w3.org/TR/CSS21/colors.html#propdef-background-position">CSS 2.1 spec</a>.
 * </li>
 * </ul>
 *
 * @author Richard
 */
public class ImagePainter extends AbstractPathPainter {
    /**
     * Logger to use
     */
    private static final Logger LOG = Logger.getLogger(ImagePainter.class.getName());
    
    /**
     * The image to draw
     */
    private transient BufferedImage img;
    
    private URL imageURL;
    
    private boolean horizontalRepeat;
    private boolean verticalRepeat;
    
    
    /**
     * Create a new ImagePainter. By default there is no image, and the alignment
     * is centered.
     */
    public ImagePainter() {
        this(null);
    }
    
    /**
     * Create a new ImagePainter with the specified image and the Style
     * Style.CENTERED
     *
     * @param image the image to be painted
     */
    public ImagePainter(BufferedImage image) {
        this(image,HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }
    
    /**
     * Create a new ImagePainter with the specified image and style.
     *
     * @param image the image to be painted
     * @param style the style of the image
     */
    public ImagePainter(BufferedImage image, HorizontalAlignment horizontal, VerticalAlignment vertical) {
        super();
        this.img = image;
        this.setVertical(vertical);
        this.setHorizontal(horizontal);
        this.setFillPaint(null);
        this.setBorderPaint(null);
    }
    
    /**
     * Sets the image to use for the background of this panel. This image is
     * painted whether the panel is opaque or translucent.
     *
     * @param image if null, clears the image. Otherwise, this will set the
     * image to be painted. If the preferred size has not been explicitly set,
     * then the image dimensions will alter the preferred size of the panel.
     */
    public void setImage(BufferedImage image) {
        if (image != img) {
            Image oldImage = img;
            img = image;
            firePropertyChange("image", oldImage, img);
        }
    }
    
    /**
     * @return the image used for painting the background of this panel
     */
    public BufferedImage getImage() {
        p("image url = " + getImageString());
        if(img == null && imageURL != null) {
            loadImage();
        }
        return img;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBackground(Graphics2D g, JComponent component, int width, int height) {
        if (img == null && imageURL != null) {
            loadImage();
        }
        
        Shape shape = provideShape(component,width,height);
        switch (getStyle()) {
            case BOTH:
                drawBackground(g,shape,width,height);
                drawBorder(g,shape,width,height);
                break;
            case FILLED:
                drawBackground(g,shape,width,height);
                break;
            case OUTLINE:
                drawBorder(g,shape,width,height);
                break;
            case NONE:
                break;
        }
    }
    
    private void drawBackground(Graphics2D g, Shape shape, int width, int height) {
        Paint p = getFillPaint();
        
        if(p != null) {
            if(isSnapPaint()) {
                p = calculateSnappedPaint(p, width, height);
            }
            g.setPaint(p);
            g.fill(shape);
        }
        
        if(getPathEffects() != null) {
            for(PathEffect ef : getPathEffects()) {
                ef.apply(g, shape, width, height);
            }
        }
        
        
        if (img != null) {
            int imgWidth = img.getWidth(null);
            int imgHeight = img.getHeight(null);
            if (imgWidth == -1 || imgHeight == -1) {
                //image hasn't completed loading, do nothing
            } else {
                Rectangle rect = calculatePosition(imgWidth, imgHeight, width, height);
                Shape clip = g.getClip();
                Area area = new Area(clip);
                if(verticalRepeat || horizontalRepeat) {
                    TexturePaint tp = new TexturePaint(img,rect);
                    if(verticalRepeat && horizontalRepeat) {
                        area.intersect(new Area(new Rectangle(0,0,width,height)));
                        g.setClip(area);
                    } else if (verticalRepeat) {
                        area.intersect(new Area(new Rectangle(rect.x,0,rect.width,height)));
                        g.setClip(area);//clip.intersection(new Rectangle(rect.x,0,rect.width,height)));
                    } else {
                        area.intersect(new Area(new Rectangle(0,rect.y,width,rect.height)));
                        g.setClip(area);//clip.intersection(new Rectangle(0,rect.y,width,rect.height)));
                    }
                    g.setPaint(tp);
                    g.fillRect(0,0,width,height);
                } else {
                    g.drawImage(img, rect.x, rect.y, rect.width, rect.height, null);
                }
                g.setClip(clip);
            }
        }
        
    }
    
    private void drawBorder(Graphics2D g, Shape shape, int width, int height) {
        if(getBorderPaint() != null) {
            g.setPaint(getBorderPaint());
            g.setStroke(new BasicStroke(getBorderWidth()));
            g.draw(shape);
        }
    }
    
    
    private double imageScale = 1.0;
    
    public void setImageScale(double imageScale) {
        double old = getImageScale();
        this.imageScale = imageScale;
        firePropertyChange("imageScale",old,this.imageScale);
    }
    public double getImageScale() {
        return imageScale;
    }
    
    private PainterUtil.PersistenceOwner resolver = null;
    public void setResolver(PainterUtil.PersistenceOwner resolver) {
        p("resolver has been set: " + resolver);
        this.resolver = resolver;
    }
    public PainterUtil.PersistenceOwner getResolver() {
        return this.resolver;
    }
    
    private void loadImage() {
        try {
            String img = getImageString();
            if(resolver != null) {
                img = resolver.fromXMLURL(img);
            }
            p("image string = " + img);
            if(img != null) {
                if(img.startsWith("http")) {
                    URL url = new URL(img);
                    setImage(ImageIO.read(url));
                    return;
                } else {
                    p("base url = " + getBaseURL());
                    p("image string = " + getImageString());
                    URL url = new URL(getBaseURL()+getImageString());
                    p("final url = " + url);
                    setImage(ImageIO.read(url));
                }
            }
        } catch (IOException ex) {
            System.out.println("ex: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private String imageString;
    
    public String getImageString() {
        return imageString;
    }
    
    public void setImageString(String imageString) {
        System.out.println("setting image string to: " + imageString);
        String old = this.getImageString();
        this.imageString = imageString;
        loadImage();
        firePropertyChange("imageString",old,imageString);
    }
    
    public String getBaseURL() {
        return baseURL;
    }
    
    private String baseURL;
    
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    
    public boolean isHorizontalRepeat() {
        return horizontalRepeat;
    }
    
    public void setHorizontalRepeat(boolean horizontalRepeat) {
        boolean old = this.isHorizontalRepeat();
        this.horizontalRepeat = horizontalRepeat;
        firePropertyChange("horizontalRepeat",old,this.horizontalRepeat);
    }
    
    public boolean isVerticalRepeat() {
        return verticalRepeat;
    }
    
    public void setVerticalRepeat(boolean verticalRepeat) {
        boolean old = this.isVerticalRepeat();
        this.verticalRepeat = verticalRepeat;
        firePropertyChange("verticalRepeat",old,this.verticalRepeat);
    }
    
    public Shape provideShape(JComponent comp, int width, int height) {
        if(getImage() != null) {
            BufferedImage img = getImage();
            int imgWidth = img.getWidth();
            int imgHeight = img.getHeight();
            Rectangle rect = calculatePosition(imgWidth, imgHeight, width, height);
            return rect;
        }
        return new Rectangle(0,0,0,0);
        
    }

    private void p(String string) {
        System.out.println(string);
    }
    
}
