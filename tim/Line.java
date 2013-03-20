package tim;

import java.awt.geom.Rectangle2D.Float;
import java.util.Arrays;
import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import tim.gui.GameGui;

public class Line implements Collidable {
	private Floor[] floors;
	private int[] holePoses;
	public float y;
	
	public Line(int len, float y, int... holePos) {
		floors = new Floor[len];
		holePoses = holePos;
		for(int i = 0; i < len; i++) {
			boolean b = contains(holePos, i, len);
			if(!b) 
				floors[i] = new Floor(Floor.WIDTH * i, y, getDir(i, holePos, len));
		}
		this.y = y;
	}
	
	private byte getDir(int i, int[] holePos, int m) {
		boolean left = !contains(holePos, i - 1, m);
		boolean right = !contains(holePos, i + 1, m);
		
		if(left && right) {
			return 0;
		}else if(!left && right) {
			return 1;
		}else if(left && !right) {
			return 2;
		}
		return 3;
	}

	@Override
	public boolean collidesWith(Float rect) {
		for(Floor f : floors) {
			if(f == null)
				continue;
			f.setY(y);
			if(f.collidesWith(rect))
				return true;
		}
		return false;
	}
	
	public void render(Graphics g, GameGui gGui, float y) {
		Player pl = gGui.getPlayer();
		int red = 0;
		int green = 50;
		int blue = 0;
		
		if(pl.getHealth() < pl.getMaxHealth()) {
			float perc = pl.getHealth() / pl.getMaxHealth();
			
			if(perc > 0.75) {
				green -= (1 - perc) * 100;
			}else if(perc > 0.25) {
				red = green = 200 - Math.round(perc * 100);
				blue = Math.round((255f / red) * 100);
			}else {
				red = 100 + Math.round(perc * 155);
			}
		}
		Color col = new Color(red, green, blue, 100);
		for(Floor fl : floors) {
			if(fl == null)
				continue;
			fl.render(g, col, y);
		}
	}
	
	private static boolean contains(int[] arr, int n, int m) {
		if(n < 0 || n >= m)
			return false;
		for(int i : arr) {
			if(i == n)
				return true;
		}
		return false;
	}

	public static Line makeRandomLine(Line linePrior, boolean b) {
		int numFloors = getNumFloors();
		if(b) 
			return new Line(numFloors, -1, new int[] { 0 });
		int maxHoles = getMaxHoles();
		int numHoles = ScrollHaS.getRND().nextInt(maxHoles) + 1;
		int[] holePos = new int[numHoles];
		for(int i = 0; i < holePos.length; i++)
			holePos[i] = -1;
		Random gen = ScrollHaS.getRND();
		int tmp;
		for(int i = 0; i < numHoles; i++) {
			if(linePrior.holePoses.length > i && gen.nextFloat() < 0.75) {
				tmp = gen.nextInt(5) * (gen.nextBoolean() ? -1 : 1);
				while(linePrior.holePoses[i] + tmp < 0 || linePrior.holePoses[i] + tmp >= numFloors) {
					tmp = gen.nextInt(5) * (gen.nextBoolean() ? -1 : 1);
				}
				holePos[i] = linePrior.holePoses[i] + tmp;
			}else {
				do {
					tmp = gen.nextInt(numFloors);
				}while(contains(holePos, tmp, numHoles));
				holePos[i] = tmp;
			}
		}
		return new Line(numFloors, Collidable.WALL.getMaxY() + 8, holePos);
	}

	private static int getNumFloors() {
		float floorCount = Collidable.WALL.getMaxX() / 32f;
		if(floorCount != ((int) floorCount)) {
			return (int) floorCount + 1;
		}
		return (int) floorCount;
	}

	private static int getMaxHoles() {
		return Math.round(Collidable.WALL.getMaxX() / 320);
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public float getMaxY() {
		return y + 8;
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getMaxX() {
		return floors.length * 32;
	}

	public int getWidth() {
		return floors.length;
	}

	public boolean hasGroundAt(int loc) {
		return floors[loc] != null;
	}
}
