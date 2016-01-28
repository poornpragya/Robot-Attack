import java.io.Serializable;

/**
 * Voting Reply message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class VotingReply implements Serializable {

	int id;
	boolean isMalacious;
	
	public VotingReply(int id, boolean isMalacious) {
		this.id=id;
		this.isMalacious=isMalacious;
	}
}
