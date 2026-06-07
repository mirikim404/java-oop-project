package view;

import entity.Steve;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EndingView extends JPanel {

    private GameFrame gameFrame;
    private Steve steve;
    private long elapsedMs;

    private static class GoldParticle {
        double x, y;
        double vx, vy;
        double alpha;
        double size;
        double rotation;
        double rotationSpeed;
        Color color;

        public GoldParticle(double x, double y, double vx, double vy, double size, Color color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.color = color;
            this.alpha = 1.0;
            this.rotation = Math.random() * 360;
            this.rotationSpeed = Math.random() * 6 - 3;
        }
    }

    public EndingView(GameFrame gameFrame, Steve steve, long elapsedMs) {
        this.gameFrame = gameFrame;
        this.steve = steve;
        this.elapsedMs = elapsedMs;

        setLayout(null);
        setBackground(new Color(10, 10, 10));
        buildUI();
    }

    private void buildUI() {
        int W = 854;
        int H = 560;

        int imgW = 480;
        int imgH = (int) (imgW * (919.0 / 1152.0));
        int imgX = (W - imgW) / 2;
        int imgY = (H - imgH) / 2;

        JLabel bg = new JLabel() {
            private final Image bgImg = new ImageIcon("resources/ui/ending_view.png").getImage();

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

        JLabel subLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(FontManager.getNeoDgm(13));
                FontMetrics fm = g2.getFontMetrics();

                String part1 = "";
                String part2 = "엔더드래곤";
                String part3 = "을 쓰러뜨렸습니다!";
                int totalW = fm.stringWidth(part1 + part2 + part3);
                int tx = (getWidth() - totalW) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2;

                g2.setColor(new Color(0, 0, 0, 180));
                g2.drawString(part1 + part2 + part3, tx + 1, ty + 1);

                g2.setColor(Color.WHITE);
                g2.drawString(part1, tx, ty);
                tx += fm.stringWidth(part1);

                g2.setColor(new Color(180, 100, 255));
                g2.drawString(part2, tx, ty);
                tx += fm.stringWidth(part2);

                g2.setColor(Color.WHITE);
                g2.drawString(part3, tx, ty);

                g2.dispose();
            }
        };
        subLabel.setBounds(0, 185, imgW, 30);
        bg.add(subLabel);

        JLabel coinLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(FontManager.getNeoDgm(15));
                FontMetrics fm = g2.getFontMetrics();

                String part1 = "클리어 타임 ";
                String part2 = formatElapsed(elapsedMs);
                String part3 = "";
                int totalW = fm.stringWidth(part1 + part2 + part3);
                int tx = (getWidth() - totalW) / 2;
                int ty = (getHeight() + fm.getAscent()) / 2;

                g2.setColor(new Color(0, 0, 0, 180));
                g2.drawString(part1 + part2 + part3, tx + 1, ty + 1);

                g2.setColor(Color.WHITE);
                g2.drawString(part1, tx, ty);
                tx += fm.stringWidth(part1);

                g2.setColor(new Color(255, 215, 0));
                g2.drawString(part2, tx, ty);
                tx += fm.stringWidth(part2);

                g2.setColor(Color.WHITE);
                g2.drawString(part3, tx, ty);

                g2.dispose();
            }

            private String formatElapsed(long ms) {
                long sec = ms / 1000;
                long min = sec / 60;
                sec = sec % 60;
                return String.format("%d:%02d", min, sec);
            }
        };
        coinLabel.setBounds(0, 225, imgW, 40);
        bg.add(coinLabel);

        JButton btnTitle = new JButton("타이틀로 돌아가기");
        btnTitle.setBounds(108, 278, 246, 55);
        btnTitle.addActionListener(e -> gameFrame.showStart());
        bg.add(btnTitle);
        
        JPanel particlePanel = new JPanel() {
            private final List<GoldParticle> particles = new ArrayList<>();
            private final Timer timer;
            
            private final Color[] goldColors = {
                new Color(255, 215, 0),
                new Color(245, 190, 25),
                new Color(255, 235, 130),
                new Color(225, 165, 15)
            };

            {
                for (int i = 0; i < 50; i++) {
                    spawnParticle(Math.random() * W, Math.random() * (H * 0.6));
                }

                timer = new Timer(30, e -> {
                    Iterator<GoldParticle> it = particles.iterator();
                    while (it.hasNext()) {
                        GoldParticle p = it.next();
                        
                        p.x += p.vx + Math.sin(p.y * 0.05) * 0.3;
                        p.y += p.vy;
                        p.vy += 0.05;
                        p.rotation += p.rotationSpeed;
                        p.alpha -= 0.006;

                        if (p.alpha <= 0 || p.y > H) {
                            it.remove();
                        }
                    }

                    while (particles.size() < 70) {
                        spawnParticle(Math.random() * W, -10);
                    }
                    repaint();
                });
                timer.start();
            }

            private void spawnParticle(double startX, double startY) {
                double vx = Math.random() * 1.2 - 0.6;
                double vy = Math.random() * 1.0 + 0.8;
                double size = Math.random() * 3.5 + 2.5;
                Color selectedColor = goldColors[(int) (Math.random() * goldColors.length)];

                particles.add(new GoldParticle(startX, startY, vx, vy, size, selectedColor));
            }

            @Override
            public boolean contains(int x, int y) {
                return false;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GoldParticle[] currentParticles;
                synchronized (particles) {
                    currentParticles = particles.toArray(new GoldParticle[0]);
                }

                for (GoldParticle p : currentParticles) {
                    if (p == null || p.alpha <= 0) continue;

                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) Math.min(1f, p.alpha)));
                    g2.setColor(p.color);

                    AffineTransform oldTransform = g2.getTransform();
                    g2.translate(p.x, p.y);
                    g2.rotate(Math.toRadians(p.rotation));

                    int pSize = (int) p.size;
                    g2.fillRect(-pSize / 2, -pSize / 2, pSize, pSize);

                    g2.setTransform(oldTransform);
                }
                g2.dispose();
            }
        };
        particlePanel.setOpaque(false);
        particlePanel.setBounds(0, 0, W, H);
        add(particlePanel, 0);

        btnTitle.setOpaque(false);
        btnTitle.setContentAreaFilled(false);
        btnTitle.setBorderPainted(true);
        btnTitle.setFocusPainted(false);
        btnTitle.setFocusable(false);
        btnTitle.setForeground(Color.WHITE);
        btnTitle.setFont(FontManager.getNeoDgm(15));
        btnTitle.setHorizontalAlignment(SwingConstants.LEFT);
        btnTitle.setBorder(BorderFactory.createEmptyBorder(0, 75, 0, 0));
    }
}