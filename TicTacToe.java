package demos;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;

import plotter.Graphic;
import plotter.Plotter;

/**
 * @author Euler
 * 
 *         Zeigt das Einbauen von Bildern am Beispiel eines Tic Tac Toe Spiels
 *         (ohne Spielelogik)
 */
public class TicTacToe {
	Graphic graphic = new Graphic("Tic Tac Toe Demo");
	Plotter plotter = graphic.getPlotter();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(new TicTacToe()).demo();

	}

	private void demo() {
		plotter.setRange(1, 4);
		plotter.setPreferredSize(new Dimension(340, 340));
		plotter.setBackground(Color.WHITE);
		graphic.pack();

		for (int i = 2; i < 4; i++) {
			plotter.setXLine(i);
			plotter.setYLine(i);
		}

		for (int feld = 1; feld <= 9; feld++) {
			plotter.setText("" + feld, (feld - 1) % 3 + 1.5,
					(feld - 1) / 3 + 1.5);
		}

		Scanner sc = new Scanner(System.in);
		for (int zug = 1; zug <= 9; zug++) {
			System.out.println(">");
			int feld = sc.nextInt();
			String filename = "o_kl.png";
			if (zug % 2 == 0)
				filename = "x_kl.png";
			plotter.setImage(filename, (feld - 1) % 3 + 1.5,
					(feld - 1) / 3 + 1.5);
			plotter.removeText("" + feld);
			plotter.repaint();
		}
	}

}