package tim.entities;

import java.awt.geom.Rectangle2D;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import tim.MusicHandler;
import tim.Entity;
import tim.EntityUtils;
import tim.resource.Resources;
import tim.ScrollHaS;
import tim.gui.GameGui;

public class Coin implements Entity {
	private Rectangle2D.Float location;
	private int worth;
	private boolean collected;
	private boolean finishedAnim;
	private boolean beingProtected;
	
	private Animation coinAnim;
	private Animation collectAnim;
	private Animation protectAnim;
	
	public Coin(float x, float y, int am) {
		location = new Rectangle2D.Float(x, y, 32, 32);
		worth = am;
		
		protectAnim = Resources.getAnimation("barrier");
		coinAnim = Resources.getAnimation("coin");
		collectAnim = Resources.getAnimation("coin-collect");
		collectAnim.setLooping(false);
		beingProtected = ScrollHaS.getRND().nextFloat() < 0.34f;
	}
	
	public Coin(Float x, Float y) {
		this(x, y, ScrollHaS.getRND().nextInt(5) + 3);
	}
	
	@Override
	public boolean collidesWith(Rectangle2D.Float rect) {
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
		return location.width;
	}

	@Override
	public void hurt(GameGui gui, int am) {
		if(beingProtected) {
			beingProtected = false;
			MusicHandler.playSound("barrier-kill", 0);
			return;
		}
		if(collected)
			return;
		collected = true;
		MusicHandler.playSound("coin-death", 0);
	}

	@Override
	public float distance(float x, float y) {
		return EntityUtils.distance(location, x, y);
	}

	@Override
	public void update(GameGui gGui, int delta) {
		if(gGui.getPlayer().collidesWith(location) && !beingProtected) {
			collect(gGui);
		}
		location.y += gGui.fSpeed * delta;
	}

	private void collect(GameGui gGui) {
		if(!collected) {
			collected = true;
			Resources.addCoins(worth);
			gGui.getPlayer().addCoins(worth);
			MusicHandler.playSound("coin-collect", 0);
		}
	}

	@Override
	public boolean shouldBeRemoved(GameGui gGui) {
		return collected && finishedAnim;
	}

	@Override
	public void render(Graphics g) {
		if(collected) {
			collectAnim.draw(getX(), getY());
			finishedAnim = collectAnim.isStopped();
			return;
		}
		coinAnim.draw(getX(), getY());
		if(beingProtected)
			protectAnim.draw(getX(), getY());
	}

}
