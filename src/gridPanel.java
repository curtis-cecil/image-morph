import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class gridPanel extends JPanel {

    private BufferedImage pic;
    public boolean drawimage = false;

    private static int defaultSize = 500;
    public int sizeX = defaultSize;
    public int sizeY = defaultSize;
    private int rows;
    private int cols;

    private int xSpacing;
    private int ySpacing;

    private controlPoint[][] controlPoints;

    //Constructor
    public gridPanel(int x, int y){
        rows = x;
        cols = y;

        //Add 2 to draw control points on the edges of the screen
        //these control points will not have listeners, so they cannot be clicked
        controlPoints = new controlPoint[rows+2][cols+2];

        //Sets up the points at an even spacing
        //Should work arbitrarily  for any # of rows, cols and panel size
        xSpacing = sizeX/(rows + 1);
        ySpacing = sizeY/(cols + 1);

        //Create the control points
        for (int i = 0; i < rows+2; i++){
            for (int j = 0; j < cols+2; j++){
                controlPoints[i][j] = new controlPoint((i * xSpacing) - 5, (j * ySpacing) - 5);
                controlPoints[i][j].setRowID(i);
                controlPoints[i][j].setColID(j);
                add(controlPoints[i][j]);
            }
        }

        setNeighbors();

        setLayout(null);
        setPreferredSize(new Dimension(sizeX, sizeY));
        setBorder(BorderFactory.createBevelBorder(1));
    }

    //Sets the neighbors of each control point
    public void setNeighbors(){
        for(int i = 0; i < rows+1; i++){
            for (int j = 0; j < cols+1; j++){
                controlPoints[i][j].setSouthNeighbor(controlPoints[i][j+1]);
                controlPoints[i][j].setEastNeighbor(controlPoints[i+1][j]);
                controlPoints[i][j].setSouthEastNeighbor(controlPoints[i+1][j+1]);

                controlPoints[i][j].south.setNorthNeighbor(controlPoints[i][j]);
                controlPoints[i][j].east.setWestNeighbor(controlPoints[i][j]);
                controlPoints[i][j].southeast.setNorthWestNeighbor(controlPoints[i][j]);

                controlPoints[i][j].setNeighborLocations();
            }
        }
    }

    //Overloaded paint function
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        //Draw the picture
        if(drawimage) {
            g.drawImage(pic, 0, 0, this);
        }

        //Draw the lines over the picture
        double x1, x2, y1, y2;
        for (int i = 0; i < rows+1; i++){
            for (int j = 0; j < cols+1; j++){
                x1 = controlPoints[i][j].trueXPos + 5;
                y1 = controlPoints[i][j].trueYPos + 5;
                x2 = controlPoints[i][j].east.getTrueXPos() + 5;
                y2 = controlPoints[i][j].east.getTrueYPos() + 5;

                g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);

                x2 = controlPoints[i][j].south.getTrueXPos() + 5;
                y2 = controlPoints[i][j].south.getTrueYPos() + 5;
                g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);

                x2 = controlPoints[i][j].southeast.getTrueXPos() + 5;
                y2 = controlPoints[i][j].southeast.getTrueYPos() + 5;
                g.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
            }
        }
    }

    //Calls repaint, just as a wrapper, Probably not necessary.
    public void drawStuff(){
        repaint();
    }

    public void setPic(BufferedImage inputPic){
        pic = inputPic;
        drawimage = true;
        repaint();
    }

    public controlPoint[][] getPoints(){
        return controlPoints;
    }

    public void setControlPoints(controlPoint[][] targetControlPoints){
        for(int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++){
                controlPoints[i][j].setLocation(targetControlPoints[i][j].getTrueXPos(), targetControlPoints[i][j].getTrueYPos());
                controlPoints[i][j].relocate();
            }
        }
    }

    public int getRows(){
        return rows;
    }

    public int getCols(){
        return cols;
    }
}