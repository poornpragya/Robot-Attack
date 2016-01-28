import java.io.Serializable;
import java.util.List;

/**
 * Start zone search request message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class StartZoneSearchRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int map[][];
	int currentAccessId;
	int N;
	List<Integer> faultyId;;

	public StartZoneSearchRequest(int map[][], int currentAssignedId, int N, List<Integer> faultyBotIds) {
		this.map = map;
		this.currentAccessId = currentAssignedId;
		this.N = N;
		this.faultyId=faultyBotIds;
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
}
