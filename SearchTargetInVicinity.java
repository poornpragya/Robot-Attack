import java.io.Serializable;
import java.net.InetAddress;

/**
 * SearchTargetInVicinity message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class SearchTargetInVicinity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	InetAddress leaderHost;
	int leaderPort;
	Point robotLocation;
	RequestToken req;

	public SearchTargetInVicinity(InetAddress leaderHost, int leaderPort, Point robotLocation, RequestToken req) {
		this.leaderHost = leaderHost;
		this.leaderPort = leaderPort;
		this.robotLocation = robotLocation;
		this.req = req;
	}

}
