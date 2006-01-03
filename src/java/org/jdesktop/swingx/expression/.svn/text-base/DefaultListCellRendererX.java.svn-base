/*
 * DefaultListCellRendererX.java
 *
 * Created on May 13, 2005, 12:58 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.expression;

import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import net.sf.jga.fn.UnaryFunctor;
import net.sf.jga.parser.ParseException;
import org.jdesktop.dataset.DataRow;
import org.jdesktop.swingx.JXList;


/**
 *
 * @author rb156199
 */
public class DefaultListCellRendererX extends DefaultListCellRenderer {
    private Parser parser;

    public DefaultListCellRendererX() {
        parser = new Parser();
    }

    public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        //construct the text (don't worry about an icon yet...)
        if (list instanceof JXList && value instanceof DataRow) {
            JXList xlist = (JXList)list;

            DataRow row = (DataRow)value;
            parser.bindThis(row.getTable().getDataSet());
            parser.setUndecoratedDecimal(true);

            RendererExpression exp = xlist.getRendererExpression();
            if (exp != null) {
                //execute each expression
                List<String> expressions = exp.getExpressions();
                Object[] values = new Object[expressions.size()];
                for (int i=0; i<values.length; i++) {
                    String e = expressions.get(i);
                    UnaryFunctor<DataRow,?> expImpl =  null;
                    try {
                        expImpl = parser.parseComputedColumn(row.getTable(), e);
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }
                    values[i] = expImpl == null ? e : expImpl.fn(row);
                }
                value = String.format(exp.getFormat(), values);
            }
        }
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }

}
