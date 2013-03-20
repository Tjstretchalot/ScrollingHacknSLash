package tim.gui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.lwjgl.Sys;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.Gui;
import tim.PressChecker;
import tim.ScrollHaS;

public class DonateGui implements Gui {
	private static final String DONATE_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=JMKELKBN86Z9W";
	private static String DONATE_MESSAGE = 
			"Donating will directly support me, the developer continue acquiring " +
			"better and better resources for new games. I will prioritize your input " +
			"for later games, and I will go to you first for alpha/beta testing of future " +
			"games.";
	private static boolean addedDelims;
	
	private PressChecker[] pressCheckers;
	
	public DonateGui() {
		pressCheckers = new PressChecker[2];
	}
	
	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		if(!addedDelims) {
			DONATE_MESSAGE = ScrollHaS.addDelimiters(g, DONATE_MESSAGE, 500);
			addedDelims = true;
		}
		
		String[] inf = DONATE_MESSAGE.split("\n");
		float y = 50f;
		for(String s : inf) {
			ScrollHaS.drawText(g, s, 70, y);
			y += 25;
		}
		
		pressCheckers[0] = ScrollHaS.drawButton(g, "Return", 100, 300);
		pressCheckers[1] = ScrollHaS.drawButton(g, "Donate", 400, 300);
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		for(int i = 0; i < pressCheckers.length; i++) {
			if(pressCheckers[i] != null) {
				if(pressCheckers[i].beingPressed()) {
					onPress(i);
				}
			}
		}
	}

	private void onPress(int i) {
		switch(i) {
		case 0:
			ScrollHaS.application.setGui(new MainMenu());
			break;
		case 1:
			Sys.openURL(DONATE_URL);
			break;
		}
	}

}
