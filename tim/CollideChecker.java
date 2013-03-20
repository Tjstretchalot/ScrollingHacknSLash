package tim;

import java.awt.geom.Rectangle2D;

/**
 * Handles collision checking
 * @author Timothy
 */
public interface CollideChecker {
	/**
	 * Checks if the specified rectangle collides with anything BESIDES
	 * the ignore collidable. This collidable is likely the calling class
	 * for collision handling
	 * @param ignore the collidable object to ignore
	 * @param fl the rectangle to check for intersecting collidables
	 * @return the collidable that is there, or null if nothing is
	 */
	public Collidable collidesWithSomething(Collidable ignore, Rectangle2D.Float fl);
}
