/*
 * JTableBinding.java
 *
 * Created on August 12, 2005, 10:34 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.binding;
import org.jdesktop.swingx.JXTable;
/**
 * A binding implementation that binds a JTable to a DataModel.
 *
 * @author rbair
 */
public class JXTableBinding extends JTableBinding {
    /** Creates a new instance of JTableBinding */
    public JXTableBinding(JXTable table) {
        super(table);
        setSelectionModelName(table.getSelectionModelName());
    }
    
    protected void doInitialize() {
        JXTable table = (JXTable)super.getComponent();
        super.doInitialize();
    }
}
