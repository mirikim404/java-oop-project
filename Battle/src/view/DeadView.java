package view;

import entity.Steve;
import entity.mob.Mob;
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

        // YOU DIED 텍스트 (레이블로)
        JLabel diedLabel = new JLabel("YOU DIED", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(FontManager.getNeoDgm(72));
                FontMetrics fm = g2.getFontMetrics();
                String text = "YOU DIED";
                int tx = (getWidth() - fm.stringWidth(text)) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                // 그림자
                g2.setColor(new Color(80, 0, 0));
                g2.drawString(text, tx + 3, ty + 3);
                // 본문
                g2.setColor(new Color(200, 30, 30));
                g2.drawString(text, tx, ty);
                g2.dispose();
            }
        };
        diedLabel.setBounds(0, 140, W, 100);

        // 코인 유지 안내 텍스트
        JLabel coinLabel = new JLabel("코인 " + steve.getCoin() + "개를 가지고 1웨이브부터 재시작합니다.", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(FontManager.getNeoDgm(14));
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
        coinLabel.setBounds(0, 250, W, 30);

        // 상점으로 버튼
        JButton btnShop = makeButton("상점으로");
        btnShop.setBounds(240, 320, 140, 44);
        btnShop.addActionListener(e -> {
            // 상점에서 "다음 웨이브" 대신 "처음으로" 동작을 해야 하므로
            // waveManager는 현재 상태 그대로 넘김 (상점 내부에서 restartAfterDeath 호출)
            gameFrame.showShop(steve, waveManager, 0); // wave=0 → 상점에서 처음으로 버튼 표시 신호
        });

        // 처음으로 버튼
        JButton btnRestart = makeButton("처음으로");
        btnRestart.setBounds(474, 320, 140, 44);
        btnRestart.addActionListener(e -> {
            gameFrame.restartAfterDeath(steve);
        });

        // 타이틀로 버튼 (작게, 맨 밑)
        JButton btnTitle = makeSmallButton("타이틀로 돌아가기");
        btnTitle.setBounds(327, 420, 200, 28);
        btnTitle.addActionListener(e -> {
            gameFrame.showStart();
        });

        add(diedLabel);
        add(coinLabel);
        add(btnShop);
        add(btnRestart);
        add(btnTitle);
    }

    private JButton makeButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Color base = getModel().isPressed() ? new Color(50, 45, 65)
                        : getModel().isRollover() ? new Color(118, 108, 132)
                        : new Color(78, 72, 94);
                g2.setColor(base);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(base.brighter().brighter());
                g2.drawLine(0, 0, getWidth() - 2, 0);
                g2.drawLine(0, 0, 0, getHeight() - 2);
                g2.setColor(base.darker().darker());
                g2.drawLine(1, getHeight() - 1, getWidth() - 1, getHeight() - 1);
                g2.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 1);
                g2.setFont(FontManager.getNeoDgm(15));
                FontMetrics fm = g2.getFontMetrics();
                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(new Color(0, 0, 0, 180));
                g2.drawString(getText(), tx + 1, ty + 1);
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), tx, ty);
                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton makeSmallButton(String text) {
        JButton btn = makeButton(text);
        btn.setFont(FontManager.getNeoDgm(11));
        return btn;
    }
}