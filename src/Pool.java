import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.*;
import java.util.Random;
import java.awt.event.*;

/**
 * Press r to reset the ball position and counters. Press n to quit the program,  and y to reset. 
 * Problems are : balls teleporting/moving to upper left corner(problem with collision method)
 * @author nseidl
 *
 */

public class Pool extends JFrame implements ActionListener, MouseListener, MouseMotionListener, KeyListener
{
  //ball info
  public static final int NUM_BALLS = 16;
  public static Ball[] balls = new Ball[NUM_BALLS]; 
  private static int cueBall = 0;
  public final Color yellow1 = new Color(255, 216, 0);//ball colors
  public final Color maroon1 = new Color(176, 48, 96);
  public final Color blue1 = new Color(0, 71, 171);
  public final Color red1 = new Color(255, 10, 0);
  public final Color orange1 = new Color(243, 132, 0);
  public final Color green1 = new Color(0, 255, 0);
  public final Color purple1 = new Color(153, 17, 152);
  public final int MAX_VELOCITY = -465;

  //table/pocket info
  public static Pocket[] topPockets = new Pocket[3];
  public static Pocket[] bottomPockets = new Pocket[3];
  public static final int POCKET_DIAMETER = 75;
  public static final int TABLE_WIDTH = 1600;
  public static final int TABLE_HEIGHT = 800;
  public static final int WINDOW_SIDE = 8;
  public static final int WINDOW_TOP = 7;
  public static final int MAX_WIDTH = 1692;
  public static final int MAX_HEIGHT = 913;
  private static final int DELAY_IN_MILLISEC = 15;
  private Table table = new Table(TABLE_WIDTH, TABLE_HEIGHT, POCKET_DIAMETER / 2 + WINDOW_SIDE, POCKET_DIAMETER - WINDOW_TOP);

  //global
  public static final Random gen = new Random();
  private static Font menuFont = new Font("Times", Font.BOLD, 50);
  private static Font playAgain = new Font("Times", Font.BOLD, 100);
  private static Font yn = new Font("Times", Font.ITALIC, 25);
  private int turnNum = 0;
  private int stripesLeft = 7;
  private int solidsLeft = 7;
  private int ballsInARow = 0;
  private Ray ray;

  //mouse
  private static boolean mouseDragging;
  private static int mouseX;
  private static int mouseY;


  public static void main(String[] args) 
  {
    Pool mb = new Pool();
    mb.addMouseListener(mb);
    mb.addMouseMotionListener(mb);
    mb.addKeyListener(mb);              
  }

  /**
   * initiate: screen, clock, pockets, balls
   */
  public Pool()
  {
    setSize(MAX_WIDTH, MAX_HEIGHT);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Timer clock= new Timer(DELAY_IN_MILLISEC, this);      
    clock.start();  

    for(int topPocketsFill = 0; topPocketsFill < 3; topPocketsFill++)
    {
      topPockets[topPocketsFill] = new Pocket(topPocketsFill * (TABLE_WIDTH / 2) + WINDOW_SIDE, POCKET_DIAMETER / 2 - WINDOW_TOP, POCKET_DIAMETER);
    }
    
    for(int bottomPocketsFill = 0; bottomPocketsFill < 3; bottomPocketsFill++)
    {
      bottomPockets[bottomPocketsFill] = new Pocket(bottomPocketsFill * (TABLE_WIDTH / 2) + WINDOW_SIDE, POCKET_DIAMETER / 2 - WINDOW_TOP + TABLE_HEIGHT, POCKET_DIAMETER);
    }
    reset();
  }

  /**
   * reset resets the game. balls go back to original position. scores and counters reset
   */
  public void reset()
  {
    int radius = 25;
    int ballRightXCenter = WINDOW_SIDE + POCKET_DIAMETER / 2 + (TABLE_WIDTH / 4) + radius;
    int ballRightYCenter = WINDOW_TOP + POCKET_DIAMETER / 2 + TABLE_HEIGHT / 2 + radius;
    stripesLeft = 7;
    solidsLeft = 7;
    turnNum = 0;
    //colors: yellow, maroon, blue, red, green, orange, purple
    //solids are 13, 11, 4, 6, 2, 12, 5
    //stripes are: 15, 14, 9, 10, 1, 7, 3
    generateFiveRow(ballRightXCenter, ballRightYCenter, radius);
    generateFourRow(ballRightXCenter, ballRightYCenter, radius);
    generateThreeRow(ballRightXCenter, ballRightYCenter, radius);
    generateRest(ballRightXCenter, ballRightYCenter, radius);
  }

  /**
   * generateFiveRow generates the row of balls that has 5
   * @param ballRightXCenter : the ball furthest to the right in the triangle
   * @param ballRightYCenter : the ball furthest to the right in the triangle
   * @param radius : radius of the ball
   */
  public void generateFiveRow(int ballRightXCenter, int ballRightYCenter, int radius)
  {
    for(int fiveRow = 1; fiveRow < 6; fiveRow++)
    {
      if(fiveRow == 1)
      {
        balls[fiveRow] = new Ball(ballRightXCenter - 4 * (radius * Math.sqrt(3)), ballRightYCenter + (-4 * radius), 0, 0, radius, yellow1, true, false);
      }
      else if(fiveRow == 2)
      {
        balls[fiveRow] = new Ball(ballRightXCenter - 4 * (radius * Math.sqrt(3)), ballRightYCenter + (-2 * radius), 0, 0, radius, yellow1, true, false);
      }
      else if(fiveRow == 3)
      {
        balls[fiveRow] = new Ball(ballRightXCenter - 4 * (radius * Math.sqrt(3)), ballRightYCenter, 0, 0, radius, blue1, true, false);
      }
      else if(fiveRow == 4)
      {
        balls[fiveRow] = new Ball(ballRightXCenter - 4 * (radius * Math.sqrt(3)), ballRightYCenter + (2 * radius), 0, 0, radius, blue1, true, false);
      }
      else if(fiveRow == 5)
      {
        balls[fiveRow] = new Ball(ballRightXCenter - 4 * (radius * Math.sqrt(3)), ballRightYCenter + (4 * radius), 0, 0, radius, orange1, true, false);
      }
    }
  }
  
  /**
   * generateFourRow generates the row of balls with four
   * @param ballRightXCenter : ball all the way to the right
   * @param ballRightYCenter : ball all the way to the right
   * @param radius : radius of the balls
   */
  public void generateFourRow(int ballRightXCenter, int ballRightYCenter, int radius)
  {
    for(int fourRow = 6; fourRow < 10; fourRow++)
    {
      if(fourRow == 6)
      {
        balls[fourRow] = new Ball(ballRightXCenter - 3 * (radius * Math.sqrt(3)) + 1, ballRightYCenter - 3 * radius, 0, 0, radius, maroon1, true, false);
      }
      else if(fourRow == 7)
      {
        balls[fourRow] = new Ball(ballRightXCenter - 3 * (radius * Math.sqrt(3)) + 1, ballRightYCenter - radius, 0, 0, radius, orange1, true, false);
      }
      else if(fourRow == 9)
      {
        balls[fourRow] = new Ball(ballRightXCenter - 3 * (radius * Math.sqrt(3)) + 1, ballRightYCenter + 3 * radius, 0, 0, radius, maroon1, true, false);
      }
      balls[11] = new Ball(ballRightXCenter - 3 * (radius * Math.sqrt(3)) + 1, ballRightYCenter + radius, 0, 0, radius, red1, true, false);
    }
  }
  
  /**
   * generateThreeRow generates the row of balls with three
   * @param ballRightXCenter : ball fursthest to the right
   * @param ballRightYCenter
   * @param radius
   */
  public void generateThreeRow(int ballRightXCenter, int ballRightYCenter, int radius)
  {
    for(int threeRow = 10; threeRow < 13; threeRow++)
    {
      if(threeRow == 10)
      {
        balls[threeRow] = new Ball(ballRightXCenter - 2 * (radius * Math.sqrt(3)) + 2, ballRightYCenter - 2 * radius, 0, 0, radius, red1, true, false);
      }
      else if(threeRow == 12)
      {
        balls[threeRow] = new Ball(ballRightXCenter - 2 * (radius * Math.sqrt(3)) + 2, ballRightYCenter + 2 * radius, 0, 0, radius, green1, true, false);
      }
      balls[8] = new Ball(ballRightXCenter - 2 * (radius * Math.sqrt(3)) + 2, ballRightYCenter, 0, 0, radius, Color.black, true, false);
    }
  }
  
  /**
   * generates the remaining balls
   * @param ballRightXCenter
   * @param ballRightYCenter
   * @param radius
   */
  public void generateRest(int ballRightXCenter, int ballRightYCenter, int radius)
  {
    for(int twoRow = 13; twoRow < 15; twoRow++)
    {
      if(twoRow == 13)
      {
        balls[twoRow] = new Ball(ballRightXCenter - 1 * (radius * Math.sqrt(3)) + 3, ballRightYCenter - radius, 0, 0, radius, purple1, true, false);
      }
      else if(twoRow == 14)
      {
        balls[twoRow] = new Ball(ballRightXCenter - 1 * (radius * Math.sqrt(3)) + 3, ballRightYCenter+ radius, 0, 0, radius, green1, true, false);
      }
    }
    balls[0] = new Ball(ballRightXCenter + 500, ballRightYCenter, 0, 0, radius, Color.white, true, false);
    balls[15] = new Ball(ballRightXCenter + 3, ballRightYCenter, 0, 0, radius, purple1, true, false);
  }
  
  public void mouseClicked(MouseEvent e) 
  {

  }

  public void mouseMoved(MouseEvent e)
  {

  }

  /**
   * called when mouse is pressed: calls cueballhit method
   */
  public void mousePressed(MouseEvent e) 
  { 
    mouseX = e.getX();
    mouseY = e.getY();
    cueBall = cueBallHit(e.getX(), e.getY());

    if(cueBall >= 0)
    {
      mouseDragging = true; 
    }
    else
    {
      mouseDragging = false;
    }
  }

  /**
   * called when mouse is released: adds velocity to the cue ball
   */
  public void mouseReleased(MouseEvent e) 
  {
    mouseDragging = false;
    if(balls[cueBall].getDx() == 0 && balls[cueBall].getDy() == 0)
    {
      double ballX = balls[cueBall].getX();
      double ballY = balls[cueBall].getY();
      double dx = ballX - e.getX();//e.getX()
      double dy = ballY - e.getY();//e.getY()

      if(dx < MAX_VELOCITY)
      {
        dx = MAX_VELOCITY;
      }

      double f = .1;
      balls[cueBall].setDx(dx * f);
      balls[cueBall].setDy(dy * f);
      turnNum = turnNum + 1;
    }
  }

  public void mouseEntered(MouseEvent e)
  {

  }

  public void mouseExited(MouseEvent e) 
  {

  }

  /**
   * called every 15 ms(by default)
   */
  public void actionPerformed(ActionEvent e) 
  {
    for (int i = 0; i < balls.length; i++)
    {
      actionPerformedNumbers(i);
      balls[i].move(topPockets, bottomPockets, i, balls);
    }
    for (int i = 0; i < balls.length; i++)//bounce the balls if needed
    {
      balls[i].bounce(WINDOW_SIDE + POCKET_DIAMETER / 2, //xmin
          WINDOW_SIDE + POCKET_DIAMETER / 2 + TABLE_WIDTH, //xmax
          4 * WINDOW_TOP + POCKET_DIAMETER / 2, //ymin
          4 * WINDOW_TOP + POCKET_DIAMETER / 2 + TABLE_HEIGHT); //ymax
    }
    for (int i = 0; i < balls.length; i++)//check for collisions
    {
      for (int j = 0; j < i; j++)
      {
        balls[i].collision(balls[j]);
      }
    }
    repaint();
  }
  
  /**
   * takes care of turn numbers and keeping the player turn the same if they get balls in a row
   * @param i : ball number
   */
  public void actionPerformedNumbers(int i)
  {
    if(i > 0)
    {
      if(i == 8)//quit if 8ball is hit in
      {
        if(balls[i].getVisible() == false && balls[i].getHasScored() == false)
        {
          balls[i].setHasScored(true);
          System.exit(0);
        }
      }
      if(i == 15 || i == 14 || i == 9 || i == 10 || i == 1 || i == 7 || i == 3)//striped ball
      {
        ifStriped(i);
      }
      else//solid
      {
        ifSolid(i);
      }
    }
  }

  /**
   * if the ball is striped
   * @param i : ball number
   */
  public void ifStriped(int i)
  {
    if(balls[i].getVisible() == false && balls[i].getHasScored() == false)
    {
      balls[i].setHasScored(true);
      stripesLeft = stripesLeft - 1;
      if(ballsInARow == 0)
      {
        turnNum = turnNum - 1;
      }
      ballsInARow = ballsInARow + 1;
    }
    else
    {
      ballsInARow = 0;
    }
  }
  
  /**
   * if the ball is solid
   * @param i : ball number
   */
  public void ifSolid(int i)
  {
    if(balls[i].getVisible() == false && balls[i].getHasScored() == false)
    {
      balls[i].setHasScored(true);
      solidsLeft = solidsLeft - 1;
      if(ballsInARow == 0)
      {
        turnNum = turnNum - 1;
      }
      ballsInARow = ballsInARow + 1;
    }
    else
    {
      ballsInARow = 0;
    }
  }

  /**
   * displays the play again text
   * @param g : graphics
   * @param mosueX : x of the mouse
   * @param mouseY : y of the mouse
   */
  public void playAgain(Graphics g, int mosueX, int mouseY)
  {
    g.setColor(Color.black);
    g.setFont(playAgain);
    g.drawString("Would you like to play again?", 250, 500);
    g.setFont(yn);
    g.setColor(Color.white);
    g.drawString("Type y for yes or n for no", 800, 600);
  }

  /**
   * to prevent double buffering
   */
  public void paint(Graphics g)
  {
    bufferPaint(g);
  }

  /**
   * paint method-draw everything
   * @param h : from above
   */
  public void bufferPaint(Graphics h)
  {
    Image buffer = createImage(MAX_WIDTH, MAX_HEIGHT);
    Graphics g = buffer.getGraphics();
    g.setColor(Color.black);
    g.fillRect(WINDOW_SIDE, WINDOW_TOP, MAX_WIDTH, MAX_HEIGHT);
    table.draw(g);
    for(int count = 0; count < 3; count ++)//draw pockets
    {
      topPockets[count].draw(g);
      bottomPockets[count].draw(g);
    }
    for (int i = 0; i < balls.length; i++)//draw balls
    {
      balls[i].draw(g, i, balls);
    }
    if(mouseDragging == true)//draw lines to/from cue ball
    {
      double[] intersection = new double[3];
      ray = new Ray(balls[0].getX(), balls[0].getY(), balls[0].getX() - mouseX, balls[0].getY() - mouseY);
      ray.intersectTable(intersection);
      g.setColor(Color.blue);
      g.drawLine((int)balls[0].getX(), (int)balls[0].getY(), mouseX, mouseY);
      g.setColor(Color.cyan);
      g.drawLine((int)balls[0].getX(), (int)balls[0].getY(), (int)intersection[0], (int)intersection[1]);
    }
    g.setColor(Color.white);
    g.setFont(menuFont);
    if(turnNum % 2 == 0)
    {
      g.drawString("Player 1's Turn", 82, 65);
    }
    else
    {
      g.drawString("Player 2's Turn", 82, 65);
    }
    g.drawString("Stripes left: " + stripesLeft, 878, 65);
    g.drawString("Solids left: " + solidsLeft, 1278, 65);
    if(stripesLeft == 0 && solidsLeft == 0)
    {
      playAgain(g, mouseX, mouseY);
    }
    h.drawImage(buffer, 0, 0, MAX_WIDTH, MAX_HEIGHT, null);
  }

  /**
   * 
   * @param x mousex
   * @param y mousey
   * @return
   */
  public int cueBallHit(double x, double y)
  {
    int ballX = (int)balls[0].getX();
    int ballY = (int)balls[0].getY();
    double distance = (ballX - x) * (ballX - x) + (ballY - y) * (ballY - y);//from mouse to ball center
    if(Math.sqrt(distance) < balls[0].getRadius())
    {
      return 0;
    }
    return -1;
  }

  /**
   * change mouse coordinates
   */
  public void mouseDragged(MouseEvent e) 
  {
    mouseX = e.getX();
    mouseY = e.getY();
  }

  /**
   * when a key is pressed, R resets everything
   */
  public void keyPressed(KeyEvent e) 
  {
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_R)
    {
      reset();
    }
    if(keyCode == KeyEvent.VK_N)
    {
      System.exit(0);
    }
    if(keyCode == KeyEvent.VK_Y)
    {
      reset();
    }
  }

  public void keyReleased(KeyEvent e) 
  {

  }

  public void keyTyped(KeyEvent e) 
  {

  }
}

/**
 * sets up the table
 *
 */
class Table
{
  private static int width;
  private static int height;
  private static int cornerX;
  private static int cornerY;
  Color mainTableColor = new Color(28, 124, 52);

  public Table(int widthIn, int heightIn, int cornerXIn, int cornerYIn)
  {
    width = widthIn;
    height = heightIn;
    cornerX = cornerXIn;
    cornerY = cornerYIn;
  }

  /**
   * getters
   * @return
   */
  public int getHeight()
  {
    return height;
  }

  public int getWidth()
  {
    return width;
  }

  public Color getColor()
  {
    return mainTableColor;
  }

  public void draw(Graphics g)
  {
    g.setColor(mainTableColor);
    g.fillRect(cornerX, cornerY, width, height);
  }
}

/**
 * sets up the pockets
 *
 */
class Pocket
{
  private static int diameter;
  private static Color pocketColor = new Color(139, 69, 19);
  private int xCorner;
  private int yCorner;


  public Pocket(int xCornerIn, int yCornerIn, int diameterIn)
  {
    xCorner = xCornerIn;
    yCorner = yCornerIn;
    diameter = diameterIn;
  }

  /**
   * getters
   * @return
   */
  public int getX()
  {
    return xCorner + diameter / 2;
  }

  public int getY()
  {
    return yCorner + diameter / 2;
  }

  public int getRadius()
  {
    return diameter / 2;
  }

  public void draw(Graphics g)
  {
    g.setColor(pocketColor);
    g.fillOval(xCorner, yCorner, diameter, diameter);
  }
}

/**
 * ball class...collisions and rays drawn to/from balls
 *
 */
class Ball
{
  // DATA:
  private double x, y;    
  private double dx, dy;    
  private double radius;    
  private Color color;  

  double elastic = 1.1;
  double dt = 1; 
  final double FRICTION_MULTIPLIER = .980;//.985
  private boolean visible;
  private boolean hasScored;


  // METHODS:

  /**
   * Ball constructor initializes the Ball object
   * 
   * @param xIn   x coordinate of center
   * @param yIn   y coordinate of center
   * @param dxIn    x velocity
   * @param dyIn    y velocity
   * @param radiusIn  radius
   * @param colorIn color
   */
  public Ball (double xIn, double yIn, double dxIn, double dyIn, double radiusIn, Color colorIn, boolean visibleIn, boolean hasScoredIn)
  {
    x = xIn;
    y = yIn;
    dx = dxIn;
    dy = dyIn;
    radius = radiusIn;
    color = colorIn;
    visible = visibleIn;
    hasScored = hasScoredIn;
  }
  
  /**
   * getters and setters
   */
  public double getDx()
  {
    return dx;
  }
  
  public void setDx(double DxIn)
  {
    dx = DxIn;
  }
  
  public double getDy()
  {
    return dy;
  }
  
  public void setDy(double DyIn)
  {
    dy = DyIn;
  }
  
  public boolean getVisible()
  {
    return visible;
  }
  
  public void setVisible(boolean visibleIn)
  {
    visible = visibleIn;
  }
  
  public boolean getHasScored()
  {
    return hasScored;
  }
  
  public void setHasScored(boolean hasScoredIn)
  {
    hasScored = hasScoredIn;
  }

  /**
   * Move the ball.  Add the velocity to its center.
   */
  public void move(Pocket[] topPockets, Pocket[] bottomPockets, int ballNum, Ball[] balls)
  {
    x += dx;
    y += dy;
    dx *= FRICTION_MULTIPLIER;
    dy *= FRICTION_MULTIPLIER;
    if (dx > -0.1 && dx < 0.1) dx = 0;
    if (dy > -0.1 && dy < 0.1) dy = 0;
    for(int count = 0; count < 3; count++)
    {
      int xDiff = (int)x - topPockets[count].getX();
      int yDiff = (int)y - topPockets[count].getY();
      int distanceToPocketMid = (int)Math.sqrt(((xDiff) * (xDiff)) + ((yDiff) * (yDiff)));
      if(distanceToPocketMid < topPockets[count].getRadius())
      {
        if(ballNum == 0)//if its cueball
        {
          balls[ballNum].x = Pool.WINDOW_SIDE + Pool.POCKET_DIAMETER / 2 + (Pool.TABLE_WIDTH / 4) + 25 + 500;
          balls[ballNum].y = Pool.WINDOW_TOP + Pool.POCKET_DIAMETER / 2 + Pool.TABLE_HEIGHT / 2 + 25;
          balls[ballNum].dx = 0;
          balls[ballNum].dy = 0;
        }
        else//move it away
        {
          balls[ballNum].setVisible(false);
          balls[ballNum].setX(-1000);
          balls[ballNum].setY(-1000);
          balls[ballNum].setDx(-100);
          balls[ballNum].setDy(-100);
        }
      }
    }
    for(int count = 0; count < 3; count++)
    {
      int xDiff = (int)x - bottomPockets[count].getX();
      int yDiff = (int)y - bottomPockets[count].getY();
      int distanceToPocketMid = (int)Math.sqrt(((xDiff) * (xDiff)) + ((yDiff) * (yDiff)));
      if(distanceToPocketMid < bottomPockets[count].getRadius())
      {
        if(ballNum == 0)//if it's cueball
        {
          balls[ballNum].x = Pool.WINDOW_SIDE + Pool.POCKET_DIAMETER / 2 + (Pool.TABLE_WIDTH / 4) + 25 + 500;
          balls[ballNum].y = Pool.WINDOW_TOP + Pool.POCKET_DIAMETER / 2 + Pool.TABLE_HEIGHT / 2 + 25;
          balls[ballNum].dx = 0;
          balls[ballNum].dy = 0;
        }
        else//move it away
        {
          balls[ballNum].setVisible(false);
          balls[ballNum].setX(-1000);
          balls[ballNum].setY(-1000);
          balls[ballNum].setDx(-100);
          balls[ballNum].setDy(-100);
        }
      }
    }
  }

  /**
   * Check if the ball should bounce off any of the walls.  It will only
   * bounce if it was heading toward the wall and went a bit past it.  If
   * so just change the sign of the corresponding velocity.  
   * 
   * @param xLow    x coord of left wall
   * @param xHigh   x coord of right wall
   * @param yLow    y coord of top wall
   * @param yHigh   y coord of bottom wall
   */
  public void bounce(double xLow, double xHigh, double yLow, double yHigh)
  {
    if ((x - radius <= xLow && dx < 0) || (x + radius >= xHigh && dx > 0))
    {
      dx = -dx;
    }

    if ((y - radius <= yLow && dy < 0) || (y + radius >= yHigh && dy > 0))
    {
      dy = -dy;
    }
  }


  /**
   * Handles ball collisions.  ASSUMES EQUAL MASSES
   * @param other   Ball to bounce off of
   */
  public void collision(Ball other)
  {
    double xOther = other.x;
    double yOther = other.y;
    double twoBallsDx = x-xOther;
    double twoBallsDy = y-yOther;
    double distSqr = twoBallsDx*twoBallsDx+twoBallsDy*twoBallsDy;
    double diamSqr = (radius + other.radius) * (radius + other.radius);
    boolean isCollision = false;

    if (distSqr < diamSqr)
    {
      isCollision = true;

      //Get ball vectors
      double dxOther = other.dx;
      double dyOther = other.dy;

      //Move backwards (forwards if dt < 0) in time until balls are just touching
      double CoefA = (dx-dxOther)*(dx-dxOther)+(dy-dyOther)*(dy-dyOther);
      double CoefB = 2*((dx-dxOther)*(x-xOther)+(dy-dyOther)*(y-yOther));
      double CoefC = (x-xOther)*(x-xOther)+(y-yOther)*(y-yOther)-diamSqr;
      double t;
      if (CoefA == 0)
      {
        t = -CoefC/CoefB;
      }
      else if (dt >= 0)
      {
        t = (-CoefB-Math.sqrt(CoefB*CoefB-4*CoefA*CoefC))/(2*CoefA);
      }
      else
      {
        t = (-CoefB+Math.sqrt(CoefB*CoefB-4*CoefA*CoefC))/(2*CoefA);
      }

      x = x+t*dx;
      y = y+t*dy;
      xOther = xOther+t*dxOther;
      yOther = yOther+t*dyOther;

      //Center of momentum coordinates
      double mx = (dx+dxOther)/2;
      double my = (dy+dyOther)/2;
      dx = dx-mx;
      dy = dy-my;
      dxOther = dxOther-mx;
      dyOther = dyOther-my;

      //New center to center line
      twoBallsDx = x-xOther;
      twoBallsDy = y-yOther;
      double dist = Math.sqrt(twoBallsDx*twoBallsDx+twoBallsDy*twoBallsDy);
      twoBallsDx = twoBallsDx/dist;
      twoBallsDy = twoBallsDy/dist;

      //Reflect balls velocity vectors in center to center line
      double OB = -(twoBallsDx*dx+twoBallsDy*dy);
      dx = dx+2*OB*twoBallsDx;
      dy = dy+2*OB*twoBallsDy;
      OB = -(twoBallsDx*dxOther+twoBallsDy*dyOther);
      dxOther = dxOther+2*OB*twoBallsDx;
      dyOther = dyOther+2*OB*twoBallsDy;

      //Back to moving coordinates with elastic velocity change
      double e = Math.sqrt(elastic);
      dx = e*(dx+mx);
      dy = e*(dy+my);
      dxOther = e*(dxOther+mx);
      dyOther = e*(dyOther+my);

      //Move to new bounced position
      x = x-t*dx;
      y = y-t*dy;
      xOther = xOther-t*dxOther;
      yOther = yOther-t*dyOther;

      //Set velocities
      other.dx = dxOther;
      other.dy = dyOther;

      //Set position
      other.x = xOther;
      other.y = yOther;
    }

  }

  /**
   * getters and setters
   * @return
   */
  public double getX()
  {
    return x;
  }

  public double getY()
  {
    return y;
  }
  
  public void setX(double xIn)
  {
    x = xIn;
  }
  
  public void setY(double yIn)
  {
    y = yIn;
  }

  public double getRadius()
  {
    return radius;
  }


  /**
   * Draw the ball.
   * @param g     Graphics object in which to draw
   */
  public void draw(Graphics g, int ballNum, Ball[] balls)
  {
    if(balls[ballNum].visible == true)//if it hasnt scored
    {
      g.setColor(color);
      g.fillOval((int)(x - radius), (int)(y - radius), (int)(2 * radius), (int)(2 * radius));//draw the solid back
      if(ballNum == 0)
      {
        g.setColor(Color.black);
        g.drawString("CUE", (int)x - 13, (int)y + 5);
      }
      else if(ballNum == 8)
      {
        g.setColor(Color.white);
        g.drawString(ballNum + "", (int)x, (int)y);
      }
      else if(ballNum == 15 || ballNum == 14 || ballNum == 9 || ballNum == 10 || ballNum == 1 || ballNum == 7 || ballNum == 3)//if its stripes, white circle
      {
        g.setColor(Color.white);
        g.fillOval((int)(x - radius / 2), (int)(y - radius / 2), (int)(radius), (int)(radius));
        g.setColor(Color.black);
        g.drawString(ballNum + "", (int)x - 6, (int)y + 6);
      }
      else      //stripes are: 15, 14, 9, 10, 1, 7, 3
      {
        g.setColor(Color.black);
        g.drawString(ballNum + "", (int)x - 6, (int)y + 6);
        //solids are 13, 11, 4, 6, 2, 12, 5
        //stripes are: 15, 14, 9, 10, 1, 7, 3
      }
      //colors: yellow, maroon, blue, red, green, orange, purple
    }
  }
}

class Ray
{
  private double xStart;
  private double yStart;
  private double directionX;
  private double directionY;
  private int tableX, tableY;
  private int ballX, ballY;
  private boolean intersectBall = false;

  public Ray(double xStartIn, double yStartIn, double directionXIn, double directionYIn)
  {
    xStart = xStartIn;
    yStart = yStartIn;
    directionX = directionXIn;
    directionY = directionYIn;
  }

  /**
   * getters
   * @return
   */
  public int tableX()
  {
    return tableX;
  }

  public int tableY()
  {
    return tableY;
  }

  public int ballX()
  {
    return ballX;
  }

  public int ballY()
  {
    return ballY;
  }

  /**
   * 
   * @param intersection : array : x of intersection, y of intersection, distance
   */
  public void intersectTable(double intersection[])
  {
    if(intersectLine(Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP,
        Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE + Pool.TABLE_WIDTH,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP, intersection))//top of table
    {

    }
    else if(intersectLine(Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP,
        Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP + Pool.TABLE_HEIGHT, intersection))//left of tabble
    {

    }
    else if(intersectLine(Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP + Pool.TABLE_HEIGHT,
        Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE + Pool.TABLE_WIDTH,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP + Pool.TABLE_HEIGHT, intersection))//bottom of table
    {

    }
    else if(intersectLine(Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE + Pool.TABLE_WIDTH,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP,
        Pool.POCKET_DIAMETER / 2 + Pool.WINDOW_SIDE + Pool.TABLE_WIDTH,
        Pool.POCKET_DIAMETER - Pool.WINDOW_TOP + Pool.TABLE_HEIGHT, intersection))//right of table
    {

    }
  }
/**
 * calculates whether the ray would intersect a line
 * @param x1 : start x of line
 * @param y1 : start y of line
 * @param x2 : end x of line
 * @param y2 : end y of line
 * @param intersection : array of x of intersection, y of intersection, distance
 * @return
 */
  public boolean intersectLine(int x1, int y1, int x2, int y2, double intersection[])
  {
    double dx = x2 - x1;
    double dy = y2 - y1;
    double s = (directionX * (y1 - yStart) - directionY * (x1 - xStart)) / (dx * directionY - directionX * dy);//segment: length from start to intersect point
    double t = (x1 + s * dx - xStart) / directionX;//length of the ray
    if(t >= 0 && s >= 0 && s<= 1)
    {
      intersection[0] = xStart + t * directionX;
      intersection[1] = yStart + t * directionY;
      intersection[2] = Math.sqrt((xStart - intersection[0]) * (xStart - intersection[0]) + (yStart - intersection[1]) * (yStart - intersection[1]));
      return true;
    }
    else
    {
      return false;
    }
  }

  public boolean intersectBall()
  {
    return intersectBall;
  }

  /**
   * draws a line from ball to ball(not finished yet)
   * @param g : graphics
   * @param mouseX : mosuex
   * @param mouseY : mosue y
   */
  public void drawToBall(Graphics g, int mouseX, int mouseY)
  {
    double ballX = (int)Pool.balls[0].getX();
    double ballY = (int)Pool.balls[0].getY();
    g.setColor(Color.blue);
    g.drawLine((int)ballX, (int)ballY, mouseX, mouseY);
    g.setColor(Color.cyan);
  }
}
