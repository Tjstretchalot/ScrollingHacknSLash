package tim.entities;

import tim.resource.Resources;
import tim.gui.GameGui;


public class Enemy1Up extends Enemy1 {

	public Enemy1Up(float x, float y) {
		super(x, y);
		curAnim = Resources.getAnimation("enemy1-up");
	}

	@Override
	public void fire(GameGui gGui, float xVel) {
		gGui.fire(this, getX(), getY() + location.height / 2, 0, -0.5f);
		fireSound.play(1f, 0.5f, false);
	}
}
