/*
 * ExpressionHighlighter.java
 *
 * Created on May 18, 2005, 6:26 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.expression;

import java.awt.Color;
import java.awt.Component;
import net.sf.jga.fn.UnaryFunctor;
import net.sf.jga.parser.ParseException;
import org.jdesktop.dataset.DataRow;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.Highlighter;

/**
 * Based on some expressions, calculates the foreground, background, selected
 * foreground and selected background colors to use for highlighting.
 *
 * NOTE: Currently expressions are based on JGA expressions for DataRows -- need
 * to be rewritten generically but I needed something for JavaOne :)
 *
 * @author rbair
 */
public class ExpressionHighlighter extends Highlighter {
    private String foregroundExpression;
    private String backgroundExpression;
    private transient Parser parser = new Parser();
    
    /** Creates a new instance of ExpressionHighlighter */
    public ExpressionHighlighter() {
    }

    protected Color computeForeground(Component renderer, ComponentAdapter adapter) {
        Color def = super.computeForeground(renderer, adapter);
        if (getForegroundExpression() == null || getForegroundExpression().equals("")) {
            return def;
        } else {
            return evaluateExpression(getForegroundExpression(), adapter, def);
        }
    }

    protected Color computeBackground(Component renderer, ComponentAdapter adapter) {
        Color def = super.computeBackground(renderer, adapter);
        if (getBackgroundExpression() == null || getBackgroundExpression().equals("")) {
            return def;
        } else {
            return evaluateExpression(getBackgroundExpression(), adapter, def);
        }
    }

    private Color evaluateExpression(String exp, ComponentAdapter comp, Color def) {
        Object value = comp.getValue();
        if (value instanceof DataRow) {
            DataRow row = (DataRow)value;
            parser.bindThis(row.getTable().getDataSet());
            parser.setUndecoratedDecimal(true);

            if (exp != null) {
                UnaryFunctor<DataRow,?> expImpl =  null;
                try {
                    expImpl = parser.parseComputedColumn(row.getTable(), exp);
                } catch (ParseException pe) {
                    pe.printStackTrace();
                }
                Color c = Color.decode((String)expImpl.fn(row));
                return c;
            }
        }
        return def;
    }

    public String getForegroundExpression() {
        return foregroundExpression;
    }

    public void setForegroundExpression(String foregroundExpression) {
        this.foregroundExpression = foregroundExpression;
    }

    public String getBackgroundExpression() {
        return backgroundExpression;
    }

    public void setBackgroundExpression(String backgroundExpression) {
        this.backgroundExpression = backgroundExpression;
    }
}
