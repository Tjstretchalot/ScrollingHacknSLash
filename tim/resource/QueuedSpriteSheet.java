package tim.resource;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class QueuedSpriteSheet extends QueuedResource<Image[]> {

	public QueuedSpriteSheet(int cl, String nm, int additional, String additionalS) {
		super(cl, nm, additional);
		super.additionalS = additionalS;
	}

	public QueuedSpriteSheet(Resource resource) {
		this(resource.type, resource.name, resource.additional, resource.additionalS);
	}

	@Override
	protected Image[] get() throws SlickException {
		Image tmp = new Image(resourceLocation.getAbsolutePath());
		
		SpriteSheet sheet = new SpriteSheet(tmp, 32, 32);
		Image[] res = new Image[sheet.getWidth() / 32];
		for(int i = 0; i < sheet.getWidth() / 32; i++) {
			res[i] = sheet.getSubImage(i, 0);
		}
		return res;
	}

	public synchronized Animation getAsAnimation() {
		if(!loaded)
			return null;
		
		Animation anim = new Animation();
		for(Image img : resource) {
			anim.addFrame(img, additional == -1 ? 34 : additional);
		}
		return anim;
	}

}
