package tim.entities;

import java.awt.geom.Rectangle2D;
import org.newdawn.slick.Animation;

import tim.Collidable;
import tim.resource.Resources;
import tim.ScrollHaS;
import tim.gui.GameGui;

public class Enemy1 extends Enemy {
	protected long lastShotTime;
	private String dir;
	
	public Enemy1(float x, float y) {
		super(new Rectangle2D.Float(x, y, 32, 32), getAppropriateAnim(x), 
				Resources.getAnimation("enemy1-death"), 
				Resources.getSound("fire-player"), Resources.getSound("enemy1-hurt"),
				Resources.getSound("enemy1-death"), 5);
		
		dir = getAppropriateDir(x);
	}

	private static Animation getAppropriateAnim(float x) {
		String dir = getAppropriateDir(x);
		
		return Resources.getAnimation("enemy1-" + dir);
	}

	private static String getAppropriateDir(float x) {
		if(x < Collidable.WALL.getMaxX() / 2)
			return "right";
		else
			return "left";
	}

	@Override
	public void update(GameGui gGui, int delta) {
		super.update(gGui, delta);
		
		if(ScrollHaS.getTime() - lastShotTime > 4500f) {
			lastShotTime = ScrollHaS.getTime();
			
			if(dir.equals("right")) {
				fire(gGui, 0.5f);
			}else {
				fire(gGui, -0.5f);
			}
		}
	}
	
	@Override
	protected void onDeath(GameGui gGui) {
		super.onDeath(gGui);
		gGui.queueEnemyOnRemoval(this, Coin.class);
		gGui.getPlayer().addScore(20);
	}
}
