package tim;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.SlickException;

import tim.resource.QueuedSound;
import tim.resource.Resource;
import tim.resource.Resources;

/**
 * Handles it's own resources for improved memory management
 * @author Timothy
 *
 */
public class MusicHandler {
	private static class SoundData {
		public SoundData(Sound s, int i) {
			snd = s;
			priority = i;
		}
		
		Sound snd;
		int priority;
	}

	public static final String[][] RESOURCE_NAMES = new String[][] {
		{ "Lay the Breadcrumbs", "Crooked Warden - The Paths We Create EP - 01 Lay the Breadcrumbs" },
		{ "There's Always a Catch", "Crooked Warden - The Paths We Create EP - 02 There's Always A Catch" },
		{ "Splash", "Crooked Warden - The Paths We Create EP - 03 Splash" },
		{ "Listen Through the Breeze", "Crooked Warden - The Paths We Create EP - 04 Listen Through the Breeze" },
		{ "Try Something New", "Crooked Warden - The Paths We Create EP - 05 Try Something New" },
		{ "Echo Back", "Crooked Warden - The Paths We Create EP - 06 Echo Back" },
		{ "The Belvedere Dawning", "Crooked Warden - The Paths We Create EP - 07 The Belvedere Dawning" },
		{ "Erase the Evidence", "Crooked Warden - The Paths We Create EP - 08 Erase the Evidence" },
		{ "Summer Time Dalliance", "Crooked Warden - The Paths We Create EP - 09 Summer Time Dalliance" }
	};
	
	private static Sound current;
	private static Sound next;
	private static volatile boolean currentBeingPlayed;
	private static volatile boolean nextBeingLoaded;
	
	private static List<SoundData> playingSounds;
	private static List<SoundData> quieted;

	private static boolean muted;
	private static long lastSongChange;
	
	public synchronized static void backgroundMusicTick() throws SlickException {
		if(playingSounds == null)
			playingSounds = new ArrayList<>();
			if(quieted == null)
				quieted = new ArrayList<>();
		if(shouldBeMuted()) {
			makeSureImMuted();
			return;
		}
		if(current == null || ((!current.playing()) && (ScrollHaS.getTime() - lastSongChange) > 1000)) {
			currentBeingPlayed = false;
			
			if(ScrollHaS.getTime() - lastSongChange < 2000) {
				// it's definitely corrupted
				System.err.println("Current song is extremely likely to be corrupted");
				System.exit(1);
			}
		}
		if(!currentBeingPlayed) {
			int newId = ScrollHaS.getRND().nextInt(RESOURCE_NAMES.length);
			if(!nextBeingLoaded) {
				if(next == null) {
					System.out.println("Loading next song");
					loadNextInBackground(1);
				}else {
					currentBeingPlayed = true;
					current = next;
					next = null;
					loadNextInBackground(newId);
					System.out.println("Playing a song and loading next one; " + current);
					current.play(true);
					lastSongChange = ScrollHaS.getTime();
				}
			}
		}
		
		List<SoundData> toRem = new ArrayList<>();
		for(SoundData sd : playingSounds) {
			if(!sd.snd.playing()) {
				toRem.add(sd);
			}
		}
		int tmp = toRem.size();
		playingSounds.removeAll(toRem);
		quieted.removeAll(toRem);
		if(tmp > 0) {
			unquietSounds();
		}
	}
	
	private static Sound loadNow(int ind) throws SlickException {
		System.out.println("Loading " + RESOURCE_NAMES[ind][1] + ".ogg");
		QueuedSound qs = new QueuedSound(Resource.SOUND, RESOURCE_NAMES[ind][1], RESOURCE_NAMES[ind][1] + ".ogg");
		return qs.getResource();
	}

	private static void loadNextInBackground(final int id) {
		nextBeingLoaded = true;
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					next = loadNow(id);
				} catch (SlickException e) {
					e.printStackTrace();
				}
				nextBeingLoaded = false;
			}
			
		}).start();
	}

	private static void makeSureImMuted() {
		for(SoundData sd : playingSounds) { 
			sd.snd.stop();
		}
		playingSounds.clear();
		quieted.clear();
	}

	private static boolean shouldBeMuted() {
		if(ScrollHaS.getTime() - ScrollHaS.linkLastPressed < 100)
			return muted;
		if(Keyboard.isKeyDown(Keyboard.KEY_M)) {
			ScrollHaS.linkLastPressed = ScrollHaS.getTime();
			muted = !muted;
		}
		return muted;
	}

	private static void unquietSounds() {
		int temp = getHighestSoundPriority();
		List<SoundData> toRem = new ArrayList<>();
		for(SoundData sd : quieted) {
			if(sd.priority >= temp) {
				toRem.add(sd);
				sd.snd.playNewVolume(1.0f);
			}
		}
		quieted.removeAll(toRem);
	}
	
	private static int getHighestSoundPriority() {
		int max = -1;
		for(SoundData sd : playingSounds) {
			if(sd.priority > max)
				max = sd.priority;
		}
		return max;
	}

	public static void playSound(Sound snd, int priority) {
		if(snd == null) {
			System.err.println("Null sound called in music handler...?");
			return;
		}
		if(muted)
			return;
		for(SoundData s : playingSounds) {
			if(s.priority < priority) {
				float vol = 1.0f - (priority * 0.2f);
				s.snd.playNewVolume(vol);
				quieted.add(s);
			}
		}
		snd.play(false);
		playingSounds.add(new SoundData(snd, priority));
	}

	public static void playSound(String string, int priority) {
		playSound(Resources.getSound(string), priority);
	}
}
