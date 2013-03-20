package tim.resource;

public class Resource {
	public static final int SOUND = 0;
	public static final int IMAGE = 1;
	public static final int SHEET = 2;
	
	public int type;
	public String name;
	public int additional;
	public String additionalS;
	
	public Resource(int cl, String nm) {
		this(cl, nm, -1);
	}

	public Resource(int cl, String nm, int i) {
		type = cl;
		name = nm;
		additional = i;
	}
	
	public Resource(int cl, String nm, String add) {
		this(cl, nm);
		additionalS = add;
	}
	
	protected String getFileName() {
		return additionalS == null ? name + preferredEnding() : additionalS;
	}
	
	/**
	 * Returns based on the type this class has
	 * @return the preferred ending for this type
	 */
	protected String preferredEnding() {
		switch(type) {
		case SOUND:
			return ".wav";
		case IMAGE:
			return ".png";
		case SHEET:
			return ".png";
		default:
			return ".unknown";
		}
	}

	public QueuedResource createAsQueued() {
		switch(type) {
		case SOUND:
			return new QueuedSound(this);
		case IMAGE:
			return new QueuedImage(this);
		case SHEET:
			return new QueuedSpriteSheet(this);
		}
		return null;
	}
}
