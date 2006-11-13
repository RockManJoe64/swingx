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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.jdesktop.swingx.JavaBean;
import org.jdesktop.swingx.painter.effects.Effect;
import org.jdesktop.swingx.painter.effects.ImageEffect;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * <p>A convenient base class from which concrete Painter implementations may
 * extend. It extends JavaBean and thus provides property change notification
 * (which is crucial for the Painter implementations to be available in a
 * GUI builder). It also saves off the Graphics2D state in its "saveState" method,
 * and restores that state in the "restoreState" method. Sublasses simply need
 * to extend AbstractPainter and implement the paintBackground method.
 *
 * <p>For example, here is the paintBackground method of BackgroundPainter:
 * <pre><code>
 *  public void paintBackground(Graphics2D g, JComponent component) {
 *      g.setColor(component.getBackground());
 *      g.fillRect(0, 0, component.getWidth(), component.getHeight());
 *  }
 * </code></pre>
 *
 * <p>AbstractPainter provides a very useful default implementation of
 * the paint method. It:
 * <ol>
 *  <li>Saves off the old state</li>
 *  <li>Sets any specified rendering hints</li>
 *  <li>Sets the Clip if there is one</li>
 *  <li>Sets the Composite if there is one</li>
 *  <li>Delegates to paintBackground</li>
 *  <li>Restores the original Graphics2D state</li>
 * <ol></p>
 *
 * <p>Specifying rendering hints can greatly improve the visual impact of your
 * applications. For example, by default Swing doesn't do much in the way of
 * antialiasing (except for Fonts, but that's another story). Pinstripes don't
 * look so good without antialiasing. So if I were going to paint pinstripes, I
 * might do it like this:
 * <pre><code>
 *   PinstripePainter p = new PinstripePainter();
 *   p.setAntialiasing(RenderingHints.VALUE_ANTIALIAS_ON);
 * </code></pre></p>
 *
 * <p>You can read more about antialiasing and other rendering hints in the
 * java.awt.RenderingHints documentation. <strong>By nature, changing the rendering
 * hints may have an impact on performance. Certain hints require more
 * computation, others require less</strong></p>
 *
 * @author rbair
 */
public abstract class AbstractPainter<T extends JComponent> extends JavaBean implements Painter<T> {
    //------------------------------------------------- Saved Graphics State
    private boolean stateSaved = false;
    private Stroke oldStroke;
    private AffineTransform oldTransform;
    private Composite oldComposite;
    private Shape oldClip;
    private Color oldBackground;
    private Color oldColor;
    
    
    
    /**
     * Different available fill styles. BOTH indicates that both the outline,
     * and the fill should be painted. This is the default. FILLED indicates that
     * the shape should be filled, but no outline painted. OUTLINE specifies that
     * the shape should be outlined, but not filled
     */
    public enum Style {BOTH, FILLED, OUTLINE, NONE}
    
    /* enum for dealing with all rendering hints */
    //RenderingHints.VALUE_ANTIALIAS_ON;
    //RenderingHints.KEY_ANTIALIASING;
    //RenderingHints.V
    
    //--------------------------------------------------- Instance Variables
    /**
     * A Shape that is used to clip the graphics area. Anything within this
     * clip shape is included in the final output.
     */
    private Shape clip;
    /**
     * The composite to use. By default this is a reasonable AlphaComposite,
     * but you may want to specify a different composite
     */
    private Composite composite;
    /**
     * RenderingHints to apply when painting
     */
    private RenderingHints renderingHints;
    /**
     * A hint as to whether or not to attempt caching the image
     */
    private boolean useCache = false;
    /**
     * The cached image, if useCache is true
     */
    private transient SoftReference<BufferedImage> cachedImage;
    /**
     * The Effects to apply to the results of the paint() operation
     */
    private Effect[] effects = new Effect[0];
    
    /**
     * Creates a new instance of AbstractPainter
     */
    public AbstractPainter() {
        //renderingHints = new RenderingHints(null);
        //renderingHints = new HashMap<RenderingHints.Key,Object>();
    }
    
    /**
     * <p>Sets whether to cache the painted image with a SoftReference in a BufferedImage
     * between calls. If true, and if the size of the component hasn't changed,
     * then the cached image will be used rather than causing a painting operation.</p>
     *
     * <p>This should be considered a hint, rather than absolute. Several factors may
     * force repainting, including low memory, different component sizes, or possibly
     * new rendering hint settings, etc.</p>
     *
     * @param b whether or not to use the cache
     */
    public void setUseCache(boolean b) {
        boolean old = isUseCache();
        useCache = b;
        firePropertyChange("useCache", old, isUseCache());
        //if there was a cached image and I'm no longer using the cache, blow it away
        if (cachedImage != null && !isUseCache()) {
            cachedImage = null;
        }
    }
    
    /**
     * @return whether or not the cache should be used
     */
    public boolean isUseCache() {
        return useCache;
    }
    
    /**
     * <p>Sets the effects to apply to the results of the AbstractPainter's
     * painting operation. Some common effects include blurs, shadows, embossing,
     * and so forth. If the given effects is a null array, no effects will be used</p>
     *
     * @param effects the Effects to apply to the results of the AbstractPainter's
     *                painting operation
     */
    public void setEffects(Effect... effects) {
        Effect[] old = getEffects();
        this.effects = new Effect[effects == null ? 0 : effects.length];
        if (effects != null) {
            System.arraycopy(effects, 0, this.effects, 0, effects.length);
        }
        firePropertyChange("effects", old, getEffects());
        //firePropertyChange("effects", old, getEffects());
    }
    
    /**
     * <p>A convenience method for specifying the effects to use based on
     * BufferedImageOps. These will each be individually wrapped by an ImageEffect
     * and then setEffects(Effect... effects) will be called with the resulting
     * array</p>
     *
     * @param filters the BufferedImageOps to wrap as effects
     */
    public void setEffects(BufferedImageOp... filters) {
        Effect[] effects = new Effect[filters == null ? 0 : filters.length];
        if (filters != null) {
            int index = 0;
            for (BufferedImageOp op : filters) {
                effects[index++] = new ImageEffect(op);
            }
        }
        setEffects(effects);
    }
    
    /**
     * @return effects a defensive copy of the Effects to apply to the results
     *          of the AbstractPainter's painting operation. Will never null
     */
    public Effect[] getEffects() {
        Effect[] results = new Effect[effects.length];
        System.arraycopy(effects, 0, results, 0, results.length);
        return results;
    }
    
    /**
     * Specifies the Shape to use for clipping the painting area. This
     * may be null
     *
     * @param clip the Shape to use to clip the area. Whatever is inside this
     *        shape will be kept, everything else "clipped". May be null. If
     *        null, the clipping is not set on the graphics object
     */
    public void setClip(Shape clip) {
        Shape old = getClip();
        this.clip = clip;
        firePropertyChange("clip", old, getClip());
    }
    
    /**
     * @return the clipping shape
     */
    public Shape getClip() {
        return clip;
    }
    
    
    /**
     * Sets the Composite to use. For example, you may specify a specific
     * AlphaComposite so that when this Painter paints, any content in the
     * drawing area is handled properly
     *
     * @param c The composite to use. If null, then no composite will be
     *        specified on the graphics object
     */
    public void setComposite(Composite c) {
        Composite old = getComposite();
        this.composite = c;
        firePropertyChange("composite", old, getComposite());
    }
    
    /**
     * @return the composite
     */
    public Composite getComposite() {
        return composite;
    }
    
    
    public enum Antialiasing {
        // define the constants we wrap
        On(RenderingHints.VALUE_ANTIALIAS_ON),
        Off(RenderingHints.VALUE_ANTIALIAS_OFF),
        Default(RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        
        private Object value;
        Antialiasing(Object value) {
            this.value = value;
        }
        public void configureGraphics(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, value);
        }
    }
    
    
    private Antialiasing antialiasing = Antialiasing.On;
    public Antialiasing getAntialiasing() {
        return antialiasing;
    }
    public void setAntialiasing(Antialiasing value) {
        Object old = getAntialiasing();
        antialiasing = value;
        firePropertyChange("antialiasing", old, getAntialiasing());
    }
    
    
    public enum FractionalMetrics {
        On(RenderingHints.VALUE_FRACTIONALMETRICS_ON),
        Off(RenderingHints.VALUE_FRACTIONALMETRICS_OFF),
        Default(RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
        
        private Object value;
        FractionalMetrics(Object value) {
            this.value = value;
        }
        public void configureGraphics(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, value);
        }
    }
    private FractionalMetrics fractionalMetrics = FractionalMetrics.Default;
    public FractionalMetrics getFractionalMetrics() {
        return fractionalMetrics;
    }
    public void setFractionalMetrics(FractionalMetrics fractionalMetrics) {
        Object old = getFractionalMetrics();
        this.fractionalMetrics = fractionalMetrics;
        firePropertyChange("fractionalMetrics", old, getFractionalMetrics());
    }
    
    
    
    public enum Interpolation { 
        Bicubic(RenderingHints.VALUE_INTERPOLATION_BICUBIC), 
        Bilinear(RenderingHints.VALUE_INTERPOLATION_BILINEAR), 
        NearestNeighbor(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        
        private Object value;
        Interpolation(Object value) {
            this.value = value;
        }
        public void configureGraphics(Graphics2D g) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, value);
        }
    }
    private Interpolation interpolation = Interpolation.NearestNeighbor;
    public Interpolation getInterpolation() {
        return interpolation;
    }
    public void setInterpolation(Interpolation value) {
        Object old = getInterpolation();
        this.interpolation = value;
        firePropertyChange("interpolation", old, getInterpolation());
    }
    
    
    
    private boolean clipPreserved = false;
    
    /**
     * @inheritDoc
     */
    public void paint(Graphics2D g, T component, int width, int height) {
        if(!isEnabled()) {
            return;
        }
        //saveState(g);
        
        Graphics2D oldGraphics = g;
        g = (Graphics2D)g.create();
        
        configureGraphics(g, component);
        
        //if I am cacheing, and the cache is not null, and the image has the
        //same dimensions as the component, then simply paint the image
        BufferedImage image = cachedImage == null ? null : cachedImage.get();
        if (isUseCache() && image != null
                && image.getWidth() == component.getWidth()
                && image.getHeight() == component.getHeight()) {
            g.drawImage(image, 0, 0, null);
        } else {
            Effect[] effects = getEffects();
            if (effects.length > 0 || isUseCache()) {
                image = PaintUtils.createCompatibleImage(
                        component.getWidth(),
                        component.getHeight(),
                        Transparency.TRANSLUCENT);
                
                Graphics2D gfx = image.createGraphics();
                configureGraphics(gfx, component);
                paintBackground(gfx, component, width, height);
                gfx.dispose();
                
                for (Effect effect : effects) {
                    image = effect.apply(image);
                }
                
                g.drawImage(image, 0, 0, null);
                
                if (isUseCache()) {
                    cachedImage = new SoftReference<BufferedImage>(image);
                }
            } else {
                paintBackground(g, component, width, height);
            }
        }
        
        //restoreState(g);
        if(isClipPreserved()) {
            oldGraphics.setClip(g.getClip());
        }
        g.dispose();
    }
    
    /**
     * Utility method for configuring the given Graphics2D with the rendering hints,
     * composite, and clip
     */
    private void configureGraphics(Graphics2D g, T c) {
        if (getComposite() != null) {
            g.setComposite(getComposite());
        }
        Shape clip = getClip();
        if (clip != null) {
            g.setClip(clip);
        }
        g.setFont(c.getFont());
        
        antialiasing.configureGraphics(g);
        /*
        if(antialiasing.equals(Antialiasing.Default)) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
        if(antialiasing.equals(Antialiasing.Off)) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        if(antialiasing.equals(Antialiasing.On)) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }*/
        if(interpolation.equals(Interpolation.Bicubic)) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        if(interpolation.equals(Interpolation.Bilinear)) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        if(interpolation.equals(Interpolation.NearestNeighbor)) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        }
    }
    
    
    /**
     * Subclasses should implement this method and perform custom painting operations
     * here. Common behavior, such as setting the clip and composite, saving and restoring
     * state, is performed in the "paint" method automatically, and then delegated here.
     *
     * @param g The Graphics2D object in which to paint
     * @param component The JComponent that the Painter is delegate for.
     */
    protected abstract void paintBackground(Graphics2D g, T component, int width, int height);
    
    public boolean isClipPreserved() {
        return clipPreserved;
    }
    
    public void setClipPreserved(boolean shouldRestoreState) {
        boolean oldShouldRestoreState = isClipPreserved();
        this.clipPreserved = shouldRestoreState;
        firePropertyChange("shouldRestoreState",oldShouldRestoreState,shouldRestoreState);
    }
    
    /**
     * Holds value of property enabled.
     */
    private boolean enabled = true;
    
    /**
     * Getter for property enabled.
     * @return Value of property enabled.
     */
    public boolean isEnabled() {
        return this.enabled;
    }
    
    /**
     * Setter for property enabled.
     * @param enabled New value of property enabled.
     */
    public void setEnabled(boolean enabled) {
        boolean oldEnabled = this.isEnabled();
        this.enabled = enabled;
        firePropertyChange("enabled", new Boolean(oldEnabled), new Boolean(enabled));
    }
}
