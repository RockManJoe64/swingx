package org.jdesktop.swingx.autocomplete;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.TextAction;

/**
 * This class contains only static utility methods that can be used to set up
 * automatic completion for some Swing components.
 * <p>Usage examples:</p>
 * <p><code>
 * JComboBox comboBox = [...];<br/>
 * Configurator.<b>enableAutoCompletion</b>(comboBox);<br/>
 * &nbsp;<br/>
 * JList list = [...];<br/>
 * JTextField textField = [...];<br/>
 * Configurator.<b>enableAutoCompletion</b>(list, textField);
 * </p></code>
 *
 * @author Thomas Bierhance
 */
public class Configurator {
    
    /**
     * Enables automatic completion for the given JTextComponent based on the
     * items contained in the given JList. The two components will be
     * synchronized. The automatic completion will always be strict.
     *
     * @param list a list
     * @param textComponent the text component that will be used for automatic
     * completion.
     */
    public static void enableAutoCompletion(JList list, JTextComponent textComponent) {
        AbstractComponentAdaptor adaptor = new ListAdaptor(list, textComponent);
        Document document = new Document(adaptor, true);
        configureTextComponent(textComponent, document, adaptor);
    }
    
    /**
     * Enables automatic completion for the given JComboBox. The automatic
     * completion will be strict (only items from the combo box can be selected)
     * if the combo box is not editable.
     * @param comboBox a combobox
     */
    public static void enableAutoCompletion(final JComboBox comboBox) {
        boolean strictMatching = !comboBox.isEditable();
        // has to be editable
        comboBox.setEditable(true);
        
        // configure the text component=editor component
        JTextComponent editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
        final AbstractComponentAdaptor adaptor = new ComboBoxAdaptor(comboBox);
        final Document document = new Document(adaptor, strictMatching);
        configureTextComponent(editor, document, adaptor);
        
        // show the popup list when the user presses a key
        final KeyListener keyListener = new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                // don't popup on action keys (cursor movements, etc...)
                if (keyEvent.isActionKey()) return;
                // don't popup if the combobox isn't visible anyway
                if (comboBox.isDisplayable() && !comboBox.isPopupVisible()) {
                    int keyCode = keyEvent.getKeyCode();
                    // don't popup when the user hits shift,ctrl or alt
                    if (keyCode==keyEvent.VK_SHIFT || keyCode==keyEvent.VK_CONTROL || keyCode==keyEvent.VK_ALT) return;
                    comboBox.setPopupVisible(true);
                }
            }
        };
        editor.addKeyListener(keyListener);
        
        // Changing the l&f can change the combobox' editor which in turn
        // would not be autocompletion-enabled. The new editor needs to be set-up.
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("editor")) {
                    ComboBoxEditor editor = comboBox.getEditor();
                    if (editor!=null && editor.getEditorComponent()!=null) {
                        configureTextComponent((JTextComponent) editor.getEditorComponent(), document, adaptor);
                        editor.getEditorComponent().addKeyListener(keyListener);
                    }
                }
            }
        });
    }
    
    /**
     * Configures a given text component for automatic completion using the
     * given Document and AbstractComponentAdaptor.
     * @param textComponent a text component that should be configured
     * @param document the Document to be installed on the text component
     * @param adaptor the AbstractComponentAdaptor to be used
     */
    public static void configureTextComponent(JTextComponent textComponent, Document document, final AbstractComponentAdaptor adaptor) {
        // install the document on the text component
        textComponent.setDocument(document);
        
        // mark entire text when the text component gains focus
        // otherwise the last mark would have been retained which is quiet confusing
        textComponent.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                JTextComponent textComponent = (JTextComponent) e.getSource();
                adaptor.markEntireText();
            }
        });
        
        // Tweak some key bindings
        InputMap editorInputMap = textComponent.getInputMap();
        if (document.isStrictMatching()) {
            // move the selection to the left on VK_BACK_SPACE
            editorInputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0), DefaultEditorKit.selectionBackwardAction);
            // ignore VK_DELETE and CTRL+VK_X and beep instead when strict matching
            editorInputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0), errorFeedbackAction);
            editorInputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK), errorFeedbackAction);
        } else {
            ActionMap editorActionMap = textComponent.getActionMap();
            // leave VK_DELETE and CTRL+VK_X as is
            // VK_BACKSPACE will move the selection to the left if the selected item is in the list
            // it will delete the previous character otherwise
            editorInputMap.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0), "nonstrict-backspace");
            editorActionMap.put("nonstrict-backspace", new NonStrictBackspaceAction(
                    editorActionMap.get(DefaultEditorKit.deletePrevCharAction),
                    editorActionMap.get(DefaultEditorKit.selectionBackwardAction),
                    adaptor));
        }
    }
    
    static class NonStrictBackspaceAction extends TextAction {
        Action backspace;
        Action selectionBackward;
        AbstractComponentAdaptor adaptor;
        
        public NonStrictBackspaceAction(Action backspace, Action selectionBackward, AbstractComponentAdaptor adaptor) {
            super("nonstrict-backspace");
            this.backspace = backspace;
            this.selectionBackward = selectionBackward;
            this.adaptor = adaptor;
        }
        
        public void actionPerformed(ActionEvent e) {
            if (adaptor.listContainsSelectedItem()) {
                selectionBackward.actionPerformed(e);
            } else {
                backspace.actionPerformed(e);
            }
        }
    }
    
    /**
     * A TextAction that provides an error feedback for the text component that invoked
     * the action. The error feedback is most likely a "beep".
     */
    static Object errorFeedbackAction = new TextAction("provide-error-feedback") {
        public void actionPerformed(ActionEvent e) {
            UIManager.getLookAndFeel().provideErrorFeedback(getTextComponent(e));
        }
    };
}