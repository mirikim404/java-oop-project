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

public class ShopView extends JFrame {

    private Steve steve;
    private WaveManager waveManager;
    private ShopManager shopManager;
    private int wave;

    private JLabel coinLabel;

    public ShopView(Steve steve, WaveManager waveManager, int wave) {
        this.steve = steve;
        this.waveManager = waveManager;
        this.shopManager = new ShopManager(steve);
        this.wave = wave;

        setTitle("Minecraft RPG - Shop");
        setSize(854, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        setContentPane(root);

        root.add(buildTopPanel(), BorderLayout.NORTH);
        root.add(buildShopPanel(), BorderLayout.CENTER);
        root.add(buildBottomPanel(), BorderLayout.SOUTH);

        setVisible(true);
    }

    // ─── 상단: 타이틀 + 코인 ───
    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("🛒 SHOP", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 215, 0));

        coinLabel = new JLabel("💰 " + steve.getCoin(), SwingConstants.RIGHT);
        coinLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        coinLabel.setForeground(new Color(255, 215, 0));

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
            {"StoneSword",     "돌 검",        "30"},
            {"IronSword",      "철 검",        "60"},
            {"DiamondSword",   "다이아몬드 검", "100"},
            {"NetheriteSword", "네더라이트 검", "150"},
        };

        for (String[] w : weapons) {
            JPanel row = buildItemRow(
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

        panel.add(buildItemRow("눈덩이 (단일 스턴, 쿨타임 3턴)", "40 코인", () -> {
            boolean ok = shopManager.buySkill(new SnowBall());
            if (ok) refreshCoin();
        }));

        panel.add(buildItemRow("화염구 (화상 2턴, 쿨타임 3턴)", "60 코인", () -> {
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

        panel.add(buildItemRow("공격 포션 (1턴 공격력 2배)", "20 코인", () -> {
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
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(new Color(50, 50, 50));
        row.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Dialog", Font.PLAIN, 15));
        nameLabel.setForeground(Color.WHITE);

        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        priceLabel.setForeground(new Color(255, 215, 0));

        MinecraftButton btnBuy = new MinecraftButton("구매");
        btnBuy.setPreferredSize(new Dimension(80, 32));
        btnBuy.setFont(new Font("Dialog", Font.BOLD, 13));
        btnBuy.addActionListener(e -> onBuy.run());

        JPanel rightPanel = new JPanel(new BorderLayout(8, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(priceLabel, BorderLayout.WEST);
        rightPanel.add(btnBuy, BorderLayout.EAST);

        row.add(nameLabel, BorderLayout.CENTER);
        row.add(rightPanel, BorderLayout.EAST);

        return row;
    }

    // ─── 하단: 나가기 버튼 ───
    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setOpaque(false);

        MinecraftButton btnNext = new MinecraftButton("다음 웨이브 ►");
        btnNext.setPreferredSize(new Dimension(200, 44));
        btnNext.setFont(new Font("Dialog", Font.BOLD, 16));
        btnNext.addActionListener(e -> {
            dispose();
            Mob nextMob = waveManager.getAliveMobs().get(0);
            new EncounterView(steve, waveManager, nextMob, wave);
        });

        panel.add(btnNext);
        return panel;
    }

    // ─── 코인 갱신 ───
    private void refreshCoin() {
        coinLabel.setText("💰 " + steve.getCoin());
    }
}