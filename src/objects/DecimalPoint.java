package objects;

import java.awt.Point;

public class DecimalPoint {

	private double x, y;
	
	public DecimalPoint(){
		x = 0;
		y = 0;
	}
	
	public DecimalPoint(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public DecimalPoint(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public int roundX(){
		return (int)Math.round(x);
	}
	
	public int roundY(){
		return (int)Math.round(y);
	}
	
	public DecimalPoint offset(double x, double y){
		return new DecimalPoint(this.x+x, this.y+y);
	}
	
	public DecimalPoint scale(double factor){
		return new DecimalPoint(x*factor, y*factor);
	}
	
	public Point toPoint(){
		return new Point(roundX(), roundY());
	}
	
}
