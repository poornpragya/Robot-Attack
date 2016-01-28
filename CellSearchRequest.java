import java.io.Serializable;
import java.net.InetAddress;

/**
 * Cell Search Request Message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class CellSearchRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	InetAddress leaderHost;
	int leaderPort;
	Point cellToSearch;
	Point locationOfAssignedRobot;
	RequestToken req;

	public CellSearchRequest(InetAddress leaderHost, int leaderPort, Point cellToSearch, Point locationOfAssignedRobot,
			RequestToken req) {
		this.leaderHost = leaderHost;
		this.leaderPort = leaderPort;
		this.cellToSearch = cellToSearch;
		this.locationOfAssignedRobot = locationOfAssignedRobot;
		this.req = req;
	}
}
