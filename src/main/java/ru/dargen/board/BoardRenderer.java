package ru.dargen.board;

import org.bukkit.ChatColor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BoardRenderer {

    private final BufferedImage bufferedImage;
    private final int width, height;
    private Graphics2D graphics;

    public BoardRenderer(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
    }

    public Graphics2D createGraphics() {
        return graphics = bufferedImage.createGraphics();
    }

    public Graphics2D getGraphics() {
        return graphics == null ? createGraphics() : graphics;
    }

    public void drawString(String text, int x, int y) {
        drawString(text, x, y, Color.WHITE);
    }

    public void drawString(String text, int x, int y, Color color) {
        if (graphics == null) createGraphics();
        Color lastColor = graphics.getColor();
        char last = 0;
        for (char c : text.toCharArray()) {
            boolean isCode = last == '§' || c == '§';
            if (last == '§' && c != 'k' && c != 'o' && c != 'l' && c != 'm' && c != 'n') {
                MinecraftColor mc = MinecraftColor.ofChar(c);
                graphics.setColor(mc == null ? color : mc.getColor());
            }
            last = c;
            if (isCode) continue;
            graphics.drawString(String.valueOf(c), x, y);
            x += graphics.getFontMetrics().charWidth(c);
        }
        graphics.setColor(lastColor);
    }

    public void drawCenteredString(String text, int y, Color color) {
        drawString(text, bufferedImage.getWidth() / 2 - graphics.getFontMetrics().stringWidth(
                ChatColor.stripColor(text)
        ) / 2, y, Color.WHITE);
    }

    public void drawCenteredString(String text, int y) {
        drawCenteredString(text, y, Color.WHITE);
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public enum MinecraftColor {

        BLACK('0', 0, 0, 0),
        DARK_BLUE('1', 0, 0, 170),
        DARK_GREEN('2', 0, 128, 0),
        DARK_AQUA('3', 0, 230, 153),
        DARK_RED('4', 170, 0, 0),
        DARK_PURPLE('5', 139, 0, 255),
        GOLD('6', 255, 165, 0),
        GRAY('7', 128, 128, 128),
        DARK_GRAY('8', 73, 66, 61),
        BLUE('9', 66, 170, 255),
        GREEN('a', 25, 255, 25),
        AQUA('b', 127, 255, 212),
        RED('c', 255, 0, 0),
        PURPLE('d', 128, 0, 128),
        YELLOW('e', 255, 255, 0),
        WHITE('f', 255, 255, 255),
        ;

        private final char code;
        private final Color color;

        MinecraftColor(char code, int r, int g, int b) {
            this.code = code;
            this.color = new Color(r, g, b);
        }

        public static MinecraftColor ofChar(char code) {
            for (MinecraftColor color : MinecraftColor.values()) {
                if (color.code == code)
                    return color;
            }
            return null;
        }

        public char getCode() {
            return code;
        }

        public Color getColor() {
            return color;
        }

    }


}
