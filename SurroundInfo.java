import java.util.List;
import java.io.Serializable;

/**
 * Surround Info Request message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class SurroundInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	Point next_location = null;
	Point location = null;
	int port;
	int[][] map;

	public SurroundInfo() {

	}

	public SurroundInfo(Point l, Point loc, int p, int[][] mp) {

		next_location = l;
		location = loc;
		port = p;
		map = mp;

	}

}