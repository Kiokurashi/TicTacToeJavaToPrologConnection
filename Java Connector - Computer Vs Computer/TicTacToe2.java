import java.awt.* ; 
import java.awt.event.* ; 
import java.io.* ; 
import java.net.* ;
import javax.swing.* ; 
import javax.swing.border.* ; 

public class TicTacToe2 extends JFrame 
                       implements ActionListener {
   JButton b11,b21,b31,
           b12,b22,b32,
           b13,b23,b33,
           start;
   JLabel winner,scoreboard;
   boolean bReset=false; //Default non-button pressed reset. I.e. End of game.
   BufferedReader br ; 
   BufferedWriter bw ;
   BufferedReader br2 ; 
   BufferedWriter bw2 ;
   Thread connection ; 
   Thread connection2 ; 
   Process prologProcess ; 
   Process prologProcess2 ; 
   String prolog,tttX,tttO,boardState;
   int port,port2,time,p1Score,p2Score,turnT;
   int turn=0,game=1,games;
   FileWriter fw;
   PrintWriter pw;

   /**
     *  Create a tic tac toe game, 
     *  prolog is the prolog command (e.g. "/opt/local/bin/swipl").
     *  ttt is the locator for ttt.pl (e.g. "/javalib/TicTacToe/ttt.pl").
     */
   public TicTacToe2(String prolog, String tttX, String tttO, String port, String port2,String time,String games) { 
      this.prolog = prolog ; 
      this.tttX = tttX ; 
      this.tttO = tttO ; 
      this.port = Integer.parseInt(port);
      this.port2 = Integer.parseInt(port2);
      this.time = (int) Double.parseDouble(time)*1000;
      this.games= Integer.parseInt(games);
      b11 = new JButton("") ; 
      b21 = new JButton("") ; 
      b31 = new JButton("") ; 
      b12 = new JButton("") ; 
      b22 = new JButton("") ; 
      b32 = new JButton("") ; 
      b13 = new JButton("") ; 
      b23 = new JButton("") ; 
      b33 = new JButton("") ; 
      start = new JButton("Start");
      b11.setActionCommand("(1,1).") ; // prolog reads pair term
      b21.setActionCommand("(2,1).") ; 
      b31.setActionCommand("(3,1).") ; 
      b12.setActionCommand("(1,2).") ; 
      b22.setActionCommand("(2,2).") ; 
      b32.setActionCommand("(3,2).") ; 
      b13.setActionCommand("(1,3).") ; 
      b23.setActionCommand("(2,3).") ; 
      b33.setActionCommand("(3,3).") ; 
      start.setActionCommand("(0,1).") ;
      Font f = new Font("monospaced",Font.PLAIN,64) ;
      Font f2 = new Font("monospaced",Font.PLAIN,12) ;
      winner = new JLabel("") ;
      scoreboard = new JLabel("<html> Wins<br> Player: 0<br> Computer: 0</html>");
      winner.setFont(f2);
      scoreboard.setFont(f2);
      b11.setFont(f) ; 
      b21.setFont(f) ; 
      b31.setFont(f) ; 
      b12.setFont(f) ; 
      b22.setFont(f) ; 
      b32.setFont(f) ; 
      b13.setFont(f) ; 
      b23.setFont(f) ; 
      b33.setFont(f) ; 
      start.setFont(f2) ;
      b11.addActionListener(this) ; 
      b21.addActionListener(this) ; 
      b31.addActionListener(this) ; 
      b12.addActionListener(this) ; 
      b22.addActionListener(this) ; 
      b32.addActionListener(this) ; 
      b13.addActionListener(this) ; 
      b23.addActionListener(this) ; 
      b33.addActionListener(this) ; 
      start.addActionListener(this);
      JPanel panel = new JPanel() ; 
      panel.setLayout(new GridLayout(4,3)) ; 
      panel.add(b11) ; 
      panel.add(b21) ; 
      panel.add(b31) ; 
      panel.add(b12) ; 
      panel.add(b22) ; 
      panel.add(b32) ; 
      panel.add(b13) ; 
      panel.add(b23) ; 
      panel.add(b33) ; 
      panel.add(start) ;
      panel.add(scoreboard);
      panel.add(winner);
      
      try {
		fw = new FileWriter("D:/Users/jerem/eclipse-workspace/a4part1/ttt2output.txt");
	} catch (IOException e1) {	e1.printStackTrace();
	}
		pw = new PrintWriter(fw);
      
      
      
      //this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE) ; 
      this.setTitle("Tic Tac Toe") ; 
      Border panelborder = BorderFactory.createLoweredBevelBorder() ; 
      panel.setBorder(panelborder) ; 
      this.getContentPane().add(panel) ; 
      this.setSize(300,300) ;
      this.setLocation(900,300) ; 

      Connector2 connector = new Connector2(this.port) ; 
      connector.start() ; 
      Connector2 connector2 = new Connector2(this.port2) ; 
      connector2.start() ; 

      Socket sock ;
      try {
         sock = new Socket("127.0.0.1",this.port) ;
         br = new BufferedReader(new InputStreamReader(sock.getInputStream())) ; 
         bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())) ; 
      } catch(Exception x) { System.out.println(x) ; }
      Socket sock2 ;
      try {
         sock2 = new Socket("127.0.0.1",this.port2) ;
         br2 = new BufferedReader(new InputStreamReader(sock2.getInputStream())) ; 
         bw2 = new BufferedWriter(new OutputStreamWriter(sock2.getOutputStream())) ; 
      } catch(Exception x) { System.out.println(x) ; }

      connection = new Thread() {
          public void run() { 
             while(true) {
                try{
                   String s = br.readLine() ; 
                   computer_move(s) ; 
                } catch(Exception xx) { System.out.println(xx) ; }
             }  
          }
       } ;
       connection.start() ;
       connection2 = new Thread() {
           public void run() { 
               while(true) {
                  try{
                     String ss = br2.readLine() ; 
                     computer_move2(ss) ; 
                  } catch(Exception xx) { System.out.println(xx) ; }
               }  
            }
         } ;
         connection2.start() ;

         Thread shows = new Thread() { 
            public void run() { 
               setVisible(true) ;
            }
         } ;
         EventQueue.invokeLater(shows);
         Thread shows2 = new Thread() { 
             public void run() { 
                setVisible(true) ;
             }
          } ;
          EventQueue.invokeLater(shows2);

      // Start the prolog player

          try { 
             prologProcess = 
     		 		Runtime.getRuntime().exec(prolog + " -f " + tttX +" -- "+port) ; 
          } catch(Exception xx) {System.out.println(xx) ; }
          try { 
              prologProcess2 = 
      		 		Runtime.getRuntime().exec(prolog + " -f " + tttO +" -- "+port2) ; 
           } catch(Exception xx) {System.out.println(xx) ; }
          try {
        	  bw.write("(0,1).\n");
        	  System.out.println("Started Player 1.");
        	  bw.flush();
          } catch(Exception xx) {System.out.println(xx) ; }

      // On closing, kill the prolog process first and then exit
      this.addWindowListener(new WindowAdapter() { 
         public void windowClosing(WindowEvent w) { 
             if (prologProcess != null) prologProcess.destroy() ;
             if (prologProcess2 != null) prologProcess2.destroy() ;
            pw.close();
            System.exit(0) ; 
         }
      }) ; 
   } 

//       /opt/local/bin/swipl   /javalib/TicTacToe/ttt.pl
   public static void main(String[] args) { 
      String prolog = "C:/Program Files/swipl/bin/swipl-win.exe" ;
      String tttX = "ttt2player1.pl"; 
      String tttO = "ttt2player2.pl"; 
	  String port = "54321";
	  String port2 = "54322";
	  String time = "1.0";
	  String games = "5";
      boolean noargs = true ; 
      try { 
         prolog = args[0] ;
         tttX = args[1] ;
         tttO = args[2] ;
		 port = args[3];
		 port2 = args[4];
		 time = args[5];
		 games = args[6];
         noargs = false ; 
      } 
      catch (Exception xx) {
         System.out.println("usage: java TicTactoe  <where prolog>  <where ttt>") ; 
      }
      if (noargs) { 
         Object[] message = new Object[14] ; 
         message[0] = new Label("  prolog command") ;
         message[1] = new JTextField(prolog) ; 
         message[2] = new Label("  where tttO.pl ") ;
         message[3] = new JTextField(tttX) ; 
		 message[4] = new Label("  where tttX.pl ") ;
		 message[5] = new JTextField(tttO) ; 
		 message[6] = new Label("  port number ") ;
         message[7] = new JTextField(port) ; 
         message[8] = new Label("  port number 2 ") ;
         message[9] = new JTextField(port2) ; 
         message[10] = new Label("  time ") ;
         message[11] = new JTextField(time) ; 
         message[12] = new Label("  run N times ") ;
         message[13] = new JTextField(games) ;
         try { 
            int I = JOptionPane.showConfirmDialog(null,message,"Where are Prolog and ttt.pl? ",JOptionPane.OK_CANCEL_OPTION) ;  
            if (I == 2 | I == 1) System.exit(0) ;
            System.out.println(I) ; 
            new TicTacToe2(((JTextField)message[1]).getText().trim(),((JTextField)message[3]).getText().trim(),((JTextField)message[5]).getText().trim(),((JTextField)message[7]).getText().trim(),((JTextField)message[9]).getText().trim(),((JTextField)message[11]).getText().trim(),((JTextField)message[13]).getText().trim());  
         } catch(Exception yy) {} 
      }
      else
    	  new TicTacToe2(prolog, tttX, tttO, port, port2, time, games) ; 
   }

   void reset() {
       winner.setText("");
	   turn = 0;
	   turnT = 0;
	   pw.printf("\n********* NEW GAME ********\n");
	   game++;
	   pw.printf("\nGame: "+game+"\n");
	 	  try { 
		         bw.write("(0,0).\n") ; 
		         bw.flush() ; 
		         bw2.write("(0,0).\n") ; 
		         bw2.flush() ; 
		      } catch(Exception xx) { System.out.println(xx) ; } 
	   b11.setText("");
	   b12.setText("");
	   b13.setText("");
	   b21.setText("");
	   b22.setText("");
	   b23.setText("");
	   b31.setText("");
	   b32.setText("");
	   b33.setText("");
	   b11.setBackground(new JButton().getBackground());
	   b12.setBackground(new JButton().getBackground());
	   b13.setBackground(new JButton().getBackground());
	   b21.setBackground(new JButton().getBackground());
	   b22.setBackground(new JButton().getBackground());
	   b23.setBackground(new JButton().getBackground());
	   b31.setBackground(new JButton().getBackground());
	   b32.setBackground(new JButton().getBackground());
	   b33.setBackground(new JButton().getBackground());
   }
   
   public String parseBoardString() {
	   String boardState = "";
	   boardState += "[";
	   JButton[] jbutton = {b11, b12, b13, b21, b22, b23, b31, b32, b33};
	   int i = 1;
	   for(JButton button : jbutton) {
		   if(i % 3 == 1)
			   boardState += "[";
		   if(button.getText() == "")
			   boardState += "#";
		   else
			   boardState += button.getText();
		   if(i % 3 == 0)
			   boardState += "]";
		   if(i % 3 != 0)
			   boardState += ", ";
		   i++;
	   }
	   boardState += "]";
	   return boardState;
   }
   void computer_move(String s) { // " x ## y '
	   turn++;
	   turnT++;
      String[] c = s.split(",") ; 
      int x = Integer.parseInt(c[0].trim()), 
          y = Integer.parseInt(c[1].trim()) ; 
      
      if (x == 1) {
         if (y == 1) b11.setText("X") ; 
         else if (y == 2) b12.setText("X") ; 
         else if (y == 3) b13.setText("X") ; 
      }
      else if (x == 2) {
         if (y == 1) b21.setText("X") ;
         else if (y == 2) b22.setText("X") ; 
         else if (y == 3) b23.setText("X") ; 
      }
      else if (x == 3) { 
         if (y == 1) b31.setText("X") ;
         else if (y == 2) b32.setText("X") ; 
         else if (y == 3) b33.setText("X") ; 
      }
      if (winner()) {
    	  games--;
          p1Score++;
          winner.setText("<html> Player 1<br>   Wins!</html>");
          scoreboard.setText("<html> Wins<br> Player 1: "+p1Score+"<br> Player 2: "+p2Score+"</htm>");
    	  return;
      }
      if(turn == 5) {
    	  games--;
          winner.setText("A tie!");
          turn = 0;
		  return;
	   }
      try {
		Thread.sleep(time);
		} catch (InterruptedException e) { e.printStackTrace(); }
      try {
    	  System.out.println("(" + s + ").\n");
  		bw2.write("(" + s + ").\n");
  		bw2.flush();
  		} catch(Exception xx) { System.out.println(xx) ; } 
      pw.printf("T = " + turnT + " Player 1 take (" + x + "," + y + ").\t" + parseBoardString() + "\n");
   }
   
   void computer_move2(String s) { // " x ## y '
	   turnT++;
      String[] c = s.split(",") ; 
      int x = Integer.parseInt(c[0].trim()), 
          y = Integer.parseInt(c[1].trim()) ; 
      //System.out.println(x+","+y) ; 
      if (x == 1) {
         if (y == 1) b11.setText("O") ; 
         else if (y == 2) b12.setText("O") ; 
         else if (y == 3) b13.setText("O") ; 
      }
      else if (x == 2) {
         if (y == 1) b21.setText("O") ;
         else if (y == 2) b22.setText("O") ; 
         else if (y == 3) b23.setText("O") ; 
      }
      else if (x == 3) { 
         if (y == 1) b31.setText("O") ;
         else if (y == 2) b32.setText("O") ; 
         else if (y == 3) b33.setText("O") ; 
      }
      if (winner()) {
          p2Score++;
          games--;
          winner.setText("<html> Player 2<br>   Wins!</html>");
          scoreboard.setText("<html> Wins<br> Player 1: "+p1Score+"<br> Player 2: "+p2Score+"</htm>");
    	  return; //connection.stop();
      } 
      try {
  		Thread.sleep(time);
  		} catch (InterruptedException e) { e.printStackTrace(); }
        try {
      	  System.out.println("(" + s + ").\n");
    		bw.write("(" + s + ").\n");
    		bw.flush();
    		} catch(Exception xx) { System.out.println(xx) ; } 
        pw.printf("T = " + turnT + " Player 2 take (" + x + "," + y + ").\t" + parseBoardString() + "\n");
   }

   /**
     * Java player
     */
   public void actionPerformed(ActionEvent act) {
		String s = ((JButton)act.getSource()).getText() ; 
		if(s.contentEquals("Start")) {
			System.out.println("Start button pressed");
			if(games != 0) {
					reset();
			      try { 
			         bw.write(act.getActionCommand() + "\n") ; 
			         bw.flush();  
			      } catch(Exception xx) { System.out.println(xx) ; } 
			}
		}
	}
   /**
     *  Do we have a winner?
     */
   boolean winner() { 
      return  line(b11,b21,b31) ||
         line(b12,b22,b32) ||
         line(b13,b23,b33) ||
         line(b11,b12,b13) ||
         line(b21,b22,b23) ||
         line(b31,b32,b33) ||
         line(b11,b22,b33) ||
         line(b13,b22,b31)  ;
   }

   /**
     *  Are three buttons marked with same player? 
     *  If, so color the line and return true.
     */
   boolean line(JButton b, JButton c, JButton d) { 
      if (!b.getText().equals("") &&b.getText().equals(c.getText()) &&
                c.getText().equals(d.getText()))  {
         if (b.getText().equals("O")) { 
            b.setBackground(Color.red) ;
            c.setBackground(Color.red) ;
            d.setBackground(Color.red) ;
         } 
         else { 
            b.setBackground(Color.green) ;
            c.setBackground(Color.green) ;
            d.setBackground(Color.green) ; 
         }
         return true ;  
      } 
      return false;  
      
   }
}
  
/*
If Java player closes GUI, then Prolog process is terminated.
Java process monitors "win" status of both players, signals a win,
and closes the connector and prolog player.
Prolog justs plays given position.
Write all of this up; it is interesting.
*/

