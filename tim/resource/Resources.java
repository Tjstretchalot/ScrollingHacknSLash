package tim.resource;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.JOptionPane;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

import tim.Player;
import tim.PlayerBoosts;
import tim.Sound;

public class Resources {
	
	private static HashMap<String, Image> mImages;
	private static HashMap<String, Animation> mAnims;
	private static HashMap<String, Sound> sfxs;
	
	public static final int ANIM_DELAY = 34;
	public static final int PLAYER_WIDTH = 32;
	public static final int PLAYER_HEIGHT = 32;
	private static final String SAVE_DIR = System.getenv("appdata") + "/scrollinghacknslash/";
	private static final String SAVE_LOC = SAVE_DIR + "data.ini";
	
	private static int coins;
	private static PlayerBoosts playerBoosts;
	protected static boolean wasLocked;
	
	private static boolean loaded;
	private static boolean loading;
	
	private static final Resource[] ALL_RESOURCES = new Resource[] {
		new Resource(Resource.SHEET, "playerstanding"),
		new Resource(Resource.SHEET, "playerleft"),
		new Resource(Resource.SHEET, "playerright"),
		new Resource(Resource.SHEET, "playerfallingleft"),
		new Resource(Resource.SHEET, "playerfallingright"),
		new Resource(Resource.SHEET, "playerfalling"),
		new Resource(Resource.SHEET, "bomb-explosion"),
		new Resource(Resource.SHEET, "coin", 500),
		new Resource(Resource.SHEET, "coin-collect"),
		new Resource(Resource.SHEET, "barrier"),
		new Resource(Resource.SHEET, "enemy1-up"),
		new Resource(Resource.SHEET, "enemy1-left"),
		new Resource(Resource.SHEET, "enemy1-right"),
		new Resource(Resource.SHEET, "enemy1-death"),
		new Resource(Resource.SOUND, "enemy1-hurt", "enemy1-hurt.wav"),
		new Resource(Resource.SOUND, "death", "death.wav"),
		new Resource(Resource.IMAGE, "wall", "wallcent.png"),
		new Resource(Resource.IMAGE, "wallleft", "wallleft.png"),
		new Resource(Resource.IMAGE, "wallright", "wallright.png"),
		new Resource(Resource.IMAGE, "wallboth", "wallboth.png"),
		new Resource(Resource.IMAGE, "bomb", "bomb.png"),
		new Resource(Resource.IMAGE, "gravity", "gravity.png"),
		new Resource(Resource.SOUND, "gravity", "gravity.wav"),
		new Resource(Resource.IMAGE, "runspeed", "runspeed.png"),
		new Resource(Resource.SOUND, "runspeed", "runspeed.wav"),
		new Resource(Resource.IMAGE, "firerate", "firerate.png"),
		new Resource(Resource.SOUND, "firerate", "firerate.wav"),
		new Resource(Resource.IMAGE, "bulletspeed", "bulletspeed.png"),
		new Resource(Resource.SOUND, "bulletspeed", "bulletspeed.wav"),
		new Resource(Resource.IMAGE, "jumpheight", "jumpheight.png"),
		new Resource(Resource.SOUND, "jumpheight", "jumpheight.wav"),
		new Resource(Resource.SOUND, "fire-player", "fire.wav"),
		new Resource(Resource.SOUND, "bomb", "boom.wav"),
		new Resource(Resource.SOUND, "upgrade", "upgrade.wav"),
		new Resource(Resource.SOUND, "upgrade-lost", "upgrade-lost.wav"),
		new Resource(Resource.SOUND, "coin-collect", "coincoll.wav"),
		new Resource(Resource.SOUND, "coin-death", "coindeath.wav"),
		new Resource(Resource.SOUND, "oof", "oof.wav"),
		new Resource(Resource.SOUND, "barrier-kill", "barrierkill.wav"),
		new Resource(Resource.SOUND, "slowdown", "slowdown.wav"),
		new Resource(Resource.SOUND, "slowdown-done", "slowdown-done.wav"),
		new Resource(Resource.SOUND, "extralife", "extralife.wav")
	};
	
	private static Queue<QueuedResource> currentlyLoading;
	public static boolean nowDead;
	
	public static void doInitTick(GameContainer container) throws SlickException {
		if(!loading && !loaded) {
			loading = true;
			System.out.println("Beginning to load resources");
		}
		if(currentlyLoading.isEmpty()) {
			loadPlayerData();
			loading = false;
			loaded = true;
			System.out.println("Done loading resources");
			return;
		}
		
		QueuedResource res = currentlyLoading.peek();
		if(res.prepared) {
			System.out.println(res.name + " is prepared, loading");
			switch(res.type) {
			case Resource.SOUND:
				QueuedSound qs = (QueuedSound) currentlyLoading.poll();
				sfxs.put(qs.name, qs.getResource());
				break;
			case Resource.IMAGE:
				QueuedImage qi = (QueuedImage) currentlyLoading.poll();
				mImages.put(qi.name, qi.getResource());
				break;
			case Resource.SHEET:
				QueuedSpriteSheet qss = (QueuedSpriteSheet) currentlyLoading.poll();
				qss.getResource(); // load 'er up
				mAnims.put(qss.name, qss.getAsAnimation());
				break;
			}
		}else if(!res.preparing) {
			System.out.println("Requesting that " + res.name + " loads in the background");
			res.prepareInBackground();
		}
	}
	
	public static void init(GameContainer container) throws SlickException {
		mImages = new HashMap<>();
		mAnims = new HashMap<>();
		sfxs = new HashMap<>();
		currentlyLoading = new ArrayBlockingQueue<>(ALL_RESOURCES.length);
		for(Resource res : ALL_RESOURCES) {
			currentlyLoading.add(res.createAsQueued());
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				nowDead = true;
				if(!wasLocked)
					savePlayerData(false);
				while(currentlyLoading.size() > 0) {
					QueuedResource qr = currentlyLoading.poll();
					try {
						qr.cleanup();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private static void loadPlayerData() {
		Properties stored = new Properties();
		File f = new File(SAVE_LOC);
		if(f.exists()) {
			try(FileReader fr = new FileReader(f)) {
				stored.load(fr);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		if(stored.getProperty("locked") != null) {
			if(stored.getProperty("locked").equals("true")) {
				int ret = JOptionPane.showConfirmDialog(null, "Store location is already in use, force unlock? (Will override other running games COIN value)");
				if(ret != JOptionPane.YES_OPTION) {
					wasLocked = true;
					System.exit(0);
				}
			}
		}
		
		playerBoosts = new PlayerBoosts();
		String cns = stored.getProperty("coins");
		
		if(cns != null) {
			try {
				coins = Integer.valueOf(cns);
				System.out.println("Loaded " + coins + " coins");
			}catch(NumberFormatException e) {
				coins = 0;
			}
		}
		
		for(int i = 0; i < Player.MODIFIER_NAMES.length; i++) {
			loadPlayerData(stored.getProperty(Player.MODIFIER_NAMES[i]), i);
		}
		String extraLife = stored.getProperty("extra-life");
		if(extraLife != null) {
			if(extraLife.equals("true")) {
				playerBoosts.setExtraLife();
			}
		}
		
		String slowItDown = stored.getProperty("slow-it-down");
		if(slowItDown != null) {
			if(slowItDown.equals("true")) {
				playerBoosts.setSlowItDown();
			}
		}
		savePlayerData(true);
	}
	
	private static void loadPlayerData(String str, int ind) {
		try {
			if(str != null) {
				playerBoosts.setBoost(ind, Float.valueOf(str));
				return;
			}
			playerBoosts.setBoost(ind, 1);
		}catch(NumberFormatException e) {
			playerBoosts.setBoost(ind, 1);
		}
	}

	public static void savePlayerData(boolean lock) {
		Properties prop = playerBoosts.getPropertyVersion();
		System.out.println("Saving " + getCoins() + " coins");
		prop.setProperty("coins", getCoins() + "");
		prop.setProperty("extra-life", Boolean.toString(playerBoosts.hasExtraLife()));
		prop.setProperty("slow-it-down", Boolean.toString(playerBoosts.hasSlowItDown()));
		prop.setProperty("locked", Boolean.toString(lock));
		File f = new File(SAVE_LOC);
		if(!f.exists()) {
			new File(SAVE_DIR).mkdirs();
			try {
				f.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		try(FileWriter fw = new FileWriter(new File(SAVE_LOC))) {
			prop.store(fw, "Do not edit CHEATER");
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static Image getImage(String nm) {
		return mImages.get(nm);
	}
	
	public static Animation getAnimation(String nm) {
		if(mAnims.get(nm) == null)
			System.err.println("Odd.. " + nm);
		return mAnims.get(nm).copy();
	}
	
	public static Sound getSound(String nm) {
		return sfxs.get(nm);
	}

	public static PlayerBoosts getPlayerBoosts() {
		return playerBoosts;
	}
	
	public static int getCoins() {
		return coins;
	}
	
	public static void addCoins(int num) {
		coins += num;
	}
	
	public static void removeCoins(int num) {
		coins -= num;
	}

	public static boolean areLoaded() {
		return loaded;
	}

	public static boolean areLoading() {
		return loading;
	}

	
}
