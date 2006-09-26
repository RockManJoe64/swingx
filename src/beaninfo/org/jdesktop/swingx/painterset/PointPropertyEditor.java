package org.jdesktop.swingx.painterset;

import com.l2fprod.common.beans.editor.StringConverterPropertyEditor;
import com.l2fprod.common.util.converter.ConverterRegistry;
import java.awt.Point;

   
public class PointPropertyEditor extends StringConverterPropertyEditor {    
    public PointPropertyEditor() {
        
    }
    protected Object convertFromString(String text) {
        return ConverterRegistry.instance().convert(Point.class, text);
    }
  }    