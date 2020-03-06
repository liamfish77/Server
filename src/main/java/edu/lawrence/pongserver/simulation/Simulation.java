package edu.lawrence.pongserver.simulation;

import edu.lawrence.pongserver.PongConstants;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import edu.lawrence.pongserver.physics.*;
import java.util.ArrayList;
import java.util.List;

public class Simulation implements PongConstants {
    private Box outer;
    private Ball ball;
    private Ball ball2;
    private Box boxOne;
    private Box boxTwo;
    private Box brick1;
    private Box brick2;
    private Box brick3;
    private Box brick4;
    private Box brick5;
    private Box brick6;
    private List<Box> boxList;
    private Lock lock;
    
    public Simulation(int dX,int dY)
    {
        boxList = new ArrayList<Box>();
        outer = new Box(0,0,WIDTH+250,HEIGHT,false,false,0);
        boxList.add(outer);
        ball = new Ball(WIDTH/2,WIDTH/2,dX,dY);
        ball2 = new Ball(WIDTH/2,WIDTH/2,dX,dY);
        boxOne = new Box(MARGIN+20,WIDTH+ MARGIN +200,THICKNESS+10,LENGTH-20,true,false,0);
        boxList.add(boxOne);
        boxTwo = new Box(MARGIN +40,WIDTH+ MARGIN+ 200,THICKNESS+10,LENGTH-20,true,false,0);
        boxList.add(boxTwo);
        brick1 = new Box(MARGIN, 50, 90, 20, true, true, 0);
        boxList.add(brick1);
        brick2 = new Box(MARGIN+102, 50, 90, 20, true, true, 0);
        boxList.add(brick2);
        brick3 = new Box(MARGIN+202, 50, 90, 20, true, true, 0);
        boxList.add(brick3);
        brick4 = new Box(MARGIN+302, 50, 90, 20, true, true, 0);
        boxList.add(brick4);
        brick5 = new Box(MARGIN+402, 50, 90, 20, true, true, 0);
        boxList.add(brick5);
        brick6 = new Box(MARGIN+502, 50, 90, 20, true, true, 0);
        boxList.add(brick6);
        lock = new ReentrantLock();
    }
    
    public void evolve(double time)
    {
        lock.lock();
        Ray newLoc = boxOne.bounceRay(ball.getRay(), time);
        if(newLoc != null)
            ball.setRay(newLoc);
        else {
            newLoc = boxTwo.bounceRay(ball.getRay(), time);
            if(newLoc != null)
                ball.setRay(newLoc);         
            else {
                newLoc = outer.bounceRay(ball.getRay(), time);
                if(newLoc != null)
                    ball.setRay(newLoc);
                else
                    ball.move(time);
        Ray newLoc2 = boxOne.bounceRay(ball2.getRay(), time);
        if(newLoc2 != null)
            ball2.setRay(newLoc2);
        else {
            newLoc2 = boxTwo.bounceRay(ball2.getRay(), time);
            if(newLoc2 != null)
                ball2.setRay(newLoc2);         
            else {
                newLoc2 = outer.bounceRay(ball2.getRay(), time);
                if(newLoc2 != null)
                    ball2.setRay(newLoc2);
                else
                    ball2.move(time);
            }                
        }
        boolean boxHit = false;
        for (Box i : boxList) {
            Ray newLoc3 = i.bounceRay(ball.getRay(), time);
            if(newLoc3 != null) {
                ball.setRay(newLoc3);
                boxHit = true;
                if (i.isBrick()) {
                    i.gotHit();
                }
                break;
            }
        }
        if (boxHit==false) {
            ball.move(time);
        }
        boolean boxHit2 = false;
        for (Box i : boxList) {
            Ray newLoc4 = i.bounceRay(ball2.getRay(), time);
            if(newLoc4 != null) {
                ball2.setRay(newLoc4);
                boxHit2 = true;
                if (i.isBrick()) {
                    i.gotHit();
                }
                break;
            }
        }
        if (boxHit2==false) {
            ball2.move(time);
        }
        lock.unlock();
    }
        }
    }
      
    public void moveBox(int box,int deltaX,int deltaY)
    {
        lock.lock();
        Box mover = boxOne;
        if(box == 2)
            mover = boxTwo;
        
        double dX = deltaX;
        double dY = deltaY;
        if(mover.x + deltaX < 0)
          dX = -mover.x;
        if(mover.x + mover.width + deltaX > outer.width)
          dX = outer.width - mover.width - mover.x;
       
        if(mover.y + deltaY < 0)
           dY = -mover.y;
        if(mover.y + mover.height + deltaY > outer.height)
           dY = outer.height - mover.height - mover.y;
        
        mover.move(dX,dY);
        if(mover.contains(ball.getRay().origin)) {
            // If we have discovered that the box has just jumped on top of
            // the ball, we nudge them apart until the box no longer
            // contains the ball.
            int bumpX = -1;
            if(dX < 0) bumpX = 1;
            int bumpY = -1;
            if(dY < 0) bumpY = 1;
            do {
            mover.move(bumpX, bumpY);
            ball.getRay().origin.x += -bumpX;
            ball.getRay().origin.y += -bumpY;
            } while(mover.contains(ball.getRay().origin));
        }
        lock.unlock();
    }
    
    public String getGameState() {
        Point ballLoc = ball.getRay().origin;
        Point ballLoc2 = ball2.getRay().origin;
        return Double.toString(ballLoc.x) + ' ' + ballLoc.y + ' ' + ballLoc2.x + ' ' +ballLoc2.y + ' ' +
                boxOne.x + ' ' + boxTwo.x;
    }
}
