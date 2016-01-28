import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Request Token message
 * 
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class RequestToken implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int map[][];
	int currentAccessId;
	int N;
	int zoneReachedCounter;
	Map<Point, List<Point>> zones;
	List<Integer> faultyId;

	public RequestToken(int map[][], int currentAccessId, int N, Map<Point, List<Point>> zones,
			List<Integer> faultyBotIds) {
		this.map = map;
		this.currentAccessId = currentAccessId;
		this.N = N;
		zoneReachedCounter = 0;
		this.zones = zones;
		this.faultyId = faultyBotIds;

	}

	public void incrementCurrentAccessId() {
		currentAccessId = (currentAccessId + 1) % N;
		while (true) {
			if (faultyId.contains(currentAccessId))
				currentAccessId = (currentAccessId + 1) % N;
			else
				break;
		}
	}

	public void incrementZoneReachedCounter() {
		zoneReachedCounter++;
	}

	public boolean hasEveryOneReachedZone() {
		return zoneReachedCounter == N ? true : false;
	}

}
