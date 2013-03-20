package tim.entities;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.util.List;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import tim.MusicHandler;
import tim.Entity;
import tim.EntityUtils;
import tim.resource.Resources;
import tim.ScrollHaS;
import tim.gui.GameGui;

public class Bomb implements Entity {
	private Rectangle2D.Float location;
	private Image imgRef;
	private Animation animRef;
	private boolean exploded;
	private boolean finishedExplodeAnim;
	
	public Bomb(float x, float y) {
		this.location = new Rectangle2D.Float(x, y, 32, 32);
		this.imgRef = Resources.getImage("bomb");
		this.animRef = Resources.getAnimation("bomb-explosion");
		animRef.setLooping(false);
	}
	@Override
	public boolean collidesWith(Float rect) {
		return location.intersects(rect);
	}

	@Override
	public float getY() {
		return location.y;
	}

	@Override
	public float getMaxY() {
		return location.y + location.height;
	}

	@Override
	public float getX() {
		return location.x;
	}

	@Override
	public float getMaxX() {
		return location.x + location.height;
	}
	
	@Override
	public void update(GameGui gGui, int delta) {
		location.y += gGui.fSpeed * delta;
		if(exploded) {
			if(animRef.isStopped()) {
				finishedExplodeAnim = true;
			}
			return;
		}
		
		if(gGui.getPlayer().collidesWith(location))
			explode(gGui);
	}
	
	@Override
	public void render(Graphics g) {
		if(!exploded) {
			g.drawImage(imgRef, getX(), getY());
		}else
			animRef.draw(getX(), getY());
	}
	
	@Override
	public boolean shouldBeRemoved(GameGui gGui) {
		return exploded && finishedExplodeAnim;
	}
	
	@Override
	public float distance(float x, float y) {
		return EntityUtils.distance(location, x, y);
	}
	
	private void explode(GameGui gui) {
		exploded = true;
		System.out.println("boom");
		MusicHandler.playSound("bomb", 0);
		List<Entity> nearby = gui.getNearbyEntities(location, 32f);
		
		for(Entity e : nearby) {
			if(e != this)
				e.hurt(gui, 10);
		}
		
		gui.getPlayer().addScore(10);
		
		if(!gui.getPlayer().hasUpgradeOn() && ScrollHaS.getRND().nextFloat() < 0.34f) {
			System.out.println("UPPPPGRAAADEEE");
			gui.getPlayer().addRandomUpgrade(15000);
		}
	}

	@Override
	public void hurt(GameGui gui, int am) {
		if(!exploded)
			explode(gui);
	}
	
	
	
}
