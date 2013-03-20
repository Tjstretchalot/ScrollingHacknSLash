package tim;

import org.newdawn.slick.Graphics;

import tim.gui.GameGui;

/**
 * A basic entity that can be hurt/updated.
 * @author Timothy
 */
public interface Entity extends Collidable {
	/**
	 * Called when this entity is hit by something
	 * @param gui the game gui
	 * @param am how much damage
	 */
	public void hurt(GameGui gui, int am);
	
	/**
	 * Checks the distance between this entity and a specified
	 * location. Should check the shortest distance (EG if the
	 * entity was at 0,0 and 32 pixels wide, 40, 16 should return 8)
	 * @param x the x to compare against
	 * @param y the y to compare against
	 * @return the distance
	 */
	public float distance(float x, float y);
	
	/**
	 * Updates this entity
	 * @param gGui the game gui
	 * @param delta the time in milliseconds since the last update
	 */
	public void update(GameGui gGui, int delta);
	
	/**
	 * Checks if this entity should be removed because it has
	 * died/finished some animation/is no longer on the screen
	 * (Collides with Collidable.WALL)
	 * @param gGui the game gui
	 * @return if this object should be removed and cleaned up
	 */
	public boolean shouldBeRemoved(GameGui gGui);
	
	/**
	 * Renders this entity onto the screen based on it's location
	 * @param g
	 */
	public void render(Graphics g);
}
