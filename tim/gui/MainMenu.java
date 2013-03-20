package tim.gui;

import static tim.ScrollHaS.*;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import tim.Gui;
import tim.PressChecker;
import tim.ScrollHaS;
import tim.highscore.HighscoreGui;

public class MainMenu implements Gui {
	private Graphics iRecallMoreYouSee;
	private static final int PLAY_GAME = 0;
	private static final int OPTIONS = 1;
	private static final int BUY_TEMPORARY_UPGRADES = 2;
	private static final int BUY_PERMANENT_UPGRADES = 3;
	private static final int VIEW_HIGHSCORES = 4;
	private static final int CREDITS = 5;
	private static final int DONATE = 6;
	private static final String[] options = {
		"Play Game",
		"Options",
		"Buy Temporary Upgrades",
		"Buy Permanent Upgrades",
		"View Highscores",
		"Credits",
		"Donate"
	};
	
	private PressChecker[] checkers;
	
	public MainMenu() {
		checkers = new PressChecker[options.length];
	}
	
	@Override
	public void render(Graphics g, GameContainer cont) {
		iRecallMoreYouSee = g;
		drawCenteredText(g, "Scrolling Platformer Hack`n`Slash by Tjstretchalot", 25);
		
		float yPos = 100;
		for(int i = 0; i < options.length; i++) {
			if(i == DONATE) {
				checkers[i] = drawButton(g, options[i], 5, Display.getHeight() - 20);
				continue;
			}
			checkers[i] = drawCenteredButton(g, options[i], yPos);
			yPos += 50;
		}
	}

	@Override
	public void update(GameContainer cont, int delta) {
		for(int i = 0; i < options.length; i++) {
			if(checkers[i] == null)
				continue;
			if(checkers[i].beingPressed())  {
				onPress(i);
			}
		}
	}

	private void onPress(int i) {
		System.out.println(options[i] + " pressed");
		switch(i) {
		case PLAY_GAME:
			ScrollHaS.application.setGui(new GameGui());
			break;
		case OPTIONS:
			ScrollHaS.application.setGui(new OptionsGui());
			break;
		case BUY_TEMPORARY_UPGRADES:
			ScrollHaS.application.setGui(new TemporaryUpgradesGui());
			break;
		case BUY_PERMANENT_UPGRADES:
			ScrollHaS.application.setGui(new PermanentUpgradesGui());
			break;
		case VIEW_HIGHSCORES:
			ScrollHaS.application.setGui(new HighscoreGui());
			break;
		case CREDITS:
			ScrollHaS.application.setGui(new CreditsGui(iRecallMoreYouSee));
			break;
		case DONATE:
			ScrollHaS.application.setGui(new DonateGui());
			break;
		}
	}
	
	
}
