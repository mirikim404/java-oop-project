package view;

import javax.swing.*;

import entity.Steve;
import entity.mob.Mob;
import manager.WaveManager;

import java.awt.*;
import java.io.File;
import java.awt.GraphicsEnvironment;

public class StartView extends JPanel {

	private JPanel contentPane;
	private MinecraftButton btnNewGame;
	private MinecraftButton btnHowTo;
	private MinecraftButton btnCredits;
	private MinecraftButton btnExit;
	private GameFrame gameFrame;

	private Font mcFont;

	public StartView(GameFrame gameFrame) {
	    this.gameFrame = gameFrame;

	    try {
	        mcFont = Font.createFont(Font.TRUETYPE_FONT, new File("fonts/Minecraftia-Regular.ttf")).deriveFont(14f);
	        GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(mcFont);
	    } catch (Exception e) {
	        e.printStackTrace();
	        mcFont = new Font("Dialog", Font.BOLD, 14);
	    }

	    contentPane = new BackgroundPanel();
	    contentPane.setLayout(new GridBagLayout());

	    setLayout(new BorderLayout());
	    add(contentPane, BorderLayout.CENTER);

	    initComponents();
	}

	private void initComponents() {

		JLabel title = new JLabel("MINECRAFT", SwingConstants.CENTER);
		title.setFont(new Font("Dialog", Font.BOLD, 48));
		title.setForeground(Color.WHITE);
		GridBagConstraints gbcTitle = new GridBagConstraints();
		gbcTitle.gridx = 0;
		gbcTitle.gridy = 0;
		gbcTitle.fill = GridBagConstraints.HORIZONTAL;
		gbcTitle.insets = new Insets(0, 0, 4, 0);
		contentPane.add(title, gbcTitle);

		JLabel subTitle = new JLabel("Java RPG Edition", SwingConstants.CENTER);
		subTitle.setFont(new Font("Dialog", Font.BOLD, 16));
		subTitle.setForeground(new Color(255, 215, 0));
		GridBagConstraints gbcSub = new GridBagConstraints();
		gbcSub.gridx = 0;
		gbcSub.gridy = 1;
		gbcSub.fill = GridBagConstraints.HORIZONTAL;
		gbcSub.insets = new Insets(0, 0, 32, 0);
		contentPane.add(subTitle, gbcSub);

		btnNewGame = new MinecraftButton("New game");
		btnNewGame.setPreferredSize(new Dimension(310, 40));
		GridBagConstraints gbcBtn1 = new GridBagConstraints();
		gbcBtn1.gridx = 0;
		gbcBtn1.gridy = 2;
		gbcBtn1.fill = GridBagConstraints.HORIZONTAL;
		gbcBtn1.insets = new Insets(4, 0, 4, 0);
		contentPane.add(btnNewGame, gbcBtn1);

		btnHowTo = new MinecraftButton("How to");
		btnHowTo.setPreferredSize(new Dimension(310, 40));
		GridBagConstraints gbcBtn2 = new GridBagConstraints();
		gbcBtn2.gridx = 0;
		gbcBtn2.gridy = 3;
		gbcBtn2.fill = GridBagConstraints.HORIZONTAL;
		gbcBtn2.insets = new Insets(4, 0, 4, 0);
		contentPane.add(btnHowTo, gbcBtn2);

		JPanel bottomRow = new JPanel(new GridLayout(1, 2, 6, 0));
		bottomRow.setOpaque(false);
		btnCredits = new MinecraftButton("Credits");
		btnExit = new MinecraftButton("Exit");
		bottomRow.add(btnCredits);
		bottomRow.add(btnExit);
		bottomRow.setPreferredSize(new Dimension(310, 40));
		GridBagConstraints gbcBottom = new GridBagConstraints();
		gbcBottom.gridx = 0;
		gbcBottom.gridy = 4;
		gbcBottom.fill = GridBagConstraints.HORIZONTAL;
		gbcBottom.insets = new Insets(4, 0, 4, 0);
		contentPane.add(bottomRow, gbcBottom);

		// 폰트
		title.setFont(mcFont.deriveFont(36f));
		subTitle.setFont(mcFont.deriveFont(12f));
		btnNewGame.setFont(mcFont.deriveFont(16f));
		btnHowTo.setFont(mcFont.deriveFont(16f));
		btnCredits.setFont(mcFont.deriveFont(14f));
		btnExit.setFont(mcFont.deriveFont(14f));

		// 액션
		btnNewGame.addActionListener(e -> {
		    String name = JOptionPane.showInputDialog(this, "닉네임을 입력하세요", "새 게임", JOptionPane.PLAIN_MESSAGE);
		    if (name != null && !name.trim().isEmpty()) {
		        gameFrame.startNewGame(name.trim());
		    }
		});
		btnHowTo.addActionListener(e -> JOptionPane.showMessageDialog(this,
				"[ 전투 ]\n공격 — 기본 공격\n막기 — 이번 턴 피해 무효\n스킬 — 쿨타임/수량 소모\n\n[ 코인 ]\n몹 처치/웨이브 클리어 시 획득\n사망해도 유지됨", "게임 방법",
				JOptionPane.PLAIN_MESSAGE));
		btnCredits.addActionListener(
				e -> JOptionPane.showMessageDialog(this, "제작진 내용", "제작진", JOptionPane.PLAIN_MESSAGE));
		btnExit.addActionListener(e -> System.exit(0));
	}
}