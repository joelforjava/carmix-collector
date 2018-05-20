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

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.ws.Action;

import org.apache.commons.lang3.StringUtils;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;

import com.joelforjava.service.CopyFileService;

/**
 *
 * @author joel_c
 */
public class CarMixCreatorGUI extends javax.swing.JFrame {

	/** Creates new form CarMixCreatorGUI */
    public CarMixCreatorGUI() {
    	copyService = new CopyFileService();
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

    Path playlistPath = Paths.get(strPlaylistFileName);
    Status status = processPlaylistPath(playlistPath);
    if (status == Status.PROC_SUCCESSFULLY) {
      //showAboutBox();
    }
  };

  private Status processPlaylistPath(Path path) {
	List<String> lines;
	try {
		lines = Files.readAllLines(path, StandardCharsets.ISO_8859_1);
		String firstLine = lines.remove(0);
		if (!M3U_HEADER.equals(firstLine)) {
			LOGGER.log(Level.WARNING, "M3U Header Not Found. for file: " + path.toString());
	        return Status.INVALID_HEADER; // preferably, throw error
		}
		LOGGER.log(Level.INFO, "M3U Header Found");
		for (String s : lines) {
			if (StringUtils.isBlank(s)) {
				continue;
			} else if (s.startsWith(M3U_INFO)) {
				continue;
				// processExtraInfo(s);
			} else {
				processTrackURL(s);
			}
		}
	} catch (IOException e) {
		e.printStackTrace();
	}
    return Status.PROC_SUCCESSFULLY;
	
  }
  
  private void processTrackURL(String strLine) {
	  Path source = Paths.get(strLine);
      if (Files.exists(source)) {
          // do stuff
          String albumName = "";
          String artistName = "";
          String fileName = source.getFileName().toString();
          String newFileName = this.getStrDestDirectoryName() + fileName;
          try {
              if (usingArtistName) {
                  MP3File mp3File = new MP3File(source.toFile(), false);
                  ID3v1 tag = mp3File.getID3v1Tag();
                  artistName = tag.getArtist();

                  newFileName = this.getStrDestDirectoryName() + artistName + "\\" + fileName;
              }
              Path target = Paths.get(newFileName);
              copyService.copy(source, target);
              String strLogInfo = "Copied: " + strLine + "\n to " + newFileName;
              setProgressInfoText(strLogInfo);
              LOGGER.log(Level.INFO, strLogInfo);
          }catch (TagException | IOException ex) {
              // Display new error message
              ex.getMessage();
              LOGGER.log(Level.SEVERE, null, ex);
          }
      } else {
        String strLogWarning = "File not found! - " + strLine;
        LOGGER.log(Level.WARNING, strLogWarning);
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

    private static final Logger LOGGER = Logger.getLogger(CarMixCreatorGUI.class.getName());

	private final CopyFileService copyService;
	
    public enum Status {
        INVALID_HEADER,
        PROC_SUCCESSFULLY
    }
}
