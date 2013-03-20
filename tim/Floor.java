package tim;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import tim.resource.Resources;

public class Floor implements Collidable {
	public static final float WIDTH = 32;
	public static final float HEIGHT = 8;
	private float x;
	private Image imgRef;
	
	private Rectangle2D.Float rect;
	
	public Floor(float x, float y, byte dir) {
		this.x = x;
		this.rect = new Rectangle2D.Float(x, y, WIDTH, HEIGHT);
		if(dir == 0) 
			imgRef = Resources.getImage("wall");
		else if(dir == 1)
			imgRef = Resources.getImage("wallleft");
		else if(dir == 2)
			imgRef = Resources.getImage("wallright");
		else
			imgRef = Resources.getImage("wallboth");
	}

	public void render(Graphics g, Color col, float y) {
		imgRef.draw(Math.round(x), Math.round(y), col);
	}
	
	@Override
	public boolean collidesWith(Float rect) {
		return this.rect.intersects(rect);
	}

	public void setY(float y) {
		rect.y = y;
	}

	@Override
	public float getY() {
		return rect.y;
	}

	@Override
	public float getMaxY() {
		return rect.y + rect.height;
	}

	@Override
	public float getX() {
		return rect.x;
	}

	@Override
	public float getMaxX() {
		return x + rect.width;
	}
}
