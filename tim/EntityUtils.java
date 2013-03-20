package tim;

import java.awt.geom.Rectangle2D.Float;

/**
 * A convienience class holding some methods for 
 * entities
 * @author Timothy
 */
public class EntityUtils {

	public static float distance(Float location, float x, float y) {
		if(location.contains(x, y)) 
			return 0;
		
		float x1 = -1, x2, y1 = -1, y2;
		x1 = location.x;
		y1 = location.y;
		x2 = x;
		y2 = y;
		
		
		if(x >= location.x && x <= location.width + location.x) {
			return (float) Math.sqrt(Math.pow(y2 - y1, 2)); // x's are the same
		}
		
		
		if(y >= location.y && y <= location.height + location.y) {
			return (float) Math.sqrt(Math.pow(x2 - x1, 2)); // y's are the same
		}
		
		if(y > location.y + location.height) {
			y1 = location.y + location.height;
		}
		
		if(x > location.x + location.width) {
			x1 = location.x + location.width;
		}
		
		return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
	}

	public static float roundTo2(float f) {
		int i = Math.round(f * 100);
		return i / 100.0f;
	}

}
