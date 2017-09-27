/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CarMixCreatorGUI.java
 *
 * Created on Jan 9, 2010, 2:41:34 PM
 */

package com.joelforjava;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.ws.Action;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;

/**
 *
 * @author joel_c
 */
public class CarMixCreatorGUI extends javax.swing.JFrame {

    /** Creates new form CarMixCreatorGUI */
    public CarMixCreatorGUI() {
        initComponents();
    }

  @Action
  public void selectPlaylistFile() {
    JFrame mainFrame = this;
    JFileChooser fileopen = new JFileChooser();
    FileFilter filter = new FileNameExtensionFilter("M3U files", "m3u");
    fileopen.addChoosableFileFilter(filter);

    int ret = fileopen.showDialog(mainFrame, "Open file");

    if (ret == JFileChooser.APPROVE_OPTION) {
      File file = fileopen.getSelectedFile();
      m3uFileNameField.setText(file.getAbsolutePath());
      this.setStrPlaylistFileName(m3uFileNameField.getText());
      System.out.println(file);
    }
  }

  @Action
  public void selectDestinationDirectory() {
    JFrame mainFrame = this;
    JFileChooser fileopen = new JFileChooser();
    fileopen.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    int ret = fileopen.showDialog(mainFrame, "Select Directory");

    if (ret == JFileChooser.APPROVE_OPTION) {
      File file = fileopen.getSelectedFile();
      String strDestinationPath = file.getAbsolutePath();
      if (!strDestinationPath.endsWith("\\")) {
          StringBuffer strBuffer = new StringBuffer(strDestinationPath);
          strBuffer.append("\\");
          strDestinationPath = strBuffer.toString();
      }
      destinationField.setText(strDestinationPath);
      this.setStrDestDirectoryName(destinationField.getText());
      System.out.println(file);
    }
  }

  @Action
  public void copyFilesToDestination() {
    if(strDestDirectoryName == null || "".equals(strDestDirectoryName)) {
      // throw alert up
      //showDirErrorBox();
    }
    if (strPlaylistFileName == null || "".equals(strPlaylistFileName)) {
      // throw alert up
      //showFileErrorBox();
    }

    File filPlaylist = new File(strPlaylistFileName);
    Status status = processPlaylistFile(filPlaylist);
    if (status == Status.PROC_SUCCESSFULLY) {
      //showAboutBox();
    }
  };

  private Status processPlaylistFile(File file) {
    try {
      BufferedReader input = new BufferedReader(new FileReader(file));
      String strLogInfo = "";

      try {
        String firstLine = input.readLine();
        String nextLine = null;
        if (!M3U_HEADER.equals(firstLine)) {
          strLogInfo = "Header Not Found for file: " + file.getName();
          Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.SEVERE, strLogInfo);
          return Status.INVALID_HEADER; // preferably, throw error
        }

        strLogInfo = "Header found for file: " + file.getName();
        Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.INFO, strLogInfo);
        setProgressInfoText(strLogInfo);

        boolean keepReading = true;
        do {
          nextLine = input.readLine();
          if (nextLine == null) {
              keepReading = false;
          } else if ("".equals(nextLine)) {
              continue;
          } else if (nextLine.startsWith(M3U_INFO)) {
              //processExtraInfo(nextLine);
          } else { // This should be a File URL
              processTrackURL(nextLine);
          }
        } while (keepReading);
      } finally {
        input.close();
      }
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }

    return Status.PROC_SUCCESSFULLY;
  }

  private void processTrackURL(String strLine) {
      // First, try strLine as if it were a complete file URL
      // i.e. C:\Music\etc
      if (checkFileOrDirectoryExists(strLine)) {
          // do stuff
          String albumName = "";
          String artistName = "";
          File inFile = new File(strLine);
          String fileName = inFile.getName();
          String newFileName = "";
          try {
              if (usingArtistName) {
                  MP3File mp3File = new MP3File(inFile, false);
                  ID3v1 tag = mp3File.getID3v1Tag();
                  artistName = tag.getArtist();

                  newFileName = this.getStrDestDirectoryName() + artistName + "\\" + fileName;
              } else {
                  newFileName = this.getStrDestDirectoryName() + fileName;
              }
              File outFile = new File(newFileName);
              copy(inFile, outFile);
              String strLogInfo = "Copied: " + strLine + "\n to " + newFileName;
              setProgressInfoText(strLogInfo);
              Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.INFO, strLogInfo);
          } catch (TagException te) {
              te.getMessage();
              Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.SEVERE, null, te);
          }catch (IOException ex) {
              // Display new error message
              ex.getMessage();
              Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.SEVERE, null, ex);
          }
      } else {
        String strLogInfo = "File not found! - " + strLine;
        Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.WARNING, strLogInfo);
      }
  }

  private boolean checkFileOrDirectoryExists(String strLine) {
      File file = new File(strLine);
      try {
          System.out.println(file.getCanonicalPath());
      } catch (IOException ioe) {

      }
      return file.exists();
  }

  /**
   * Performs verifications as follows:
   * 1. Verfies that a file or directory exists
   * 2. Verifies a file is a file or a directory is a directory
   * 3. Verifies that a file/directory can be read from or written to
   * @param aFile - file/directory to verify
   * @param indFileDir - indicates if aFile is a file or directory
   * @param indReadWrite - indicates if read or write functionality should be checked
   * @return
   * @throws java.io.IOException
   */
  private static boolean verifyFile(File aFile, String indFileDir, String indReadWrite) throws IOException {
    if (!aFile.exists()) {
      throw new IOException("File Verification: " + aFile.getPath() + " does not exist");
    }
    if (FILE_TYPE.equals(indFileDir)) {
      if (!aFile.isFile()) {
        throw new IOException("File Verification: " + aFile.getPath() + " does not exist");
      }
    } else if (DIR_TYPE.equals(indFileDir)) {
      if (!aFile.isDirectory()) {
        throw new IOException("Directory Verification: " + aFile.getPath() + " does not exist");
      }
    }
    if (READ_FILE.equals(indReadWrite)) {
      if (!aFile.canRead()) {
        throw new IOException("File Verification: Cannot read file " + aFile.getPath());
      }
    } else if (WRITE_FILE.equals(indReadWrite)){
      if (!aFile.canWrite()) {
        throw new IOException("File Verification: Cannot write to file " + aFile.getPath());
      }
    }
    return false;
  }
  
  public static void copy(File inFile, File outFile) throws IOException {

      if (inFile.getCanonicalPath().equals(outFile.getCanonicalPath())) {
        // inFile and outFile are the same;
        // hence no copying is required.
        Logger.getLogger(CarMixCreatorGUI.class.getName()).log(Level.INFO, "Files are the same, no copy performed");
        return;
      }

      verifyFile(inFile, FILE_TYPE, READ_FILE);

      if (outFile.isDirectory()) {
          outFile = new File(outFile, inFile.getName());
      }

      InputStream in = null;
      OutputStream out = null;

      if (outFile.exists()) {
          if (!outFile.canWrite()) {
              throw new IOException("Cannot write to: " + outFile);
          }
          // This should become a prompt for the GUI
          System.out.print("Overwrite existing file " + outFile.getName() + "? (Y/N): ");
          System.out.flush();
          BufferedReader promptIn = new BufferedReader(new InputStreamReader(System.in));
          String response = promptIn.readLine();
          if (!response.toUpperCase().equals("Y")) {
              throw new IOException("FileCopy: " + "existing file was not overwritten.");
          }
      } else {
          File dirFile = outFile.getParentFile();

          if (dirFile != null && (!dirFile.exists())) {
            if (!dirFile.mkdirs()) {
              if(!dirFile.exists()) {
                throw new IOException("Cannot create directory: " + dirFile);
              }
            }
          }

          // check for exists, isFile, canWrite
          verifyFile(inFile, FILE_TYPE, WRITE_FILE);
      }

      try {
        in = new BufferedInputStream(new FileInputStream(inFile));
        out = new BufferedOutputStream(new FileOutputStream(outFile));
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1){
            out.write(buffer, 0, bytesRead);
        } // write

      } finally {
        if (in != null) {
          try {
            in.close();
          } catch (Exception e) {
          }
        }
        if (out != null) {
          try {
            out.close();
          } catch (Exception e) {
          }
        }
      }
  }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        m3uFileNameField = new javax.swing.JTextField();
        destinationField = new javax.swing.JTextField();
        m3uFileLabel = new javax.swing.JLabel();
        destinationLabel = new javax.swing.JLabel();
        copyStatusProgressBar = new javax.swing.JProgressBar();
        progressInfoTextField = new javax.swing.JTextField();
        m3uFileSelectButton = new javax.swing.JButton();
        selectDestinationButton = new javax.swing.JButton();
        copyFilesButton = new javax.swing.JButton();
        usingArtistCheckbox = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        m3uFileLabel.setText("M3U File:");

        destinationLabel.setText("Destination:");

        progressInfoTextField.setBackground(new java.awt.Color(226, 226, 226));
        progressInfoTextField.setText("Make Selections, then click 'Copy Files'");
        progressInfoTextField.setBorder(null);

        m3uFileSelectButton.setText("Select");
        m3uFileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m3uFileSelectButtonActionPerformed(evt);
            }
        });

        selectDestinationButton.setText("Select");
        selectDestinationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectDestinationButtonActionPerformed(evt);
            }
        });

        copyFilesButton.setText("Copy Files");
        copyFilesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyFilesButtonActionPerformed(evt);
            }
        });

        usingArtistCheckbox.setText("Artist");
        usingArtistCheckbox.setToolTipText("Use Artist Name");
        usingArtistCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usingArtistCheckboxActionPerformed(evt);
            }
        });

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(destinationLabel)
                            .addComponent(m3uFileLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(m3uFileNameField, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(destinationField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                            .addComponent(usingArtistCheckbox, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addComponent(progressInfoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(copyFilesButton)
                    .addComponent(copyStatusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m3uFileSelectButton)
                    .addComponent(selectDestinationButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m3uFileNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(m3uFileLabel)
                    .addComponent(m3uFileSelectButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(destinationLabel)
                    .addComponent(selectDestinationButton))
                .addGap(46, 46, 46)
                .addComponent(usingArtistCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(copyFilesButton)
                        .addGap(18, 18, 18)
                        .addComponent(copyStatusProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(progressInfoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void m3uFileSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m3uFileSelectButtonActionPerformed
        // TODO add your handling code here:
        selectPlaylistFile();
    }//GEN-LAST:event_m3uFileSelectButtonActionPerformed

    private void selectDestinationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectDestinationButtonActionPerformed
        // TODO add your handling code here:
        selectDestinationDirectory();
    }//GEN-LAST:event_selectDestinationButtonActionPerformed

    private void copyFilesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyFilesButtonActionPerformed
        // TODO add your handling code here:
        copyFilesToDestination();
    }//GEN-LAST:event_copyFilesButtonActionPerformed

    private void usingArtistCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usingArtistCheckboxActionPerformed
        // TODO add your handling code here:
        if (usingArtistCheckbox.isSelected()) {
            setUsingArtistName(true);
        } else {
            setUsingArtistName(false);
        }
    }//GEN-LAST:event_usingArtistCheckboxActionPerformed

    private void setProgressInfoText(String text) {
        progressInfoTextField.setText(text);
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CarMixCreatorGUI().setVisible(true);
            }
        });
    }

  public String getStrDestDirectoryName() {
    return strDestDirectoryName;
  }

  public void setStrDestDirectoryName(String strDestDirectoryName) {
    this.strDestDirectoryName = strDestDirectoryName;
  }

  public String getStrPlaylistFileName() {
    return strPlaylistFileName;
  }

  public void setStrPlaylistFileName(String strPlaylistFileName) {
    this.strPlaylistFileName = strPlaylistFileName;
  }

  public boolean isUsingArtistName() {
      return usingArtistName;
  }

  public void setUsingArtistName(boolean usingArtistName) {
      this.usingArtistName = usingArtistName;
  }

  public boolean isUsingAlbumName() {
      return usingAlbumName;
  }

  public void setUsingAlbumName(boolean usingAlbumName) {
      this.usingAlbumName = usingAlbumName;
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton copyFilesButton;
    private javax.swing.JProgressBar copyStatusProgressBar;
    private javax.swing.JTextField destinationField;
    private javax.swing.JLabel destinationLabel;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel m3uFileLabel;
    private javax.swing.JTextField m3uFileNameField;
    private javax.swing.JButton m3uFileSelectButton;
    private javax.swing.JTextField progressInfoTextField;
    private javax.swing.JButton selectDestinationButton;
    private javax.swing.JCheckBox usingArtistCheckbox;
    // End of variables declaration//GEN-END:variables


    private String strPlaylistFileName;
    private String strDestDirectoryName;

    private boolean usingArtistName;
    private boolean usingAlbumName;

    private static final String M3U_HEADER = "#EXTM3U";
    private static final String M3U_INFO = "#EXTINF";

    private static final String READ_FILE = "R";
    private static final String WRITE_FILE = "W";

    private static final String DIR_TYPE = "D";
    private static final String FILE_TYPE = "F";

    public enum Status {
        INVALID_HEADER,
        PROC_SUCCESSFULLY
    }
}
