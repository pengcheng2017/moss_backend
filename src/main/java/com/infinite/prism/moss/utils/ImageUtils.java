package com.infinite.prism.moss.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/12 14:39
 */

public class ImageUtils {

    public static void mergeWithFixedArea(String framePath, String photoPath, String outputPath,
                                          int x, int y, int targetWidth, int targetHeight) throws IOException {
        BufferedImage frame = ImageIO.read(new File(framePath));
        BufferedImage photo = ImageIO.read(new File(photoPath));
        // 2. 去除白色背景（容差设为 15）
        photo = makeWhiteTransparent(photo, 30);

        // 创建与背景图大小一致的画布（类型保持与背景图一致，通常为 TYPE_INT_RGB 或 TYPE_INT_ARGB）
        BufferedImage result = new BufferedImage(frame.getWidth(), frame.getHeight(), frame.getType());
        Graphics2D g = result.createGraphics();

        // 先绘制背景图
        g.drawImage(frame, 0, 0, null);

        // 绘制画像图到指定区域（自动缩放）
        g.drawImage(photo, x, y, targetWidth, targetHeight, null);

        g.dispose();

        // 输出
        ImageIO.write(result, "PNG", new File(outputPath));
    }

    public static void main(String[] args) throws IOException {
        mergeWithFixedArea("C:\\Users\\10316\\Pictures\\background.png", "C:\\Users\\10316\\Pictures\\画像.png", "C:\\Users\\10316\\Pictures\\result.png", 235, 393, 544, 776);
    }

    /**
     * 将图像中的白色背景变为透明（默认容差 10）
     *
     * @param image 原始画像图（可为 RGB 或 ARGB）
     * @return 处理后的 ARGB 图像，白色区域透明
     */
    public static BufferedImage makeWhiteTransparent(BufferedImage image) {
        return makeWhiteTransparent(image, 20);
    }

    /**
     * 将图像中的白色背景变为透明（可指定容差）
     *
     * @param image     原始画像图
     * @param tolerance 容差值 (0-255)，数值越大去除的颜色范围越广
     *                  （例如 10 表示 R/G/B 各分量 >=245 时视为白色）
     * @return 处理后的 ARGB 图像
     */
    public static BufferedImage makeWhiteTransparent(BufferedImage image, int tolerance) {
        int width = image.getWidth();
        int height = image.getHeight();

        // 创建支持透明的 ARGB 图像
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // 遍历每个像素
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);

                // 提取各颜色分量
                int alpha = (rgb >> 24) & 0xff;
                int red   = (rgb >> 16) & 0xff;
                int green = (rgb >> 8)  & 0xff;
                int blue  = rgb & 0xff;

                // 判断是否为白色（接近白色的像素）
                if (red   >= 255 - tolerance &&
                        green >= 255 - tolerance &&
                        blue  >= 255 - tolerance) {
                    alpha = 0; // 变为完全透明
                }

                // 重新组合像素（保留原颜色值，仅修改 alpha）
                int newRgb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                result.setRGB(x, y, newRgb);
            }
        }
        return result;
    }

}
