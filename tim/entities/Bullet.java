package tim.entities;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import tim.Collidable;
import tim.Entity;
import tim.gui.GameGui;

public class Bullet implements Collidable {
	private Rectangle2D.Float myLocation;
	private float xVel;
	private float yVel;
	private Entity shooter;
	private boolean collided;
	
	public Bullet(Entity shooter, float x, float y, float xVel, float yVel) {
		this.shooter = shooter;
		myLocation = new Rectangle2D.Float(x, y, 5, 3);
		this.xVel = xVel;
		this.yVel = yVel;
	}
	
	public void render(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(getX(), getY(), myLocation.width, myLocation.height);
	}
	
	public void update(GameGui gGui, int delta) {
		myLocation.y += gGui.fSpeed * delta;
		myLocation.y += yVel * delta;
		myLocation.x += xVel * delta;
		
		Entity ent = gGui.collidesWithEntity(shooter, myLocation);
		if(ent != null && ent != shooter) {
			ent.hurt(gGui, 3);
			collided = true;
		}
	}
	
	@Override
	public boolean collidesWith(Float rect) {
		return myLocation.intersects(rect);
	}

	@Override
	public float getY() {
		return myLocation.y;
	}

	@Override
	public float getMaxY() {
		return myLocation.y + myLocation.height;
	}

	@Override
	public float getX() {
		return myLocation.x;
	}

	@Override
	public float getMaxX() {
		return myLocation.x + myLocation.width;
	}

	public boolean shouldBeRemoved(GameGui gGui) {
		return collided;
	}
}
