package tim;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Ellipse;

import tim.gui.GameGui;
import tim.gui.LoadingScreenGui;
import tim.gui.MainMenu;
import tim.resource.Resources;

/**
 * Entry point for a scrolling platformer. The screen appears to be
 * moving down, and you lose if you touch the top. However, another 
 * layer is you have to kill enemies to get speed boosts. Score is a
 * combination of enemies killed and distance traveled
 * 
 * @author Timothy
 */
public class ScrollHaS extends BasicGame {
	/**
	 * The website regular expression used for determining if a noncommented string is
	 * a website.  Used internally for making websites clickable
	 */
	public static final String WEBSITE_REGEX = "^(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\ "
			+ "-\\.\\?\\,\\'\\/\\+&amp;%\\$#_]*)?$";
	
	/**
	 * The email regular expression used. Not compliant to pretty much anything
	 */
	public static final String EMAIL_REGEX = "^[_a-z0-9-]+(\\.[_a-z0-9-]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$";
	
	/**
	 * The random generator used across the application
	 */
	private static Random gen;

	/**
	 * The last time a 'link' or button was pressed. Could be anything
	 * that is based off Mouse.isPressed method to limit double/triple
	 * clicks when the user meant to use just one
	 */
	public static long linkLastPressed;
	
	/**
	 * The single instance of ScrollHaS
	 */
	public static ScrollHaS application;
	
	/**
	 * The current gui that is being drawn/updated
	 */
	private Gui mGui;
	
	/**
	 * Creates an instance of the scrolling platformer
	 * @param title the name the window should have
	 */
	public ScrollHaS(String title) {
		super(title);
	}
	
	/**
	 * Entry point. Creates the scroll hack and slash and prepares
	 * the initial GUI.
	 * @param args not used
	 */
	public static void main(String[] args) {
		try {
        	application = new ScrollHaS("Scrolling HacknSlash");
        	application.mGui = new LoadingScreenGui();
            AppGameContainer app = new AppGameContainer(application);
            app.setTargetFrameRate(60);
            app.setShowFPS(false);
            app.start();
        } catch (SlickException e) {
            e.printStackTrace();
        }
	}

	/**
	 * Calls the current GUI's render method
	 * @param container the game container 
	 * @param graphics the graphics
	 */
	@Override
	public void render(GameContainer container, Graphics graphics) throws SlickException {
		graphics.setColor(Color.white);
		mGui.render(graphics, container);
		graphics.setColor(Color.blue);
		drawText(graphics, "Coins: " + Resources.getCoins(), 5, 10);
	}

	/**
	 * Initializes the resources needed, including all the graphics
	 * and sound
	 * @param container the game container for getting font size
	 */
	@Override
	public void init(final GameContainer container) throws SlickException {
		Resources.init(container);
	}

	/**
	 * Updates the gui in delta-time
	 * @param container the game ocntainer
	 * @param delta the time, in milliseconds, since the last update.
	 */
	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		if(Resources.areLoaded()) {
			MusicHandler.backgroundMusicTick();
			if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && Display.isFullscreen()) {
				AppGameContainer app = (AppGameContainer) container;
				app.setDisplayMode(Display.getWidth(), Display.getHeight(), false);
			}
		}else {
			Resources.doInitTick(container);
		}
		mGui.update(container, delta);
	}

	/**
	 * Draws the specified text in the center of the screen 
	 * at the specified y coordinate.  If the text contains
	 * any websites or emails, they will be made clickable.
	 * @param graphics the graphics to use for drawing
	 * @param txt the text
	 * @param y the y coordinate
	 * @return the location where the text is
	 */
	public static Rectangle2D.Float drawCenteredText(Graphics graphics, String txt, float y) {
		float leftX = 0f;
		float rightX = Display.getWidth();
		return drawCenteredText(graphics, txt, leftX, rightX, y);
	}
	
	/**
	 * Draws the specified text in the center of the screen and 
	 * returns if it is being clicked. Same as drawCenteredText(graphics, txt, 0, Display.getWidth(), y)
	 * @param graphics the graphics
	 * @param txt the text
	 * @param y the y coordinate
	 * @return if it is being pressed
	 */
	public static PressChecker drawCenteredHText(Graphics graphics, String txt, float y)
	{
		return drawCenteredHText(graphics, txt, 0, Display.getWidth(), y);
	}
	
	/**
	 * Draws the specified text in the center of the screen and 
	 * returns if it is being clicked. Same as drawCenteredText(graphics, txt, 0, Display.getWidth(), y)
	 * @param graphics the graphics
	 * @param txt the text
	 * @param y the y coordinate
	 * @return if it is being pressed
	 */
	public static PressChecker drawCenteredButton(Graphics graphics, String txt,
			float y) {
		float leftX = 0f;
		float rightX = Display.getWidth();
		return drawCenteredButton(graphics, txt, leftX, rightX, y);
	}
	
	/**
	 * Draws a button centered in the specified area
	 * @param graphics the graphics
	 * @param txt the text to draw
	 * @param leftX the left x of the area
	 * @param rightX the right x of the area
	 * @param y the y-location
	 * @return if the button is being pressed
	 */
	public static PressChecker drawCenteredButton(Graphics graphics, String txt,
			float leftX, float rightX, float y) {
		float width = graphics.getFont().getWidth(txt);
		float x = leftX + ((rightX - leftX) / 2 - width / 2);

		return drawButton(graphics, txt, x, y);
	}

	/**
	 * Draws a centered text, except rather than using 0 and 
	 * the screen width it uses set values.
	 * @param graphics  the graphics
	 * @param txt the text
	 * @param leftX the left x
	 * @param rightX the right x
	 * @param y the y coordinate
	 * @return if it is being clicked
	 */
	public static PressChecker drawCenteredHText(Graphics graphics, String txt, float leftX, float rightX, float y)
	{
		float width = graphics.getFont().getWidth(txt);
		float x = leftX + ((rightX - leftX) / 2 - width / 2);
		return drawHighlightableText(graphics, txt, x, y);
	}
	
	/**
	 * Draws the specified text at the specified coordinate.  If
	 * the text contains any websites or emails they will be highlighted
	 * and upon being pressed the appropriate website or mailto address
	 * will be opened.
	 * 
	 * @param graphics the graphics
	 * @param txt the text
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @return the location where the text is being drawn
	 */
	public static Rectangle2D.Float drawText(Graphics graphics, String txt, final float x, final float y)
	{
		String[] split = txt.split(" ");
		float height = graphics.getFont().getHeight(txt);
		float drawingX = x;
		for(String str : split)
		{
			if(!isWebsite(str) && !isEmail(str))
			{
				graphics.drawString(str, drawingX, y);
				drawingX += graphics.getFont().getWidth(str) + 5;
			}else
			{
				if(isWebsite(str))
				{
					boolean goToWebsite = drawHighlightableText(graphics, str, drawingX, y).beingPressed();

					if(goToWebsite)
					{
						System.out.println("here");
						Sys.openURL(str);
						linkLastPressed = getTime();
					}
				}else if(isEmail(str))
				{
					boolean goToEmail = drawHighlightableText(graphics, str + " ", drawingX, y).beingPressed();
					
					if(goToEmail)
					{
						Sys.openURL("mailto:" + str);
						linkLastPressed = getTime();
					}
				}
				drawingX += graphics.getFont().getWidth(str) + 5;
				
			}
		}
		
		Rectangle2D.Float res = new Rectangle2D.Float();
		res.x = x;
		res.y = y;
		res.width = drawingX;
		res.height = height;
		
		return res;
	}
	
	private static boolean isEmail(String str) {
		return str.matches(EMAIL_REGEX);
	}

	/**
	 * Same as the global one but does not highlight websites if told not to
	 * @param graphics the graphics
	 * @param txt the text to write
	 * @param x xcoord
	 * @param y ycoord 
	 * @param b highlight website
	 * @return the rectangle the text is located in
	 */
	public static Rectangle2D.Float drawText(Graphics graphics, String txt, float x, float y, boolean b)
	{
		if(b)
			return drawText(graphics, txt, x, y);
		
		float width = graphics.getFont().getWidth(txt);
		float height = graphics.getFont().getHeight(txt);
		
		graphics.drawString(txt, x, y);
		
		Rectangle2D.Float res = new Rectangle2D.Float();
		res.x = x;
		res.y = y;
		res.width = width;
		res.height = height;
		
		return res;
	}
	
	/**
	 * Checks if the string is a website URL
	 * @param txt the text
	 * @return if it is a url
	 */
	private static boolean isWebsite(String txt) {
		return txt.matches(WEBSITE_REGEX);
	}

	/**
	 * Draws a text that gets brighter if the mouse is hovering on it
	 * @param graphics the graphics
	 * @param text the text
	 * @param x x location
	 * @param y y location
	 * @return if the text is being clicked
	 */
	public static PressChecker drawHighlightableText(Graphics graphics, String text, float x, float y)
	{
		Rectangle2D.Float rect = drawText(graphics, text, x, y, false);
		float areaAround = 6f;
		rect.x -= areaAround / 2;
		rect.y -= areaAround / 2;
		rect.width += areaAround;
		rect.height += areaAround;
		if(rect.contains(Mouse.getX(), (Display.getHeight() - Mouse.getY())))
		{
			if(Mouse.isButtonDown(0)) 
			{
				graphics.setColor(Color.yellow);
				graphics.drawString(text, rect.x, rect.y);
				graphics.setColor(Color.white);
			}else
				drawText(graphics, text, x, y, false); // Double drawing gives a great effect
		}
		return new PressChecker(rect);
	}
	
	/**
	 * Draws a button
	 * @param graphics the graphics
	 * @param text the text
	 * @param x x location
	 * @param y y location
	 * @return if the button is being pressed
	 */
	public static PressChecker drawButton(Graphics graphics, String text, float x, float y)
	{
		Rectangle2D.Float rect = drawText(graphics, text, x, y, false);
		float areaAround = 6f;
		rect.x -= areaAround / 2;
		rect.y -= areaAround / 2;
		rect.width += areaAround;
		rect.height += areaAround;
		
		float brightenAmount = 0;
		
		if(rect.contains(Mouse.getX(), (Display.getHeight() - Mouse.getY()))) {
			if(Mouse.isButtonDown(0)) 
			{
				brightenAmount = -0.1f;
			}else
				brightenAmount = -0.05f;
		}else {
			brightenAmount = 0.05f;
		}
		Color rectColor = Color.gray;
		if(brightenAmount > 0)
			rectColor = rectColor.brighter(brightenAmount);
		else if(brightenAmount < 0) 
			rectColor = rectColor.darker(-brightenAmount);
		
		Color flareColor = rectColor.brighter(0.05f);
		graphics.setColor(rectColor);
		graphics.fillRoundRect(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2, (int) Math.round(areaAround - 1));
		graphics.setColor(flareColor);
		drawFlare(rect, graphics, 2);
		graphics.setColor(Color.white);
		
		drawText(graphics, text, x, y, false);
		return new PressChecker(rect);
	}
	
	/**
	 * Creates a flare effect behind a shape by making a 
	 * white oval on the top and right third of the shape, about 1/12 the
	 * size of the shape
	 * @param shape the shape
	 * @param i how many levels of flare
	 */
	private static void drawFlare(Shape shape, Graphics graphics, int i) {
		if(i <= 0)
			return;
		int x = shape.getBounds().width - (shape.getBounds().width * 1 / 12);
		int y = (shape.getBounds().height * 1 / 12);
		
		Rectangle2D.Float curr = new Rectangle2D.Float(x + shape.getBounds().x, y + shape.getBounds().y, shape.getBounds().width / 12, shape.getBounds().height / 12);
		graphics.fill(new Ellipse(curr.x, curr.y, curr.width, curr.height));
		drawFlare(shape, graphics, i - 1);
	}
	
	/**
	 * Returns the time in milliseconds
	 * @return the time in milliseconds
	 */
	public static long getTime()
	{
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	/**
	 * Adds delimiters to a string based off of the specified graphics
	 * 
	 * @param graphics the graphics
	 * @param string the string
	 * @param i maximum width
	 * @return the result
	 */
	public static String addDelimiters(Graphics graphics, String string, int i) {
		if(graphics.getFont().getWidth(string) <= i || string.split(" ").length == 1)
			return string;
		
//		System.out.println("Width of '" + string + "': " + graphics.getFont().getWidth(string));
		
		String[] splt = string.split(" ");
		String newString = "";
		
		int counter = -1;
		String temp = null;
		while(graphics.getFont().getWidth(newString) < i && (counter + 1) < splt.length)
		{
			counter++;
			temp = newString;
			newString += splt[counter] + " ";
//			System.out.println("String is now '" + newString + "', length: " + graphics.getFont().getWidth(newString));
		}
		newString = temp;
//		System.out.println("End result is: " + newString);
		
		return newString + "\n" + addDelimiters(graphics, string.substring(newString.length()), i);
	}

	/**
	 * Returns the random number generator used
	 * @return the random number generator
	 */
	public static Random getRND() {
		if(gen == null)
			gen = new Random();
		return gen;
	}

	/**
	 * Draws centered text where leftX is the left 
	 * of the center and right x is the right of the screen
	 * @param graphics the graphics
	 * @param txt the string
	 * @param leftX left x
	 * @param rightX right x
	 */
	public static Rectangle2D.Float drawCenteredText(Graphics graphics, String txt,
		float leftX, float rightX, float y) {
		float width = graphics.getFont().getWidth(txt);
		float x = leftX + ((rightX - leftX) / 2 - width / 2);
		
		return drawText(graphics, txt, x, y);
	}

	public void setGui(Gui gui) {
		mGui = gui;
	}

	/**
	 * Draws an image, centered based on the images width
	 * @param g the graphics to draw with
	 * @param image the image to draw
	 * @param y the y-location to draw at
	 * @param opacity the opacity
	 */
	public static void drawCenteredImage(Graphics graphics, Image image, float y, float opacity) {
		float leftX = 0f;
		float rightX = Display.getWidth();
		float width = image.getWidth();
		float x = leftX + ((rightX - leftX) / 2 - width / 2);
		image.setAlpha(opacity);
		graphics.drawImage(image, x, y);
	}
}
