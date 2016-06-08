import java.awt.*;
import java.io.Serializable;

public class CommonWall implements Serializable {
	public static final int width = 22; 
	public static final int height = 21;
	int x, y;

	TankClient tc;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] wallImags = null;
	static {
		wallImags = new Image[]{
			tk.getImage("Images/commonWall.gif"),
		};
	}

	public CommonWall(int x, int y, TankClient tc) { 
		this.x = x;
		this.y = y;
		this.tc = tc; 
	}

	public void draw(Graphics g) {
		g.drawImage(wallImags[0], x, y, null);
	}

	public Rectangle getRect() {  
		return new Rectangle(x, y, width, height);
	}
}
