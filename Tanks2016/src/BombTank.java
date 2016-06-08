import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.Serializable;

public class BombTank implements Serializable {
	private int x, y;
	private boolean live = true; 
	private TankClient tc;
	private static Toolkit tk = Toolkit.getDefaultToolkit();

	private static Image[] imgs = {
			tk.getImage(
					"images/1.gif"),
			tk.getImage(
					"images/2.gif"),
			tk.getImage(
					"images/3.gif"),
			tk.getImage(
					"images/4.gif"),
			tk.getImage(
					"images/5.gif"),
			tk.getImage(
					"images/6.gif"),
			tk.getImage(
					"images/7.gif"),
			tk.getImage(
					"images/8.gif"),
			tk.getImage(
					"images/9.gif"),
			tk.getImage(
					"images/10.gif"), };
	int step = 0;

	public BombTank(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}

	public void draw(Graphics g) { 

		if (!live) { 
			tc.bombTanks.remove(this);
			return;
		}
		if (step == imgs.length) {
			live = false;
			step = 0;
			return;
		}

		g.drawImage(imgs[step], x, y, null);
		step++;
	}
}
