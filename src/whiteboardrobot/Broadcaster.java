package whiteboardrobot;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tdw10kcu
 *         Broadcaster.java: This class uses two types of network communication, 
 *         UDP and multi casting, to send packets to all users on an multi cast
 *         IP, and also to users that are subscribed using UDP. 
 */
public class Broadcaster {
 private theRobot p;
    int             udpPort, tcpPort;
    MulticastSocket mcSocket;
    String          mcIP;
    Broadcaster(theRobot p){
        this.p = p;
        udpPort     = 64121;
        tcpPort     = 55555;
        mcIP        = "224.0.132.0";
  
    }
    //This fucntion is typically called when the program begins so that a packet
    //is multi cast to the network to let anyone who receives it know that the 
    //Peer at the IP in the message wishes to join with another peer. The Multi
    //Cast socket is created here and then the send() function is called to deal
    //with the packet construction and sending.
    public void join(){
        try {
            mcSocket = new MulticastSocket();
            // also create TCP connection (in thread) to receive accept/GUI data
        } catch (IOException ex) {
            Logger.getLogger(Broadcaster.class.getName()).log(Level.SEVERE, null, ex);
        }
        //String temp = createMsg("join", p.peerIP, "", "" );
        try {
            String temp = "join-"+p.peerIP+"-000-000-";
            send(temp);
        } catch (Exception ex) {
            Logger.getLogger(Broadcaster.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    //This function takes a String as an argument and then constructs a Datagram
    //packet usign the String. The function then joins the multi cast group using
    //the socket created in the previous method and attempts to send the packet
    //to the group. The function then requests that the socket leaves the group
    //as its work has been completed.
    public void send(String m) throws Exception {
        byte[] sendMsg = m.getBytes();
        InetAddress ia = InetAddress.getByName(mcIP);
        mcSocket.joinGroup(ia);
        DatagramPacket dPacket = new DatagramPacket(sendMsg, sendMsg.length, ia, udpPort);
        try {
            System.out.println("Sending Message: " + m);
            mcSocket.send(dPacket);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("couldn't send message!");
        } finally {
            System.out.println("Message send Success!");
        }
        mcSocket.setSoTimeout(3000);
        mcSocket.leaveGroup(ia);
    }
    
    //This function takes a String as an argument and then iterates over the 
    //list of currently subscribed peers, the function is called if a new 
    //instruction is issued by the application so all other white boards can 
    //be updated with the information. A Datagram packet is constructed using 
    //the string and then this is sent via UDP to all IP addresses of subscribed
    //peers. 
    public void sendToSubs(String s){
       //p.theGUI.GuiControl.newInstruction = false;
        byte[] msg = s.getBytes();
        for(int i = 0; i < this.p.peerList.size(); i++){
           String ts = this.p.peerList.get(i).IP;
           System.out.println("Sending instructions ("+s+") to: "+ts);
            try {
                 InetAddress ia = InetAddress.getByName(ts);
                 DatagramPacket di = new DatagramPacket(msg, msg.length, ia, udpPort);
                try {
                    DatagramSocket ds =  new DatagramSocket();
                    try {
                    ds.send(di);
                    ds.close();
                } catch (IOException ex) {
                    Logger.getLogger(Broadcaster.class.getName()).log
                                                (Level.SEVERE, null, ex);
                }
                } catch (SocketException ex) {
                    Logger.getLogger(Broadcaster.class.getName()).log
                                                (Level.SEVERE, null, ex);
                }
                
            } catch (UnknownHostException ex) {
                Logger.getLogger(Broadcaster.class.getName()).log
                                                (Level.SEVERE, null, ex);
            }

        }
    }
}//end of class
    
//    public String createMsg(String type, String ip, String xPos, String yPos){
//        String[] msg = new String[5];
//        if(type !=null)msg[0] = type;       else msg[0] = "type";
//        if(ip   !=null)msg[1] = ip;         else msg[1] = "ip"; 
//        if(!xPos.isEmpty())msg[2] = xPos;   else msg[2] = "000";
//        if(!yPos.isEmpty())msg[3] = yPos;   else msg[3] = "000";  
//        msg[4] = p.peerID;
//       //create a string from the char array and return it 
//       String temp = msg[0]+"-"+msg[1]+"-"+msg[2]+"-"+msg[3]+"-"+msg[4]+"-";
//       return temp;
//    }
