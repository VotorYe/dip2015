import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.Graphics2D; 
import java.awt.Color; 

public  class MyFilter {
    private int width;
    private int height;

    // 源图rgb
    private int[] srcRGB;

    // 缓存

    private int[] rgb;
    private int[] transparent;
    private int[] greyLevel;
    private int[] r;
    private int[] g;
    private int[] b;

    private BufferedImage src;

    public MyFilter(BufferedImage src) {
        this.src = src;
        width = src.getWidth();
        height = src.getHeight();

        srcRGB = new int[width*height];
        r = new int[width*height];
        g = new int[width*height];
        b = new int[width*height];
        transparent = new int[width*height];
        rgb = new int[4];

        src.getRGB(0, 0, width, height, srcRGB, 0, width);

        get_grey_level();
    }

    // 获取srcRGB，index处像素值
    private void getRGB(int index, int[] srcRGB) {
        rgb[0] = (srcRGB[index] >> 24) & 0xff;
        rgb[1] = (srcRGB[index] >> 16) & 0xff;
        rgb[2] = (srcRGB[index] >> 8) & 0xff;
        rgb[3] = srcRGB[index] & 0xff;
    }

    // 计算并保存灰度图的各个像素点的灰度值和透明度
    // 默认图片不是彩色图
    private void get_grey_level() {
        for (int k = 0; k < width*height; k++) {
            getRGB(k, srcRGB);
            transparent[k] = rgb[0];
            r[k] = rgb[1];
            g[k] = rgb[2];
            b[k] = rgb[3];
        }
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dstCM) {
        if ( dstCM == null )
            dstCM = src.getColorModel();
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
    }

    public BufferedImage filter2d(double[][] filter) {
        BufferedImage targetImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
        // 目标图rgb
        int[] targetRgb = new int[width*height];

        // 我们默认滤镜的长宽都为偶数,且相等
        int filter_size = filter.length;

        // 进行滤镜处理
        int tem;
        // w 左数个数 col
        // h 上数个数 row
        int index;
        int temW, temH, temIndex;
        int filter_index;
        int center = filter_size/2;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                double[] result  = {0.0, 0.0, 0.0};
                // 灰度图中的坐标
                index = h*width+w;
                // 核心算法
                // 根据滤镜大小及目标像素位置计算新的像素值
                for (int k = 0; k < filter_size; k++) {     // k -- height
                    for (int p = 0; p < filter_size; p++) {     // p -- width
                        temH = h + (k - center);
                        temW = w + (p - center);
                        temIndex = temH*width+temW;     // 确定目标
                        if (temH < 0 | temH >= height |
                            temW < 0 | temW >= width) {     // 像素点超出图像范围
                            result[0] += 0;
                            result[1] += 0;
                            result[2] += 0;
                        } else {
                            result[0] += ((double)(r[temIndex])*filter[k][p]);
                            result[1] += ((double)(g[temIndex])*filter[k][p]);
                            result[2] += ((double)(b[temIndex])*filter[k][p]);
                        }
                    }
                }
                for (int k = 0; k < 3; k++) {
                    if (result[k] > 255) result[k] = 255;
                    if (result[k] < 0) result[k] = 0;
                }
                targetRgb[index] = (int) transparent[index] << 24 |
                                                (int) result[0] << 16 |
                                                (int) result[1] << 8 |
                                                (int) result[2];
                getRGB(index, targetRgb);
            }
        }
        getRGB(160, targetRgb);
        targetImage.setRGB(0, 0, width, height, targetRgb, 0, width);
        targetImage.getRGB(0, 0, width, height, targetRgb, 0, width);
        for (int k = 0; k < width*height; k++) {
            getRGB(k, targetRgb);
            if (rgb[3] > 255 | rgb[3] < 0) {
                System.out.println("160: "+rgb[3]+rgb[2]+rgb[1]+rgb[0]);
            }
        }

        return targetImage;
    }
}
