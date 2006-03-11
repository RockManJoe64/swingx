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
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.binding.DataModel;
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

    protected ListSelectionListener createSelectionListener() {
        return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int[] indices = ((JXTable)getComponent()).getSelectedRows();
                    List<DataModel.Row> rows = new ArrayList<DataModel.Row>();
                    for (int i=0; i<indices.length; i++) {
                        int modelIndex = ((JXTable)getComponent()).convertRowIndexToModel(indices[i]);
                        rows.add(getDataModel().getRow(modelIndex));
                    }
                    tableSelectionModel.setSelection(rows);
                }
            }
        };
    }
}
