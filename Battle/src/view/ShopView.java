package view;

import entity.Steve;
import entity.mob.Mob;
import manager.ShopManager;
import manager.WaveManager;
import skill.active.FireCharge;
import skill.active.SnowBall;
import skill.consumable.AttackPotion;
import skill.consumable.HealPotion;
import weapon.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ShopView extends JPanel {

    private GameFrame gameFrame;
    private Steve steve;
    private WaveManager waveManager;
    private ShopManager shopManager;
    private int wave;

    private JLabel coinLabel;

    // 마인크래프트 색상 팔레트
    private static final Color MC_BG         = new Color(198, 198, 198);   // 인벤토리 밝은 회색
    private static final Color MC_PANEL      = new Color(139, 139, 139);   // 패널 중간 회색
    private static final Color MC_DARK       = new Color(55,  55,  55);    // 어두운 테두리
    private static final Color MC_SLOT_BG   = new Color(139, 139, 139);   // 슬롯 배경
    private static final Color MC_SLOT_DARK = new Color(55,  55,  55);    // 슬롯 테두리 어두운
    private static final Color MC_SLOT_LITE = new Color(255, 255, 255);   // 슬롯 테두리 밝은
    private static final Color MC_TITLE_BG  = new Color(16,  16,  16);    // 제목 배경
    private static final Color MC_TEXT      = new Color(255, 255, 255);   // 흰 텍스트
    private static final Color MC_TEXT_DARK = new Color(63,  63,  63);    // 어두운 텍스트
    private static final Color MC_GOLD      = new Color(255, 215, 0);
    private static final Color MC_BTN_BASE  = new Color(160, 160, 160);
    private static final Color MC_BTN_LITE  = new Color(220, 220, 220);
    private static final Color MC_BTN_DARK  = new Color(80,  80,  80);

    // 선택된 탭
    private int selectedTab = 0; // 0=무기, 1=스킬, 2=포션
    private static final String[] TAB_NAMES = {"⚔ 무기", "✨ 스킬", "🧪 포션"};

    // 선택된 슬롯
    private int hoveredSlot = -1;
    private int selectedSlot = -1;

    // 아이템 정의
    private static class ShopItem {
        String imageName, name, desc;
        int price;
        Runnable onBuy;
        ShopItem(String imageName, String name, String desc, int price, Runnable onBuy) {
            this.imageName = imageName;
            this.name = name;
            this.desc = desc;
            this.price = price;
            this.onBuy = onBuy;
        }
    }

    private List<ShopItem>[] tabItems;
    private List<Rectangle> slotBounds = new ArrayList<>();

    // 메시지 (구매 결과)
    private String message = "";
    private String lastBoughtImageName = "";
    private javax.swing.Timer msgTimer;

    @SuppressWarnings("unchecked")
    public ShopView(GameFrame gameFrame, Steve steve, WaveManager waveManager, int wave) {
        this.gameFrame = gameFrame;
        this.steve = steve;
        this.waveManager = waveManager;
        this.shopManager = new ShopManager(steve);
        this.wave = wave;

        tabItems = new List[3];
        buildTabItems();

        setLayout(new BorderLayout());
        setOpaque(false);

        // 배경
        JPanel bg = new BackgroundPanel("resources/bg/shop_bg.png");
        bg.setLayout(new GridBagLayout());
        add(bg, BorderLayout.CENTER);

        // 중앙 인벤토리 패널
        MCInventoryPanel invPanel = new MCInventoryPanel();
        bg.add(invPanel);

        msgTimer = new javax.swing.Timer(2500, e -> { message = ""; repaint(); });
        msgTimer.setRepeats(false);
    }

    @SuppressWarnings("unchecked")
    private void buildTabItems() {
        // 무기
        tabItems[0] = new ArrayList<>();
        tabItems[0].add(new ShopItem("StoneSword",     "돌 검",          "ATK +5",   40,  () -> tryBuy(() -> shopManager.buyWeapon(new StoneSword()))));
        tabItems[0].add(new ShopItem("IronSword",      "철 검",          "ATK +9",   75,  () -> tryBuy(() -> shopManager.buyWeapon(new IronSword()))));
        tabItems[0].add(new ShopItem("DiamondSword",   "다이아몬드 검",  "ATK +14",  115, () -> tryBuy(() -> shopManager.buyWeapon(new DiamondSword()))));
        tabItems[0].add(new ShopItem("NetheriteSword", "네더라이트 검",  "ATK +18",  160, () -> tryBuy(() -> shopManager.buyWeapon(new NetheriteSword()))));

        // 스킬
        tabItems[1] = new ArrayList<>();
        tabItems[1].add(new ShopItem("SnowBall",    "눈덩이",  "스턴 (쿨타임 3턴)", 45, () -> tryBuy(() -> shopManager.buySkill(new SnowBall()))));
        tabItems[1].add(new ShopItem("FireCharge",  "화염구",  "화상 2턴 (쿨타임 3턴)", 65, () -> tryBuy(() -> shopManager.buySkill(new FireCharge()))));

        // 포션
        tabItems[2] = new ArrayList<>();
        tabItems[2].add(new ShopItem("AttackPotion", "공격 포션", "다음 공격 2배",  18, () -> tryBuy(() -> shopManager.buyPotion(new AttackPotion()))));
        tabItems[2].add(new ShopItem("HealPotion",   "회복 포션", "체력 회복",       15, () -> tryBuy(() -> shopManager.buyPotion(new HealPotion()))));
    }

    private void tryBuy(java.util.function.BooleanSupplier action) {
        boolean ok = action.getAsBoolean();
        if (ok) {
            if (selectedSlot >= 0 && selectedSlot < tabItems[selectedTab].size()) {
                lastBoughtImageName = tabItems[selectedTab].get(selectedSlot).imageName;
            }
            showMsg("§a구매 완료!");
        } else {
            showMsg("§c코인이 부족합니다.");
        }
        repaint();
    }

    private void showMsg(String msg) {
        this.message = msg;
        msgTimer.restart();
        repaint();
    }

    // ─── 배경 패널 ───
    private static class BackgroundPanel extends JPanel {
        private Image bgImage;
        BackgroundPanel(String path) {
            setOpaque(true);
            try {
                ImageIcon ic = new ImageIcon(path);
                if (ic.getIconWidth() > 0) bgImage = ic.getImage();
            } catch (Exception ignored) {}
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            if (bgImage != null) {
                g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2.setColor(new Color(30, 30, 40));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.setColor(new Color(0, 0, 0, 100));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    // ─── 메인 인벤토리 패널 ───
    private class MCInventoryPanel extends JPanel {
        private static final int SLOT_SIZE = 54;
        private static final int SLOT_GAP  = 4;
        private static final int COLS      = 6;
        private static final int W         = 700;
        private static final int H         = 500;

        // 탭 버튼 영역
        private List<Rectangle> tabBounds = new ArrayList<>();
        // 구매 버튼
        private Rectangle buyBtnBounds;
        // 다음 웨이브 버튼
        private Rectangle nextBtnBounds;

        MCInventoryPanel() {
            setPreferredSize(new Dimension(W, H));
            setOpaque(false);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    // 탭 클릭
                    for (int i = 0; i < tabBounds.size(); i++) {
                        if (tabBounds.get(i).contains(e.getPoint())) {
                            selectedTab = i;
                            selectedSlot = -1;
                            hoveredSlot = -1;
                            repaint();
                            return;
                        }
                    }
                    // 슬롯 클릭
                    for (int i = 0; i < slotBounds.size(); i++) {
                        if (slotBounds.get(i).contains(e.getPoint())) {
                            selectedSlot = (selectedSlot == i) ? -1 : i;
                            repaint();
                            return;
                        }
                    }
                    // 구매 버튼
                    if (buyBtnBounds != null && buyBtnBounds.contains(e.getPoint())) {
                        if (selectedSlot >= 0 && selectedSlot < tabItems[selectedTab].size()) {
                            tabItems[selectedTab].get(selectedSlot).onBuy.run();
                        }
                        return;
                    }
                    // 다음 웨이브 버튼
                    if (nextBtnBounds != null && nextBtnBounds.contains(e.getPoint())) {
                        Mob nextMob = waveManager.getAliveMobs().get(0);
                        gameFrame.showEncounter(steve, waveManager, nextMob, wave);
                        return;
                    }
                }
                @Override public void mouseExited(MouseEvent e) {
                    hoveredSlot = -1;
                    repaint();
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    int prev = hoveredSlot;
                    hoveredSlot = -1;
                    for (int i = 0; i < slotBounds.size(); i++) {
                        if (slotBounds.get(i).contains(e.getPoint())) {
                            hoveredSlot = i;
                            break;
                        }
                    }
                    if (hoveredSlot != prev) repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            tabBounds.clear();
            slotBounds.clear();

            int x = 0, y = 0;

            // ── 탭 버튼 ──
            int tabW = 126, tabH = 28;
            for (int i = 0; i < TAB_NAMES.length; i++) {
                int tx = x + i * (tabW + 2);
                int ty = y;
                Rectangle tb = new Rectangle(tx, ty, tabW, tabH);
                tabBounds.add(tb);
                boolean active = (selectedTab == i);
                drawMCTab(g2, tx, ty, tabW, tabH, TAB_NAMES[i], active);
            }
            y += tabH;

            // ── 메인 패널 테두리 ──
            int panelW = W;
            int panelH = H - tabH;
            drawMCPanel(g2, x, y, panelW, panelH);

            int px = x + 8, py = y + 8;

            // ── 상단 타이틀 바 ──
            g2.setColor(MC_TITLE_BG);
            g2.fillRect(px, py, panelW - 16, 22);
            g2.setFont(mcFont(13));
            g2.setColor(new Color(63, 63, 63));
            g2.drawString("SHOP", px + 7, py + 15);
            g2.setColor(MC_TEXT);
            g2.drawString("SHOP", px + 6, py + 14);

            // 코인 표시 (우측)
            String coinStr = String.valueOf(steve.getCoin());
            FontMetrics fm = g2.getFontMetrics(mcFont(13));
            int coinIconX = px + panelW - 42;
            int coinX = coinIconX - fm.stringWidth(coinStr) - 6;
            g2.setColor(new Color(63, 63, 63));
            g2.drawString(coinStr, coinX + 1, py + 15);
            g2.setColor(MC_GOLD);
            g2.drawString(coinStr, coinX, py + 14);
            drawMCIcon(g2, "resources/icon/Coin.png", coinIconX, py + 3, 18, 18);

            py += 30;

            // ── 아이템 슬롯 그리드 ──
            List<ShopItem> items = tabItems[selectedTab];
            int cols = COLS;
            int rows = 2;
            int gridX = px + 6;
            int gridY = py;

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int idx = row * cols + col;
                    int sx = gridX + col * (SLOT_SIZE + SLOT_GAP);
                    int sy = gridY + row * (SLOT_SIZE + SLOT_GAP);
                    Rectangle sr = new Rectangle(sx, sy, SLOT_SIZE, SLOT_SIZE);
                    slotBounds.add(sr);

                    boolean hovered  = (hoveredSlot == idx);
                    boolean selected = (selectedSlot == idx);
                    ShopItem item = (idx < items.size()) ? items.get(idx) : null;

                    drawMCSlot(g2, sx, sy, SLOT_SIZE, SLOT_SIZE, item, hovered, selected);
                }
            }

            py += rows * (SLOT_SIZE + SLOT_GAP) + 8;

            // ── 구분선 ──
            g2.setColor(MC_SLOT_DARK);
            g2.drawLine(px, py, px + panelW - 24, py);
            g2.setColor(MC_SLOT_LITE);
            g2.drawLine(px, py + 1, px + panelW - 24, py + 1);
            py += 10;

            // ── 선택된 아이템 정보 ──
            ShopItem sel = (selectedSlot >= 0 && selectedSlot < items.size()) ? items.get(selectedSlot) : null;
            if (sel != null) {
                // 아이콘 크게
                int bigIconX = px + 8;
                int bigIconY = py;
                g2.setColor(MC_SLOT_DARK);
                g2.fillRect(bigIconX - 2, bigIconY - 2, 68, 68);
                g2.setColor(new Color(30, 30, 30));
                g2.fillRect(bigIconX, bigIconY, 64, 64);
                drawMCIcon(g2, "resources/shop/" + sel.imageName + ".png", bigIconX + 4, bigIconY + 4, 56, 56);

                // 이름 + 설명
                int infoX = bigIconX + 76;
                g2.setFont(mcFont(14));
                g2.setColor(new Color(63, 63, 63));
                g2.drawString(sel.name, infoX + 1, py + 17);
                g2.setColor(MC_GOLD);
                g2.drawString(sel.name, infoX, py + 16);

                g2.setFont(mcFont(11));
                g2.setColor(new Color(160, 160, 160));
                g2.drawString(sel.desc, infoX, py + 34);

                // 가격
                g2.setFont(mcFont(12));
                g2.setColor(new Color(63, 63, 63));
                g2.drawString(sel.price + " 코인", infoX + 1, py + 52);
                g2.setColor(MC_GOLD);
                g2.drawString(sel.price + " 코인", infoX, py + 51);

                // 구매 버튼
                int btnX = panelW - 110;
                int btnY = py + 12;
                buyBtnBounds = new Rectangle(btnX, btnY, 96, 30);
                drawMCButton(g2, btnX, btnY, 96, 30, "Buy", false);
            } else {
                buyBtnBounds = null;
                g2.setFont(mcFont(11));
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("아이템을 선택하세요", px + 8, py + 20);
            }

            // ── 메시지 ──
            if (!message.isEmpty()) {
                String displayMsg = message.replace("§a", "").replace("§c", "");
                Color msgColor = message.startsWith("§a") ? new Color(85, 255, 85) : new Color(255, 85, 85);
                g2.setFont(mcFont(12));
                g2.setColor(new Color(0, 0, 0, 180));
                g2.drawString(displayMsg, px + 9, y + panelH - 91);
                g2.setColor(msgColor);
                g2.drawString(displayMsg, px + 8, y + panelH - 92);
            }

            // ── 다음 웨이브 버튼 ──
            drawHotbar(g2, px + 8, y + panelH - 76, panelW - 32, 44);

            int nbW = 180, nbH = 30;
            int nbX = panelW - nbW - 12;
            int nbY = y + panelH - nbH - 12;
            nextBtnBounds = new Rectangle(nbX, nbY, nbW, nbH);
            drawMCButton(g2, nbX, nbY, nbW, nbH, "다음 웨이브 ▶", false);

            g2.dispose();
        }

        // ─── MC 스타일 탭 ───
        private void drawMCTab(Graphics2D g2, int x, int y, int w, int h, String label, boolean active) {
            if (active) {
                g2.setColor(MC_BG);
            } else {
                g2.setColor(MC_PANEL);
            }
            g2.fillRect(x, y, w, h);

            // 테두리 (위/좌 밝게, 아래/우 어둡게)
            g2.setColor(MC_SLOT_LITE);
            g2.drawLine(x, y, x + w - 1, y);
            g2.drawLine(x, y, x, y + h - 1);
            g2.setColor(MC_SLOT_DARK);
            if (!active) {
                g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            }
            g2.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

            g2.setFont(mcFont(11));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(label)) / 2;
            int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2 - 1;
            g2.setColor(new Color(63, 63, 63));
            g2.drawString(label, tx + 1, ty + 1);
            g2.setColor(active ? MC_TEXT : new Color(200, 200, 200));
            g2.drawString(label, tx, ty);
        }

        // ─── MC 스타일 패널 ───
        private void drawMCPanel(Graphics2D g2, int x, int y, int w, int h) {
            g2.setColor(MC_BG);
            g2.fillRect(x, y, w, h);
            // 외곽 테두리 (MC 특유의 3D 효과)
            g2.setColor(MC_SLOT_LITE);
            g2.drawLine(x, y, x + w - 1, y);
            g2.drawLine(x, y, x, y + h - 1);
            g2.setColor(MC_SLOT_DARK);
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            // 내부 라인
            g2.setColor(new Color(100, 100, 100));
            g2.drawLine(x + 1, y + 1, x + w - 2, y + 1);
            g2.drawLine(x + 1, y + 1, x + 1, y + h - 2);
            g2.setColor(new Color(220, 220, 220));
            g2.drawLine(x + 1, y + h - 2, x + w - 2, y + h - 2);
            g2.drawLine(x + w - 2, y + 1, x + w - 2, y + h - 2);
        }

        // ─── MC 스타일 슬롯 ───
        private void drawMCSlot(Graphics2D g2, int x, int y, int w, int h,
                                ShopItem item, boolean hovered, boolean selected) {
            // 슬롯 배경
            Color bg = selected ? new Color(120, 120, 180) :
                       hovered  ? new Color(160, 160, 160) : MC_SLOT_BG;
            g2.setColor(bg);
            g2.fillRect(x, y, w, h);

            // 슬롯 테두리 (안쪽은 어둡게, 바깥은 밝게 → MC 음각 효과)
            g2.setColor(MC_SLOT_DARK);
            g2.drawLine(x, y, x + w - 1, y);
            g2.drawLine(x, y, x, y + h - 1);
            g2.setColor(MC_SLOT_LITE);
            g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
            g2.drawLine(x + w - 1, y, x + w - 1, y + h - 1);

            if (item == null) return;

            // 아이콘
            drawMCIcon(g2, "resources/shop/" + item.imageName + ".png", x + 5, y + 5, w - 10, h - 20);

            // 가격 (슬롯 하단)
            g2.setFont(mcFont(9));
            FontMetrics fm = g2.getFontMetrics();
            String priceStr = String.valueOf(item.price);
            int tx = x + w - fm.stringWidth(priceStr) - 3;
            g2.setColor(new Color(0, 0, 0, 160));
            g2.drawString(priceStr, tx + 1, y + h - 3);
            g2.setColor(MC_GOLD);
            g2.drawString(priceStr, tx, y + h - 4);
        }

        // ─── MC 스타일 버튼 ───
        private void drawHotbar(Graphics2D g2, int x, int y, int w, int h) {
            g2.setFont(mcFont(10));
            g2.setColor(new Color(63, 63, 63));
            g2.drawString("Inventory", x + 1, y - 3);
            g2.setColor(MC_TEXT);
            g2.drawString("Inventory", x, y - 4);

            int slots = 9;
            int gap = 4;
            int size = Math.min(h, (w - gap * (slots - 1)) / slots);
            List<String[]> entries = buildHotbarEntries();
            for (int i = 0; i < slots; i++) {
                int sx = x + i * (size + gap);
                boolean flash = i < entries.size() && entries.get(i)[0].equals(lastBoughtImageName);
                g2.setColor(flash ? new Color(210, 190, 92) : MC_SLOT_BG);
                g2.fillRect(sx, y, size, size);
                g2.setColor(MC_SLOT_DARK);
                g2.drawLine(sx, y, sx + size - 1, y);
                g2.drawLine(sx, y, sx, y + size - 1);
                g2.setColor(MC_SLOT_LITE);
                g2.drawLine(sx, y + size - 1, sx + size - 1, y + size - 1);
                g2.drawLine(sx + size - 1, y, sx + size - 1, y + size - 1);

                if (i < entries.size()) {
                    String[] entry = entries.get(i);
                    drawMCIcon(g2, "resources/shop/" + entry[0] + ".png", sx + 4, y + 4, size - 8, size - 8);
                    if (!entry[1].isEmpty()) {
                        g2.setFont(mcFont(9));
                        g2.setColor(new Color(0, 0, 0, 180));
                        g2.drawString(entry[1], sx + size - 14, y + size - 3);
                        g2.setColor(Color.WHITE);
                        g2.drawString(entry[1], sx + size - 15, y + size - 4);
                    }
                }
            }
        }

        private List<String[]> buildHotbarEntries() {
            List<String[]> entries = new ArrayList<>();
            if (steve.getWeapon() != null) {
                entries.add(new String[] { steve.getWeapon().getClass().getSimpleName(), "" });
            }
            for (skill.active.ActiveSkill skill : steve.getActiveSkills()) {
                if (skill != null) entries.add(new String[] { skill.getClass().getSimpleName(), "" });
            }
            for (skill.consumable.ConsumableSkill item : steve.getConsumables()) {
                if (item != null && item.getQuantity() > 0) {
                    entries.add(new String[] { item.getClass().getSimpleName(), String.valueOf(item.getQuantity()) });
                }
            }
            return entries;
        }

        private void drawMCButton(Graphics2D g2, int x, int y, int w, int h, String label, boolean pressed) {
            Color base = pressed ? new Color(100, 100, 100) : MC_BTN_BASE;
            g2.setColor(base);
            g2.fillRect(x, y, w, h);

            if (pressed) {
                g2.setColor(MC_BTN_DARK);
                g2.drawLine(x, y, x + w - 1, y);
                g2.drawLine(x, y, x, y + h - 1);
                g2.setColor(MC_BTN_LITE);
                g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                g2.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            } else {
                g2.setColor(MC_BTN_LITE);
                g2.drawLine(x, y, x + w - 1, y);
                g2.drawLine(x, y, x, y + h - 1);
                g2.setColor(MC_BTN_DARK);
                g2.drawLine(x, y + h - 1, x + w - 1, y + h - 1);
                g2.drawLine(x + w - 1, y, x + w - 1, y + h - 1);
            }

            g2.setFont(mcFont(12));
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(label)) / 2;
            int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(new Color(63, 63, 63));
            g2.drawString(label, tx + 1, ty + 1);
            g2.setColor(MC_TEXT);
            g2.drawString(label, tx, ty);
        }

        // ─── 아이콘 렌더 ───
        private void drawMCIcon(Graphics2D g2, String path, int x, int y, int w, int h) {
            try {
                if (path.startsWith("resources/shop/") && path.endsWith(".png")) {
                    String cleanPath = path.substring(0, path.length() - 4) + "_clean.png";
                    if (new java.io.File(cleanPath).exists()) {
                        path = cleanPath;
                    }
                }
                ImageIcon ic = new ImageIcon(path);
                if (ic.getIconWidth() <= 0) return;
                Image img = ic.getImage();
                int iw = ic.getIconWidth(), ih = ic.getIconHeight();
                double scale = Math.min((double) w / iw, (double) h / ih);
                int dw = (int)(iw * scale), dh = (int)(ih * scale);
                int dx = x + (w - dw) / 2, dy = y + (h - dh) / 2;
                Object old = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                g2.drawImage(img, dx, dy, dw, dh, null);
                if (old != null) g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, old);
            } catch (Exception ignored) {}
        }

        private Font mcFont(int size) {
            return new Font("Monospaced", Font.BOLD, size);
        }
    }
}
