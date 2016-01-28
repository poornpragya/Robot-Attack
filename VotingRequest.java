import java.io.Serializable;
import java.net.InetAddress;

/**
 * Voting Request Message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class VotingRequest implements Serializable {

	int leaderPort;
	InetAddress leaderHost;

	public VotingRequest(InetAddress leaderHost, int leaderPort) {
		this.leaderHost = leaderHost;
		this.leaderPort = leaderPort;
	}
}
