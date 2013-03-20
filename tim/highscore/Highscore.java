package tim.highscore;

public class Highscore {
	public static final Highscore NO_HIGHSCORE = new Highscore("----", -1, -1);
	private final String playerName;
	private int position;
	private final long score;
	
	public Highscore(String plName, int pos, long score) {
		this.playerName = plName;
		this.score = score;
		this.position = pos;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public long getScore() {
		return score;
	}
	
	public void setPosition(int pos) {
		position = pos;
	}
	
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		return "#" + position + ": " + playerName + " with " + score;
	}
}
