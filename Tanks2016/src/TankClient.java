import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TankClient extends Frame implements ActionListener, Serializable {

	private static final long serialVersionUID = 1L;
	private int i;
	public static boolean replay = false;
	public static boolean bot = false;
	public static final int Fram_width = 800;
	public static final int Fram_height = 600;
	public static boolean printable = true;
	public int enemyLifes = 20;
	MenuBar jmb = null;
	Menu jm1 = null, jm2 = null, jm3 = null, jm4 = null, jm5 = null;
	MenuItem jmi1 = null, jmi2 = null, jmi3 = null, jmi4 = null, jmi5 = null,
			jmi6 = null, jmi7 = null, jmi8 = null, jmi9 = null, jmi10 = null, jmi11 = null, jmi12 = null, jmi13 = null, jmi14 = null;
	Image screenImage = null;

	Tank homeTank = new Tank(300, 560, true, Direction.STOP, this, 1);
	Tank homeTank2 = new Tank(449, 560, true, Direction.STOP, this, 2);
	Boolean Player2 = false;
	Home home = new Home(373, 557, this);
	Boolean win = false, lose = false;
	List<River> theRiver = new ArrayList<River>();
	List<Tank> tanks = new LinkedList<Tank>();
	List<BombTank> bombTanks = new ArrayList<BombTank>();
	List<Bullets> bullets = new ArrayList<Bullets>();
	List<Tree> trees = new ArrayList<Tree>();
	List<CommonWall> homeWall = new ArrayList<CommonWall>();
	List<CommonWall> otherWall = new ArrayList<CommonWall>();
	List<MetalWall> metalWall = new ArrayList<MetalWall>();
	LinkedList<LinkedList> totalStates = new LinkedList<LinkedList>();
	LinkedList<GameState> playerStates = new LinkedList<GameState>();
	LinkedList<GameState> enemyStates = new LinkedList<GameState>();
	private ReplayThread replayThread;
	GameState nextPlayer, nextEnemy;
	Iterator<GameState> iteratorPlayer;
	Iterator<GameState> iteratorEnemy;
	public static File fileDes;
	public GameStatistics statistic;

	public void update(Graphics g) {
		screenImage = this.createImage(Fram_width, Fram_height);

		Graphics gps = screenImage.getGraphics();
		Color c = gps.getColor();
		gps.setColor(Color.GRAY);
		gps.fillRect(0, 0, Fram_width, Fram_height);
		gps.setColor(c);
		framPaint(gps);
		g.drawImage(screenImage, 0, 0, null);
	}


	public void framPaint(Graphics g) {

		if (!replay) {
			playerStates.add(new GameState(homeTank.getX(), homeTank.getY(), homeTank.getShot(), homeTank.getDirection()));
			for (Tank tank : tanks) {
				enemyStates.add(new GameState(tank.getX(), tank.getY(), tank.getShot(), tank.getDirection()));
			}
			totalStates.add(playerStates);
			totalStates.add(enemyStates);
		}

		Color c = g.getColor();
		g.setColor(Color.green);

		Font f1 = g.getFont();
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		if (!Player2) g.drawString("Tanks left in the field: ", 200, 70);
		else g.drawString("Tanks left in the field: ", 100, 70);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		if (!Player2) g.drawString("" + enemyLifes, 400, 70);
		else g.drawString("" + enemyLifes, 300, 70);
		g.setFont(new Font("Times New Roman", Font.BOLD, 20));
		if (!Player2) g.drawString("Health: ", 580, 70);
		else g.drawString("Health: ", 380, 70);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 30));
		if (!Player2) g.drawString("" + homeTank.getLife(), 650, 70);
		else g.drawString("Player1: " + homeTank.getLife() + "    Player2:" + homeTank2.getLife(), 450, 70);
		g.setFont(f1);
		if (!Player2) {
			if (enemyLifes == 0 && home.isLive() && homeTank.isLive() && lose == false) {
				Font f = g.getFont();
				g.setFont(new Font("Times New Roman", Font.BOLD, 60));
				g.drawString("Congratulations! ", 200, 300);
				statistic.wins++;
				g.setFont(f);
				win = true;
			}

			if (!homeTank.isLive() && !win) {
				Font f = g.getFont();
				try {
					Font font = Font.createFont(Font.TRUETYPE_FONT, new File("font.ttf"));
					font = font.deriveFont(Font.PLAIN, 60);
					g.setFont(font);
					g.drawString("You lose", 200, 300);
				} catch (FontFormatException | IOException e) {
					e.printStackTrace();
				}
				statistic.loses++;
				lose = true;
				g.setFont(f);
			}
		} else {
			if (enemyLifes == 0 && home.isLive() && (homeTank.isLive() || homeTank2.isLive()) && !lose) {
				Font f = g.getFont();
				g.setFont(new Font("Times New Roman", Font.BOLD, 60));
				g.drawString("Congratulations! ", 200, 300);
				g.setFont(f);
				statistic.wins++;
				win = true;
			}

			if (!homeTank.isLive() && !homeTank2.isLive() && !win) {
				Font f = g.getFont();
				g.setFont(new Font("Qix (large)", Font.PLAIN, 40));
				g.drawString("Sorry. You lose!", 200, 300);
				System.out.println("2");
				g.setFont(f);
				statistic.loses++;
				lose = true;
			}
		}
		g.setColor(c);

		for (int i = 0; i < theRiver.size(); i++) {
			River r = theRiver.get(i);
			r.draw(g);
		}

		for (int i = 0; i < theRiver.size(); i++) {
			River r = theRiver.get(i);
			homeTank.collideRiver(r);
			if (Player2) homeTank2.collideRiver(r);
		}

		home.draw(g);
		homeTank.draw(g);
		if (Player2) {
			homeTank2.draw(g);
		}

		for (int i = 0; i < bullets.size(); i++) {
			Bullets m = bullets.get(i);
			m.hitTanks(tanks);
			m.hitTank(homeTank);
			m.hitTank(homeTank2);
			m.hitHome();
			for (int j = 0; j < bullets.size(); j++) {
				if (i == j) continue;
				Bullets bts = bullets.get(j);
				m.hitBullet(bts);
			}
			for (int j = 0; j < metalWall.size(); j++) {
				MetalWall mw = metalWall.get(j);
				m.hitWall(mw);
			}

			for (int j = 0; j < otherWall.size(); j++) {
				CommonWall w = otherWall.get(j);
				m.hitWall(w);
			}

			for (int j = 0; j < homeWall.size(); j++) {
				CommonWall cw = homeWall.get(j);
				m.hitWall(cw);
			}
			m.draw(g);
		}

		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);

			for (int j = 0; j < homeWall.size(); j++) {
				CommonWall cw = homeWall.get(j);
				t.collideWithWall(cw);
			}
			for (int j = 0; j < otherWall.size(); j++) {
				CommonWall cw = otherWall.get(j);
				t.collideWithWall(cw);
			}
			for (int j = 0; j < metalWall.size(); j++) {
				MetalWall mw = metalWall.get(j);
				t.collideWithWall(mw);
			}
			for (int j = 0; j < theRiver.size(); j++) {
				River r = theRiver.get(j);
				t.collideRiver(r);
			}

			t.collideWithTanks(tanks);
			t.collideHome(home);
			if (t.isLive())
				t.draw(g);
		}

		for (int i = 0; i < trees.size(); i++) {
			Tree tr = trees.get(i);
			tr.draw(g);
		}

		for (int i = 0; i < bombTanks.size(); i++) {
			BombTank bt = bombTanks.get(i);
			bt.draw(g);
		}

		for (int i = 0; i < otherWall.size(); i++) {
			CommonWall cw = otherWall.get(i);
			cw.draw(g);
		}

		for (int i = 0; i < metalWall.size(); i++) {
			MetalWall mw = metalWall.get(i);
			mw.draw(g);
		}

		homeTank.collideWithTanks(tanks);
		homeTank.collideHome(home);
		if (Player2) {
			homeTank2.collideWithTanks(tanks);
			homeTank2.collideHome(home);
		}

		for (int i = 0; i < metalWall.size(); i++) {
			MetalWall w = metalWall.get(i);
			homeTank.collideWithWall(w);
			if (Player2) homeTank2.collideWithWall(w);
		}

		for (int i = 0; i < otherWall.size(); i++) {
			CommonWall cw = otherWall.get(i);
			homeTank.collideWithWall(cw);
			if (Player2) homeTank2.collideWithWall(cw);
		}

		for (int i = 0; i < homeWall.size(); i++) {
			CommonWall w = homeWall.get(i);
			homeTank.collideWithWall(w);
			if (Player2) homeTank2.collideWithWall(w);
		}

		if (win || lose) {
			printable = false;
		}
		try {
			if (replay && printable) {
				nextPlayer = iteratorPlayer.next();
				homeTank.setX(nextPlayer.x);
				homeTank.setY(nextPlayer.y);
				if (nextPlayer.shot) {
					homeTank.fire();
				}
				homeTank.setDirection(nextPlayer.direction);
				for (Tank tank : tanks) {
					nextEnemy = iteratorEnemy.next();
					tank.setX(nextEnemy.x);
					tank.setY(nextEnemy.y);
					if (nextEnemy.shot) {
						tank.fire();
					}
					tank.setDirection(nextEnemy.direction);

				}
			}
		} catch (NoSuchElementException ignored) {
		}
	}

	public TankClient() {
		this.i = 0;

		jmb = new MenuBar();
		jm1 = new Menu("Game");
		jm2 = new Menu("Pause/Continue");
		jm3 = new Menu("Help");
		jm4 = new Menu("Level");
		jm5 = new Menu("Addition");
		jm1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm2.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm3.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm4.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jm5.setFont(new Font("Times New Roman", Font.BOLD, 15));

		jmi1 = new MenuItem("New Game");
		jmi2 = new MenuItem("Exit");
		jmi3 = new MenuItem("Stop");
		jmi4 = new MenuItem("Continue");
		jmi5 = new MenuItem("Help");
		jmi6 = new MenuItem("Level 1");
		jmi7 = new MenuItem("Level 2");
		jmi8 = new MenuItem("Level 3");
		jmi9 = new MenuItem("Level 4");
		jmi10 = new MenuItem("Add Player 2");
		jmi11 = new MenuItem("Replay");
		jmi12 = new MenuItem("Bot");
		jmi13 = new MenuItem("Sort");
		jmi14 = new MenuItem("Pseudocode");
		jmi1.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi2.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi3.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi4.setFont(new Font("Times New Roman", Font.BOLD, 15));
		jmi5.setFont(new Font("Times New Roman", Font.BOLD, 15));

		jm1.add(jmi1);
		jm1.add(jmi11);
		jm1.add(jmi12);
		jm1.add(jmi13);
		jm1.add(jmi14);
		jm1.add(jmi2);
		jm2.add(jmi3);
		jm2.add(jmi4);
		jm3.add(jmi5);
		jm4.add(jmi6);
		jm4.add(jmi7);
		jm4.add(jmi8);
		jm4.add(jmi9);
		jm5.add(jmi10);

		jmb.add(jm1);
		jmb.add(jm2);

		jmb.add(jm4);
		jmb.add(jm5);
		jmb.add(jm3);


		jmi1.addActionListener(this);
		jmi1.setActionCommand("NewGame");
		jmi2.addActionListener(this);
		jmi2.setActionCommand("Exit");
		jmi3.addActionListener(this);
		jmi3.setActionCommand("Stop");
		jmi4.addActionListener(this);
		jmi4.setActionCommand("Continue");
		jmi5.addActionListener(this);
		jmi5.setActionCommand("help");
		jmi6.addActionListener(this);
		jmi6.setActionCommand("Level 1");
		jmi7.addActionListener(this);
		jmi7.setActionCommand("Level 2");
		jmi8.addActionListener(this);
		jmi8.setActionCommand("Level 3");
		jmi9.addActionListener(this);
		jmi9.setActionCommand("Level 4");
		jmi10.addActionListener(this);
		jmi10.setActionCommand("Player2");
		jmi11.addActionListener(this);
		jmi11.setActionCommand("Replay");
		jmi12.addActionListener(this);
		jmi12.setActionCommand("Bot");
		jmi13.addActionListener(this);
		jmi13.setActionCommand("Sort");
		jmi14.addActionListener(this);
		jmi14.setActionCommand("Pseudocode");

		if (replay) {
			replayThread = new ReplayThread(fileDes);
			replayThread.run();
			totalStates = replayThread.getTotalState();
			playerStates = totalStates.getFirst();
			enemyStates = totalStates.getLast();
			iteratorPlayer = playerStates.iterator();
			iteratorEnemy = enemyStates.iterator();
		}

		this.setMenuBar(jmb);
		this.setVisible(true);

		for (int i = 0; i < 10; i++) {
			if (i < 4)
				homeWall.add(new CommonWall(350, 580 - 21 * i, this));
			else if (i < 7)
				homeWall.add(new CommonWall(372 + 22 * (i - 4), 517, this));
			else
				homeWall.add(new CommonWall(416, 538 + (i - 7) * 21, this));

		}

		for (int i = 0; i < 32; i++) {
			if (i < 16) {
				otherWall.add(new CommonWall(200 + 21 * i, 300, this));
				otherWall.add(new CommonWall(500 + 21 * i, 180, this));
				otherWall.add(new CommonWall(200, 400 + 21 * i, this));
				otherWall.add(new CommonWall(500, 400 + 21 * i, this));
			} else if (i < 32) {
				otherWall.add(new CommonWall(200 + 21 * (i - 16), 320, this));
				otherWall.add(new CommonWall(500 + 21 * (i - 16), 220, this));
				otherWall.add(new CommonWall(222, 400 + 21 * (i - 16), this));
				otherWall.add(new CommonWall(522, 400 + 21 * (i - 16), this));
			}
		}

		for (int i = 0; i < 20; i++) {
			if (i < 10) {
				metalWall.add(new MetalWall(140 + 30 * i, 150, this));
				metalWall.add(new MetalWall(600, 400 + 20 * (i), this));
			} else if (i < 20)
				metalWall.add(new MetalWall(140 + 30 * (i - 10), 180, this));

		}

		for (int i = 0; i < 4; i++) {
			if (i < 4) {
				trees.add(new Tree(0 + 30 * i, 360, this));
				trees.add(new Tree(220 + 30 * i, 360, this));
				trees.add(new Tree(440 + 30 * i, 360, this));
				trees.add(new Tree(660 + 30 * i, 360, this));
			}

		}

		theRiver.add(new River(85, 100, this));

		for (int i = 0; i < 20; i++) {
			if (i < 9)
				tanks.add(new Tank(150 + 70 * i, 40, false, Direction.D, this, 0));
			else if (i < 15)
				tanks.add(new Tank(700, 140 + 50 * (i - 6), false, Direction.D, this, 0));
			else
				tanks.add(new Tank(10, 50 * (i - 12), false, Direction.D, this, 0));
		}

		this.setSize(Fram_width, Fram_height);
		this.setLocation(280, 50);
		this.setTitle("Tanks 2016");

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (!replay && (win || lose)) {
					replayThread = new ReplayThread(totalStates);
					replayThread.run();
				}
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		this.setVisible(true);

		statistic = new GameStatistics();

		if (!bot) this.addKeyListener(new KeyMonitor());
		new Thread(new PaintThread()).start();
	}

	public static void main(String[] args) {
		new TankClient();
	}

	private class PaintThread implements Runnable {
		public void run() {
			while (printable) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class ReplayThread implements Runnable {
		private LinkedList list = new LinkedList<GameState>();
		private File fileDes;
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH-mm-ss_dd-MM-yyyy");

		ReplayThread(LinkedList<LinkedList> list) {
			super();
			this.list = list;
		}

		ReplayThread(File fileDes) {
			super();
			this.fileDes = fileDes;
		}

		private void write() {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./saves/" + dateFormat.format(date)))) {
				oos.writeObject(this.list);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void read() {
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileDes))) {
				this.list = (LinkedList<LinkedList>) ois.readObject();
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		public LinkedList<LinkedList> getTotalState() {
			return list;
		}

		public void run() {
			if (replay) {
				read();
			} else if (!replay) {
				write();
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	private void Pseudocode(LinkedList<GameState> playerState){
		Direction prevDirection = Direction.STOP;
		for(GameState player: playerState) {
					if (player.direction != prevDirection) switch(player.direction.ordinal()) {
						case 0: {System.out.println("Игрок движется вниз"); break;}
						case 1: {System.out.println("Игрок движется влево"); break;}
						case 2: { System.out.println("Игрок движется вправо"); break;}
						case 3: { System.out.println("Игрок движется вверх"); break;}
						case 4: { System.out.println("Игрок остановился"); break;}
					}
			if(player.shot) System.out.println("Игрок выстрелил");
			prevDirection = player.direction;
		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			homeTank.keyReleased(e);
			homeTank2.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			homeTank.keyPressed(e);
			homeTank2.keyPressed(e);
		}

	}

	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("NewGame")) {
			printable = false;
			Object[] options = {"Confirm", "Cancel"};
			int response = JOptionPane.showOptionDialog(this, "Confirm to start a new game?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				printable = true;
				this.dispose();
				bot = false;
				replay = false;
				new TankClient();
			} else {
				printable = true;
				new Thread(new PaintThread()).start();
			}

		} else if (e.getActionCommand().endsWith("Stop")) {
			printable = false;
		} else if (e.getActionCommand().equals("Continue")) {

			if (!printable) {
				printable = true;
				new Thread(new PaintThread()).start();
			}
		} else if (e.getActionCommand().equals("Exit")) {
			printable = false;
			Object[] options = {"Confirm", "Cancel"};
			int response = JOptionPane.showOptionDialog(this, "Confirm to exit?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				ScalaStatistic.writeStat(statistic.playerDeaths,
						statistic.enemiesKilled,
						statistic.playerBulletsFired,
						statistic.enemyBulletsFired,
						statistic.homeDeaths,
						statistic.wins,
						statistic.loses,
						statistic.bricksDestroyed);
				if (!replay && (win || lose)) {
					replayThread = new ReplayThread(totalStates);
					replayThread.run();
				}
				System.out.println("break down");
				System.exit(0);
			} else {
				printable = true;
				new Thread(new PaintThread()).start();

			}

		} else if (e.getActionCommand().equals("Player2")) {
			printable = false;
			Object[] options = {"Confirm", "Cancel"};
			int response = JOptionPane.showOptionDialog(this, "Confirm to add player2?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				printable = true;
				this.dispose();
				replay = false;
				TankClient Player2add = new TankClient();
				Player2add.Player2 = true;
			} else {
				printable = true;
				new Thread(new PaintThread()).start();
			}
		} else if (e.getActionCommand().equals("help")) {
			printable = false;
			JOptionPane.showMessageDialog(null, "Use WSAD to control Player1's direction, use SPACE to fire and restart with pressing R\nUse diection key to Control Player2, use slash to fire",
					"Help", JOptionPane.INFORMATION_MESSAGE);
			this.setVisible(true);
			printable = true;
			new Thread(new PaintThread()).start();
		} else if (e.getActionCommand().equals("Level 1")) {
			Tank.speedX = 6;
			Tank.speedY = 6;
			Bullets.speedX = 10;
			Bullets.speedY = 10;
			this.dispose();
			replay = false;
			new TankClient();
		} else if (e.getActionCommand().equals("Level 2")) {
			Tank.speedX = 10;
			Tank.speedY = 10;
			Bullets.speedX = 12;
			Bullets.speedY = 12;
			this.dispose();
			replay = false;
			new TankClient();

		} else if (e.getActionCommand().equals("Level 3")) {
			Tank.speedX = 14;
			Tank.speedY = 14;
			Bullets.speedX = 16;
			Bullets.speedY = 16;
			this.dispose();
			replay = false;
			new TankClient();
		} else if (e.getActionCommand().equals("Level 4")) {
			Tank.speedX = 16;
			Tank.speedY = 16;
			Bullets.speedX = 18;
			Bullets.speedY = 18;
			this.dispose();
			replay = false;
			new TankClient();
		} else if (e.getActionCommand().equals("Replay")) {
			replay = false;
			printable = false;

			JFileChooser dialog = new JFileChooser();
			dialog.setCurrentDirectory(new File("./saves/"));
			int state = dialog.showOpenDialog(this);
			setVisible(true);
			if (state == JFileChooser.APPROVE_OPTION) {
				fileDes = dialog.getSelectedFile();
				printable = true;
				this.dispose();
				replay = true;
				new TankClient();
			} else {
				printable = true;
				new Thread(new PaintThread()).start();
			}
		} else if (e.getActionCommand().equals("Bot")) {
			printable = false;
			Object[] options = {"Confirm", "Cancel"};
			int response = JOptionPane.showOptionDialog(this, "Confirm to replace player for bot?", "",
					JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null,
					options, options[0]);
			if (response == 0) {
				printable = true;
				this.dispose();
				bot = true;
				replay = false;
				new TankClient();
			} else {
				printable = true;
				new Thread(new PaintThread()).start();
			}

		} else if (e.getActionCommand().equals("Sort")) {
			ScalaSort.sortReplays();
			JavaSort.javaSort();
		}
		else if (e.getActionCommand().equals("Pseudocode")) {
			if(replay) {
				Pseudocode(playerStates);
			}else{
				System.out.println("Run replay");
			}
		}
	}
}