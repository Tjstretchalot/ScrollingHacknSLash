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

public class TemporaryUpgradesGui implements Gui {
	private static final int EXTRA_LIFE = 0;
	private static final int SLOW_IT_DOWN = 1;
	
	public static final String[] TEMPORARY_BUY_NAMES = new String[] {
		"1 Extra Life",
		"Slow It Down"
	};
	
	public static final long SLOW_IT_DOWN_LENGTH = 5000;

	
	private PressChecker[] pressCheckers;
	private PressChecker returnToMain;
	
	public TemporaryUpgradesGui() {
		pressCheckers = new PressChecker[TEMPORARY_BUY_NAMES.length];
	}
	
	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		ScrollHaS.drawCenteredText(g, "Temporary Upgrades", 25f);
		ScrollHaS.drawText(g, "Coins: " + Resources.getCoins() + "        Upgrade Cost: " + getUpgradeCost(), 100, 75);
		
		float upgradeLen = g.getFont().getWidth("Upgrade") + 6f;
		
		float yPos = 125f;
		float wid = Display.getWidth() - 100;
		if(wid > 720)
			wid = 720;
		final float magicXNum = (Display.getWidth() / 2 - wid / 2);
		int magicNum = 0;
		PlayerBoosts plBoosts = Resources.getPlayerBoosts();
		for(int i = 0; i < TEMPORARY_BUY_NAMES.length; i++) {
			float x = magicXNum + ((wid / 2) * magicNum);
			
			pressCheckers[i] = ScrollHaS.drawButton(g, "Upgrade", x, yPos);
			x += upgradeLen + 10;
			ScrollHaS.drawText(g, "Buy " + TEMPORARY_BUY_NAMES[i], x, yPos);
			magicNum++;
			if(magicNum >= 2) {
				magicNum = 0;
				yPos += 100;
			}
		}
		float x = magicXNum;
		ScrollHaS.drawText(g, "Press left control at any time to activate Slow It Down", x, yPos);
		yPos += 50f;
		ScrollHaS.drawText(g, "Extra life is used automatically", x, yPos);
		yPos += 75;
		returnToMain = ScrollHaS.drawCenteredButton(g, "Return to main menu", yPos);
	}

	private String round(float f) {
		return NumberFormat.getInstance().format(f);
	}

	private int getUpgradeCost() {
		return 100;
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
		System.out.println(TEMPORARY_BUY_NAMES[i] + " bought!");
		Resources.removeCoins(upCost);
		switch(i) {
		case EXTRA_LIFE:
			Resources.getPlayerBoosts().setExtraLife();
			break;
		case SLOW_IT_DOWN:
			Resources.getPlayerBoosts().setSlowItDown();
			break;
		}
	}

	private boolean isAlreadyMax(int i) {
		switch(i) {
		case EXTRA_LIFE:
			return Resources.getPlayerBoosts().hasExtraLife();
		case SLOW_IT_DOWN:
			return Resources.getPlayerBoosts().hasSlowItDown();
		}
		return false;
	}
}
