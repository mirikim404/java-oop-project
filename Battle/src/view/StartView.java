package view;

import javax.swing.*;
import java.awt.*;

public class StartView extends JPanel {

	private JPanel contentPane;
	private MinecraftButton btnNewGame;
	private MinecraftButton btnHowTo;
	private MinecraftButton btnCredits;
	private MinecraftButton btnExit;
	private GameFrame gameFrame;
	
	public StartView(GameFrame gameFrame) {
	    this.gameFrame = gameFrame;
	    contentPane = new BackgroundPanel();
	    contentPane.setLayout(new GridBagLayout());
	    setLayout(new BorderLayout());
	    add(contentPane, BorderLayout.CENTER);
	    initComponents();
	}

	private void initComponents() {

		JLabel title = new JLabel("MINECRAFT", SwingConstants.CENTER);
		title.setForeground(Color.WHITE);
		GridBagConstraints gbcTitle = new GridBagConstraints();
		gbcTitle.gridx = 0;
		gbcTitle.gridy = 0;
		gbcTitle.fill = GridBagConstraints.HORIZONTAL;
		gbcTitle.insets = new Insets(0, 0, 4, 0);
		contentPane.add(title, gbcTitle);

		JLabel subTitle = new JLabel("Java RPG Edition", SwingConstants.CENTER);
		subTitle.setForeground(new Color(255, 215, 0));
		GridBagConstraints gbcSub = new GridBagConstraints();
		gbcSub.gridx = 0;
		gbcSub.gridy = 1;
		gbcSub.fill = GridBagConstraints.HORIZONTAL;
		gbcSub.insets = new Insets(0, 0, 32, 0);
		contentPane.add(subTitle, gbcSub);

		btnNewGame = new MinecraftButton("새 게임");
		btnNewGame.setPreferredSize(new Dimension(310, 40));
		GridBagConstraints gbcBtn1 = new GridBagConstraints();
		gbcBtn1.gridx = 0;
		gbcBtn1.gridy = 2;
		gbcBtn1.fill = GridBagConstraints.HORIZONTAL;
		gbcBtn1.insets = new Insets(4, 0, 4, 0);
		contentPane.add(btnNewGame, gbcBtn1);

		btnHowTo = new MinecraftButton("게임 방법");
		btnHowTo.setPreferredSize(new Dimension(310, 40));
		GridBagConstraints gbcBtn2 = new GridBagConstraints();
		gbcBtn2.gridx = 0;
		gbcBtn2.gridy = 3;
		gbcBtn2.fill = GridBagConstraints.HORIZONTAL;
		gbcBtn2.insets = new Insets(4, 0, 4, 0);
		contentPane.add(btnHowTo, gbcBtn2);

		JPanel bottomRow = new JPanel(new GridLayout(1, 2, 6, 0));
		bottomRow.setOpaque(false);
		btnCredits = new MinecraftButton("크레딧");
		btnExit = new MinecraftButton("종료");
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
		title.setFont(FontManager.getMC(36));
		subTitle.setFont(FontManager.get(12));
		btnNewGame.setFont(FontManager.get(16));
		btnHowTo.setFont(FontManager.get(16));
		btnCredits.setFont(FontManager.get(14));
		btnExit.setFont(FontManager.get(14));

		// 액션
		btnNewGame.addActionListener(e -> {
		    JDialog dialog = new JDialog();
		    dialog.setTitle("새 게임");
		    dialog.setModal(true);
		    dialog.setSize(350, 180);
		    dialog.setLocationRelativeTo(this);

		    JLabel label = new JLabel("닉네임을 입력하세요", SwingConstants.CENTER);
		    label.setFont(FontManager.get(13));

		    JTextField input = new JTextField();
		    input.setFont(FontManager.get(13));
		    input.setMargin(new Insets(4, 8, 4, 8));

		    MinecraftButton btnOk = new MinecraftButton("확인");
		    btnOk.setFont(FontManager.get(13));
		    btnOk.setPreferredSize(new Dimension(100, 35));
		    btnOk.addActionListener(e2 -> {
		        String name = input.getText().trim();
		        if (!name.isEmpty()) {
		            dialog.dispose();
		            gameFrame.startNewGame(name);
		        }
		    });
		    input.addActionListener(e2 -> btnOk.doClick());

		    JPanel btnPanel = new JPanel();
		    btnPanel.setOpaque(false);
		    btnPanel.add(btnOk);

		    JPanel centerPanel = new JPanel(new GridLayout(2, 1, 0, 8));
		    centerPanel.setOpaque(false);
		    centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
		    centerPanel.add(label);
		    centerPanel.add(input);

		    dialog.setLayout(new BorderLayout());
		    dialog.add(centerPanel, BorderLayout.CENTER);
		    dialog.add(btnPanel, BorderLayout.SOUTH);
		    dialog.setVisible(true);
		});
		
		btnHowTo.addActionListener(e -> {
		    JDialog dialog = new JDialog();
		    dialog.setTitle("게임 방법");
		    dialog.setModal(true);
		    dialog.setSize(400, 300);
		    dialog.setLocationRelativeTo(this);
		    
		    JTextArea text = new JTextArea(
		        "[ 전투 ]\n공격 — 기본 공격\n막기 — 이번 턴 피해 무효\n스킬 — 쿨타임/수량 소모\n\n[ 코인 ]\n몹 처치/웨이브 클리어 시 획득\n사망해도 유지됨"
		    );
		    text.setEditable(false);
		    text.setFont(FontManager.get(13));
		    text.setBackground(null);
		    text.setOpaque(false);
		    text.setMargin(new Insets(15, 15, 15, 15));
		    
		    MinecraftButton btnOk = new MinecraftButton("확인");
		    btnOk.setFont(FontManager.get(13));
		    btnOk.setPreferredSize(new Dimension(100, 35));
		    btnOk.addActionListener(e2 -> dialog.dispose());
		    
		    JPanel btnPanel = new JPanel();
		    btnPanel.setOpaque(false);
		    btnPanel.add(btnOk);
		    
		    dialog.setLayout(new BorderLayout());
		    dialog.add(text, BorderLayout.CENTER);
		    dialog.add(btnPanel, BorderLayout.SOUTH);
		    dialog.setVisible(true);
		});

		btnCredits.addActionListener(e -> {
		    JDialog dialog = new JDialog();
		    dialog.setTitle("크레딧");
		    dialog.setModal(true);
		    dialog.setSize(400, 300);
		    dialog.setLocationRelativeTo(this);

		    JTextArea text = new JTextArea(
		        "[ 개발팀 ]\n\n" +
		        "  김미리\n" +
		        "  임민경\n" +
		        "  김새미\n"
		    );
		    text.setEditable(false);
		    text.setFont(FontManager.get(13));
		    text.setBackground(null);
		    text.setOpaque(false);
		    text.setMargin(new Insets(15, 15, 15, 15));

		    MinecraftButton btnOk = new MinecraftButton("확인");
		    btnOk.setFont(FontManager.get(13));
		    btnOk.setPreferredSize(new Dimension(100, 35));
		    btnOk.addActionListener(e2 -> dialog.dispose());

		    JPanel btnPanel = new JPanel();
		    btnPanel.setOpaque(false);
		    btnPanel.add(btnOk);

		    dialog.setLayout(new BorderLayout());
		    dialog.add(text, BorderLayout.CENTER);
		    dialog.add(btnPanel, BorderLayout.SOUTH);
		    dialog.setVisible(true);
		});

		btnExit.addActionListener(e -> System.exit(0));
	}
}