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
package org.jdesktop.swingx.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.Document;
import org.jdesktop.swingx.JXEditorPane;


/**
 * An URLLinkAction using a JXEditorPane to display the URL
 * 
 * adds an internal HyperlinkListener to visit links contained
 * in the document. 
 * 
 * @author Jeanette Winzenburg
 * @author rbair
 */
public class EditorPaneLinkAction extends URLLinkAction {
    private JXEditorPane editorPane;
    private HyperlinkListener hyperlinkListener;
    
    public EditorPaneLinkAction() {
        this(null);
    }
    
    public EditorPaneLinkAction(JXEditorPane pane) {
        if (editorPane != null) {
            editorPane.removeHyperlinkListener(getHyperlinkListener());
        }
        if (pane == null) {
            pane = createDefaultEditorPane();
        }
        this.editorPane = pane;
        pane.addHyperlinkListener(getHyperlinkListener());
    }
    

    public JXEditorPane getOutputComponent() {
        return editorPane;
    }
    
    public void actionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                visit(getURL());
            }
        });
    }

    public void visit(URL url) {
        try {
            // make sure to reload
            editorPane.getDocument().putProperty(Document.StreamDescriptionProperty, null);
            // JW: editorPane defaults to asynchronous loading
            // no need to explicitly start a thread - really?
            editorPane.setPage(url);
            setVisited(true);
        } catch (IOException e1) {
            editorPane.setText("<html>Error 404: couldn't show " + url + " </html>");
        }
    }

    protected JXEditorPane createDefaultEditorPane() {
        final JXEditorPane editorPane = new JXEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        return editorPane;
    }

    protected HyperlinkListener getHyperlinkListener() {
        if (hyperlinkListener == null) {
            hyperlinkListener = createHyperlinkListener();
        }
        return hyperlinkListener;
    }

    protected HyperlinkListener createHyperlinkListener() {
        HyperlinkListener l = new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (HyperlinkEvent.EventType.ACTIVATED == e.getEventType()) {
                    visit(e.getURL());
                }
                
            }
            
        };
        return l;
    }
}
