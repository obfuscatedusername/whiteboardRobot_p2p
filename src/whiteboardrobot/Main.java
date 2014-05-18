package whiteboardrobot;
import java.util.*;
/**
 * @author tdw10kcu
 *         Main.java: In this file the main object used to provide functionality 
 *         of the white board, "theRobot", is created. When creating the user
 *         can choose how many draw commands the bot should issue and the delay
 *         between each of these draw commands.
 */
public class Main {
    static boolean automated = false;
    public static void main(String[] args) {
       if(!automated){
            System.out.println("Please enter number of iterations...");
            Scanner in = new Scanner(System.in);
            int iter = in.nextInt();
            System.out.println("Now, please enter the delay in ms "
                               + "between commands...");
            int delay = in.nextInt();
            theRobot Robot = new theRobot("IamRobot");
            Robot.setDelayandIter(delay, iter);
            Robot.start(); 
       }else{
            theRobot Robot = new theRobot("IamRobot");
            Robot.setDelayandIter(1500, 20);
            Robot.start();  
       }
    }
}
