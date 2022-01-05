import java.awt.* ; 
import java.awt.event.* ; 
import java.io.* ; 
import java.net.* ;
import javax.swing.* ; 
import javax.swing.border.* ; 

public class TicTacToe1 extends JFrame 
                       implements ActionListener {
   JButton b11,b21,b31,
           b12,b22,b32,
           b13,b23,b33,
           reset;
   JLabel winner,scoreboard;
   boolean myturn;
   boolean bReset=false; //Default non-button pressed reset. I.e. End of game.
   BufferedReader br ; 
   BufferedWriter bw ;
   Thread connection ; 
   Process prologProcess ; 
   String prolog,ttt,boardState;
   int port,p1Score,p2Score,turnT;
   int turn=0,game=1;
   FileWriter fw;
   PrintWriter pw;

   /**
     *  Create a tic tac toe game, 
     *  prolog is the prolog command (e.g. "/opt/local/bin/swipl").
     *  ttt is the locator for ttt.pl (e.g. "/javalib/TicTacToe/ttt.pl").
     */
   public TicTacToe1(String prolog, String ttt, String port) { 
      this.prolog = prolog ; 
      this.ttt = ttt ; 
      this.port = Integer.parseInt(port);
      b11 = new JButton("") ; 
      b21 = new JButton("") ; 
      b31 = new JButton("") ; 
      b12 = new JButton("") ; 
      b22 = new JButton("") ; 
      b32 = new JButton("") ; 
      b13 = new JButton("") ; 
      b23 = new JButton("") ; 
      b33 = new JButton("") ; 
      reset = new JButton("Reset");
      b11.setActionCommand("(1,1).") ; // prolog reads pair term
      b21.setActionCommand("(2,1).") ; 
      b31.setActionCommand("(3,1).") ; 
      b12.setActionCommand("(1,2).") ; 
      b22.setActionCommand("(2,2).") ; 
      b32.setActionCommand("(3,2).") ; 
      b13.setActionCommand("(1,3).") ; 
      b23.setActionCommand("(2,3).") ; 
      b33.setActionCommand("(3,3).") ; 
      reset.setActionCommand("(0,0).") ;
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
      reset.setFont(f2) ;
      b11.addActionListener(this) ; 
      b21.addActionListener(this) ; 
      b31.addActionListener(this) ; 
      b12.addActionListener(this) ; 
      b22.addActionListener(this) ; 
      b32.addActionListener(this) ; 
      b13.addActionListener(this) ; 
      b23.addActionListener(this) ; 
      b33.addActionListener(this) ; 
      reset.addActionListener(this);
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
      panel.add(reset) ;
      panel.add(scoreboard);
      panel.add(winner);
      
      try {
		fw = new FileWriter("D:/Users/jerem/eclipse-workspace/a4part1/ttt1output.txt");
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
      this.myturn = true ; 

      Connector1 connector = new Connector1(this.port) ; 
      connector.start() ; 

      Socket sock ;
      try {
         sock = new Socket("127.0.0.1",this.port) ;
         br = new BufferedReader(new InputStreamReader(sock.getInputStream())) ; 
         bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())) ; 
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

      Thread shows = new Thread() { 
         public void run() { 
            setVisible(true) ;
         }
      } ;
      EventQueue.invokeLater(shows);

      // Start the prolog player

      try { 
         prologProcess = 
//         		Runtime.getRuntime().exec(prolog + " -f " + ttt) ; 
 		 		Runtime.getRuntime().exec(prolog + " -f " + ttt +" -- "+port) ; 
      } catch(Exception xx) {System.out.println(xx) ; }

      // On closing, kill the prolog process first and then exit
      this.addWindowListener(new WindowAdapter() { 
         public void windowClosing(WindowEvent w) { 
            if (prologProcess != null) prologProcess.destroy() ;
            pw.close();
            System.exit(0) ; 
         }
      }) ; 
   } 

//       /opt/local/bin/swipl   /javalib/TicTacToe/ttt.pl
   public static void main(String[] args) { 
      String prolog = "C:/Program Files/swipl/bin/swipl-win.exe" ;
      String ttt = "ttt1.pl"; 
	  String port = "54321";
      boolean noargs = true ; 
      try { 
         prolog = args[0] ;
         ttt = args[1] ;
         port = args[2];
         noargs = false ; 
      } 
      catch (Exception xx) {
         System.out.println("usage: java TicTactoe  <where prolog>  <where ttt>") ; 
      }
      if (noargs) { 
         Object[] message = new Object[6] ; 
         message[0] = new Label("  prolog command") ;
         message[1] = new JTextField(prolog) ; 
         message[2] = new Label("  where ttt.pl ") ;
         message[3] = new JTextField(ttt) ; 
         message[4] = new Label("  what port ") ;
         message[5] = new JTextField(port) ; 
         try { 
            int I = JOptionPane.showConfirmDialog(null,message,"Where are Prolog and ttt.pl? ",JOptionPane.OK_CANCEL_OPTION) ;  
            if (I == 2 | I == 1) System.exit(0) ;
            System.out.println(I) ; 
            new TicTacToe1(prolog = ((JTextField)message[1]).getText().trim(),ttt = ((JTextField)message[3]).getText().trim(),port=((JTextField)message[5]).getText().trim());  
         } catch(Exception yy) {} 
      }
      else
         new TicTacToe1(prolog,ttt,port) ; 
   }

   void reset() {
	   turn = 0;
	   turnT = 0;
	   game++;
	   pw.printf("********* NEW GAME ********");
	   pw.printf("Game: "+game);
       scoreboard.setText("<html> Wins<br> Player: "+p1Score+"<br> Computer: "+p2Score+"</htm>");
	 	  try { 
		         bw.write("(0,0).\n") ; 
		         bw.flush() ; 
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
	   myturn = true;
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
      pw.printf("T = " + turnT +"Player 1 takes ("+x+", " +y+").\t"+parseBoardString()+"\n");
      if (winner()) {
    	  reset(); //connection.stop();
      }
      else  myturn = true ;
   }

   /**
     * Java player
     */
   public void actionPerformed(ActionEvent act) { 
      if (!myturn) return ; // otherwise 
	  winner.setText("");
	  turn++;
	  turnT++;
	  if(turn == 5) {
    	  winner.setText("  A Tie!");
    	  reset();
	  } else {
	  String s = ((JButton)act.getSource()).getText() ; 
	  if (s.contentEquals("Reset")) {
		  try { 
			  bw.write(act.getActionCommand() + "\n") ; 
			  bw.flush() ;  
		  	} catch(Exception xx) { System.out.println(xx) ; } 
		  reset();
	  }
	  if (!s.equals("")) return  ; 
	  ((JButton)(act.getSource())).setText("X") ; 
	  try { 
		  bw.write(act.getActionCommand() + "\n") ; 
		  bw.flush() ;  
	  } catch(Exception xx) { System.out.println(xx) ; } 
	  myturn = false ; 
	  pw.printf("T = " + turnT  +"Player 1 takes "+act.getActionCommand()+"\t"+parseBoardString()+"\n");
	  if (winner()) {
		  reset(); //connection.stop();
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
            winner.setText("<html> Computer<br>   Wins!</html>");
            p2Score++;
         } 
         else { 
            b.setBackground(Color.green) ;
            c.setBackground(Color.green) ;
            d.setBackground(Color.green) ; 
            winner.setText("<html> Player<br>   Wins!</html>");
            p1Score++;
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

