/*
 * JXImagePanelBinding.java
 *
 * Created on September 13, 2005, 10:29 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;
import org.jdesktop.conversion.ConversionException;
import org.jdesktop.binding.FieldBinding;
import org.jdesktop.swingx.JXImagePanel;

/**
 * Hmmmm....
 * The problem here is that the data in the image panel could be bits, image url,
 * etc. In fact, there isn't just one data type that I want to support, but a few
 * of them. That is, if I get a String, then it is a URL of some kind. If I get
 * an ImageIcon, us that, a BufferedImage, use that, a set of bytes, then use
 * that.
 *
 * @author Richard
 */
public class JXImagePanelBinding extends FieldBinding {
    private BufferedImage oldImage;
    
    /** Creates a new instance of JXImagePanelBinding */
    public JXImagePanelBinding(JXImagePanel panel) {
        super(panel, BufferedImage.class);
    }

    protected Object convert(Object src, Class dstType) throws ConversionException {
        if (dstType == BufferedImage.class) {
            try {
                //converting to BufferedImage:
                if (src instanceof byte[]) {
                    return ImageIO.read(new ByteArrayInputStream((byte[])src));
                } else if (src instanceof String) {
                    return ImageIO.read(new URL((String)src));
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

    protected void initialize() {
        oldImage = getComponent().getImage();
    }

    public void release() {
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
}
