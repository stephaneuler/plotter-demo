import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

import plotter.LineStyle;
import plotter.Plotter;

public class Histogram {
	private static int nAverage = 32;
	private static Color[] color = { Color.RED, Color.GREEN, Color.BLUE };;
	private int[][] bins = new int[3][256];
	private double[][] probs;
	private File refDir;
	private String fileName;
	private FeatureType type = FeatureType.RGB;

	public FeatureType getType() {
		return type;
	}

	public static int getnAverage() {
		return nAverage;
	}

	public static void setnAverage(int nAverage) {
		Histogram.nAverage = nAverage;
	}

	public File getRefDir() {
		return refDir;
	}

	public void setRefDir(File refDir) {
		this.refDir = refDir;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Calculate the probabilities for this histogram using 
	 * p_i = h[i] / n where n is the total number of pixels. 
	 * 
	 * In order to save computation time nAverage values are represented by their average.
	 * E. g. for nAverage=32 only 256/32=8 prob values result pre channel and an image is 
	 * represented by only 3*8=24 values. 
	 */
	public void calcProbs() {
		probs = new double[3][256 / nAverage];
		
		for (int i = 0; i < bins.length; i++) {
			int n = 0;
			for (int j = 0; j < bins[i].length; j++) {
				n += bins[i][j];
			}
			for (int j = 0; j < bins[i].length; j+=nAverage) {
				for( int k=0; k<nAverage; k++ ) {
					probs[i][j/nAverage] += (double) bins[i][j+k] / n;
				}
			}
		}

	}

	public Histogram(BufferedImage img, FeatureType type) {
		Raster raster = img.getRaster();
		int height = raster.getHeight();
		int width = raster.getWidth();
		this.type = type;

		// System.out
		// .println(raster.getSampleModel() + " " + raster.getNumBands());
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (type == FeatureType.HSB) {
					int r = raster.getSample(i, j, 0);
					int g = raster.getSample(i, j, 1);
					int b = raster.getSample(i, j, 2);
					float[] hsv = new float[3];
					Color.RGBtoHSB(r, g, b, hsv);
					for (int k = 0; k < 3; k++) {
						++bins[k][(int) (hsv[k] * 255)];
					}
				} else {
					for (int b = 0; b < raster.getNumBands(); b++) {
						bins[b][raster.getSample(i, j, b)]++;
					}
				}
			}
		}
		
		calcProbs();

	}

	public Histogram(BufferedReader br) {
		try {
			refDir = new File(br.readLine());
			fileName = br.readLine();
			type = FeatureType.valueOf(br.readLine());
			int len = Integer.valueOf(br.readLine());
			probs = new double[3][len];
			for (int i = 0; i < probs.length; i++) {
				String s = br.readLine();
				// System.out.println( s );
				Scanner sc = new Scanner(s);
				sc.useLocale(Locale.US);
				for (int j = 0; j < probs[i].length; j++) {
					probs[i][j] = sc.nextDouble();
				}
				sc.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void plot(Plotter plotter) {
		int step = bins[0].length / probs[0].length;
		System.out.println( "Step: " + step);
		
		plotter.setYLine(0);
		
		double max = 0;
		for (int i = 0; i < 3; i++) {
			plotter.setDataColor(color[i]);
			//plotter.setDataLineStyle(LineStyle.HISTOGRAM);
			plotter.add( 0, 0 );
			for (int j = 0; j < probs[i].length; ++j) {
				plotter.add((j+0.5)*step, probs[i][j] / step);
				max = Math.max(max,   probs[i][j] / step );
			}
			plotter.add( bins[i].length, 0 );
			plotter.nextVector();
			//plotter.setDataLineStyle(LineStyle.LINE);
		}
		
		if( step != 1 ) {
			int n = 0;
			for (int j = 0; j < bins[0].length; j++) {
				n += bins[0][j];
			}
			for (int i = 0; i < 3; i++) {
				plotter.setDataColor(color[i]);
				plotter.add( 0, 0 );
				for (int j = 0; j < bins[i].length; ++j) {
					plotter.add(j+0.5, max + (double) bins[i][j] / n);
				}
				plotter.add( bins[i].length, 0 );
				plotter.nextVector();
			}
		
			
		}

	}

	public double euklid_dist(Histogram h2) {
		if (type != h2.type) {
			return Double.MAX_VALUE;
		}
		double d = 0;
		for (int i = 0; i < probs.length; i++) {
			for (int j = 0; j < probs[i].length; j++) {
				double di = probs[i][j] - h2.probs[i][j];
				d += di * di;
			}
			// System.out.println( i + " " + d );
		}
		return Math.sqrt(d);
	}

	public void print(PrintStream out) {
		out.println(refDir);
		out.println(fileName);
		out.println(type);
		out.println(probs[0].length);
		for (double[] prob : probs) {
			for (double w : prob) {
				out.print(w + " ");
			}
			out.println();
		}

	}
}
