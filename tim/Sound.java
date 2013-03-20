package tim;

import java.io.InputStream;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.Log;
/*
 * Added playNewSound method
 */

/**
 * A single sound effect loaded from either OGG or XM/MOD file. Sounds are allocated to 
 * channels dynamically - if not channel is available the sound will not play. 
 *
 * @author kevin
 */
public class Sound {
	/** The internal sound effect represent this sound */
	private Audio sound;
	
	/* Beautiful hack */
	private long start;
	
	private boolean music;

	/**
	 * Create a new Sound 
	 * 
	 * @param in The location of the OGG or MOD/XM to load
	 * @param ref The name to associate this stream
	 * @throws SlickException Indicates a failure to load the sound effect
	 */
	public Sound(InputStream in, String ref) throws SlickException {
		SoundStore.get().init();

		try {
			if (ref.toLowerCase().endsWith(".ogg")) {
				sound = SoundStore.get().getOgg(in);
			} else if (ref.toLowerCase().endsWith(".wav")) {
				sound = SoundStore.get().getWAV(in);
			} else if (ref.toLowerCase().endsWith(".aif")) {
				sound = SoundStore.get().getAIF(in);
			} else if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
				sound = SoundStore.get().getMOD(in);
			} else {
				throw new SlickException("Only .xm, .mod, .aif, .wav and .ogg are currently supported.");
			}
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to load sound: "+ref);
		}
	}

	/**
	 * Create a new Sound 
	 * 
	 * @param ref The location of the OGG or MOD/XM to load
	 * @throws SlickException Indicates a failure to load the sound effect
	 */
	public Sound(String ref) throws SlickException {
		SoundStore.get().init();

		try {
			if (ref.toLowerCase().endsWith(".ogg")) {
				sound = SoundStore.get().getOgg(ref);
			} else if (ref.toLowerCase().endsWith(".wav")) {
				sound = SoundStore.get().getWAV(ref);
			} else if (ref.toLowerCase().endsWith(".aif")) {
				sound = SoundStore.get().getAIF(ref);
			} else if (ref.toLowerCase().endsWith(".xm") || ref.toLowerCase().endsWith(".mod")) {
				sound = SoundStore.get().getMOD(ref);
			} else {
				throw new SlickException("Only .xm, .mod, .aif, .wav and .ogg are currently supported.");
			}
		} catch (Exception e) {
			Log.error(e);
			throw new SlickException("Failed to load sound: "+ref);
		}
	}

	/**
	 * Play this sound effect at default volume and pitch
	 */
	public void play(boolean music) {
		play(1.0f, 1.0f, music);
	}

	/**
	 * Play this sound effect at a given volume and pitch
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 * @param music 
	 */
	public void play(float pitch, float volume, boolean music) {
		if(!music)
			sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), false);
		else
			sound.playAsMusic(pitch, volume * SoundStore.get().getSoundVolume(), false);
		assert sound.isPlaying();
		start = System.currentTimeMillis();
		
		this.music = music;
	}
	
	/**
	 * Loop this sound effect at default volume and pitch
	 */
	public void loop() {
		loop(1.0f, 1.0f);
	}

	/**
	 * Loop this sound effect at a given volume and pitch
	 * 
	 * @param pitch The pitch to play the sound effect at
	 * @param volume The volumen to play the sound effect at
	 */
	public void loop(float pitch, float volume) {
		sound.playAsSoundEffect(pitch, volume * SoundStore.get().getSoundVolume(), true);
	}

	/**
	 * Check if the sound is currently playing
	 * 
	 * @return True if the sound is playing
	 */
	public boolean playing() {
		return sound.isPlaying();
	}

	/**
	 * Stop the sound being played
	 */
	public void stop() {
		sound.stop();
	}
	
	/**
	 * Continues playing at the specified volume
	 * @param vol the volume
	 */
	public void playNewVolume(float volume) {
		if(!playing()) {
			play(1f, volume, false);
			return;
		}
		stop();
		float loc = (System.currentTimeMillis() - start) / 1000f;
		if(!music)
			sound.playAsSoundEffect(1.0f, volume * SoundStore.get().getSoundVolume(), false);
		else
			sound.playAsMusic(1.0f, volume * SoundStore.get().getSoundVolume(), false);
		loc = (System.currentTimeMillis() - start) / 1000f;
 		boolean b = sound.setPosition(loc);
	}
	
	@Override
	public String toString() {
		return sound.toString();
	}
}
