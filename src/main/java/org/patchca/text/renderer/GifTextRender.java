package org.patchca.text.renderer;

import org.patchca.color.ColorFactory;
import org.patchca.font.FontFactory;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Gif文字渲染器
 *
 * @author veione
 */
public class GifTextRender implements TextRenderer {
    protected int leftMargin;
    protected int rightMargin;
    protected int topMargin;
    protected int bottomMargin;
    protected int length = 6;
    // 常用颜色
    public static final int[][] COLOR = {{0, 135, 255}, {51, 153, 51}, {255, 102, 102}, {255, 153, 0}, {153, 102, 0}, {153, 102, 153}, {51, 153, 153}, {102, 102, 255}, {0, 102, 204}, {204, 51, 51}, {0, 153, 204}, {0, 51, 102}};
    protected static final SecureRandom RANDOM = new SecureRandom();

    protected void arrangeCharacters(int width, int height, TextString ts) {
        double widthRemaining = (width - ts.getWidth() - leftMargin - rightMargin) / ts.getCharacters().size();
        double x = leftMargin + widthRemaining / 2;
        height -= topMargin + bottomMargin;
        for (TextCharacter tc : ts.getCharacters()) {
            double y = topMargin + (height + tc.getAscent() * 0.7) / 2;
            tc.setX(x);
            tc.setY(y);
            x += tc.getWidth() + widthRemaining;
        }
    }

    public GifTextRender() {
        leftMargin = rightMargin = 5;
        topMargin = bottomMargin = 5;
    }

    @Override
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    @Override
    public void setRightMargin(int rightMargin) {
        this.rightMargin = rightMargin;
    }

    @Override
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    @Override
    public void setBottomMargin(int bottomMargin) {
        this.bottomMargin = bottomMargin;
    }

    /**
     * 随机画干扰圆
     *
     * @param num    数量
     * @param color  颜色
     * @param g      Graphics2D
     * @param width
     * @param height
     */
    public void drawOval(int num, Color color, Graphics2D g, int width, int height) {
        for (int i = 0; i < num; i++) {
            g.setColor(color == null ? color() : color);
            int w = 5 + num(10);
            g.drawOval(num(width - 25), num(height - 15), w, w);
        }
    }

    /**
     * 随机画干扰圆
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    public void drawOval(int num, Graphics2D g, int width, int height) {
        drawOval(num, null, g, width, height);
    }

    /**
     * 给定范围获得随机颜色
     *
     * @param fc 0-255
     * @param bc 0-255
     * @return 随机颜色
     */
    protected Color color(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + num(bc - fc);
        int g = fc + num(bc - fc);
        int b = fc + num(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 获取随机常用颜色
     *
     * @return 随机颜色
     */
    protected Color color() {
        int[] color = COLOR[num(COLOR.length)];
        return new Color(color[0], color[1], color[2]);
    }

    /**
     * 产生两个数之间的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int num(int min, int max) {
        return min + RANDOM.nextInt(max - min);
    }

    /**
     * 产生0-num的随机数,不包括num
     *
     * @param num 最大值
     * @return 随机数
     */
    public static int num(int num) {
        return RANDOM.nextInt(num);
    }

    @Override
    public void draw(String text, BufferedImage canvas, FontFactory fontFactory, ColorFactory colorFactory) {
        Graphics2D g = (Graphics2D) canvas.getGraphics();
        TextString ts = convertToCharacters(text, g, fontFactory, colorFactory);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        arrangeCharacters(width, height, ts);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 获取验证码数组
        final ArrayList<TextCharacter> characters = ts.getCharacters();
        int wordSize = characters.size();
        // 随机生成每个文字的颜色
        Color[] fontColor = new Color[wordSize];
        for (int i = 0; i < wordSize; i++) {
            fontColor[i] = color();
        }
        // 绘制噪点
        this.drawNoise(fontColor, text, g, width, height);

        for (TextCharacter tc : ts.getCharacters()) {
            g.setColor(tc.getColor());
            g.drawString(tc.iterator(), (float) tc.getX(), (float) tc.getY());
        }
    }

    /**
     * 绘制噪点
     *
     * @param fontColor
     * @param text
     * @param g
     * @param width
     * @param height
     */
    private void drawNoise(Color[] fontColor, String text, Graphics2D g, int width, int height) {
        // 随机生成贝塞尔曲线参数
        int x1 = 5, y1 = num(5, height / 2);
        int x2 = width - 5, y2 = num(height / 2, height - 5);
        int ctrlx = num(width / 4, width / 4 * 3), ctrly = num(5, height - 5);
        if (num(2) == 0) {
            int ty = y1;
            y1 = y2;
            y2 = ty;
        }
        int ctrlx1 = num(width / 4, width / 4 * 3), ctrly1 = num(5, height - 5);
        int[][] besselXY = new int[][]{{x1, y1}, {ctrlx, ctrly}, {ctrlx1, ctrly1}, {x2, y2}};
        // 画干扰圆圈
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f * num(10)));  // 设置透明度
        drawOval(2, g, width, height);
        // 画干扰线
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));  // 设置透明度
        g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g.setColor(fontColor[0]);
        CubicCurve2D shape = new CubicCurve2D.Double(besselXY[0][0], besselXY[0][1], besselXY[1][0], besselXY[1][1], besselXY[2][0], besselXY[2][1], besselXY[3][0], besselXY[3][1]);
        g.draw(shape);
    }

    protected TextString convertToCharacters(String text, Graphics2D g, FontFactory fontFactory, ColorFactory colorFactory) {
        TextString characters = new TextString();
        FontRenderContext frc = g.getFontRenderContext();
        double lastx = 0;
        for (int i = 0; i < text.length(); i++) {
            Font font = fontFactory.getFont(i);
            char c = text.charAt(i);
            FontMetrics fm = g.getFontMetrics(font);
            Rectangle2D bounds = font.getStringBounds(String.valueOf(c), frc);
            TextCharacter tc = new TextCharacter();
            tc.setCharacter(c);
            tc.setFont(font);
            tc.setWidth(fm.charWidth(c));
            tc.setHeight(fm.getAscent() + fm.getDescent());
            tc.setAscent(fm.getAscent());
            tc.setDescent(fm.getDescent());
            tc.setX(lastx);
            tc.setY(0);
            tc.setFont(font);
            tc.setColor(colorFactory.getColor(i));
            lastx += bounds.getWidth();
            characters.addCharacter(tc);
        }
        return characters;
    }
}
