package demos;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Random;

import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;

public class PiRegen {

	public static void main(String[] args) {
		int anzahl = 30000;
		Graphic graphic = new Graphic("Zufallsregen");
		graphic.setBounds(new Rectangle(0,0,500,500));
		Plotter plotter = graphic.getPlotter();
		plotter.setXrange(0, 1);
		plotter.setYrange(0, 1);
		
		plotter.setDataLineStyle(LineStyle.DOT);
		plotter.setDataColor("in", Color.RED);

		Random random = new Random();
		int drin = 0;
		for (int n = 0; n < anzahl ; n++) {
			double x = random.nextFloat();
			double y = random.nextFloat();
			if (x * x + y * y < 1) {
				plotter.add("in", x, y);
				++drin;
			} else {
//				plotter.add("out", x, y);
			}
			if( n % 1000 == 0 & n > 0) {
				System.out.println( n + " " + 4. * drin / n );
			}
		}
	}
}
