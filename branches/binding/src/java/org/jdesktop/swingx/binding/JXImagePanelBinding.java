/*
 * JXImagePanelBinding.java
 *
 * Created on September 13, 2005, 10:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import org.jdesktop.conversion.ConversionException;
import org.jdesktop.swingx.JXImagePanel;

/**
 * A binding for the JXImagePanel component.
 * The data in the image panel could be bits, image url,
 * etc. In fact, there isn't just one data type that I want to support, but a few
 * of them. That is, if I get a String, then it is a URL of some kind. If I get
 * an ImageIcon, use that, a BufferedImage, use that, a set of bytes, then use
 * that.
 *
 * TODO JXImagePanel, if it will indeed support editability, must be refactored.
 * The problem is that the image to view can be specified by URL, or by actually
 * passing in the Image. If editable, then the URL specifies what the image is
 * (otherwise how could I save what the user's image choice was?). This gets
 * weird quick.
 *
 * <strong>This Binding should be considered HIGHLY EXPERIMENTAL and not
 * used in a production application</strong>
 *
 * @author Richard
 */
public class JXImagePanelBinding extends SwingColumnBinding {
    private Image oldImage;
    
    /** Creates a new instance of JXImagePanelBinding */
    public JXImagePanelBinding(JXImagePanel panel) {
        super(panel, BufferedImage.class);
    }

    /**
     * @inheritDoc
     * 
     * This method is overridden to perform a custom conversion. I could.. ya, will.
     */
    protected Object convert(Object src, Class dstType) throws ConversionException {
        if (src == null) {
            return null;
        }
        if (dstType == BufferedImage.class) {
            try {
                //converting to BufferedImage:
                if (src instanceof byte[]) {
                    return ImageIO.read(new ByteArrayInputStream((byte[])src));
                } else if (src instanceof String) {
                    try {
                        return ImageIO.read(new URL((String)src));
                    } catch (Exception e) {
                        return ImageIO.read(new FileInputStream((String)src));
                    }
                } else if (src instanceof URL) {
                    return ImageIO.read((URL)src);
                } else if (src instanceof File) {
                    return ImageIO.read((File)src);
                } else if (src instanceof BufferedImage) {
                    return (BufferedImage)src;
    //            } else if (src instanceof ImageIcon) {
    //                return ((ImageIcon)src).getImage().
                } else {
                    throw new ConversionException("Unable to convert " + src + " to BufferedImage");
                }
            } catch (ConversionException ce) {
                throw ce;
            } catch (Exception e) {
                    throw new ConversionException("Unable to convert String " + src + " to BufferedImage", e);
            }
        } else {
            //converting from BufferedImage
            assert src instanceof BufferedImage;
            if (dstType == BufferedImage.class) {
                return (BufferedImage)src;
            } else {
                throw new ConversionException("Unable to convert from BufferedImage to " + dstType);
            }
        }
    }

    protected void doInitialize() {
        oldImage = getComponent().getImage();
    }

    public void doRelease() {
        getComponent().setImage(oldImage);
    }
    
    protected Object getComponentValue() {
        return getComponent().getImage();
    }

    protected void setComponentValue(Object obj) {
        getComponent().setImage((BufferedImage)obj);
    }
    
    public JXImagePanel getComponent() {
        return (JXImagePanel)super.getComponent();
    }

    protected void setComponentEditable(boolean editable) {
        getComponent().setEditable(editable);
    }
}
