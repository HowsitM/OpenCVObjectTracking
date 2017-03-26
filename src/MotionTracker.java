import com.sun.org.apache.xpath.internal.SourceTree;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import sun.plugin.javascript.navig.*;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.math.*;

public class MotionTracker {


    public static double getTimeNow(){

        return System.currentTimeMillis()%1000;
    }

    public static double convertSpeed(double contourWidth){
        double speed = 0;

        double objectMM = 68.58;
        double objectPx = contourWidth;

        speed = (objectMM/objectPx * 0.0036);

        return speed;
    }



    public static void main(String[] args) {
        //Load opencv native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        JFrame frame1 = new JFrame("Video Feed");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(640, 480);
        frame1.setBounds(0, 0, frame1.getWidth(), frame1.getHeight());
        Panel panel1 = new Panel();
        frame1.setContentPane(panel1);
        frame1.setVisible(true);

        //Read the Video Stream
        String srcPath = "C:/Users/Michael/Videos/serve.mp4";
        VideoCapture capture = new VideoCapture(srcPath);
        //print to console to see if the video file is available.
        System.out.println(capture.isOpened());

        Mat videoImage = new Mat();

        capture.read(videoImage);
        frame1.setSize(videoImage.width() + 40, videoImage.height() + 60);

        //Motion event settings

        //set variables
        double frameCount;
        String fpsTime;
        Boolean firstEvent = true;
        double eventTimer = getTimeNow();
        double startPosX = 0;
        double endPosX = 0;
        double averageSpeed = 0.0;

        int cx = 0;
        int cy = 0;
        int mx = 0;
        int my = 0;
        int mw = 0;
        int mh = 0;


        System.out.println("Starting Camera feed...");
        System.out.println(capture.isOpened());

        Mat thresholded = new Mat();
        int MIN_AREA = 20;              //exclude all contours less than or equal to this sq-px area
        Mat circles = new Mat();
        double biggestArea = MIN_AREA;
        double startTimer = 0;
        double totalTrackDist;
        int x, y, w, h;

        if (capture.isOpened()) {

            while (true) {
                capture.read(videoImage); //grabs the next frame from the video file.
                //get Image1
                Mat Image1 = videoImage;
                //GreyScale Image1
                Mat Image1Gray = new Mat();
                Imgproc.cvtColor(Image1, Image1Gray, Imgproc.COLOR_BGR2GRAY);
                Mat absDifference = new Mat();
                //Start Loop
                List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                double endTime;
                double totalTrackTime;
                int xDiffMin = 1;
                int xDiffMax = 50;

                while (true) {
                    //Get Image2
                    Mat Image2 = new Mat();
                    capture.read(Image2);

                    //Grayscale Image2
                    Mat Image2Gray = new Mat();
                    Imgproc.cvtColor(Image2, Image2Gray, Imgproc.COLOR_BGR2GRAY);

                    //absDiff image subtract image 1 from image2
                    Core.absdiff(Image2Gray, Image1Gray, absDifference);

                    //Get the threshold of the difference image based on the threshold sensitivity variable
                    Imgproc.threshold(absDifference, thresholded, 100, 255, Imgproc.THRESH_BINARY);

                    //Blur the thresholded Image
                    Mat blurredImage = new Mat();
                    Imgproc.GaussianBlur(thresholded, blurredImage, new Size(45, 45), 1);

                    //Prepare for reading next image 2
                    Image1Gray = Image2Gray;

                    //Get Contours in blurred Image
                    Imgproc.findContours(blurredImage, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                    //Loop contours and get the biggest one.
                    int totalContours = contours.size();
                    System.out.println("C: " + totalContours);
                    Image1Gray = Image2Gray;
                    Imgproc.HoughCircles(blurredImage, circles, Imgproc.CV_HOUGH_GRADIENT, 2, blurredImage.height(), 500, 50, 0, 0);

                    double foundArea;
                    for (MatOfPoint c : contours) {
                        foundArea = Imgproc.contourArea(c);

                        if (foundArea > biggestArea) {

                            boolean motionFound = true;
                            biggestArea = foundArea;
                            for (int i = 0; i < contours.size(); i++) {
                                Imgproc.drawContours(blurredImage, contours, i, new Scalar(255, 255, 255), 2);
                            }
                            Rect bb = Imgproc.boundingRect(c);
                            x = bb.x;
                            y = bb.y;
                            w = bb.width;
                            h = bb.height;
                            cx = (int) ((x + w) / 2);
                            cy = (int) ((y + h) / 2);
                            mx = x;
                            my = y;
                            mw = w;
                            mh = h;

                            if (motionFound) {
                                //process motion event and track data
                                //This is the first valid motion event
                                if (firstEvent) {
                                    firstEvent = false;
                                    startPosX = cx;
                                    endPosX = cx;
                                    startTimer = getTimeNow();
                                    System.out.println("New Track - Start Time: " + startTimer + " - Motion at: " + "x:" + cx + " y: " + cy);
                                }
                                else if (Math.abs(cx - endPosX) > xDiffMin && Math.abs(cx - endPosX) < xDiffMax) {
                                    //movement is within acceptable distance range of last event
                                    endPosX = cx;
                                    endTime = getTimeNow();
                                    totalTrackDist = Math.abs(endPosX - startPosX);
                                    totalTrackTime = Math.abs(endTime - startTimer);
                                    averageSpeed = (float) Math.abs((totalTrackDist / totalTrackTime)) * (0.621371 * convertSpeed(bb.width));
                                    System.out.println("Average Speed " + averageSpeed);

                                startPosX = 0;
                                endPosX = 0;
                                firstEvent = true;
                            }
                        }
                        }
                    }
                    panel1.setImageWithMat(blurredImage);
                    frame1.repaint();
                }
            }
        }
        return;
    }
}