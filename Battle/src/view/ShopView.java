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

public class ShopView extends JPanel {
	private GameFrame gameFrame;
    private Steve steve;
    private WaveManager waveManager;
    private ShopManager shopManager;
    private int wave;

    private JLabel coinLabel;

    public ShopView(GameFrame gameFrame, Steve steve, WaveManager waveManager, int wave) {
    	this.gameFrame = gameFrame;
    	this.steve = steve;
        this.waveManager = waveManager;
        this.shopManager = new ShopManager(steve);
        this.wave = wave;


        JPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);

        root.add(buildTopPanel(), BorderLayout.NORTH);
        root.add(buildShopPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

    }

    // ─── 상단: 타이틀 + 코인 ───
    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("SHOP", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 215, 80));

        coinLabel = new JLabel("Coin " + steve.getCoin(), SwingConstants.RIGHT);
        coinLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        coinLabel.setForeground(new Color(255, 230, 120));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(coinLabel, BorderLayout.EAST);
        return panel;
    }

    // ─── 중앙: 상점 탭 ───
    private JTabbedPane buildShopPanel() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Dialog", Font.BOLD, 14));
        tabs.setBackground(new Color(50, 50, 50));
        tabs.setForeground(Color.WHITE);

        tabs.addTab("⚔ 무기", buildWeaponPanel());
        tabs.addTab("✨ 스킬", buildSkillPanel());
        tabs.addTab("🧪 포션", buildPotionPanel());

        return tabs;
    }

    // ─── 무기 패널 ───
    private JPanel buildWeaponPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 0, 8));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        String[][] weapons = {
            {"StoneSword",     "돌 검",        "40"},
            {"IronSword",      "철 검",        "75"},
            {"DiamondSword",   "다이아몬드 검", "115"},
            {"NetheriteSword", "네더라이트 검", "160"},
        };

        for (String[] w : weapons) {
            JPanel row = buildItemRow(
                w[0],
                w[1],
                w[2] + " 코인",
                () -> {
                    Sword sword = switch (w[0]) {
                        case "StoneSword"     -> new StoneSword();
                        case "IronSword"      -> new IronSword();
                        case "DiamondSword"   -> new DiamondSword();
                        case "NetheriteSword" -> new NetheriteSword();
                        default -> null;
                    };
                    if (sword != null) {
                        boolean ok = shopManager.buyWeapon(sword);
                        if (ok) refreshCoin();
                    }
                }
            );
            panel.add(row);
        }
        return panel;
    }

    // ─── 스킬 패널 ───
    private JPanel buildSkillPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        panel.add(buildItemRow("눈덩이 (보조 스턴, 쿨타임 3턴)", "45 코인", () -> {
            boolean ok = shopManager.buySkill(new SnowBall());
            if (ok) refreshCoin();
        }));

        panel.add(buildItemRow("화염구 (화상 2턴, 쿨타임 3턴)", "65 코인", () -> {
            boolean ok = shopManager.buySkill(new FireCharge());
            if (ok) refreshCoin();
        }));

        return panel;
    }

    // ─── 포션 패널 ───
    private JPanel buildPotionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        panel.add(buildItemRow("공격 포션 (다음 공격 2배)", "18 코인", () -> {
            boolean ok = shopManager.buyPotion(new AttackPotion());
            if (ok) refreshCoin();
        }));

        panel.add(buildItemRow("회복 포션 (체력 회복)", "15 코인", () -> {
            boolean ok = shopManager.buyPotion(new HealPotion());
            if (ok) refreshCoin();
        }));

        return panel;
    }

    // ─── 아이템 행 공통 컴포넌트 ───
    private JPanel buildItemRow(String name, String price, Runnable onBuy) {
        String imageName = switch (price.replaceAll("[^0-9]", "")) {
            case "45" -> "SnowBall";
            case "65" -> "FireCharge";
            case "18" -> "AttackPotion";
            case "15" -> "HealPotion";
            default -> "WoodSword";
        };
        return buildItemRow(imageName, name, price, onBuy);
    }

    private JPanel buildItemRow(String imageName, String name, String price, Runnable onBuy) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(24, 24, 34));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(86, 78, 110), 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 12)
        ));

        JLabel iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(56, 56));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(new Color(14, 14, 22));
        iconLabel.setBorder(BorderFactory.createLineBorder(new Color(128, 113, 70), 1));
        ImageIcon icon = loadShopIcon(imageName);
        if (icon != null) {
            iconLabel.setIcon(icon);
        }

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        nameLabel.setForeground(Color.WHITE);

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        priceLabel.setForeground(new Color(255, 215, 80));

        MinecraftButton btnBuy = new MinecraftButton("Buy");
        btnBuy.setPreferredSize(new Dimension(84, 34));
        btnBuy.setFont(new Font("Dialog", Font.BOLD, 13));
        btnBuy.addActionListener(e -> onBuy.run());

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(priceLabel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(btnBuy);

        row.add(iconLabel, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);

        return row;
    }

    private ImageIcon loadShopIcon(String imageName) {
        ImageIcon icon = new ImageIcon("resources/shop/" + imageName + ".png");
        if (icon.getIconWidth() <= 0) return null;

        Image scaled = icon.getImage().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ─── 하단: 나가기 버튼 ───
    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        MinecraftButton btnNext = new MinecraftButton("다음 웨이브 ►");
        btnNext.setPreferredSize(new Dimension(200, 44));
        btnNext.setFont(new Font("Dialog", Font.BOLD, 16));
        btnNext.addActionListener(e -> {
            Mob nextMob = waveManager.getAliveMobs().get(0);
            gameFrame.showEncounter(steve, waveManager, nextMob, wave);
        });

        panel.add(btnNext);
        return panel;
    }

    // ─── 코인 갱신 ───
    private void refreshCoin() {
        coinLabel.setText("Coin " + steve.getCoin());
    }
}
