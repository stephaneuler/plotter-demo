# plotter-demo
This project contains some  demo applications based on the Java Plotter. In the project plotter http://wan15.mnd.thm.de/plotter/index.php (currently only in German) a set of Java classes for plotting data has been developed. They are mainly intended for beginners. As an example the few lines
```
Plotter plotter = new Graphic("Sinus").getPlotter();

for (double x = 0; x <= 2*Math.PI; x += 0.05) {
	plotter.add( x, Math.sin(x));
}
```
are all you need for drawing the following graph of sin(x):

![http://wan15.mnd.thm.de/plotter/bilder/sin0.jpg](http://wan15.mnd.thm.de/plotter/bilder/sin0.jpg)
