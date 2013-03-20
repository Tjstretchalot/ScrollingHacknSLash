package tim.gui;

import javax.swing.JOptionPane;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.Gui;
import tim.Player;
import tim.PressChecker;
import tim.ScrollHaS;
import tim.highscore.HighscoreGui;

public class GameOverGui implements Gui {

	private Player player;
	private PressChecker returnChecker;
	private PressChecker submitHighscore;

	public GameOverGui(Player mPlayer) {
		this.player = mPlayer;
	}

	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		ScrollHaS.drawCenteredText(g, "Congrats! You got a score of " + player.getScore() + " and earned " + player.getCoins() + " coins", 100f);
		returnChecker = ScrollHaS.drawCenteredButton(g, "Return to main menu", 0f, 320f, 150f);
		if(Display.getWidth() == 640 && Display.getHeight() == 480)
			submitHighscore = ScrollHaS.drawCenteredButton(g, "Submit Highscore", 320f, 640f, 150f);
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		if(returnChecker != null && returnChecker.beingPressed()) {
			ScrollHaS.application.setGui(new MainMenu());
		}else if(submitHighscore != null && submitHighscore.beingPressed()) {
			String nm = JOptionPane.showInputDialog(null, "What name would you like to put on this highscore?");
			ScrollHaS.application.setGui(new HighscoreGui(nm, player.getScore()));
		}
	}

}
