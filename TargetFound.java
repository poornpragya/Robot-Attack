import java.io.Serializable;

/**
 * Target Found message object
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class TargetFound implements Serializable {
	Point target;
	int map[][];

	public TargetFound(int map[][], Point target) {
		this.map = map;
		this.target = target;
	}

}
