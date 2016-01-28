import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

/**
 * This class is used by Robot Helper to assist Robot to surround the target
 * 
 * @author Poorn Pragya
 * @author Manas Mandhani
 *
 */
class MoveRobots {

	int m;
	int n;
	int[][] plan = null;
	int target_x = -1;
	int target_y = -1;
	int no_of_robots = 0;
	int[][] map = null;
	List<Point> final_positions = new ArrayList<Point>();
	HashMap<Point, List<Point>> pathsToTake = new HashMap<Point, List<Point>>();
	List<Point> original_positions = new ArrayList<Point>();
	HashMap<Point, Point> pairing = new HashMap<Point, Point>();

	MoveRobots(int[][] _m, int x, int y, int nr) {

		map = _m;
		target_x = x;
		target_y = y;
		no_of_robots = nr;
		this.m = _m.length;
		this.n = _m[0].length;
		plan = new int[m][n];
		plan[target_x][target_x] = 2;

	}

	/*
	 * public static void main(String[] args){
	 * 
	 * 
	 * MoveRobots mr = new MoveRobots(); for(int i = 0 ;i < mr.m;i++)
	 * Arrays.fill(mr.board[i],'-');
	 * 
	 * for(int i = 0 ;i < mr.m;i++) Arrays.fill(mr.plan[i],'-');
	 * 
	 * HashSet<Point> alreadySelected = new HashSet<Point>();
	 * mr.plan[mr.target_x][mr.target_y] = 'T';
	 * mr.board[mr.target_x][mr.target_y] = 'T';
	 * mr.board[mr.target_x+1][mr.target_y] = '&';
	 * mr.randomiseBoard(mr.no_of_robots-1); mr.printGrid(mr.board);
	 * mr.surround(alreadySelected); mr.printGrid(mr.plan); //Point p = new
	 * Point(5,5); //mr.board[p.x][p.y]='*'; //mr.printGrid(mr.board);
	 * //List<Point> l = mr.shortestPath(p);
	 * 
	 * for(Map.Entry<Point,List<Point>> e: mr.pathsToTake.entrySet()){
	 * 
	 * System.out.println(e.getKey()+"-->"+e.getValue());
	 * 
	 * 
	 * 
	 * }
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	/*
	 * void randomiseBoard(int num){
	 * 
	 * int temp = num; Random r = new Random(System.currentTimeMillis());
	 * 
	 * 
	 * while(temp > 0){
	 * 
	 * int a = r.nextInt(m); int b = r.nextInt(n); if(board[a][b] == '&' ||
	 * board[a][b] == 'T') continue; board[a][b] = '&'; temp--;
	 * 
	 * }
	 * 
	 * }
	 */

	void printGrid(int[][] grid) {

		for (int i = 0; i < m; i++) {

			for (int j = 0; j < n; j++) {

				System.out.print(grid[i][j] + " ");

			}

			System.out.println();

		}

	}

	void surround_corner(Point p, Point p2) {

		System.out.println(" surround_corner ");
		final_positions.clear();
		int _case = 4;
		int rem = no_of_robots;
		if (p2.x == m - 1 && p2.y == n - 1) {
			_case = 1;

		} else if (p2.x == 0 && p2.y == 0) {
			_case = 2;

		}

		else if (p2.x == 0 && p2.y == n - 1) {
			_case = 3;

		}

		if (_case == 1) {

			final_positions.add(new Point(p.x + 1, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y - 1));
		} else if (_case == 2) {
			final_positions.add(new Point(p.x - 1, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y + 1));

		}

		else if (_case == 3) {
			final_positions.add(new Point(p.x - 1, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y - 1));

		}

		else if (_case == 4) {
			final_positions.add(new Point(p.x + 1, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y - 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x + 1, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x, p.y + 1));
			rem--;
			if (rem == 0)
				return;
			final_positions.add(new Point(p.x - 1, p.y + 1));

		}

	}

	void surround(HashSet<Point> alreadySelected) {

		// System.out.println("surround "+no_of_robots);
		Point p = new Point(target_x, target_y);
		Queue<Point> q = new LinkedList<Point>();
		q.add(p);
		while (no_of_robots > 0 && !q.isEmpty()) {
			Point temp = q.remove();
			no_of_robots = fill_adj(temp, no_of_robots, q, alreadySelected);
			if (no_of_robots <= 0)
				break;
			no_of_robots = fill_diag(temp, no_of_robots, q, alreadySelected);
			// System.out.println("Queue size "+q.size());

		}
		// (x+1,y+1)(x,y+1)(y+1,x)(x+1,y-1)(x-1,y+1)(x,y-1)(x-1,y)(x-1,y-1)
		System.out.println("Remaining bots " + no_of_robots + " Queue is empty? " + q.size());

	}

	int fill_diag(Point p, int rem, Queue<Point> q, HashSet<Point> alreadySelected) {

		int[][] grid = plan;
		int t_x = p.x;
		int t_y = p.y;

		if (rem == 0)
			return 0;

		if (t_x + 1 < n && t_y + 1 < m && grid[t_x + 1][t_y + 1] != 9 && grid[t_x + 1][t_y + 1] != 2) {

			grid[t_x + 1][t_y + 1] = 9;
			rem--;
			Point t_p = new Point(t_x + 1, t_y + 1);
			final_positions.add(t_p);
			// shortestPath(t_p,alreadySelected);
			q.add(t_p);

		}

		if (rem == 0)
			return 0;

		if (t_x - 1 >= 0 && t_y + 1 < m && grid[t_x - 1][t_y + 1] != 9 && grid[t_x - 1][t_y + 1] != 2) {
			grid[t_x - 1][t_y + 1] = 9;
			rem--;
			Point t_p = new Point(t_x - 1, t_y + 1);
			final_positions.add(t_p);
			// shortestPath(t_p,alreadySelected);
			q.add(t_p);

		}

		if (rem == 0)
			return 0;

		if (t_x + 1 < n && t_y - 1 >= 0 && grid[t_x + 1][t_y - 1] != 9 && grid[t_x + 1][t_y - 1] != 2) {
			grid[t_x + 1][t_y - 1] = 9;
			rem--;
			Point t_p = new Point(t_x + 1, t_y - 1);
			final_positions.add(t_p);
			// shortestPath(t_p,alreadySelected);
			q.add(t_p);

		}

		if (rem == 0)
			return 0;

		if (t_x - 1 >= 0 && t_y - 1 >= 0 && grid[t_x - 1][t_y - 1] != 9 && grid[t_x - 1][t_y - 1] != 2) {
			grid[t_x - 1][t_y - 1] = 9;
			rem--;
			Point t_p = new Point(t_x - 1, t_y - 1);
			final_positions.add(t_p);
			// shortestPath(t_p,alreadySelected);
			q.add(t_p);

		}

		return rem;

	}

	int fill_adj(Point p, int rem, Queue<Point> q, HashSet<Point> alreadySelected) {

		int t_x = p.x;
		int t_y = p.y;
		int[][] grid = plan;

		if (rem == 0)
			return 0;
		if (t_x + 1 < n && grid[t_x + 1][t_y] != 9 && grid[t_x + 1][t_y] != 2) {
			grid[t_x + 1][t_y] = 9;
			rem--;
			Point t_p = new Point(t_x + 1, t_y);
			final_positions.add(t_p);
			// shortestPath(t_p,alreadySelected);
			q.add(t_p);
		}

		if (rem == 0)
			return 0;
		if (t_x - 1 >= 0 && grid[t_x - 1][t_y] != 9 && grid[t_x - 1][t_y] != 2) {
			grid[t_x - 1][t_y] = 9;
			rem--;
			Point t_p = new Point(t_x - 1, t_y);
			final_positions.add(t_p);
			q.add(t_p);
		}
		if (rem == 0)
			return 0;
		if (t_y + 1 < m && grid[t_x][t_y + 1] != 9 && grid[t_x][t_y + 1] != 2) {
			grid[t_x][t_y + 1] = 9;
			rem--;
			Point t_p = new Point(t_x, t_y + 1);
			final_positions.add(t_p);
			q.add(t_p);
		}
		if (rem == 0)
			return 0;
		if (t_y - 1 >= 0 && grid[t_x][t_y - 1] != 9 && grid[t_x][t_y - 1] != 2) {
			grid[t_x][t_y - 1] = 9;
			rem--;
			Point t_p = new Point(t_x, t_y - 1);
			final_positions.add(t_p);
			q.add(t_p);
		}

		return rem;

	}

	void shortestPath(Point p, HashSet<Point> alreadySelected) {

		HashSet<Point> h = new HashSet<Point>();
		h.add(new Point(target_x, target_y));
		Queue<Point> q = new LinkedList<Point>();
		q.add(p);
		h.add(p);

		while (!q.isEmpty()) {

			Point temp = q.remove();
			if (map[temp.x][temp.y] == 3 && !alreadySelected.contains(temp)) {

				pairing.put(p, temp);
				alreadySelected.add(temp);
				break;

			}

			else {

				if (temp.x + 1 < n) {

					Point t_p = new Point(temp.x + 1, temp.y);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}

				}

				if (temp.x - 1 >= 0) {

					Point t_p = new Point(temp.x - 1, temp.y);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}

				}

				if (temp.y + 1 < m) {

					Point t_p = new Point(temp.x, temp.y + 1);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}
				}

				if (temp.y - 1 >= 0) {

					Point t_p = new Point(temp.x, temp.y - 1);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}
				}

				if (temp.x + 1 < n && temp.y + 1 < m) {

					Point t_p = new Point(temp.x + 1, temp.y + 1);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}
				}

				if (temp.x + 1 < n && temp.y - 1 >= 0) {

					Point t_p = new Point(temp.x + 1, temp.y - 1);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}
				}

				if (temp.x - 1 >= 0 && temp.y - 1 >= 0) {
					Point t_p = new Point(temp.x - 1, temp.y - 1);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}

				}

				if (temp.x - 1 >= 0 && temp.y + 1 < m) {
					Point t_p = new Point(temp.x - 1, temp.y + 1);
					// if(!h.contains(t_p) && map[t_p.x][t_p.y]!=3){
					if (!h.contains(t_p)) {
						q.add(t_p);
						h.add(t_p);

					}
				}

			}

		}

	}

}
