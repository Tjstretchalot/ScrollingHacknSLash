package tim.highscore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Handles the high score acquiring and uploading. The website will
 * drop ridiculous scores
 * @author Timothy
 */
public class HighscoreManager {
	private static final String WEBSITE = "http://umad-barnyard.com/servlet/servlets/Game1Highscores";
	
	public static Highscore[] putHighScore(String playerName, long score) {
		Highscore[] res = new Highscore[10];
		String params = "name=" + playerName + "&score=" + score;
		System.out.println(params);
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(WEBSITE).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Length", Integer.toString(params.getBytes("UTF-8").length));
			conn.setDoOutput(true);
			
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.write(params.getBytes("UTF-8"));
			wr.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String str = br.readLine();
			String[] eachHighscore = str.split(";");
			int pos = 0;
			for(String highscoreStr : eachHighscore) {
				String[] data = highscoreStr.split(", ");
				String nm = data[0];
				int sc = Integer.valueOf(data[1]);
				res[pos] = new Highscore(nm, pos + 1, sc);
				pos++;
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static Highscore[] getHighScores() {
		Highscore[] res = new Highscore[10];
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(WEBSITE).openConnection();
			conn.setRequestMethod("GET");
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String str = br.readLine();
			String[] eachHighscore = str.split(";");
			if(eachHighscore.length == 1)
				return res;
			int pos = 0;
			for(String highscoreStr : eachHighscore) {
				String[] data = highscoreStr.split(", ");
				String nm = data[0];
				int sc = Integer.valueOf(data[1]);
				res[pos] = new Highscore(nm, pos + 1, sc);
				pos++;
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
}
