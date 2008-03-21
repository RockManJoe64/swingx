package org.jdesktop.swingx.color;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.apache.batik.ext.awt.MultipleGradientPaint;
import org.jdesktop.swingx.JXMultiThumbSlider;
import org.jdesktop.swingx.JXGradientChooser;
import org.jdesktop.swingx.multislider.Thumb;

public class GradientTrackRenderer implements JXMultiThumbSlider.TrackRenderer {
    private Paint checker_paint;
    
    public GradientTrackRenderer(JXGradientChooser gradientPicker) {
	this.gradientPicker = gradientPicker;
        checker_paint = ColorUtil.getCheckerPaint();
    }
    
    private final JXGradientChooser gradientPicker;
    
    public void paintTrack(Graphics2D g, JXMultiThumbSlider slider) {
	
	// get the list of colors
	List<Thumb<Color>> stops = slider.getModel().getSortedThumbs();
	int len = stops.size();
	
	// set up the data for the gradient
	float[] fractions = new float[len];
	Color[] colors = new Color[len];
	int i = 0;
	for(Thumb<Color> thumb : stops) {
	    colors[i] = (Color)thumb.getObject();
	    fractions[i] = thumb.getPosition();
	    i++;
	}
	

	// calculate the track area
	int thumb_width = 12;
	int track_width = slider.getWidth() - thumb_width;
	g.translate(thumb_width / 2, 12);
	Rectangle2D rect = new Rectangle(0, 0, track_width, 20);
	
	// fill in the checker
	g.setPaint(checker_paint);
	g.fill(rect);
	
	// fill in the gradient
	Point2D start = new Point2D.Float(0,0);
	Point2D end = new Point2D.Float(track_width,0);
	MultipleGradientPaint paint = new org.apache.batik.ext.awt.LinearGradientPaint(
		(float)start.getX(),
		(float)start.getY(),
		(float)end.getX(),
		(float)end.getY(),
		fractions,colors);
	g.setPaint(paint);
	g.fill(rect);
	
	
	// draw a border
	g.setColor(Color.black);
	g.draw(rect);
	g.translate(-thumb_width / 2, -12);
    }
}