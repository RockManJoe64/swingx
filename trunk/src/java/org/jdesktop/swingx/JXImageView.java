/*
 * JXImageView.java
 *
 * Created on April 25, 2006, 9:31 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.event.MouseInputAdapter;

/**
 * a panel which shows an image centered. the user can drag an image into the panel
 * to display it. The view has built in actions for 
 *  scaling, 
 * rotating,  
 * opening a new image, and 
 * cropping.
 *
 * has dashed rect and text indicating you should drag there.
 *
 * allows user to drag image within the panel, if allowed. shows move cursor
 * or hand cursor.
 *
 * allows to set a crop/ restriction rect, if allowed
 *
 *
 * JXImageView allows a user to drag photos into the well. If the user drags 
 * more than one photo at a time the first photo will be loaded and shown in the well. 
 * 
 * JXImageView provides actions to do common image operations like rotation, opening
 * and saving, and zooming.
 *
 * @author joshy
 */
public class JXImageView extends JXPanel {
    
    /* ======= instance variables ========= */
    // the image this view will show
    private Image image;
    
    // location to draw image. if null then draw in the center
    private Point2D imageLocation;
    

    /** Creates a new instance of JXImageView */
    public JXImageView() {
        MouseInputAdapter mia = new MoveHandler();
        addMouseMotionListener(mia);
        addMouseListener(mia);
        
        this.setTransferHandler(new TransferHandler() {
            public boolean canImport(JComponent c, DataFlavor[] flavors) {
                for(int i=0; i<flavors.length; i++) {
                    if(DataFlavor.javaFileListFlavor.equals(flavors[i])) {
                        return true;
                    }/*
                    if(DataFlavor.javaJVMLocalObjectMimeType.equals(flavors[i])) {
                        return true;
                    }*/
                }
                return false;
            }
            
            public boolean importData(JComponent comp, Transferable t) {
                if (canImport(comp,t.getTransferDataFlavors())) {
                    try {
                        List files = (List)t.getTransferData(DataFlavor.javaFileListFlavor);
                        if(files.size() > 0) {
                            File file = (File)files.get(0);
                            BufferedImage img = ImageIO.read(file);
                            setImage(img);
                            return true;
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                return false;
            }

        });
        
    }

    /* ========= properties ========= */
    public Point2D getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(Point2D imageLocation) {
        this.imageLocation = imageLocation;
    }
    
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        setImageLocation(null);
        repaint();
    }
    
    public void setImage(URL url) throws IOException {
        setImage(ImageIO.read(url));
    }
    
    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }
    
    // an action which will open a file chooser and load the selected image
    // if any.
    public Action getOpenAction() {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser chooser = new JFileChooser();
                chooser.showOpenDialog(JXImageView.this);
                File file = chooser.getSelectedFile();
                if(file != null) {
                    try {
                        setImage(file);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        };
        action.putValue(Action.NAME,"Open");
        return action;
    }
    
    // an action that will open a file chooser then save the current image to
    // the selected file, if any.
    public Action getSaveAction() {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                Image img = getImage();
                BufferedImage dst = new BufferedImage(
                            img.getWidth(null),
                            img.getHeight(null), 
                            BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D)dst.getGraphics();
                // smooth scaling
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                   RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.drawImage(img,0,0,null);
                g.dispose();
                JFileChooser chooser = new JFileChooser();
                chooser.showSaveDialog(JXImageView.this);
                File file = chooser.getSelectedFile();
                if(file != null) {
                    try {
                        ImageIO.write(dst,"png",file);
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        };

        action.putValue(Action.NAME,"Save");
        return action;
    }
    
    public Action getRotateClockwiseAction() {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                Image img = getImage();
                BufferedImage src = new BufferedImage(
                            img.getWidth(null),
                            img.getHeight(null), 
                            BufferedImage.TYPE_INT_ARGB);
                BufferedImage dst = new BufferedImage(
                            img.getHeight(null), 
                            img.getWidth(null),
                            BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D)src.getGraphics();
                // smooth scaling
                g.drawImage(img,0,0,null);
                g.dispose();
                AffineTransform trans = AffineTransform.getRotateInstance(Math.PI/2,0,0);
                trans.translate(0,-src.getHeight());
                BufferedImageOp op = new AffineTransformOp(trans, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                Rectangle2D rect = op.getBounds2D(src);
                op.filter(src,dst);
                setImage(dst);
            }
        };
        action.putValue(Action.NAME,"Rotate Clockwise");
        return action;        
    }
    
    public Action getRotateCounterClockwiseAction() {
        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent evt) {
                Image img = getImage();
                BufferedImage src = new BufferedImage(
                            img.getWidth(null),
                            img.getHeight(null), 
                            BufferedImage.TYPE_INT_ARGB);
                BufferedImage dst = new BufferedImage(
                            img.getHeight(null), 
                            img.getWidth(null),
                            BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = (Graphics2D)src.getGraphics();
                // smooth scaling
                g.drawImage(img,0,0,null);
                g.dispose();
                AffineTransform trans = AffineTransform.getRotateInstance(-Math.PI/2,0,0);
                trans.translate(-src.getWidth(),0);
                BufferedImageOp op = new AffineTransformOp(trans, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                Rectangle2D rect = op.getBounds2D(src);
                op.filter(src,dst);
                setImage(dst);
            }
        };
        action.putValue(Action.NAME, "Rotate CounterClockwise");
        return action;        
    }
    
    /* === overriden methods === */
    
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0,0,getWidth(),getHeight());
        if(getImage() != null) {
            if(getImageLocation() == null) {
                g.drawImage(getImage(),
                        (getWidth()-getImage().getWidth(null))/2,
                        (getHeight()-getImage().getHeight(null))/2,
                        null);
            } else {
                g.drawImage(getImage(),
                        (int)getImageLocation().getX(),
                        (int)getImageLocation().getY(),
                        null);
            }
        }
    }

    
    /* === Internal helper classes === */

    private class MoveHandler extends MouseInputAdapter {

        private Point prev = null;

        public void mousePressed(MouseEvent evt) {
            prev = evt.getPoint();
        }

        public void mouseDragged(MouseEvent evt) {
            Point curr = evt.getPoint();
            int offx = curr.x - prev.x;
            int offy = curr.y - prev.y;
            Point2D offset = getImageLocation();
            if (offset == null) {
                if (image != null) {
                    offset = new Point2D.Double((getWidth() - getImage().getWidth(null)) / 2, (getHeight() - getImage().getHeight(null)) / 2);
                } else {
                    offset = new Point2D.Double(0, 0);
                }
            }
            offset = new Point2D.Double(offset.getX() + offx, offset.getY() + offy);
            setImageLocation(offset);
            prev = curr;
            repaint();
        }

        public void mouseReleased(MouseEvent evt) {
            prev = null;
        }
    }

    
}
