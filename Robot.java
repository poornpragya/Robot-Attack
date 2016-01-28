import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a Robot simulated by a thread.
 * 
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
class Robot implements Runnable {
	final static String INET_ADDR = "224.0.0.3";
	final static int PORT = 8888;
	static boolean started = false;
	static int gridX;
	static int gridY;
	static int numOfBots;
	static boolean isFaultToleranceEnabled = false;
	static List<Integer> faultyBotId = new ArrayList<Integer>();

	boolean quit = false;
	boolean isMalacious;
	int id;
	public Point location;
	Thread t;
	Point target;
	boolean initializedRequestToken;
	MulticastSocket receiveSocket;
	DatagramSocket sendSocket;
	InetAddress addr;
	public boolean isLeader;
	List<Point> zoneTosearch;
	List<Point> malaciousRobotList;

	public Robot(int id, Point location, int targetx, int targety, boolean isMalacious) {
		this.id = id;
		this.location = location;

		initializedRequestToken = false;
		isLeader = false;
		target = null;
		this.isMalacious = isMalacious;
		System.out.println("Intitial Bot location: id: " + id + "location :" + location);
		// initialzeMap(targetx, targety);
		// printMap();
		try {
			sendSocket = new DatagramSocket();
			receiveSocket = new MulticastSocket(PORT);
			addr = InetAddress.getByName(INET_ADDR);
		} catch (Exception e) {
			e.printStackTrace();
		}

		t = new Thread(this);
		t.start();

	}

	void start(int map[][]) throws IOException, InterruptedException {

		if (isFaultToleranceEnabled) {
			ServerSocket ss = new ServerSocket(0);
			int port = ss.getLocalPort();
			sendMessage(new VotingRequest(InetAddress.getLocalHost(), port));
			int n = numOfBots;
			while (n > 0) {
				Object o = RobotHelper.retrieveObject(ss.accept());
				if (o instanceof VotingReply) {
					VotingReply v = (VotingReply) o;
					if (v.isMalacious)
						faultyBotId.add(v.id);
				}
				n--;
			}
			System.out.println("Malacious Bots=" + faultyBotId);
		}
		isLeader = true;
		List<Integer> chunks = computeChunks();
		Map<Point, List<Point>> zones = computeZones(chunks);
		RequestToken req = new RequestToken(map, id, numOfBots, zones, faultyBotId);
		if (!initializedRequestToken) {
			req.map[location.x][location.y] = 3;
			req.incrementCurrentAccessId();
			initializedRequestToken = true;
			sendMessage(req);
		}

	}

	// public Zone getZone() {
	// List<Zone> zones = Zone.zones(gridX, gridY);
	// Zone zoneAllocated = zones.get(id % 4);
	// return zoneAllocated;
	// }

	/**
	 * 0 - empty, 3 - other robot, 2 - target
	 */
	// void initialzeMap(int targetx, int targety) {
	// if ((this.location.x >= 0 && this.location.x < map.length)
	// && (this.location.y >= 0 && this.location.y < map[0].length))
	// map[this.location.x][this.location.y] = 3;
	//
	// // if ((targetx >= 0 && targetx < map.length) && (targety >= 0 &&
	// // targety < map[0].length))
	// // map[targetx][targety] = 2;
	// }

	public void run() {
		byte[] buf = new byte[6400];
		try {
			receiveSocket.joinGroup(addr);
			receiveSocket.setReceiveBufferSize(32 * 1024);
			while (!quit) {
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				receiveSocket.receive(msgPacket);
				new RobotHelper(this, msgPacket);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (receiveSocket != null)
				receiveSocket.close();
		}
	}

	static void printMap(int map[][]) {
		// System.out.println("id=" + id);
		// System.out.println("location=" + location);
		for (int i = 0; i < map.length; ++i) {
			for (int j = 0; j < map[0].length; ++j)
				System.out.print(map[i][j] + "    ");
			System.out.println();
		}
		System.out.println();
	}

	void sendMessage(Object object) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(outputStream);
			os.writeObject(object);
			byte[] data = outputStream.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr, PORT);
			sendSocket.send(sendPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Point nextcell(int map[][], Point p2) {
		ArrayList<Point> temp = new ArrayList<Point>();
		for (int i = location.x - 1; i <= location.x + 1; ++i) {
			for (int j = location.y - 1; j <= location.y + 1; ++j) {
				if (i != location.x || j != location.y)
					temp.add(new Point(i, j));
			}
		}
		ArrayList<Point> temp1 = new ArrayList<Point>();
		for (Point p : temp) {
			if ((p.x >= 0 && p.x < map.length) && (p.y >= 0 && p.y < map[0].length) && (map[p.x][p.y] == 0))
				temp1.add(p);
		}

		double min = Double.MAX_VALUE;
		Point p1 = null;

		for (Point p : temp1) {
			double dist = p.distance(new Point(p2.x, p2.y));
			if (dist < min) {
				min = dist;
				p1 = p;
			}
		}

		// if (p1 == null)
		// System.err.println("Failed in Robot:nextCell");
		return p1;
	}

	Point checkTargetInVicinity(int map[][]) {
		ArrayList<Point> temp = new ArrayList<Point>();
		for (int i = location.x - 1; i <= location.x + 1; ++i) {
			for (int j = location.y - 1; j <= location.y + 1; ++j) {
				if (i != location.x || j != location.y)
					temp.add(new Point(i, j));
			}
		}

		for (Point p : temp) {
			if ((p.x >= 0 && p.x < map.length) && (p.y >= 0 && p.y < map[0].length) && (map[p.x][p.y] == 2))
				return p;
		}

		return null;
	}

	// boolean liesInZone(Point p) {
	// int current_x = p.x;
	// int current_y = p.y;
	// Point topLeft = getZone().topLeft;
	// Point bottomRight = getZone().bottomRight;
	//
	// if (current_x >= topLeft.x && current_x <= bottomRight.x && current_y >=
	// topLeft.y
	// && current_y <= bottomRight.y)
	// return true;
	// else
	// return false;
	// }

	public void clearMap(int map[][]) {
		for (int i = 0; i < map.length; ++i) {
			for (int j = 0; j < map[0].length; ++j) {
				if (i != location.x && j != location.y)
					map[i][j] = 0;
			}
		}
	}

	List<Integer> computeChunks() {
		int totalCells = gridX * gridY;

		List<Integer> chunks = new ArrayList<Integer>();
		int load = totalCells / numOfBots;
		for (int i = 0; i < numOfBots; ++i)
			chunks.add(load);
		int remainderLoad = totalCells % numOfBots;
		for (int i = 0; i < remainderLoad; ++i) {
			chunks.remove(i);
			chunks.add(i, load + 1);
		}

		return chunks;
	}

	Map<Point, List<Point>> computeZones(List<Integer> chunks) {
		int x = 0;
		int y = 0;
		HashMap<Point, List<Point>> zones1 = new HashMap<Point, List<Point>>();
		Point p = null;
		for (Integer chunk : chunks) {

			for (int k = 0; k < chunk; k++) {
				if (y == gridY) {
					x++;
					y = gridY - 1;
				}

				if (y == -1) {
					x++;
					y = 0;
				}

				if (k == 0) {
					List<Point> cellAssigned = new ArrayList<Point>();
					p = new Point(x, y);
					cellAssigned.add(p);
					zones1.put(p, cellAssigned);
				} else {
					List<Point> temp = zones1.get(p);
					temp.add(new Point(x, y));
					zones1.put(p, temp);
				}

				if (x % 2 == 0)
					y++;
				else
					y--;

			}
		}

		return zones1;

	}
}

/*
 * { System.out.println("GridX= " + gridX); System.out.println("GridY= " +
 * gridY); System.out.println("Number of Bots = " + numOfBots); RequestToken req
 * = new RequestToken(new int[gridX][gridY], id, numOfBots);
 * req.map[location.x][location.y] = 3;
 * 
 * if (liesInZone(location)) { System.out.println("ZONE REACHED-->" +" id= " +
 * id + " location " + location); zoneReached = true;
 * req.incrementZoneReachedCounter(); //System.out.println(id+
 * " Incrementing zone reachcount" + req.zoneReachedCounter); }
 * req.incrementnextId(); started = true; initializedRequestToken = true;
 * sendMessage(req); }
 */
