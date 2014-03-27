package simulatorWrapper;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Random;

public class PlasmaPanel extends JPanel {

	private double[][] values;
	private Point[] targets;
	private double rough;
	private double minval;
	private double maxval;
	private Random rnd = new Random();
	private int squareResolution = 50;
	
	private boolean painting = false;
	
	private int currentColorScheme = 0;
	static final int REDtoGREEN = 0, BLACKtoWHITE = 1, BLUEtoWHITE = 2;
	
	private double ColorModifier;
	
	public PlasmaPanel(){
		this.setBounds(0, 0, 100, 100);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				try {
					setSize(values.length*squareResolution, values[0].length*squareResolution);
				}
				catch (Exception e){
					setSize(100, 100);
				}
			}
		});
	}
	
	public double[][] genorateLandscape(int size, double roughFactor){
		double rough = size * roughFactor;
		this.rough = rough;
		double seed = System.currentTimeMillis() / 10000.0;
		while (seed > 30){
			seed = seed / 10.0;
		}
		values = new double[2][2];
		values[0][0] = Math.abs(seed + random());
		values[0][1] = Math.abs(seed + random());
		values[1][0] = Math.abs(seed + random());
		values[1][1] = Math.abs(seed + random());		
		int master = 0;
		while (master <= size){
			values = expand(values);
			int x = 0;
			while (x < values.length){
				int y = 0;
				while (y < values.length){
					if ((x+1) % 2 == 0){
						if ((y+1) % 2 == 0){
							values[x][y] = center(values[x-1][y-1], values[x-1][y+1], values[x+1][y-1], values[x+1][y+1], rough);
						}
						else {
							values[x][y] = midpoint(values[x-1][y], values[x+1][y], rough);
						}
					}
					else {
						if ((y+1) % 2 == 0){
							values[x][y] = midpoint(values[x][y-1], values[x][y+1], rough);
						}
					}
					y++;
				}
				x++;
			}
			rough -= roughFactor;
			master++;
		}				
		double[][] values2 = new double[values.length-4][values.length-4];
		int count = 9;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values.length){
				if (x < 2 || y < 2 || x > values.length - 3 || y > values.length - 3) {}
				else {
					if (count % 4 == 0){
						values2[x-2][y-2] = (values[x][y] + (values[x-1][y-2] + values[x+1][y+2]) / 2) / 2;
					}
					else if (count % 4 == 1){
						values2[x-2][y-2] = (values[x][y] + (values[x-2][y-1] + values[x+2][y+1]) / 2) / 2;
					}
					else if (count % 4 == 2){
						values2[x-2][y-2] = (values[x][y] + (values[x-2][y+1] + values[x+2][y-1]) / 2) / 2;
					}
					else {
						values2[x-2][y-2] = (values[x][y] + (values[x-1][y+2] + values[x+1][y-2]) / 2) / 2;
					}
				}
				count++;
				if (count == Integer.MAX_VALUE){
					count = 0;
				}
				y++;
			}
			x++;
		}
		values = values2;
		minval = getMin();
		maxval = getMax();
		setColorMultipliers();
		this.repaint();
		return values;
	}
	
	public void setValues(double[][] vals){
		values = vals;
		minval = getMin();
		maxval = getMax();
		setColorMultipliers();
		this.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		painting = true;
		try { 
			if (values.length > 0){
				this.setSize(values.length*squareResolution, values[0].length*squareResolution);
				int x = 0;
				while (x < values.length){
					int y = 0;
					while (y < values[0].length){
						try {
							int z = 0;
							while (z < targets.length){
								if ((int)targets[z].getX() == x && (int)targets[z].getY() == y){
									g.setColor(Color.MAGENTA);
									break;
								}
								z++;
							}
							if (z == targets.length){
								z = 1/0; // Force Catch Statement
							}
						}
						catch (Exception e){
							g.setColor(getColor(values[x][y]));
						}
						g.fillRect(x * squareResolution, y * squareResolution, squareResolution, squareResolution);
						switch (currentColorScheme){
						case REDtoGREEN:
							g.setColor(Color.DARK_GRAY);
							break;
						case BLACKtoWHITE:
							g.setColor(new Color(240, 250, 0));
							break;
						case BLUEtoWHITE:
							g.setColor(Color.BLACK);
							break;
						}
						g.drawRect(x * squareResolution, y * squareResolution, squareResolution, squareResolution);
						y++;
					}
					x++;
				}	
			}
		} catch (Exception e) {}
		painting = false;
	}

	public void setTargets(Point[] targs){
		targets = targs;
		this.repaint();
	}
	
	public Point[] getTargets(){
		return targets;
	}
	
	public Point[] genorateTargets(){
		Point[] targets = new Point[rnd.nextInt(10) + 7];
		int x = 0;
		while (x < targets.length){
			targets[x] = new Point(rnd.nextInt(values.length), rnd.nextInt(values.length));
			x++;
		}
		return targets;
	}
	
	private double[][] expand(double[][] in){
		double[][] out = new double[in.length * 2 - 1][in.length * 2 - 1];
		int x = 0;
		while (x < in.length){
			int y = 0;
			while (y < in.length){
				out[x*2][y*2] = in[x][y];
				y++;
			}
			x++;
		}
		return out;
	}
	
	private double center(double a, double b, double c, double d, double rough){
		return ((a+b+c+d)/4 + (rough*random()));
	}
	
	private double midpoint(double a, double b, double rough){
		return ((a+b)/2 + (rough*random()));
	}
	
	private double random(){
		int rough = (int)(this.rough * 10.0);
		while (rough < 1){
			rough *= 10;
		}
		double out = rnd.nextInt(rough) + rnd.nextDouble();
		if (rnd.nextBoolean()){
			out *= -1;
		}
		return out;
	}
	
	private double getMax(){
		double max = 0;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values.length){
				if (values[x][y] > max){
					max = values[x][y];
				}
				y++;
			}
			x++;
		}
		return max;
	}
	
	private double getMin(){
		double min = Integer.MAX_VALUE;
		int x = 0;
		while (x < values.length){
			int y = 0;
			while (y < values.length){
				if (values[x][y] < min){
					min = values[x][y];
				}
				y++;
			}
			x++;
		}
		return min;
	}
	
	private void setColorMultipliers(){
		double x = ((maxval-minval)/2.0+minval);
		ColorModifier = 255 / (x*x - 2*minval*x + minval*minval);
	}
	
	private Color getColor(double numb) {
		switch (currentColorScheme){
		case REDtoGREEN:
			int red = (int)(255 - (ColorModifier*4/3.0)*Math.pow((numb-((maxval-minval)/2.0+minval)), 2));
			if (red < 0){
				red = 0;
			}
			int green = (int)(255 - (ColorModifier*3/4.0)*Math.pow((numb-maxval), 2));
			if (green < 0){
				green = 0;
			}
			int blue = (green - 240) / 2;
			if (blue < 0){
				blue = 0;
			}
			return new Color(red, green, blue);
		case BLACKtoWHITE:
			int x = (int) Math.round((numb - minval) / maxval * 255);
			return new Color(x, x, x);
		case BLUEtoWHITE:
			int y = (int) Math.round((numb - minval) / maxval * 255);
			return new Color(y, y, 255);
		default:
			return null;
		}
	}
	
	public void setColorScheme(int which){
		currentColorScheme = which;
		this.repaint();
	}
	
	public void setResolution(int res){
		if (res > 0){
			squareResolution = res;
			this.repaint();
		}
	}
	
	public int getResolution(){
		return squareResolution;
	}
	
}
