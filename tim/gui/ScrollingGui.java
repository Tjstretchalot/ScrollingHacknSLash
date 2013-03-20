package tim.gui;

import static tim.ScrollHaS.*;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import tim.Gui;
import tim.ScrollHaS;

public class ScrollingGui implements Gui {
	private String[] messages;
	private float topY;
	
	public ScrollingGui(Graphics graphics, String[] messages)
	{
		this.messages = new String[messages.length];
		
		for(int i = 0; i < messages.length; i++)
		{
			this.messages[i] = addDelimiters(graphics, messages[i], 480);
		}
		topY = 480;
	}

	@Override
	public void render(Graphics graphics, GameContainer container)
			throws SlickException {
		
		if(topY > 10f)
		{
			float y = topY;
			for(int i = 0; i < messages.length; i++)
			{
				String[] txt = messages[i].split("\n");
				
				for(String str : txt)
				{
					drawCenteredText(graphics, str, y);
					y += 15f;
				}
				y += 15;
			}
		}else
		{
			float y = 10f;
			
			for(String msg : messages)
			{
				String[] txt = msg.split("\n");
				for(String str : txt)
				{
					drawCenteredText(graphics, str, y);
					y += 15f;
				}
				y += 15;
			}
			
			y = 240f > y + 50f ? 240f : y + 50f;
			
			boolean returnToMainMenu = drawCenteredButton(graphics, "Return to main menu", y).beingPressed();
			
			if(returnToMainMenu)
			{
				ScrollHaS.application.setGui(new MainMenu());
			}
		}
	}

	@Override
	public void update(GameContainer cont, int delta) {
		if(topY > 10f)
			topY -= delta / 50f;
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			topY = 10f;
	}
}
