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
package org.jdesktop.swingx.autocomplete;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxEditor;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;

/**
 * This class contains only static utility methods that can be used to set up
 * automatic completion for some Swing components.
 * <p>Usage examples:</p>
 * <p><code>
 * JComboBox comboBox = [...];<br/>
 * AutoCompleteDecorator.<b>decorate</b>(comboBox);<br/>
 * &nbsp;<br/>
 * List items = [...];<br/>
 * JTextField textField = [...];<br/>
 * AutoCompleteDecorator.<b>decorate</b>(textField, items);
 * &nbsp;<br/>
 * JList list = [...];<br/>
 * JTextField textField = [...];<br/>
 * AutoCompleteDecorator.<b>decorate</b>(list, textField);
 * </p></code>
 *
 * @author Thomas Bierhance
 */
public class AutoCompleteDecorator {
    
    /**
     * Enables automatic completion for the given JTextComponent based on the
     * items contained in the given <tt>List</tt>.
     * @param textComponent the text component that will be used for automatic
     * completion.
     * @param items contains the items that are used for autocompletion
     * @param strictMatching <tt>true</tt>, if only given items should be allowed to be entered
     */
    public static void decorate(JTextComponent textComponent, List items, boolean strictMatching) {
        decorate(textComponent, items, strictMatching, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
    }
    
    /**
     * Enables automatic completion for the given JTextComponent based on the
     * items contained in the given <tt>List</tt>.
     * @param items contains the items that are used for autocompletion
     * @param textComponent the text component that will be used for automatic
     * completion.
     * @param strictMatching <tt>true</tt>, if only given items should be allowed to be entered
     * @param stringConverter the converter used to transform items to strings
     */
    public static void decorate(JTextComponent textComponent, List items, boolean strictMatching, ObjectToStringConverter stringConverter) {
        AbstractAutoCompleteAdaptor adaptor = new TextComponentAdaptor(textComponent, items);
        AutoCompleteDocument document = new AutoCompleteDocument(adaptor, strictMatching, stringConverter);
        decorate(textComponent, document, adaptor);
    }
    
    /**
     * Enables automatic completion for the given JTextComponent based on the
     * items contained in the given JList. The two components will be
     * synchronized. The automatic completion will always be strict.
     * @param list a <tt>JList</tt> containing the items for automatic completion
     * @param textComponent the text component that will be enabled for automatic
     * completion
     */
    public static void decorate(JList list, JTextComponent textComponent) {
        decorate(list, textComponent, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
    }
    
    /**
     * Enables automatic completion for the given JTextComponent based on the
     * items contained in the given JList. The two components will be
     * synchronized. The automatic completion will always be strict.
     * @param list a <tt>JList</tt> containing the items for automatic completion
     * @param textComponent the text component that will be used for automatic
     * completion
     * @param stringConverter the converter used to transform items to strings
     */
    public static void decorate(JList list, JTextComponent textComponent, ObjectToStringConverter stringConverter) {
        AbstractAutoCompleteAdaptor adaptor = new ListAdaptor(list, textComponent, stringConverter);
        AutoCompleteDocument document = new AutoCompleteDocument(adaptor, true, stringConverter);
        decorate(textComponent, document, adaptor);
    }
    
    /**
     * Enables automatic completion for the given JComboBox. The automatic
     * completion will be strict (only items from the combo box can be selected)
     * if the combo box is not editable.
     * @param comboBox a combo box
     */
    public static void decorate(final JComboBox comboBox) {
        decorate(comboBox, ObjectToStringConverter.DEFAULT_IMPLEMENTATION);
    }
    
    /**
     * Enables automatic completion for the given JComboBox. The automatic
     * completion will be strict (only items from the combo box can be selected)
     * if the combo box is not editable.
     * @param comboBox a combo box
     * @param stringConverter the converter used to transform items to strings
     */
    public static void decorate(final JComboBox comboBox, final ObjectToStringConverter stringConverter) {
        boolean strictMatching = !comboBox.isEditable();
        // has to be editable
        comboBox.setEditable(true);
        
        // configure the text component=editor component
        JTextComponent editor = (JTextComponent) comboBox.getEditor().getEditorComponent();
        final AbstractAutoCompleteAdaptor adaptor = new ComboBoxAdaptor(comboBox);
        final AutoCompleteDocument document = new AutoCompleteDocument(adaptor, strictMatching, stringConverter);
        decorate(editor, document, adaptor);
        
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
                    // don't popup when the user hits escape (see issue #311)
                    if (keyCode==keyEvent.VK_ESCAPE) return;
                    comboBox.setPopupVisible(true);
                }
            }
        };
        editor.addKeyListener(keyListener);
        
        if (stringConverter!=ObjectToStringConverter.DEFAULT_IMPLEMENTATION) {
            comboBox.setEditor(new AutoCompleteComboBoxEditor(comboBox.getEditor(), stringConverter));
        }
        
        comboBox.addPropertyChangeListener("editor", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (!(e.getNewValue() instanceof AutoCompleteComboBoxEditor)) {
                    comboBox.setEditor(new AutoCompleteComboBoxEditor((ComboBoxEditor) e.getNewValue(), stringConverter));
                }
            }
        });
        
        // Changing the l&f can change the combobox' editor which in turn
        // would not be autocompletion-enabled. The new editor needs to be set-up.
        comboBox.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("editor")) {
                    ComboBoxEditor editor = comboBox.getEditor();
                    if (editor!=null && editor.getEditorComponent()!=null) {
                        if (stringConverter!=ObjectToStringConverter.DEFAULT_IMPLEMENTATION) {
                            editor = new AutoCompleteComboBoxEditor(editor, stringConverter);
                            comboBox.setEditor(editor);
                        }
                        decorate((JTextComponent) editor.getEditorComponent(), document, adaptor);
                        editor.getEditorComponent().addKeyListener(keyListener);
                    }
                }
            }
        });
    }
    
    /**
     * Decorates a given text component for automatic completion using the
     * given AutoCompleteDocument and AbstractAutoCompleteAdaptor.
     * 
     * 
     * @param textComponent a text component that should be decorated
     * @param document the AutoCompleteDocument to be installed on the text component
     * @param adaptor the AbstractAutoCompleteAdaptor to be used
     */
    public static void decorate(JTextComponent textComponent, AutoCompleteDocument document, final AbstractAutoCompleteAdaptor adaptor) {
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
        AbstractAutoCompleteAdaptor adaptor;
        
        public NonStrictBackspaceAction(Action backspace, Action selectionBackward, AbstractAutoCompleteAdaptor adaptor) {
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
