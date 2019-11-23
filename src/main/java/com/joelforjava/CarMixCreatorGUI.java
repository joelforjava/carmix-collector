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

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.joelforjava.model.MusicFileData;
import com.joelforjava.processor.M3UPlaylistProcessor;
import com.joelforjava.processor.MusicFileDataExtractor;
import com.joelforjava.request.CopyRequest;
import com.joelforjava.service.CopyFileService;
import org.apache.commons.lang3.StringUtils;

/**
 * @author joel_c
 */
public class CarMixCreatorGUI {

    /**
     * Creates new form CarMixCreatorGUI
     */
    public CarMixCreatorGUI() {
        frame = new JFrame();
        copyService = new CopyFileService();
        playlistProcessor = new M3UPlaylistProcessor().withDataExtractor(new MusicFileDataExtractor());
        initComponents();
    }

    private void selectPlaylistFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter filter = new FileNameExtensionFilter(FILE_EXTENSION_DESCRIPTION, PERMITTED_EXTENSIONS);
        fileChooser.addChoosableFileFilter(filter);

        int ret = fileChooser.showDialog(getFrame(), DIALOG_SELECT_PLAYLIST_BUTTON_TEXT);

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            m3uFileNameField.setText(file.getAbsolutePath());
            this.setStrPlaylistFileName(m3uFileNameField.getText());
            System.out.println(file);
        }
    }

    private void selectDestinationDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int ret = fileChooser.showDialog(getFrame(), DIALOG_SELECT_DESTINATION_BUTTON_TEXT);

        if (ret == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String strDestinationPath = file.getAbsolutePath();
            if (!strDestinationPath.endsWith(FILE_SEPARATOR)) {
                strDestinationPath = strDestinationPath + FILE_SEPARATOR;
            }
            destinationField.setText(strDestinationPath);
            this.setStrDestDirectoryName(destinationField.getText());
            System.out.println(file);
        }
    }

    private void copyFilesToDestination() {
        String destDirectoryName = this.getStrDestDirectoryName();
        if (StringUtils.isBlank(destDirectoryName)) {
            // throw alert up
            //showDirErrorBox();
            return;
        }

        String playlistFileName = this.getStrPlaylistFileName();
        if (StringUtils.isBlank(playlistFileName)) {
            // throw alert up
            //showFileErrorBox();
            return;
        }

        Path playlistPath = Paths.get(playlistFileName);
        Status status = processPlaylist(playlistPath);
        if (status == Status.PROC_SUCCESSFULLY) {
            //showAboutBox();
            setProgressInfoText(PROGRESS_INFO_COMPLETE_LABEL_TEXT);
        }
    }

    private Status processPlaylist(Path path) {
        List<MusicFileData> musicFileData = playlistProcessor.withExtractArtist(this.isUsingArtistName()).process(path);
        for (MusicFileData entry : musicFileData) {
            processTrackData(entry);
        }
        return Status.PROC_SUCCESSFULLY;

    }

    private void processTrackData(MusicFileData fileData) {
        String fileDataUri = fileData.getUri();
        Path source = Paths.get(fileDataUri);
        if (Files.exists(source)) {
            try {
                final String newFileName = generateDestinationFileUri(source, fileData);
                Path target = Paths.get(newFileName);
                CopyRequest request = new CopyRequest(source, target, overwriteExisting);
                copyService.copy(request);
                String strLogInfo = "Copied: " + fileDataUri + "\n to " + newFileName;
                setProgressInfoText(strLogInfo);
                LOGGER.log(Level.INFO, strLogInfo);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        } else {
            String strLogWarning = "File not found! - " + fileDataUri;
            LOGGER.log(Level.WARNING, strLogWarning);
        }
    }

    private String generateDestinationFileUri(Path source, MusicFileData fileData) {
        String fileName = source.getFileName().toString();
        final String newFIleUri;
        if (this.isUsingArtistName()) {
            String artistName = fileData.getArtistName();
            newFIleUri = this.getStrDestDirectoryName() + artistName + FILE_SEPARATOR + fileName;
        } else {
            newFIleUri = this.getStrDestDirectoryName() + fileName;
        }
        return newFIleUri;
    }

    private void initMenuBar() {
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new JMenu();
        editMenu = new JMenu();
        helpMenu = new JMenu();

        fileMenu.setText(FILE_MENU_LABEL);
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(evt -> System.out.println("I'd close, but I don't know how to!"));
        fileMenu.add(exitMenuItem);
        jMenuBar1.add(fileMenu);

        editMenu.setText(EDIT_MENU_LABEL);
        jMenuBar1.add(editMenu);

        JMenu optionsMenu = new JMenu();
        optionsMenu.setText("Options");
        JMenuItem overwriteMenuItem = new JMenuItem(OVERWRITE_EXISTING_LABEL_TEXT);
        overwriteMenuItem.setEnabled(false);
        optionsMenu.add(overwriteMenuItem);
        jMenuBar1.add(optionsMenu);

        helpMenu.setText(HELP_MENU_LABEL);
        JMenuItem aboutMenuItem = new JMenuItem("About");
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        frame.setJMenuBar(jMenuBar1);

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     */
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
        usingArtistCheckbox = new JCheckBox();
        overwriteExistingCheckbox = new JCheckBox();

        initMenuBar();

        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        m3uFileLabel.setText(PLAYLIST_LABEL_TEXT);

        destinationLabel.setText(DESTINATION_LABEL_TEXT);

        progressInfoTextField.setBackground(new java.awt.Color(226, 226, 226));
        progressInfoTextField.setText(PROGRESS_INFO_LABEL_TEXT);
        progressInfoTextField.setBorder(null);

        m3uFileSelectButton.setText(MAIN_PLAYLIST_SELECT_BUTTON_TEXT);
        m3uFileSelectButton.addActionListener(evt -> selectPlaylistFile());

        selectDestinationButton.setText(MAIN_DESTINATION_SELECT_BUTTON_TEXT);
        selectDestinationButton.addActionListener(evt -> selectDestinationDirectory());

        copyFilesButton.setText(COPY_FILES_BUTTON_TEXT);
        copyFilesButton.addActionListener(evt -> copyFilesToDestination());

        usingArtistCheckbox.setText(USE_ARTIST_LABEL_TEXT);
        usingArtistCheckbox.setToolTipText(USE_ARTIST_TOOLTIP_TEXT);
        usingArtistCheckbox.addActionListener(this::usingArtistCheckboxActionPerformed);

        overwriteExistingCheckbox.setText(OVERWRITE_EXISTING_LABEL_TEXT);
        overwriteExistingCheckbox.setToolTipText(OVERWRITE_EXISTING_TOOLTIP_TEXT);
        overwriteExistingCheckbox.addActionListener(this::overwriteExistingCheckboxActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
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
                                                        .addComponent(overwriteExistingCheckbox, javax.swing.GroupLayout.Alignment.LEADING)
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
                                .addComponent(overwriteExistingCheckbox)
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

        frame.pack();
    }

    private void usingArtistCheckboxActionPerformed(java.awt.event.ActionEvent evt) {
        if (usingArtistCheckbox.isSelected()) {
            setUsingArtistName(true);
        } else {
            setUsingArtistName(false);
        }
    }

    private void overwriteExistingCheckboxActionPerformed(ActionEvent evt) {
        if (overwriteExistingCheckbox.isSelected()) {
            setOverwriteExisting(true);
        } else {
            setOverwriteExisting(false);
        }
    }

    private void setProgressInfoText(String text) {
        progressInfoTextField.setText(text);
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

    public boolean willOverwriteExisting() {
        return overwriteExisting;
    }

    public void setOverwriteExisting(boolean overwriteExisting) {
        this.overwriteExisting = overwriteExisting;
    }

    private JFrame getFrame() {
        return this.frame;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new CarMixCreatorGUI().getFrame().setVisible(true));
    }

    private javax.swing.JButton copyFilesButton;
    private javax.swing.JProgressBar copyStatusProgressBar;
    private javax.swing.JTextField destinationField;
    private javax.swing.JLabel destinationLabel;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JLabel m3uFileLabel;
    private javax.swing.JTextField m3uFileNameField;
    private javax.swing.JButton m3uFileSelectButton;
    private javax.swing.JTextField progressInfoTextField;
    private javax.swing.JButton selectDestinationButton;
    private JCheckBox usingArtistCheckbox;
    private JCheckBox overwriteExistingCheckbox;

    private String strPlaylistFileName;
    private String strDestDirectoryName;

    private boolean usingArtistName;
    private boolean overwriteExisting;

    private final CopyFileService copyService;

    private final M3UPlaylistProcessor playlistProcessor;

    private final JFrame frame;

    private static final Logger LOGGER = Logger.getLogger(CarMixCreatorGUI.class.getName());

    private static final String FILE_SEPARATOR = File.separator;

    private static final String FILE_EXTENSION_DESCRIPTION = "M3U files";

    private static final String[] PERMITTED_EXTENSIONS = { "m3u" };

    private static final String DIALOG_SELECT_PLAYLIST_BUTTON_TEXT = "Open file";

    private static final String DIALOG_SELECT_DESTINATION_BUTTON_TEXT = "Select Directory";

    private static final String PLAYLIST_LABEL_TEXT = "M3U File:";

    private static final String DESTINATION_LABEL_TEXT = "Destination:";

    private static final String COPY_FILES_BUTTON_TEXT = "Copy Files";

    private static final String PROGRESS_INFO_LABEL_TEXT = "Make Selections, then click '" + COPY_FILES_BUTTON_TEXT + "'";

    private static final String PROGRESS_INFO_COMPLETE_LABEL_TEXT = "Copying Complete!";

    private static final String MAIN_PLAYLIST_SELECT_BUTTON_TEXT = "Select";

    private static final String MAIN_DESTINATION_SELECT_BUTTON_TEXT = "Select";

    private static final String USE_ARTIST_LABEL_TEXT = "Artist";

    private static final String USE_ARTIST_TOOLTIP_TEXT = "Use Artist Name";

    private static final String OVERWRITE_EXISTING_LABEL_TEXT = "Overwrite Existing";

    private static final String OVERWRITE_EXISTING_TOOLTIP_TEXT = "Overwrite existing files in the destination directory";

    private static final String FILE_MENU_LABEL = "File";

    private static final String EDIT_MENU_LABEL = "Edit";

    private static final String HELP_MENU_LABEL = "Help";

    private static String OUTPUT_FORMAT = "{OUTPUT_DIR}" + FILE_SEPARATOR + "{ARTIST}" + FILE_SEPARATOR + "{SONG_NAME}";

    private static List<OutputFormatTokens> requiredTokens = Collections.singletonList(OutputFormatTokens.OUTPUT_DIR);

    public enum OutputFormatTokens {
        OUTPUT_DIR, ARTIST, SONG_NAME
        // TODO - add ALBUM_ARTIST, TRACK_NUM, and others that might be of use
    }
    public enum Status {
        INVALID_HEADER,
        PROC_SUCCESSFULLY,
        PROC_FAILED
    }
}
