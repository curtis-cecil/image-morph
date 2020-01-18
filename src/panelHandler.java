import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.util.Timer;
import java.util.TimerTask;

public class panelHandler extends JPanel {
    public gridPanel panel1;
    public gridPanel panel2;

    private int xSize;
    private int ySize;

    private int x;
    private int y;

    private boolean edgeflag1;
    private boolean edgeflag2;

    Timer timer;

    MorphTools morphTools = new MorphTools();

    Triangle[][][] preTriangles;
    Triangle[][][] postTriangles;

    int ticker = 0;

    int animationDuration = 5;
    int frames = 20;

    BufferedImage prePic;
    BufferedImage postPic;
    BufferedImage[] tweenPics;

    //Constructor
    public panelHandler(int x, int y){

        xSize = x;
        ySize = y;

        edgeflag1 = false;
        edgeflag2 = false;

        makeGrids();

        add(panel1);
        add(panel2);

        setListeners();
    }



    /*
    -Creates 2 new gridPanel objects
    -Is its own function so I didn't have to copy the same code twice
     in the constructor and in changeResolution
    */
    public void makeGrids(){
        panel1 = new gridPanel(xSize, ySize);
        panel2 = new gridPanel(xSize, ySize);
    }



    /*
    -The listeners are put on out here to make it easier for the 2 gridPanels
     to communicate with each other
    */
    private void setListeners(){
        controlPoint[][] p1 = panel1.getPoints();
        controlPoint[][] p2 = panel2.getPoints();

        //Sets the mouse drag listeners to redraw the lines and the point
        for (int i = 1; i < xSize+1; i++){
            for (int j = 1; j < ySize+1; j++){
                x = i;
                y = j;
                p1[x][y].addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        controlPoint clickedPoint = (controlPoint)e.getSource();
                        int tempX = clickedPoint.getRowID();
                        int tempY = clickedPoint.getColID();

                        //Get distance to n, get dist to ne
                        //check to see if dist n + dist ne  = dist n-> ne

                        double NtoE = Dist(p1[tempX][tempY].north, p1[tempX][tempY].east);
                        double EtoSE = Dist(p1[tempX][tempY].east, p1[tempX][tempY].southeast);
                        double SEtoS = Dist(p1[tempX][tempY].southeast, p1[tempX][tempY].south);
                        double StoW = Dist(p1[tempX][tempY].south, p1[tempX][tempY].west);
                        double WtoNW = Dist(p1[tempX][tempY].west, p1[tempX][tempY].northwest);
                        double NWtoN = Dist(p1[tempX][tempY].northwest, p1[tempX][tempY].north);

                        double toN = Dist(p1[tempX][tempY], p1[tempX][tempY].north);
                        double toE = Dist(p1[tempX][tempY], p1[tempX][tempY].east);
                        double toSE = Dist(p1[tempX][tempY], p1[tempX][tempY].southeast);
                        double toS = Dist(p1[tempX][tempY], p1[tempX][tempY].south);
                        double toW = Dist(p1[tempX][tempY], p1[tempX][tempY].west);
                        double toNW = Dist(p1[tempX][tempY], p1[tempX][tempY].northwest);

                        if(toN + toE == NtoE || toE + toSE == EtoSE || toSE + toS == SEtoS || toS + toW == StoW || toW + toNW == WtoNW || toNW + toN == NWtoN){
                            edgeflag1 = true;
                        }

                        if (!edgeflag1) {
                            p1[tempX][tempY].setLocation(e.getX() + p1[tempX][tempY].getTrueXPos() - 5, e.getY() + p1[tempX][tempY].getTrueYPos() - 5);
                            p1[tempX][tempY].relocate();
                            panel1.drawStuff();
                        }
                    }
                });
                p1[x][y].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        controlPoint clickedPoint = (controlPoint)e.getSource();
                        int tempX = clickedPoint.getRowID();
                        int tempY = clickedPoint.getColID();

                        p1[tempX][tempY].setActive();
                        p2[tempX][tempY].setActive();
                        panel1.drawStuff();
                        panel2.drawStuff();
                    }
                });
                p1[x][y].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        controlPoint clickedPoint = (controlPoint)e.getSource();
                        int tempX = clickedPoint.getRowID();
                        int tempY = clickedPoint.getColID();

                        p1[tempX][tempY].setInactive();
                        p2[tempX][tempY].setInactive();
                        panel1.drawStuff();
                        panel2.drawStuff();
                        edgeflag1 = false;
                    }
                });
            }
        }

        for (int i = 1; i < xSize+1; i++){
            for (int j = 1; j < ySize+1; j++){
                x = i;
                y = j;
                p2[x][y].addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {
                        controlPoint clickedPoint = (controlPoint)e.getSource();
                        int tempX = clickedPoint.getRowID();
                        int tempY = clickedPoint.getColID();

                        double NtoE = Dist(p2[tempX][tempY].north, p2[tempX][tempY].east);
                        double EtoSE = Dist(p2[tempX][tempY].east, p2[tempX][tempY].southeast);
                        double SEtoS = Dist(p2[tempX][tempY].southeast, p2[tempX][tempY].south);
                        double StoW = Dist(p2[tempX][tempY].south, p2[tempX][tempY].west);
                        double WtoNW = Dist(p2[tempX][tempY].west, p2[tempX][tempY].northwest);
                        double NWtoN = Dist(p2[tempX][tempY].northwest, p2[tempX][tempY].north);

                        double toN = Dist(p2[tempX][tempY], p2[tempX][tempY].north);
                        double toE = Dist(p2[tempX][tempY], p2[tempX][tempY].east);
                        double toSE = Dist(p2[tempX][tempY], p2[tempX][tempY].southeast);
                        double toS = Dist(p2[tempX][tempY], p2[tempX][tempY].south);
                        double toW = Dist(p2[tempX][tempY], p2[tempX][tempY].west);
                        double toNW = Dist(p2[tempX][tempY], p2[tempX][tempY].northwest);

                        if(toN + toE == NtoE || toE + toSE == EtoSE || toSE + toS == SEtoS || toS + toW == StoW || toW + toNW == WtoNW || toNW + toN == NWtoN){
                            edgeflag2 = true;
                        }
                        if (!edgeflag2) {
                            p2[tempX][tempY].setLocation(e.getX() + p2[tempX][tempY].getTrueXPos() - 5, e.getY() + p2[tempX][tempY].getTrueYPos() - 5);
                            p2[tempX][tempY].relocate();
                            panel2.drawStuff();
                        }
                    }
                });
                p2[x][y].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        controlPoint clickedPoint = (controlPoint)e.getSource();
                        int tempX = clickedPoint.getRowID();
                        int tempY = clickedPoint.getColID();

                        p2[tempX][tempY].setActive();
                        p1[tempX][tempY].setActive();
                        panel1.drawStuff();
                        panel2.drawStuff();
                    }
                });
                p2[x][y].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        controlPoint clickedPoint = (controlPoint)e.getSource();
                        int tempX = clickedPoint.getRowID();
                        int tempY = clickedPoint.getColID();

                        p2[tempX][tempY].setInactive();
                        p1[tempX][tempY].setInactive();
                        panel1.drawStuff();
                        panel2.drawStuff();
                        edgeflag2 = false;
                    }
                });
            }
        }
    }



    //Finds the distance between 2 points
    private double Dist(controlPoint a, controlPoint b){
        return Math.sqrt(Math.pow(a.getTrueXPos() - b.getTrueXPos(), 2) + Math.pow(a.getTrueYPos() - b.getTrueYPos(), 2));
    }


    //Called when the user changes the resolution of the grid
    public void changeResolution(int x, int y){
        panel1.setVisible(false);
        panel2.setVisible(false);
        remove(panel1);
        remove(panel2);

        xSize = x;
        ySize = y;

        makeGrids();

        add(panel1);
        add(panel2);

        setListeners();
    }



    //These 2 send the pictures down one more layer
    public void setPrePic(BufferedImage pic){
        prePic = pic;
        panel1.setPic(pic);
    }
    public void setPostPic(BufferedImage pic){
        postPic = pic;
        panel2.setPic(pic);
    }


    public void animateGrid(JFrame targetFrame, boolean isPreview){
        gridPanel previewPanel = new gridPanel(xSize, ySize);
        previewPanel.drawStuff();

        ticker = 0;

        int gridRows = previewPanel.getRows();
        int gridCols = previewPanel.getCols();

        previewPanel.setControlPoints(panel1.getPoints());

        controlPoint previewControlPoints[][] = previewPanel.getPoints();
        controlPoint beginControlPoints[][] = panel1.getPoints();
        controlPoint endControlPoints[][] = panel2.getPoints();

        targetFrame.add(previewPanel);

        //code for the PREVIEW
        if(isPreview){
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    for(int currentRow = 0; currentRow < gridRows; currentRow++){
                        for(int currentCol = 0; currentCol < gridCols; currentCol++){
                            double currentPointXPos = previewControlPoints[currentRow][currentCol].getTrueXPos();
                            double currentPointYPos = previewControlPoints[currentRow][currentCol].getTrueYPos();
                            double beginPointXPos = beginControlPoints[currentRow][currentCol].getTrueXPos();
                            double beginPointYPos = beginControlPoints[currentRow][currentCol].getTrueYPos();
                            double endPointXPos = endControlPoints[currentRow][currentCol].getTrueXPos();
                            double endPointYPos = endControlPoints[currentRow][currentCol].getTrueYPos();

                            double distanceSegmentX = (endPointXPos - beginPointXPos)/frames;
                            double distanceSegmentY = (endPointYPos - beginPointYPos)/frames;

                            previewControlPoints[currentRow][currentCol].setLocation(currentPointXPos+distanceSegmentX,currentPointYPos+distanceSegmentY);
                        }
                        previewPanel.setControlPoints(previewControlPoints);
                        previewPanel.drawStuff();
                    }
                    ticker++;
                    if (ticker == frames){
                        timer.cancel();
                    }

                }
            }, 0, (animationDuration * 1000)/frames);
        }

        //code for the MORPH
        else if(!isPreview){
            //get the buffered images
            BufferedImage[] imageFrames = generateTweenPics();

            previewPanel.setPic(imageFrames[0]);

            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    previewPanel.setPic(imageFrames[ticker]);

                    ticker++;
                    if (ticker == frames){
                        timer.cancel();
                    }
                    previewPanel.drawStuff();

                }
            }, 0, (animationDuration * 1000)/frames);
        }
    }

    public void setDuration(int seconds){
        animationDuration = seconds;
    }

    public BufferedImage[] generateTweenPics(){
        tweenPics = new BufferedImage[frames];
        for (int i = 0; i < frames; i++){
            tweenPics[i] = new BufferedImage(prePic.getWidth(), prePic.getHeight(), BufferedImage.TYPE_INT_ARGB);
        }

        int[][][] diffs = new int[prePic.getWidth()][prePic.getHeight()][4];

        //Subtract the first image from the second
        int argbA;
        int argbB;
        for (int i = 0; i < prePic.getWidth(); i++) {
            for (int j = 0; j < prePic.getHeight(); j++) {
                argbA = prePic.getRGB(i, j);
                argbB = postPic.getRGB(i, j);

                int aPre = (argbA >> 24) & 0xFF;
                int rPre = (argbA >> 16) & 0xFF;
                int gPre = (argbA >> 8) & 0xFF;
                int bPre = (argbA) & 0xFF;

                int aPost = (argbB >> 24) & 0xFF;
                int rPost = (argbB >> 16) & 0xFF;
                int gPost = (argbB >> 8) & 0xFF;
                int bPost = (argbB) & 0xFF;

                int aDiff = aPost - aPre;
                int rDiff = rPost - rPre;
                int gDiff = gPost - gPre;
                int bDiff = bPost - bPre;

                int diff = (aDiff << 24) | (rDiff << 16) | (gDiff << 8) | bDiff;

                diffs[i][j][0] = aDiff/20;
                diffs[i][j][1] = rDiff/20;
                diffs[i][j][2] = gDiff/20;
                diffs[i][j][3] = bDiff/20;
            }
        }
        //add add those to a copy of the original image
        for(int k = 0; k < frames; k++){
            for (int i = 0; i < prePic.getWidth(); i++){
                for (int j = 0; j < prePic.getHeight(); j++){
                    if (k == 0){
                        argbA = prePic.getRGB(i, j);

                        int aPre = (argbA >> 24) & 0xFF;
                        int rPre = (argbA >> 16) & 0xFF;
                        int gPre = (argbA >> 8) & 0xFF;
                        int bPre = (argbA) & 0xFF;

                        int aNext = aPre + diffs[i][j][0];
                        int rNext = rPre + diffs[i][j][1];
                        int gNext = gPre + diffs[i][j][2];
                        int bNext = bPre + diffs[i][j][3];

                        int next = (aNext << 24) | (rNext << 16) | (gNext << 8) | bNext;
                        tweenPics[k].setRGB(i, j, next);
                    }
                    else{
                        argbA = tweenPics[k-1].getRGB(i, j);

                        int aPre = (argbA >> 24) & 0xFF;
                        int rPre = (argbA >> 16) & 0xFF;
                        int gPre = (argbA >> 8) & 0xFF;
                        int bPre = (argbA) & 0xFF;

                        int aNext = aPre + diffs[i][j][0];
                        int rNext = rPre + diffs[i][j][1];
                        int gNext = gPre + diffs[i][j][2];
                        int bNext = bPre + diffs[i][j][3];

                        int next = (aNext << 24) | (rNext << 16) | (gNext << 8) | bNext;
                        tweenPics[k].setRGB(i, j, next);
                    }
                }
            }
        }
        return tweenPics;
    }

    private BufferedImage subtract(BufferedImage a, BufferedImage b){
        BufferedImage output = new BufferedImage(a.getWidth(), a.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int argbA;
        int argbB;
        for (int i = 0; i < a.getWidth(); i++){
            for (int j = 0; j < a.getHeight(); j++){
                argbA = a.getRGB(i, j);
                argbB = b.getRGB(i, j);

                int aA = (argbA >> 24) & 0xFF;
                int rA = (argbA >> 16) & 0xFF;
                int gA = (argbA >>  8) & 0xFF;
                int bA = (argbA      ) & 0xFF;

                int aB = (argbB >> 24) & 0xFF;
                int rB = (argbB >> 16) & 0xFF;
                int gB = (argbB >>  8) & 0xFF;
                int bB = (argbB      ) & 0xFF;

                int aDiff = aB - aA;
                int rDiff = rB - rA;
                int gDiff = gB - gA;
                int bDiff = bB - bA;

                int diff =
                        (aDiff << 24) | (rDiff << 16) | (gDiff << 8) | bDiff;
                output.setRGB(i, j, diff);
            }
        }
        return output;
    }

    public void boostPreImage(){
        RescaleOp op = new RescaleOp(1.2f, 20, null);
        op.filter(prePic, prePic);
    }

    public void boostPostImage(){
        RescaleOp op = new RescaleOp(1.2f, 20, null);
        op.filter(postPic, postPic);
    }
}