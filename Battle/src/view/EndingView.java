package view;

import entity.Steve;
import javax.swing.*;
import java.awt.*;

public class EndingView extends JPanel {

    private GameFrame gameFrame;
    private Steve steve;

    public EndingView(GameFrame gameFrame, Steve steve) {
        this.gameFrame = gameFrame;
        this.steve = steve;

        setLayout(null);
        setBackground(new Color(10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        int W = 854;
        int H = 560;

        // 축하 텍스트
        JLabel titleLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                String line1 = "축하합니다!";
                String line2 = "엔더드래곤을 쓰러뜨렸습니다!";

                g2.setFont(FontManager.getNeoDgm(36));
                FontMetrics fm = g2.getFontMetrics();

                // line1
                int tx1 = (getWidth() - fm.stringWidth(line1)) / 2;
                g2.setColor(new Color(80, 60, 0));
                g2.drawString(line1, tx1 + 3, 60 + 3);
                g2.setColor(new Color(255, 215, 0));
                g2.drawString(line1, tx1, 60);

                // line2
                g2.setFont(FontManager.getNeoDgm(24));
                fm = g2.getFontMetrics();
                int tx2 = (getWidth() - fm.stringWidth(line2)) / 2;
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(line2, tx2 + 2, 110 + 2);
                g2.setColor(new Color(200, 200, 200));
                g2.drawString(line2, tx2, 110);

                g2.dispose();
            }
        };
        titleLabel.setBounds(0, 180, W, 160);

        // 타이틀로 버튼
        JButton btnTitle = makeButton("타이틀로 돌아가기");
        btnTitle.setBounds((W - 200) / 2, 370, 200, 44);
        btnTitle.addActionListener(e -> gameFrame.showStart());

        add(titleLabel);
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
}