package whiteboardrobot;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author tdw10kcu
 *         theRobot.java: the Robot for causing havoc on the white board is 
 *         created and defined here, the "Listener" and "Broadcaster" objects
 *         are created and their associated threads started so the Robot can 
 *         issue multiple draw commands to the white board.
 */

//This small class imitates a struct, and holds the details pertaining to each 
//peer, that is the relevant IP and ID of each peer. 
class peers{
  String ID;
  String IP;
  peers(){
      ID = IP = "";
  }
};
public class theRobot implements Runnable {
     GUI theGUI;
    String nodename;
    public String peerID;
    public String peerIP;
    public int iter, delay;
    public ArrayList<peers> peerList = new ArrayList();
    public Listener        theListener;
    public Broadcaster     theSender;
    Thread          peerThread;
    boolean         isJoined, hasInit, shouldCont;
    
    public theRobot(String nodename){
        this.nodename = nodename;
        this.theGUI = new GUI(this.nodename, 1000, 600);
        peerThread = new Thread (peerThreadRunnable);
        isJoined = hasInit = false;
        peerID = String.valueOf(1 + (int)(Math.random() * ((100 - 1)+1)));
        theListener = new Listener(this);
        theListener.udpThread.start();
        theListener.tcpThread.start();
        theSender = new Broadcaster(this);
        hasInit = false;
        shouldCont = true;
    }

    
    public void start(){
        JFrame.setDefaultLookAndFeelDecorated(true);
        javax.swing.SwingUtilities.invokeLater(this);
        peerThread.start();
    }
    
    public void run(){
        this.theGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.theGUI.setPreferredSize(new Dimension(1000, 600));
        this.theGUI.pack();
        this.theGUI.setVisible(true);
    }
     
    //This is the main thread of the robot, it runs when a new peer is created 
    //and handles all functions that each peer has, i.e. connecting, disconnecting
    //and when a new draw instruction is detected. The thread uses a while loop
    //to iterate over the number of draw instructions the robot should issue, 
    //and uses a method of random number generation to generate the points the 
    //line should have, and also the colours that the line will be. When the 
    //robot has finished it's job it disconnects and closes all communication
    //and shuts down.
    Runnable peerThreadRunnable = new Runnable() {
        @Override
        public void run(){
            try {
                System.out.println("peerThreadRunnable Started");
                theSender.join();
                int iterations = 0;
                while(iterations < iter){
                    int x, y, r, g, b;
                    Random rand = new Random();
                    x = rand.nextInt(1000);
                    y = rand.nextInt(600);
                    r = rand.nextInt(255);
                    g = rand.nextInt(255);
                    b = rand.nextInt(255);
                    String col = r+"/"+g+"/"+b+"/";
                    String s = "draw-"+x+"-"+y+"-"+col;
                    Color tc = new Color(r,g,b);
                    Point tp = new Point(x,y);
                    theGUI.GuiControl.drawLine(tp, tc);
                    theSender.sendToSubs(s);
                    Thread.sleep(delay);
                    iterations++;
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(theRobot.class.getName()).log(Level.SEVERE, null, ex);                
            }finally{
                System.out.println("I'm finished, goodbye!");
                String dc = "disc-"+peerID+"-"+peerIP+"-";
                theSender.sendToSubs(dc);
                theGUI.MenuAL.d = false;
                peerList.clear();
                System.exit(0);
            }
        }
    };
    
    //this function is set via the Main.java file using the console as input, 
    //although this is only if the Robot is not set to automated, if so then 
    //the delay and iteration numbers are defined already.
   public void setDelayandIter(int d, int i){
       iter = i;
       delay = d;   
   }
    
   //This function adds a peer to the ArrayList using the details passed
    public void addPeer(String ip, String id){
        peers temp = new peers();
        temp.ID = id;
        temp.IP = ip;
        peerList.add(temp);
    }//end of addPeer()
    
    //This function iterates over the ArrayList of peers, if a match between the
    //current iteration and the passed variables is found then that iteration
    //is removed from the ArrayList.
    public void removePeer(String ip, String id){
        System.out.println("Removing Peer: "+ip);
        peerList.size();
        for(int i = 0; i < peerList.size(); i++){
            System.out.println("peerList element at "+i+": "+peerList.get(i).IP);
            if(ip.equals(peerList.get(i).IP)){
                peerList.remove(i);
                System.err.println("Removed Peer: "+ip);
            }
        }
    }//end of removePeer()
}
