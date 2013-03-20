package tim;

import java.util.Properties;

public class PlayerBoosts {
	public static final int GRAVITY_SPEED = 0;
	public static final int RUN_SPEED = 1;
	public static final int FIRE_RATE = 2;
	public static final int BULLET_SPEED = 3;
	public static final int JUMP_HEIGHT = 4;
	
	private float[] boosts;
	
	public PlayerBoosts() {
		boosts = new float[] {
				1,
				1,
				1,
				1,
				1
		};
	}
	
	private boolean extraLife;
	private boolean slowItDown;
	
	public float getGravitySpeedMod() {
		return boosts[GRAVITY_SPEED];
	}
	
	public void setGravitySpeedMod(float gSpeedMod) {
		boosts[GRAVITY_SPEED] = gSpeedMod;
	}
	
	public float getRunSpeedMod() {
		return boosts[RUN_SPEED];
	}
	
	public void setRunSpeedMod(float rSpeedMod) {
		boosts[RUN_SPEED] = rSpeedMod;
	}
	
	public float getFireRateMod() {
		return boosts[FIRE_RATE];
	}
	
	public void setFireRateMod(float fRateMod) {
		boosts[FIRE_RATE] = fRateMod;
	}
	
	public float getBulletSpeedMod() {
		return boosts[BULLET_SPEED];
	}
	
	public void setBulletSpeedMod(float bSpeedMod) {
		boosts[BULLET_SPEED] = bSpeedMod;
	}
	
	public float getJumpHeightMod() {
		return boosts[JUMP_HEIGHT];
	}
	
	public void setJumpHeightMod(float jHeightMod) {
		boosts[JUMP_HEIGHT] = jHeightMod;
	}

	public float getBoost(int type) {
		return boosts[type];
	}
	
	public void setBoost(int ind, float v) {
		boosts[ind] = v;
	}

	public Properties getPropertyVersion() {
		Properties prop = new Properties();
		for(int i = 0; i < Player.MODIFIER_NAMES.length; i++) {
			prop.setProperty(Player.MODIFIER_NAMES[i], "" + boosts[i]);
		}
		return prop;
	}

	public boolean hasExtraLife() {
		return extraLife;
	}
	
	public boolean useExtraLife() {
		if(extraLife) {
			extraLife = false;
			return true;
		}
		return false;
	}
	
	public void setExtraLife() {
		extraLife = true;
	}
	
	public boolean hasSlowItDown() {
		return slowItDown;
	}
	
	public boolean useSlowItDown() {
		if(slowItDown) {
			slowItDown = false;
			return true;
		}
		return false;
	}
	
	public void setSlowItDown() {
		slowItDown = true;
	}
}
