import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.Serializable;

public class Tree implements Serializable {
	public static final int width = 30;
	public static final int height = 30;
	int x, y;
	TankClient tc ;
	private static Toolkit tk = Toolkit.getDefaultToolkit();
	private static Image[] treeImags = null;
	static {
		treeImags = new Image[]{
				tk.getImage("Images/tree.gif"),
		};
	}
	
	
	public Tree(int x, int y, TankClient tc) { 
		this.x = x;
		this.y = y;
		this.tc = tc;
	}
	
	public void draw(Graphics g) {        
		g.drawImage(treeImags[0],x, y, null);
	}
	
}
