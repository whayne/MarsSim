package interfaceGUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class BackgroundImage extends JLabel {

	@Override
	public void paintComponent(Graphics g) {
		try { 
			int x = 0;
			while (super.getIcon().getIconWidth()*x < this.getWidth()){
				int y = 0;
				while (super.getIcon().getIconHeight()*y < this.getHeight()){
					g.drawImage(iconToImage(super.getIcon()), super.getIcon().getIconWidth()*x, super.getIcon().getIconHeight()*y, null);
					y++;
				}
				x++;
			}
		} catch (Exception e) {}
	}
	
	private Image iconToImage(Icon icon) {
		if (icon instanceof ImageIcon) {
			return ((ImageIcon)icon).getImage();
		} 
		else {
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			GraphicsEnvironment ge = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gd.getDefaultConfiguration();
			BufferedImage image = gc.createCompatibleImage(w, h);
			Graphics2D g = image.createGraphics();
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}
	
}
