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

package org.jdesktop.swingx;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.jdesktop.binding.Binding;
import org.jdesktop.binding.BindingContext;
import org.jdesktop.binding.impl.ColumnBinding;
import org.jdesktop.binding.impl.ManyToOneStrategy;
import org.jdesktop.conversion.Converter;
import org.jdesktop.swingx.binding.AWTColumnBinding;
import org.jdesktop.swingx.binding.JXImagePanelBinding;
import org.jdesktop.swingx.validation.ValidationDecorator;
import org.jdesktop.swingx.validation.ValidationDecoratorFactory;
import org.jdesktop.validation.Validator;


/**
 * <p>A panel that draws an image. The standard (and currently only supported)
 * mode is to draw the specified image starting at position 0,0 in the
 * panel. The component&amp;s preferred size is based on the image, unless
 * explicitly set by the user.</p>
 *
 * <p>In the future, the JXImagePanel will also support tiling of images,
 * scaling, resizing, cropping, segways etc.</p>
 *
 * <p>This component also supports allowing the user to set the image. If the
 * <code>JXImagePanel</code> is editable, then when the user clicks on the
 * <code>JXImagePanel</code> a FileChooser is shown allowing the user to pick
 * some other image to use within the <code>JXImagePanel</code>.</p>
 *
 * <p>Images to be displayed can be set based on URL, Image, etc.
 *
 * @author unattributed, rbair
 */
public class JXImagePanel extends JXPanel implements DataAware {
    public static enum Style {CENTERED, TILED, SCALED};
    private static final Logger LOG = Logger.getLogger(JXImagePanel.class
            .getName());
    /**
     * Text informing the user that clicking on this component will allow them to set the image
     */
    private static final String TEXT = "<html><i><b>Click here<br>to set the image</b></i></html>";
    /**
     * The image to draw
     */
    private Image img;
    /**
     * If true, then the image can be changed. Perhaps a better name is
     * &quot;readOnly&quot;, but editable was chosen to be more consistent
     * with other Swing components.
     */
    private boolean editable = false;
    /**
     * The mouse handler that is used if the component is editable
     */
    private MouseHandler mhandler = new MouseHandler();
    /**
     * If not null, then the user has explicitly set the preferred size of
     * this component, and this should be honored
     */
    private Dimension preferredSize;
    /**
     * Specifies how to draw the image, i.e. what kind of Style to use
     * when drawing
     */
    private Style style = Style.CENTERED;
    
    public JXImagePanel() {
    }
    
    public JXImagePanel(URL imageUrl) {
        try {
            setImage(ImageIO.read(imageUrl));
        } catch (Exception e) {
            //TODO need convert to something meaningful
           LOG.log(Level.WARNING, "", e);
        }
    }
    
    /**
     * Sets the image to use for the background of this panel. This image is
     * painted whether the panel is opaque or translucent.
     * @param image if null, clears the image. Otherwise, this will set the
     * image to be painted. If the preferred size has not been explicitly set,
     * then the image dimensions will alter the preferred size of the panel.
     */
    public void setImage(Image image) {
        if (image != img) {
            Image oldImage = img;
            img = image;
            firePropertyChange("image", oldImage, img);
            invalidate();
            repaint();
        }
    }
    
    /**
     * @return the image used for painting the background of this panel
     */
    public Image getImage() {
        return img;
    }
    
    /**
     * @param editable
     */
    public void setEditable(boolean editable) {
        if (editable != this.editable) {
            //if it was editable, remove the mouse handler
            if (this.editable) {
                removeMouseListener(mhandler);
            }
            this.editable = editable;
            //if it is now editable, add the mouse handler
            if (this.editable) {
                addMouseListener(new MouseHandler());
            }
            setToolTipText(editable ? TEXT : "");
            firePropertyChange("editable", !editable, editable);
            repaint();
        }
    }
    
    /**
     * @return whether the image for this panel can be changed or not via
     * the UI. setImage may still be called, even if <code>isEditable</code>
     * returns false.
     */
    public boolean isEditable() {
        return editable;
    }
    
    /**
     * Sets what style to use when painting the image
     *
     * @param s
     */
    public void setStyle(Style s) {
        if (style != s) {
            Style oldStyle = style;
            style = s;
            firePropertyChange("style", oldStyle, s);
            repaint();
        }
    }

    /**
     * @return the Style used for drawing the image (CENTERED, TILED, etc).
     */
    public Style getStyle() {
        return style;
    }
    
    public void setPreferredSize(Dimension pref) {
        preferredSize = pref;
        super.setPreferredSize(pref);
    }
    
    public Dimension getPreferredSize() {
        if (preferredSize == null && img != null) {
            //it has not been explicitly set, so return the width/height of the image
            int width = img.getWidth(null);
            int height = img.getHeight(null);
            if (width == -1 || height == -1) {
                return super.getPreferredSize();
            }
            return new Dimension(width, height);
        } else {
            return super.getPreferredSize();
        }
    }
    
    /**
     * Overriden to paint the image on the panel
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Insets insets = getInsets();
//        g.fillRect(insets.left, insets.top, getWidth() - insets.right - insets.left, getHeight() - insets.bottom - insets.top);
        Graphics2D g2 = (Graphics2D)g;
        if (img != null) {
            int imgWidth = img.getWidth(null);
            int imgHeight = img.getHeight(null);
            if (imgWidth == -1 || imgHeight == -1) {
                //image hasn't completed loading, return
                return;
            }
            
            switch (style) {
                case CENTERED:
                    Rectangle clipRect = g2.getClipBounds();
                    int imageX = (getWidth() - imgWidth) / 2;
                    int imageY = (getHeight() - imgHeight) / 2;
                    Rectangle r = SwingUtilities.computeIntersection(imageX, imageY, imgWidth, imgHeight, clipRect);
                    if (r.x == 0 && r.y == 0 && (r.width == 0 || r.height == 0)) {
                        return;
                    }
                    //I have my new clipping rectangle "r" in clipRect space.
                    //It is therefore the new clipRect.
                    clipRect = r;
                    //since I have the intersection, all I need to do is adjust the
                    //x & y values for the image
                    int txClipX = clipRect.x - imageX;
                    int txClipY = clipRect.y - imageY;
                    int txClipW = clipRect.width;
                    int txClipH = clipRect.height;
                    
                    g2.drawImage(img, clipRect.x, clipRect.y, clipRect.x + clipRect.width, clipRect.y + clipRect.height,
                            txClipX, txClipY, txClipX + txClipW, txClipY + txClipH, null);
                    break;
                case TILED:
                case SCALED:
                    Image temp = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g2.drawImage(temp, (getWidth() - temp.getWidth(null)) / 2,
                            (getHeight() - temp.getHeight(null)) / 2, null);
                    break;
                default:
                    LOG.fine("unimplemented");
                    g2.drawImage(img, 0, 0, this);
                    break;
            }
        }
    }
    
    /**
     * Handles click events on the component
     */
    private class MouseHandler extends MouseAdapter {
        private Cursor oldCursor;
        private JFileChooser chooser;
        
        public void mouseClicked(MouseEvent evt) {
            if (chooser == null) {
                chooser = new JFileChooser();
            }
            int retVal = chooser.showOpenDialog(JXImagePanel.this);
            if (retVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    setImage(new ImageIcon(file.toURL()).getImage());
                } catch (Exception ex) {
                }
            }
        }
        
        public void mouseEntered(MouseEvent evt) {
            JXImagePanel label = (JXImagePanel)evt.getSource();
            if (oldCursor == null) {
                oldCursor = label.getCursor();
                label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        }
        
        public void mouseExited(MouseEvent evt) {
            JXImagePanel label = (JXImagePanel)evt.getSource();
            if (oldCursor != null) {
                label.setCursor(oldCursor);
                oldCursor = null;
            }
        }
    }

    /*************      Data Binding    ****************/
    private String dataPath = "";
    private BindingContext ctx = null;
    private JXImagePanelBinding binding;
    private AWTColumnBinding.AutoCommit autoCommit = AWTColumnBinding.AutoCommit.NEVER;
    private Object conversionFormat = null;
    private Converter converter = null;
    private ManyToOneStrategy manyToOneStrategy = null;
    private ValidationDecorator validationDecorator = ValidationDecoratorFactory.getSeverityBackgroundTooltipDecorator();
    private Object validationKey = null;
    private Validator validator = null;
    
    /**
     * @inheritDoc
     */
    public JXImagePanelBinding getBinding() {
        return binding;
    }
    
    /**
     * @inheritDoc
     */
    public Object getDomainData() {
        return binding == null ? null : binding.getDomainData();
    }
    
    /**
     * @param path
     */
    public void setDataPath(String path) {
        path = path == null ? "" : path;
        if (!this.dataPath.equals(path)) {
            String oldPath = this.dataPath;
            this.dataPath = path;
            firePropertyChange("dataPath", oldPath, this.dataPath);
            DataBoundUtils.unbind(ctx, this);
            bind();
        }
    }
    
    public String getDataPath() {
        return dataPath;
    }

    public void setBindingContext(BindingContext ctx) {
        if (this.ctx != ctx) {
            BindingContext old = this.ctx;
            this.ctx = ctx;
            firePropertyChange("bindingContext", old, ctx);
            DataBoundUtils.unbind(old, this);
            bind();
        }
    }
    
    private void bind() {
        binding = (JXImagePanelBinding)DataBoundUtils.bind(ctx, this, dataPath);
        if (binding != null) {
            binding.setAutoCommit(autoCommit);
            binding.setConversionFormat(conversionFormat);
            binding.setConverter(converter);
            binding.setManyToOneStrategy(manyToOneStrategy);
            binding.setValidationDecorator(validationDecorator);
            binding.setValidationKey(validationKey);
            binding.setValidator(validator);
        }
    }

    public BindingContext getBindingContext() {
        return ctx;
    }
    
    public AWTColumnBinding.AutoCommit getAutoCommit() {
        return autoCommit;
    }
    
    public void setAutoCommit(AWTColumnBinding.AutoCommit autoCommit) {
        Object old = this.autoCommit;
        this.autoCommit = autoCommit;
        if (binding != null) {
            binding.setAutoCommit(autoCommit);
        }
        firePropertyChange("autoCommit", old, autoCommit);
    }
    
    public Object getConversionFormat() {
        return conversionFormat;
    }
    
    public void setConversionFormat(Object conversionFormat) {
        Object old = this.conversionFormat;
        this.conversionFormat = conversionFormat;
        if (binding != null) {
            binding.setConversionFormat(conversionFormat);
        }
        firePropertyChange("conversionFormat", old, conversionFormat);
    }
    
    public Converter getConverter() {
        return converter;
    }
    
    public void setConverter(Converter converter) {
        Object old = this.converter;
        this.converter = converter;
        if (binding != null) {
            binding.setConverter(converter);
        }
        firePropertyChange("converter", old, converter);
    }
    
    public ManyToOneStrategy getManyToOneStrategy() {
        return manyToOneStrategy;
    }
    
    public void setManyToOneStrategy(ManyToOneStrategy manyToOneStrategy) {
        Object old = this.manyToOneStrategy;
        this.manyToOneStrategy = manyToOneStrategy;
        if (binding != null) {
            binding.setManyToOneStrategy(manyToOneStrategy);
        }
        firePropertyChange("manyToOneStrategy", old, manyToOneStrategy);
    }
    
    public ValidationDecorator getValidationDecorator() {
        return validationDecorator;
    }
    
    public void setValidationDecorator(ValidationDecorator validationDecorator) {
        Object old = this.validationDecorator;
        this.validationDecorator = validationDecorator;
        if (binding != null) {
            binding.setValidationDecorator(validationDecorator);
        }
        firePropertyChange("validationDecorator", old, validationDecorator);
    }
    
    public String getValidationKey() {
        return (String)validationKey;
    }
    
    public void setValidationKey(String validationKey) {
        Object old = this.validationKey;
        this.validationKey = validationKey;
        if (binding != null) {
            binding.setValidationKey(validationKey);
        }
        firePropertyChange("validationKey", old, validationKey);
    }
    
    public Validator getValidator() {
        return validator;
    }
    
    public void setValidator(Validator validator) {
        Object old = this.validator;
        this.validator = validator;
        if (binding != null) {
            binding.setValidator(validator);
        }
        firePropertyChange("validator", old, validator);
    }
        
    /**
     * @inheritDoc
     * Overridden so that if no binding context has been specified for this
     * component by this point, then we'll try to locate a BindingContext
     * somewhere in the containment heirarchy.
     */
    public void addNotify() {
        super.addNotify();
        if (ctx == null && DataBoundUtils.isValidPath(dataPath)) {
            setBindingContext(DataBoundUtils.findBindingContext(this));
        }
    }
    
//
//    //BEANS SPECIFIC CODE:
//    private boolean designTime = false;
//    public void setDesignTime(boolean designTime) {
//        this.designTime = designTime;
//    }
//    public boolean isDesignTime() {
//        return designTime;
//    }
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        if (designTime && dataPath != null && !dataPath.equals("")) {
//            //draw the binding icon
//            ImageIcon ii = new ImageIcon(getClass().getResource("icon/chain.png"));
//            g.drawImage(ii.getImage(), getWidth() - ii.getIconWidth(), 0, ii.getIconWidth(), ii.getIconHeight(), ii.getImageObserver());
//        }
//    }
}

