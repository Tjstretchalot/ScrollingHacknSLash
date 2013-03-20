package tim;

import static tim.PlayerBoosts.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;
import java.awt.geom.RectangularShape;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import tim.gui.GameGui;
import tim.gui.TemporaryUpgradesGui;
import tim.resource.Resources;

public class Player implements Entity {
	private static final float PADDING = 7f;
	public static final float HEALTH_BAR_WIDTH = 30;
	
	private int coins;
	
	public static final String[] MODIFIER_NAMES = new String[] {
		"Gravity Speed",
		"Run Speed",
		"Fire Rate",
		"Bullet Speed",
		"Jump Height"
	};
	
	public static final String[] MODIFIER_RESOURCE_NAMES = new String[] { // update resources when this is changed
		"gravity", "runspeed", "firerate", "bulletspeed", "jumpheight"
	};
	
	public float[][] modifiers = new float[][] {
			{ 0.005f, Resources.getPlayerBoosts().getBoost(0), 0.5f },
			{ 0.2f, Resources.getPlayerBoosts().getBoost(1), 1.25f },
			{ 1250f, Resources.getPlayerBoosts().getBoost(2), 1.25f },
			{ 0.15f, Resources.getPlayerBoosts().getBoost(3), 3f },
			{ -0.2f, Resources.getPlayerBoosts().getBoost(4), 2f }
	};
	
	private Animation anim;
	private String animName;
	private Rectangle2D.Float myLocation;
	private Rectangle2D.Float collisionRect;
	
	private float xVel;
	private float yVel;
	private long timeSinceLastFire;
	private int health;
	private long score;
	private int lastDir;
	
	private boolean hadUpgrade;
	private int upgradeType;
	private long upgradeTimeLeft;
	private boolean playedSound;
	
//	private GameGui gGui;
	
	public Player(float x, float y) {
		collisionRect = new Rectangle2D.Float(x, y, Resources.PLAYER_WIDTH - PADDING * 2, Resources.PLAYER_HEIGHT);
		myLocation = new Rectangle2D.Float(x, y, Resources.PLAYER_WIDTH, Resources.PLAYER_HEIGHT);
		anim = Resources.getAnimation("playerstanding");
		animName = "playerstanding";
		health = getMaxHealth();
	}
	
	@Override
	public void update(GameGui gGui, int delta) {
//		this.gGui = gGui;
		timeSinceLastFire += delta;
		if(hadUpgrade) {
			upgradeTimeLeft -= delta;
			if(!hasUpgradeOn()) {
				hadUpgrade = false;
				MusicHandler.playSound("upgrade-lost", 0);
				modifiers[upgradeType][1] = 1f;
			}
			
			if(!playedSound) {
				if(!Resources.getSound("upgrade").playing()) {
					playedSound = true;
					MusicHandler.playSound(MODIFIER_RESOURCE_NAMES[upgradeType], 3);
				}
			}
		}
		myLocation.y += (gGui.fSpeed * delta);
		
		if(getY() < 0)
			gGui.gameOver = true;
		if(!onGround(gGui)) {
			yVel += modifiers[GRAVITY_SPEED][0] * modifiers[GRAVITY_SPEED][1];
		}
		
		getKeyboardInput(gGui);
		xVel *= modifiers[RUN_SPEED][1];
		
		if(willHit(gGui, (xVel * delta), (gGui.fSpeed * delta)) != null) {
			xVel = -Math.signum(xVel) * 0.01f;
		}
		Collidable c = willHit(gGui, 0, (yVel * delta));
		if(c != null) {
			if(yVel > 0.25)
				MusicHandler.playSound("oof", 3);
			yVel = -Math.signum(yVel) * 0.01f;
			if(equalsDelta(c.getY(), getMaxY(), 1f)) {
				myLocation.y = c.getY() - myLocation.height;
			}
		}
		
		if(xVel != 0)
			lastDir = (int) Math.signum(xVel);
		
		setAnim();
		myLocation.x += xVel * delta;
		myLocation.y += yVel * delta;
		
		if(willHit(gGui, 0, 0) != null)
			freePlayer(gGui, delta);
	}
	
	private void freePlayer(GameGui gGui, int delta) {
		System.out.println("Player got stuck.. not sure how");
		if(willHit(gGui, 1, 0) == null) {
			myLocation.x += 1;
			return;
		}
		if(willHit(gGui, -1, 0) == null) {
			myLocation.x -= 1;
			return;
		}
		
		float amY = 0.1f;
		int counter = 0;
		while(willHit(gGui, 0, amY) != null && counter < 25) {
			amY += 0.1f;
			counter++;
		}
		if(willHit(gGui, 0, amY) != null) {
			amY = -0.1f;
			counter = 0;
			while(willHit(gGui, 0, amY) != null && counter < 5)
			{
				amY -= 0.1f;
				counter++;
			}
		}
		myLocation.y += amY;
	}
	
	private boolean equalsDelta(float f1, float f2, float d) {
		return Math.abs(f2 - f1) < d;
	}

	private void getKeyboardInput(GameGui gGui) {
		if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			xVel = -modifiers[RUN_SPEED][0];
		}else if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			xVel = modifiers[RUN_SPEED][0];
		}else {
			xVel = 0;
		}
		
		if((Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)) && onGround(gGui)) {
			yVel = modifiers[JUMP_HEIGHT][0] * modifiers[JUMP_HEIGHT][1];
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE) || Keyboard.isKeyDown(Keyboard.KEY_RETURN) || Keyboard.isKeyDown(Keyboard.KEY_NUMPAD0)) {
			fireIfPossible(gGui);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			fireDownIfPossible(gGui);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			if(Resources.getPlayerBoosts().useSlowItDown()) 
				gGui.slowDown(TemporaryUpgradesGui.SLOW_IT_DOWN_LENGTH); // yea yea this is terrible
		}
	}

	

	private void setAnim() {
		String res = "player";
		if(!equalsDelta(yVel, 0, 0.03f))
			res += "falling";
		
		if(xVel < 0)
			res += "left";
		else if(xVel > 0)
			res += "right";
		else if(xVel == 0 && equalsDelta(yVel, 0, 0.03f))
			res += "standing";
		setAnim(res);
	}
	
	private void setAnim(String string) {
		if(!animName.equals(string)) {
			animName = string;
			anim = Resources.getAnimation(string);
		}
	}

	private Collidable willHit(CollideChecker cChecker, float xChange, float yChange) {
		return willHit(cChecker, xChange, yChange, 4);
	}
	
	private Collidable willHit(CollideChecker cChecker, float xChange, float yChange, int i) {
		Collidable c = cChecker.collidesWithSomething(this, 
				new Rectangle2D.Float(getCollisionRect().x + xChange, getCollisionRect().y + yChange, 
						getCollisionRect().width, getCollisionRect().height)
		 );
		if(c != null)
			return c;
		if(i <= 0)
			return null;
		
		return willHit(cChecker, xChange / 2, yChange / 2, i - 1);
	}
	
	private void fireIfPossible(GameGui gGui) {
		if(timeSinceLastFire > (modifiers[FIRE_RATE][0] * (2 - modifiers[FIRE_RATE][1]))) {
			float x = getX();
			float y = getY() + myLocation.height / 2;
			int mod = lastDir;
			gGui.fire(this, x, y, mod * (modifiers[BULLET_SPEED][0] * modifiers[BULLET_SPEED][1]), 0);
			MusicHandler.playSound("fire-player", 0);
			timeSinceLastFire = 0;
		}
	}

	
	private void fireDownIfPossible(GameGui gGui) {
		if(timeSinceLastFire > (modifiers[FIRE_RATE][0] * (2 - modifiers[FIRE_RATE][1]))) {
			float x = getX();
			float y = getY() + myLocation.height / 2;
			gGui.fire(this, x, y, 0, (modifiers[BULLET_SPEED][0] * modifiers[BULLET_SPEED][1]));
			MusicHandler.playSound("fire-player", 0);
			timeSinceLastFire = 0;
		}
	}
	private boolean onGround(GameGui gGui) {
		Rectangle2D.Float temp = new Rectangle2D.Float(getCollisionRect().x, getCollisionRect().y + getCollisionRect().height + 0.03f, getCollisionRect().width, 1);
		Collidable c = gGui.collidesWithSomething(this, temp);
		return c != null && c != Collidable.WALL;
	}

	public void render(Graphics g) {
		anim.draw(Math.round(myLocation.x), Math.round(myLocation.y));
		drawHealthBar(g);
		
		if(hadUpgrade) {
			float y = getY() > 240f ? 1f : 380f;
			ScrollHaS.drawCenteredImage(g, Resources.getImage(MODIFIER_RESOURCE_NAMES[upgradeType]), y, 1f);
		}
//		if(gGui != null && onGround(gGui)) {
//			g.setColor(Color.green);
//			ScrollHaS.drawCenteredText(g, "onGround", 150f);
//		}
	}
	
	private void drawHealthBar(Graphics g) {
		int y = Math.round(getY() - 5f);
		float cx = getX() + myLocation.width / 2 + 5;
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
	
	public void hurt(GameGui gGui, int i) {
		health -= i;
		System.out.println("Ouch!");
		if(health < 0) {
			if(Resources.getPlayerBoosts().useExtraLife()) {
				health = getMaxHealth();
				MusicHandler.playSound("extralife", 3);
				return;
			}
			gGui.gameOver = true;
		}
	}
	
	@Override
	public boolean collidesWith(Float rect) {
		return rect.intersects(getCollisionRect());
	}

	private Rectangle2D.Float getCollisionRect() {
		collisionRect.x = myLocation.x + PADDING;
		collisionRect.y = myLocation.y;
		return collisionRect;
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

	public int getMaxHealth() {
		return 15;
	}

	public int getHealth() {
		return health;
	}
	
	public long getScore() {
		return score;
	}
	
	public void addScore(long am) {
		score += am;
	}

	@Override
	public float distance(float x, float y) {
		return EntityUtils.distance(myLocation, x, y);
	}

	@Override
	public boolean shouldBeRemoved(GameGui gGui) {
		return false;
	}

	public boolean hasUpgradeOn() {
		return upgradeTimeLeft > 0;
	}

	public void addRandomUpgrade(long time) {
		hadUpgrade = true;
		playedSound = false;
		upgradeTimeLeft = time;
		MusicHandler.playSound("upgrade", 0);
		final int type = ScrollHaS.getRND().nextInt(5);
		System.out.println("Upgrade type: " + MODIFIER_NAMES[type]);
		upgradeType = type;
		modifiers[type][1] = modifiers[type][2] + (Resources.getPlayerBoosts().getBoost(type) - 1);
	}
	
	public void addCoin() {
		coins++;
	}

	public int getCoins() {
		return coins;
	}

	public void addCoins(int worth) {
		coins += worth;
	}
}
