/*
 * @(#)GameSpace.java
 *
 *
 * @author redmon56
 * @version 1.9 2020/9/20
 */
package Snake;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

public class GameSpace extends JFrame implements Runnable {
	static int SQUARE_SIDE = 22;//Number of tiles per side
	private Byte[][] tiles;//0=empty; 1=player; 2=goal;
	private Color 	empty = Color.BLACK,//empty game space STATE
					player = Color.GREEN,//player owned space STATE
					goal = Color.WHITE;//current player goal STATE
	private int xGoalCord,yGoalCord,xCord = 0, yCord = 0,increaseSize=0,
			direction = -1,lastDirection=-1;
	private UsersSnake snake;
	private String[] goalSetting;
	private boolean play = true,gameOver;
	private JLabel score;


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
				label = new JLabel("Generic Snake",SwingConstants.CENTER);
					label.setFont(new Font("Dialog",0,16));
				temp.add(label);

				JPanel middle = new JPanel(new GridLayout(2,1));
				{
					label = new JLabel("Score:",SwingConstants.CENTER);
						label.setFont(new Font("Courier",0,16));
					middle.add(label);
					score = new JLabel("1",SwingConstants.CENTER);
						score.setFont(new Font("Courier",0,16));
					middle.add(score);
				}
				temp.add(middle);

				label = new JLabel("test",SwingConstants.CENTER);
					label.setFont(new Font("Courier",0,18));
				temp.add(label);
				temp.setBorder(new EtchedBorder());
			}add(temp,BorderLayout.NORTH);
			System.out.println("--Grid Graphic's and Screen Layout");
		}
		@Override
    	public void paintComponent(Graphics g){
			super.paintComponent(g);

			for(int x=0;x<SQUARE_SIDE-2;x++)
				for(int y=0;y<SQUARE_SIDE-2;y++){
					switch(tiles[x+1][y+1]){
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
			direction = snake.getFacingDirection();
			lastDirection=-1;

			//Prevents 180 degree direction change
			if(snake.getLastDirection()!=-1)
				 lastDirection = snake.getLastDirection()+2;
			if(lastDirection>3)
				lastDirection-=4;
			snake.setLastDirection(direction);

			switch(e.getKeyCode()){
				case KeyEvent.VK_SPACE: play = !play;System.out.println("Space");break;
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_A: if(direction!=1){
										if(lastDirection!=1){
											xCord=1;yCord=0;snake.setFacingDirection(3);}}break;
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_D: if(direction!=3){
										if(lastDirection!=3){
											xCord=-1;yCord=0;snake.setFacingDirection(1);}}break;
				case KeyEvent.VK_UP:
				case KeyEvent.VK_W: if(direction!=2){
										if(lastDirection!=2){
											xCord=0;yCord=1;snake.setFacingDirection(0);}}break;
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_S: if(direction!=0){
										if(lastDirection!=0){
											xCord=0;yCord=-1;snake.setFacingDirection(2);}}break;
			}
			if(snake.getCurrentSize()==1){
				snake.setFacingDirection(-1);
				snake.setLastDirection(-1);
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
	setGoal();
	while(!gameOver && !snake.getSnakeComplete()){
		if(play){
			repaint();
			update();

			delay();
		}else
			delay();
		//System.out.println("pop");
	}
	if(snake.getSnakeComplete())
		System.out.println("You won!");
	else if(gameOver)
		System.out.println("Game Over");
	else
		System.out.println("Error");
	//	}
	}
	void update(){
		int tempX=-1,tempY=-1,tempXTail=-1,tempYTail=-1,temp1,temp2,size = snake.getCurrentSize(),
			xCordForThisUpdate = xCord,yCordForThisUpdate = yCord;
		CordOfSnake temp = snake.getSnakeHead();
		gameOver =!testSnakeHead(temp.getX(),temp.getY(),xCordForThisUpdate,yCordForThisUpdate,temp);
		if(!gameOver){
			tiles[temp.getX()][temp.getY()] = 0;

			temp1 = temp.getX();
			temp2 = temp.getY();
			temp.setCord(temp1-xCordForThisUpdate,temp2-yCordForThisUpdate);
			tempX = temp1;
			tempY = temp2;

			tiles[temp.getX()][temp.getY()] = 1;
			//Checks if snake will increase its size, if yes then
			//the tails coords of obtained before the snake is moved.
			if(increaseSize>0){
				tempXTail = snake.getSnakeTail().getX();
				tempYTail = snake.getSnakeTail().getY();
			}

			while(temp.getNext()!=null){
				temp = temp.getNext();

				tiles[temp.getX()][temp.getY()] = 0;
				//Switching Current Position with Previous
				temp1 = temp.getX();
				temp2 = temp.getY();
				temp.setCord(tempX,tempY);
				tempX = temp1;
				tempY = temp2;

				tiles[temp.getX()][temp.getY()] = 1;
			}
			if(increaseSize>0){
				increaseSize--;
				snake.addToTail(tempXTail,tempYTail);
				//System.out.println(tempXTail + " " + tempYTail);
				tiles[tempXTail][tempYTail] = 1;
			}
			snakeHeadAtGoal(snake.getSnakeHead().getX(),snake.getSnakeHead().getY());
		}
		//System.out.println("Complete Update");
	}
	private boolean testSnakeHead(int x, int y, int xTest, int yTest,CordOfSnake temp){
		if(x==0||x==SQUARE_SIDE-1||y==0||y==SQUARE_SIDE-1)
			return false;
		if(temp.getNext()!=null){
			temp = temp.getNext();
			while(temp.getNext()!=null){
				if(temp.getX()==x && temp.getY()==y)
					return false;
				temp = temp.getNext();
			}
		}
		return true;
	}
	//Method: snakeHeadAtGoal() -- Type void
	//	Checks if current goal is met; If yes, then
	//	a new goal is set, and snake size increases
	private void snakeHeadAtGoal(int x, int y){
		if(x==xGoalCord&&y==yGoalCord){
			increaseSize+=3;
			score.setText(""+(snake.getCurrentSize()+3));
			setGoal();
		}
	}

	//Method: setGoal() -- Type void
	//	Sets the current goal; making sure not to be
	//	in the player occupied spaces
	private void setGoal(){
		goalSetting = new String[snake.getMaxSize()];
		int snakeCurrentSize = snake.getCurrentSize();
		CordOfSnake temp = snake.getSnakeHead();

		//adds only empty spaces to goalSetting list
		for(int x = 0;x<SQUARE_SIDE-2;x++)
			for(int y = 0;y<SQUARE_SIDE-2;y++)
				goalSetting[(x*(SQUARE_SIDE-2)+y)] = (x+1)+" "+(y+1);

		//Removes spaces taken by the snake
		goalSetting[((temp.getX()-1)*(SQUARE_SIDE-2))+(temp.getY()-1)] = null;
		while(temp.getNext()!=null){
			temp = temp.getNext();
			goalSetting[((temp.getX()-1)*(SQUARE_SIDE-2))+(temp.getY()-1)] = null;
		}

		//randomly selects a empty space to place new goal
		int randomNum = (int)(Math.random()*(snake.getMaxSize()));
		String temp2 = goalSetting[randomNum];

		//If space is taken by snake, then it tries the next space
		while(temp2==null){
			randomNum+=1;
			temp2 = goalSetting[randomNum];
		}

		xGoalCord = Integer.valueOf(temp2.substring(0,temp2.indexOf(" ")));
		yGoalCord = Integer.valueOf(temp2.substring(temp2.indexOf(" ")+1));

		tiles[xGoalCord][yGoalCord] = 2;
	}
	//Method: delay() -- Type void
	//	game delay between game tic
	private void delay(){try{Thread.sleep(128);}catch(InterruptedException e){}}

}

class UsersSnake{
	int maxSize,border,currentSize=1,
		facingDirection = -1,lastDirection=-1;//[-1=no direction,0=up,1=right,2=down,3=left]
	CordOfSnake snakeHead,snakeTail;
	boolean snakeComplete = false;

	public UsersSnake(int sqrtSize){
		sqrtSize = sqrtSize-2;
		maxSize = sqrtSize * sqrtSize;
		border = sqrtSize;
		snakeHead = new CordOfSnake();
		snakeHead.setCord(border/2,border/2);
		snakeTail = snakeHead;
		System.out.print("Snake data Initialization Complete");
	}
	public int getCurrentSize(){return currentSize;}
	public int getMaxSize(){return maxSize;}
	public int getFacingDirection(){return facingDirection;}
	public int getLastDirection(){return lastDirection;}
//	public CordOfSnake[] getSnakePos(){return snakePos;}
	public CordOfSnake getSnakeHead(){return snakeHead;}
	public CordOfSnake getSnakeTail(){return snakeTail;}
	public boolean getSnakeComplete(){return snakeComplete;}

	public void setFacingDirection(int f){ facingDirection = f;}
	public void setLastDirection(int d){ lastDirection = d;}
	public void addToTail(int x, int y){
		currentSize++;
		snakeTail.setNext();
		snakeTail = snakeTail.getNext();
		snakeTail.setCord(x,y);
		if(currentSize==maxSize)
			snakeComplete = true;
	}
}

class CordOfSnake{
	private int x,y;
	private CordOfSnake next = null;

	public int getX(){return x;}
	public int getY(){return y;}
	public CordOfSnake getNext(){return next;}

	//void setCord(int xPos, int yPos){x = x + xPos; y = y + yPos;}
	void setCord(int xPos, int yPos){x = xPos; y = yPos;}
	public void setNext(){next = new CordOfSnake();}
	}