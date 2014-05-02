/** *******************************************************
  * Image comparator 
  * 
  *   version  who    when      what 
  *     0.2    se    140502     Writing this header
  *     
  */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import plotter.Graphic;
import plotter.LineStyle;
import plotter.Plotter;

@SuppressWarnings("serial")
public class CompView extends JFrame implements ActionListener {

	static int xsize = 1000;
	static int ysize = 800;
	public static final String clearRefText = "Lösche alle Referenzen";
	private static String version = "0.2 Mai 2014";
	private final String loadRefText = "Lade Referenzen";
	private final String saveHistsText = "Speichere Referenzen-Informationen";
	private final String loadHistsText = "Lade Referenzen-Informationen";
	private final String listHistsText = "Liste";
	private final String n1Text = "keine Mittelung";
	private final String n32Text = "32-er Mittelung";
	private final String distText = "Distanzen";
	private final String testText = "Lade Testdatei";
	private final String rgbText = "RGB";
	private final String hsbText = "HSB";
	private final String aboutText = "Über";

	private String fileOpenDirectory = null;
	private File refDirectory = null;
	private Properties properties = new Properties();
	private String propertieFile = "compview.ini";
	private String saveFileName = "savehists.dat";

	private Plotter plotterTestImage = new Plotter("Histogramm");
	private Plotter plotterBestImage = new Plotter("Histogramm");
	private JLabel testImage = new JLabel();
	private JLabel bestImage = new JLabel();
	private JLabel info = new JLabel("Noch nichts geladen. ");
	private Box cands = new Box(BoxLayout.X_AXIS);
	private Box infos = new Box(BoxLayout.X_AXIS);
	private ImageFileListPanel iflp = new ImageFileListPanel(this);
	private ImageFileFilter imageFileFilter = new ImageFileFilter();
	private JProgressBar progressBar = new JProgressBar(0, 100);

	private FeatureType type = FeatureType.RGB;
	private Histogram testHist = null;
	private List<Histogram> hists = new ArrayList<Histogram>();
	private Dist[] dists = null;

	public CompView() {
		setup("CompView " + version);
	}

	public void setup(String string) {
		System.out.println("creating  CompView");

		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(propertieFile));
			properties.load(stream);
			stream.close();
			fileOpenDirectory = properties.getProperty("testDir");
			String refDirectoryName = properties.getProperty("refDir");
			if (refDirectoryName != null) {
				refDirectory = new File(refDirectoryName);
			}
		} catch (FileNotFoundException e) {
			System.out.println("property file " + propertieFile + " not found");
		} catch (IOException e) {
			e.printStackTrace();
		}

		setName(string);
		setTitle(string);
		// setSize(xsize, ysize);
		setPreferredSize(new Dimension(xsize, ysize));
		Component contents = svCreateComponents();
		getContentPane().add(contents, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		System.out.println("CompView completed ");
	}

	private void loadHists(File refDir) {
		String[] fileNames = refDir.list();

		//System.out.println("in " + refDir);
		//System.out.println(Arrays.toString(fileNames));

		for (int i = 0; i < fileNames.length; i++) {
			String f = fileNames[i];
			File file = new File(refDir, f);
			if (imageFileFilter.accept(file)) {
				if (file.isDirectory()) {
					loadHists(file);
				} else {
					BufferedImage img = loadImage(file);
					Histogram h = new Histogram(img, type);
					h.setFileName(f);
					h.setRefDir(refDir);
					hists.add(h);
					iflp.append(file.getName());
					progressBar.setValue(100 * i / fileNames.length);
				}
			}
		}

	}

	public static BufferedImage loadImage(File file) {
		try {
			BufferedImage img = ImageIO.read( file );
			System.out.println("Image loaded: " + file.getName());
			return img;		
		} catch (IOException e) {
			System.out.println("Image failed: " +  file.getName());
		}			
		return null;
	}

	/**
	 * create all components (buttons, sliders, views, etc) and arrange them
	 */
	public Component svCreateComponents() {

		plotterTestImage.setPreferredSize(600, 250);
		plotterBestImage.setPreferredSize(600, 250);

		testImage.setPreferredSize(new Dimension(600, 300));
		bestImage.setPreferredSize(new Dimension(600, 300));

		initImages();

		JPanel testTab = new JPanel();
		testTab.setLayout(new GridLayout(2, 2));
		testTab.add(testImage);
		testTab.add(plotterTestImage);
		testTab.add(bestImage);
		testTab.add(plotterBestImage);

		JButton[] nbest = new JButton[10];
		for (int n = 0; n < nbest.length; n++) {
			nbest[n] = new JButton("" + (n + 1));
			nbest[n].setActionCommand("c_" + n);
			nbest[n].addActionListener(this);
			cands.add(nbest[n]);
		}
		JButton last = new JButton("last");
		last.setActionCommand("c_last");
		last.addActionListener(this);
		cands.add(last);

		infos.add(info);
		Box left = new Box(BoxLayout.Y_AXIS);
		left.add(testTab);
		left.add(infos);
		left.add(cands);

		Box right = new Box(BoxLayout.Y_AXIS);
		iflp.setPreferredSize(new Dimension(200, 100));
		right.add(iflp);
		right.add(progressBar);

		Box all = new Box(BoxLayout.X_AXIS);
		all.add(left);
		all.add(right);

		info.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		testImage.setHorizontalAlignment(SwingConstants.CENTER);
		bestImage.setHorizontalAlignment(SwingConstants.CENTER);

		// now combine all panes into base pane
		JPanel basePane = new JPanel();
		basePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		basePane.setLayout(new BorderLayout());
		basePane.add("Center", all);

		setJMenuBar(buildMenuBar());

		return basePane;
	}

	private JMenuBar buildMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu refMenu = new JMenu("Referenzen");
		JMenu testMenu = new JMenu("Test");
		JMenu optionMenu = new JMenu("Optionen");
		JMenu helpMenu = new JMenu("Hilfe");
		menuBar.add(refMenu);
		menuBar.add(testMenu);
		menuBar.add(optionMenu);
		menuBar.add(helpMenu);

		JMenuItem mi;
		mi = new JMenuItem(loadRefText);
		mi.addActionListener(this);
		mi.setToolTipText("lade alle Bilddateien aus einem Verzeichnis (inkl. Unterverzeichnissen) Referenzen");
		refMenu.add(mi);

		mi = new JMenuItem(clearRefText);
		mi.addActionListener(this);
		mi.setToolTipText("lösche alle Referenzen aus Liste");
		refMenu.add(mi);

		mi = new JMenuItem(listHistsText);
		mi.addActionListener(this);
		mi.setToolTipText("liste alle Referenzen auf");
		refMenu.add(mi);

		mi = new JMenuItem(saveHistsText);
		mi.addActionListener(this);
		mi.setToolTipText("speichere berechnete Informationen zu allen aktuellen Referenzen in Datei");
		refMenu.add(mi);

		mi = new JMenuItem(loadHistsText);
		mi.addActionListener(this);
		mi.setToolTipText("lädt gespeicherte Informationen zu Referenzen aus Datei");
		refMenu.add(mi);

		mi = new JMenuItem(testText);
		mi.addActionListener(this);
		mi.setToolTipText("Lade Datei");
		testMenu.add(mi);

		mi = new JMenuItem(distText);
		mi.addActionListener(this);
		mi.setToolTipText("zeige alle berechneten Distanzen");
		testMenu.add(mi);

		mi = new JMenuItem(rgbText);
		mi.addActionListener(this);
		mi.setToolTipText("verwende RGB-Farbraum");
		optionMenu.add(mi);

		mi = new JMenuItem(hsbText);
		mi.addActionListener(this);
		mi.setToolTipText("verwende HSB-Farbraum");
		optionMenu.add(mi);

		mi = new JMenuItem(n1Text);
		mi.addActionListener(this);
		mi.setToolTipText("1-er Mittel");
		optionMenu.add(mi);

		mi = new JMenuItem(n32Text);
		mi.addActionListener(this);
		mi.setToolTipText("32-er Mittel");
		optionMenu.add(mi);

		mi = new JMenuItem(aboutText);
		mi.addActionListener(this);
		helpMenu.add(mi);

		return menuBar;
	}

	private void initImages() {
		String imageDir = "images/";
		String initialTestImage = imageDir + "test.png";
		String initialRefImage = imageDir + "best.png";
		BufferedImage img, scaledImage;
		try {
			img = ImageIO.read(new File(initialTestImage));
			scaledImage = scale(img);
			testImage.setIcon(new ImageIcon(scaledImage));

			img = ImageIO.read(new File(initialRefImage));
			scaledImage = scale(img);
			bestImage.setIcon(new ImageIcon(scaledImage));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		System.out.println("cmd: " + cmd);

		if (cmd.startsWith("c_")) {
			if (hists.size() == 0) {
				info.setText("keine Referenzen!");
				return;
			}

			int index;
			if (cmd.equals("c_last")) {
				index = dists.length - 1;
			} else {
				index = Integer.parseInt(cmd.substring(2));
				if (index >= dists.length)
					return;
			}

			int refIndex = dists[index].getIndex();
			Histogram h = showImage(refIndex);
			String infoText = "";
			infoText += (index + 1) + ". Treffer: " + h.getFileName() + " aus "
					+ hists.size() + " Referenzen,  d_" + h.getType() + "= ";
			if (dists[index].getD() == Double.MAX_VALUE)
				infoText += "PASST NICHT";
			else
				infoText += dists[index].getD();
			info.setText(infoText);
			repaint();

		} else if (cmd.startsWith("i_")) {
			int index = Integer.parseInt(cmd.substring(2));
			Histogram h = showImage(index);
			String infoText = "";
			infoText += hists.size() + " Referenzen, ";
			if (dists != null) {
				infoText += (getPos(index) + 1) + ". Treffer: "
						+ h.getFileName() + " aus " + hists.size()
						+ " Referenzen,  d_" + h.getType() + "= ";
				if (dists[getPos(index)].getD() == Double.MAX_VALUE)
					infoText += "PASST NICHT";
				else
					infoText += dists[getPos(index)].getD();
			}
			info.setText(infoText);
			repaint();

		} else if (cmd.startsWith("ib_")) {
			int index = Integer.parseInt(cmd.substring(3));
			JOptionPane.showMessageDialog(null, infotext(index), "Datei-Info",
					JOptionPane.INFORMATION_MESSAGE);

		} else if (cmd.equals(aboutText)) {
			String aboutInfo = "Bildvergleicher, S. Euler, TH Mittelhessen";
			JOptionPane.showMessageDialog(null, aboutInfo, "Über CompView",
					JOptionPane.INFORMATION_MESSAGE);

		} else if (cmd.equals(rgbText)) {
			type = FeatureType.RGB;
			info.setText("umgeschaltet auf " + type);

		} else if (cmd.equals(hsbText)) {
			type = FeatureType.HSB;
			info.setText("umgeschaltet auf " + type);

		} else if (cmd.equals(n1Text)) {
			Histogram.setnAverage(1);
			String aboutInfo = "Keine Mittelung der Histogramm-Werte, alte Referenzen bleiben unverändert";
			JOptionPane.showMessageDialog(null, aboutInfo, "Histogramm-Mittelung",
					JOptionPane.INFORMATION_MESSAGE);
			
		} else if (cmd.equals(n32Text)) {
			Histogram.setnAverage(32);
			String aboutInfo =  "Mittelung über 32 Histogramm-Werte, alte Referenzen bleiben unverändert";
			JOptionPane.showMessageDialog(null, aboutInfo, "Histogramm-Mittelung",
					JOptionPane.INFORMATION_MESSAGE);
			
		
		} else if (cmd.equals(distText)) {
			Graphic gd = new Graphic("Distanzen");
			Plotter pd = gd.getPlotter();
			pd.setDataLineStyle(LineStyle.HISTOGRAM);
			pd.setAutoYgrid(1);
			pd.setYLabelFormat("%.1f");
			for (Dist d : dists) {
				if (d.getD() == Double.MAX_VALUE)
					pd.add(-0.2);
				else
					pd.add(d.getD());
			}
			gd.repaint();

		} else if (cmd.equals(clearRefText)) {
			hists.clear();
			info.setText("alle Referenzen gelöscht");
			iflp.clear();
			bestImage.setIcon(null);
			plotterBestImage.removeAllDataObjects();
			repaint();

		} else if (cmd.equals(listHistsText)) {
			for (Histogram h : hists) {
				System.out.println(h.getRefDir() + " -> " + h.getFileName()
						+ "  " + h.getType());
			}

		} else if (cmd.equals(saveHistsText)) {
			try {
				PrintStream ps = new PrintStream(saveFileName);
				ps.println(hists.size());
				for (Histogram h : hists) {
					h.print(ps);
				}
				ps.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

		} else if (cmd.equals(loadHistsText)) {
			try {
				// Scanner sc = new Scanner(new File(saveFileName));
				// sc.useLocale(Locale.US);
				BufferedReader br = new BufferedReader(new FileReader(
						saveFileName));
				int n = Integer.parseInt(br.readLine());
				System.out.println(n + " hists in file " + saveFileName);
				for (int i = 0; i < n; i++) {
					Histogram h = new Histogram(br);
					hists.add(h);
					iflp.append(h.getFileName());
				}
				info.setText(refInfo());
				if (testHist != null) {
					calcDists();
					actionPerformed(new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED, "c_0"));
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}

		} else if (cmd.equals(loadRefText)) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setCurrentDirectory(refDirectory);
			int option = chooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File refDir = chooser.getSelectedFile();
				System.out.println(refDir);
				if (refDir == null)
					return;

				progressBar.setValue(0);
				Task task = new Task(refDir);
				task.execute();

				refDirectory = refDir;
				String p = chooser.getCurrentDirectory().getAbsolutePath();
				properties.setProperty("refDir", p);
				try {
					properties.store(new FileWriter(propertieFile), "");
				} catch (IOException e) {
					e.printStackTrace();
				}

			}

		} else if (cmd.equals(testText)) {
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(imageFileFilter);
			if (fileOpenDirectory != null) {
				chooser.setCurrentDirectory(new File(fileOpenDirectory));
			}
			int option = chooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				if (file == null)
					return;
				fileOpenDirectory = chooser.getCurrentDirectory()
						.getAbsolutePath();
				properties.setProperty("testDir", fileOpenDirectory);
				try {
					properties.store(new FileWriter(propertieFile), "");
					BufferedImage img = ImageIO.read(file);
					BufferedImage scaledImage = scale(img);
					testImage.setIcon(new ImageIcon(scaledImage));

					testHist = new Histogram(img, type);

					plotterTestImage.removeAllDataObjects();
					plotterBestImage.removeAllDataObjects();

					testHist.plot(plotterTestImage);

					calcDists();
					actionPerformed(new ActionEvent(this,
							ActionEvent.ACTION_PERFORMED, "c_0"));

					// pack();
					repaint();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private String infotext(int index) {
		String info = "";
		info += hists.get(index).getFileName();
		info += "\n";
		info += hists.get(index).getRefDir();
		info += "\n";
		info += hists.get(index).getType();
		return info;
	}

	private void calcDists() {
		dists = new Dist[hists.size()];
		for (int i = 0; i < dists.length; i++) {
			dists[i] = new Dist(testHist.euklid_dist(hists.get(i)));
			dists[i].setIndex(i);
		}
		Arrays.sort(dists);

	}

	private int getPos(int index) {
		for (int n = 0; n < dists.length; n++) {
			if (index == dists[n].getIndex()) {
				return n;
			}
		}
		return 0;
	}

	private Histogram showImage(int refIndex) {
		plotterBestImage.removeAllDataObjects();
		Histogram h = hists.get(refIndex);
		h.plot(plotterBestImage);
		BufferedImage img;
		try {
			img = ImageIO.read(new File(h.getRefDir(), h.getFileName()));
			BufferedImage scaledImage = scale(img);
			bestImage.setIcon(new ImageIcon(scaledImage));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return h;

	}

	private String refInfo() {
		String infoText = "Insgesamt " + hists.size() + " Referenzen geladen ";
		return infoText;
	}

	private BufferedImage scale(BufferedImage img) {
		int scaleX = 400;
		int scaleY = 300;
		if (img.getWidth() > img.getHeight()) {
			double fak = (double) scaleX / img.getWidth();
			scaleY = (int) (img.getHeight() * fak);
		} else {
			double fak = (double) scaleY / img.getHeight();
			scaleX = (int) (img.getWidth() * fak);
		}
		Image image = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(scaleX, scaleY,
				BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(image, 0, 0, null);
		return buffered;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CompView();
	}

	/**
	 * Define custom file filter for acceptable image files.
	 */
	private static class ImageFileFilter extends
			javax.swing.filechooser.FileFilter {

		public boolean accept(java.io.File file) {
			if (file == null)
				return false;
			return file.isDirectory()
					|| file.getName().toLowerCase().endsWith(".gif")
					|| file.getName().toLowerCase().endsWith(".png")
					|| file.getName().toLowerCase().endsWith(".jpg");
		}

		public String getDescription() {
			return "Image files (*.gif, *.png, *.jpg)";
		}

	}

	class Task extends SwingWorker<Void, Void> {
		private File refDir;

		public Task(File refDir) {
			this.refDir = refDir;
		}

		@Override
		public Void doInBackground() {
			loadHists(refDir);

			info.setText(refInfo());
			if (testHist != null) {
				calcDists();
				actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, "c_0"));
			}

			return null;
		}

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			// progressMonitor.setProgress(0);
			progressBar.setValue(100);
		}
	}

}
