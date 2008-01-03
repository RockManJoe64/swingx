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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.plaf.LabelUI;

import org.jdesktop.swingx.painter.BusyPainter;
import org.jdesktop.swingx.painter.PainterIcon;
import org.jdesktop.swingx.plaf.BusyLabelAddon;
import org.jdesktop.swingx.plaf.BusyLabelUI;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;

/**
 * <p>A simple circular animation, useful for denoting an action is taking
 * place that may take an unknown length of time to complete. Similar to an
 * indeterminant JProgressBar, but with a different look.</p>
 *
 * <p>For example:
 * <pre><code>
 *     JXFrame frame = new JXFrame("test", true);
 *     JXBusyLabel label = new JXBusyLabel();
 *     frame.add(label);
 *     //...
 *     label.setBusy(true);
 * </code></pre></p>
 * Another more complicated example:
 * <pre><code>
 * JXBusyLabel label = new JXBusyLabel(new Dimension(100,84));
 * BusyPainter painter = new BusyPainter(
 * new Rectangle2D.Float(0, 0,13.500001f,1),
 * new RoundRectangle2D.Float(12.5f,12.5f,59.0f,59.0f,10,10));
 * painter.setTrailLength(5);
 * painter.setPoints(31);
 * painter.setFrame(1);
 * label.setPreferredSize(new Dimension(100,84));
 * label.setIcon(new EmptyIcon(100,84));
 * label.setBusyPainter(painter);

 *</code></pre>
 *
 * @author rbair
 * @author joshy
 * @author rah003
 */
public class JXBusyLabel extends JLabel {

    private static final long serialVersionUID = 5979268460848257147L;
    private BusyPainter busyPainter;
    private Timer busy;
    private int delay;
    /** Status flag to save/restore status of timer when moving component between containers. */
    private boolean wasBusyOnNotify = false;
    
    /**
     * UI Class ID
     */
    public final static String uiClassID = "BusyLabelUI";

    /**
     * Direction is used to set the initial direction in which the
     * animation starts.
     * 
     * @see #setStartDirection
     */
    public static enum Direction {
        /**
         * cycle proceeds forward
         */
    RIGHT,
        /** cycle proceeds backward */
    LEFT,
    };

    /**
     * Sets direction of rotation. <code>Direction.RIGHT</code> is the default 
     * value. Direction is taken from the very top point so <code>Direction.RIGHT</code> enables rotation clockwise.
     * @param dir Direction of rotation.
     */
    public void setDirection(Direction dir) {
        direction = dir;
        busyPainter.setDirection(dir);
    }
    
    private Direction direction;

    /**
     * Creates a default JXLoginPane instance
     */
    static {
        LookAndFeelAddons.contribute(new BusyLabelAddon());
    }

    /** Creates a new instance of <code>JXBusyLabel</code> initialized to circular shape in bounds of 26 by 26 points.*/
    public JXBusyLabel() {
        this(null);
    }
    
    /**
     * Creates a new instance of <code>JXBusyLabel</code> initialized to the arbitrary size and using default circular progress indicator.
     * @param dim Preferred size of the label.
     */
    public JXBusyLabel(Dimension dim) {
        super();
        createBusyPainter(dim);
    }

    protected void initPainter(Dimension dim) {
        PainterIcon icon = new PainterIcon(dim);
        icon.setPainter(busyPainter);
        this.setPreferredSize(dim);
        this.setIcon(icon);
    }
    /**
     * Create and return an painter to use for the Label. This may be overridden
     * to return any painter you like.
     * @param dim Painter size.
     */
    protected void createBusyPainter(Dimension dim) {
        BusyLabelUI ui = getUI() == null ? null : (BusyLabelUI)getUI();
        if (ui != null) {
            busyPainter = ui.getBusyPainter();
            delay = ui.getDelay();
        }
        if (busyPainter != null) {
            if (dim == null) {
                Rectangle rt = busyPainter.getTrajectory().getBounds();
                Rectangle rp = busyPainter.getPointShape().getBounds();
                int max = Math.max(rp.width, rp.height);
                dim = new Dimension(rt.width + max, rt.height + max);
            }
            initPainter(dim);
        }
    }
    
    /**
     * <p>Gets whether this <code>JXBusyLabel</code> is busy. If busy, then
     * the <code>JXBusyLabel</code> instance will indicate that it is busy,
     * generally by animating some state.</p>
     * 
     * @return true if this instance is busy
     */
    public boolean isBusy() {
        return busy != null;
    }

    /**
     * <p>Sets whether this <code>JXBusyLabel</code> instance should consider
     * itself busy. A busy component may indicate that it is busy via animation,
     * or some other means.</p>
     *
     * @param busy whether this <code>JXBusyLabel</code> instance should
     *        consider itself busy
     */
    public void setBusy(boolean busy) {
        boolean old = isBusy();
        if (!old && busy) {
            startAnimation();
            firePropertyChange("busy", old, isBusy());
        } else if (old && !busy) {
            stopAnimation();
            firePropertyChange("busy", old, isBusy());
        }
    }
    
    private void startAnimation() {
        if(busy != null) {
            stopAnimation();
        }
        
        busy = new Timer(delay, new ActionListener() {
            int frame = busyPainter.getPoints();
            public void actionPerformed(ActionEvent e) {
                frame = (frame+1)%busyPainter.getPoints();
                busyPainter.setFrame(direction == Direction.LEFT ? busyPainter.getPoints() - frame : frame);
                frameChanged();
            }
        });
        busy.start();
    }
    
    
    
    
    private void stopAnimation() {
        if (busy != null) {
            busy.stop();
            busyPainter.setFrame(-1);
            repaint();
            busy = null;
        }
    }
    
    @Override
    public void removeNotify() {
        // fix for #698
        wasBusyOnNotify = isBusy();
    	// fix for #626
    	stopAnimation();
    	super.removeNotify();
    }
    
    @Override
    public void addNotify() {
    	super.addNotify();
        // fix for #698
        if (wasBusyOnNotify) {
            // fix for #626
            startAnimation();
        }
    }

    protected void frameChanged() {
        repaint();
    }

    /**
     * @return the busyPainter
     */
    public final BusyPainter getBusyPainter() {
        return busyPainter;
    }

    /**
     * @param busyPainter the busyPainter to set
     */
    public final void setBusyPainter(BusyPainter busyPainter) {
        this.busyPainter = busyPainter;
        initPainter(new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));
    }

    /**
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @param delay the delay to set
     */
    public void setDelay(int delay) {
        int old = getDelay();
        this.delay = delay;
        if (old != getDelay()) {
            if (busy != null && busy.isRunning()) {
                busy.setDelay(getDelay());
            }
            firePropertyChange("delay", old, getDelay());
        }
    }
    //------------------------------------------------------------- UI Logic
    
    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see javax.swing.JComponent#updateUI
     */
    public void updateUI() {
        setUI((LabelUI) LookAndFeelAddons.getUI(this, BusyLabelUI.class));
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string {@link #uiClassID}
     * @see javax.swing.JComponent#getUIClassID
     * @see javax.swing.UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


}
