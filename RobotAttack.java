import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Robot Attack
 * 
 * @author Poorn Pragya
 * @author Manas Mandhani
 */
public class RobotAttack {
	public static void main(String[] args) {
		// int gridx=Integer.parseInt(args[0]);
		// int gridy=Integer.parseInt(args[1]);
		// int N=Integer.parseInt(args[2]);

		try {
			int gridx = Integer.parseInt(args[0]);// 9;
			int gridy = Integer.parseInt(args[1]);// 9;
			int N = Integer.parseInt(args[2]);// 8;
			int targetx = Integer.parseInt(args[3]);// 7;//
			int targety = Integer.parseInt(args[4]);// 7; //1

			// 5x 5 n=3 (4,4)
			// 5x 5 n=3 (2,2)
			// 6x6 n=8 (1,1)
			// 6x6 n=15 (1,1)

			// 9x9 n=8 (7,7)
			// 9x9 n=8 (7,7)
			// 9x9 n= 8 (4,5)
			// 6X6 n=8 (2,2)
			//
			if (N > gridx * gridy - 1)
				throw new IllegalArgumentException("N >gridx*gridy-1");
			if (targetx > gridx - 1 && targety > gridy - 1)
				throw new IllegalArgumentException("targetx>gridx-1 && targety>gridy-1");

			Random rand = new Random();
			HashSet<String> set = new HashSet<String>();
			do {
				int x = rand.nextInt(gridx);
				int y = rand.nextInt(gridy);
				if ((x + " " + y).equals(targetx + " " + targety))
					continue;
				set.add(x + " " + y);
			} while (set.size() < N - 1);

			// ArrayList<String> set = new ArrayList<String>();
			// set.add(1 + " " + 3);
			// set.add(3 + " " + 2);
			// set.add(1 + " " + 5);
			// set.add(3 + " " + 4);

			int i = 0;

			Robot.gridX = gridx;
			Robot.gridY = gridy;
			Robot.numOfBots = set.size();
			// Robot.malaciousCount = 0;
			Robot.isFaultToleranceEnabled = Boolean.parseBoolean(args[5]);
			List<Robot> robotList = new ArrayList<Robot>();
			for (String p : set) {
				String[] temp = p.split(" ");

				if (i != 0 && i % 4 == 0)
					robotList.add(new Robot(i, new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])), targetx,
							targety, true));
				else
					robotList.add(new Robot(i, new Point(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])), targetx,
							targety, false));

				i++;
			}
			Thread.sleep(1000);

			int map[][] = new int[gridx][gridy];
			map[targetx][targety] = 2;
			robotList.get(0).start(map);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
