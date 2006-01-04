/*
 * NodeDescriptor.java
 *
 * Created on December 31, 2005, 4:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jdesktop.swingx.binding;

/**
 *
 * @author Owner
 */
public interface NodeDescriptor {
    public boolean allowsChildren(Object nodeData);
    public boolean isLeaf(Object nodeData);
    public boolean include(Object nodeData);
}
