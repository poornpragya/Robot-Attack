import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * @author Poorn Pragya
 * @author Manas Mandhani
 * 
 *         This class creates a new thread to assist Robot on recepit of
 *         Multicast message
 *
 */
public class RobotHelper implements Runnable {

	Robot r;
	Object msg;
	Thread t;

	public RobotHelper(Robot r, DatagramPacket msgPacket) {
		this.r = r;
		byte[] data = msgPacket.getData();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		ObjectInputStream is;
		try {
			is = new ObjectInputStream(in);
			this.msg = is.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
			if (msg instanceof RequestToken) {
				RequestToken req = (RequestToken) msg;
				if (req.currentAccessId != r.id)
					return;
				if (!r.initializedRequestToken) {
					req.map[r.location.x][r.location.y] = 3;
					req.incrementCurrentAccessId();
					r.initializedRequestToken = true;
					r.sendMessage(req);
				} else {
					if (r.isLeader) {
						System.out.println();
						System.out.println("Initial Map after Multicast -->");
						Robot.printMap(req.map);

						if (Robot.numOfBots <= (int) (Robot.gridX * Robot.gridY) / 2) {

							List<Point> locations = computeRobotLocation(req);
							// System.out.println("Locations" + locations);

							System.out.println("Zones assigned --->");
							for (Point p : req.zones.keySet()) {
								System.out.println(req.zones.get(p));
							}

							int port = 8891;
							ServerSocket ss = new ServerSocket(port);

							System.out.println();
							for (Point p : req.zones.keySet()) {

								Point minDistBot = findMinDistBotFromPoint(p, locations);
								System.out.println("Zone " + p + " assigned to Robot at location" + minDistBot);

								r.sendMessage(new ZoneAssign(InetAddress.getLocalHost(), port, p, req.zones.get(p),
										minDistBot, req));
								Object receviedMsg = retrieveObject(ss.accept());

								if (receviedMsg instanceof ZoneReached)
									req = ((ZoneReached) receviedMsg).req;

								if (receviedMsg instanceof StopNewLeaderFound) {
									StopNewLeaderFound req1 = (StopNewLeaderFound) receviedMsg;
									r.isLeader = false;
									sendUnicastMessage(
											new StopNewLeaderFound(InetAddress.getLocalHost(), req1.fromPort, port),
											InetAddress.getLocalHost(), req1.fromPort);
									ss.close();
									return;
								}
								locations.remove(minDistBot);
							}

							if (!ss.isClosed())
								ss.close();

							// System.out.println();
							// System.out.println("Updated Map after Zone
							// Allocation");
							// Robot.printMap(req.map);

							r.isLeader = false;
							StartZoneSearchRequest zoneSearch = new StartZoneSearchRequest(req.map, r.id,
									Robot.numOfBots, Robot.faultyBotId);
							zoneSearch.incrementCurrentAccessId();
							r.sendMessage(zoneSearch);
						} else {
							List<Point> robotLocations = computeRobotLocation(req);
							List<Point> unexploredCells = computeUnexploredLocations(req.map);

							System.out.println("Checking Targets in Vicinity first --> ");
							int port = 8897;
							ServerSocket ss = new ServerSocket(port);

							for (Point p : robotLocations) {
								r.sendMessage(new SearchTargetInVicinity(InetAddress.getLocalHost(), port, p, req));
								Object rcvdMsg = retrieveObject(ss.accept());
								if (rcvdMsg instanceof StopNewLeaderFound) {
									StopNewLeaderFound req1 = (StopNewLeaderFound) rcvdMsg;
									r.isLeader = false;
									sendUnicastMessage(
											new StopNewLeaderFound(InetAddress.getLocalHost(), req1.fromPort, port),
											InetAddress.getLocalHost(), req1.fromPort);
									ss.close();
									return;
								}

								if (rcvdMsg instanceof TargetNotFound) {
									continue;
								}

							}

							if (!ss.isClosed())
								ss.close();

							ss = new ServerSocket(8900);

							System.out.println("Target not found in vicinity");
							System.out.println("Unexplored Locations --> ");
							System.out.println(unexploredCells);
							for (Point p : unexploredCells) {
								Point minDistBot = findMinDistBotFromPoint(p, robotLocations);
								System.out.println("Cell " + p + " assigned to Robot at location" + minDistBot);
								r.sendMessage(
										new CellSearchRequest(InetAddress.getLocalHost(), port, p, minDistBot, req));
								Object msgReceived = retrieveObject(ss.accept());

								if (msgReceived instanceof ZoneReached)
									req = ((ZoneReached) msgReceived).req;

								if (msgReceived instanceof StopNewLeaderFound) {
									StopNewLeaderFound req1 = (StopNewLeaderFound) msgReceived;
									r.isLeader = false;
									sendUnicastMessage(
											new StopNewLeaderFound(InetAddress.getLocalHost(), req1.fromPort, port),
											InetAddress.getLocalHost(), req1.fromPort);
									ss.close();
									return;
								}

								robotLocations.remove(minDistBot);

							}

							if (!ss.isClosed())
								ss.close();

						}
						// System.out.println();
						// System.out.println("Updated Map after Cell
						// Allocation");
						// Robot.printMap(req.map);
					}

				}
			} else if (msg instanceof ZoneAssign) {

				ZoneAssign zoneAssignedReq = (ZoneAssign) msg;

				if (!r.location.equals(zoneAssignedReq.toRobot))
					return;

				while (!r.location.equals(zoneAssignedReq.zoneStartingPoint)) {
					Point target = r.checkTargetInVicinity(zoneAssignedReq.req.map);
					if (target != null) {
						System.out.println("Target Found by id=" + r.id + " at location " + target);
						int port = 8893;
						ServerSocket ss = new ServerSocket(port);
						sendUnicastMessage(
								new StopNewLeaderFound(InetAddress.getLocalHost(), zoneAssignedReq.leaderPort, port),
								zoneAssignedReq.leaderHost, zoneAssignedReq.leaderPort);
						Object o = retrieveObject(ss.accept());
						if (o instanceof StopNewLeaderFound) {
							r.isLeader = true;
							r.target = target;
							ss.close();
						}

						r.sendMessage(new TargetFound(zoneAssignedReq.req.map, target));
						return;
					}
					Point nextCell = r.nextcell(zoneAssignedReq.req.map, zoneAssignedReq.zoneStartingPoint);
					if (nextCell != null) {
						printMoveMessage(r.location, nextCell);
						move(nextCell, zoneAssignedReq.req.map);
					} else {
						System.err.println("Failed in RobotHelper: msg instanceof ZoneAssign");
					}
				}
				r.zoneTosearch = zoneAssignedReq.zonePointsToVisit;
				r.zoneTosearch.remove(0);
				System.out.println("id=" + r.id + " Location" + r.location + "  Zone To search=" + r.zoneTosearch);
				sendUnicastMessage(new ZoneReached(zoneAssignedReq.req), zoneAssignedReq.leaderHost,
						zoneAssignedReq.leaderPort);

			} else if (msg instanceof StartZoneSearchRequest) {
				StartZoneSearchRequest req = (StartZoneSearchRequest) msg;
				if (r.id != req.currentAccessId)
					return;

				if (!r.zoneTosearch.isEmpty()) {
					Point nextCell = r.zoneTosearch.remove(0);
					if (nextCell != null) {
						printMoveMessage(r.location, nextCell);
						move(nextCell, req.map);
						Point target = r.checkTargetInVicinity(req.map);
						if (target != null) {
							System.out.println("Target Found by id=" + r.id + " at location " + target);
							r.sendMessage(new TargetFound(req.map, target));
							r.isLeader = true;
							return;
						}

					} else {
						System.err.println(
								"Should not happen : Failed in RobotHelper:  msg instanceof StartZoneSearchRequest");
					}
				}

				req.incrementCurrentAccessId();
				r.sendMessage(req);

			} else if (msg instanceof TargetFound) {
				TargetFound treq = (TargetFound) msg;
				r.target = treq.target;
				if (r.isLeader) {

					System.out.println("New Leader started");
					Robot.printMap(treq.map);
					HashSet<Point> alreadySelected = new HashSet<Point>();
					System.out.println("Target:" + treq.target);
					MoveRobots mr = new MoveRobots(treq.map, treq.target.x, treq.target.y, Robot.numOfBots);
					System.out.println("Target is " + treq.target);
					mr.surround(alreadySelected);
					System.out.println(mr.final_positions);
					for (int i = 0; i < mr.final_positions.size(); i++) {
						Point fp = mr.final_positions.get(i);
						if (fp.equals(new Point(0, 0))) {
							mr.surround_corner(r.target, new Point(0, 0));
						} else if (fp.equals(new Point(treq.map.length - 1, treq.map[0].length - 1))) {
							mr.surround_corner(r.target, new Point(treq.map.length - 1, treq.map[0].length - 1));
						} else if (fp.equals(new Point(0, treq.map[0].length - 1))) {
							mr.surround_corner(r.target, new Point(0, treq.map[0].length - 1));
						} else if (fp.equals(new Point(treq.map[0].length - 1, 0))) {
							mr.surround_corner(r.target, new Point(treq.map[0].length - 1, 0));
						}
					}

					System.out.println("Finally" + mr.final_positions);
					ServerSocket ss = new ServerSocket(0);
					int port = ss.getLocalPort();
					List<Point> locations = computeRobotLocation(treq.map);
					System.out.println(locations);
					// int[][] new_map = treq.map;
					for (Point p : mr.final_positions) {

						if (!locations.isEmpty())
							System.out.println("Sending to" + p);
						Point minsDistBot = findMinDistBotFromPoint(p, locations);
						r.sendMessage(new SurroundInfo(p, minsDistBot, port, treq.map));

						Object o = retrieveObject(ss.accept());
						if (o instanceof int[][]) {
							treq.map = (int[][]) o;
						}
						locations.remove(minsDistBot);
					}
				}
				Thread.sleep(1000);
				System.exit(1);

			} else if (msg instanceof SurroundInfo) {

				SurroundInfo sInfo = (SurroundInfo) msg;
				if (sInfo != null && sInfo.location == null)
					return;
				if (!sInfo.location.equals(r.location))
					return;

				System.out.println("For point " + r.location);
				Point _p = sInfo.next_location;
				while (true) {
					Point nc = r.nextcell(sInfo.map, sInfo.next_location);
					// System.out.println("Next cell is " + nc);
					if (nc != null)
						move(nc, sInfo.map);
					Robot.printMap(sInfo.map);

					System.out.println("r.location" + r.location);
					System.out.println("sInfo.next_Location" + sInfo.next_location);
					if (r.location.equals(sInfo.next_location))
						break;
				}

				sendUnicastMessage(sInfo.map, InetAddress.getLocalHost(), sInfo.port);

			} else if (msg instanceof CellSearchRequest) {
				CellSearchRequest creq = (CellSearchRequest) msg;
				if (!r.location.equals(creq.locationOfAssignedRobot))
					return;

				while (!r.location.equals(creq.cellToSearch)) {
					Point target = r.checkTargetInVicinity(creq.req.map);
					if (target != null) {
						System.out.println("Target Found by id=" + r.id + " at location " + target);
						int port = 8893;
						ServerSocket ss = new ServerSocket(port);
						sendUnicastMessage(new StopNewLeaderFound(InetAddress.getLocalHost(), creq.leaderPort, port),
								creq.leaderHost, creq.leaderPort);
						Object o = retrieveObject(ss.accept());
						if (o instanceof StopNewLeaderFound) {
							r.isLeader = true;
							r.target = target;
							ss.close();
						}

						r.sendMessage(new TargetFound(creq.req.map, target));
						return;
					}
					Point nextCell = r.nextcell(creq.req.map, creq.cellToSearch);
					if (nextCell != null) {
						printMoveMessage(r.location, nextCell);
						move(nextCell, creq.req.map);
					} else {
						System.err.println("Failed in RobotHelper: msg instanceof ZoneAssign");
					}
				}

				System.out.println("id=" + r.id + " Location" + r.location + "reached assigned cell to search");
				sendUnicastMessage(new ZoneReached(creq.req), creq.leaderHost, creq.leaderPort);
			} else if (msg instanceof SearchTargetInVicinity) {
				SearchTargetInVicinity sreq = (SearchTargetInVicinity) msg;
				if (!sreq.robotLocation.equals(r.location))
					return;

				Point target = r.checkTargetInVicinity(sreq.req.map);
				if (target != null) {
					System.out.println("Target Found by id=" + r.id + " at location " + target);
					int port = 8893;
					ServerSocket ss = new ServerSocket(port);
					sendUnicastMessage(new StopNewLeaderFound(InetAddress.getLocalHost(), sreq.leaderPort, port),
							sreq.leaderHost, sreq.leaderPort);
					Object o = retrieveObject(ss.accept());
					if (o instanceof StopNewLeaderFound) {
						r.isLeader = true;
						r.target = target;
						ss.close();
					}

					r.sendMessage(new TargetFound(sreq.req.map, target));
					return;
				}

				sendUnicastMessage(new TargetNotFound(), sreq.leaderHost, sreq.leaderPort);

			} else if (msg instanceof VotingRequest) {

				VotingRequest vreq = (VotingRequest) msg;
				sendUnicastMessage(new VotingReply(r.id, r.isMalacious), vreq.leaderHost, vreq.leaderPort);
				if (r.isMalacious) {
					r.t.stop();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	List<Point> computeUnexploredLocations(int[][] map) {
		List<Point> locations = new ArrayList<Point>();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 0)
					locations.add(new Point(i, j));
			}
		}
		return locations;
	}

	void printMoveMessage(Point prev, Point next) {
		System.out.println("id " + r.id + " Moving from location " + prev + " to " + next);
	}

	void move(Point nextCell, int map[][]) {
		map[nextCell.x][nextCell.y] = 3;
		map[r.location.x][r.location.y] = 0;
		r.location.x = nextCell.x;
		r.location.y = nextCell.y;
	}

	List<Point> computeRobotLocation(RequestToken req) {
		List<Point> locations = new ArrayList<Point>();
		for (int i = 0; i < req.map.length; i++) {
			for (int j = 0; j < req.map[0].length; j++) {
				if (req.map[i][j] == 3)
					locations.add(new Point(i, j));
			}
		}
		return locations;
	}

	List<Point> computeRobotLocation(int[][] map) {
		List<Point> locations = new ArrayList<Point>();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j] == 3)
					locations.add(new Point(i, j));
			}
		}
		return locations;
	}

	Point findMinDistBotFromPoint(Point p, List<Point> locations) {
		double dist = Double.MAX_VALUE;
		Point temp = null;
		for (Point bot : locations) {
			if (p.distance(bot) < dist) {
				dist = p.distance(bot);
				temp = bot;
			}
		}

		return temp;
	}

	static Object retrieveObject(Socket s) {
		ObjectInputStream in;
		Object o = null;
		try {
			in = new ObjectInputStream(s.getInputStream());
			o = in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return o;
	}

	void sendUnicastMessage(Object msg, InetAddress host, int port) {
		try {
			Socket s = new Socket(host, port);
			ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
			out.writeObject(msg);
			s.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

// void intitalizeLocationRequestToken(RequestToken req, Point newlocation) {
// if (req.map[newlocation.x][newlocation.y] == 0) {
// req.map[newlocation.x][newlocation.y] = 3;
// req.incrementnextId();
// } else
// System.err.println("Some other robot present at for id=" + r.id);
// }
//
// void updateLocationRequestToken(RequestToken req, Point prevLocation, Point
// newlocation) {
// if (req.map[newlocation.x][newlocation.y] == 0) {
// req.map[newlocation.x][newlocation.y] = 3;
// req.map[prevLocation.x][prevLocation.y] = 0;
// req.incrementnextId();
// } else
// System.err.println("Some other robot present at for id=" + r.id);
// }

/*
 * 
 * 
 * RequestToken req = (RequestToken) msg; if (req.currentAccessId != r.id)
 * return;
 * 
 * // System.out.println("Token in id=" + r.id); if (!r.initializedRequestToken)
 * { intitalizeLocationRequestToken(req, r.location); r.initializedRequestToken
 * = true; if (r.liesInZone(r.location)) { System.out.println("ZONE REACHED-->"
 * +" id= " + r.id + " location " + r.location); r.zoneReached = true;
 * req.incrementZoneReachedCounter(); //System.out.println(r.id +
 * " Incrementing zone reachcount" + req.zoneReachedCounter); }
 * r.sendMessage(msg); } else { if (!r.zoneReached) { Point nextLocation =
 * r.nextcell(req.map, r.getZone().topLeft.x, r.getZone().topLeft.y); if
 * (nextLocation != null) { updateLocationRequestToken(req, r.location,
 * nextLocation); System.out.println( "id " + r.id + " moved from location " +
 * r.location + " to " + nextLocation); r.location.copy(nextLocation); }
 * 
 * if (r.liesInZone(r.location)) { System.out.println("ZONE REACHED-->" +" id= "
 * + r.id + " location " + r.location); r.zoneReached = true;
 * req.incrementZoneReachedCounter(); //System.out.println(r.id +
 * " Incrementing zone reachcount" + req.zoneReachedCounter); }
 * r.sendMessage(msg); } else { // Reached Zone if
 * (req.hasEveryOneReachedZone()) { //System.out.println("here");
 * req.incrementnextId(); r.sendMessage(msg); r.quit = true; } else {
 * req.incrementnextId(); r.sendMessage(msg); } }
 * 
 * }
 * 
 */

// Point computeZoneToTake(RequestToken req) {
// if (req.zones.isEmpty())
// return null;
// Point temp = null;
// for (Point p : req.zones.keySet()) {
// if (p.equals(r.location))
// temp = p;
// }
//
// if (temp != null) {
// return temp;
// }
//
// double dist = Double.MAX_VALUE;
// for (Point p : req.zones.keySet()) {
// if (r.location.distance(p) < dist && req.map[p.x][p.y] == 0) {
// dist = r.location.distance(p);
// temp = p;
// }
// }
//
// if (temp == null)
// System.err.println("Faliled in RobotHelper:computeZoneToTake");
// return temp;
// }
