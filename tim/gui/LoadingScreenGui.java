package tim.gui;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Line;

import tim.Gui;
import tim.resource.Resources;
import tim.ScrollHaS;

public class LoadingScreenGui implements Gui {
	private static final int LINE_MOVE_TIME = 75;
	private Line[] lines;
	private int counter;
	private long nextUpdate;
	
	private Color[] nearnessColors;
	
	private static final int TRAIL_LENGTH = 5;
	
	public LoadingScreenGui() {
		nextUpdate = LINE_MOVE_TIME;
		
		nearnessColors = new Color[TRAIL_LENGTH];
		
		for(int i = 0; i < TRAIL_LENGTH; i++) {
			nearnessColors[i] = Color.white;
			for(int j = 0; j < i; j++) {
				nearnessColors[i] = nearnessColors[i].darker();
			}
		}
	}
	
	@Override
	public void render(Graphics g, GameContainer cont) throws SlickException {
		if(lines == null)
			return;
		for(int i = 0; i < lines.length; i++) {
			int closeAmount = 4;
			if(counter < 3) { // todo not right
				int maxMinusAmount = -3 + counter;
				if(i > counter) {
					closeAmount = (i - lines.length) - counter;
					closeAmount = -closeAmount;
				}else if(i < counter) {
					closeAmount = (counter - i);
				}
			}else {
				if(counter >= i)
					closeAmount = counter - i;
			}
			if(closeAmount < TRAIL_LENGTH) {
				g.setColor(new Color(nearnessColors[closeAmount]));
				g.draw(lines[i]);
			}
		}
		g.setColor(Color.white);
		g.draw(lines[counter]);
	}

	@Override
	public void update(GameContainer cont, int delta) throws SlickException {
		if(lines == null) {
			Circle circ = new Circle(Display.getWidth() / 2 - 25, Display.getHeight() / 2 - 25, 30);
			lines = new Line[(circ.getPointCount() - 1) / 2];
			float[] tmp, tmp2;
			for(int i = 0; i < circ.getPointCount() - 1; i += 2) {
				tmp = circ.getPoint(i);
				tmp2 = circ.getPoint(i + 1);
				lines[i / 2] = new Line(tmp[0], tmp[1], tmp2[0], tmp2[1]);
			}
		}
		
		if(Resources.areLoaded()) {
			ScrollHaS.application.setGui(new MainMenu());
		}
		
		nextUpdate -= delta;
		if(nextUpdate <= 0) {
			nextUpdate = LINE_MOVE_TIME;
			counter++;
			if(counter >= lines.length) {
				counter = 0;
			}
		}
	}
	
}
