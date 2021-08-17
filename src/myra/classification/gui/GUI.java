/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myra.classification.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

/**
 *
 * @author eyouness
 */
public class GUI extends javax.swing.JFrame {
  private static final String ATTRIBUTE = "@attribute";

    /**
     * Constant representing the data section.
     */
    private static final String DATA = "@data";

    /**
     * Constant representing the relation section.
     */
    private static final String RELATION = "@relation";

    /**
     * Constant representing a continuous value attribute.
     */
    private static final String CONTINUOUS = "continuous";

    /**
     * Constant representing a numeric value attribute.
     */
    private static final String NUMERIC = "numeric";

    /**
     * Constant representing a real value attribute.
     */
    private static final String REAL = "real";

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setText("Browse ");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTextField1.setText(" ");
        jTextField1.setEnabled(false);
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(110, 110, 110)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101)
                .addComponent(jButton1)
                .addContainerGap(150, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addContainerGap(472, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 10, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File("*.arff"));
            chooser.setDialogTitle("Browse the folder to process");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                
                jTextField1.setText(chooser.getSelectedFile().getPath());
                try {
                    read(jTextField1.getText());
                    System.out.println("myra.classification.gui.GUI.jButton1ActionPerformed()"+jTextField1.getText());
                } catch (IOException ex) {
                    Logger.getLogger(GUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                                jTextField1.setText("");

            }
     }//GEN-LAST:event_jButton1ActionPerformed
  private String[] split(String line) {
	String[] words = new String[0];
	int index = 0;

	while (index < line.length()) {
	    StringBuffer word = new StringBuffer();

	    boolean copying = false;
	    boolean quotes = false;
	    boolean brackets = false;

	    int i = index;

	    for (; i < line.length(); i++) {
		char c = line.charAt(i);

		if (!copying && !Character.isWhitespace(c)) {
		    copying = true;
		}

		if (c == '"' || c == '\'') {
		    quotes ^= true;
		} else if (c == '{' || c == '}') {
		    brackets ^= true;
		}

		if (copying) {
		    if (Character.isWhitespace(c) && !quotes && !brackets) {
			index = i + 1;
			break;
		    }

		    word.append(c);

		    // if (!(c == '"' || c == '\''))
		    // {
		    // word.append(c);
		    // }
		}
	    }

	    if (i >= line.length()) {
		// we reached the end of the line, need to stop the while loop
		index = i;
	    }

	    if (word.length() > 0) {
		words = Arrays.copyOf(words, words.length + 1);
		words[words.length - 1] = word.toString();
	    }
	}

	return words;
    }


    private boolean isComment(String line) {
	if (line.startsWith("%") || line.startsWith("#")) {
	    return true;
	}

	return false;
    }

    public void read(String  inputPath) throws IOException {
        FileReader fileReader =new FileReader(inputPath);
	BufferedReader reader = new BufferedReader(fileReader);
                int totalLinenumber=0;
	String line = null;

	while ((line = reader.readLine()) != null) {
            totalLinenumber++;
        }
	reader.close();

        FileWriter fstream = new FileWriter(inputPath+"1", true);
        BufferedWriter out = new BufferedWriter(fstream);
            FileWriter fstream2 = new FileWriter(inputPath+"2", true);
        BufferedWriter out2 = new BufferedWriter(fstream2);
    FileWriter fstream3 = new FileWriter(inputPath+"3", true);
        BufferedWriter out3 = new BufferedWriter(fstream3);

            fileReader =new FileReader(inputPath);

         reader = new BufferedReader(fileReader);
      
        line = null;
 double cusor=0;
 double part1=0;
 double part2=0;
 double part3=0;

	while ((line = reader.readLine()) != null) {
            cusor++;
            if (line!=null&&!line.equals("")){
	    String[] split = split(line);
            		split[0] = split[0].toLowerCase();
if (part1==0){
                out.write(line);
                    out.newLine();
                    out2.write(line);
                    out2.newLine(); 
                    out3.write(line);
                    out3.newLine();
}
		// are we dealing with an attribute?
		 if (split[0].startsWith(DATA)) {

                 double startdata=totalLinenumber-cusor;
                 part1= (cusor+(startdata*.6));
                 part2= ((startdata*0.2)+part1);
                 part3=((startdata*0.2)+part2);
                 


                 } 
                 else {
                     if (cusor<part1){
                      out.write(line);
                    out.newLine();
                     }else if (cusor<part2){
                       out2.write(line);
                    out2.newLine();
                     
                     }
                     else if (cusor<part3){
                       out3.write(line);
                    out3.newLine();
                     
                     }
                 }
                 
	    }
	}

	reader.close();
	out.close();
	out2.close();
	out3.close();

    }

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

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
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}