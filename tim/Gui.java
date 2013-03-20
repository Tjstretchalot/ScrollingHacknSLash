package tim;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public interface Gui {
	public void render(Graphics g, GameContainer cont) throws SlickException;
	
	public void update(GameContainer cont, int delta) throws SlickException;
}
