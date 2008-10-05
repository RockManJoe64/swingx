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

package org.jdesktop.swingx.plaf.basic;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.HierarchyBoundsAdapter;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import org.jdesktop.swingx.JXHeader;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXHeader.IconPosition;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.plaf.HeaderUI;
import org.jdesktop.swingx.plaf.PainterUIResource;

/**
 * Base implementation of <code>Header</code> UI. <p>
 * 
 * PENDING JW: This implementation is unusual in that it does not keep a reference
 * to the component it controls. Typically, such is only the case if the ui is
 * shared between instances. Historical? A consequence is that the un/install methods 
 * need to carry the header as parameter. Which looks funny when at the same time 
 * the children of the header are instance fields in this. Should think about cleanup:
 * either get rid off the instance fields here, or reference the header and remove
 * the param (would break subclasses).
 * 
 * @author rbair
 * @author rah003
 * @author Jeanette Winzenburg
 */
public class BasicHeaderUI extends HeaderUI {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(BasicHeaderUI.class
            .getName());
	// Implementation detail. Neeeded to expose getMultiLineSupport() method to allow restoring view
	// lost after LAF switch
    protected class DescriptionPane extends JXLabel {
            @Override
            public void paint(Graphics g) {
//                LOG.info(getText() + ": all hints " + ((Graphics2D)g).getRenderingHints()
//                        + "\n     " + ": aliased " + ((Graphics2D)g).getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));
                // switch off jxlabel default antialiasing
                // JW: that cost me dearly to track down - it's the default foreground painter
                // which is an AbstractPainter which has _global_ antialiased on by default
                // and here the _text_ antialiased is turned off
                ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
                super.paint(g);
            }

            @Override
            public MultiLineSupport getMultiLineSupport() {
            	return super.getMultiLineSupport();
            }
	}

    protected JLabel titleLabel;
    protected DescriptionPane descriptionPane;
    protected JLabel imagePanel;
    private PropertyChangeListener propListener;
    private HierarchyBoundsListener boundsListener;
    private Color gradientLightColor;
    private Color gradientDarkColor;

    /** Creates a new instance of BasicHeaderUI */
    public BasicHeaderUI() {
    }

    /**
     * Returns an instance of the UI delegate for the specified component.
     * Each subclass must provide its own static <code>createUI</code>
     * method that returns an instance of that UI delegate subclass.
     * If the UI delegate subclass is stateless, it may return an instance
     * that is shared by multiple components.  If the UI delegate is
     * stateful, then it should return a new instance per component.
     * The default implementation of this method throws an error, as it
     * should never be invoked.
     */
    public static ComponentUI createUI(JComponent c) {
        return new BasicHeaderUI();
    }

    /**
     * Configures the specified component appropriate for the look and feel.
     * This method is invoked when the <code>ComponentUI</code> instance is being installed
     * as the UI delegate on the specified component.  This method should
     * completely configure the component for the look and feel,
     * including the following:
     * <ol>
     * <li>Install any default property values for color, fonts, borders,
     *     icons, opacity, etc. on the component.  Whenever possible,
     *     property values initialized by the client program should <i>not</i>
     *     be overridden.
     * <li>Install a <code>LayoutManager</code> on the component if necessary.
     * <li>Create/add any required sub-components to the component.
     * <li>Create/install event listeners on the component.
     * <li>Create/install a <code>PropertyChangeListener</code> on the component in order
     *     to detect and respond to component property changes appropriately.
     * <li>Install keyboard UI (mnemonics, traversal, etc.) on the component.
     * <li>Initialize any appropriate instance data.
     * </ol>
     * @param c the component where this UI delegate is being installed
     *
     * @see #uninstallUI
     * @see javax.swing.JComponent#setUI
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        assert c instanceof JXHeader;
        JXHeader header = (JXHeader)c;

        installDefaults(header);
        installComponents(header);
        installListeners(header);
    }

    /**
     * Reverses configuration which was done on the specified component during
     * <code>installUI</code>.  This method is invoked when this
     * <code>UIComponent</code> instance is being removed as the UI delegate
     * for the specified component.  This method should undo the
     * configuration performed in <code>installUI</code>, being careful to
     * leave the <code>JComponent</code> instance in a clean state (no
     * extraneous listeners, look-and-feel-specific property objects, etc.).
     * This should include the following:
     * <ol>
     * <li>Remove any UI-set borders from the component.
     * <li>Remove any UI-set layout managers on the component.
     * <li>Remove any UI-added sub-components from the component.
     * <li>Remove any UI-added event/property listeners from the component.
     * <li>Remove any UI-installed keyboard UI from the component.
     * <li>Nullify any allocated instance data objects to allow for GC.
     * </ol>
     * @param c the component from which this UI delegate is being removed;
     *          this argument is often ignored,
     *          but might be used if the UI object is stateless
     *          and shared by multiple components
     *
     * @see #installUI
     * @see javax.swing.JComponent#updateUI
     */
    @Override
    public void uninstallUI(JComponent c) {
        assert c instanceof JXHeader;
        JXHeader header = (JXHeader)c;

        uninstallListeners(header);
        uninstallComponents(header);
        uninstallDefaults(header);

    }

    /**
     * Installs default header properties. <p>
     * 
     * NOTE: this method is called before the children are created, so
     * must not try to access any of those!.
     * 
     * @param the header to install.
     */
    protected void installDefaults(JXHeader h) {
        gradientLightColor = UIManager.getColor("JXHeader.startBackground");
        if (gradientLightColor == null) {
        	// fallback to white
        	gradientLightColor = Color.WHITE;
        }
        gradientDarkColor = UIManager.getColor("JXHeader.background");
        //for backwards compatibility (mostly for substance and synthetica,
        //I suspect) I'll fall back on the "control" color if JXHeader.background
        //isn't specified.
        if (gradientDarkColor == null) {
            gradientDarkColor = UIManager.getColor("control");
        }

        Painter p = h.getBackgroundPainter();
        if (p == null || p instanceof PainterUIResource) {
            h.setBackgroundPainter(createBackgroundPainter());
        }

        // title properties
        Font titleFont = h.getTitleFont();
        if (titleFont == null || titleFont instanceof FontUIResource) {
        	titleFont = UIManager.getFont("JXHeader.titleFont");
        	// fallback to label font
        	h.setTitleFont(titleFont != null ? titleFont : UIManager.getFont("Label.font"));
        	
        }

        Color titleForeground = h.getTitleForeground();
        if (titleForeground == null || titleForeground instanceof ColorUIResource) {
        	titleForeground = UIManager.getColor("JXHeader.titleForeground");
        	// fallback to label foreground
        	h.setTitleForeground(titleForeground != null ? titleForeground : UIManager.getColor("Label.foreground"));
        }


        // description properties
        Font descFont = h.getDescriptionFont();
        if (descFont == null || descFont instanceof FontUIResource) {
        	descFont = UIManager.getFont("JXHeader.descriptionFont");
        	// fallback to label font
        	h.setDescriptionFont(descFont != null ? descFont : UIManager.getFont("Label.font"));
        }

        Color descForeground = h.getDescriptionForeground();
        if (descForeground == null || descForeground instanceof ColorUIResource) {
        	descForeground = UIManager.getColor("JXHeader.descriptionForeground");
        	// fallback to label foreground
        	h.setDescriptionForeground(descForeground != null ? descForeground : UIManager.getColor("Label.foreground"));
        }
        
        Icon icon = h.getIcon();
        if ((icon == null) || icon instanceof UIResource) {
            icon = UIManager.getIcon("Header.defaultIcon");
            h.setIcon(icon);
        }
    }
    
    /**
     * Uninstalls the given header's default properties. This implementation
     * does nothing.
     * 
     * 
     * @param h the header to ininstall the properties from.
     */
    protected void uninstallDefaults(JXHeader h) {
    }

    /**
     * Creates, configures, adds contained components.
     * PRE: header's default properties must be set before calling this.
     * 
     * @param the header to install the components into.
     */
    protected void installComponents(JXHeader h) {
        titleLabel = new JLabel();
        descriptionPane = new DescriptionPane();
        imagePanel = new JLabel();
        installComponentDefaults(h);
        h.setLayout(new GridBagLayout());
        resetLayout(h);
    }

    /**
     * Unconfigures, removes and nulls contained components.
     * 
     * @param the header to install the components into.
     */
    protected void uninstallComponents(JXHeader h) {
        uninstallComponentDefaults(h);
        h.remove(titleLabel);
        h.remove(descriptionPane);
        h.remove(imagePanel);
        titleLabel = null;
        descriptionPane = null;
        imagePanel = null;
    }

    /**
     * Configures the component default properties from the given header.
     * 
     * 
     * @param the header to install the components into.
     */
    protected void installComponentDefaults(JXHeader h) {
        //JW: force a not UIResource
        // PENDING JW: correct way to create another font instance?
        titleLabel.setFont(h.getTitleFont().deriveFont(h.getTitleFont().getStyle()));
        // PENDING JW: correct way to create another color instance?
        float[] rgb = h.getTitleForeground().getRGBComponents(null);
        titleLabel.setForeground(new Color(rgb[0], rgb[1], rgb[2], rgb[3]));
        titleLabel.setText(h.getTitle());
        
        descriptionPane.setFont(h.getDescriptionFont().deriveFont(h.getDescriptionFont().getStyle()));
        rgb = h.getDescriptionForeground().getRGBComponents(null);
        descriptionPane.setForeground(new Color(rgb[0], rgb[1], rgb[2], rgb[3]));
        descriptionPane.setOpaque(false);
        descriptionPane.setText(h.getDescription());
        descriptionPane.setLineWrap(true);

        imagePanel.setIcon(h.getIcon());

    }
    
    /**
     * Uninstalls component defaults. This implementation does nothing.
     * 
     * @param the header to uninstall from.
     */
    protected void uninstallComponentDefaults(JXHeader h) {
        // TODO Auto-generated method stub
        
    }


    protected void installListeners(final JXHeader header) {
        propListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                onPropertyChange(header, evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        };
        boundsListener = new HierarchyBoundsAdapter() {
            @Override
            public void ancestorResized(HierarchyEvent e) {
                if (header == e.getComponent()) {
                    View v = (View) descriptionPane.getClientProperty(BasicHTML.propertyKey);
                    // view might get lost on LAF change ...
                    if (v == null) {
                    	descriptionPane.putClientProperty(BasicHTML.propertyKey, 
                    	        descriptionPane.getMultiLineSupport().createView(descriptionPane));
                    	v = (View) descriptionPane.getClientProperty(BasicHTML.propertyKey);
                    }
                    if (v != null) {
                        Container tla = header.getTopLevelAncestor();
                        if (tla == null) {
                            tla = header.getParent();
                            while (tla.getParent() != null) {
                                tla = tla.getParent();
                            }
                        }
                        int h = Math.max(descriptionPane.getHeight(), tla.getHeight());
                        int w = Math.min(tla.getWidth(), header.getParent().getWidth());
                        // 35 = description pane insets, TODO: obtain dynamically
                        w -= 35 + header.getInsets().left + header.getInsets().right + descriptionPane.getInsets().left + descriptionPane.getInsets().right + imagePanel.getInsets().left + imagePanel.getInsets().right + imagePanel.getWidth() + descriptionPane.getBounds().x;
                    	v.setSize(w, h);
                    	descriptionPane.setSize(w, (int) Math.ceil(v.getPreferredSpan(View.Y_AXIS)));
                    }
                }
            }};
        header.addPropertyChangeListener(propListener);
        header.addHierarchyBoundsListener(boundsListener);
    }

    protected void uninstallListeners(JXHeader h) {
        h.removePropertyChangeListener(propListener);
        h.removeHierarchyBoundsListener(boundsListener);
    }

    protected void onPropertyChange(JXHeader h, String propertyName, Object oldValue, final Object newValue) {
        if ("title".equals(propertyName)) {
            titleLabel.setText(h.getTitle());
        } else if ("description".equals(propertyName)) {
            descriptionPane.setText(h.getDescription());
        } else if ("icon".equals(propertyName)) {
            imagePanel.setIcon(h.getIcon());
        } else if ("enabled".equals(propertyName)) {
            boolean enabled = h.isEnabled();
            titleLabel.setEnabled(enabled);
            descriptionPane.setEnabled(enabled);
            imagePanel.setEnabled(enabled);
        } else if ("titleFont".equals(propertyName)) {
            titleLabel.setFont((Font)newValue);
        } else if ("descriptionFont".equals(propertyName)) {
            descriptionPane.setFont((Font)newValue);
        } else if ("titleForeground".equals(propertyName)) {
            titleLabel.setForeground((Color)newValue);
        } else if ("descriptionForeground".equals(propertyName)) {
            descriptionPane.setForeground((Color)newValue);
        } else if ("iconPosition".equals(propertyName)) {
            resetLayout(h);
        }
    }

    private void resetLayout(JXHeader h) {
    	h.remove(titleLabel);
    	h.remove(descriptionPane);
    	h.remove(imagePanel);
    	if (h.getIconPosition() == null || h.getIconPosition() == IconPosition.RIGHT) {
	        h.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
	        h.add(descriptionPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 24, 12, 11), 0, 0));
	        h.add(imagePanel, new GridBagConstraints(1, 0, 1, 2, 0.0, 1.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(12, 0, 11, 11), 0, 0));
    	} else {
	        h.add(titleLabel, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL, new Insets(12, 12, 0, 11), 0, 0));
	        h.add(descriptionPane, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 24, 12, 11), 0, 0));
	        h.add(imagePanel, new GridBagConstraints(0, 0, 1, 2, 0.0, 1.0, GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(12, 11, 0, 11), 0, 0));
    	}
	}
    
    

    protected Painter createBackgroundPainter() {
        MattePainter p = new MattePainter(new GradientPaint(0, 0, gradientLightColor, 1, 0, gradientDarkColor));
        p.setPaintStretched(true);
        return new PainterUIResource(p);
    }
}
