package demos;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import plotter.Graphic;
import plotter.Plotter;
import plotter.Sleep;
import plotter.TextObject;

public class Sudoko {
	static Graphic graphic = new Graphic("Sudoku");
	static Plotter plotter = graphic.getPlotter();
	static boolean animation = true;
	static int sum = 0;

	List<Integer>[][] kandidaten;
	int[][] sudoku = new int[9][9];
	int level = 0;
	private int pauseTime = 1000;

	public Sudoko() {
		kandidaten = new ArrayList[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				kandidaten[i][j] = new ArrayList<Integer>();
			}
		}

		plotter.setRange(-0.5, 8.5);
		plotter.setXLine(2.5);
		plotter.setXLine(5.5);
		plotter.setYLine(2.5);
		plotter.setYLine(5.5);
	}

	public Sudoko clone() {
		Sudoko neu = new Sudoko();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				neu.sudoku[i][j] = sudoku[i][j];
			}
		}
		neu.level = level + 1;
		neu.buildKandidates();
		neu.showValues();
		return neu;
	}

	public static void main(String[] args) {
		LineNumberReader lnr = null;
		try {
			lnr = new LineNumberReader(new FileReader("sudoku.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

    // loop over all puzzles
		for (int n = 0; n < 50; n++) {
			Sudoko e = new Sudoko();
			e.read(lnr);
			if (n < -2)
				continue;
			String statusLine = "sudoko # " + n + "  beginnt mit "
					+ e.countOpen() + " freien Feldern";
			System.out.println(statusLine);
			plotter.setStatusLine(statusLine);
			e.solve();
		}
	}

	private void print() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(sudoku[i][j]);
			}
			System.out.println();
		}

	}

	private int getCheckSum() {
		String c = "" + sudoku[0][0] + sudoku[0][1] + sudoku[0][2];
		return Integer.parseInt(c);
	}

	private void solve() {
		buildKandidates();
		while (removeSingles() > 0)
			System.out.println(countOpen());
		if (countOpen() > 0) {
			nextTry();
		} else {
			print();
			int g = getCheckSum();
			sum += g;
			System.out.println(g + " " + sum);
		}
		System.out.println("Solved!!");
		System.out.println("================================================");

	}

	private boolean nextTry() {
		// System.out.println( "next try: " + countOpen() + " L: " + level);

		int minsize = 9;
		int im = 0, jm = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] == 0) {
					if (kandidaten[i][j].size() == 0) {
						// System.out.println( "failed: " +i + " " +j );
						if (animation)
							Sleep.sleep(4000);
						return false;
					}
					if (kandidaten[i][j].size() < minsize) {
						minsize = kandidaten[i][j].size();
						im = i;
						jm = j;
					}
				}
			}
		}

		for (int w : kandidaten[im][jm]) {
			Sudoko e2 = clone();
			if (e2.iterate(im, jm, w))
				return true;

		}
		return false;
	}

	private boolean iterate(int i, int j, int w) {
		// System.out.println( "iterate: " + countOpen() + " " + i + " " + j +
		// " " + w);
		// System.out.print( level +". ");
		sudoku[i][j] = w;
		kandidaten[i][j].clear();
		remove(i, j);
		if (animation) {
			plotter.removeText(i, j);
			TextObject to = plotter.setText("" + sudoku[i][j], i, j);
			to.setColor(Color.RED);
			graphic.repaint();
		}
		while (removeSingles() > 0)
			if (hasNoSolution()) {
				// System.out.println();
				return false;
			}
		// System.out.println(countOpen());
		if (countOpen() > 0)
			return nextTry();

		print();
		int g = getCheckSum();
		sum += g;
		System.out.println(g + " " + sum);

		return true;
	}

	private boolean hasNoSolution() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] == 0 & kandidaten[i][j].size() == 0) {
					// System.out.println( i +" " + j + " " +
					// kandidaten[i][j].size());
					return true;
				}
			}
		}
		return false;
	}

	private int countOpen() {
		int c = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] == 0)
					++c;

			}
		}
		return c;
	}

	private int removeSingles() {
		int c = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (kandidaten[i][j].size() == 1) {
					++c;
					sudoku[i][j] = kandidaten[i][j].get(0);
					if (animation) {
						plotter.removeText(i, j);
						TextObject to = plotter.setText(
								"" + kandidaten[i][j].get(0), i, j);
						to.setColor(Color.GREEN);
						graphic.repaint();
					}
					remove(i, j);
					showKandidaten();
					if (animation) {
						Sleep.sleep(pauseTime);
					}
				}
			}
		}
		return c;
	}

	private void buildKandidates() {

		// fill with all candidates
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] == 0) {
					for (int l = 1; l <= 9; l++)
						kandidaten[i][j].add(l);
					// if (animation)
					// ma.setLabelColor(i, j, Color.WHITE);
				} else {
					// if (animation)
					// ma.setLabelColor(i, j, Color.YELLOW);
				}
			}
		}

		// remove canddiates based on constraints
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] != 0) {
					remove(i, j);
				}
			}
		}

		showKandidaten();

	}

	private void showKandidaten() {
		if (!animation)
			return;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] == 0) {
					plotter.removeText(i, j);
					plotter.setText(kandidaten[i][j].toString(), i, j);
				}
			}
		}
	}

	private void showValues() {
		if (!animation)
			return;

		plotter.removeAllText();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (sudoku[i][j] != 0) {
					plotter.removeText(i, j);
					TextObject to = plotter.setText("" + sudoku[i][j], i, j);
					to.setColor(Color.BLACK);
				}
			}
		}
	}

	private void remove(int i, int j) {
		Integer W = new Integer(sudoku[i][j]);
		for (int l = 0; l < 9; l++) {
			kandidaten[i][l].remove(W);
			kandidaten[l][j].remove(W);
		}
		int is = i / 3 * 3;
		int js = j / 3 * 3;
		// System.out.println(i + " " + j + ": " + is + " " + js);
		for (int l = 0; l < 3; l++) {
			for (int m = 0; m < 3; m++) {
				kandidaten[is + l][js + m].remove(W);
			}
		}
	}

	private void read(LineNumberReader lnr) {
		try {
			String line;
			while ((line = lnr.readLine()) != null) {
				System.out.println(line);
				if (line.startsWith("G")) {
					for (int i = 0; i < 9; i++) {
						line = lnr.readLine();
						System.out.println(line);
						for (int j = 0; j < 9; j++) {
							int w = Integer.parseInt(line.substring(j, j + 1));
							sudoku[i][j] = w;
						}
					}
					showValues();
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
