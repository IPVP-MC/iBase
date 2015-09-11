package com.doctordark.util.imagemessage;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;
import org.bukkit.util.ChatPaginator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * Represents a image that uses ASCII characters to send a message for a player.
 *
 * @author bobacadodl
 */
public class ImageMessage {

    private final static char TRANSPARENT_CHAR = ' ';

    private final String[] lines;
    private final Color[] colors = {
            new Color(0, 0, 0),
            new Color(0, 0, 170),
            new Color(0, 170, 0),
            new Color(0, 170, 170),
            new Color(170, 0, 0),
            new Color(170, 0, 170),
            new Color(255, 170, 0),
            new Color(170, 170, 170),
            new Color(85, 85, 85),
            new Color(85, 85, 255),
            new Color(85, 255, 85),
            new Color(85, 255, 255),
            new Color(255, 85, 85),
            new Color(255, 85, 255),
            new Color(255, 255, 85),
            new Color(255, 255, 255),
    };

    public ImageMessage(String... imgLines) {
        this.lines = imgLines;
    }

    public ImageMessage(ChatColor[][] chatColors, char imgChar) {
        this.lines = toImgMessage(chatColors, imgChar);
    }

    public ImageMessage(BufferedImage image, int height, char imgChar) {
        this.lines = toImgMessage(toColourArray(image, height), imgChar);
    }

    public ImageMessage(String url, int height, char imgChar) {
        String[] result;
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            ChatColor[][] colours = toColourArray(image, height);
            result = toImgMessage(colours, imgChar);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }

        this.lines = result;
    }

    public ImageMessage(String fileName, File folder, int height, char imgChar) {
        String[] result;
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(folder, fileName));
            ChatColor[][] colours = toColourArray(bufferedImage, height);
            result = toImgMessage(colours, imgChar);
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }

        this.lines = result;
    }

    public ImageMessage appendText(String... text) {
        for (int y = 0; y < lines.length; y++) {
            if (text.length > y) {
                lines[y] += ' ' + text[y];
            }
        }
        return this;
    }

    public ImageMessage appendCenteredText(String... text) {
        for (int y = 0; y < lines.length; y++) {
            if (text.length <= y) {
                return this;
            }

            int len = ChatPaginator.AVERAGE_CHAT_PAGE_WIDTH - lines[y].length();
            lines[y] = lines[y] + center(text[y], len);
        }

        return this;
    }

    private ChatColor[][] toColourArray(BufferedImage image, int height) {
        double ratio = (double) image.getHeight() / image.getWidth();

        //int width = (int) (height / ratio);
        //if (width > 10) width = 10;

        BufferedImage reSized = resizeImage(image, (int) (height / ratio), height);
        ChatColor[][] chatImg = new ChatColor[reSized.getWidth()][reSized.getHeight()];
        for (int x = 0; x < reSized.getWidth(); x++) {
            for (int y = 0; y < reSized.getHeight(); y++) {
                int rgb = reSized.getRGB(x, y);
                ChatColor closest = getClosestChatColor(new Color(rgb, true));
                chatImg[x][y] = closest;
            }
        }

        return chatImg;
    }

    private String[] toImgMessage(ChatColor[][] colors, char imgChar) {
        String[] lines = new String[colors[0].length];
        for (int y = 0; y < colors[0].length; y++) {
            StringBuilder line = new StringBuilder();
            for (ChatColor[] color1 : colors) {
                ChatColor color = color1[y];
                line.append((color != null) ? color1[y].toString() + imgChar : TRANSPARENT_CHAR);
            }

            lines[y] = line.toString() + ChatColor.RESET;
        }

        return lines;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        AffineTransform af = new AffineTransform();
        af.scale(width / (double) originalImage.getWidth(), height / (double) originalImage.getHeight());
        AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(originalImage, null);
    }

    private double getDistance(Color c1, Color c2) {
        double redMean = (c1.getRed() + c2.getRed()) / 2.0;
        double r = c1.getRed() - c2.getRed();
        double g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        double weightR = 2 + redMean / 256.0;
        double weightG = 4.0;
        double weightB = 2 + (255 - redMean) / 256.0;
        return weightR * r * r + weightG * g * g + weightB * b * b;
    }

    private boolean areIdentical(Color c1, Color c2) {
        return Math.abs(c1.getRed() - c2.getRed()) <= 5 &&
                Math.abs(c1.getGreen() - c2.getGreen()) <= 5 &&
                Math.abs(c1.getBlue() - c2.getBlue()) <= 5;
    }

    private ChatColor getClosestChatColor(Color color) {
        if (color.getAlpha() < 128) return null;

        int index = 0;
        double best = -1;

        for (int i = 0; i < colors.length; i++) {
            if (areIdentical(colors[i], color)) {
                return ChatColor.values()[i];
            }
        }

        for (int i = 0; i < colors.length; i++) {
            double distance = getDistance(color, colors[i]);
            if (distance < best || best == -1) {
                best = distance;
                index = i;
            }
        }

        // Minecraft has 15 colors
        return ChatColor.values()[index];
    }

    private String center(String string, int length) {
        if (string.length() > length) {
            return string.substring(0, length);
        } else if (string.length() == length) {
            return string;
        } else {
            return Strings.repeat(' ', (length - string.length()) / 2) + string;
        }
    }

    public String[] getLines() {
        return Arrays.copyOf(lines, lines.length);
    }

    public void sendToPlayer(Player player) {
        for (String line : lines) {
            player.sendMessage(line);
        }
    }
}