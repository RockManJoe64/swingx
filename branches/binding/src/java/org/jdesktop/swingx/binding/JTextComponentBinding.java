/*
 * JTextComponentBinding.java
 *
 * Created on August 24, 2005, 5:14 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.binding.AWTColumnBinding.AutoCommit;


/**
 * Base class for all Swing Binding implementations that extend JTextComponent,
 * including JTextField, JTextArea, JEditorPane, etc.
 *
 * @author Richard
 */
public class JTextComponentBinding extends SwingColumnBinding {
    /**
     * The original value (prior to binding)
     */
    private String oldValue;
    /**
     * Listens for changes in the value of the Document for this JTextComponent,
     * and updates the edited state accordingly.
     */
    private DocumentChangeListener listener;
    /**
     * The value in the data model -- used to calculate whether the state has changed
     * for the JTextComponent
     */
    private String modelValue;

    /**
     */
    public JTextComponentBinding(JTextComponent comp) {
        super(comp, String.class);
        listener = new DocumentChangeListener();
    }

    /**
     * @inheritDoc
     */
    protected void doInitialize() {
        oldValue = getComponent().getText();
        getComponent().getDocument().addDocumentListener(listener);
    }

    /**
     * @inheritDoc
     */
    public void doRelease() {
        getComponent().getDocument().removeDocumentListener(listener);
        getComponent().setText(oldValue);
    }

    /**
     * @inheritDoc
     */
    protected void setComponentValue(Object value) {
        modelValue = (String)value;
        listener.ignoreEvent = true;
        getComponent().setText(value == null ? "" : modelValue);
        listener.ignoreEvent = false;
    }
    
    /**
     * @inheritDoc
     */
    protected String getComponentValue() {
        return getComponent().getText();
    }
    
    /**
     * @inheritDoc
     */
    public JTextComponent getComponent() {
        return (JTextComponent)super.getComponent();
    }

    /**
     * Listens to changes in the Document, and updates the "edited" flag of
     * this binding accordingly
     */
    private final class DocumentChangeListener implements DocumentListener {
        private boolean ignoreEvent = false;
        public void changedUpdate(DocumentEvent e) {
            handleChange(e);
        }
        public void insertUpdate(DocumentEvent e) {
            handleChange(e);
        }
        public void removeUpdate(DocumentEvent e) {
            handleChange(e);
        }
        private void handleChange(DocumentEvent e) {
            if (!ignoreEvent) {
                if (e.getDocument() != null && modelValue !=  null) {
                    try {
                        setEdited(e.getDocument().getLength() != modelValue.length()
                            || !e.getDocument().getText(0, e.getDocument().getLength()).equals(modelValue));
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                        setEdited(true);
                    }
                }

                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    save();
                } else {
                    validate();
                }
            }
        }
    }
}
