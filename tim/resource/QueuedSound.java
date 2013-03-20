package tim.resource;

import org.newdawn.slick.SlickException;

import tim.Sound;

public class QueuedSound extends QueuedResource<Sound> {

	public QueuedSound(int cl, String nm, int additional, String additionalS) {
		super(cl, nm, additional);
		super.additionalS = additionalS;
	}

	public QueuedSound(Resource resource) {
		this(resource.type, resource.name, resource.additional, resource.additionalS);
	}

	public QueuedSound(int sound, String string, String string2) {
		this(sound, string, -1, string2);
	}

	@Override
	protected Sound get() throws SlickException {
		return new Sound(resourceLocation.getAbsolutePath());
	}

}
