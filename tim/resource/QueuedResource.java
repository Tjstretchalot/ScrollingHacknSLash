package tim.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.newdawn.slick.SlickException;

import tim.ScrollHaS;

public abstract class QueuedResource<E> extends Resource {
	public static final String DOWNLOAD_URL = "http://umad-barnyard.com/mostrecentextracted/";
	protected volatile boolean preparing;
	protected volatile boolean prepared;
	protected volatile boolean loading;
	protected volatile boolean loaded;
	
	protected File resourceLocation;
	protected volatile boolean killed;
	
	protected Thread myThread;
	protected E resource;
	
	public QueuedResource(int cl, String nm) {
		super(cl, nm);
	}
	
	public QueuedResource(int cl, String nm, int i) {
		super(cl, nm, i);
	}
	
	public QueuedResource(int cl, String nm, String add) {
		super(cl, nm, add);
	}
	
	protected synchronized void prepare() throws IOException, InterruptedException {
		String dir = "res/";
		File tmp = new File(dir);
		if(!tmp.exists()) {
			tmp.mkdirs();
		}
		
		resourceLocation = new File(tmp, getFileName());
		if(!resourceLocation.exists()) {
			downloadResource(resourceLocation);
			if(killed) {
				cleanup();
				return;
			}
		}
		preparing = false;
		prepared = true;
	}

	/**
	 * Must appropriately check for killed during lengthy processes
	 */
	protected synchronized void downloadResource(File file) throws IOException {
		URL url = getResourceURL();
		System.out.println("Downloading from " + url.getPath());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestProperty("User-Header", "tim.resource.QueuedResource");
		conn.connect();
		
		if(killed) {
			conn.disconnect();
			return;
		}
		long length = conn.getContentLengthLong();
		System.out.println("Downloading file to " + file.getAbsolutePath() + ", expected length: " + length);
		InputStream inStream = conn.getInputStream();
		OutputStream outStream = new FileOutputStream(file);
		int i;
		long counter = 0;
		while((i = inStream.read()) != -1 && !killed) {
			if(Resources.nowDead)  {
				System.err.println("Dead while reading from a stream");
				try {
					cleanup();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			outStream.write(i);
			counter++;
		}
		inStream.close();
		outStream.close();
		
		System.out.println("Finished downloading " + file.getAbsolutePath());
		inStream = null;
		outStream = null;
		System.gc(); // Bug fix
		
		if(counter != length) {
			System.err.println("That's odd.. file download size did not match the content-length. Cleaning up and killing everything");
			try {
				cleanup();
				System.exit(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if(killed) {
			System.err.println("Killed while downloading; attempting to delete the file");
			inStream = null;
			outStream = null;
			System.gc();
			if(file.delete()) {
				System.err.println("Succesfully deleted the file");
			}else {
				System.err.println("Could not delete the file");
			}
			return;
		}
	}
	
	public synchronized void prepareInBackground() {
		if(preparing) {
			throw new RuntimeException("Already preparing bro..");
		}
		preparing = true;
		myThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					if(killed)
						return;
					prepare();
					if(killed)
						return;
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					try {
						cleanup();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			
		});
		myThread.start();
	}
	
	public E getResource() throws SlickException {
		if(resource == null) {
			if(!prepared()) {
				preparing = true;
				try {
					prepare();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					try {
						cleanup();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					return null;
				}
				preparing = false;
				prepared = true;
			}
			loading = true;
			resource = get();
			loading = false;
			loaded = true;
		}
		return resource;
	}
	
	protected abstract E get() throws SlickException;
	
	/**
	 * This destroys all local information, including the file, on this resource.
	 * Should only be called if interrupted in the middle of something that might
	 * cause corruption
	 * @throws InterruptedException if this is interrupted while attempting to cleanup
	 */
	public void cleanup() throws InterruptedException {
		kill();
		preparing = false;
		prepared = false;
		loading = false;
		loaded = false;
		if(resourceLocation != null) {
			while(resourceLocation.exists()) {
				System.err.println("Forced to cleanup but resource still found. Deleting.");
				resourceLocation.delete();
				System.gc();
				Thread.sleep(50);
			}
		}
	}
	
	protected URL getResourceURL() throws MalformedURLException {
		return new URL(DOWNLOAD_URL + "res/" + getFileName().replace(" ", "%20"));
	}

	public synchronized boolean preparing() {
		return preparing;
	}
	
	public synchronized boolean prepared() {
		return prepared;
	}
	
	public synchronized boolean loading() {
		return loading;
	}
	
	public synchronized boolean loaded() {
		return loaded;
	}
	
	/**
	 * Kills this resource, no matter what it is doing it will release all
	 * locks on files
	 */
	public synchronized void kill() {
		killed = true;
	}
}
