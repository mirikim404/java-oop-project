package view;

import entity.Steve;
import manager.WaveManager;

import javax.swing.*;
import java.awt.*;

public class DeadView extends JPanel {

    private GameFrame gameFrame;
    private Steve steve;
    private WaveManager waveManager;

    public DeadView(GameFrame gameFrame, Steve steve, WaveManager waveManager) {
        this.gameFrame = gameFrame;
        this.steve = steve;
        this.waveManager = waveManager;

        setLayout(null);
        setBackground(new Color(10, 10, 10));

        buildUI();
    }

    private void buildUI() {
        int W = 854;
        int H = 560;

        int imgW = 480;
        int imgH = (int) (imgW * (1124.0 / 1399.0));
        int imgX = (W - imgW) / 2;
        int imgY = (H - imgH) / 2;

        JLabel bg = new JLabel() {
        	private final Image bgImg = new ImageIcon(DeadView.class.getResource("/resources/ui/died_view.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2.drawImage(bgImg, 0, 0, getWidth(), getHeight(), this);
                g2.dispose();
            }
        };
        bg.setLayout(null);
        bg.setBounds(imgX, imgY, imgW, imgH);
        add(bg);

        JLabel coinLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(FontManager.getNeoDgm(13));
                FontMetrics fm = g2.getFontMetrics();
                String text = "코인 " + steve.getCoin() + "개를 가지고 1웨이브부터 재시작합니다.";
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2;
                g2.setColor(new Color(0, 0, 0, 180));
                g2.drawString(text, tx + 1, ty + 1);
                g2.setColor(new Color(255, 215, 0));
                g2.drawString(text, tx, ty);
                g2.dispose();
            }
        };
        coinLabel.setBounds(0, (int)(imgH * 0.44), imgW, 30);
        bg.add(coinLabel);

        JButton btnShop = new JButton("상점으로");
        btnShop.setBounds(40, 212, 188, 62);
        btnShop.addActionListener(e -> gameFrame.showShop(steve, waveManager, 0));
        bg.add(btnShop);

        JButton btnRestart = new JButton("처음으로");
        btnRestart.setBounds(256, 212, 184, 62);
        btnRestart.addActionListener(e -> gameFrame.restartAfterDeath(steve));
        bg.add(btnRestart);

        JButton btnTitle = new JButton("타이틀로 돌아가기");
        btnTitle.setBounds(108, 294, 262, 62);
        btnTitle.addActionListener(e -> gameFrame.showStart());
        bg.add(btnTitle);

        for (JButton btn : new JButton[]{btnShop, btnRestart, btnTitle}) {
            btn.setOpaque(false);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setFocusable(false);
            btn.setForeground(Color.WHITE);
            btn.setFont(FontManager.getNeoDgm(17));
        }

        btnTitle.setHorizontalAlignment(SwingConstants.LEFT);
        btnTitle.setBorder(BorderFactory.createEmptyBorder(0, 75, 0, 0));
        btnShop.setBorder(BorderFactory.createEmptyBorder());
        btnRestart.setBorder(BorderFactory.createEmptyBorder());
    }
}