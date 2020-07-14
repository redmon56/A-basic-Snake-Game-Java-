/**
 * @(#)GameSpace.java
 *
 *
 * @author redmon56
 * @version 1.00 2020/7/2
 */
package Snake;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class GameSpace extends JFrame {
	static int SQUARE_SIDE = 20;//Number of tiles per side
	private Color[][] tiles;
	private Color 	empty = Color.BLACK,//empty game space STATE
					player = Color.GREEN,//player owned space STATE
					goal = Color.WHITE;//current player goal STATE
	private int xGoalCord,yGoalCord;
	private UsersSnake snake;


	public static void main(String[] arg){
		new GameSpace();
	}

	public GameSpace(){
		snake = new UsersSnake(SQUARE_SIDE);

		startGame();
	}

	//sets starting data
	void initalize(){
		tiles = new Color[SQUARE_SIDE][SQUARE_SIDE];

		for(int r=0;r<SQUARE_SIDE;r++)
			for(int c=0;c<SQUARE_SIDE;c++)
				tiles[r][c] = empty;
		System.out.println("--Grid Data Initalization");
	}

	@Override
    protected void frameInit() {
    	System.out.println("Game Space Start-up");
    	super.frameInit();
    	this.initalize();

    	setTitle("A Basic Snake Game (Java)");
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	setContentPane(new Panel());
		setSize(720,576);
		setLocationRelativeTo(null);
    	setVisible(true);
    	setResizable(false);
    	System.out.println("Game Space Start-up Complete");
    }

	class Panel extends JPanel{
		JPanel temp,temp2;
		JLabel label;
		public Panel(){
			setLayout(new BorderLayout());

			temp = new JPanel(new GridLayout(1,3));
			{
				label = new JLabel("test");
					label.setFont(new Font("Courier",0,42));
				temp.add(label);
				label = new JLabel("test");
					label.setFont(new Font("Courier",0,36));
				temp.add(label);
				label = new JLabel("test");
					label.setFont(new Font("Courier",0,36));
				temp.add(label);
				temp.setBorder(new EtchedBorder());
			}add(temp,BorderLayout.NORTH);
			System.out.println("--Grid Graphic's and Screen Layout");
		}
		@Override
    	public void paintComponent(Graphics g){
			super.paintComponent(g);

			for(int x=0;x<20;x++)
				for(int y=0;y<20;y++){
					g.setColor(tiles[x][y]);
					g.fillRect(x*22+129,y*22+75,22,22);
				}
    	}
	}
	////////////////////////////////////////////////////////End of Start up////////////////////////////////////////////////////////
	void startGame(){

	//	while(snake.getCurrentSize()<(SQUARE_SIDE*SQUARE_SIDE)){
	//		for(int x=0;x<1000;x++){
	//			System.out.println("dd");
	//		}
			update();
			repaint();

	//	}
	}
	void update(){
		int endLoopAt = snake.getCurrentSize();
		CordOfSnake[] temp = snake.getSnakePos();
		for(int x=0;x<snake.getCurrentSize();x++)
			tiles[temp[x].getX()][temp[x].getY()] = player;
	}
}

class UsersSnake{
	int maxSize,border,currentSize=1;
	CordOfSnake[] snakePos;

	public UsersSnake(int sqrtSize){
		maxSize = sqrtSize * sqrtSize;
		border = sqrtSize;
		snakePos = new CordOfSnake[maxSize];
		for(int x=0;x<maxSize;x++)
			snakePos[x] = new CordOfSnake();
		snakePos[0].setCord(border/2,border/2);
		System.out.print("Snake data Initialization Complete");
	}
	public int getCurrentSize(){return currentSize;}
	public CordOfSnake[] getSnakePos(){return snakePos;}
}

class CordOfSnake{
	private int x,y;

	void setCord(int xPos, int yPos){x = xPos; y = yPos;}
	public int getX(){return x;}
	public int getY(){return y;}
}