import java.util.ArrayList;
import java.util.List;
/**
 * This class computes zones
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class Zone {

	Point topLeft;
	Point bottomRight;

	public Zone(Point topLeft, Point bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	public static List<Zone> zones(int gridX, int gridY) {
		int x = gridX;
		int y = gridY;
		ArrayList<Zone> temp = new ArrayList<Zone>();
		temp.add(new Zone(new Point(0, 0), new Point(x / 2 - 1, y / 2 - 1)));
		temp.add(new Zone(new Point(0, y / 2), new Point(x / 2 - 1, y - 1)));
		temp.add(new Zone(new Point(x / 2, 0), new Point(x - 1, y / 2 - 1)));
		temp.add(new Zone(new Point(x / 2, y / 2), new Point(x - 1, y - 1)));
		return temp;
	}

	public String toString() {
		return "Zone: " + topLeft + " " + bottomRight;
	}

}
