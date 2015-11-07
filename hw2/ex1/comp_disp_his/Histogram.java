import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.Graphics2D; 
import java.awt.Color; 

public  class Histogram {
    private int width;
    private int height;

    // 源图rgb
    private int[] srcRGB;

    // 目标图rgb
    private int[] targetRgb;

    // 直方图大小
    private int hisImage_size = 300;

    private BufferedImage src;
    private BufferedImage targetImage;

    Graphics2D g2d;
    // 存储原图灰度值出现次数
    private int[] count;

    // 存储灰度值均衡化后的值，此处下标表示原图中的灰度值
    private int[] map;

    // 存储均衡化后图片灰度值出现次数
    private int[] his_count;

    // 一个用来存储某一像素点灰度值的数组
    private int[] rgb;

    public Histogram(BufferedImage src) {
        this.src = src;
        this.width = src.getWidth();
        this.height = src.getHeight();
        srcRGB = new int[width*height];
        targetRgb = new int[width*height];

        rgb = new int[4];
        count = new int[256];
        his_count = new int[256];

        src.getRGB(0, 0, width, height, srcRGB, 0, width);

        // 这里做初始化
        for (int k = 0; k < 256 ; k++) {
            count[k] = 0;
        }
    }

    // 获取数组最大值
    private int findMaxValue(int[] tar) {
        int max = -1000;
        for (int k = 0; k < 256; k++) {
            if (tar[k] > max) max = tar[k];
        }
        return max;
    }

    // 获取srcRGB，index处像素值
    private void getRGB(int index, int[] srcRGB) {
        rgb[0] = (srcRGB[index] >> 24) & 0xff;
        rgb[1] = (srcRGB[index] >> 16) & 0xff;
        rgb[2] = (srcRGB[index] >> 8) & 0xff;
        rgb[3] = srcRGB[index] & 0xff;
    }

    // 计算传入像素数组的像素值出现次数存于count数组中
    public  void compute_his(int[] count, int[] srcRGB) {
        for (int k = 0; k < 256; k++) {
            count[k] = 0;
        }
        for (int k = 0; k < width*height ; k++) {
            getRGB(k, srcRGB);
            count[rgb[3]]++;
        }
    } 

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if ( dstCM == null )
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
    }

    // 均衡化原图的直方图，返回处理后图片的BufferedImage
    public BufferedImage equalize_his() {
        compute_his(this.count, srcRGB);

        map = new int[256];
        int total = 0;

        // 均衡化核心算法
        for (int k = 0; k < 256; k++) {
            total +=count[k];
            map[k] = (int)(total*(double)255/(width*height));
        }

        for (int k = 0; k < width*height; k++) {
            getRGB(k, srcRGB);

            targetRgb[k] =  (int)rgb[0] << 24 |
                                      (int)(map[rgb[3]] << 16) |
                                      (int)(map[rgb[3]] << 8) |
                                      (int)map[rgb[3]];
        }

        targetImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

        targetImage.setRGB(0, 0, width, height, targetRgb, 0, width);
        return targetImage;
    }

    // 返回原图的直方图的BufferedImage
    public BufferedImage draw_his_of_origin() {
        compute_his(count, srcRGB);
        return draw_his(count);
    }

    // 返回均衡化后的图片的直方图的BufferedImage
    public BufferedImage draw_his_after_equ() {
        if (targetImage == null)
            equalize_his();
        compute_his(his_count, targetRgb);
        return draw_his(his_count);
    }

    // 绘制直方图，返回BufferedImage
    public BufferedImage draw_his(int[] tar) {
        BufferedImage hisImage = new BufferedImage( hisImage_size,hisImage_size, BufferedImage.TYPE_4BYTE_ABGR);;

        g2d = hisImage.createGraphics();

        int bottom = hisImage_size - 10;
        int left = 10;
        int top = 10;
        float max_height = 250.0f;  // 直方图纵坐标最大值

        g2d.setPaint(Color.GRAY);
        g2d.fillRect(0, 0, hisImage_size, hisImage_size);
        g2d.setPaint(Color.WHITE);
        g2d.drawLine(left,  bottom, 270, bottom);  // x 轴 左边距离，上面距离
        g2d.drawLine(left, bottom, left, top);  // y 轴

        g2d.setPaint(Color.BLACK);
        int max = findMaxValue(tar);
        float rate = max_height/((float)max);
        for (int k = 0; k < 256; k++) {
            int frequency = (int)(tar[k]*rate);
            int offset = 3;
            g2d.drawLine(left+k+offset, bottom, left+k+offset, bottom - frequency);
        }
        return hisImage;
    } 
}
