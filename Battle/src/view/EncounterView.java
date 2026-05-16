package view;

import entity.Steve;
import entity.mob.Mob;
import manager.WaveManager;

import javax.swing.*;
import java.awt.*;

public class EncounterView extends JFrame {

    private Steve steve;
    private Mob mob;
    private int wave;

    public EncounterView(Steve steve, WaveManager waveManager, Mob mob, int wave) {
        this.steve = steve;
        this.wave = wave;
        this.mob = mob;

        setTitle("Minecraft RPG");
        setSize(854, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new BackgroundPanel();
        panel.setLayout(new GridBagLayout());
        setContentPane(panel);

        // 웨이브 라벨
        JLabel waveLabel = new JLabel(wave == 6 ? "⚠ BOSS WAVE ⚠" : "Wave " + wave, SwingConstants.CENTER);
        waveLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        waveLabel.setForeground(wave == 6 ? new Color(255, 80, 80) : new Color(255, 215, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 16, 0);
        panel.add(waveLabel, gbc);

        // 등장 메시지
        String msg = "[ " + mob.getName() + " ] 이(가) 나타났다!";
        JLabel msgLabel = new JLabel(msg, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Dialog", Font.BOLD, 28));
        msgLabel.setForeground(Color.WHITE);
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        panel.add(msgLabel, gbc);

        // 전투 시작 버튼
        MinecraftButton btnFight = new MinecraftButton("► 전투 시작");
        btnFight.setPreferredSize(new Dimension(250, 44));
        btnFight.setFont(new Font("Dialog", Font.BOLD, 18));
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(btnFight, gbc);

        btnFight.addActionListener(e -> {
            dispose();
            new BattleView(steve, waveManager, mob, wave); // 다음 화면
        });

        setVisible(true);
    }
}