package com.zixi.usermanagementsystem.util;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageUtils {

    private static final int scaledWidth = 640;
    private static final int scaledHeight = 640;

    /**
     *
     * @param originalImage 原始图片
     * @param text 水印文本
     * @return 加水印之后的图片
     */
    public static BufferedImage addWatermark(BufferedImage originalImage, String text) {
        // 创建加水印的图片对象
        BufferedImage bufImg = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB); // 发生 OOM
        Graphics2D g = bufImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null); // 画图片
        g.drawString(text, 0, 0); // 加水印
        g.dispose();// 释放资源
        return bufImg;
    }
}
