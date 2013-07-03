package demos;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;

public class RasterMatrix {
	int N = 3;
	private boolean randomize = false;
	private boolean showLines = true;
	private List<Boolean> isBlack = new ArrayList<Boolean>();

	private Graphic graphic = new Graphic(N + "x" + N + "-Matrix");
	private Plotter plotter = graphic.getPlotter();

	public static void main(String[] args) {
		(new RasterMatrix()).male();

	}

	public RasterMatrix() {
		plotter.setXrange(-.5, N * N + N + .5);
		plotter.setYrange(-.5, N * N + N + N + .5);
		plotter.setAutoIncrementColor(false);
		plotter.setDataColor(Color.BLACK);
		plotter.setStatusLine(N + "*" + N + " size raster matrix");
	}

	private void male() {

		for (int n = 0; n <= N * N; n++) {
			setBlacks(n);
			matrix(n);
		}
		graphic.repaint();

	}

	private void setBlacks(int n) {
		isBlack.clear();
		for (int i = 0; i < N * N; i++) {
			isBlack.add(i < n);
		}
		if (randomize) {
			Collections.shuffle(isBlack);
		}

	}

	private void matrix(int n) {
		int c = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (showLines) {
					plotter.nextVector();
					plotter.setOffset(n % N * (N + 1), n / N * (N + 1));
					plotter.setDataLineStyle(LineStyle.LINE);
					plotter.setDataColor(Color.BLACK);
					plotter.add(i, j);
					plotter.addD(1, 0);
					plotter.addD(0, 1);
					plotter.addD(-1, 0);
					plotter.add(i, j);
				}
				if (isBlack.get(c)) {
					plotter.nextVector();
					plotter.setDataColor(Color.BLACK);
					plotter.setOffset(n % N * (N + 1), n / N * (N + 1));
					plotter.setDataLineStyle(LineStyle.FILL);
					plotter.add(i, j);
					plotter.addD(1, 0);
					plotter.addD(0, 1);
					plotter.addD(-1, 0);
					plotter.add(i, j);
				}
				++c;
			}
		}

	}

}
