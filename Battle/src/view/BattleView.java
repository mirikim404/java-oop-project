package view;

import entity.Steve;
import entity.mob.Mob;
import manager.WaveManager;
import skill.active.ActiveSkill;
import skill.consumable.ConsumableSkill;
import weapon.Sword;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleView extends JPanel {

	private GameFrame gameFrame;
	private Steve steve;
	private Mob mob;
	private int wave;
	private WaveManager waveManager;

    private JLabel expLabel;
    private JProgressBar expBar;
    private JLabel steveCoinLabel;
    private JLabel messageLabel;
    private HeartPanel heartPanel;
    private ItemPanel itemPanel;

    private MobBattlePanel mobBattlePanel;
    private CardPanel cardPanel;
    private ImageIcon mobNormalIcon;
    private ImageIcon mobHurtIcon;
    private ImageIcon atkIcon;
    private ImageIcon defIcon;
    private ImageIcon aoeSlashIcon;
    private ImageIcon snowBallIcon;
    private ImageIcon fireChargeIcon;
    private ImageIcon healPotionIcon;
    private ImageIcon attackPotionIcon;
    private boolean attackPotionReady = false;
    private boolean guardReady = false;
    private int dragonPhase = -1;
    private float phaseFlashAlpha = 0f;
    private javax.swing.Timer phaseFlashTimer;
    private boolean inputLocked = false;

    private static final Color GOLD = new Color(230, 184, 76);
    private static final Color PANEL_DARK = new Color(12, 12, 20, 210);
    private static final Color PANEL_EDGE = new Color(52, 46, 72);

    private static final Map<String, String[]> MOB_IMAGE_MAP = new HashMap<>();
    static {
        MOB_IMAGE_MAP.put("\uC880\uBE44", new String[]{"resources/monster/zombie.png", "resources/monster/zombie_v1.png"});
        MOB_IMAGE_MAP.put("\uC2A4\uCF08\uB808\uD1A4", new String[]{"resources/monster/skeleton.png", "resources/monster/skeleton_v1.png"});
        MOB_IMAGE_MAP.put("\uB9C8\uB140", new String[]{"resources/monster/witch.png", "resources/monster/witch.png"});
        MOB_IMAGE_MAP.put("\uD06C\uB9AC\uD37C", new String[]{"resources/monster/creeper.png", "resources/monster/creeper_v1.png"});
        MOB_IMAGE_MAP.put("\uC704\uB354\uC2A4\uCF08\uB808\uD1A4", new String[]{"resources/monster/witherskeleton.png", "resources/monster/witherskeleton_v1.png"});
        MOB_IMAGE_MAP.put("\uD53C\uAE00\uB9B0", new String[]{"resources/monster/piglin.png", "resources/monster/piglin_v1.png"});
        MOB_IMAGE_MAP.put("\uC5D4\uB354\uB4DC\uB798\uACE4", new String[]{"resources/monster/enderdragon_phase0.png", "resources/monster/enderdragon_phase0.png"});
    }

    public BattleView(GameFrame gameFrame, Steve steve, WaveManager waveManager, Mob mob, int wave) {
        this.gameFrame = gameFrame;
        this.steve = steve;
        this.mob = mob;
        this.wave = wave;
        this.waveManager = waveManager;

        loadImages();
        updateDragonPhaseImage(false);
        resetSkillCooldownsForWave();

        BackgroundPanel root = new BackgroundPanel("resources/bg/battle_bg.png");
        root.setLayout(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);

        root.add(buildTopHud(), BorderLayout.NORTH);
        root.add(buildPlayerSidebar(), BorderLayout.WEST);

        mobBattlePanel = new MobBattlePanel(
                mobNormalIcon != null ? mobNormalIcon.getImage() : null,
                mobHurtIcon != null ? mobHurtIcon.getImage() : null
        );

        cardPanel = new CardPanel();
        root.add(new BattleLayerPanel(), BorderLayout.CENTER);

        refreshUI();
    }

    private static class BackgroundPanel extends JPanel {
        private Image bgImage;

        BackgroundPanel(String imagePath) {
            setOpaque(true);
            try {
                ImageIcon ic = new ImageIcon(imagePath);
                if (ic.getIconWidth() > 0) bgImage = ic.getImage();
            } catch (Exception ignored) {
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (bgImage != null) {
                g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                GradientPaint gp = new GradientPaint(0, 0, new Color(31, 27, 44),
                        0, getHeight(), new Color(13, 13, 20));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setColor(new Color(0, 0, 0, 80));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private static class DamageNumber {
        String text;
        Color color;
        float x, y, alpha;

        DamageNumber(String text, Color color, float x, float y) {
            this.text = text;
            this.color = color;
            this.x = x;
            this.y = y;
            this.alpha = 1.0f;
        }
    }

    private class BattleLayerPanel extends JLayeredPane {
        BattleLayerPanel() {
            setOpaque(false);
            messageLabel = new JLabel("", SwingConstants.CENTER);
            messageLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            messageLabel.setForeground(Color.WHITE);
            messageLabel.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            add(mobBattlePanel, JLayeredPane.DEFAULT_LAYER);
            add(messageLabel, JLayeredPane.MODAL_LAYER);
            add(cardPanel, JLayeredPane.PALETTE_LAYER);
        }

        @Override
        public void doLayout() {
            int w = getWidth();
            int h = getHeight();

            int cardW = Math.min(w - 40, 620);
            int cardH = 180;
            int cardX = (w - cardW) / 2;
            int cardY = h - cardH;
            mobBattlePanel.setCardTopY(cardY);
            mobBattlePanel.setBounds(0, 0, w, h);
            messageLabel.setBounds(0, Math.max(64, cardY - 28), w, 24);
            cardPanel.setBounds(cardX, cardY, cardW, cardH);
        }
    }

    private class MobBattlePanel extends JPanel {
        private Image normalImg, hurtImg;
        private boolean showHurt = false;
        private boolean isDead = false;
        private float deadAlpha = 1.0f;
        private int deadDropY = 0;
        private int cardTopY = 330;

        private List<DamageNumber> dmgNumbers = new ArrayList<>();
        private javax.swing.Timer animTimer;

        MobBattlePanel(Image normal, Image hurt) {
            this.normalImg = normal;
            this.hurtImg = hurt;
            setOpaque(false);

            animTimer = new javax.swing.Timer(30, e -> {
                for (DamageNumber d : dmgNumbers) {
                    d.y -= 1.8f;
                    d.alpha = Math.max(0f, d.alpha - 0.018f);
                }
                dmgNumbers.removeIf(d -> d.alpha <= 0f);
                repaint();
                if (dmgNumbers.isEmpty()) animTimer.stop();
            });
        }

        void setCardTopY(int cardTopY) {
            this.cardTopY = cardTopY;
        }

        void setImages(Image normal, Image hurt) {
            this.normalImg = normal;
            this.hurtImg = hurt;
            repaint();
        }

        void showDamage(int dmg, boolean isPlayerAttack) {
            float cx = getWidth() * 0.5f + (float) (Math.random() * 60 - 30);
            float cy = getHeight() * 0.40f + (float) (Math.random() * 30 - 15);
            Color c = isPlayerAttack ? new Color(255, 80, 80) : new Color(255, 220, 60);
            dmgNumbers.add(new DamageNumber("-" + dmg, c, cx, cy));
            if (!animTimer.isRunning()) animTimer.start();
        }

        void showDamage(int dmg, Color color) {
            float cx = getWidth() * 0.5f + (float) (Math.random() * 60 - 30);
            float cy = getHeight() * 0.40f + (float) (Math.random() * 30 - 15);
            dmgNumbers.add(new DamageNumber("-" + dmg, color, cx, cy));
            if (!animTimer.isRunning()) animTimer.start();
        }

        void playHitFlash() {
            if (hurtImg == null) return;
            showHurt = true;
            repaint();
            javax.swing.Timer t = new javax.swing.Timer(180, e -> {
                showHurt = false;
                repaint();
            });
            t.setRepeats(false);
            t.start();
        }

        void playShake() {
            Point origin = getLocation();
            int[] offsets = {-10, 10, -7, 7, -4, 4, -2, 2, 0};
            int[] idx = {0};
            javax.swing.Timer shake = new javax.swing.Timer(35, null);
            shake.addActionListener(e -> {
                if (idx[0] < offsets.length) {
                    setLocation(origin.x + offsets[idx[0]++], origin.y);
                } else {
                    setLocation(origin);
                    shake.stop();
                }
            });
            shake.start();
        }

        void playDeathAnimation(Runnable onComplete) {
            isDead = true;
            javax.swing.Timer deathTimer = new javax.swing.Timer(25, null);
            deathTimer.addActionListener(e -> {
                deadAlpha -= 0.045f;
                deadDropY += 5;
                repaint();
                if (deadAlpha <= 0f) {
                    deadAlpha = 0f;
                    deathTimer.stop();
                    if (onComplete != null) SwingUtilities.invokeLater(onComplete);
                }
            });
            deathTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            drawMobStats(g2);

            Image img = (showHurt && hurtImg != null) ? hurtImg : normalImg;
            if (img != null) {
                int pw = getWidth(), ph = getHeight();
                int iw = img.getWidth(null), ih = img.getHeight(null);
                if (iw > 0 && ih > 0) {
                    int topLimit = 78;
                    int bottomLimit = Math.max(topLimit + 90, cardTopY - 18);
                    int availableH = bottomLimit - topLimit;
                    double scale = (availableH * 0.92) / ih;
                    if (iw * scale > pw * 0.46) scale = (pw * 0.46) / iw;
                    int dw = (int) (iw * scale);
                    int dh = (int) (ih * scale);
                    if (dh > availableH) {
                        double fit = (double) availableH / dh;
                        dw = (int) (dw * fit);
                        dh = availableH;
                    }
                    int dx = (pw - dw) / 2;
                    int dy = topLimit + (availableH - dh) / 2;
                    if (isDead) {
                        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, deadAlpha)));
                        g2.drawImage(img, dx, dy + deadDropY, dw, dh, null);
                    } else {
                        g2.drawImage(img, dx, dy, dw, dh, null);
                    }
                }
            } else {
                g2.setFont(new Font("Dialog", Font.BOLD, 62));
                g2.setColor(Color.WHITE);
                drawCentered(g2, mob.getName(), 0, getHeight() / 2, getWidth());
            }

            g2.setFont(new Font("Dialog", Font.BOLD, 28));
            for (DamageNumber d : dmgNumbers) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, d.alpha));
                g2.setColor(Color.BLACK);
                g2.drawString(d.text, (int) d.x + 2, (int) d.y + 2);
                g2.setColor(d.color);
                g2.drawString(d.text, (int) d.x, (int) d.y);
            }

            if (phaseFlashAlpha > 0f) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, phaseFlashAlpha));
                g2.setColor(new Color(170, 80, 255));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.dispose();
        }

        private void drawMobStats(Graphics2D g2) {
            int w = Math.min(118, getWidth() - 50);
            int x = (getWidth() - w) / 2;
            int y = 14;

            drawMobStat(g2, x, y + 20, mob.getAttackPower(), true);
            drawMobStat(g2, x + 62, y + 20, getMobDefense(), false);
        }

        private void drawMobStat(Graphics2D g2, int x, int y, int value, boolean attack) {
            ImageIcon icon = attack ? atkIcon : defIcon;
            drawIcon(g2, icon, x, y - 17, 18, 18);
            g2.setFont(new Font("Monospaced", Font.BOLD, 12));
            g2.setColor(new Color(255, 226, 120));
            g2.drawString(String.valueOf(value), x + 24, y - 3);
        }
    }

    enum CardType { ATTACK, GUARD, AOE, ICE, FIRE }
    enum CardAction { ATTACK, GUARD, AOE_SLASH, ACTIVE_SKILL }

    static class Card {
        String name, type, description;
        int cost;
        Color color;
        CardType cardType;
        CardAction action;
        ActiveSkill activeSkill;
        Rectangle bounds;

        Card(String name, String type, int cost, Color color, String description, CardType cardType, CardAction action) {
            this.name = name;
            this.type = type;
            this.cost = cost;
            this.color = color;
            this.description = description;
            this.cardType = cardType;
            this.action = action;
        }

        Card(String name, String type, int cost, Color color, String description, CardType cardType, ActiveSkill activeSkill) {
            this(name, type, cost, color, description, cardType, CardAction.ACTIVE_SKILL);
            this.activeSkill = activeSkill;
        }
    }

    static class CardStyle {
        static final int WIDTH = 94;
        static final int HEIGHT = 138;
        static final int GAP = 12;
        static final int ARC = 0;
        static final int HEADER_H = 20;
        static final int ART_X = 8;
        static final int ART_Y = 25;
        static final int ART_H = 60;
        static final int DESC_Y = 90;
        static final int DESC_H = 43;
        static final Color SHADOW = new Color(0, 0, 0, 80);
        static final Color ART_BG = new Color(18, 12, 10, 165);
        static final Color DESC_BG = new Color(47, 49, 86);
        static final Color BADGE = new Color(74, 180, 82);
        static final Color BORDER = new Color(234, 196, 74);
    }

    private class CardPanel extends JPanel {
        private List<Card> deckCards = new ArrayList<>();
        private List<Card> handCards = new ArrayList<>();
        private Map<Card, Float> disappearingCards = new HashMap<>();
        private Card hoveredCard = null;
        private Card selectedCard = null;
        private int mouseX, mouseY;
        private javax.swing.Timer discardTimer;

        CardPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 180));
            rebuildDeck();
            refillHand();
            discardTimer = new javax.swing.Timer(25, e -> tickDiscardAnimation());

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                    Card prev = hoveredCard;
                    hoveredCard = null;
                    for (Card c : handCards) {
                        if (!disappearingCards.containsKey(c) && c.bounds != null && c.bounds.contains(e.getPoint())) {
                            hoveredCard = c;
                            break;
                        }
                    }
                    if (hoveredCard != prev) repaint();
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (inputLocked) return;
                    for (Card c : new ArrayList<>(handCards)) {
                        if (!disappearingCards.containsKey(c) && c.bounds != null && c.bounds.contains(e.getPoint())) {
                            selectedCard = c;
                            repaint();
                            if (handleCardAction(c)) {
                                consumeCard(c);
                            }
                            return;
                        }
                    }
                    selectedCard = null;
                    repaint();
                }
            });
        }

        private void rebuildDeck() {
            deckCards.clear();
            deckCards.add(new Card("Attack", "Basic", 0, new Color(0x8B2020), "Deal weapon damage.", CardType.ATTACK, CardAction.ATTACK));
            deckCards.add(new Card("Guard", "Basic", 0, new Color(0x1A3A8A), "Block the next hit.", CardType.GUARD, CardAction.GUARD));
            Card aoeSlash = new Card("AoeSlash", "Skill", 0, new Color(0x7A3A0A), "Default sword skill.", CardType.AOE, CardAction.AOE_SLASH);
            if (isCardAvailable(aoeSlash)) deckCards.add(aoeSlash);

            ActiveSkill[] skills = steve.getActiveSkills();
            if (skills != null) {
                for (ActiveSkill skill : skills) {
                    if (skill == null) continue;
                    String simpleName = skill.getClass().getSimpleName();
                    if ("SnowBall".equals(simpleName)) {
                        Card snowBall = new Card("SnowBall", "Skill", 0, new Color(0x1D5F8F), "Stun the mob.", CardType.ICE, skill);
                        if (isCardAvailable(snowBall)) deckCards.add(snowBall);
                    } else if ("FireCharge".equals(simpleName)) {
                        Card fireCharge = new Card("FireCharge", "Skill", 0, new Color(0xA63D16), "Damage and burn.", CardType.FIRE, skill);
                        if (isCardAvailable(fireCharge)) deckCards.add(fireCharge);
                    }
                }
            }
        }

        private void refillHand() {
            rebuildDeck();
            handCards.clear();
            handCards.addAll(deckCards);
            disappearingCards.clear();
            selectedCard = null;
            hoveredCard = null;
        }

        private void consumeCard(Card card) {
            disappearingCards.put(card, 0f);
            selectedCard = null;
            hoveredCard = null;
            if (!discardTimer.isRunning()) discardTimer.start();
            repaint();
        }

        private void tickDiscardAnimation() {
            List<Card> finished = new ArrayList<>();
            for (Map.Entry<Card, Float> entry : disappearingCards.entrySet()) {
                float next = entry.getValue() + 0.12f;
                if (next >= 1f) {
                    finished.add(entry.getKey());
                } else {
                    entry.setValue(next);
                }
            }

            for (Card card : finished) {
                disappearingCards.remove(card);
                handCards.remove(card);
            }

            if (handCards.isEmpty()) {
                refillHand();
            }
            if (disappearingCards.isEmpty()) {
                discardTimer.stop();
            }
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int bandY = 76;
            g2.setColor(new Color(9, 9, 15, 158));
            g2.fillRect(0, bandY, getWidth(), getHeight() - bandY);
            g2.setColor(new Color(255, 255, 255, 30));
            g2.drawLine(0, bandY, getWidth(), bandY);

            drawCards(g2);
            if (hoveredCard != null) drawTooltip(g2, hoveredCard);
            g2.dispose();
        }

        private void drawCards(Graphics2D g2) {
            int cardW = CardStyle.WIDTH, cardH = CardStyle.HEIGHT;
            int gap = CardStyle.GAP;
            int total = handCards.size();
            int startX = (getWidth() - (total * cardW + (total - 1) * gap)) / 2;
            int baseY = 20;

            for (int i = 0; i < total; i++) {
                Card c = handCards.get(i);
                int cx = startX + i * (cardW + gap);
                boolean lifted = c == hoveredCard || c == selectedCard;
                int drawW = lifted ? cardW + 14 : cardW;
                int drawH = lifted ? cardH + 20 : cardH;
                int cy = lifted ? baseY - 16 : baseY;
                int drawX = lifted ? cx - 7 : cx;
                c.bounds = new Rectangle(drawX, cy, drawW, drawH);
                float discardProgress = disappearingCards.getOrDefault(c, 0f);
                drawCard(g2, c, drawX, cy - (int) (28 * discardProgress), drawW, drawH,
                        lifted, 1f - discardProgress);
            }
        }

        private void drawCard(Graphics2D g2, Card card, int x, int y, int w, int h, boolean highlighted, float alpha) {
            Composite oldComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));

            g2.setColor(highlighted ? new Color(0, 0, 0, 130) : CardStyle.SHADOW);
            g2.fillRoundRect(x + 4, y + 6, w, h, CardStyle.ARC, CardStyle.ARC);

            g2.setColor(card.color.darker());
            g2.fillRoundRect(x, y, w, h, CardStyle.ARC, CardStyle.ARC);

            g2.setColor(card.color);
            g2.fillRect(x + 4, y + 4, w - 8, CardStyle.HEADER_H - 4);

            g2.setStroke(highlighted ? new BasicStroke(2.5f) : new BasicStroke(1.5f));
            g2.setColor(highlighted ? new Color(255, 230, 120) : CardStyle.BORDER);
            g2.drawRoundRect(x, y, w, h, CardStyle.ARC, CardStyle.ARC);

            g2.setColor(CardStyle.ART_BG);
            g2.fillRect(x + CardStyle.ART_X, y + CardStyle.ART_Y, w - CardStyle.ART_X * 2, CardStyle.ART_H);
            drawCardArt(g2, card, x + CardStyle.ART_X, y + CardStyle.ART_Y, w - CardStyle.ART_X * 2, CardStyle.ART_H);

            g2.setColor(CardStyle.DESC_BG);
            g2.fillRect(x + 4, y + CardStyle.DESC_Y, w - 8, Math.max(20, h - CardStyle.DESC_Y - 5));

            g2.setColor(new Color(12, 15, 26));
            g2.fillOval(x + 5, y + 4, 22, 22);
            int cooldown = getCardCooldown(card);
            g2.setColor(CardStyle.BADGE);
            g2.fillOval(x + 7, y + 6, 18, 18);
            g2.setColor(Color.WHITE);
            g2.setFont(pixelFont(11));
            String badge = String.valueOf(cooldown);
            FontMetrics badgeMetrics = g2.getFontMetrics();
            g2.drawString(badge, x + 16 - badgeMetrics.stringWidth(badge) / 2, y + 19);

            g2.setFont(pixelFont(10));
            g2.setColor(Color.WHITE);
            drawCentered(g2, card.name, x + 22, y + 17, w - 24);
            g2.setFont(pixelFont(8));
            g2.setColor(new Color(214, 190, 136));
            drawCentered(g2, card.type, x, y + CardStyle.DESC_Y + 12, w);
            g2.setColor(new Color(225, 222, 210));
            drawWrappedCentered(g2, card.description, x + 8, y + CardStyle.DESC_Y + 25, w - 16, 10);
            g2.setComposite(oldComposite);
        }

        private void drawCardArt(Graphics2D g2, Card card, int x, int y, int w, int h) {
            int mx = x + w / 2, my = y + h / 2;
            if (card.cardType == CardType.ATTACK) {
                drawIcon(g2, atkIcon, x + 8, y + 6, w - 16, h - 12);
                return;
            }
            if (card.cardType == CardType.GUARD) {
                drawIcon(g2, defIcon, x + 8, y + 6, w - 16, h - 12);
                return;
            }
            if (card.cardType == CardType.AOE) {
                drawIcon(g2, aoeSlashIcon, x + 8, y + 6, w - 16, h - 12);
                return;
            }
            if (card.cardType == CardType.ICE && snowBallIcon != null) {
                drawIcon(g2, snowBallIcon, x + 8, y + 6, w - 16, h - 12);
                return;
            }
            if (card.cardType == CardType.FIRE && fireChargeIcon != null) {
                drawIcon(g2, fireChargeIcon, x + 8, y + 6, w - 16, h - 12);
                return;
            }
            if (card.cardType == CardType.ATTACK || card.cardType == CardType.AOE) {
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(210, 210, 220));
                g2.drawLine(mx - 16, my + 18, mx + 16, my - 16);
                g2.setColor(new Color(118, 75, 46));
                g2.drawLine(mx - 22, my + 24, mx - 9, my + 11);
                if (card.cardType == CardType.AOE) {
                    g2.setColor(new Color(255, 210, 90, 170));
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawArc(mx - 24, my - 18, 48, 36, 20, 140);
                }
            } else if (card.cardType == CardType.FIRE) {
                GradientPaint flame = new GradientPaint(mx, y, new Color(255, 225, 72), mx, y + h, new Color(210, 54, 24));
                g2.setPaint(flame);
                Path2D.Double fire = new Path2D.Double();
                fire.moveTo(mx, my - 23);
                fire.curveTo(mx + 24, my, mx + 9, my + 25, mx, my + 25);
                fire.curveTo(mx - 18, my + 20, mx - 20, my, mx, my - 23);
                g2.fill(fire);
            } else if (card.cardType == CardType.ICE) {
                g2.setColor(new Color(190, 230, 255));
                g2.fillOval(mx - 18, my - 18, 36, 36);
                g2.setColor(new Color(80, 150, 230));
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(mx - 18, my - 18, 36, 36);
            } else {
                g2.setColor(new Color(80, 145, 255, 90));
                g2.fillOval(mx - 21, my - 22, 42, 44);
                g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(220, 235, 255));
                g2.drawLine(mx, my - 18, mx, my + 18);
                g2.drawLine(mx - 13, my - 4, mx + 13, my - 4);
            }
        }

        private void drawTooltip(Graphics2D g2, Card card) {
            int tw = 180, th = 64;
            int tx = mouseX + 14;
            int ty = mouseY - th - 10;
            if (tx + tw > getWidth() - 8) tx = mouseX - tw - 10;
            if (ty < 4) ty = mouseY + 20;

            g2.setColor(new Color(15, 14, 24, 238));
            g2.fillRoundRect(tx, ty, tw, th, 8, 8);
            g2.setColor(GOLD);
            g2.drawRoundRect(tx, ty, tw, th, 8, 8);
            g2.setFont(pixelFont(10));
            g2.setColor(new Color(255, 226, 120));
            g2.drawString(card.name, tx + 10, ty + 19);
            g2.setFont(pixelFont(8));
            g2.setColor(Color.WHITE);
            g2.drawString(card.description, tx + 10, ty + 40);
        }

        private Font pixelFont(int size) {
            return new Font("Monospaced", Font.BOLD, size);
        }
    }

    private class ItemPanel extends JPanel {
        private final List<ItemSlot> slots = new ArrayList<>();

        ItemPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(96, 132));
            setMaximumSize(new Dimension(96, 132));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (inputLocked) return;
                    for (ItemSlot slot : slots) {
                        if (slot.bounds.contains(e.getPoint())) {
                            useConsumable(slot.className);
                            return;
                        }
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            slots.clear();
            drawConsumableSlot(g2, "HealPotion", healPotionIcon, 8, 12);
            drawConsumableSlot(g2, "AttackPotion", attackPotionIcon, 8, 66);
            g2.dispose();
        }

        private void drawConsumableSlot(Graphics2D g2, String className, ImageIcon icon, int x, int y) {
            int qty = getConsumableQuantity(className);
            Rectangle bounds = new Rectangle(x, y, 48, 44);
            slots.add(new ItemSlot(className, bounds));

            g2.setColor(new Color(8, 8, 14, 185));
            g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            g2.setColor(qty > 0 ? GOLD : new Color(88, 88, 96));
            g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

            drawIcon(g2, icon, bounds.x + 6, bounds.y + 5, 28, 28);
            g2.setFont(new Font("Monospaced", Font.BOLD, 11));
            g2.setColor(Color.WHITE);
            g2.drawString("x" + qty, bounds.x + 29, bounds.y + 35);

            if (qty <= 0) {
                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRect(bounds.x + 1, bounds.y + 1, bounds.width - 1, bounds.height - 1);
            }
        }
    }

    private static class ItemSlot {
        String className;
        Rectangle bounds;

        ItemSlot(String className, Rectangle bounds) {
            this.className = className;
            this.bounds = bounds;
        }
    }

    private class HeartPanel extends JPanel {
        HeartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(86, 180));
            setMaximumSize(new Dimension(86, 180));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 10;
            int y = 18;
            int w = getWidth() - 18;
            g2.setColor(new Color(8, 8, 14, 185));
            g2.fillRect(x, y, w, getHeight() - 34);
            g2.setColor(PANEL_EDGE);
            g2.drawRect(x, y, w, getHeight() - 34);

            g2.setFont(new Font("Dialog", Font.BOLD, 12));
            g2.setColor(Color.WHITE);
            drawCentered(g2, steve.getName(), x, y + 22, w);

            int heartSize = 52;
            int heartX = x + (w - heartSize) / 2;
            int heartY = y + 58;
            drawHeart(g2, heartX, heartY, heartSize, getHealthRatio());

            g2.setFont(new Font("Monospaced", Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            drawCentered(g2, String.valueOf(steve.getHealth()), heartX, heartY + 22, heartSize);
            g2.setColor(new Color(230, 230, 230));
            g2.drawLine(heartX + 15, heartY + 27, heartX + heartSize - 15, heartY + 27);
            g2.setColor(Color.WHITE);
            drawCentered(g2, String.valueOf(steve.getMaxHealth()), heartX, heartY + 39, heartSize);
            g2.dispose();
        }

        private double getHealthRatio() {
            if (steve.getMaxHealth() <= 0) return 0;
            return Math.max(0, Math.min(1, (double) steve.getHealth() / steve.getMaxHealth()));
        }

        private void drawHeart(Graphics2D g2, int x, int y, int size, double ratio) {
            Path2D.Double heart = new Path2D.Double();
            double s = size / 18.0;
            heart.moveTo(x + 9 * s, y + 16 * s);
            heart.curveTo(x + 2 * s, y + 10 * s, x, y + 6 * s, x + 3 * s, y + 2 * s);
            heart.curveTo(x + 6 * s, y - 1 * s, x + 9 * s, y + 2 * s, x + 9 * s, y + 5 * s);
            heart.curveTo(x + 9 * s, y + 2 * s, x + 12 * s, y - 1 * s, x + 15 * s, y + 2 * s);
            heart.curveTo(x + 18 * s, y + 6 * s, x + 16 * s, y + 10 * s, x + 9 * s, y + 16 * s);
            g2.setColor(new Color(72, 70, 82));
            g2.fill(heart);

            Shape oldClip = g2.getClip();
            g2.setClip(heart);
            int fillHeight = (int) Math.round(size * ratio);
            g2.setColor(new Color(230, 52, 68));
            g2.fillRect(x, y + size - fillHeight, size, fillHeight);
            g2.setClip(oldClip);

            g2.setColor(new Color(255, 178, 190));
            g2.draw(heart);
        }
    }

    private static class CoinLabel extends JLabel {
        CoinLabel() {
            super("", SwingConstants.RIGHT);
            setFont(new Font("Monospaced", Font.BOLD, 12));
            setForeground(new Color(255, 224, 82));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            FontMetrics fm = g2.getFontMetrics(getFont());
            int textW = fm.stringWidth(getText());
            int icon = 12;
            int gap = 5;
            int totalW = icon + gap + textW;
            int x = getWidth() - totalW;
            int y = (getHeight() - icon) / 2 + 1;

            drawCoinIcon(g2, x, y, icon);
            g2.setFont(getFont());
            g2.setColor(getForeground());
            g2.drawString(getText(), x + icon + gap, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }

        private void drawCoinIcon(Graphics2D g2, int x, int y, int size) {
            g2.setColor(new Color(86, 56, 18));
            g2.fillRect(x + 2, y, size - 4, size);
            g2.fillRect(x, y + 2, size, size - 4);
            g2.setColor(new Color(246, 198, 58));
            g2.fillRect(x + 3, y + 2, size - 6, size - 4);
            g2.fillRect(x + 2, y + 4, size - 4, size - 8);
            g2.setColor(new Color(255, 238, 121));
            g2.fillRect(x + 4, y + 3, 3, 3);
        }
    }

    private static class MinecraftButton extends JButton {
        MinecraftButton(String text) {
            super(text);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            Color base = getModel().isPressed() ? new Color(50, 45, 65) :
                    getModel().isRollover() ? new Color(118, 108, 132) : new Color(78, 72, 94);
            g2.setColor(base);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setColor(base.brighter().brighter());
            g2.drawLine(0, 0, getWidth() - 2, 0);
            g2.drawLine(0, 0, 0, getHeight() - 2);
            g2.setColor(base.darker().darker());
            g2.drawLine(1, getHeight() - 1, getWidth() - 1, getHeight() - 1);
            g2.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight() - 1);
            g2.setColor(isEnabled() ? Color.WHITE : Color.GRAY);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int tx = (getWidth() - fm.stringWidth(getText())) / 2;
            int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(getText(), tx, ty);
            g2.dispose();
        }
    }

    private void loadImages() {
        atkIcon = loadIcon("resources/icon/ATK.png");
        defIcon = loadIcon("resources/icon/DEF.png");
        aoeSlashIcon = loadIcon("resources/icon/AoeSlash.png");
        snowBallIcon = loadIcon("resources/shop/SnowBall.png");
        fireChargeIcon = loadIcon("resources/shop/FireCharge.png");
        healPotionIcon = loadIcon("resources/shop/HealPotion.png");
        attackPotionIcon = loadIcon("resources/shop/AttackPotion.png");
        String[] paths = MOB_IMAGE_MAP.get(mob.getName());
        if (paths != null) {
            mobNormalIcon = loadIcon(paths[0]);
            mobHurtIcon = loadIcon(paths[1]);
        }
    }

    private ImageIcon loadIcon(String path) {
        try {
            ImageIcon ic = new ImageIcon(path);
            return ic.getIconWidth() > 0 ? ic : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void updateDragonPhaseImage(boolean animate) {
        if (!"\uC5D4\uB354\uB4DC\uB798\uACE4".equals(mob.getName())) return;

        double hpRatio = mob.getMaxHealth() <= 0 ? 0 : (double) mob.getHealth() / mob.getMaxHealth();
        int nextPhase;
        if (hpRatio > 0.75) {
            nextPhase = 0;
        } else if (hpRatio > 0.45) {
            nextPhase = 1;
        } else {
            nextPhase = 2;
        }

        if (nextPhase == dragonPhase) return;
        dragonPhase = nextPhase;

        mobNormalIcon = loadIcon("resources/monster/enderdragon_phase" + dragonPhase + ".png");
        mobHurtIcon = mobNormalIcon;
        if (mobBattlePanel != null) {
            mobBattlePanel.setImages(
                    mobNormalIcon != null ? mobNormalIcon.getImage() : null,
                    mobHurtIcon != null ? mobHurtIcon.getImage() : null
            );
        }

        if (animate) {
            playDragonPhaseEffect();
        }
    }

    private void playDragonPhaseEffect() {
        phaseFlashAlpha = 0.45f;
        if (phaseFlashTimer != null && phaseFlashTimer.isRunning()) {
            phaseFlashTimer.stop();
        }
        phaseFlashTimer = new javax.swing.Timer(35, e -> {
            phaseFlashAlpha = Math.max(0f, phaseFlashAlpha - 0.045f);
            if (mobBattlePanel != null) mobBattlePanel.repaint();
            if (phaseFlashAlpha <= 0f) {
                phaseFlashTimer.stop();
            }
        });
        phaseFlashTimer.start();
        if (mobBattlePanel != null) {
            mobBattlePanel.playShake();
        }
        showMessage("EnderDragon phase " + (dragonPhase + 1) + "!");
    }

    private static void drawIcon(Graphics2D g2, ImageIcon icon, int x, int y, int w, int h) {
        if (icon == null || icon.getIconWidth() <= 0) return;

        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();

        double scale = Math.min((double) w / iw, (double) h / ih);
        int dw = Math.max(1, (int) (iw * scale));
        int dh = Math.max(1, (int) (ih * scale));
        int dx = x + (w - dw) / 2;
        int dy = y + (h - dh) / 2;

        Object oldHint = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);

        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        g2.drawImage(icon.getImage(), dx, dy, dw, dh, null);

        if (oldHint != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
        }
    }
    private JPanel buildTopHud() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 64));
        panel.setBorder(BorderFactory.createEmptyBorder(4, 98, 4, 24));

        JPanel expWrap = new JPanel(new BorderLayout(0, 3));
        expWrap.setOpaque(false);
        expLabel = new JLabel("", SwingConstants.CENTER);
        expLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        expLabel.setForeground(Color.WHITE);

        expBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(12, 12, 18));
                g2.fillRect(0, 0, getWidth(), getHeight());
                double ratio = getMaximum() == 0 ? 0 : (double) getValue() / getMaximum();
                g2.setColor(new Color(48, 108, 255));
                g2.fillRect(2, 2, (int) ((getWidth() - 4) * ratio), getHeight() - 4);
                g2.setColor(GOLD);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        expBar.setPreferredSize(new Dimension(0, 14));
        expBar.setBorderPainted(false);
        expBar.setStringPainted(false);
        expWrap.add(expLabel, BorderLayout.NORTH);
        expWrap.add(expBar, BorderLayout.CENTER);

        steveCoinLabel = new CoinLabel();
        steveCoinLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        panel.add(expWrap, BorderLayout.CENTER);
        panel.add(steveCoinLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildPlayerSidebar() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(96, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        itemPanel = new ItemPanel();
        heartPanel = new HeartPanel();

        panel.add(itemPanel);
        panel.add(Box.createVerticalGlue());
        panel.add(heartPanel);
        return panel;
    }

    private JPanel buildBottomArea() {
        JPanel unused = new JPanel();
        unused.setOpaque(false);
        return unused;
    }

    private int getConsumableQuantity(String className) {
        ConsumableSkill[] consumables = steve.getConsumables();
        if (consumables == null) return 0;
        for (ConsumableSkill item : consumables) {
            if (item != null && className.equals(item.getClass().getSimpleName())) {
                return item.getQuantity();
            }
        }
        return 0;
    }

    private ConsumableSkill findConsumable(String className) {
        ConsumableSkill[] consumables = steve.getConsumables();
        if (consumables == null) return null;
        for (ConsumableSkill item : consumables) {
            if (item != null && className.equals(item.getClass().getSimpleName())) {
                return item;
            }
        }
        return null;
    }

    private void useConsumable(String className) {
        ConsumableSkill item = findConsumable(className);
        if (item == null || !item.hasStock()) {
            showMessage("No item left.");
            return;
        }

        if ("HealPotion".equals(className)) {
            item.use(steve);
            showMessage("HealPotion used.");
        } else if ("AttackPotion".equals(className)) {
            item.setQuantity(item.getQuantity() - 1);
            attackPotionReady = true;
            showMessage("AttackPotion ready. Next attack deals double damage.");
        }

        refreshUI();
    }

    private boolean handleCardAction(Card card) {
        if (inputLocked) return false;
        if (card == null) return false;

        switch (card.action) {
            case ATTACK:
                playerAttack();
                break;
            case GUARD:
                steve.block();
                guardReady = true;
                showMessage(steve.getName() + " is guarding. Choose another card.");
                refreshUI();
                return true;
            case AOE_SLASH:
                if (!useAoeSlash()) return false;
                break;
            case ACTIVE_SKILL:
                if (!useActiveSkill(card.activeSkill)) return false;
                if (card.activeSkill != null && "SnowBall".equals(card.activeSkill.getClass().getSimpleName())) {
                    refreshUI();
                    return true;
                }
                break;
        }

        refreshUI();
        if (!mob.isAlive()) {
            handleMobDead();
            return true;
        }
        mobTurn(guardReady);
        guardReady = false;
        return true;
    }

    private void playerAttack() {
        int rawDmg = steve.getTotalAttackPower();
        if (attackPotionReady) {
            rawDmg *= 2;
            attackPotionReady = false;
        }
        int mobHpBefore = mob.getHealth();
        mob.takeDamage(rawDmg);
        updateDragonPhaseImage(true);
        int actualDmg = mobHpBefore - mob.getHealth();
        showMessage(steve.getName() + " attacks! " + mob.getName() + " takes " + actualDmg + " damage.");
        mobBattlePanel.showDamage(actualDmg, true);
        mobBattlePanel.playHitFlash();
        mobBattlePanel.playShake();
    }

    private boolean useAoeSlash() {
        if (!(steve.getWeapon() instanceof Sword)) {
            showMessage("AoeSlash requires a sword.");
            return false;
        }

        Sword sword = (Sword) steve.getWeapon();
        if (sword.getAoeSlash() == null || !sword.getAoeSlash().isReady()) {
            showMessage("AoeSlash is on cooldown.");
            return false;
        }

        int before = mob.getHealth();
        List<Mob> mobs = new ArrayList<>();
        mobs.add(mob);
        sword.getAoeSlash().use(steve, mobs, sword);
        updateDragonPhaseImage(true);
        int actualDmg = before - mob.getHealth();
        showMessage("AoeSlash! " + mob.getName() + " takes " + actualDmg + " damage.");
        mobBattlePanel.showDamage(actualDmg, true);
        mobBattlePanel.playHitFlash();
        mobBattlePanel.playShake();
        return true;
    }

    private boolean useActiveSkill(ActiveSkill skill) {
        if (skill == null) return false;
        if (!skill.isReady()) {
            showMessage(skill.getClass().getSimpleName() + " is on cooldown.");
            return false;
        }

        int before = mob.getHealth();
        skill.use(steve, mob);
        updateDragonPhaseImage(true);
        int actualDmg = before - mob.getHealth();
        showMessage(skill.getClass().getSimpleName() + " used.");
        if (actualDmg > 0) {
            mobBattlePanel.showDamage(actualDmg, true);
            mobBattlePanel.playHitFlash();
            mobBattlePanel.playShake();
        }
        return true;
    }

    private void mobTurn(boolean playerBlocked) {
        int effectHpBefore = mob.getHealth();
        mob.processEffects();
        int effectDmg = effectHpBefore - mob.getHealth();
        if (effectDmg > 0) {
            mobBattlePanel.showDamage(effectDmg, new Color(255, 128, 32));
            mobBattlePanel.playHitFlash();
            showMessage(mob.getName() + " takes " + effectDmg + " burn damage.");
        }
        updateDragonPhaseImage(true);
        refreshUI();
        if (!mob.isAlive()) {
            handleMobDead();
            return;
        }

        if (mob.isStunned()) {
            showMessage(mob.getName() + " is stunned.");
            mob.setStunned(false);
            steve.onTurnEnd();
            decrementAoeSlashCooldown();
            refreshUI();
            return;
        }

        if (playerBlocked) {
            showMessage(mob.getName() + "'s attack was blocked.");
        } else {
            int rawDmg = mob.getAttackPower();
            int steveHpBefore = steve.getHealth();
            steve.takeDamage(rawDmg);
            int actualDmg = steveHpBefore - steve.getHealth();
            showMessage(mob.getName() + " attacks! " + steve.getName() + " takes " + actualDmg + " damage.");
            mobBattlePanel.showDamage(actualDmg, false);
        }

        steve.onTurnEnd();
        decrementAoeSlashCooldown();
        refreshUI();
        if (!steve.isAlive()) {
            handleSteveDead();
            return;
        }
    }

    private void decrementAoeSlashCooldown() {
        if (steve.getWeapon() instanceof Sword) {
            Sword sword = (Sword) steve.getWeapon();
            if (sword.getAoeSlash() != null) sword.getAoeSlash().decrementCooldown();
        }
    }

    private void resetSkillCooldownsForWave() {
        if (steve.getWeapon() instanceof Sword) {
            Sword sword = (Sword) steve.getWeapon();
            if (sword.getAoeSlash() != null) {
                writeIntField(sword.getAoeSlash(), "currentCooldown", 0);
            }
        }

        ActiveSkill[] skills = steve.getActiveSkills();
        if (skills == null) return;
        for (ActiveSkill skill : skills) {
            if (skill != null) {
                writeIntField(skill, "currentCooldown", 0);
            }
        }
    }

    private void handleMobDead() {
        if (inputLocked) return;
        inputLocked = true;
        setGlassPaneBlocking(true);

        int coin = mob.getDropCoin();
        int exp = mob.getDropExp();

        steve.gainCoin(coin);
        steve.gainExp(exp);

        if (steve.hasPendingLevelUp()) {
            showLevelUpDialog();
        }

        showMessage(mob.getName() + " defeated! Coin +" + coin + "  EXP +" + exp);
        refreshUI();

        mobBattlePanel.playDeathAnimation(() -> {
            javax.swing.Timer t = new javax.swing.Timer(800, e -> {
                gameFrame.showVictory(steve, waveManager, wave);
            });
            t.setRepeats(false);
            t.start();
        });
    }

    private void handleSteveDead() {
        if (inputLocked) return;
        inputLocked = true;
        setGlassPaneBlocking(true);

        showMessage(steve.getName() + " has fallen.");
        refreshUI();

        javax.swing.Timer t = new javax.swing.Timer(1400, e -> {
            gameFrame.showDead(steve, waveManager);
        });
        t.setRepeats(false);
        t.start();
    }

    private void refreshUI() {
        int expMax = getSteveMaxExp();
        int exp = Math.max(0, steve.getExp());
        expBar.setMaximum(Math.max(1, expMax));
        expBar.setValue(Math.min(exp, expBar.getMaximum()));
        expLabel.setText("Lv " + steve.getLevel() + "   EXP " + exp + " / " + expBar.getMaximum());
        steveCoinLabel.setText(String.valueOf(steve.getCoin()));

        if (heartPanel != null) heartPanel.repaint();
        if (itemPanel != null) itemPanel.repaint();
        if (mobBattlePanel != null) mobBattlePanel.repaint();
        if (cardPanel != null) cardPanel.repaint();
    }
    
    private void showLevelUpDialog() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Level Up",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        root.setBackground(new Color(24, 24, 32));

        JLabel title = new JLabel("LEVEL UP!", SwingConstants.CENTER);
        title.setFont(new Font("Dialog", Font.BOLD, 28));
        title.setForeground(new Color(255, 215, 0));
        root.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setOpaque(false);

        cards.add(buildLevelUpCard("Max HP +10", "체력을 올리고 전부 회복합니다.", () -> {
            steve.applyLevelUpChoice(1);
            dialog.dispose();
            refreshUI();
            if (steve.hasPendingLevelUp()) showLevelUpDialog();
        }));

        cards.add(buildLevelUpCard("Attack +2", "기본 공격력이 증가합니다.", () -> {
            steve.applyLevelUpChoice(2);
            dialog.dispose();
            refreshUI();
            if (steve.hasPendingLevelUp()) showLevelUpDialog();
        }));

        cards.add(buildLevelUpCard("Defense +1", "받는 피해를 줄입니다.", () -> {
            steve.applyLevelUpChoice(3);
            dialog.dispose();
            refreshUI();
            if (steve.hasPendingLevelUp()) showLevelUpDialog();
        }));

        root.add(cards, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.setSize(560, 260);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);
        dialog.setVisible(true);
    }
    
    private JButton buildLevelUpCard(String title, String desc, Runnable onClick) {
        JButton button = new JButton("<html><center><b>" + title + "</b><br><br>" + desc + "</center></html>");
        button.setFocusPainted(false);
        button.setFont(new Font("Dialog", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(58, 54, 78));
        button.setBorder(BorderFactory.createLineBorder(GOLD, 2));
        button.addActionListener(e -> onClick.run());
        return button;
    }

    private int getMobDefense() {
        return readIntMethod(mob, 0, "getDefensePower", "getDefense", "getArmor");
    }

    private int getSteveMaxExp() {
        return readIntMethod(steve, 100, "getMaxExp", "getRequiredExp", "getExpToNextLevel");
    }

    private boolean isCardAvailable(Card card) {
        return getCurrentCooldown(card) == 0;
    }

    private int getCardCooldown(Card card) {
        if (card == null) return 0;
        if (card.action == CardAction.AOE_SLASH && steve.getWeapon() instanceof Sword) {
            Sword sword = (Sword) steve.getWeapon();
            if (sword.getAoeSlash() == null) return 0;
            int cooldown = readIntField(sword.getAoeSlash(), 0, "cooldown");
            return cooldown <= 0 ? 0 : Math.max(1, cooldown - 1);
        }
        if (card.action == CardAction.ACTIVE_SKILL && card.activeSkill != null) {
            int cooldown = readIntField(card.activeSkill, 0, "cooldown");
            return cooldown <= 0 ? 0 : Math.max(1, cooldown - 1);
        }
        return 0;
    }

    private int getCurrentCooldown(Card card) {
        if (card == null) return 0;
        if (card.action == CardAction.AOE_SLASH && steve.getWeapon() instanceof Sword) {
            Sword sword = (Sword) steve.getWeapon();
            if (sword.getAoeSlash() == null) return 0;
            return Math.max(0, readIntField(sword.getAoeSlash(), 0, "currentCooldown"));
        }
        if (card.action == CardAction.ACTIVE_SKILL && card.activeSkill != null) {
            return Math.max(0, readIntField(card.activeSkill, 0, "currentCooldown"));
        }
        return 0;
    }

    private int readIntMethod(Object target, int fallback, String... methodNames) {
        for (String methodName : methodNames) {
            try {
                Method m = target.getClass().getMethod(methodName);
                Object value = m.invoke(target);
                if (value instanceof Number) return ((Number) value).intValue();
            } catch (Exception ignored) {
            }
        }
        return fallback;
    }

    private int readIntField(Object target, int fallback, String fieldName) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                java.lang.reflect.Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(target);
                if (value instanceof Number) return ((Number) value).intValue();
            } catch (Exception ignored) {
            }
            type = type.getSuperclass();
        }
        return fallback;
    }

    private void writeIntField(Object target, String fieldName, int value) {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                java.lang.reflect.Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.setInt(target, value);
                return;
            } catch (Exception ignored) {
            }
            type = type.getSuperclass();
        }
    }

    private void setGlassPaneBlocking(boolean blocking) {
        inputLocked = blocking;
    }

    private void showMessage(String msg) {
        messageLabel.setText(msg);
    }

    private static void drawCentered(Graphics2D g2, String s, int x, int y, int w) {
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(s, x + (w - fm.stringWidth(s)) / 2, y);
    }

    private static void drawWrappedCentered(Graphics2D g2, String s, int x, int y, int w, int lineHeight) {
        FontMetrics fm = g2.getFontMetrics();
        String[] words = s.split(" ");
        String line = "";
        int lineY = y;
        for (String word : words) {
            String next = line.isEmpty() ? word : line + " " + word;
            if (fm.stringWidth(next) > w && !line.isEmpty()) {
                drawCentered(g2, line, x, lineY, w);
                line = word;
                lineY += lineHeight;
            } else {
                line = next;
            }
        }
        if (!line.isEmpty()) drawCentered(g2, line, x, lineY, w);
    }
}
