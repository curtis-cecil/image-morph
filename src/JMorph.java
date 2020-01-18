import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JMorph {
    JFrame frame;
    JPanel mainPanel;
    panelHandler handler;
    int animationDuration = 5;
    int gridResolution = 10;
    JFileChooser picker; //Maybe add in something to get working directory

    private BufferedImage prePic;
    private BufferedImage postPic;

    public static void main(String args[]){
        new JMorph();
    }

    public JMorph(){
        frame = new JFrame("JMorph");
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
        frame.add(mainPanel);

        buildMenus();
        buildControls();
        buildGrids();

        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener (new WindowAdapter() {
                                     public void windowClosing (WindowEvent e) {
                                         System.exit(0);
                                     }
                                 }
        );
    }

    private void buildMenus () {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenu settingsMenu = new JMenu("Settings");
        menuBar.add(settingsMenu);

        //Stuff with the file chooser
        picker = new JFileChooser();
        picker.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        picker.addChoosableFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "tif"));
        picker.setAcceptAllFileFilterUsed(false);

        JMenuItem openFileMenuItemPre = new JMenuItem("Open Pre Picture");
        openFileMenuItemPre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int validFile = picker.showOpenDialog(frame);
                if (validFile == JFileChooser.APPROVE_OPTION){
                    try{
                        prePic = ImageIO.read(picker.getSelectedFile());
                    } catch(IOException exc){
                        System.out.println("Problem reading file");
                    };
                    handler.setPrePic(prePic);
                }
            }
        });
        fileMenu.add(openFileMenuItemPre);

        JMenuItem openFileMenuItemPost = new JMenuItem("Open Post Picture");
        openFileMenuItemPost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int validFile = picker.showOpenDialog(frame);
                if (validFile == JFileChooser.APPROVE_OPTION){
                    try{
                        postPic = ImageIO.read(picker.getSelectedFile());
                    } catch(IOException exc){
                        System.out.println("Problem reading file");
                    };
                    handler.setPostPic(postPic);
                }
            }
        });
        fileMenu.add(openFileMenuItemPost);
        fileMenu.addSeparator();

        JMenuItem boostImage1 = new JMenuItem("Brighten Image 1");
        boostImage1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.boostPreImage();
            }
        });
        fileMenu.add(boostImage1);
        JMenuItem boostImage2 = new JMenuItem("Brighten Image 2");
        boostImage2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handler.boostPostImage();
            }
        });
        fileMenu.add(boostImage2);
        fileMenu.addSeparator();

        JMenuItem saveMorphMenuItem = new JMenuItem("Save Morph");
        saveMorphMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BufferedImage[] imageFrames = handler.generateTweenPics();
                for(int currentFrame = 0; currentFrame < imageFrames.length; currentFrame++){
                    try {
                        File outputFile = new File("frame" + currentFrame + ".png");
                        ImageIO.write(imageFrames[currentFrame], "png", outputFile);
                    } catch (IOException error) {
                    }
                }
            }
        });
        fileMenu.add(saveMorphMenuItem);

        fileMenu.addSeparator();

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);

        JMenuItem animationDurationMenuItem = new JMenuItem("Animation Duration");
        animationDurationMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String animationDurationChoice = (String)JOptionPane.showInputDialog(frame,
                        "Specify a time duration (secs).", "Animation Duration", JOptionPane.PLAIN_MESSAGE,
                        null, null, null);

                if ((animationDurationChoice != null) && (animationDurationChoice.length() > 0)) {
                    try{
                        int convertedInput = Integer.parseInt(animationDurationChoice);

                        if(convertedInput < 0){
                            JOptionPane.showMessageDialog(frame, "Value is below zero.");
                        }
                        else{
                            animationDuration = convertedInput;
                            handler.setDuration(animationDuration);
                        }
                    }

                    //catches if user input is not an integer
                    catch(Exception e){
                        JOptionPane.showMessageDialog(frame, "Value is not an integer.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(frame, "No value specified.");
                }
            }
        });
        settingsMenu.add(animationDurationMenuItem);

        JMenuItem gridResolutionMenuItem = new JMenuItem("Grid Resolution");
        gridResolutionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String gridResolutionChoice = (String)JOptionPane.showInputDialog(frame,
                        "Specify a grid resolution.", "Grid Resolution", JOptionPane.PLAIN_MESSAGE,
                        null, null, null);

                if ((gridResolutionChoice != null) && (gridResolutionChoice.length() > 0)) {
                    try{
                        int convertedInput = Integer.parseInt(gridResolutionChoice);

                        if(convertedInput < 0){
                            JOptionPane.showMessageDialog(frame, "Value is below zero.");
                        }
                        else{
                            gridResolution = convertedInput;
                            handler.changeResolution(gridResolution, gridResolution);
                        }
                    }

                    //catches if user input is not an integer
                    catch(Exception e){
                        JOptionPane.showMessageDialog(frame, "Value is not an integer.");
                    }
                }
                else{
                    JOptionPane.showMessageDialog(frame, "No value specified.");
                }
            }
        });
        settingsMenu.add(gridResolutionMenuItem);

        frame.setJMenuBar(menuBar);
    }

    private void buildControls(){
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        mainPanel.add(controlPanel);

        JButton previewButton = new JButton("Preview");
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                previewAnimation();
            }
        });
        controlPanel.add(previewButton);

        JButton morphButton = new JButton("Morph");
        morphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                morphAnimation();
            }
        });
        controlPanel.add(morphButton);
    }

    private void buildGrids(){
        handler = new panelHandler(gridResolution, gridResolution);
        mainPanel.add(handler);
    }

    private void morphAnimation(){
        JFrame morphFrame = new JFrame("JMorph [MORPH]");

        handler.animateGrid(morphFrame, false);

        morphFrame.pack();
        morphFrame.setVisible(true);
    }

    private void previewAnimation(){
        JFrame previewFrame = new JFrame("JMorph [PREVIEW]");

        handler.animateGrid(previewFrame, true);

        previewFrame.pack();
        previewFrame.setVisible(true);
    }
}