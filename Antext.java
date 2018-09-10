import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.event.KeyEvent;
//import java.awt.Component.*;

import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

public class Antext implements ActionListener, KeyListener, MouseListener {
   //FRAME
   private JFrame frame = new JFrame();
   
   //FONT
   private Font font = new Font("Times New Roman", Font.PLAIN, 20);
   
   //TEXT AREA
   private JTextArea txt = new JTextArea();
   private JTextArea res = new JTextArea();
   
   //INPUT AREA  
   private JTextField sch = new JTextField();
   private JTextField sch2 = new JTextField();
   private JTextField sch3 = new JTextField();
   private JTextField findAllOccur = new JTextField();
   //HANDLE DEACTIVE EVENT
   private boolean active = false;

   //SCROLLBAR
   private JScrollPane scroll = new JScrollPane (txt, 
           JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
   private JScrollPane scroll2 = new JScrollPane (res, 
           JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
           JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
              
   //HIGHLIGHT   
   private Highlighter hl = txt.getHighlighter();
   private HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.yellow);
   private int clickCount = 0;
   
   //DISPLAY/BUTTONS        
   private String display = "";
   private String goButton = "Analyze";
   private String clearButton = "Clear";
   private String searchButton = "Initialize Search";
   private String searchAllButton = "Find All";
   private String replaceAllButton = "Replace All";
   private String browseButton = "Browse...";
   private String saveButton = "Save";
   private String forwardButton = ">>>>";
   private String backwardButton = "<<<<";
   
   //Data
   private ArrayList<String> wordlib = new ArrayList<String>(); 
   private ArrayList<String> chlib = new ArrayList<String>(); 
   private String lineStr;
   private String wordStr;
   private String chStr;
   private int num_of_lines = 0;
   private int words = 0;
   private int chs = 0;
   private int uniqueChs = 0;
   private int uniqueWords = 0;
   private int count;
   
   //Search
   private ArrayList<Integer> point1 = new ArrayList<Integer>();   //For search
   private ArrayList<Integer> point2 = new ArrayList<Integer>();   //For search
   private int searchCounter = 0;
   private boolean searchActivated = false; 
   
   //File
   private String fileNamex = "";
   private String filePathx = "";
   
   //MAIN 
   public static void main(String[] args)
            throws FileNotFoundException {
                 
      Antext tp = new Antext();
   }

   public Antext(){      
      tpGui();     
   }
   
   //USER ACTION
   public void actionPerformed(ActionEvent event) {
      String pressed = event.getActionCommand();
            
      //Go
      if(goButton.equals(pressed)){
         display = txt.getText();
         processInfo();
         sysPrint();
         resultDisplay();
         res.setCaretPosition(0);            
      }      
      //Clear
      if(clearButton.equals(pressed)){
         cleardata();
         txt.setText("");
         res.setText("");         
      }
      //Search
      if(searchButton.equals(pressed)){
         String userSearchAll = sch.getText();
         String txtGrabAll = txt.getText();
         int p0 = txtGrabAll.indexOf(userSearchAll);
         int p1 = p0 + userSearchAll.length();         
         //Set search indicator
         searchActivated = true;
         //Reset past search data.
         hl.removeAllHighlights();
         point1.clear();
         point2.clear();
         searchCounter = 0;
         
         //If user search is not empty, highlight
         if(userSearchAll.length() > 0){
         
            try {
               hl.addHighlight(p0,p1,painter);
            } catch(BadLocationException e) {
               System.out.println(e.toString());
            }
         
            int j = 0;
            
            //Search for all occurances   
            for(int i = 0; i<txtGrabAll.length(); i++) {
   
            	if(txtGrabAll.charAt(i) == userSearchAll.charAt(j)){
            		j++;                        
               } else if(txtGrabAll.charAt(i) == userSearchAll.charAt(0)){
                  j = 1;   //Adjustment EX: "maman"
               } else {    //Else restart count
                  j = 0;
               }	

               //If found add to point array and reset count.
	            if(j == userSearchAll.length()){
	             	point1.add(i + 1 - userSearchAll.length());
		            point2.add(i + 1 - userSearchAll.length() + userSearchAll.length());
		            j = 0;
	            }
             }
           } else {
               searchActivated = false;
           } 
         }
      
      //Forward to next searched word.
      if(forwardButton.equals(pressed)) {
         if(searchActivated == true){
            //Remove last hl
            hl.removeAllHighlights();
            //Allows for looping to first found.
            if(searchCounter < point1.size()-1){
	            searchCounter++;
         	} else {
               searchCounter = 0;
            }
            //HL
            int p1 = point1.get(searchCounter);	
         	int p2 = point2.get(searchCounter);
            try {
               hl.addHighlight(p1,p2,painter);
               txt.setCaretPosition(p1);
            } catch(BadLocationException e) {
               //..
            }             
         }
      }

      //Backwards to last searched word.
      if(backwardButton.equals(pressed)) {
         if(searchActivated == true){
            hl.removeAllHighlights();
            //Allows looping to last found;
            if(searchCounter > 0){  
         	   searchCounter--;
            } else {
               searchCounter = point1.size()-1;
            }   
         	int p1 = point1.get(searchCounter);	
           	int p2 = point2.get(searchCounter);
            try {
               hl.addHighlight(p1,p2,painter);
               txt.setCaretPosition(p1);
            } catch(BadLocationException e) {
               //..
            } 
         }             
      }
      
      
      //Search All
      if(searchAllButton.equals(pressed)) {
         String userSearchAll = findAllOccur.getText();
         String txtGrabAll = txt.getText();         
         int p00 = 0;
         int p11 = 0;         
         hl.removeAllHighlights();
         point1.clear();
         point2.clear();         
         int found = 0;
         //Disable single search.
         searchActivated = false;

         if(userSearchAll.length() > 0){         

            for(int i = 0; i<txtGrabAll.length(); i++) {

            	if(txtGrabAll.charAt(i) == userSearchAll.charAt(found)){
            		found++;                        
               } else if(txtGrabAll.charAt(i) == userSearchAll.charAt(0)){
                  found = 1;
               } else {
                  found = 0;
               }	

	            if(found == userSearchAll.length()){
	            	 p00 = i - userSearchAll.length() + 1;
	            	 p11 = p00 + userSearchAll.length();
                      
                   try {
                       hl.addHighlight(p00,p11,painter);
                       System.out.println("index: " + p00 + ", " + p11);
                   } catch(BadLocationException e) {
                       System.out.println(e.toString());
                   } 
	                found = 0;
	            }
            }
            txt.setCaretPosition(0);   
          }     
       }
              
       //Replace All
       if(replaceAllButton.equals(pressed)){
         active = true;
         String msg = "(YES) to replace whole input -- (NO)  to replace all occurrences";
         int ans = JOptionPane.showConfirmDialog(frame, msg);
            
         if(ans == JOptionPane.YES_OPTION){
            replaceInputOnly();   
         } else if(ans == JOptionPane.NO_OPTION){
            replaceAllCase();
         }
         active = false;
       }   
      
       //Browse
       if(browseButton.equals(pressed)){
         active = true;
         JFileChooser fc = new JFileChooser();
         int returnVal = fc.showOpenDialog(frame);
         String result = "";
         String errormsg = "File selected must be a .txt";
         
         if(returnVal == JFileChooser.APPROVE_OPTION){
            File file = fc.getSelectedFile();
            
            if( file.getName().substring(file.getName().indexOf("."), file.getName().length()).equals(".txt")){
               filePathx = file.getPath();
               fileNamex = file.getName();
               try {
                  Scanner s = new Scanner(file);
                  while(s.hasNextLine()){
                     result += s.nextLine() + '\n';
                  }
                  res.setText(""); 
               } catch (FileNotFoundException e) {
                  System.out.println("NO SUCH FILE!");
               }
               txt.setText(result.substring(0,result.length()-1));                
            } else {
               JOptionPane.showMessageDialog(new JFrame(), errormsg, "ERROR!",
                    JOptionPane.ERROR_MESSAGE);
            }
         }
         active = false;
       }
       
       if(saveButton.equals(pressed)){
         active = true;
         String msg = "Save to original " + filePathx + " or (no) to save to a different file";
                      
         int ans = JOptionPane.showConfirmDialog(frame, msg);             
         if(ans == JOptionPane.YES_OPTION) {              
                  
            try {
               PrintStream output = new PrintStream(new File(filePathx));
               Scanner s = new Scanner(txt.getText());
               String result = "";
               while(s.hasNextLine()){
                  result = s.nextLine();
                  output.println(result);
               }
                 
            } catch (IOException e) {
                  System.out.println(e.toString());
            }
         } else if(ans == JOptionPane.NO_OPTION){
                  JFileChooser fc = new JFileChooser();
                  int returnVal = fc.showOpenDialog(frame);
                  String result = "";
                  if(returnVal == JFileChooser.APPROVE_OPTION){
                     File file = fc.getSelectedFile();
            
                     if( file.getName().substring(file.getName().indexOf("."), file.getName().length()).equals(".txt")){
                         filePathx = file.getPath();
                         fileNamex = file.getName();
                         try {
                           PrintStream output = new PrintStream(new File(filePathx));
                           Scanner s = new Scanner(txt.getText());
                           while(s.hasNextLine()){
                              result = s.nextLine();
                              output.println(result);
                           }
                         } catch (IOException e) {
                            System.out.println("NO SUCH FILE!");
                         }                
                     } else {
                        JOptionPane.showMessageDialog(new JFrame(), "BAD", "ERROR!",
                            JOptionPane.ERROR_MESSAGE);
                     }
                }

         }
         active = false;
       }
       //Keep cursor at end    
       sch.setCaretPosition((sch.getText()).length());
       sch2.setCaretPosition((sch2.getText()).length());
       sch3.setCaretPosition((sch3.getText()).length()); 
       findAllOccur.setCaretPosition((findAllOccur.getText()).length());
   }  
           
   //PROCESS USER TXT INFO
   public void processInfo(){
      Scanner line = new Scanner(display);           

      while(line.hasNextLine()){
         lineStr = line.nextLine();
         Scanner word = new Scanner(lineStr);  
         while(word.hasNext()){
            wordStr = word.next();
            if(!wordlib.contains(wordStr)){
               uniqueWords++;
               wordlib.add(wordStr);
            }
            count = 0;
            while(count < wordStr.length()){
               chStr = wordStr.charAt(count) + "";
               if(!chlib.contains(chStr)){
                  uniqueChs++;
                  chlib.add(chStr);
               }
               chs++;
               count++;
            }   
            words++;            
         }
         num_of_lines++;      
      }
      Collections.sort(wordlib);
      Collections.sort(chlib);
   }
  
   //REPLACEALLCASE
   public void replaceAllCase(){
         String userSearchAll = sch2.getText();
         String replaceAll = sch3.getText();
         String txtGrabAll = txt.getText();

         if(txt.getSelectedText() != null){
            int start = txt.getSelectionStart();
            int end = txt.getSelectionEnd();
            String uno = txtGrabAll.substring(0, start);
            String doso = txtGrabAll.substring(end, txtGrabAll.length());
            
            txtGrabAll = uno + txtGrabAll.substring(start, end).replaceAll(userSearchAll, replaceAll) + doso;           
            txt.setText("");
            cleardata();        
            txt.setText(txtGrabAll);  
         } else if(userSearchAll.length() > 0){
            txtGrabAll = txtGrabAll.replaceAll(userSearchAll, replaceAll);           
            txt.setText("");
            cleardata();        
            txt.setText(txtGrabAll);         
         }          
   }
   
   //REPLACE USER INPUT ONLY
   public void replaceInputOnly(){
         String userSearchAll = sch2.getText();
         String replaceAll = sch3.getText();
         String txtGrabAll = txt.getText();
         String combine = "";
          
         if(txt.getSelectedText() != null){
            int start = txt.getSelectionStart();
            int end = txt.getSelectionEnd();
            String uno = txtGrabAll.substring(0, start);
            String doso = txtGrabAll.substring(end, txtGrabAll.length());
            
            combine = uno + replaceAtIdx(txtGrabAll.substring(start, end), userSearchAll, replaceAll) + doso;           
            txt.setText("");
            cleardata();        
            txt.setText(combine);  
         } else if(userSearchAll.length() > 0) {                 
            txt.setText("");
            cleardata(); 
            combine = replaceAtIdx(txtGrabAll, userSearchAll, replaceAll);       
            txt.setText(combine);       
         }      
   }  
   
   //Custom User Input replacement.
   public String replaceAtIdx(String text, String target, String replacement){
      String result = "";
    
      Scanner scan = new Scanner(text);
      while(scan.hasNext()) {
        ArrayList<Integer> start = new ArrayList<Integer>();
        ArrayList<Integer> end = new ArrayList<Integer>();
         
        String line = scan.nextLine();
        int linelen = line.length();
      
        int count = 0;
        for(int i = 0; i < line.length(); i++) {              
            if(line.charAt(i) == target.charAt(count)){
                count++;                        
            } else {
                count = 0;
            }	         
            System.out.println("Count: " + count + " i: " + i);
            
            if(count == target.length() && i == target.length()-1 && line.length() == target.length()) {   //only one word
               start.add(0);
               end.add(target.length());
               count = 0;
               System.out.println("Enter 0");
            } else if(count == target.length() && i == target.length()-1 && line.length() > target.length()){     //first text at idx 0 then space
               if(target.length() <= line.length() && (line.charAt(target.length()) == ' ' || line.charAt(target.length()) == '\t') ){
                  start.add(0);
                  end.add(target.length());
                  count = 0;
                  System.out.println("Enter 1"); 
               } else if(target.length() <= line.length() && (line.charAt(target.length()) != ' ' || line.charAt(target.length()) != '\t') ) {
                  count = 0;
               }
               
               System.out.println("Visit 1");
            } else if(count == target.length() && i == line.length()-1) {  //text at last word
               if(line.charAt(i-target.length()) == ' ' || line.charAt(i-target.length()) == '\t' ){
                  start.add(line.length()-target.length());
                  end.add(line.length());
                  count = 0;
                  System.out.println("Enter 2");
               } else if(line.charAt(i-target.length()) != ' ' || line.charAt(target.length()) != '\t' ) {
                  count = 0;
               }
               
                System.out.println("visit 2");
            } else {                      //The middle case (space or tab) text (space or tab)
               if(count == target.length() && (line.charAt(i-target.length()) == ' ' || line.charAt(i-target.length()) == '\t') 
                     && (line.charAt(i+1) == ' ' || line.charAt(i+1) == '\t') ){
                     
	               start.add(i - target.length() + 1);
		            end.add(i - target.length() + 1 + target.length());
		            count = 0;
                  System.out.println("Enter 3");
	            } else if(count == target.length() && (line.charAt(i+1) != ' ' || line.charAt(i+1) != '\t') ){ 
                  count = 0;
               }
                
               System.out.println("Visit 3");
            }             
        }
            
        int offset = 0;
        for(int x = 0; x < start.size(); x++) {
            if(x > 0){   //adjusts after replacing, scales by adding to itself.
               offset += replacement.length() - target.length();
            }
            
            String sub1 = line.substring(0, start.get(x) + offset);
            String sub2 = line.substring(end.get(x) + offset, line.length());
            line = sub1 + replacement + sub2;
        }
        //Add to result
        result = result + line + '\n';
      }     
      //Remove extra '\n' at end.
      result = result.substring(0,result.length()-1); 
      return result;         
   }
   
      
   //CLEAR ALL DATA
      public void cleardata(){
      wordlib.clear();
      chlib.clear();
      lineStr = "";
      wordStr = "";
      chStr = "";
      num_of_lines = 0;
      words = 0;
      chs = 0;
      uniqueChs = 0;
      uniqueWords = 0;
      count = 0;
      //search
      searchActivated = false;
      searchCounter = 0;
      point1.clear();
      point2.clear();
   }
   
   //SYS DISPLAY
   public void sysPrint() {   
      System.out.println(display + "\n");             
      System.out.println("# of lines: " + num_of_lines);
      System.out.println("# of words: " + words);
      System.out.println("# of chs: " + chs);
      System.out.println("# of unique words: " + uniqueWords);
      System.out.println("# of unique chs: " + uniqueChs);
      System.out.println("WORDs: " + wordlib);
      System.out.println("CHs: " + chlib);
   }
   
   //GUI DISPLAY
   public void resultDisplay(){
       res.setText("# of lines: " + num_of_lines + "\n" +
         "# of words: " + words + "\n" +
         "# of chs: " + chs + "\n" +
         "# of unique words: " + uniqueWords + "\n" +
         "# of unique chs: " + uniqueChs + "\n\n" +
         "WORDs: " + wordlib + "\n\n" +
         "CHs: " + chlib);
   }
   
   //Mouse
   public void mouseClicked(MouseEvent e) {
      if(clickCount == 2){
		   hl.removeAllHighlights();
         clickCount = 0;
      } else {
         clickCount++;      
      }
	}
   public void mouseEntered(MouseEvent e){   
   }
   public void mouseExited(MouseEvent e){   
   }
   public void mousePressed(MouseEvent e){   
   }
   public void mouseReleased(MouseEvent e){   
   }
   
   //Keyboard input actions
   public void keyPressed(KeyEvent e){ 
      res.setText("");   
   }    
   public void keyTyped(KeyEvent e) {
   }
   public void keyReleased(KeyEvent e) {
   }   
   
   //GUI
   public void tpGui(){
      frame.addWindowListener(new WindowAdapter() {
         @Override
         public void windowDeactivated(WindowEvent wEvt) {
            frame.setState(Frame.ICONIFIED);
         }
      });
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      frame.setSize(screenSize.width, screenSize.height-40);
      frame.setTitle("Antext");
      frame.setLayout(new BorderLayout());
      frame.setLocationRelativeTo(null);
      
      //TEXTAREA GUI - INPUT
      JPanel westPanel = new JPanel(new GridLayout(1,1));
      westPanel.add(scroll);
      txt.setColumns(65);
      txt.setLineWrap(true);
      txt.addKeyListener(this);
      txt.addMouseListener(this);
      txt.setFont(font);
      frame.add(westPanel, BorderLayout.WEST);
      
      //TEXTAREA 1 GUI - RESULTS
      JPanel eastPanel = new JPanel(new GridLayout(1,2));
      eastPanel.add(scroll2);
      res.setColumns(65);
      res.setLineWrap(true);
      res.setEditable(false);
      res.setFont(font);
      res.setForeground(Color.green);
      res.setBackground(Color.black);
      frame.add(eastPanel, BorderLayout.EAST);      
      
      //INPUT AREA - BOTTOM  
      JPanel southPanel = new JPanel(new GridLayout(14,1));
      
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));     
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      buttonCreate(goButton, southPanel);
      buttonCreate(clearButton, southPanel);
      
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));     
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(findAllOccur);
      buttonCreate(searchAllButton, southPanel);

      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(sch);
      buttonCreate(searchButton, southPanel);
      buttonCreate(backwardButton, southPanel, Color.lightGray);
      buttonCreate(forwardButton, southPanel, Color.lightGray);      

      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(new JLabel("Target", SwingConstants.CENTER));
      southPanel.add(new JLabel("Replacement", SwingConstants.CENTER));
      southPanel.add(sch2);
      southPanel.add(sch3);
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));     
      buttonCreate(replaceAllButton, southPanel);
      
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      buttonCreate(browseButton, southPanel);
      southPanel.add(new JLabel(" ", SwingConstants.CENTER));
      buttonCreate(saveButton, southPanel);

      frame.add(southPanel, BorderLayout.SOUTH);
      
      frame.setAlwaysOnTop(false);
      frame.setAutoRequestFocus(true);
      frame.setVisible(true);   
   }
   
  //Creates button in panel.
  public void buttonCreate(String name, JPanel panel){
     JButton button = new JButton(name);
     button.addActionListener(this);
     panel.add(button);   
  }  
  //BUTTON WITH COLOR!!
  public void buttonCreate(String name, JPanel panel, Color cr){
     JButton button = new JButton(name);
     button.addActionListener(this);
     button.setBackground(cr);
     panel.add(button);   
  }  
}