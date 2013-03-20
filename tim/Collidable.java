package tim;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import org.lwjgl.opengl.Display;

/**
 * A collidable object, not necessarily rectangular but must be able
 * to check collisions on other rectangular objects. Shape support
 * is not currently needed and as such not used
 * @author Timothy
 */
public interface Collidable {
	/**
	 * The 'wall' of the screen, or anything below 0 or above y 480 or x 640.
	 */
	public Collidable WALL = new Collidable() {

		@Override
		public boolean collidesWith(Float rect) {
			return rect.x < 0 || rect.x + rect.width > getMaxX() || rect.y + rect.height > getMaxY();
		}

		@Override
		public float getY() {
			return 0;
		}

		@Override
		public float getMaxY() {
			return Display.getHeight();
		}

		@Override
		public float getX() {
			return 0;
		}

		@Override
		public float getMaxX() {
			return Display.getWidth();
		}
		
	};
	
	/**
	 * Checks if this collidable intersects the specified rectangle
	 * @param rect the rectangle
	 * @return if they are intersecting
	 */
	public boolean collidesWith(Rectangle2D.Float rect);
	
	/**
	 * @return the top y
	 */
	public float getY();
	
	/**
	 * @return the lowest y
	 */
	public float getMaxY();
	
	/**
	 * @return the left-most x
	 */
	public float getX();
	
	/**
	 * @return the right-most x
	 */
	public float getMaxX();
}
