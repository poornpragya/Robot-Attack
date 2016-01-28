import java.io.Serializable;
import java.net.InetAddress;
import java.util.List;

/**
 * Zone Assign message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class ZoneAssign implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	InetAddress leaderHost;
	int leaderPort;
	Point zoneStartingPoint;
	List<Point> zonePointsToVisit;
	Point toRobot;
	RequestToken req;
	
	
	public ZoneAssign(InetAddress leaderHost, int portToCommunicateWithLeader, Point zoneStartingPoint, List<Point> zonePointsToVisit,
			Point toRobot, RequestToken req) {

		this.leaderHost=leaderHost;
		this.leaderPort = portToCommunicateWithLeader;
		this.zoneStartingPoint = zoneStartingPoint;
		this.zonePointsToVisit = zonePointsToVisit;
		this.toRobot = toRobot;
		this.req=req;
	}

	public ZoneAssign(InetAddress leaderHost, int portToCommunicateWithLeader, Point toRobot) {

		this.leaderHost=leaderHost;
		this.leaderPort = portToCommunicateWithLeader;
		this.toRobot = toRobot;
	}
}
