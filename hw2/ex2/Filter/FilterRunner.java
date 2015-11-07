import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.Image;
import java.util.Scanner;

public  class FilterRunner {
    public static double[][] get_ave_filter(int level) {

        double[][] filter_1 = new double [level][level];

        double x = (double)level*(double)level;
        double tem = 1.0/x;
        for (int k = 0; k < level; k++) {
            for (int p = 0; p < level; p++) {
                filter_1[k][p] = tem;
            }
        }
        return filter_1;
    }

    public  static void compute_and_dis() {
        try {
            Scanner input = new Scanner(System.in);
            
            System.out.println("input filename: ");
            String filename = input.next();     

            BufferedImage srcImage = ImageIO.read(new File(filename));
            MyFilter operator = new MyFilter(srcImage);
            
            double [][] filter_3 = get_ave_filter(3);
            double [][] filter_7 = get_ave_filter(7);
            double [][] filter_11 = get_ave_filter(11);
            double [][] lap_filter = {
                {-1, -1, -1},
                {-1, 9, -1},
                {-1, -1, -1}
            };

            double [][] unit_filter = {
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
            };
            double [][] ave_filter = get_ave_filter(3);
            double [][] hb_filter = /*{
                {-1/9, -1/9, -1/9},
                {-1/9, 17/9, -1/9},
                {-1/9, -1/9, -1/9},
            };*/ new double[3][3];
            double rate = 2;
            for (int k = 0; k < 3 ; k++) {
                for (int p = 0; p < 3; p++) {
                    hb_filter[k][p] = rate*unit_filter[k][p] - ave_filter[k][p];
                }
            }

            double [][] homework_filter = {
                {1, 1, 1},
                {0, 0, 0},
                {-1, -1, -1},
            };

            BufferedImage tarImage_3 = operator.filter2d(filter_3);
            BufferedImage tarImage_7 = operator.filter2d(filter_7);
            BufferedImage tarImage_11 = operator.filter2d(filter_11);
            BufferedImage tarImage_lap = operator.filter2d(lap_filter);
            BufferedImage tarImage_hb = operator.filter2d(hb_filter);
            BufferedImage tarImage_hw = operator.filter2d(homework_filter);
            //BufferedImage targetImage = target.equalize_his();
            //BufferedImage tarHisImage= target.draw_his_after_equ();

            ImageIO.write(tarImage_3, "png", new File("filter_3-"+filename+"-"));
            ImageIO.write(tarImage_7, "png", new File("filter_7-"+filename+"-"));
            ImageIO.write(tarImage_11, "png", new File("filter_11-"+filename+"-"));
            ImageIO.write(tarImage_lap, "png", new File("lap_filter-"+filename+"-"));
            ImageIO.write(tarImage_hb, "png", new File("hb_filter-"+filename+"-"));
            ImageIO.write(tarImage_hw, "png", new File("hw_filter-"+filename+"-"));
            //ImageIO.write(targetImage, "png", new File("target-"+filename));
            //ImageIO.write(tarHisImage, "png", new File("targetHis-"+filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {  
            compute_and_dis();
    }
}