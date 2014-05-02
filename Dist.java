
/**
 * @author Euler
 *
 */

public class Dist implements Comparable<Object> {
	private double d;
	private int index;


	public Dist(double d) {
		super();
		this.d = d;
	}


	public double getD() {
		return d;
	}


	public void setD(double d) {
		this.d = d;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}
	
	@Override
	public int compareTo(Object arg0) {
		Dist p2 = (Dist) arg0;
		return Double.compare(d, p2.d);
	}
	

}
