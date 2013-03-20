package tim.gui;

import java.text.NumberFormat;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.EntityUtils;
import tim.Gui;
import tim.Player;
import tim.PlayerBoosts;
import tim.PressChecker;
import tim.resource.Resources;
import tim.ScrollHaS;

public class PermanentUpgradesGui implements Gui {
	
	public static final float[] INCREASE_AMOUNTS = new float[] {
		-0.05f,
		0.01f,
		0.02f,
		0.02f,
		0.05f
	};
	
	private PressChecker[] pressCheckers;
	private PressChecker returnToMain;
	
	public PermanentUpgradesGui() {
		pressCheckers = new PressChecker[INCREASE_AMOUNTS.length];
	}
	
	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		ScrollHaS.drawCenteredText(g, "Permanent Upgrades", 25f);
		ScrollHaS.drawText(g, "Coins: " + Resources.getCoins() + "        Upgrade Cost: " + getUpgradeCost(), 100, 75);
		
		float upgradeLen = g.getFont().getWidth("Upgrade") + 6f;
		
		float yPos = 125f;
		float wid = Display.getWidth() - 100;
		if(wid > 720)
			wid = 720;
		float magicXNum = (Display.getWidth() / 2 - wid / 2);
		
		int magicNum = 0;
		PlayerBoosts plBoosts = Resources.getPlayerBoosts();
		for(int i = 0; i < INCREASE_AMOUNTS.length; i++) {
			float x = magicXNum + ((wid / 2) * magicNum);
			
			pressCheckers[i] = ScrollHaS.drawButton(g, "Upgrade", x, yPos);
			x += upgradeLen + 10;
			ScrollHaS.drawText(g, round(plBoosts.getBoost(i) * 100) + "% " + Player.MODIFIER_NAMES[i], x, yPos);
			magicNum++;
			if(magicNum >= 2) {
				magicNum = 0;
				yPos += 100;
			}
		}
		yPos += 75;
		returnToMain = ScrollHaS.drawCenteredButton(g, "Return to main menu", yPos);
	}

	private String round(float f) {
		return NumberFormat.getInstance().format(f);
	}

	private int getUpgradeCost() {
		int tot = 0;
		for(int i = 0; i < INCREASE_AMOUNTS.length; i++) {
			float boostValue = EntityUtils.roundTo2(Resources.getPlayerBoosts().getBoost(i) - 1);
			tot += Math.round(boostValue / INCREASE_AMOUNTS[i]);
		}
		return Math.round(getUpgradeCost(tot));
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		for(int i = 0; i < pressCheckers.length; i++) {
			PressChecker pc = pressCheckers[i];
			if(pc != null && pc.beingPressed()) {
				onPress(i);
			}
		}
		
		if(returnToMain.beingPressed()) {
			ScrollHaS.application.setGui(new MainMenu());
		}
	}

	private void onPress(int i) {
		int upCost = getUpgradeCost();
		
		if(Resources.getCoins() < upCost) 
			return;
		if(isAlreadyMax(i))
			return;
		
		Resources.removeCoins(upCost);
		Resources.getPlayerBoosts().setBoost(i, Resources.getPlayerBoosts().getBoost(i) + INCREASE_AMOUNTS[i]);
	}

	private boolean isAlreadyMax(int i) {
		int level = (int) Math.round((Resources.getPlayerBoosts().getBoost(i) - 1 ) / INCREASE_AMOUNTS[i]);
		return level >= 10;
	}

//	
//	public static void main(String[] args) {
//		for(int i = 0; i < 10; i++) {
//			System.out.println(getUpgradeCost(i));
//		}
//	}

	private static float getUpgradeCost(int i) {
		float f = (float) 500 + 250 * i;
		if(f > 2000)
			f = 2000;
		return f;
	}
}
