package com.zixi.usermanagementsystem.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtils {

    private static final int scaledWidth = 640;
    private static final int scaledHeight = 640;

    /**
     * 为图片添加水印
     * @param originalImage 原始图片
     * @param text 水印文本
     * @return 加水印之后的图片
     */
    public static BufferedImage addWatermark(BufferedImage originalImage, String text) {
        // 检查输入参数
        if (originalImage == null || text == null) {
            throw new IllegalArgumentException("原始图片和水印文本不能为空");
        }
        
        try {
            // 创建加水印的图片对象
            BufferedImage bufImg = new BufferedImage(
                Math.min(originalImage.getWidth(), scaledWidth), 
                Math.min(originalImage.getHeight(), scaledHeight), 
                BufferedImage.TYPE_INT_RGB
            );
            
            Graphics2D g = bufImg.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制原始图片（按比例缩放）
                g.drawImage(originalImage, 0, 0, bufImg.getWidth(), bufImg.getHeight(), null);
                
                // 添加水印文本
                g.drawString(text, 10, 20);
                
                return bufImg;
            } finally {
                // 确保资源被正确释放
                g.dispose();
            }
        } catch (OutOfMemoryError e) {
            // 处理内存不足异常
            throw new RuntimeException("处理图片时发生内存不足错误", e);
        }
    }
}