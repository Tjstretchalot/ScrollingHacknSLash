package tim.gui;

import static tim.ScrollHaS.drawCenteredButton;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.Gui;
import tim.PressChecker;
import tim.ScrollHaS;

public class OptionsGui implements Gui {

	protected List<String> displayModes;
	protected DisplayMode[] associated;
	private int currentMode;
	private PressChecker resolutionChecker;
	private PressChecker fullscreenChecker;
	private PressChecker returnChecker;

	public OptionsGui() {
		displayModes = new ArrayList<>();
		DisplayMode[] modes = null;
		try {
			modes = Display.getAvailableDisplayModes();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < modes.length; i++) {
			DisplayMode current = modes[i];
			if(current.getWidth() == Display.getWidth() && current.getHeight() == Display.getHeight())
				currentMode = i;
			displayModes.add(current.getWidth() + "x" + current.getHeight() + " at " + current.getBitsPerPixel() +"b/p");
		}
		associated = modes;
	}

	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		float y = 100;
		
		StringBuilder txt = new StringBuilder("Resolution: ");
		resolutionChecker = new PressChecker(cont, txt.toString(), displayModes.get(currentMode), y);
		txt.append(displayModes.get(currentMode));
		
		ScrollHaS.drawCenteredText(g, txt.toString(), y);
		if(resolutionChecker.beingHovered()) {
			ScrollHaS.drawText(g, displayModes.get(currentMode), resolutionChecker.getX(), y);
		}
		y += 50;
		fullscreenChecker = ScrollHaS.drawCenteredButton(g, "Toggle Fullscreen", y);
		
		y += 100f;
		returnChecker = drawCenteredButton(g, "Return to main menu", y);
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		if(returnChecker.beingPressed()) {
			ScrollHaS.application.setGui(new MainMenu());
			return;
		}
		AppGameContainer app = (AppGameContainer) cont;
		if(resolutionChecker.beingPressed()) {
			currentMode++;
			if(currentMode >= displayModes.size()) {
				currentMode = 0;
			}
			
			app.setDisplayMode(associated[currentMode].getWidth(), associated[currentMode].getHeight(), app.isFullscreen());
		}
		
		if(fullscreenChecker.beingPressed()) {
			app.setDisplayMode(associated[currentMode].getWidth(), associated[currentMode].getHeight(), !app.isFullscreen());
		}
	}

}
