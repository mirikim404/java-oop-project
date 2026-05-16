package view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;

public class MinecraftButton extends JButton {

    private boolean hovered = false;

    public MinecraftButton(String text) {
        super(text);
        setUI(new BasicButtonUI());       
        setOpaque(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        // 배경
        g2.setColor(hovered ? new Color(123, 123, 204) : new Color(107, 107, 107));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 위/왼쪽 밝은 테두리
        g2.setColor(new Color(155, 155, 155));
        g2.fillRect(0, 0, getWidth(), 3);
        g2.fillRect(0, 0, 3, getHeight());

        // 아래/오른쪽 어두운 테두리
        g2.setColor(new Color(43, 43, 43));
        g2.fillRect(0, getHeight() - 3, getWidth(), 3);
        g2.fillRect(getWidth() - 3, 0, 3, getHeight());

        // ✅ 텍스트 (안티앨리어싱 OFF, 그림자 포함)
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
        int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        // 그림자 (마인크래프트 스타일)
        g2.setColor(new Color(63, 63, 63));
        g2.drawString(getText(), tx + 2, ty + 2);

        // 본문 텍스트
        g2.setColor(Color.WHITE);
        g2.drawString(getText(), tx, ty);

        g2.dispose();
        
        
    }

    @Override
    protected void paintBorder(Graphics g) {
        // paintComponent에서 직접 그리므로 비워둠
    }
}