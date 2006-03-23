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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * A bean which represents an URL link.
 * 
 * Text, URL and visited are bound properties. Compares by Text.
 * 
 * @author Mark Davidson
 * @author Jeanette Winzenburg
 * @author rbair
 */
public abstract class URLLinkAction extends LinkAction implements Comparable {
    private static final Logger LOG = Logger.getLogger(URLLinkAction.class
            .getName());

    public static final String URL = "url";
    public static final String TARGET = "target";

    /**
     * 
     * @param text
     * @param target
     * @param url
     */
    public URLLinkAction(String text, String target, URL url) {
        super(text);
        setURL(url);
        setTarget(target);
    }
 
    public URL getURL() {
        return (URL)getValue(URL);
    }
    
    /**
     * Set the url and resets the visited flag.
     * 
     * Think: keep list of visited urls here?
     */
    public void setURL(URL url) {
        setShortDescription(url == null ? "" : url.toString());
        putValue(URL, url);
    }
    
    /**
     * Return the target for the URL.
     * 
     * @return value of the target. If null then "_blank" will be returned.
     */
    public String getTarget() {
        return (String)getValue(TARGET);
    }
    
    /**
     * Set the target that the URL should load into. This can be a uri
     * representing another control or the name of a window or special targets.
     * See: http://www.w3c.org/TR/html401/present/frames.html#adef-target
     */
    public void setTarget(String target) {
        putValue(TARGET, target);
    }

    public URLLinkAction() {
        this(" ", null, null);
    }
    
    public URLLinkAction(String text) {
        this(text, null, null);
    }

    /**
     * @param text text to that a renderer would display
     * @param target the target that a URL should load into.
     * @param template a string that represents a URL with
     * @{N} place holders for string substitution
     * @param args an array of strings which will be used for substitition
     */
    public URLLinkAction(String text, String target, String template, String[] args) {
        super(text);
        setTarget(target);
        setURL(createURL(template, args));
    }

    public void setURLString(String howToURLString) {
        URL url = null;
        try {
            url = new URL(howToURLString);
        } catch (MalformedURLException e) {
            LOG.warning("the given urlString is malformed: " + howToURLString);
        }
        setURL(url);
    }

    /**
     * Create a URL from a template string that has place holders and an array
     * of strings which will be substituted into the place holders. The place
     * holders are represented as
     * 
     * @{N} where N = { 1..n }
     *      <p>
     *      For example, if the template contains a string like:
     *      http://bugz.sfbay/cgi-bin/showbug?cat=@{1}&sub_cat=@{2} and a two
     *      arg array contains: java, classes_swing The resulting URL will be:
     *      http://bugz.sfbay/cgi-bin/showbug?cat=java&sub_cat=classes_swing
     *      <p>
     * @param template a url string that contains the placeholders
     * @param args an array of strings that will be substituted
     */
    private URL createURL(String template, String[] args) {
        URL url = null;
        try {
            String urlStr = template;
            for (int i = 0; i < args.length; i++) {
                urlStr = urlStr.replaceAll("@\\{" + (i + 1) + "\\}", args[i]);
            }
            url = new URL(urlStr);
        } catch (MalformedURLException ex) {
            //
        }
        return url;
    }

    // Comparable interface for sorting.
    public int compareTo(Object obj) {
        if (obj == null) {
            return 1;
        }
        if (obj == this) {
            return 0;
        }
        return getName().compareTo(((URLLinkAction) obj).getName());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof URLLinkAction) {
            URLLinkAction other = (URLLinkAction) obj;
            if (!getName().equals(other.getName())) {
                return false;
            }

            if (!getTarget().equals(other.getTarget())) {
                return false;
            }

            if (!getURL().equals(other.getURL())) {
                return false;
            }
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = 7;

        result = 37 * result + ((getName() == null) ? 0 : getName().hashCode());
        result = 37 * result
                + ((getTarget() == null) ? 1 : getTarget().hashCode());
        result = 37 * result + ((getURL() == null) ? 2 : getURL().hashCode());

        return result;
    }

    public String toString() {

        StringBuffer buffer = new StringBuffer("[");
        // RG: Fix for J2SE 5.0; Can't cascade append() calls because
        // return type in StringBuffer and AbstractStringBuilder are different
        buffer.append("url=");
        buffer.append(getURL());
        buffer.append(", target=");
        buffer.append(getTarget());
        buffer.append(", name=");
        buffer.append(getName());
        buffer.append("]");

        return buffer.toString();
    }

}
