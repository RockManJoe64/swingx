/*
 * RendererExpression.java
 *
 * Created on May 13, 2005, 1:00 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.jdesktop.swingx.expression;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rb156199
 */
public class RendererExpression {
    private String format;
    private List<String> expressions;

    public RendererExpression() {
        format = "";
        expressions = new ArrayList<String>();
    }

    public RendererExpression(String format, String... expressions) {
        this.format = format;
        this.expressions = new ArrayList<String>();
        for (String e : expressions) {
            this.expressions.add(e);
        }
    }

    public void setFormat(String format) {
//            if (this.format == format || (this.format != null && !this.format.equals(format))) {
//                return;
//            }

        String oldFormat = this.format;
        this.format = format == null ? "" : format;
//            firePropertyChange("format", oldFormat, format);
    }

    public String getFormat() {
        return format;
    }

    public void setExpressions(List<String> expressions) {
        this.expressions.clear();
        this.expressions.addAll(expressions);
    }

    public void setExpressions(String... expressions) {
        this.expressions.clear();
        for (String e : expressions) {
            this.expressions.add(e);
        }
    }

    public List<String> getExpressions() {
        return new ArrayList<String>(expressions);
    }
}
