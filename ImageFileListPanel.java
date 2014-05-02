import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
//Used by JDK 1.2 Beta 4 and all

/**
 * The list of all image files. 
 */

public class ImageFileListPanel extends JPanel implements ActionListener,
		ListSelectionListener {
	private JList<String> list;
	private DefaultListModel listModel;
	private int emptyCount = 0;
	private int popupIndex = -1;
	private int vcCount = 0;
	private CompView compView;
	JPopupMenu popup = new JPopupMenu();
	String[] commands = { "info", "alle löschen" };

	DefaultListModel getListModel() {
		return listModel;
	}

	public ImageFileListPanel(CompView compView) {
		this.compView = compView;

		popup.setLabel("popUp");
		for (String s : commands) {
			JMenuItem m = new JMenuItem(s);
			m.addActionListener(this);
			popup.add(m);
		}

		listModel = new DefaultListModel();

		// Create the list and put it in a scroll pane
		list = new JList<String>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(this);
		// list.setCellRenderer(new MyCellRenderer());
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// int index = list.locationToIndex(e.getPoint());
				System.out.println(e.getPoint());
			}

			public void mouseReleased(MouseEvent event) {
				if (event.isPopupTrigger()) {
					System.out.println("POPUP");
					popupIndex = list.locationToIndex(event.getPoint());
					popup.show(event.getComponent(), event.getX(), event.getY());
				}
			}
		});
		JScrollPane listScrollPane = new JScrollPane(list);
		Dimension dim = new Dimension(160, 600);
		listScrollPane.setPreferredSize(dim);
		listScrollPane.setMinimumSize(dim);

		setLayout(new BorderLayout());

		setMaximumSize(new Dimension(440, Short.MAX_VALUE));
		add(listScrollPane, BorderLayout.CENTER);
	}

	public void append(String file) {
		listModel.addElement(file);
	}

	public void updateIndex(int n) {
		list.setSelectedIndex(n);
	}

	public void moveBottomAfterSelected() {
		listModel.add(list.getSelectedIndex() + 1,
				listModel.remove(listModel.size() - 1 - emptyCount));
	}

	public synchronized void valueChanged(ListSelectionEvent ev) {
		System.out.println("********************************");
		System.out.println("count: " + vcCount++ + " selected: "
				+ list.getSelectedIndex());
		System.out.println("********************************");

		if( list.getSelectedIndex() < 0 ) return;
		
		if (ev.getValueIsAdjusting() == false) {
			System.out.println("ValueChanged: ");
			System.out.println("Size = " + listModel.size());
			try {
				compView.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, "i_"
								+ list.getSelectedIndex()));
			} catch (Exception e) {
				System.out.println("Exception " + e
						+ " occured in valueChanged");
				e.printStackTrace();
			}
		}
	}

	/**
	 * ****************************************************************** Action
	 * handler for popup menu
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		System.out.println(cmd);
		System.out.println(popupIndex);
		if (popupIndex == list.getSelectedIndex()) {
			if (popupIndex < listModel.size() - 1) {
				list.setSelectedIndex(popupIndex + 1);
			} else {
				list.setSelectedIndex(popupIndex - 1);
			}
		}

		if (cmd.equals(commands[0])) { // info
			compView.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, "ib_" + popupIndex ) );
			

		} else if (cmd.equals(commands[1])) { // alle loeschen
			compView.actionPerformed(new ActionEvent(this,
					ActionEvent.ACTION_PERFORMED, compView.clearRefText ) );
		}
	}

	public void clear() {
		listModel.removeAllElements();

	}
}
