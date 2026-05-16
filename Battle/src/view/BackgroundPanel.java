package view;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class BackgroundPanel extends JPanel {

    private static final String[] SPLASHES = {
        "Now with Java!",
        "RPG Edition!",
        "Kill the mobs!",
        "100% Handcrafted!",
        "Creeper? Aw man!",
        "Not Notch approved!",
        "Made by students!",
    };

    private final String splash;
    private Image bgImage;

    public BackgroundPanel() {
        splash = SPLASHES[new Random().nextInt(SPLASHES.length)];
        try {
        	bgImage = new ImageIcon("resources/title-background.png").getImage();
        } catch (Exception e) {
            bgImage = null; // 이미지 못 불러오면 기존 배경으로 fallback
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // ─── 배경 ───
        if (bgImage != null) {
            // 이미지 배경
            g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // 이미지 없을 때 fallback
            g2.setColor(new Color(91, 163, 224));
            g2.fillRect(0, 0, getWidth(), getHeight() / 2);
            g2.setColor(new Color(106, 170, 58));
            g2.fillRect(0, getHeight() / 2, getWidth(), 40);
            g2.setColor(new Color(123, 90, 58));
            g2.fillRect(0, getHeight() / 2 + 40, getWidth(), getHeight());
        }

        // ─── 어두운 오버레이 ───
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // ─── 버전 텍스트 (좌측 하단) ───
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setFont(new Font("Dialog", Font.PLAIN, 12));
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString("Java RPG Edition 1.0", 3, getHeight() - 6);
        g2.setColor(Color.WHITE);
        g2.drawString("Java RPG Edition 1.0", 2, getHeight() - 7);

        // ─── Splash Text ───
        Graphics2D gs = (Graphics2D) g2.create();
        gs.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        gs.setFont(new Font("Dialog", Font.BOLD, 16));

        int sx = getWidth() / 2 + 100;
        int sy = getHeight() / 2 - 90;
        gs.translate(sx, sy);
        gs.rotate(Math.toRadians(-20));
        gs.setColor(new Color(180, 130, 0));
        gs.drawString(splash, 2, 2);
        gs.setColor(Color.YELLOW);
        gs.drawString(splash, 0, 0);

        gs.dispose();
        g2.dispose();
    }
}