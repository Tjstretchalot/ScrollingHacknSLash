package tim.entities;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import tim.Entity;
import tim.EntityUtils;
import tim.MusicHandler;
import tim.Sound;
import tim.gui.GameGui;

public abstract class Enemy implements Entity {
	private static final int HEALTH_BAR_WIDTH = 30;
	protected Rectangle2D.Float location;
	protected Animation curAnim;
	protected Animation deathAnim;
	private Sound hurtSound;
	private Sound deathSound;
	private boolean finishedDeathAnim;
	protected Sound fireSound;
	
	private int health;
	private int maxHealth;
	
	protected Enemy(Rectangle2D.Float loc, Animation anim, Animation deathAnim, 
			Sound fireSound, Sound hurtSound, Sound deathSound, int health) {
		location = loc;
		curAnim = anim;
		this.hurtSound = hurtSound;
		this.deathSound = deathSound;
		this.deathAnim = deathAnim;
		this.fireSound = fireSound;
		deathAnim.setLooping(false);
		this.health = health;
		this.maxHealth = health;
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
		return location.x + location.width;
	}

	@Override
	public void hurt(GameGui gui, int am) {
		health -= am;
		if(health > 0)
			MusicHandler.playSound(hurtSound, 0);
		else {
			MusicHandler.playSound(deathSound, 0);
			onDeath(gui);
		}
	}

	protected void onDeath(GameGui gGui) {
		
	}

	@Override
	public float distance(float x, float y) {
		return EntityUtils.distance(location, x, y);
	}

	@Override
	public void update(GameGui gGui, int delta) {
		location.y += gGui.fSpeed * delta;
	}

	@Override
	public boolean shouldBeRemoved(GameGui gGui) {
		return health <= 0 && finishedDeathAnim;
	}

	@Override
	public void render(Graphics g) {
		if(health <= 0) {
			deathAnim.draw(location.x, location.y);
			finishedDeathAnim = deathAnim.isStopped();
			return;
		}
		curAnim.draw(location.x, location.y);
		drawHealthBar(g);
	}
	
	private void drawHealthBar(Graphics g) {
		int y = Math.round(getY() - 5f);
		float cx = getX() + location.width / 2;
		int x = Math.round(cx - HEALTH_BAR_WIDTH / 2);
		
		float percHealth = (float) getHealth() / getMaxHealth();
		int dis = Math.round(percHealth * HEALTH_BAR_WIDTH);
//		System.out.println(dis + ", " + percHealth);
		if(percHealth > 0.75) {
			g.setColor(Color.green);
		}else if(percHealth > 0.25) {
			g.setColor(Color.yellow);
		}else {
			g.setColor(Color.red);
		}
		g.fillRect(x, y, dis, 2);
		g.setColor(Color.black);
		g.drawRect(x - 1, y - 1, HEALTH_BAR_WIDTH + 1, 3);
	}

	protected void fire(GameGui gGui, float xVel) {
		gGui.fire(this, getX(), getY() + location.height / 2, xVel, 0);
		MusicHandler.playSound(fireSound, 0);
	}
	
	public int getMaxHealth() {
		return maxHealth;
	}

	public float getHealth() {
		return health;
	}
}
