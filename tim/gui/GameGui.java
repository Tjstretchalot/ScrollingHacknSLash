package tim.gui;

import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.Collidable;
import tim.CollideChecker;
import tim.Entity;
import tim.Gui;
import tim.Line;
import tim.MusicHandler;
import tim.Player;
import tim.ScrollHaS;
import tim.entities.Bomb;
import tim.entities.Bullet;
import tim.entities.Coin;
import tim.entities.Enemy1;
import tim.entities.Enemy1Up;

public class GameGui implements Gui, CollideChecker {
	public float fSpeed;
	private float lSpacing;
	
	private long slowTimeRemaining;
	
	private Player mPlayer;
	private List<Line> mLines;
	private List<Bullet> bullets;
	private List<Entity> entities;
	public boolean gameOver;
	private long totalTime;
	private HashMap<Entity, Class<? extends Entity>> onRemoval;
	private boolean debugVar;
	
	private float dNextLine;
	
	public GameGui() {
		onRemoval = new HashMap<>();
		fSpeed = -0.02f;
		lSpacing = 64;
		mLines = new ArrayList<>();
		bullets = new ArrayList<>();
		entities = new ArrayList<>();
		float yPos = 0;
		while(dNextLine <= 0) {
			Line l = Line.makeRandomLine(mLines.size() == 0 ? null : mLines.get(mLines.size() - 1), true);
			l.y = yPos;
			mLines.add(l);
			dNextLine = lSpacing - (Display.getHeight() - yPos);
			yPos += lSpacing;
		} // ypos = 448 and height = 480, distance to next would be 64 - (480 - 448) = 32
		yPos -= lSpacing;
		Line tmp;
		for(int i = 0; i < mLines.size() / 2; i++) {
			tmp = mLines.get(i);
			mLines.set(i, mLines.get(mLines.size() - i - 1));
			mLines.set(mLines.size() - i - 1, tmp);
		}
		mPlayer = new Player(15, mLines.get(3).y - 35);
	}
	
	@Override
	public void render(Graphics g, GameContainer cont) {
		g.setColor(Color.white);
		g.fillRect(0, 0, Display.getWidth(), Display.getHeight());
		for(Line l : mLines) {
			l.render(g, this, l.y);
		}
		
		for(Bullet b : bullets) {
			b.render(g);
		}
		
		for(Entity e : entities) {
			e.render(g);
		}
		
		mPlayer.render(g);
	}

	@Override
	public void update(GameContainer cont, int delta) {
		if(gameOver) {
			MusicHandler.playSound("death", 4);
			mPlayer.addScore(totalTime / 500);
			ScrollHaS.application.setGui(new GameOverGui(mPlayer));
			return;
		}
		totalTime += delta;
		float fMin = -0.01f - (totalTime / 10000000f);
		if(fMin < -0.03f) {
			if(!debugVar) {
				debugVar = true;
				System.out.println("Reached max speed");
			}
			fMin = -0.03f;
		}
		
		if(slowTimeRemaining > 0) {
			slowTimeRemaining -= delta;
			if(slowTimeRemaining <= 0) {
				// one frame off, whatever
				MusicHandler.playSound("slowdown-done", 3);
			}
			fMin = 0;
		}
		
		float fMax = fMin - 0.02f;
		
		float percDown = mPlayer.getY() / Display.getHeight();
		fSpeed = fMax * percDown;
		if(fSpeed < fMax)
			fSpeed = fMax;
		if(fSpeed > fMin) {
			fSpeed = fMin;
		}
		mPlayer.update(this, delta);
		List<Line> toRemLi = new ArrayList<>();
		for(Line l : mLines) {
			l.y += fSpeed * delta;
			if(l.y < -8f)
				toRemLi.add(l);
		}
		
		mLines.removeAll(toRemLi);
		
		dNextLine += fSpeed * delta;
		if(dNextLine <= 0) {
			dNextLine = lSpacing;
			Line l = Line.makeRandomLine(mLines.get(mLines.size() - 1), false);
			entities.addAll(createEnemies(l));
			mLines.add(l);
		}
		
		List<Bullet> toRemBu = new ArrayList<>();
		for(Bullet b : bullets) {
			b.update(this, delta);
			if(b.shouldBeRemoved(this)) {
				toRemBu.add(b);
			}
		}
		bullets.removeAll(toRemBu);
		
		List<Entity> toRemEnt = new ArrayList<>();
		for(Entity e : entities) {
			e.update(this, delta);
			if(e.getMaxY() < 0 || e.shouldBeRemoved(this)) {
				toRemEnt.add(e);
			}
		}
		entities.removeAll(toRemEnt);
		
		for(Entity e : toRemEnt) {
			if(e.getMaxY() < 0)
				continue;
			Class<? extends Entity> cl = onRemoval.get(e);
			if(cl != null) {
				onRemoval.remove(e);
				Float xF = (Float) e.getX();
				Float yF = (Float) e.getY();
				try {
					entities.add(cl.getDeclaredConstructor(Float.class, Float.class).newInstance(xF, yF));
				} catch (InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	private List<Entity> createEnemies(Line l) {
		List<Entity> res = new ArrayList<>();
		float ch = 1 - (totalTime / 20000);
		if(ch < 0.1f)
			ch = 0.1f;
		if(ScrollHaS.getRND().nextFloat() < ch)
			return res;
		int num = ScrollHaS.getRND().nextInt(Display.getWidth() / 213) + 1;
		int[] used = new int[num];
		for(int i = 0; i < used.length; i++) {
			used[i] = -1; 
		}
		
		for(int i = 0; i < num; i++) {
			int loc = -1;
			while(true) {
				loc = ScrollHaS.getRND().nextInt(l.getWidth());
				if(!l.hasGroundAt(loc)) {
					continue;
				}
				
				boolean b = false;
				for(int n : used) {
					if(n == loc) {
						b = true;
						break;
					}
				}
				if(b)
					continue;
				used[i] = loc;
				res.add(getRandomEnemy(loc * 32, l.y - 32));
				break;
			}
		}
		return res;
	}

	private Entity getRandomEnemy(float x, float y) {
		int maxN = (int) (2 + (totalTime / 1000) / 60);
		if(maxN > 4)
			maxN = 4;
		int n = ScrollHaS.getRND().nextInt(maxN);
		switch(n) {
		case 0:
			return new Bomb(x, y);
		case 1:
			return new Coin(x, y, ScrollHaS.getRND().nextInt(5) + 1);
		case 2:
			return new Enemy1(x, y);
		case 3:
			return new Enemy1Up(x, y);
		default:
			throw new IllegalArgumentException("?");
		}
	}

	@Override
	public Collidable collidesWithSomething(Collidable ignore, Rectangle2D.Float fl) {
		if(Collidable.WALL.collidesWith(fl))
			return Collidable.WALL;
		for(Line l : mLines) {
			if(l == ignore)
				continue;
			if(l.collidesWith(fl))
				return l;
		}
		if(ignore != mPlayer) {
			if(mPlayer.collidesWith(fl))
				return mPlayer;
		}
		return null;
	}

	public void fire(Entity e, float x, float y, float xVel, float yVel) {
		bullets.add(new Bullet(e, x, y, xVel, yVel));
	}
	
	public Player getPlayer() {
		return mPlayer;
	}

	public Entity collidesWithEntity(Entity e, Rectangle2D.Float loc) {
		if(mPlayer != e && mPlayer.collidesWith(loc))
			return mPlayer;
		
		for(Entity en : entities) {
			if(e != en && en.collidesWith(loc)) 
				return en;
		}
		return null;
	}

	public List<Entity> getNearbyEntities(float x, float y, float dist) {
		List<Entity> res = new ArrayList<>();
		if(mPlayer.distance(x, y) < dist) {
			res.add(mPlayer);
		}
		
		for(Entity e : entities) {
			if(e.distance(x, y) < dist)
				res.add(e);
		}
		return res;
	}

	public List<Entity> getNearbyEntities(Rectangle2D.Float location, float dist) {
		List<Entity> res = new ArrayList<>();
		float x = location.x;
		float y = location.y;
		if(mPlayer.distance(x, y) < dist || mPlayer.distance(x + location.width, y) < dist ||
				mPlayer.distance(x, y + location.height) < dist || mPlayer.distance(x + location.width, y + location.height) < dist) {
			res.add(mPlayer);
		}
		
		for(Entity e : entities) {
			if(e.distance(x, y) < dist ||
			   e.distance(x + location.width, y) < dist ||
			   e.distance(x, y + location.height) < dist ||
			   e.distance(x + location.width, y + location.height) < dist)
				res.add(e);
		}
		return res;
	}

	public void queueEnemyOnRemoval(Entity e, Class<? extends Entity> cl) {
		onRemoval.put(e, cl);
	}

	public void slowDown(long time) {
		slowTimeRemaining = time;
		MusicHandler.playSound("slowdown", 3);
	}
}
