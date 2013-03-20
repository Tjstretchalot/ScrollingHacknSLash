package tim;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;

public class PressChecker {
	private Rectangle2D.Float mRect;

	public PressChecker(float x, float y, float wid, float height) {
		this(new Rectangle2D.Float(x, y, wid, height));
	}
	
	public PressChecker(Rectangle2D.Float rect) {
		this.mRect = rect;
	}

	/**
	 * Creates a press checker on centered text that only worries about the text 
	 * in 'additional'. Assumes the text is centered
	 * @param txt the text
	 * @param additional the additional characters to check
	 * @param y the y
	 */
	public PressChecker(GameContainer cont, String txt, String additional, float y) {
		String newStr = txt + additional;
		boolean spaceMagic = false;
		if(txt.endsWith(" ")) {
			spaceMagic = true;
			txt = txt.substring(0, txt.length() - 2);
		}
		// get the x if it was both and add the length of txt
		float txtLen = cont.getDefaultFont().getWidth(txt);
		float addLen = cont.getDefaultFont().getWidth(additional);
		float x = (Display.getWidth() / 2) - (cont.getDefaultFont().getWidth(newStr) / 2) + txtLen;
		if(spaceMagic) {
			x += 5;
		}
		
		this.mRect = new Rectangle2D.Float(x, y, addLen, cont.getDefaultFont().getHeight(additional));
	}
	
	

	public boolean beingPressed() {
		if(ScrollHaS.getTime() - ScrollHaS.linkLastPressed < 100)
			return false;
		if(Mouse.isButtonDown(0)) {
			if(beingHovered()) {
				ScrollHaS.linkLastPressed = ScrollHaS.getTime();
				return true;
			}
		}
		return false;
	}
	
	public boolean beingHovered() {
		return mRect.contains(Mouse.getX(), Display.getHeight() - Mouse.getY());
	}

	public float getX() {
		return mRect.x;
	}
}
