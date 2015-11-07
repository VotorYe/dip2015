import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Image;
import java.util.Scanner;

public  class HistogramRunner {
    public  static void compute_and_dis() {
        try {
            Scanner input = new Scanner(System.in);
            
            System.out.println("input filename: ");
            String filename = input.next();     

            BufferedImage srcImage = ImageIO.read(new File(filename));
            Histogram target = new Histogram(srcImage);
            
            BufferedImage hisImage = target.draw_his_of_origin();
            BufferedImage targetImage = target.equalize_his();
            BufferedImage tarHisImage= target.draw_his_after_equ();

            ImageIO.write(hisImage, "png", new File("his-"+filename));
            ImageIO.write(targetImage, "png", new File("target-"+filename));
            ImageIO.write(tarHisImage, "png", new File("targetHis-"+filename));
            srcImage = ImageIO.read(new File("target-"+filename));
            Histogram tar = new Histogram(srcImage);
            tar.draw_his_of_origin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {  
            compute_and_dis();
    }
}