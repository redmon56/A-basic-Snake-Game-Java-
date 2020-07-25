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
import java.awt.event.*;

public class GameSpace extends JFrame implements Runnable {
	static int SQUARE_SIDE = 20;//Number of tiles per side
	private Byte[][] tiles;//0=empty; 1=player; 2=goal;
	private Color 	empty = Color.BLACK,//empty game space STATE
					player = Color.GREEN,//player owned space STATE
					goal = Color.WHITE;//current player goal STATE
	private int xGoalCord,yGoalCord,xCord = 0, yCord = 0;
	private UsersSnake snake;
	private String[] goalSetting;
	private boolean play = true,gameOver;


	public static void main(String[] arg){
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				new Thread(new GameSpace()).start();
			}
		});
	}

	public GameSpace(){
		snake = new UsersSnake(SQUARE_SIDE);
		//startGame();
	}

	//sets starting data
	void initalize(){
		tiles = new Byte[SQUARE_SIDE][SQUARE_SIDE];

		for(int r=0;r<SQUARE_SIDE;r++)
			for(int c=0;c<SQUARE_SIDE;c++)
				tiles[r][c] = 0;
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
    	addKeyListener(new Keypress());
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
					switch(tiles[x][y]){
						case 0: g.setColor(empty); break;
						case 1: g.setColor(player); break;
						case 2: g.setColor(goal); break;
					}
					g.fillRect(x*22+129,y*22+75,22,22);
				}
    	}
	}
	class Keypress implements KeyListener{
		@Override
		public void keyPressed(KeyEvent e){
			switch(e.getKeyCode()){
				case KeyEvent.VK_SPACE: play = !play;break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A: xCord=-1;yCord=0;System.out.println("Left");break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D: xCord=1;yCord=0;System.out.println("Right");break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W: xCord=0;yCord=-1;break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S: xCord=0;yCord=1;break;
			}

		}
		@Override
		public void keyReleased(KeyEvent e){}
		@Override
		public void keyTyped(KeyEvent e){}
	}
	////////////////////////////////////////////////////////End of Start up////////////////////////////////////////////////////////
	@Override
	public void run(){

	//	while(snake.getCurrentSize()<(SQUARE_SIDE*SQUARE_SIDE)){
	//		for(int x=0;x<1000;x++){
	//			System.out.println("dd");
	//		}
	System.out.println("g");
	setGoal();
	while(!gameOver){
		if(play){
			update();
			repaint();
			delay();
		}
	}
	System.out.println("Game Over");

	//	}
	}
	void update(){
		int endLoopAt = snake.getCurrentSize();
		CordOfSnake[] temp = snake.getSnakePos();
		gameOver =!testSnakeHead(temp[0].getX(),temp[0].getY());
		if(!gameOver)
			for(int x=0;x<snake.getCurrentSize();x++){
				tiles[temp[x].getX()][temp[x].getY()] = 0;
				temp[x].setCord(xCord,yCord);
				tiles[temp[x].getX()][temp[x].getY()] = 1;
			}
	}
	private boolean testSnakeHead(int x, int y){
		x = x+xCord;
		y = y+yCord;
		if(x<0||x>SQUARE_SIDE-1||y<0||y>SQUARE_SIDE-1)
			return false;
		return true;
	}
	private void snakeHeadAtGoal(int x, int y){

	}
	//Method: setGoal() -- Type void
	//	Sets the current goal; making sure not to be
	//	in the player occupied spaces
	private void setGoal(){
		goalSetting = new String[snake.getMaxSize()-snake.getCurrentSize()];
		int goalCounter = 0,snakeCounter,snakeCurrentSize = snake.getCurrentSize();
		CordOfSnake temp = null;
		boolean checkSnake = true;

		//adds only empty spaces to goalSetting list
		for(int x = 0;x<SQUARE_SIDE;x++){
			for(int y = 0;y<SQUARE_SIDE;y++){
				snakeCounter = 0;
				//Checks current x,y values with snake Positions
				while(snakeCounter<snakeCurrentSize&&checkSnake){
					temp = snake.getSnakePos()[snakeCounter];
					if(x==temp.getX()&&y==temp.getY())
						checkSnake = false;
					snakeCounter++;
					temp = null;
				}
				//adds to goalSetting list if previous x,y test success
				if(checkSnake){
					goalSetting[goalCounter] = x+" "+y;
					goalCounter++;
				}
				snakeCounter++;
				checkSnake = true;
			}

		}
		//randomly selects a empty space to place new goal
		String temp2 = goalSetting[(int)(Math.random()*(snake.getMaxSize()-snake.getCurrentSize()))];
		xGoalCord = Integer.valueOf(temp2.substring(0,temp2.indexOf(" ")));
		yGoalCord = Integer.valueOf(temp2.substring(temp2.indexOf(" ")+1));

		tiles[xGoalCord][yGoalCord] = 2;

	}
	//Method: delay() -- Type void
	//	game delay between game tic
	private void delay(){try{Thread.sleep(128);}catch(InterruptedException e){}}

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
	public int getMaxSize(){return maxSize;}
	public CordOfSnake[] getSnakePos(){return snakePos;}
	public CordOfSnake getSnakeHead(){return snakePos[0];}
}

class CordOfSnake{
	private int x,y;

	void setCord(int xPos, int yPos){x = x + xPos; y = y + yPos;}
	public int getX(){return x;}
	public int getY(){return y;}
}