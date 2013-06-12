package demos;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.WindowConstants;

import plotter.DataObject;
import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;
import plotter.Point;

/**
 * 
 * Beschreibung
 * 
 * @version 1.0 vom 08.12.2010
 * @author
 */

public class Mouse implements MouseListener {
	Graphic graphic = new Graphic("Mouse");
	Plotter plotter = graphic.getPlotter();
	
	Mouse() {
		graphic.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		//plotter.setRange( 0, 1);
		plotter.setDataLineStyle(LineStyle.BOTH);
		plotter.setSymbolSize(5);
		plotter.setStatusLine("click: left: new point, right: delete , shift-right: move to");
		
		plotter.add("hidden", -1., -1 );
		plotter.add("hidden", 1., 1 );
		plotter.setDataLineStyle("hidden", LineStyle.HIDDEN);
		plotter.addMouseListener(this);
	}

	public static void main(String[] args) {
		Mouse m = new Mouse();
		m.graphic.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println(e);
		int x, y;
		x = e.getX();
		y = e.getY();
		System.out.println("Mouse released at " + x + ", " + y);
		System.out.println("# of clicks: " + e.getClickCount());
		System.out.println("# button: " + e.getButton() );
		
		double wx = plotter.scaleXR(x);
		double wy = plotter.scaleYR(y);
		System.out.println("in world coordinates: " + wx + ", " + wy);
		
		DataObject dO = plotter.getDataSet();
		
		if( e.getButton() == 1 ) {
			plotter.add( wx, wy );
			plotter.getDataSet().sort();
		} else {
			Point next = dO.findNext( wx, wy );
			System.out.println("neigbour: " + next.getX() + ", " + next.getY());
			dO.remove(next);
		    if( (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK  )== InputEvent.SHIFT_DOWN_MASK ) {
				plotter.add( wx, wy );
				plotter.getDataSet().sort();		         
		    }
		}
		graphic.repaint();

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
