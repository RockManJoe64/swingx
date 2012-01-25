/**
 * 
 */
package org.jdesktop.swingx;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.Field;
import java.util.Map;

import junit.framework.TestCase;

import org.jdesktop.swingx.multisplitpane.DefaultSplitPaneModel;

/**
 * Submitted on the forums to test for a bug that was since fixed by Hans
 * Muller in {@code MultiSplitPaneLayout} version 1.4.
 * 
 * @author Bruce Alspaugh
 */
public class JXMultiSplitPaneUnitTest extends TestCase {
    private JXMultiSplitPane multiSplitPane;

    protected void setUp() throws Exception {
        DefaultSplitPaneModel model = new DefaultSplitPaneModel();
        MultiSplitLayout layout = new MultiSplitLayout(model);
        multiSplitPane = new JXMultiSplitPane();
        multiSplitPane.setLayout(layout);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Component> getChildMap(MultiSplitLayout layout) {
        Field fields[] = MultiSplitLayout.class.getDeclaredFields();
        try {
            for (Field f : fields) {
                if ("childMap".equals(f.getName())) {
                    f.setAccessible(true);
                    return (Map<String, Component>) f.get(layout);
                }
            }
        } catch (IllegalAccessException ex) {
        }

        return null;
    }

    public final void testLayoutAddRemoveComponent() {
        Component testComponent = new Container();

        MultiSplitLayout layout = multiSplitPane.getMultiSplitLayout();
        Map<String, Component> childMap = getChildMap(layout);
        assertEquals(0, childMap.size());

        layout.addLayoutComponent(DefaultSplitPaneModel.LEFT, testComponent);
        assertEquals(testComponent, childMap.get(DefaultSplitPaneModel.LEFT));

        layout.removeLayoutComponent(testComponent);
        assertEquals(0, childMap.size());
    }
}
