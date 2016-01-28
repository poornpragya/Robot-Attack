import java.io.Serializable;

/**
 * Point class represents x,y coordinate on 2d plane
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int x;
	int y;

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Object p) {
		Point temp = (Point) p;
		return ((temp.x == this.x) && (temp.y == this.y)) ? true : false;
	}

	public int hashCode() {
		int h = 1;
		h += new Integer(this.x).hashCode() * 31;
		h += new Integer(this.y).hashCode() * 31;
		return h;
	}

	public double distance(Point p2) {
		return Math.sqrt(Math.hypot((p2.x - this.x), (p2.y - this.y)));
	}

	public void copy(Point p) {
		this.x = p.x;
		this.y = p.y;
	}

	public String toString() {
		return "(" + x + "," + y + ")";
	}
}