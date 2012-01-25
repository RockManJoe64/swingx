package org.jdesktop.swingx.autocomplete;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.AbstractCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 * <p>This is a cell editor that can be used when a combo box (that has been set
 * up for automatic completion) is to be used in a JTable. The
 * {@link javax.swing.DefaultCellEditor DefaultCellEditor} won't work in this
 * case, because each time an item gets selected it stops cell editing and hides
 * the combo box.
 * </p>
 * <p>
 * Usage example:
 * </p>
 * <code>
 * JTable table = ...;</br>
 * JComboBox comboBox = ...;</br>
 * ...</br>
 * TableColumn column = table.getColumnModel().getColumn(0);</br>
 * column.setCellEditor(new ComboBoxCellEditor(comboBox));
 *  </code>
 */
public class ComboBoxCellEditor extends AbstractCellEditor implements TableCellEditor, Serializable {
    
    private JComboBox comboBox;
    // a Listener listening for key events (handling enter-key)
    // and changes of the combo box' editor component.
    private Handler handler;
    
    /**
     * Creates a new ComboBoxCellEditor.
     * @param comboBox the comboBox that should be used as the cell editor.
     */
    public ComboBoxCellEditor(final JComboBox comboBox) {
        this.comboBox = comboBox;
        
        handler = new Handler();
        
        // Don't do this:
        // this.comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
        // it probably breaks various things
        
        // hitting enter in the combo box should stop cellediting
        JComponent editorComponent = (JComponent) comboBox.getEditor().getEditorComponent();
        editorComponent.addKeyListener(handler);
        // remove the editor's border - the cell itself already has one
        editorComponent.setBorder(null);

        // editor component might change (e.g. a look&feel change)
        // the new editor component needs to be modified then (keyListener, border)
        comboBox.addPropertyChangeListener(handler);
    }
    
    // ------ Implementing CellEditor ------
    /**
     * Returns the value contained in the combo box
     * @return the value contained in the combo box
     */
    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }
    
    /**
     * Tells the combo box to stop editing and accept any partially edited value as the value of the combo box.
     * Always returns true.
     * @return true
     */
    public boolean stopCellEditing() {
        if (comboBox.isEditable()) {
            // Notify the combo box that editing has stopped (e.g. User pressed F2)
            comboBox.actionPerformed(new ActionEvent(this, 0, ""));
        }
        fireEditingStopped();
        return true;
    }
    
    // ------ Implementing TableCellEditor ------
    /**
     * Sets an initial value for the combo box.
     * Returns the combo box that should be added to the client's Component hierarchy.
     * Once installed in the client's hierarchy this combo box will then be able to draw and receive user input.
     * @param table the JTable that is asking the editor to edit; can be null
     * @param value the value of the cell to be edited; null is a valid value
     * @param isSelected will be ignored
     * @param row the row of the cell being edited
     * @param column the column of the cell being edited
     * @return the combo box for editing
     */
    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
        comboBox.setSelectedItem(value);
        return comboBox;
    }
    
    // ------ Implementing TreeCellEditor ------
//    public java.awt.Component getTreeCellEditorComponent(javax.swing.JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
//        String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, false);
//        comboBox.setSelectedItem(stringValue);
//        return comboBox;
//    }
    
    class Handler extends KeyAdapter implements PropertyChangeListener {
        public void keyPressed(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode==keyEvent.VK_ENTER) stopCellEditing();
        }
        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("editor")) {
                ComboBoxEditor editor = comboBox.getEditor();
                if (editor!=null && editor.getEditorComponent()!=null) {
                    JComponent editorComponent = (JComponent) comboBox.getEditor().getEditorComponent();
                    editorComponent.addKeyListener(this);
                    editorComponent.setBorder(null);
                }
            }
        }
    }
}