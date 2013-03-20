package tim.gui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.Gui;

public class CreditsGui extends ScrollingGui {
	private static final String[] messages = new String[] {
		"Timothy Moore - Code/Programming",
		"Crooked Warden - The Paths We Create EP album",
		"http://www.online-convert.com - Converting wma to wav",
		"http://www.cooltext.com - Cool Text",
		"Slick2D",
		"Amanda Moore - Testing"
	};
	
	public CreditsGui(Graphics graphics) {
		super(graphics, messages);
	}


}
