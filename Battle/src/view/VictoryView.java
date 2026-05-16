package view;

import entity.Steve;
import entity.mob.Mob;
import manager.WaveManager;

import javax.swing.*;
import java.awt.*;

public class VictoryView extends JPanel {
	
	private GameFrame gameFrame;

	public VictoryView(GameFrame gameFrame, Steve steve, WaveManager waveManager, int wave) {
	    this.gameFrame = gameFrame;
	    

        JPanel panel = new BackgroundPanel();
        panel.setLayout(new GridBagLayout());
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        boolean isFinalWave = waveManager.isLastWave();

        // ─── 클리어 타이틀 ───
        JLabel titleLabel = new JLabel(
            isFinalWave ? "★ ALL WAVES CLEAR! ★" : "Wave " + wave + " Clear!",
            SwingConstants.CENTER
        );
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 36));
        titleLabel.setForeground(isFinalWave ? new Color(255, 100, 100) : new Color(255, 215, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(titleLabel, gbc);

        // ─── 현재 상태 표시 ───
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        statsPanel.setOpaque(false);

        JLabel hpLabel = new JLabel("❤ HP : " + steve.getHealth() + " / " + steve.getMaxHealth(), SwingConstants.CENTER);
        hpLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        hpLabel.setForeground(new Color(100, 220, 100));

        JLabel coinLabel = new JLabel("💰 Coin : " + steve.getCoin(), SwingConstants.CENTER);
        coinLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        coinLabel.setForeground(new Color(255, 215, 0));

        statsPanel.add(hpLabel);
        statsPanel.add(coinLabel);

        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 32, 0);
        panel.add(statsPanel, gbc);

        // ─── 버튼 ───
        if (isFinalWave) {
            MinecraftButton btnTitle = new MinecraftButton("Title");
            btnTitle.setPreferredSize(new Dimension(250, 44));
            btnTitle.setFont(new Font("Dialog", Font.BOLD, 18));
            btnTitle.addActionListener(e -> {
            	gameFrame.showStart();
            });
            gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(btnTitle, gbc);

        } else {
            JPanel btnRow = new JPanel(new GridLayout(1, 2, 10, 0));
            btnRow.setOpaque(false);
            btnRow.setPreferredSize(new Dimension(400, 44));

            MinecraftButton btnNext = new MinecraftButton("Next Wave ►");
            btnNext.setFont(new Font("Dialog", Font.BOLD, 16));
            btnNext.addActionListener(e -> {
                waveManager.nextWave();
                gameFrame.showShop(steve, waveManager, wave + 1);
            });

            MinecraftButton btnTitle = new MinecraftButton("Title");
            btnTitle.setFont(new Font("Dialog", Font.BOLD, 16));
            btnTitle.addActionListener(e -> {
                gameFrame.showStart();
            });

            btnRow.add(btnNext);
            btnRow.add(btnTitle);

            gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 2;
            panel.add(btnRow, gbc);
        }

        setVisible(true);
    }
}