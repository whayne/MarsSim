package interfaceGUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;

public class ImageButton extends JLabel {

	protected boolean working = false;
	private ThreadTimer animation;
	private ImageIcon Image;
	private ImageIcon hoverImage;
	private int Margin = 10;
	
	public ImageButton(){
		this.setBorder(new LineBorder(Color.GRAY));
		this.setCursor(new Cursor(Cursor.HAND_CURSOR));
		this.setVerticalAlignment(CENTER);
		this.setHorizontalAlignment(CENTER);
		this.setOpaque(true);
		this.addMouseListener(new MouseAdapter() {			
			@Override
			public void mouseClicked(MouseEvent arg0){
				try{
					hoverImage.equals(Image);
				} catch (Exception e) {
					if (isEnabled()){
						RunAnimation();
					}
				}
			}
			@Override
			public void mouseEntered(MouseEvent arg1){
				startHover();
			}
			@Override
			public void mouseExited(MouseEvent arg2){
				endHover();
			}
		});
	}
	
	private void RunAnimation(){
		setBackground(new Color(200, 200, 200));
		animation = new ThreadTimer(500, new Runnable(){
			public void run(){
				setBackground(new Color(240, 240, 240));
			}
		}, 1);
	}
	
	public void setImage(ImageIcon img){
		Image = img;
		try {
			setIcon(Image);
			updateImage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Icon getImage(){
		return Image;
	}
	
	public void setHoverImage(ImageIcon img){
		hoverImage = img;
	}
	
	public ImageIcon getHoverImage(){
		return hoverImage;
	}
	
	@Override
	public void setBounds(int x, int y, int width, int height){
		super.setBounds(x, y, width, height);
		updateImage();
	}
	
	protected void updateImage(){
		try {
			super.setIcon(resize(getIcon(), this.getWidth() - Margin, this.getHeight() - Margin));
		} catch (Exception e) {}
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
	
	private void startHover(){
		try {
			hoverImage.equals(Image);
			setIcon(resize(hoverImage, getWidth() - Margin, getHeight() - Margin));
		} catch (Exception e) {}
	}
	
	private void endHover(){
		try {
			setIcon(resize(Image, getWidth() - Margin, getHeight() - Margin));
		} catch (Exception e) {}
	}
	
	public int getLeft(){
		return (int) (super.getLocation().getX() + getWidth());
	}
	
	public int getBottom(){
		return (int) (super.getLocation().getY() + getHeight());
	}
	
	public void setMargin(int marg){
		if (marg >= 0){
			Margin = marg;
		}
	}
	
	public int getMargin(){
		return Margin;
	}
}
