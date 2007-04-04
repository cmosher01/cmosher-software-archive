/*
 * Created on May 27, 2006
 */
package ui;

import java.awt.Graphics2D;

public interface Drawable
{
	void draw(final Graphics2D graphics, DrawingOptions options);
	void calc(final Graphics2D graphics);
}
