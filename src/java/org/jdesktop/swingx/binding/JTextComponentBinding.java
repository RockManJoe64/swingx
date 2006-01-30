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
        //AbstractDocument doesn't let me mutate the value if it was in
        //the middle of an event notification. I'm not sure of a way around
        //this at present. I'm going to simply punt and not update the
        //value if readers are in the process of firing. Note, this situation
        //occurs when autocommit save occurs (because save causes an implicit
        //load). This will probably not affect to many people, but it is still
        //not right. Humph.
        if (!listener.eventInProgress) {
            listener.ignoreEvent = true;
            getComponent().setText(value == null ? "" : modelValue);
            listener.ignoreEvent = false;
        }
    }
    
    /**
     * @inheritDoc
     */
    protected String getComponentValue() {
        String s = getComponent().getText();
        System.out.println("getComponentValue=" + s);
        return s;
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
        private boolean eventInProgress = false;
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
            try {
            System.out.println("Event = " + e.getDocument().getText(0, e.getDocument().getLength()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (!ignoreEvent) {
                if (e.getDocument() != null && modelValue !=  null) {
                    try {
                        setEdited(e.getDocument().getLength() != modelValue.length()
                            || !e.getDocument().getText(0, e.getDocument().getLength()).equals(modelValue));
                    } catch (BadLocationException ex) {
                        ex.printStackTrace();
                        setEdited(true);
                    }
                } else if (modelValue == null) {
                    setEdited(e.getDocument() != null);
                }

                if (getAutoCommit() == AutoCommit.ON_CHANGE) {
                    eventInProgress = true;
                    save();
                    eventInProgress = false;
                } else {
                    validate();
                }
            }
        }
    }
}
