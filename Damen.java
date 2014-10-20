package demos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.WindowConstants;

import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;
import plotter.Shapes;
import plotter.Sleep;
import plotter.TextObject;

public class Damen {
	public String DAME = "\u2655"; // unicode for white queen
	private int pause = 100; // pause time in msec
	private int N = 20; // size of the board
	private boolean zeigeBesetzt = false;  
	private Graphic graphic = new Graphic(N + "-Queens Problem");
	private Plotter plotter = graphic.getPlotter();
	private TextObject[][] felder = new TextObject[N + 1][N + 1];
	private TextObject header;
	private int[][] besetzt = new int[N + 1][N + 1];
	private Font font = new Font("Arial", Font.BOLD, 16);
	private Font fontDame = new Font("Arial", Font.BOLD, 24);

	public static void main(String[] args) {
		Damen queens = new Damen();
		queens.zeichneSchachbrett();
		queens.spalte(1);

	}

	public Damen() {
		fontDame = testFonts().deriveFont(22.f).deriveFont(Font.BOLD);
		System.out.println(fontDame);
		plotter.setStatusLine("");

		graphic.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		plotter.setRange(-0.5, N + 1.5);
		plotter.setPreferredSize(new Dimension(600, 600));
		graphic.pack();

	}

	/**
	 * Search a font with a glyph for chess pieces
	 * 
	 * @return the font or null if no matching font was found
	 */
	private static Font testFonts() {
		// erfrage alle verfügbaren Fonts
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		// das Feld fonts enthält alle fonts
		Font[] fonts = ge.getAllFonts();
		System.out.println("total of " + fonts.length + " fonts found");

		for (Font f : fonts) {
			if (f.canDisplay(0x2655)) {
				System.out.println("Font : " + f);
				return f;
			}
		}
		System.out.println("No Font found");
		return null;
	}

	/**
	 * search in a column for free squares. Each free square is then tested.
	 * 
	 * @param s
	 */
	private void spalte(int s) {
		for (int i = 1; i <= N; i++) {
			if (besetzt[s][i] > 0)
				continue;
			setDame(s, i, 1);
			graphic.repaint();
			Sleep.sleep(pause);
			if (s < N) {
				spalte(s + 1);
			} else {
				System.out.println("SOLUTION");
				Sleep.sleep(1000000);
				;
			}
			setDame(s, i, -1);
			Sleep.sleep(pause);
		}

	}

	/**
	 * sets or removes a queen.
	 * 
	 * @param linie
	 *            the column (file) of the square
	 * @param reihe
	 *            the row (rank) of the square
	 * @param mode
	 *            -1 for remove and +1 for setting
	 */
	private void setDame(int linie, int reihe, int mode) {
		for (int i = 1; i <= N; i++) {
			add(mode, linie, i);
			add(mode, i, reihe);
		}
		set_diag(linie, reihe, 1, 1, mode);
		set_diag(linie, reihe, 1, -1, mode);
		set_diag(linie, reihe, -1, 1, mode);
		set_diag(linie, reihe, -1, -1, mode);

		if (mode == 1) {
			felder[linie][reihe].setColor(Color.BLACK);
			felder[linie][reihe].setFont(fontDame);
			felder[linie][reihe].setText(DAME);
			header.setText("# Queens: " + linie);
		} else {
			if (zeigeBesetzt) {
				felder[linie][reihe].setText("" + besetzt[linie][reihe]);
				felder[linie][reihe].setColor(Color.RED);
				felder[linie][reihe].setFont(font);
			} else {
				felder[linie][reihe].setText("");
			}
			header.setText("# Queens: " + linie);
		}
	}

	void set_diag(int linie, int reihe, int incr, int incl, int mode) {
		for (;;) {
			linie += incl;
			reihe += incr;
			if (linie < 1 || linie > N)
				return;
			if (reihe < 1 || reihe > N)
				return;
			besetzt[linie][reihe] += mode;
			if (zeigeBesetzt & !felder[linie][reihe].getText().equals(DAME)) {
				felder[linie][reihe].setText(niceText(besetzt[linie][reihe]));
			}
		}
	}

	private String niceText(int i) {
		if (i == 0)
			return "";
		return "" + i;
	}

	private void add(int inc, int z, int s) {
		besetzt[z][s] += inc;
		if (zeigeBesetzt & !felder[z][s].getText().equals(DAME)) {
			felder[z][s].setText(niceText(besetzt[z][s]));
		}
	}

	public void zeichneSchachbrett() {

    // zeichne Schachbrett
		String border = Shapes.rect(plotter, 0.5, 0.5, N + .5, N + .5);
		plotter.setDataColor(border, Color.BLUE);

		header = plotter.setText("#Queens: ", N / 2., N + 1.8, Color.BLACK,
				font);

		for (int i = 1; i <= N; i++) {
			String labels = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			if (N < labels.length()) {
				char c = labels.charAt(i);

				plotter.setText("" + c, i, 0, Color.BLACK, font);
				plotter.setText("" + c, i, N + 1, Color.BLACK, font);
				plotter.setText("" + i, 0, i, Color.BLACK, font);
				plotter.setText("" + i, N + 1, i, Color.BLACK, font);
			}

			for (int j = 1; j <= N; j++) {
				felder[i][j] = plotter.setText("", i, j, Color.RED, font);
				if ((i + j) % 2 == 0) {
					String name = Shapes.rect(plotter, i - 0.5, j - 0.5,
							i + 0.5, j + 0.5);
					plotter.setDataColor(name, new Color(0, 0, 200, 100));
					plotter.setDataLineStyle(name, LineStyle.FILL);
				}
			}
		}
		graphic.repaint();

	}
}
