import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;
import plotter.Sleep;

/**
 * Visualization of the recursive calculation of the Fibonacci numbers. 
 * Two versions - with and without memoization - are called. 
 * 
 * @author  Stephan Euler
 * @version June 2015
 */
public class FiboRec {

	static Graphic graphic;
	static Plotter plotter;
	static Map<Integer, Long> memory = new HashMap<Integer, Long>();

	static {
		graphic = new Graphic("Fibonacci rekursiv");
		plotter = graphic.getPlotter();
		plotter.setDataLineStyle(LineStyle.VALUE);
		plotter.setDataColor(Color.BLUE);
		memory.put( 0, 0l );
		memory.put( 1, 1l );
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 10;
		
		plotter.setStatusLine("call trace for fibo(" + n + ")");
		fibo( n);
		plotter.nextVector();
		plotter.setDataColor(Color.BLACK);
		fiboMem( n );

	}

	public static long fibo(int n) {
		plotter.add(n);
		Sleep.sleep(30);
		plotter.repaint();
		if (n == 1 | n == 2)
			return 1;
		return fibo(n - 1) + fibo(n - 2);
	}

	public static long fiboMem(int n) {
		plotter.add(n);
		Sleep.sleep(30);
		plotter.repaint();
		if( ! memory.containsKey(n) ) {
			memory.put(n, fiboMem(n - 1) + fiboMem(n - 2));
		}
		return memory.get(n);
	}

}
