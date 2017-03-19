import com.sun.org.apache.xpath.internal.SourceTree;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.*;

public class MotionTracker {


    public static String getTimeNow(){
        String timeNow;

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date dateObj = new Date();
        System.out.println(df.format(dateObj));
        timeNow = df.format(dateObj);

        return timeNow;
    }


    public static void main(String[] args) {
        //Load opencv native library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        JFrame frame1 = new JFrame("Video Feed");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(640,480);
        frame1.setBounds(0,0,frame1.getWidth(),frame1.getHeight());
        Panel panel1 = new Panel();
        frame1.setContentPane(panel1);
        frame1.setVisible(true);

        //Read the Video Stream
        String srcPath = "C:/Users/Michael/Videos/serve.mp4";
        VideoCapture capture = new VideoCapture(srcPath);
        //print to console to see if the video file is available.
        System.out.println(capture.isOpened());

        Mat videoImage = new Mat();
        Mat thresholded = new Mat();
        Mat thresholded2 = new Mat();

        capture.read(videoImage);
        frame1.setSize(videoImage.width()+40, videoImage.height()+60);
        //define the colour depth, number of channels and channel layout in the image.
        //CV_8UC1 is a gray scale image.
        Mat array255 = new Mat(videoImage.height(),videoImage.width(), CvType.CV_8UC1);
        array255.setTo(new Scalar(255));
        Mat distance = new Mat(videoImage.height(), videoImage.width(), CvType.CV_8UC1);
        List<Mat> lhsv = new ArrayList<>(3);

        //System Variables
        double yUpper = 75;         //exclude the event if y less than this value
        double yLower = 185;        //exclude event if y is greater than this value
        double xLeft = 75;          //exclude event if x less that this px position
        double xRight = 295;        //exclude event if x greater than this px position
        //Display and log settings
        Boolean displayFPS = false;

        //Motion event settings
        Boolean SPEED_MPH = true;       //set the speed conversion kph = false mph = true
        double trackLenTrig = 75;       //Length of track to trigger speed photo
        double trackTimeout = 1;        //Number of seconds to wait after track end(prevents dual tracking)
        double eventTimeout = 2;        //Number of seconds to wait for next motion event before starting new track

        //Camera settings
        double CameraWidth = 320;       //Image stream width for opencv motion scanning
        double CameraHeight = 240;      //Image stream height for opencv motion scanning
        double CameraFrameRate = 240;   //framerate for video stream

        //OpenCV motion settings
        boolean showCircle = true;      //true = circle in center of motion, false = rectangle
        int circleSize = 2;             //diameter circle in px if showCircle is true
        int lineThickness = 1;          //size of lines for circle or rectangle
        double fontScale = 0.5;         //opencv window text font size scalling factor (lower is smaller)
        int windowBigger = 1;           //resize multiplier for opencv window
        int blurSize = 10;              //OpenCV setting for Gaussian difference image blur
        int ThresholdSensitivity = 20;  //OpenCV setting for difference image threshold

        //set variables
        double frameCount;
        String fpsTime;
        Boolean firstEvent;
        String eventTimer = getTimeNow();
        double startPosX;
        double endPosX = 0;
        double averageSpeed = 0.0;
        Mat prevImage = new Mat();

        System.out.println("Starting Camera feed...");

        System.out.println(capture.isOpened());
        if(capture.isOpened())
        {
            while(true)
            {
                capture.read(videoImage); //grabs the next frame from the video file.
                if (!videoImage.empty())
                {
                    double fps = capture.get(Videoio.CAP_PROP_FPS);
                    System.out.println("FPS: " + fps);

                    frameCount = 0;
                    fpsTime = getTimeNow();
                    firstEvent = true;
                    eventTimer = getTimeNow();
                    startPosX = 0;
                    endPosX = 0;

                    double xBuf = (int)((xRight-xLeft) / 10);
                    double yBuf = (int)((yLower-yUpper) / 8);

                    System.out.println("Start Speed Motion Tracking!");
                    String travelDirection = "";
                    //initialise a cropped grayscale image

                    Mat Image2 = new Mat();
                    capture.read(videoImage);

                    //param1:the image you are taking, param2: the image to be param3: what you are doing
                    Imgproc.cvtColor(videoImage, Image2 ,Imgproc.COLOR_BGR2GRAY);

                    eventTimer = getTimeNow();
                    //initialize prevImage used for taking speed image photo.
                    prevImage = Image2;

                    boolean stillScanning = true;

                    while(stillScanning){







                    }

                    Core.split(videoImage, lhsv); // We get 3 2D one channel Mats
                    Mat S = lhsv.get(1);
                    Mat V = lhsv.get(2);
                    Core.subtract(array255, S, S);
                    Core.subtract(array255, V, V);
                    S.convertTo(S, CvType.CV_32F);
                    V.convertTo(V, CvType.CV_32F);

                    panel1.setImageWithMat(Image2);
                    frame1.repaint();

                }

            }
        }
        return;
    }
}