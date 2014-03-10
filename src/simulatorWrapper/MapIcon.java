package simulatorWrapper;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MapIcon extends JLabel{

	private double Angle = 0;
	
	public void setAngle(double angle){
		Angle = angle % 360;
		try {
			ImageIcon img = new ImageIcon(MapFrame.class.getResource("/Rover Marker " + (int)(Angle - Angle%5) + ".png"));
			img = resize(img, this.getWidth(), this.getHeight());
			super.setIcon(img);
		}
		catch (Exception e){
			super.setIcon(null);
		}
	}
	
	public double getAngle(){
		return Angle;
	}
	
	protected ImageIcon resize(Icon image, int width, int height) throws Exception {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g = bi.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            RenderingHints.VALUE_ANTIALIAS_ON);
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        g.setComposite(comp);
        g.drawImage(((ImageIcon) (image)).getImage(), 0, 0, width, height, null);
	    g.dispose();
	    return new ImageIcon(bi);
	}
	
	@Override
	public void setIcon(Icon img){
		
	}
	
	@Override
	public void setLocation(int x, int y){
		super.setBounds((x - this.getWidth()/2), (y - this.getHeight()/2), this.getWidth(), this.getHeight());
	}
	
	@Override
	public Point getLocation(){
		return new Point((super.getX() + this.getWidth()/2), (super.getY() + this.getWidth()/2));
	}

	@Override
	public void setBounds(int x, int y, int width, int height){
		super.setBounds((x - width/2), (y - height/2), width, height);
		setAngle(Angle);
	}
}
