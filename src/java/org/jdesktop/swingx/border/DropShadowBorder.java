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

package org.jdesktop.swingx.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Implements a DropShadow for components. In general, the DropShadowBorder will
 * work with any rectangular components that do not have a default border installed
 * as part of the look and feel, or otherwise. For example, DropShadowBorder works
 * wonderfully with JPanel, but horribly with JComboBox.
 *
 * @author rbair
 */
public class DropShadowBorder implements Border {
    private static enum Position {TOP, TOP_LEFT, LEFT, BOTTOM_LEFT,
                    BOTTOM, BOTTOM_RIGHT, RIGHT, TOP_RIGHT};
                    
    private static final Map<Integer,Map<Position,BufferedImage>> CACHE 
            = new HashMap<Integer,Map<Position,BufferedImage>>();
                        
    private Color lineColor;
    private int lineWidth;
    private int shadowSize;
    private float shadowOpacity;
    private int cornerSize;
    private boolean showTopShadow;
    private boolean showLeftShadow;
    private boolean showBottomShadow;
    private boolean showRightShadow;
    
    public DropShadowBorder() {
        this(UIManager.getColor("Control"), 1, 5);
    }
    
    public DropShadowBorder(Color lineColor, int lineWidth, int shadowSize) {
        this(lineColor, lineWidth, shadowSize, .5f, 12, false, false, true, true);
    }
    
    public DropShadowBorder(Color lineColor, int lineWidth, boolean showLeftShadow) {
        this(lineColor, lineWidth, 5, .5f, 12, false, showLeftShadow, true, true);
    }
    
    public DropShadowBorder(Color lineColor, int lineWidth, int shadowSize,
            float shadowOpacity, int cornerSize, boolean showTopShadow,
            boolean showLeftShadow, boolean showBottomShadow, boolean showRightShadow) {
        this.lineColor = lineColor;
        this.lineWidth = lineWidth;
        this.shadowSize = shadowSize;
        this.shadowOpacity = shadowOpacity;
        this.cornerSize = cornerSize;
        this.showTopShadow = showTopShadow;
        this.showLeftShadow = showLeftShadow;
        this.showBottomShadow = showBottomShadow;
        this.showRightShadow = showRightShadow;
    }
    
    /**
     * @inheritDoc
     */
    public void paintBorder(Component c, Graphics graphics, int x, int y, int width, int height) {
        /*
         * 1) Get images for this border
         * 2) Paint the images for each side of the border that should be painted
         */
       	Map<Position,BufferedImage> images = getImages(null);
        
        //compute the edges of the component -- not including the border
        Insets borderInsets = getBorderInsets(c);
        int leftEdge = x + borderInsets.left - lineWidth;
        int rightEdge = x + width - borderInsets.right;
        int topEdge = y + borderInsets.top - lineWidth;
        int bottomEdge = y + height - borderInsets.bottom;
//        
//        //draw a rectangular border.
        Graphics2D g2 = (Graphics2D)graphics;
        g2.setColor(lineColor);
//        if (lineWidth > 0) {
//            g2.drawRect(leftEdge, topEdge, rightEdge - leftEdge, bottomEdge - topEdge);
//        }
        
        //The location and size of the shadows depends on which shadows are being
        //drawn. For instance, if the left & bottom shadows are being drawn, then
        //the left shadow extends all the way down to the corner, a corner is drawn,
        //and then the bottom shadow begins at the corner. If, however, only the
        //bottom shadow is drawn, then the bottom-left corner is drawn to the
        //right of the corner, and the bottom shadow is somewhat shorter than before.
        
        Point topLeftShadowPoint = null;
        if (showLeftShadow || showTopShadow) {
            topLeftShadowPoint = new Point();
            if (showLeftShadow && !showTopShadow) {
                topLeftShadowPoint.setLocation(x, y + shadowSize);
            } else if (showLeftShadow && showTopShadow) {
                topLeftShadowPoint.setLocation(x, y);
            } else if (!showLeftShadow && showTopShadow) {
                topLeftShadowPoint.setLocation(x + shadowSize, y);
            }
        }
  
        Point bottomLeftShadowPoint = null;
        if (showLeftShadow || showBottomShadow) {
            bottomLeftShadowPoint = new Point();
            if (showLeftShadow && !showBottomShadow) {
                bottomLeftShadowPoint.setLocation(x, y + height - shadowSize - shadowSize);
            } else if (showLeftShadow && showBottomShadow) {
                bottomLeftShadowPoint.setLocation(x, y + height - shadowSize);
            } else if (!showLeftShadow && showBottomShadow) {
                bottomLeftShadowPoint.setLocation(x + shadowSize, y + height - shadowSize);
            }
        }
        
        Point bottomRightShadowPoint = null;
        if (showRightShadow || showBottomShadow) {
            bottomRightShadowPoint = new Point();
            if (showRightShadow && !showBottomShadow) {
                bottomRightShadowPoint.setLocation(x + width - shadowSize, y + height - shadowSize - shadowSize);
            } else if (showRightShadow && showBottomShadow) {
                bottomRightShadowPoint.setLocation(x + width - shadowSize, y + height - shadowSize);
            } else if (!showRightShadow && showBottomShadow) {
                bottomRightShadowPoint.setLocation(x + width - shadowSize - shadowSize, y + height - shadowSize);
            }
        }
        
        Point topRightShadowPoint = null;
        if (showRightShadow || showTopShadow) {
            topRightShadowPoint = new Point();
            if (showRightShadow && !showTopShadow) {
                topRightShadowPoint.setLocation(x + width - shadowSize, y + shadowSize);
            } else if (showRightShadow && showTopShadow) {
                topRightShadowPoint.setLocation(x + width - shadowSize, y);
            } else if (!showRightShadow && showTopShadow) {
                topRightShadowPoint.setLocation(x + width - shadowSize - shadowSize, y);
            }
        }
        
        if (showLeftShadow) {
            Rectangle leftShadowRect = new Rectangle(x, (int)(topLeftShadowPoint.getY() + shadowSize), shadowSize, (int)(bottomLeftShadowPoint.getY() - topLeftShadowPoint.getY() - shadowSize));
            g2.drawImage(images.get(Position.LEFT).getScaledInstance(leftShadowRect.width, leftShadowRect.height, Image.SCALE_FAST), leftShadowRect.x, leftShadowRect.y, null);
        }

        if (showBottomShadow) {
            Rectangle bottomShadowRect = new Rectangle((int)(bottomLeftShadowPoint.getX() + shadowSize), y + height - shadowSize, (int)(bottomRightShadowPoint.getX() - bottomLeftShadowPoint.getX() - shadowSize), shadowSize);
            g2.drawImage(images.get(Position.BOTTOM).getScaledInstance(bottomShadowRect.width, bottomShadowRect.height, Image.SCALE_FAST), bottomShadowRect.x, bottomShadowRect.y, null);
        }
        
        if (showRightShadow) {
            Rectangle rightShadowRect = new Rectangle(x + width - shadowSize, (int)(topRightShadowPoint.getY() + shadowSize), shadowSize, (int)(bottomRightShadowPoint.getY() - topRightShadowPoint.getY() - shadowSize));
            g2.drawImage(images.get(Position.RIGHT).getScaledInstance(rightShadowRect.width, rightShadowRect.height, Image.SCALE_FAST), rightShadowRect.x, rightShadowRect.y, null);
        }
        
        if (showTopShadow) {
            Rectangle topShadowRect = new Rectangle((int)topLeftShadowPoint.getX() + shadowSize, y, (int)(topRightShadowPoint.getX() - topLeftShadowPoint.getX() - shadowSize), shadowSize);
            g2.drawImage(images.get(Position.TOP).getScaledInstance(topShadowRect.width, topShadowRect.height, Image.SCALE_FAST), topShadowRect.x, topShadowRect.y, null);
        }
        
        if (showLeftShadow || showTopShadow) {
            g2.drawImage(images.get(Position.TOP_LEFT), null, (int)topLeftShadowPoint.getX(), (int)topLeftShadowPoint.getY());
        }
        if (showLeftShadow || showBottomShadow) {
            g2.drawImage(images.get(Position.BOTTOM_LEFT), null, (int)bottomLeftShadowPoint.getX(), (int)bottomLeftShadowPoint.getY());
        }
        if (showRightShadow || showBottomShadow) {
            g2.drawImage(images.get(Position.BOTTOM_RIGHT), null, (int)bottomRightShadowPoint.getX(), (int)bottomRightShadowPoint.getY());
        }
        if (showRightShadow || showTopShadow) {
            g2.drawImage(images.get(Position.TOP_RIGHT), null, (int)topRightShadowPoint.getX(), (int)topRightShadowPoint.getY());
        }
    }
    
    private Map<Position,BufferedImage> getImages(Graphics2D g2) {
        //first, check to see if an image for this size has already been rendered
        //if so, use the cache. Else, draw and save
        Map<Position,BufferedImage> images = CACHE.get(shadowSize);
        if (images == null) {
            images = new HashMap<Position,BufferedImage>();

            /*
             * Do draw a drop shadow, I have to:
             *  1) Create a rounded rectangle
             *  2) Create a BufferedImage to draw the rounded rect in
             *  3) Translate the graphics for the image, so that the rectangle
             *     is centered in the drawn space. The border around the rectangle
             *     needs to be shadowWidth wide, so that there is space for the
             *     shadow to be drawn.
             *  4) Draw the rounded rect as black, with an opacity of 50%
             *  5) Create the BLUR_KERNEL
             *  6) Blur the image
             *  7) copy off the corners, sides, etc into images to be used for
             *     drawing the Border
             */
            int rectWidth = cornerSize + 1;
            RoundRectangle2D rect = new RoundRectangle2D.Double(0, 0, rectWidth, rectWidth, cornerSize, cornerSize);
            int imageWidth = rectWidth + shadowSize * 2;
            BufferedImage image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_ARGB);
            Graphics2D buffer = (Graphics2D)image.getGraphics();
            buffer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            buffer.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            buffer.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            buffer.setColor(new Color(0.0f, 0.0f, 0.0f, shadowOpacity));
            buffer.translate(shadowSize, shadowSize);
            buffer.fill(rect);
            float blurry = 1.0f / (float)(shadowSize * shadowSize);
            float[] blurKernel = new float[shadowSize * shadowSize];
            for (int i=0; i<blurKernel.length; i++) {
                blurKernel[i] = blurry;
            }
            ConvolveOp blur = new ConvolveOp(new Kernel(shadowSize, shadowSize, blurKernel));
            BufferedImage targetImage = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_ARGB);
            ((Graphics2D)targetImage.getGraphics()).drawImage(image, blur, -(shadowSize/2), -(shadowSize/2));

//            g2.drawImage(targetImage, null, 0, 0);
            int x = 1;
            int y = 1;
            int w = shadowSize;
            int h = shadowSize;
            images.put(Position.TOP_LEFT, targetImage.getSubimage(x, y, w, h));
            x = 1;
            y = h;
            w = shadowSize;
            h = 1;
            images.put(Position.LEFT, targetImage.getSubimage(x, y, w, h));
            x = 1;
            y = rectWidth;
            w = shadowSize;
            h = shadowSize;
            images.put(Position.BOTTOM_LEFT, targetImage.getSubimage(x, y, w, h));
            x = cornerSize + 1;
            y = rectWidth;
            w = 1;
            h = shadowSize;
            images.put(Position.BOTTOM, targetImage.getSubimage(x, y, w, h));
            x = rectWidth;
            y = x;
            w = shadowSize;
            h = shadowSize;
            images.put(Position.BOTTOM_RIGHT, targetImage.getSubimage(x, y, w, h));
            x = rectWidth;
            y = cornerSize + 1;
            w = shadowSize;
            h = 1;
            images.put(Position.RIGHT, targetImage.getSubimage(x, y, w, h));
            x = rectWidth;
            y = 1;
            w = shadowSize;
            h = shadowSize;
            images.put(Position.TOP_RIGHT, targetImage.getSubimage(x, y, w, h));
            x = shadowSize;
            y = 1;
            w = 1;
            h = shadowSize;
            images.put(Position.TOP, targetImage.getSubimage(x, y, w, h));
            
//            try {
//                ImageIO.write(images.get(Position.BOTTOM), "PNG", new FileOutputStream("/home/rb156199/Desktop/bottom.png"));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            
            //paint some trial images
//            int xoffset = 100;
//            int yoffset = 100;
//            int width = 200;
//            int height = 200;
//            g2.drawImage(images.get(Position.TOP_LEFT), null, xoffset, yoffset);
//            g2.drawImage(images.get(Position.LEFT).getScaledInstance(shadowSize, height - (shadowSize * 2), Image.SCALE_FAST), xoffset, yoffset + shadowSize, null);
//            g2.drawImage(images.get(Position.BOTTOM_LEFT), null, xoffset, yoffset + height - shadowSize);
//            g2.drawImage(images.get(Position.BOTTOM).getScaledInstance(width - (shadowSize * 2), shadowSize, Image.SCALE_FAST), xoffset + shadowSize, yoffset + height - shadowSize, null);
//            g2.drawImage(images.get(Position.BOTTOM_RIGHT), null, xoffset + width - shadowSize, yoffset + height - shadowSize);
//            g2.drawImage(images.get(Position.RIGHT).getScaledInstance(shadowSize, height - (shadowSize * 2), Image.SCALE_FAST), xoffset + width - shadowSize, yoffset + shadowSize, null);
//            g2.drawImage(images.get(Position.TOP_RIGHT), null, xoffset + width - shadowSize, yoffset);
//            g2.drawImage(images.get(Position.TOP).getScaledInstance(width - (shadowSize * 2), shadowSize, Image.SCALE_FAST), xoffset + shadowSize, yoffset, null);
            
            buffer.dispose();
            image.flush();
        }
        return images;
    }
    
    /**
     * @inheritDoc
     */
    public Insets getBorderInsets(Component c) {
        int top = showTopShadow ? lineWidth + shadowSize : lineWidth;
        int left = showLeftShadow ? lineWidth + shadowSize : lineWidth;
        int bottom = showBottomShadow ? lineWidth + shadowSize : lineWidth;
        int right = showRightShadow ? lineWidth + shadowSize : lineWidth;
        return new Insets(top, left, bottom, right);
    }
    
    /**
     * @inheritDoc
     */
    public boolean isBorderOpaque() {
        return true;
    }
    
    public boolean isShowTopShadow() {
        return showTopShadow;
    }
    
    public boolean isShowLeftShadow() {
        return showLeftShadow;
    }
    
    public boolean isShowRightShadow() {
        return showRightShadow;
    }
    
    public boolean isShowBottomShadow() {
        return showBottomShadow;
    }
    
    public int getLineWidth() {
        return lineWidth;
    }
    
    public Color getLineColor() {
        return lineColor;
    }
    
    public int getShadowSize() {
        return shadowSize;
    }
    
    public float getShadowOpacity() {
        return shadowOpacity;
    }
    
    public int getCornerSize() {
        return cornerSize;
    }
    
//    public static void main(String... args) {
//        JFrame frame = new JFrame();
//        frame.setSize(500, 500);
//        
//        JXPanel contentPane = new JXPanel(new BorderLayout());        
//
//        //add sliders
//        JXPanel sliderPanel = new JXPanel();
//        contentPane.add(sliderPanel, BorderLayout.SOUTH);
//        
//        //add the slider for adjusting the shadow thickness
//        final JSlider slider = new JSlider(1, 10, 4);
//        slider.setSnapToTicks(true);
//        slider.setPaintTicks(true);
//        slider.setPaintLabels(true);
//        sliderPanel.add(slider);
//        
//        //add the slider for adjusting the corner radius
//        final JSlider cornerSlider = new JSlider(1, 24, 12);
//        cornerSlider.setSnapToTicks(true);
//        cornerSlider.setPaintTicks(true);
//        cornerSlider.setPaintLabels(true);
//        sliderPanel.add(cornerSlider);
//        
//        //add the checkboxes for what sides to generate images for
//        
//        //add the display panel
//        final DropShadowBorder border = new DropShadowBorder();
//        final JXPanel display = new JXPanel() {
//            public void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                border.getImages((Graphics2D)g);
//            }
//        };
//        display.setDoubleBuffered(false);
//        slider.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
////                border.setShadowSize(slider.getValue());
//                display.repaint();
//            }
//        });
//        cornerSlider.addChangeListener(new ChangeListener() {
//            public void stateChanged(ChangeEvent e) {
////                border.setCornerSize(cornerSlider.getValue());
//                display.repaint();
//            }
//        });
//        contentPane.add(display, BorderLayout.CENTER);
//        
//        frame.setContentPane(contentPane);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
//    }
}