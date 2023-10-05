import java.io.File;
import java.util.Scanner;

import java.awt.Color;     
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.net.DatagramSocket;   
import java.net.DatagramPacket;
import java.net.InetAddress;

import java.awt.Image;
import javax.imageio.ImageIO;

public class kiki extends javax.swing.JFrame {
    
    private char p[][]= new char[15][20]; 
    
    private Image p1,p2,fal,bur;                            //Patrick, Spongebob, wall, burger pictures
    int s1=6,o1=9,s2=6,o2=12,bs=1,bo=1,b1=0,b2=0;           //cordination 
    
    private DatagramSocket socket;
    private final int sport=678;                            //port=678 
    
                                      //Multiplayer: 
    
    private boolean server = false;                         //   Server's = True and the other's false (The server must run first)
    private String ip="10.201.2.19",lip="?";                //   Here you enter the IP of the person you want to play with lip=last ip we play with
    
    
    
    public kiki() {
        initComponents();
        betolt("C:\\Users\\user\\Desktop\\spongya újított\\kiki\\palya.dat");                 //meghívás
        try {
         p1 = ImageIO.read(new File("patrik.png"));       // gif, jpg, png
         fal = ImageIO.read(new File("fal.png"));         //wall
         p2 = ImageIO.read(new File("spongyabob.png"));   //Spongebob
         bur = ImageIO.read(new File("burger.png"));
         socket=new DatagramSocket(sport);                // try! creates a variable which it receives data on the network but needs a port and it tries to connect to 678
        } catch (Exception e) {
         System.out.printf("%s\n",e.toString());
        }	
        Thread receive = new Thread(new Receiver());
        receive.start();
        if (server){                                    //you have to invite the thought if another person is the server
            gondol(); 
        }else{ 
            send("burger?",ip,sport);  
            o1=12; o2=9;                                //Burger placement
    
        }
    }
    
    public class Receiver implements Runnable {       
     private int rport;                               //melyik portrül küüldre
     private String be,rip,szam[];                    //message you sent   //rip sent from which ip
     private byte[] beb=new byte[100];   
     private DatagramPacket rp = new DatagramPacket(beb,beb.length);  
     public void run() {
      while(true) {
       try {
        socket.receive(rp);                            // try! trying to read a packet
        be=new String(rp.getData(),0,rp.getLength());  //converts string (comes from byte)
        rip=rp.getAddress().getHostAddress();          //reads it from the package and returns it in the form it arrived 
        rport=rp.getPort();                            //returns the port
        //receive(be,rip,rport);                       //calls an external function 
        
        
        if(rip.equals(ip)){                          //only one person can connect 
        szam=be.split(",");                            
        
        if (szam[0].equals("burger?")) {
         String ko="b,"+Integer.toString(bs)+","+Integer.toString(bo);           
         send(ko,ip,sport);
        }
        if (szam[0].equals("p")) {
        s2=Integer.parseInt(szam[1]);                  //row
        o2=Integer.parseInt(szam[2]);                  //column
        }
        if (szam[0].equals("b")) {
        bs=Integer.parseInt(szam[1]);                  //burger row
        bo=Integer.parseInt(szam[2]);                  //burger column
        }
        if (szam[0].equals("x")){               //player 2 score
        b2=Integer.parseInt(szam[1]);
        }
        
        
        lip=rip;   ////put it in the global variable and the paint method will write it out
        
        
        repaint();       
        }
        
        
       } catch (Exception e) { System.out.printf("Receive Error\n"); }
      }
     }
    }
   
    
    
    private void send(String ki, String ip, int port) {   //this is how we send a package
     byte[] kib=ki.getBytes();
     InetAddress sip=null;
     if (kib.length!=0) {
      try {
       sip=InetAddress.getByName(ip); // try!
       DatagramPacket sp = new DatagramPacket(kib,kib.length,sip,port);
       socket.send(sp); // try!
      } catch (Exception e) { System.out.printf("Send Error\n"); }
     }
    }
    
    
    
    private void betolt(String fn){                          //track loading
    File file=new File(fn);                           //file
      Scanner input=null;                                    //file read initial value=0
      String sor;                                     
      int s,o;                                               //s=row o=column
      try {                                                  //try to open the file
       input=new Scanner(file,"cp1250");              
       for (s=0; s<15; s++) {                                //row
        sor=input.nextLine();                                //reading 1 row 
        for (o=0;o<20; o++) p[s][o]=sor.charAt(o);      //column  
       }
      } catch(Exception ex) {                                
       System.out.printf("%s\n",ex.getMessage());  //if it's wrong, write this: (first character)
      } finally {                                            //if one of them succeeds, try or catch does it
       if (input!=null) input.close();                       //close it 
      }
    }
    
    
    
    private void gondol(){    
    do{                              //burger
    bo=(int) (Math.random()*19);
    bs=(int) (Math.random()*14);
    } while (p[bs][bo]!=' ');        //do it until there are no spaces on the track
    repaint();
    }
    
    
    
    
    @Override
    public void paint(Graphics g) {                                   //paint
     super.paint(g);
     Image tmp=createImage(800,600);                       //800-600
     Graphics2D t2d=(Graphics2D)tmp.getGraphics();
     int s,o;
     for (s=0; s<15; s++) for (o=0; o<20; o++) {                     // row column
         if (p[s][o]=='X') t2d.drawImage(fal,o*40,s*40,null);  //if you find 'X' in the array, draw the wall
     }
     t2d.setColor(new Color(0,0,0));                       //Corner counter color
     t2d.fillRect(85,10,30,20);
     t2d.fillRect(685,10,30,20);                      //the other player's point
     t2d.fillRect(605,570,150,20);                    //fill in the rectangle for the IP with black
     t2d.setColor(new Color(255,255,255));    
     t2d.drawString(Integer.toString(b1),92,23);           //how many burgers did he pick up b1=burger
     t2d.drawString(Integer.toString(b2),692,23);          //how many burgers did b2=burger pick up
     t2d.drawString(lip,612,583);                            //write the ipt
     t2d.drawImage(bur,bo*40,bs*40,null);                 //burger
     if (server){
     t2d.drawImage(p1,1*40,0*40,null);                     //writing patrik's score as patrik (patrik appears at the top left of the counter)
     t2d.drawImage(p1,o1*40,s1*40,null); 
     t2d.drawImage(p2,16*40,0*40,null);                   //drawing + placement of patrik
     t2d.drawImage(p2,o2*40,s2*40,null);
     }else{             
     t2d.drawImage(p2,1*40,0*40,null);                     //writing out the sponge's score as patrik (patrik appears on the top left of the counter)
     t2d.drawImage(p2,o1*40,s1*40,null);
     t2d.drawImage(p1,16*40,0*40,null);                    
     t2d.drawImage(p1,o2*40,s2*40,null);
     }
     
     g.drawImage(tmp,8,31,this);                        // The side frame is 8px, the headline is 31px! (Win10)
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    char b=evt.getKeyChar();
    if (b==27) System.exit(0);
    if (b=='w' && p[s1-1][o1]==' ') s1--;
    if (b=='s' && p[s1+1][o1]==' ') s1++;
    if (b=='a' && p[s1][o1-1]==' ') o1--;
    if (b=='d' && p[s1][o1+1]==' ') o1++;                                                    //folyamatosan tudunk vele menni
    String ko="p,"+Integer.toString(s1)+","+Integer.toString(o1);   //sor, oszlop küldés
    send(ko,ip,sport);                                   //ide ipt akivel szeretnénk játszani 
    if (s1==bs && o1==bo) {                        //pontszám kiírás
    b1++; 
    ko="x,"+Integer.toString(b1);          // pontszáma p2, pontszáma
    send(ko,ip,sport);   //kód 
    gondol();                                                      //folyamatosan tudunk vele menni
    ko="b,"+Integer.toString(bs)+","+Integer.toString(bo);          // burger sor, oszlop küldés
    send(ko,ip,sport);   //kód    
    }
    repaint();  
    }//GEN-LAST:event_formKeyPressed
    
    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
    /* char b=evt.getKeyChar();
    
    if (b==27) System.exit(0);
    if (b=='w' && p[s1-1][o1]==' ') s1--;
    if (b=='s' && p[s1+1][o1]==' ') s1++;
    if (b=='a' && p[s1][o1-1]==' ') o1--;
    if (b=='d' && p[s1][o1+1]==' ') o1++;
    repaint(); */                                                   //ha csak kattintással menjen a bábunk akkor használjuk ezt
    }//GEN-LAST:event_formKeyReleased
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(kiki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(kiki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(kiki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(kiki.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new kiki().setVisible(true);
            }
        });
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    // End of variables declaration//GEN-END:variables
}
