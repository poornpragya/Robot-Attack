import java.io.Serializable;
import java.net.InetAddress;

/**
 * Stop new leader request message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class StopNewLeaderFound implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int fromPort;
	int toPort;
	InetAddress host;

	public StopNewLeaderFound(InetAddress host, int toPort, int fromPort) {
		this.host = host;
		this.toPort = toPort;
		this.fromPort = fromPort;
	}

}
