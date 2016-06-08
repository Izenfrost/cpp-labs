import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.Serializable;

public class Home implements Serializable {
	private int x, y;
	private TankClient tc;
	public static final int width = 43, height = 43; 
	private boolean live = true;

	private static Toolkit tk = Toolkit.getDefaultToolkit(); 
	private static Image[] homeImags = null;
	static {
		homeImags = new Image[] {
			tk.getImage("Images/home.png")
		};
	}

	public Home(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc; 
	}

	public void gameOver(Graphics g) {
		tc.homeTank.setLive(false);
		Color c = g.getColor();
		g.setColor(Color.green);
		Font f = g.getFont();
		g.setFont(new Font(" ", Font.PLAIN, 40));
		g.setFont(f);
		g.setColor(c);
	}

	public void draw(Graphics g) {
		for (int i = 0; i < tc.homeWall.size(); i++) {
			CommonWall w = tc.homeWall.get(i);
			w.draw(g);
		}
		if (live) { 
			g.drawImage(homeImags[0], x, y, null);
		} else {
			gameOver(g); 

		}
	}

	public boolean isLive() { 
		return live;
	}

	public void setLive(boolean live) { 
		this.live = live;
	}

	public Rectangle getRect() { 
		return new Rectangle(x, y, width, height);
	}

}
