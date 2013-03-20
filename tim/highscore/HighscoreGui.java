package tim.highscore;

import static tim.ScrollHaS.drawCenteredButton;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.Gui;
import tim.Player;
import tim.PressChecker;
import tim.ScrollHaS;
import tim.gui.MainMenu;

public class HighscoreGui implements Gui {
	private Highscore[] highscores;
	private int playerInd;
	private PressChecker returnToMainMenu;
	
	public HighscoreGui(String name, long l) {
		highscores = HighscoreManager.putHighScore(name, l);
		playerInd = getPlayerIndex(name, l);
	}
	
	public HighscoreGui() {
		highscores = HighscoreManager.getHighScores();
		playerInd = -1;
	}
	
	private int getPlayerIndex(String name, long score) {
		for(int i = 0; i < highscores.length; i++) {
			Highscore hScore = highscores[i];
			if(hScore != null && hScore.getPlayerName().equals(name) && hScore.getScore() == score) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		float yPos = 100;
		float xPos = 50;
		
		for(int i = 0; i < highscores.length; i++) {
			if(highscores[i] == null)
				break;
			ScrollHaS.drawText(g, highscores[i].toString(), xPos, yPos);
			if(i == playerInd) 
				ScrollHaS.drawText(g, highscores[i].toString(), xPos, yPos);
			yPos += 25;
		}

		returnToMainMenu = drawCenteredButton(g, "Return to main menu", yPos);
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		if(returnToMainMenu != null) {
			if(returnToMainMenu.beingPressed()) {
				ScrollHaS.application.setGui(new MainMenu());
			}
		}
	}

}
