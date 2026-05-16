package view;

import entity.Steve;
import entity.mob.Mob;
import manager.BattleManager;
import manager.WaveManager;

import javax.swing.*;
import java.awt.*;

public class BattleView extends JFrame {

    private Steve steve;
    private Mob mob;
    private int wave;
    private BattleManager battleManager;
    private WaveManager waveManager;

    // UI 컴포넌트
    private JLabel mobNameLabel;
    private JLabel mobHpLabel;
    private JProgressBar mobHpBar;

    private JLabel steveHpLabel;
    private JProgressBar steveHpBar;
    private JLabel steveCoinLabel;

    private JTextArea logArea;   // 전투 로그

    private MinecraftButton btnAttack;
    private MinecraftButton btnBlock;
    private MinecraftButton btnSkill;

    public BattleView(Steve steve, WaveManager waveManager, Mob mob, int wave) {
        this.steve = steve;
        this.mob = mob;
        this.wave = wave;
        this.waveManager = new WaveManager();
        this.battleManager = new BattleManager(steve);

        setTitle("Minecraft RPG - Battle");
        setSize(854, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new BackgroundPanel();
        root.setLayout(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));
        setContentPane(root);

        root.add(buildTopPanel(), BorderLayout.NORTH);
        root.add(buildLogPanel(), BorderLayout.CENTER);
        root.add(buildActionPanel(), BorderLayout.SOUTH);

        refreshUI();
        setVisible(true);
    }

    // ── 상단: 몹 HP / 스티브 HP ──────────────────────────
    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 40, 0));
        panel.setOpaque(false);

        // 왼쪽: 몹 정보
        JPanel mobPanel = new JPanel(new GridLayout(3, 1, 0, 4));
        mobPanel.setOpaque(false);

        mobNameLabel = new JLabel(mob.getName(), SwingConstants.LEFT);
        mobNameLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        mobNameLabel.setForeground(new Color(255, 80, 80));

        mobHpBar = new JProgressBar(0, mob.getMaxHealth());
        mobHpBar.setValue(mob.getHealth());
        mobHpBar.setForeground(new Color(200, 40, 40));
        mobHpBar.setBackground(new Color(60, 60, 60));

        mobHpLabel = new JLabel("HP: " + mob.getHealth() + " / " + mob.getMaxHealth());
        mobHpLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        mobHpLabel.setForeground(Color.WHITE);

        mobPanel.add(mobNameLabel);
        mobPanel.add(mobHpBar);
        mobPanel.add(mobHpLabel);

        // 오른쪽: 스티브 정보
        JPanel stevePanel = new JPanel(new GridLayout(4, 1, 0, 4));
        stevePanel.setOpaque(false);

        JLabel steveNameLabel = new JLabel(steve.getName(), SwingConstants.RIGHT);
        steveNameLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        steveNameLabel.setForeground(new Color(100, 220, 100));

        steveHpBar = new JProgressBar(0, steve.getMaxHealth());
        steveHpBar.setValue(steve.getHealth());
        steveHpBar.setForeground(new Color(40, 180, 40));
        steveHpBar.setBackground(new Color(60, 60, 60));

        steveHpLabel = new JLabel("HP: " + steve.getHealth() + " / " + steve.getMaxHealth(), SwingConstants.RIGHT);
        steveHpLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        steveHpLabel.setForeground(Color.WHITE);

        steveCoinLabel = new JLabel("💰 " + steve.getCoin(), SwingConstants.RIGHT);
        steveCoinLabel.setFont(new Font("Dialog", Font.PLAIN, 13));
        steveCoinLabel.setForeground(new Color(255, 215, 0));

        stevePanel.add(steveNameLabel);
        stevePanel.add(steveHpBar);
        stevePanel.add(steveHpLabel);
        stevePanel.add(steveCoinLabel);

        panel.add(mobPanel);
        panel.add(stevePanel);
        return panel;
    }

    // ── 중앙: 전투 로그 ──────────────────────────────────
    private JScrollPane buildLogPanel() {
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Dialog", Font.PLAIN, 13));
        logArea.setBackground(new Color(30, 30, 30, 200));
        logArea.setForeground(Color.WHITE);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        log("⚔ " + mob.getName() + "와(과)의 전투가 시작됐다!");

        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        return scroll;
    }

    // ── 하단: 액션 버튼 ──────────────────────────────────
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 0));
        panel.setOpaque(false);

        btnAttack = new MinecraftButton("⚔ 공격");
        btnBlock  = new MinecraftButton("🛡 막기");
        btnSkill  = new MinecraftButton("✨ 스킬");

        Font btnFont = new Font("Dialog", Font.BOLD, 16);
        btnAttack.setFont(btnFont);
        btnBlock.setFont(btnFont);
        btnSkill.setFont(btnFont);

        btnAttack.setPreferredSize(new Dimension(0, 48));
        btnBlock.setPreferredSize(new Dimension(0, 48));
        btnSkill.setPreferredSize(new Dimension(0, 48));

        btnAttack.addActionListener(e -> handlePlayerAction(1));
        btnBlock.addActionListener(e  -> handlePlayerAction(2));
        btnSkill.addActionListener(e  -> handlePlayerAction(3));

        panel.add(btnAttack);
        panel.add(btnBlock);
        panel.add(btnSkill);
        return panel;
    }

    // ── 플레이어 액션 처리 ────────────────────────────────
    private void handlePlayerAction(int action) {
        setButtonsEnabled(false);  // 처리 중 버튼 잠금

        switch (action) {
            case 1 -> {
                int dmg = steve.getTotalAttackPower();
                mob.takeDamage(dmg);
                log("▶ " + steve.getName() + "이 공격! → " + mob.getName() + "에게 " + dmg + " 데미지");
            }
            case 2 -> {
                steve.block();
                log("▶ " + steve.getName() + "이 막기 자세! 이번 턴 피해 무효");
            }
            case 3 -> {
                log("▶ 스킬 준비 중... (미구현)");
            }
        }

        refreshUI();

        // 몹 사망 체크
        if (!mob.isAlive()) {
            handleMobDead();
            return;
        }

        // 몹 턴
        mobTurn(action == 2); // 막기 여부 전달
    }

    // ── 몹 턴 ────────────────────────────────────────────
    private void mobTurn(boolean playerBlocked) {
        // 스턴 체크
        if (mob.isStunned()) {
            log("▷ " + mob.getName() + "은(는) 스턴 상태! 행동 불가");
            mob.setStunned(false);
            refreshUI();
            setButtonsEnabled(true);
            return;
        }

        if (playerBlocked) {
            log("▷ " + mob.getName() + "이 공격했지만 막혔다!");
        } else {
            int dmg = mob.getAttackPower();
            steve.takeDamage(dmg);
            log("▷ " + mob.getName() + "이 공격! → " + steve.getName() + "에게 " + dmg + " 데미지");
        }

        steve.onTurnEnd();
        refreshUI();

        // 스티브 사망 체크
        if (!steve.isAlive()) {
            handleSteveDead();
            return;
        }

        setButtonsEnabled(true);
    }

    // ── 몹 처치 ──────────────────────────────────────────
    private void handleMobDead() {
        int coin = mob.getDropCoin();
        int exp  = mob.getDropExp();
        steve.gainCoin(coin);
        steve.gainExp(exp);

        log("✅ " + mob.getName() + " 처치! | 코인 +" + coin + " / EXP +" + exp);
        refreshUI();

        // 잠깐 기다렸다가 VictoryView로
        Timer t = new Timer(1200, e -> {
            dispose();
            new VictoryView(steve, waveManager, wave);
        });
        t.setRepeats(false);
        t.start();
    }

    // ── 스티브 사망 ──────────────────────────────────────
    private void handleSteveDead() {
        log("💀 " + steve.getName() + " 사망...");
        refreshUI();

        Timer t = new Timer(1200, e -> {
            dispose();
            new DeadView(steve, waveManager);
        });
        t.setRepeats(false);
        t.start();
    }

    // ── UI 갱신 ──────────────────────────────────────────
    private void refreshUI() {
        mobHpBar.setValue(mob.getHealth());
        mobHpLabel.setText("HP: " + mob.getHealth() + " / " + mob.getMaxHealth());

        steveHpBar.setValue(steve.getHealth());
        steveHpLabel.setText("HP: " + steve.getHealth() + " / " + steve.getMaxHealth());
        steveCoinLabel.setText("💰 " + steve.getCoin());
    }

    // ── 로그 출력 ────────────────────────────────────────
    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength()); // 스크롤 맨 아래
    }

    // ── 버튼 활성/비활성 ─────────────────────────────────
    private void setButtonsEnabled(boolean enabled) {
        btnAttack.setEnabled(enabled);
        btnBlock.setEnabled(enabled);
        btnSkill.setEnabled(enabled);
    }
}