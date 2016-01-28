import java.util.ArrayList;
import java.util.List;

public class Test {
	public static void main(String[] args) {
		int gridx = 6;
		int gridy = 6;

		int bots = 5;
		int totalCells = gridx * gridy;

		List<Integer> chunks = new ArrayList<Integer>();
		int load = totalCells / bots;
		for (int i = 0; i < bots; ++i)
			chunks.add(load);
		int remainderLoad = totalCells % bots;
		for (int i = 0; i < remainderLoad; ++i) {
			chunks.remove(i);
			chunks.add(i, load + 1);
		}

		System.out.println(chunks);

		int x = 0;
		int y = 0;
		String zones = "";
		for (Integer chunk : chunks) {
			
			for (int k = 0; k < chunk; k++) {
				if (y == gridy) {
					x++;
					y=gridy-1;
				}

				if (y == -1) {
					x++;
					y=0;
				}

				zones += ("(" + x + "," + y + "), ");


				if (x % 2 == 0)
					y++;
				else
					y--;

			}
			zones += "\n";
		}

		System.out.println(zones);
	}
}

// for (int z = 0; z < chunks.size(); z++) {
// int chunk = chunks.get(z);
// if (x % 2 == 0) {
// if (y + chunk - 1 <= gridy - 1) {
// for (; y < chunk; y++)
// zones += ("(" + x + "," + y + "), ");
// zones += "\n";
//
// if (y == gridy - 1)
// x++;
//
// // System.out.println("y="+y);
// } else {
// int canOccupy = gridy - y;
// for (; y < gridy; y++)
// zones += ("(" + x + "," + y + "), ");
// int remaining = chunk - canOccupy;
// x++;
// y = gridy - 1;
// for (int k = 0; k < remaining; k++) {
// zones += ("(" + x + "," + y + "), ");
// y--;
// }
// zones += "\n";
// }
// } else {
// if (y - (chunk - 1) >= 0) {
// for (int k = 0; k < chunk; k++, y--)
// zones += ("(" + x + "," + y + "), ");
//
// if (y == 0)
// x++;
//
// zones += "\n";
// // System.out.println("Hi");
//
// } else {
// int canOccupy = y;
// for (; y >= 0; y--)
// zones += ("(" + x + "," + y + "), ");
//
// x++;
// y = 0;
// int remaining = chunk - canOccupy;
// for (int j = 0; j < remaining - 1; j++) {
// zones += ("(" + x + "," + y + "), ");
// y++;
// }
// zones += "\n";
// }
// }
// }
