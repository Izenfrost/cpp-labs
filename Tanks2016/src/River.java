import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.Serializable;


public class River implements Serializable {
	public static final int riverWidth = 55;
	public static final int riverHeight = 154;
	private int x, y;
	TankClient tc ;
	
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] riverImags = null;
	
	static {   
		riverImags = new Image[]{
				tk.getImage("Images/river.jpg"),
		};
	}
	
	
	public River(int x, int y, TankClient tc) {   
		this.x = x;
		this.y = y;
		this.tc = tc;           
	}
	
	public void draw(Graphics g) {
		g.drawImage(riverImags[0],x, y, null);          
	}
	public static int getRiverWidth() {
		return riverWidth;
	}

	public static int getRiverLength() {
		return riverHeight;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	public Rectangle getRect() {
		return new Rectangle(x, y, riverWidth, riverHeight);
	}


}
