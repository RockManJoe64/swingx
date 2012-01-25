/*
 * PainterEditorPanel.java
 *
 * Created on July 12, 2006, 2:44 PM
 */

package org.jdesktop.swingx.painterset;

import com.jhlabs.image.GaussianFilter;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXPropertySheet2;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import com.jhlabs.image.ShadowFilter;
import org.jdesktop.swingx.painterset.TestPlaf;
import org.jdesktop.swingx.util.BeanArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.color.ColorUtil;
import org.jdesktop.swingx.editors.PainterUtil;
import org.jdesktop.swingx.painter.AbstractPainter;
import org.jdesktop.swingx.painter.CheckerboardPainter;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.ImagePainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.painter.PinstripePainter;
import org.jdesktop.swingx.painter.RectanglePainter;
import org.jdesktop.swingx.painter.effects.AbstractAreaEffect;
import org.jdesktop.swingx.painter.ShapePainter;
import org.jdesktop.swingx.painter.TextPainter;
import org.jdesktop.swingx.painterset.actions.AddEffectAction;
import org.jdesktop.swingx.painterset.actions.AddPainterAction;
import org.jdesktop.swingx.painterset.actions.DeleteAction;
import org.jdesktop.swingx.painterset.actions.ExportAction;
import org.jdesktop.swingx.painterset.actions.NewPainterSetAction;
import org.jdesktop.swingx.painterset.actions.OpenAction;
import org.jdesktop.swingx.painterset.actions.SaveAction;
import org.jdesktop.swingx.painterset.actions.SaveAsAction;
import org.jdesktop.swingx.painterset.dndtree.DnDTree;

/**
 *
 * @author  jm158417
 */
public class PainterEditorPanel extends javax.swing.JPanel implements PropertyChangeListener {
    public JComponent testPanel;
    public Icon effectsIcon, painterIcon;
    Object selectedPainter;
    BeanArrayList<PainterSet> painterSets = new BeanArrayList<PainterSet>("painterSets",this);
    PainterSet selectedPainterSet = null;
    BeanArrayList<Class<? extends Painter>> painterClasses;
    BeanArrayList<Class> effectClasses;
    BeanArrayList<JComponent> previewComponents = new BeanArrayList<JComponent>("previewComponents",this);
    
    /** Creates new form PainterEditorPanel */
    public PainterEditorPanel() {
        initComponents();
        
        painterTabs.setUI(new TestPlaf() {
            public void tabClosed(int i) {
                closePainterSet(i);
            }
        });
        painterClasses = new BeanArrayList<Class<? extends Painter>>();
        painterClasses.add(PinstripePainter.class);
        painterClasses.add(GlossPainter.class);
        painterClasses.add(CheckerboardPainter.class);
        painterClasses.add(ImagePainter.class);
        painterClasses.add(MattePainter.class);
        painterClasses.add(RectanglePainter.class);
        painterClasses.add(ShapePainter.class);
        painterClasses.add(TextPainter.class);
        //painterClasses.add(AffineTransformPainter.class);
        painterClasses.add(CompoundPainter.class);
        
        effectClasses = new BeanArrayList<Class>();
        effectClasses.add(AbstractAreaEffect.class);
        effectClasses.add(ShadowFilter.class);
        effectClasses.add(GaussianFilter.class);
        
        previewComponents.add(new JXButton("This is a JXButton"));
        previewComponents.add(new JXLabel("This is a JXLabel"));
        previewComponents.add(new JXPanel());
        
        previewCombo.setModel(new BeanArrayComboBoxModel(previewComponents));
        previewCombo.setRenderer(new ClassNameCellRenderer());
        
        newPainterCombo.setModel(new ListComboBoxModel(painterClasses));
        newPainterCombo.setRenderer(new ClassNameCellRenderer());
        
        newEffectCombo.setModel(new ListComboBoxModel(effectClasses));
        newEffectCombo.setRenderer(new ClassNameCellRenderer());
        
        foregroundCombo.setModel(new BeanArrayComboBoxModel(painterSets));
        foregroundCombo.setRenderer(new PainterSetComboRenderer());
        backgroundCombo.setModel(new BeanArrayComboBoxModel(painterSets));
        backgroundCombo.setRenderer(new PainterSetComboRenderer());
        
        effectsIcon = new ImageIcon(this.getClass().getResource("resources/EffectIcon.png"));
        painterIcon = new ImageIcon(this.getClass().getResource("resources/PainterIcon.png"));
        
        
        // init extra stuff
        preview.setLayout(new BorderLayout());
        
        // the test panel
        setPreviewComponent(new JXPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g;
                g2.setPaint(ColorUtil.getCheckerPaint());
                g2.fillRect(0,0,getWidth(),getHeight());
                super.paintComponent(g);
            }
        });
        
        /*
        try {
            ImagePainter.baseURL = new File("../PainterTest/src/samples/").toURL();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }*/
        
        
        //setup the none item
        PainterSet none = setupNewPainter(new CompoundPainter());
        none.name = "- none -";
        foregroundCombo.setSelectedItem(none);
        backgroundCombo.setSelectedItem(none);
        
        // set up the default item
        PainterSet set = setupNewPainter(null);
        set.name = "Untitled";
        painterTabs.addTab(set.name, set.tree);
        foregroundCombo.setSelectedItem(set);
        selectedPainterSet = set;
        setPreviewComponent(previewComponents.get(0));
    }
    
    private void setPreviewComponent(JComponent comp) {
        JComponent old = testPanel;
        testPanel = comp;
        if(old != null) {
            preview.remove(old);
            PainterUtil.setFGP(testPanel,PainterUtil.getFGP(old));
            PainterUtil.setBGP(testPanel,PainterUtil.getBGP(old));
        }
        preview.add(testPanel,"Center");
        preview.validate();
        testPanel.repaint();
    }
    
    public PainterSet setupNewPainter(CompoundPainter painter) {
        if(painter == null) {
            ImagePainter ip = new ImagePainter();
            painter = new CompoundPainter(ip);
        }
        
        PainterSet set = new PainterSet();
        set.tree = new CustomPainterTree();
        set.tree.setCellRenderer(new PainterTreeCellRenderer(this, (DnDTree) set.tree));
        set.tree.setShowsRootHandles(true);
        set.tree.setEditable(false);
        set.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        
        PainterTreeModel model = new PainterTreeModel(painter);
        model.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) { refreshPainters(); }
            public void treeNodesInserted(TreeModelEvent e) { refreshPainters(); }
            public void treeNodesRemoved(TreeModelEvent e) {
                refreshPainters();
            }
            public void treeStructureChanged(TreeModelEvent e) { refreshPainters(); }
        });
        set.model = model;
        set.tree.setModel(model);
        set.tree.addTreeSelectionListener(new PainterTreeSelectionListener());
        set.name = "no name";
        painterSets.add(set);
        return set;
    }
    
    public void closePainterSet(int i) {
        //        u.p("closing painter: " + i);
        painterSets.remove(i);
    }
    public Action getNewDocumentAction() {
        Action act =  new NewPainterSetAction(this);
        act.putValue(Action.NAME,"New Painter Set");
        act.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta N"));
        return act;
    }
    
    public Action getSaveAsAction() {
        Action act = new SaveAsAction(this);
        act.putValue(Action.NAME, "Save As");
        act.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta shift S"));
        return act;
    }
    
    public Action getAddPainterAction() {
        Action act = new AddPainterAction(this);
        act.putValue(Action.NAME, "Add Painter");
        return act;
    }
    
    public Action getAddEffectAction() {
        Action act = new AddEffectAction(this);
        act.putValue(Action.NAME, "Add Effect");
        return act;
    }
    
    public Action getOpenAction() {
        Action act = new OpenAction(this);
        act.putValue(Action.NAME, "Open");
        act.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta O"));
        return act;
    }
    
    public Action getDeleteAction() {
        Action act = new DeleteAction(this);
        act.putValue(Action.NAME, "Delete");
        return act;
    }
    
    public Action getExportAction() {
        Action act = new ExportAction(this);
        act.putValue(Action.NAME, "Export As PNG");
        return act;
    }
    
    public Action getSaveAction() {
        Action act = new SaveAction(this);
        act.putValue(Action.NAME, "Save");
        act.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("meta S"));
        return act;
    }
    
    
    public void propertyChange(PropertyChangeEvent evt) {
        refreshPainters();
    }
    
    public Object getSelectedPainter() {
        return selectedPainter;
    }
    
    public void setSelectedPainter(Object pt) {
        if(selectedPainter instanceof AbstractPainter) {
            ((AbstractPainter)selectedPainter).removePropertyChangeListener(this);
        }
        
        selectedPainter = pt;
        
        if(selectedPainter instanceof AbstractPainter) {
            ((AbstractPainter)selectedPainter).addPropertyChangeListener(this);
        }
        
        try {
            if(pt != null) {
                JXPropertySheet2 sheet1a = (JXPropertySheet2)sheet1;
                JXPropertySheet2 sheet2a = (JXPropertySheet2)sheet2;
                sheet1a.setBean(pt);
                sheet1a.setHiddenShown(false);
                sheet1a.setExpertShown(false);
                sheet2a.setBean(pt);
                sheet2a.setHiddenShown(false);
                sheet2a.setExpertShown(true);
                sheet2a.setExpertOnly(true);
            } else {
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    
    private void refreshPainters() {
        //testPanel.setBackgroundPainter((Painter)getSelectedTree().getModel().getRoot());
        testPanel.repaint();
    }
    
    
    public CompoundPainter getRootPainter() {
        return (CompoundPainter)getSelectedTree().getModel().getRoot();
    }
    
    public void addPainterSet(PainterSet set) {
        painterTabs.addTab(set.name,set.tree);
    }
    
    public JTree getSelectedTree() {
        return selectedPainterSet.tree;
    }
    
    public static Shape stringToShape(String string, Font font) {
        BufferedImage img = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        GlyphVector vect = font.createGlyphVector(g2.getFontRenderContext(),string);
        Shape shape = vect.getOutline(0f,(float)-vect.getVisualBounds().getY());//50,50);
        g2.dispose();
        return shape;
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        checkerboardPainter1 = new org.jdesktop.swingx.painter.CheckerboardPainter();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenu = new javax.swing.JMenuItem();
        openMenu = new javax.swing.JMenuItem();
        saveMenu = new javax.swing.JMenuItem();
        saveAsMenu = new javax.swing.JMenuItem();
        exportMenu = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        quitMenu = new javax.swing.JMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        painterTabs = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        newPainterCombo = new javax.swing.JComboBox();
        addPainterButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        addEffectButton = new javax.swing.JButton();
        newEffectCombo = new javax.swing.JComboBox();
        sheetPane = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        sheet1 = new JXPropertySheet2();
        jScrollPane2 = new javax.swing.JScrollPane();
        sheet2 = new JXPropertySheet2();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        foregroundCombo = new javax.swing.JComboBox();
        backgroundCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        previewCombo = new javax.swing.JComboBox();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jInternalFrame1 = new javax.swing.JInternalFrame();
        preview = new javax.swing.JPanel();

        fileMenu.setText("File");
        newMenu.setAction(getNewDocumentAction());
        newMenu.setText("New Painter");
        fileMenu.add(newMenu);

        openMenu.setAction(getOpenAction());
        openMenu.setText("Open from XML");
        fileMenu.add(openMenu);

        saveMenu.setAction(getSaveAction());
        fileMenu.add(saveMenu);

        saveAsMenu.setAction(getSaveAsAction());
        fileMenu.add(saveAsMenu);

        exportMenu.setAction(getExportAction());
        exportMenu.setText("Export As PNG");
        fileMenu.add(exportMenu);

        fileMenu.add(jSeparator1);

        quitMenu.setText("Quit");
        quitMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuActionPerformed(evt);
            }
        });

        fileMenu.add(quitMenu);

        menuBar.add(fileMenu);

        jSplitPane1.setBorder(null);
        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setResizeWeight(1.0);
        jSplitPane1.setContinuousLayout(true);
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        painterTabs.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                painterTabsStateChanged(evt);
            }
        });

        newPainterCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        addPainterButton.setAction(getAddPainterAction());
        addPainterButton.setText("Add Painter");

        deleteButton.setAction(getDeleteAction());
        deleteButton.setText("Delete");

        addEffectButton.setAction(getAddEffectAction());
        addEffectButton.setText("Add Effect");

        newEffectCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(addPainterButton)
                            .add(addEffectButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(newEffectCombo, 0, 0, Short.MAX_VALUE)
                            .add(newPainterCombo, 0, 0, Short.MAX_VALUE)))
                    .add(deleteButton))
                .addContainerGap())
        );

        jPanel2Layout.linkSize(new java.awt.Component[] {addEffectButton, addPainterButton, deleteButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addPainterButton)
                    .add(newPainterCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addEffectButton)
                    .add(newEffectCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteButton))
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(painterTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .add(painterTabs, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jSplitPane1.setLeftComponent(jPanel1);

        sheet1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(sheet1);

        sheetPane.addTab("Basic", jScrollPane1);

        sheet2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(sheet2);

        sheetPane.addTab("Advanced", jScrollPane2);

        jSplitPane1.setRightComponent(sheetPane);

        jLabel1.setText("Foreground:");

        jLabel2.setText("Background:");

        foregroundCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        foregroundCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foregroundComboActionPerformed(evt);
            }
        });

        backgroundCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        backgroundCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backgroundComboActionPerformed(evt);
            }
        });

        jLabel3.setText("Preview Component:");

        previewCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        previewCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previewComboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(backgroundCombo, 0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(foregroundCombo, 0, 107, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(previewCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(195, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel3)
                    .add(previewCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(foregroundCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(backgroundCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jInternalFrame1.setResizable(true);
        jInternalFrame1.setVisible(true);
        preview.setBorder(javax.swing.BorderFactory.createTitledBorder("Preview"));
        preview.setPreferredSize(new java.awt.Dimension(200, 100));
        org.jdesktop.layout.GroupLayout previewLayout = new org.jdesktop.layout.GroupLayout(preview);
        preview.setLayout(previewLayout);
        previewLayout.setHorizontalGroup(
            previewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 194, Short.MAX_VALUE)
        );
        previewLayout.setVerticalGroup(
            previewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 82, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jInternalFrame1Layout = new org.jdesktop.layout.GroupLayout(jInternalFrame1.getContentPane());
        jInternalFrame1.getContentPane().setLayout(jInternalFrame1Layout);
        jInternalFrame1Layout.setHorizontalGroup(
            jInternalFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(preview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
        );
        jInternalFrame1Layout.setVerticalGroup(
            jInternalFrame1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(preview, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
        );
        jInternalFrame1.setBounds(10, 10, 210, 150);
        jDesktopPane1.add(jInternalFrame1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jDesktopPane1)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 623, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jDesktopPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void previewComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previewComboActionPerformed
        JComponent selected = (JComponent)previewCombo.getSelectedItem();
        if(selected != null) {
            setPreviewComponent(selected);
        }
    }//GEN-LAST:event_previewComboActionPerformed
    
    private void backgroundComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backgroundComboActionPerformed
        PainterSet set = (PainterSet)backgroundCombo.getSelectedItem();
        if(set != null) {
            PainterUtil.setBGP(testPanel,(Painter)set.model.getRoot());// TODO add your handling code here:
            testPanel.repaint();
        }
    }//GEN-LAST:event_backgroundComboActionPerformed
    
    private void foregroundComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foregroundComboActionPerformed
        PainterSet set = (PainterSet)foregroundCombo.getSelectedItem();
        if(set != null) {
            PainterUtil.setFGP(testPanel,(Painter)set.model.getRoot());// TODO add your handling code here:
            testPanel.repaint();
        }
    }//GEN-LAST:event_foregroundComboActionPerformed
    
    private void painterTabsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_painterTabsStateChanged
        if(painterSets.size() > 0 && painterTabs.getSelectedIndex() >= 0) {
            // add one to skip over the 'none' item
            selectedPainterSet = painterSets.get(painterTabs.getSelectedIndex()+1);
            
        }
    }//GEN-LAST:event_painterTabsStateChanged
    
    
    private void quitMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuActionPerformed
        System.exit(0);// TODO add your handling code here:
    }//GEN-LAST:event_quitMenuActionPerformed
    
    public PainterSet getSelectedPainterSet() {
        return selectedPainterSet;
    }
    
    public void updateFromPainterSet(PainterSet set) {
        // note the off by 1 to account for the invisible 'none' painter set
        int n = painterSets.indexOf(set);
        if(n > 0) {
            painterTabs.setTitleAt(n-1,set.name);
        }
    }
    
    
    private class PainterTreeSelectionListener implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent evt) {
            TreePath path = getSelectedTree().getSelectionPath();
            if(path != null && evt.isAddedPath()) {
                Object comp = path.getLastPathComponent();
                setSelectedPainter(comp);
                if(comp != getSelectedTree().getModel().getRoot() &&
                        (comp instanceof Painter ||
                        comp instanceof BufferedImageOp)) {
                    deleteButton.setEnabled(true);
                } else {
                    deleteButton.setEnabled(false);
                }
            } else {
                deleteButton.setEnabled(false);
            }
        }
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEffectButton;
    private javax.swing.JButton addPainterButton;
    private javax.swing.JComboBox backgroundCombo;
    private org.jdesktop.swingx.painter.CheckerboardPainter checkerboardPainter1;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuItem exportMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JComboBox foregroundCombo;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JInternalFrame jInternalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    public javax.swing.JMenuBar menuBar;
    public javax.swing.JComboBox newEffectCombo;
    private javax.swing.JMenuItem newMenu;
    public javax.swing.JComboBox newPainterCombo;
    private javax.swing.JMenuItem openMenu;
    public javax.swing.JTabbedPane painterTabs;
    private javax.swing.JPanel preview;
    private javax.swing.JComboBox previewCombo;
    private javax.swing.JMenuItem quitMenu;
    private javax.swing.JMenuItem saveAsMenu;
    private javax.swing.JMenuItem saveMenu;
    private javax.swing.JTable sheet1;
    private javax.swing.JTable sheet2;
    private javax.swing.JTabbedPane sheetPane;
    // End of variables declaration//GEN-END:variables
    
    private class ClassNameCellRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if(value instanceof Class) {
                label.setText(((Class) value).getSimpleName());
            } else {
                Class clss = value.getClass();
                label.setText(clss.getSimpleName());
            }
            return label;
        }
    }
    
    private class PainterSetComboRenderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (comp instanceof JLabel && value instanceof PainterSet) {
                JLabel label = (JLabel) comp;
                PainterSet set = (PainterSet) value;
                label.setText(set.name);
            }
            return comp;
        }
    }
    
    
}














