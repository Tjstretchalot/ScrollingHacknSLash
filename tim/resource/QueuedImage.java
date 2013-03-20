package tim.resource;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class QueuedImage extends QueuedResource<Image> {

	public QueuedImage(int cl, String nm, int additional, String additionalS) {
		super(cl, nm, additional);
		super.additionalS = additionalS;
	}

	public QueuedImage(Resource resource) {
		this(resource.type, resource.name, resource.additional, resource.additionalS);
	}

	@Override
	protected Image get() throws SlickException {
		return new Image(resourceLocation.getAbsolutePath());
	}

}
