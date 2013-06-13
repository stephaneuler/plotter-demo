package demos;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Random;

import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;
import plotter.Sleep;
import plotter.TextObject;

public class Hanoi {
	Graphic graphic = new Graphic("Türme von Hanoi");
	Plotter plotter = graphic.getPlotter();
	int sleepTime = 30;
	int N = 22;
	int[][] tuerme = new int[3][N];
	private int anzahlSchritte = 0;
	private Color[] colors = new Color[N];

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		(new Hanoi()).demo();

	}

	public Hanoi() {
		plotter.setXrange(-N, 3.5 * N);
		plotter.setYrange(0, N+1);
		plotter.setPreferredSize(new Dimension(800, 400));
		plotter.setDataLineStyle(LineStyle.FILL);
		graphic.pack();

		Random r = new Random();
		for (int i = 0; i < N; i++) {
			tuerme[0][i] = N - i;
			colors[i] = new Color( r.nextInt(255),r.nextInt(255),r.nextInt(155));
		}
		
		for (int n = 0; n < 3; n++) {
			double x = n * (N + 1);
			plotter.setXLine(x);
		}

	}

	void zeichnen() {
		plotter.clearPlotVector();
		for (int n = 0; n < 3; n++) {
			double x = n * (N + 1);
			for (int s = 0; s < N; s++) {
				if (tuerme[n][s] != 0) {
					int scheibe = tuerme[n][s];
					int r =  255 * scheibe / N;
					plotter.nextVector();
					plotter.setDataColor( colors[scheibe-1]);
					plotter.add(x - scheibe / 2., s + 1);
					plotter.addD(scheibe, 0);
					plotter.addD(0, .5);
					plotter.addD(-scheibe, 0);
					plotter.addD(0, -.5);
				}
			}
		}
	}

	void lege(int n, int von, int nach, int zwischen) {
		if (n > 0) {
			lege(n - 1, von, zwischen, nach);
			//System.out.println(n + ". Scheibe von " + von + " nach " + nach);
			ziehe( von, nach );
			zeichnen();
			plotter.setStatusLine("Zug Nr. " + anzahlSchritte);
			graphic.repaint();
			Sleep.sleep( sleepTime);
			lege(n - 1, zwischen, nach, von);
			anzahlSchritte++;
		}
	}

	private void ziehe(int von, int nach) {
		for( int i=N-1; i>=0; i-- ) {
			if( tuerme[von][i] != 0 ) {
				int scheibe = tuerme[von][i];
				tuerme[von][i] = 0;
				for( int j=0; j<N; j++ ) {
					if( tuerme[nach][j] == 0) {
						tuerme[nach][j] = scheibe;
						return;
					}
				}
			}
		}
		
	}

	private void demo() {
		zeichnen();
		Sleep.sleep( 2* sleepTime);

		lege(N, 0, 2, 1);
	}
}
