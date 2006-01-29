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
import com.jgoodies.validation.Severity;
import com.jgoodies.validation.ValidationResult;
import com.jgoodies.validation.view.ValidationComponentUtils;
import java.awt.Color;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;


/**
 * Base class for all Swing Binding implementations that extends JTextComponent
 * @author Richard
 */
public class JTextComponentBinding extends SwingColumnBinding {
    private String oldValue;
    private DocumentChangeListener listener;
    private String cachedValue;
    private Color preValidationBackgroundColor;
    
    private boolean autoCommit = true;
    
    public JTextComponentBinding(JTextComponent comp) {
        super(comp, String.class);
        listener = new DocumentChangeListener();
    }
    
    protected void doInitialize() {
        oldValue = getComponent().getText();
        getComponent().getDocument().addDocumentListener(listener);
        ValidationComponentUtils.setMessageKey(getComponent(), "KEY");
    }

    public void doRelease() {
        getComponent().getDocument().removeDocumentListener(listener);
        getComponent().setText(oldValue);
        ValidationComponentUtils.setMessageKey(getComponent(), null);
    }

    protected void setComponentValue(Object value) {
        listener.updatingTextField = true;
        getComponent().setText(value == null ? "" : (String)value);
        cachedValue = getComponent().getText();
        listener.updatingTextField = false;
    }
    
    protected String getComponentValue() {
        return getComponent().getText();
    }
    
    public JTextComponent getComponent() {
        return (JTextComponent)super.getComponent();
    }

    protected void setComponentEditable(boolean editable) {
        getComponent().setEditable(editable);
    }

    /**
     * Listens to changes in the Document, and updates the "edited" flag of
     * this binding accordingly
     */
    private final class DocumentChangeListener implements DocumentListener {
        private boolean updatingTextField = false;
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
            if (!updatingTextField) {
                if (e.getDocument() != null && cachedValue !=  null) {
                    try {
                        setEdited(e.getDocument().getLength() != cachedValue.length()
                            || !e.getDocument().getText(0, e.getDocument().getLength()).equals(cachedValue));
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                        setEdited(true);
                    }
                }

                //TODO should only do this if the policy is to validate on edit
                if (autoCommit) {
                    save();
                } else {
                    validate();
                }
            }
        }
    }
}
