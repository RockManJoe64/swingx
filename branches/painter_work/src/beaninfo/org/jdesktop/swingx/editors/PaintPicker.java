/*
 * PaintPicker.java
 *
 * Created on July 19, 2006, 7:13 PM
 */

package org.jdesktop.swingx.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Paint;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.jdesktop.swingx.JXGradientChooser;
import org.jdesktop.swingx.color.ColorUtil;

/**
 *
 * @author  joshy
 */
public class PaintPicker extends javax.swing.JPanel {
    Component lastPickerUsed = null;
    Paint selectedPaint = Color.PINK;
    JXGradientChooser gradientPicker;
    /** Creates new form PaintPicker */
    public PaintPicker() {
        initComponents();
        
        // set up the color picker
        lastPickerUsed = colorPicker;
        ColorListener colorListener = new ColorListener();
        colorPicker.getSelectionModel().addChangeListener(colorListener);
        alphaSlider.addChangeListener(colorListener);
        
        // set up the gradient picker
        gradientPicker = new JXGradientChooser();
        tabbedPane.addTab("Gradient",gradientPicker);
        gradientPicker.addPropertyChangeListener("gradient",new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                lastPickerUsed = gradientPicker;
                setPaint(gradientPicker.getGradient());
            }
        });
        
        // update when the tabs change
        tabbedPane.getModel().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                lastPickerUsed = tabbedPane.getSelectedComponent();
                Paint old = selectedPaint;
                if(lastPickerUsed == colorPickerParent) {
                    selectedPaint = colorPicker.getSelectionModel().getSelectedColor();
                }
                if(lastPickerUsed == gradientPicker) {
                    selectedPaint = gradientPicker.getGradient();
                }
                firePropertyChange("paint",old,selectedPaint);
            }
        });
        
    }
    
    public Paint getPaint() {
        return selectedPaint;
    }
    
   
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        tabbedPane = new javax.swing.JTabbedPane();
        colorPickerParent = new javax.swing.JPanel();
        colorPicker = new javax.swing.JColorChooser();
        jLabel1 = new javax.swing.JLabel();
        alphaSlider = new javax.swing.JSlider();

        jLabel1.setText("Alpha:");

        alphaSlider.setMaximum(255);
        alphaSlider.setValue(255);

        org.jdesktop.layout.GroupLayout colorPickerParentLayout = new org.jdesktop.layout.GroupLayout(colorPickerParent);
        colorPickerParent.setLayout(colorPickerParentLayout);
        colorPickerParentLayout.setHorizontalGroup(
            colorPickerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(colorPickerParentLayout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(alphaSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
                .addContainerGap())
            .add(colorPicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
        );
        colorPickerParentLayout.setVerticalGroup(
            colorPickerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, colorPickerParentLayout.createSequentialGroup()
                .add(colorPicker, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(colorPickerParentLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(alphaSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addContainerGap())
        );

        colorPickerParentLayout.linkSize(new java.awt.Component[] {alphaSlider, jLabel1}, org.jdesktop.layout.GroupLayout.VERTICAL);

        tabbedPane.addTab("Color", colorPickerParent);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    void setPaint(Paint paint) {
        if(paint == selectedPaint) {
            return;
        }
        Paint old = selectedPaint;
        selectedPaint = paint;
        if(paint instanceof Color) {
            tabbedPane.setSelectedComponent(colorPickerParent);
            colorPicker.setColor((Color)paint);
            alphaSlider.setValue(((Color)paint).getAlpha());
        }
        if(paint instanceof MultipleGradientPaint) {
            tabbedPane.setSelectedComponent(gradientPicker);
            gradientPicker.setGradient((MultipleGradientPaint)paint);
        }
        firePropertyChange("paint", old, selectedPaint);
    }

    // return a paint suitable for display in a property sheet preview
    Paint getDisplayPaint(Rectangle box) {
        if(getPaint() instanceof MultipleGradientPaint) {
            return gradientPicker.getFlatGradient(box.getWidth());
        }
        return getPaint();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider alphaSlider;
    public javax.swing.JColorChooser colorPicker;
    private javax.swing.JPanel colorPickerParent;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables

    private class ColorListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            lastPickerUsed = colorPicker;
            Paint old = selectedPaint;
            selectedPaint = ColorUtil.setAlpha(colorPicker.getSelectionModel().getSelectedColor(), alphaSlider.getValue());
            firePropertyChange("paint", old, selectedPaint);
        }
    }

    
}
