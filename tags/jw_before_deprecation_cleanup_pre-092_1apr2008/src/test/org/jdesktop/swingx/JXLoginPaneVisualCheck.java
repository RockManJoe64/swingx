/*
 * Copyright 2006 Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * California 95054, U.S.A. All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package org.jdesktop.swingx;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jdesktop.swingx.JXLoginPane.JXLoginFrame;
import org.jdesktop.swingx.JXLoginPane.SaveMode;
import org.jdesktop.swingx.auth.LoginService;
import org.jdesktop.swingx.graphics.GraphicsUtilities;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.basic.BasicLoginPaneUI;
import org.jdesktop.swingx.util.PaintUtils;

/**
 * Simple tests to ensure that the {@code JXLoginPane} can be instantiated and
 * displayed.
 * 
 * @author Karl Schaefer
 */
public class JXLoginPaneVisualCheck extends InteractiveTestCase {
    public JXLoginPaneVisualCheck() {
        super("JXLoginPane Test");
    }

    public static void main(String[] args) throws Exception {
        // setSystemLF(true);
        JXLoginPaneVisualCheck test = new JXLoginPaneVisualCheck();
        
        try {
            test.runInteractiveTests();
        } catch (Exception e) {
            System.err.println("exception when executing interactive tests:");
            e.printStackTrace();
        }
    }
    
    private static class SetPlafAction extends AbstractAction {
        private String plaf;
        
        public SetPlafAction(String name, String plaf) {
            super(name);
            this.plaf = plaf;
        }
        
        /**
         * {@inheritDoc}
         */
        public void actionPerformed(ActionEvent e) {
            try {
                Component c = (Component) e.getSource();
                Window w = null;
                
                for (Container p = c.getParent(); p != null; p = p instanceof JPopupMenu ? (Container) ((JPopupMenu) p)
                        .getInvoker() : p.getParent()) {
                    if (p instanceof Window) {
                        w = (Window) p;
                    }
                }
                
                UIManager.setLookAndFeel(plaf);
                SwingUtilities.updateComponentTreeUI(w);
                w.pack();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
            } catch (UnsupportedLookAndFeelException e1) {
                e1.printStackTrace();
            }
        }
    }
    
    private JMenuBar createMenuBar(final JComponent component) {
        LookAndFeelInfo[] plafs = UIManager.getInstalledLookAndFeels();
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Set L&F");
        
        for (LookAndFeelInfo info : plafs) {
            menu.add(new SetPlafAction(info.getName(), info.getClassName()));
        }
        menu.add(new AbstractAction("Change Locale") {

            public void actionPerformed(ActionEvent e) {
                if (component.getLocale() == Locale.FRANCE) {
                    component.setLocale(Locale.ENGLISH);
                } else {
                    component.setLocale(Locale.FRANCE);
                }
            }});
        bar.add(menu);
        
        return bar;
    }
    
    public JXFrame wrapInFrame(JComponent component, String title) {
        JXFrame frame = super.wrapInFrame(component, title);
        frame.setJMenuBar(createMenuBar(component));
        
        return frame;
    }
    
    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveDisplay() {
        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        JXLoginPane panel = new JXLoginPane();
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);
        
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #538-swingx Failure to set locale at runtime
     *
     */
    public void interactiveSetBackground() {
        JXLoginPane panel = new JXLoginPane();
        panel.setBackgroundPainter(new MattePainter(Color.RED, true));
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);
        
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #777-swingx Custom banner not picked up due to double updateUI() call
     *
     */
    public void interactiveCustomBannerDisplay() {
        JXLoginPane panel = new JXLoginPane();
        panel.setUI(new DummyLoginPaneUI(panel));
        JFrame frame = JXLoginPane.showLoginFrame(panel);
        frame.setJMenuBar(createMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);
        
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Issue #636-swingx Unexpected resize on long exception message.
     *
     */
    public void interactiveError() {
        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        final JXLoginPane panel = new JXLoginPane(new LoginService() {

                        public boolean authenticate(String name, char[] password,
                                        String server) throws Exception {
                                if (true) {
                                        throw new Exception("Ex.");
                                }
                                return false;
                        }});
        final JXLoginFrame frame = JXLoginPane.showLoginFrame(panel);
        // if uncomented dialog will disappear immediatelly dou to invocation of login action
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar(panel));
        panel.setErrorMessage("TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO Unexpected resize on long exception message. Unexpected resize on long exception message.");

        panel.setSaveMode(SaveMode.BOTH);
        
        frame.pack();
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                                evaluateChildren(frame.getContentPane().getComponents());
                        }});
        
    }
    
    /**
     * Issue #636-swingx Unexpected resize on long exception message.
     *
     */
    public void interactiveBackground() {
        sun.awt.AppContext.getAppContext().put("JComponent.defaultLocale", Locale.FRANCE);
        final JXLoginPane panel = new JXLoginPane(new LoginService() {

                        public boolean authenticate(String name, char[] password,
                                        String server) throws Exception {
                                if (true) {
                                        throw new Exception("Ex.");
                                }
                                return false;
                        }});
        final JXLoginFrame frame = JXLoginPane.showLoginFrame(panel);
        // if uncomented dialog will disappear immediatelly dou to invocation of login action
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setJMenuBar(createMenuBar(panel));
        panel.setErrorMessage("TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO TO Unexpected resize on long exception message. Unexpected resize on long exception message.");

        panel.setSaveMode(SaveMode.BOTH);
        frame.getContentPane().setBackgroundPainter(new MattePainter(new GradientPaint(0,0,Color.BLUE, 1,0,Color.YELLOW), true));
        
        frame.pack();
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                                evaluateChildren(frame.getContentPane().getComponents());
                        }});
        
    }
    
    /**
     * Progress message test.
     */
    public void interactiveProgress() {
        final JXLoginPane panel = new JXLoginPane();
        final JFrame frame = JXLoginPane.showLoginFrame(panel);
        panel.setLoginService(new LoginService() {

			public boolean authenticate(String name, char[] password,
					String server) throws Exception {
				panel.startLogin();
				Thread.sleep(5000);
				return true;
			}});
        
        frame.setJMenuBar(createMenuBar(panel));

        panel.setSaveMode(SaveMode.BOTH);
        
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				evaluateChildren(frame.getContentPane().getComponents());
			}});
        
    }

    private boolean evaluateChildren(Component[] components) {
        for (Component c: components) {
        	if (c instanceof JButton && "login".equals(((JButton) c).getActionCommand())) {
        		((JButton) c).doClick();
        		
        		return true;
        	} else if (c instanceof Container) {
        		if (evaluateChildren(((Container) c).getComponents()) ){
        			return true;
        		}
        	}
        }
        return false;

	}

	/**
     * Do nothing, make the test runner happy
     * (would output a warning without a test fixture).
     *
     */
    public void testDummy() {
        
    }
    
    public class DummyLoginPaneUI extends BasicLoginPaneUI {

        public DummyLoginPaneUI(JXLoginPane dlg) {
            super(dlg);
            // TODO Auto-generated constructor stub
        }
        
        @Override
        public Image getBanner() {
            Image banner = super.getBanner();
            BufferedImage im = GraphicsUtilities.createCompatibleTranslucentImage(banner.getWidth(null), banner.getHeight(null));
            Graphics2D g = im.createGraphics();
            g.setComposite(AlphaComposite.Src);
            g.drawImage(banner, 0, 0, 100, 100, null);
            g.dispose();
            return im;
        }
    }

}