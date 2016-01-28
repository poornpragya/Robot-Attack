import java.io.Serializable;

/**
 * Zone Reached Message
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
public class ZoneReached implements Serializable {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	RequestToken req;

	public ZoneReached(RequestToken req) {
		this.req = req;
	}
}
