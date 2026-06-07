package view;

import effect.Burn;
import effect.StatusEffect;
import effect.Stun;
import entity.Steve;
import entity.mob.Mob;
import manager.WaveManager;
import skill.active.ActiveSkill;
import skill.consumable.ConsumableSkill;
import skill.weapon.AoeSlash;
import weapon.Weapon;

import javax.swing.*;

import ability.Explode;
import ability.Mobability;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BattleView extends JPanel {

	private GameFrame gameFrame;
	private Steve steve;
	private Mob mob;
	private List<Mob> waveMobs;
	private int mobIndex;
	private int wave;
	private WaveManager waveManager;

	private JLabel expLabel;
	private JProgressBar expBar;
	private JLabel steveCoinLabel;
	private JLabel messageLabel;
	private HeartPanel heartPanel;
	private ItemPanel itemPanel;
	private EquipmentPanel equipmentPanel;

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
	private ImageIcon effectBurnIcon;
	private ImageIcon effectStunIcon;
	private Image scopeImage;
	private boolean attackPotionReady = false;
	private boolean guardReady = false;
	private int dragonPhase = -1;
	private float phaseFlashAlpha = 0f;
	private javax.swing.Timer phaseFlashTimer;
	private IntroLabel introLabel;
	private boolean introActive = false;
	private float introAlpha = 1f;
	private javax.swing.Timer introTimer;
	private boolean inputLocked = false;
	private boolean creepExploding;

	private static final Color GOLD = new Color(230, 184, 76);
	private static final Color HOTBAR_SLOT = new Color(118, 118, 118, 82);
	private static final Color HOTBAR_INNER = new Color(210, 210, 210, 34);
	private static final Color XP_GREEN = new Color(116, 255, 70);
	private static final String COIN_ICON_PATH = "resources/icon/Coin.png";
	private static final String PIXEL_FONT_PATH = "fonts/Minecraftia-Regular.ttf";
	private static final String MOB_ENCOUNTER_FRAME_PATH = "resources/ui/mob_encounter.png";
	private static final int TOP_HUD_LEVEL_X_OFFSET = -20;
	private static final int BATTLE_CONTENT_X_OFFSET = -50;
	private static Font pixelFontBase;
	private static final Map<String, ImageIcon> ICON_CACHE = new HashMap<>();
	private static final Map<String, ImageIcon> SCALED_ICON_CACHE = new HashMap<>();

	private static final Map<String, String[]> MOB_IMAGE_MAP = new HashMap<>();
	static {
		MOB_IMAGE_MAP.put("\uC880\uBE44",
				new String[] { "resources/monster/zombie_clean.png", "resources/monster/zombie_v1_clean.png" });
		MOB_IMAGE_MAP.put("\uC2A4\uCF08\uB808\uD1A4",
				new String[] { "resources/monster/skeleton_clean.png", "resources/monster/skeleton_v1_clean.png" });
		MOB_IMAGE_MAP.put("\uB9C8\uB140",
				new String[] { "resources/monster/witch_clean.png", "resources/monster/witch_clean.png" });
		MOB_IMAGE_MAP.put("\uD06C\uB9AC\uD37C",
				new String[] { "resources/monster/creeper_clean.png", "resources/monster/creeper_v1_clean.png" });
		MOB_IMAGE_MAP.put("\uC704\uB354\uC2A4\uCF08\uB808\uD1A4", new String[] {
				"resources/monster/witherskeleton_clean.png", "resources/monster/witherskeleton_v1_clean.png" });
		MOB_IMAGE_MAP.put("\uD53C\uAE00\uB9B0",
				new String[] { "resources/monster/piglin_clean.png", "resources/monster/piglin_v1_clean.png" });
		MOB_IMAGE_MAP.put("\uC5D4\uB354\uB4DC\uB798\uACE4", new String[] {
				"resources/monster/enderdragon_phase0_clean.png", "resources/monster/enderdragon_phase0_clean.png" });
	}

	/** 이/가 */
	private static String iGa(String name) {
		if (name == null || name.isEmpty())
			return "이/가";
		char last = name.charAt(name.length() - 1);
		if (last < 0xAC00 || last > 0xD7A3)
			return "이/가";
		return (last - 0xAC00) % 28 == 0 ? "가" : "이";
	}

	/** 을/를 */
	private static String eulReul(String name) {
		if (name == null || name.isEmpty())
			return "을/를";
		char last = name.charAt(name.length() - 1);
		if (last < 0xAC00 || last > 0xD7A3)
			return "을/를";
		return (last - 0xAC00) % 28 == 0 ? "를" : "을";
	}

	public BattleView(GameFrame gameFrame, Steve steve, WaveManager waveManager, Mob mob, int wave) {
		this(gameFrame, steve, waveManager, mob, wave, false);
	}

	public BattleView(GameFrame gameFrame, Steve steve, WaveManager waveManager, Mob mob, int wave,
			boolean showEncounterFirst) {
		this.gameFrame = gameFrame;
		this.steve = steve;
		this.wave = wave;
		this.waveManager = waveManager;

		this.waveMobs = new ArrayList<>(waveManager.getAliveMobs());
		this.mobIndex = 0;
		this.mob = waveMobs.isEmpty() ? mob : waveMobs.get(0);

		showBattleContent(showEncounterFirst);
	}

	private String buildEncounterMessage() {
		if (waveMobs.size() > 1) {
			List<String> parts = new ArrayList<>();
			String prevName = null;
			int cnt = 0;
			for (Mob m : waveMobs) {
				String name = m.getName();
				if (name.equals(prevName)) {
					cnt++;
				} else {
					if (prevName != null)
						parts.add(cnt > 1 ? prevName + " x" + cnt : prevName);
					prevName = name;
					cnt = 1;
				}
			}
			if (prevName != null)
				parts.add(cnt > 1 ? prevName + " x" + cnt : prevName);

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < parts.size(); i++) {
				if (i > 0)
					sb.append(" + ");
				sb.append(parts.get(i));
			}
			sb.append(" 등장!");
			return sb.toString();
		}
		return mob.getName() + " 등장!";
	}

	private void playEncounterIntro() {
		if (introLabel == null)
			return;
		if (introTimer != null && introTimer.isRunning())
			introTimer.stop();

		javax.swing.Timer holdTimer = new javax.swing.Timer(1000, e -> {
			introTimer = new javax.swing.Timer(35, fadeEvent -> {
				introAlpha = Math.max(0f, introAlpha - 0.08f);
				introLabel.setAlpha(introAlpha);
				if (introAlpha <= 0f) {
					introTimer.stop();
					introActive = false;
					inputLocked = false;
					introLabel.setVisible(false);
					cardPanel.setVisible(true);
					cardPanel.repaint();
					requestFocusInWindow();

					playMobSpawnEffect();
				}
			});
			introTimer.start();
		});
		holdTimer.setRepeats(false);
		holdTimer.start();
	}

	private void showBattleContent(boolean playIntro) {
		introActive = playIntro;
		introAlpha = 1f;
		inputLocked = playIntro;

		loadImages();
		updateDragonPhaseImage(false);
		resetSkillCooldownsForWave();

		BackgroundPanel root = new BackgroundPanel(getBackgroundPath());

		setLayout(new BorderLayout());
		add(root, BorderLayout.CENTER);

		JPanel topHud = buildTopHud();
		JPanel playerSidebar = buildPlayerSidebar();

		mobBattlePanel = new MobBattlePanel(buildMobImageList());

		cardPanel = new CardPanel();
		cardPanel.setVisible(!playIntro);
		introLabel = playIntro ? new IntroLabel("WAVE " + wave, "-" + buildEncounterMessage() + "-") : null;
		BattleLayerPanel battleLayer = new BattleLayerPanel();
		root.setLayout(new LayoutManager() {
			@Override
			public void addLayoutComponent(String name, Component comp) {
			}

			@Override
			public void removeLayoutComponent(Component comp) {
			}

			@Override
			public Dimension preferredLayoutSize(Container parent) {
				return new Dimension(854, 560);
			}

			@Override
			public Dimension minimumLayoutSize(Container parent) {
				return new Dimension(854, 560);
			}

			@Override
			public void layoutContainer(Container parent) {
				layoutBattleFrame(parent, topHud, playerSidebar, battleLayer);
			}
		});

		root.add(battleLayer);
		root.add(topHud);
		root.add(playerSidebar);

		refreshUI();

		if (playIntro) {
			mobBattlePanel.setMobAlpha(0f);
			SwingUtilities.invokeLater(this::playEncounterIntro);
			return;
		}

		if (waveMobs.size() > 1) {
			StringBuilder sb = new StringBuilder("웨이브 " + wave + " - 적 등장: ");
			for (int i = 0; i < waveMobs.size(); i++) {
				if (i > 0)
					sb.append(", ");
				sb.append(waveMobs.get(i).getName());
			}
			showMessage(sb.toString());
			System.out.println("[웨이브 " + wave + "] " + sb.toString());
		} else if (!waveMobs.isEmpty()) {
			showMessage("웨이브 " + wave + " - " + this.mob.getName() + " 등장!");
			System.out.println("[웨이브 " + wave + "] " + this.mob.getName() + iGa(this.mob.getName()) + " 등장했습니다!");
		}
	}

	private void playMobSpawnEffect() {
		mobBattlePanel.setMobAlpha(0f);

		float[] alpha = { 0f };
		javax.swing.Timer fadeIn = new javax.swing.Timer(30, null);
		fadeIn.addActionListener(e -> {
			alpha[0] = Math.min(1f, alpha[0] + 0.05f);
			mobBattlePanel.setMobAlpha(alpha[0]);

			if (alpha[0] >= 0.3f && !mobBattlePanel.hasSpawnedParticles()) {
				int cx = mobBattlePanel.getWidth() / 2;
				int cy = mobBattlePanel.getHeight() / 2;
				int mobW = (int) (mobBattlePanel.getWidth() * 0.4);
				int mobH = (int) (mobBattlePanel.getHeight() * 0.5);
				mobBattlePanel.playSpawnParticles(cx, cy, mobW, mobH);
			}

			if (alpha[0] >= 1f) {
				fadeIn.stop();
			}
		});
		fadeIn.start();
	}

	private List<Image[]> buildMobImageList() {
		List<Image[]> images = new ArrayList<>();
		for (Mob m : waveMobs) {
			String[] paths = MOB_IMAGE_MAP.get(m.getName());
			Image normal = null, hurt = null;
			if (paths != null) {
				ImageIcon ni = loadIcon(paths[0]);
				ImageIcon hi = loadIcon(paths[1]);
				if (ni != null)
					normal = ni.getImage();
				if (hi != null)
					hurt = hi.getImage();
			}
			images.add(new Image[] { normal, hurt });
		}
		return images;
	}

	private String getBackgroundPath() {
		if (wave <= 2)
			return "resources/bg/battle_easy.png";
		if (wave == 3)
			return "resources/bg/battle_normal.png";
		if (wave <= 5)
			return "resources/bg/battle_hard.png";
		return "resources/bg/battle_boss.png";
	}

	private void layoutBattleFrame(Container root, JPanel topHud, JPanel playerSidebar, BattleLayerPanel battleLayer) {
		int w = root.getWidth();
		int h = root.getHeight();
		if (w <= 0 || h <= 0)
			return;

		topHud.setBounds(0, 0, w, scaleY(h, 96));
		playerSidebar.setBounds(0, 0, scaleX(w, 146), h);
		battleLayer.setBounds(scaleX(w, 132), scaleY(h, 104), w - scaleX(w, 132), h - scaleY(h, 104));
	}

	private static int scaleX(int width, int designX) {
		return Math.round(width * designX / 854f);
	}

	private static int scaleY(int height, int designY) {
		return Math.round(height * designY / 560f);
	}

	private static class BackgroundPanel extends JPanel {
		private static final int FRAME_SOURCE_W = 1536;
		private static final int FRAME_SOURCE_H = 1024;
		private Image bgImage;
		private Image frameImage;

		BackgroundPanel(String imagePath) {
			setOpaque(true);
			try {
				ImageIcon ic = new ImageIcon(imagePath);
				if (ic.getIconWidth() > 0)
					bgImage = ic.getImage();
			} catch (Exception ignored) {
			}
			try {
				ImageIcon frameIcon = loadIcon("resources/ui/battle_frame.png");
				if (frameIcon.getIconWidth() > 0)
					frameImage = frameIcon.getImage();
			} catch (Exception ignored) {
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			if (bgImage != null) {
				g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
			} else {
				GradientPaint gp = new GradientPaint(0, 0, new Color(31, 27, 44), 0, getHeight(),
						new Color(13, 13, 20));
				g2.setPaint(gp);
				g2.fillRect(0, 0, getWidth(), getHeight());
			}
			if (frameImage != null) {
				drawFrameSection(g2, 236, 48, 990, 116);
				drawFrameSection(g2, 1270, 60, 206, 104);
				drawFrameSection(g2, 38, 234, 216, 348);
				drawFrameSection(g2, 38, 596, 216, 254);
			}
			g2.dispose();
		}

		private void drawFrameSection(Graphics2D g2, int sx, int sy, int sw, int sh) {
			int dx = Math.round(getWidth() * sx / (float) FRAME_SOURCE_W);
			int dy = Math.round(getHeight() * sy / (float) FRAME_SOURCE_H);
			int dw = Math.round(getWidth() * sw / (float) FRAME_SOURCE_W);
			int dh = Math.round(getHeight() * sh / (float) FRAME_SOURCE_H);
			g2.drawImage(frameImage, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh, this);
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

	private static class SpawnParticle {
		float x, y;
		float vx, vy;
		float alpha;
		int size;
		Color color;

		SpawnParticle(float x, float y) {
			this.x = x;
			this.y = y;
			// 랜덤 방향으로 퍼져나감
			double angle = Math.random() * Math.PI * 2;
			float speed = (float) (Math.random() * 2.5 + 0.5f);
			this.vx = (float) Math.cos(angle) * speed;
			this.vy = (float) Math.sin(angle) * speed - 1.5f; 
			this.alpha = 1.0f;
			this.size = (int) (Math.random() * 6 + 3); 
			// 보라색 계열 랜덤
			int r = (int) (Math.random() * 60 + 120); // 120~180
			int g = (int) (Math.random() * 30); // 0~30
			int b = (int) (Math.random() * 60 + 180); // 180~240
			this.color = new Color(r, g, b);
		}
	}

	private static class StatusBadge {
		ImageIcon icon;
		int remainingTurns;
		int cropX, cropY, cropW, cropH;

		StatusBadge(ImageIcon icon, int remainingTurns, int cropX, int cropY, int cropW, int cropH) {
			this.icon = icon;
			this.remainingTurns = remainingTurns;
			this.cropX = cropX;
			this.cropY = cropY;
			this.cropW = cropW;
			this.cropH = cropH;
		}
	}

	private static class IntroLabel extends JLabel {
		private float alpha = 1f;
		private final String title;
		private final String subtitle;
		private final ImageIcon frameIcon;

		IntroLabel(String title, String subtitle) {
			super("", SwingConstants.CENTER);
			this.title = title;
			this.subtitle = subtitle;
			this.frameIcon = loadIcon(MOB_ENCOUNTER_FRAME_PATH);
			setOpaque(false);
			setForeground(Color.WHITE);
		}

		void setAlpha(float alpha) {
			this.alpha = Math.max(0f, Math.min(1f, alpha));
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			int frameW = Math.min(getWidth(), 430);
			int frameH = 96;
			if (frameIcon != null && frameIcon.getIconWidth() > 0) {
				double ratio = frameIcon.getIconHeight() / (double) frameIcon.getIconWidth();
				frameH = (int) Math.round(frameW * ratio);
				frameH = Math.min(frameH, getHeight());
			}
			int x = (getWidth() - frameW) / 2;
			int y = (getHeight() - frameH) / 2;

			if (frameIcon != null && frameIcon.getIconWidth() > 0) {
				g2.drawImage(frameIcon.getImage(), x, y, frameW, frameH, null);
			} else {
				g2.setColor(new Color(33, 18, 12, 235));
				g2.fillRect(x, y, frameW, frameH);
				g2.setColor(GOLD);
				g2.drawRect(x + 1, y + 1, frameW - 3, frameH - 3);
			}

			drawIntroText(g2, title, uiPixelFont(24), Color.WHITE, x, y + frameH / 2 + 18, frameW);
			drawIntroText(g2, subtitle, koreanPixelFont(15), new Color(255, 203, 68), x, y + frameH / 2 + 25, frameW);
			g2.dispose();
		}

		private void drawIntroText(Graphics2D g2, String text, Font font, Color color, int x, int baseline, int w) {
			g2.setFont(font);
			FontMetrics fm = g2.getFontMetrics();
			String trimmed = trimToWidth(g2, text, w - 46);
			int tx = x + (w - fm.stringWidth(trimmed)) / 2;
			g2.setColor(new Color(0, 0, 0, 230));
			g2.drawString(trimmed, tx + 3, baseline + 3);
			g2.setColor(color);
			g2.drawString(trimmed, tx, baseline);
		}
	}

	private List<StatusBadge> getMobStatusBadges(Mob targetMob) {
		List<StatusBadge> badges = new ArrayList<>();
		if (targetMob == null || targetMob.getEffects() == null)
			return badges;

		for (StatusEffect effect : targetMob.getEffects()) {
			if (effect instanceof Burn) {
				int turns = ((Burn) effect).getRemainingTurns();
				if (turns > 0)
					badges.add(new StatusBadge(effectBurnIcon, turns, 543, 275, 210, 231));
			} else if (effect instanceof Stun) {
				int turns = ((Stun) effect).getRemainingTurns();
				if (turns > 0)
					badges.add(new StatusBadge(effectStunIcon, turns, 543, 295, 211, 211));
			}
		}

		badges.sort((a, b) -> Integer.compare(a.remainingTurns, b.remainingTurns));
		return badges;
	}

	private static void drawCroppedImage(Graphics2D g2, ImageIcon icon, int sx, int sy, int sw, int sh, int dx, int dy,
			int dw, int dh) {
		if (icon == null || icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0)
			return;
		Image img = icon.getImage();
		g2.drawImage(img, dx, dy, dx + dw, dy + dh, sx, sy, sx + sw, sy + sh, null);
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
			if (introLabel != null)
				add(introLabel, JLayeredPane.DRAG_LAYER);
		}

		@Override
		public void doLayout() {

			int w = getWidth();
			int h = getHeight();

			int cardW = Math.min(w - 40, 620);
			int cardH = 180;
			int cardX = (w - cardW) / 2 + BATTLE_CONTENT_X_OFFSET;
			int cardY = h - cardH;

			mobBattlePanel.setCardTopY(cardY);
			mobBattlePanel.setBounds(0, 0, w, h);

			int targetY = cardY - 30;

			if (messageLabel.getText().isEmpty()) {
				messageLabel.setVisible(false);
			} else {
				messageLabel.setVisible(true);
				messageLabel.setBounds(BATTLE_CONTENT_X_OFFSET, targetY, w, 24);
			}

			cardPanel.setBounds(cardX, cardY, cardW, cardH);
			if (introLabel != null) {
				introLabel.setVisible(introActive);
				int introW = Math.min(w - 40, 500);
				int introH = 200;
				int introX = (w - introW) / 2 + BATTLE_CONTENT_X_OFFSET;
				int introY = Math.max(0, cardY - 160);
				introLabel.setBounds(introX, introY, introW, introH);
			}
		}
	}

	private class MobBattlePanel extends JPanel {
		private List<Image[]> mobImages;
		private int currentMobIndex = 0;

		private boolean showHurt = false;
		private boolean isDead = false;
		private float deadAlpha = 1.0f;
		private int deadDropY = 0;
		private int cardTopY = 330;

		private List<DamageNumber> dmgNumbers = new ArrayList<>();
		private javax.swing.Timer animTimer;

		private java.util.List<Rectangle> effectBounds = new java.util.ArrayList<>();
		private java.util.List<String> renderedEffectNames = new java.util.ArrayList<>();
		private int hoveredEffectIdx = -1;

		private List<SpawnParticle> spawnParticles = new ArrayList<>();
		private javax.swing.Timer particleTimer;

		private float mobAlpha = 1f;
		private boolean spawnedParticles = false;
		
		void playCreepFlash(Runnable onComplete) {
		    // 크리퍼 이미지를 흰색으로 여러번 깜빡임
		    int[] count = {0};
		    javax.swing.Timer flashTimer = new javax.swing.Timer(120, null);
		    flashTimer.addActionListener(e -> {
		        showHurt = (count[0] % 2 == 0); 
		        repaint();
		        count[0]++;
		        if (count[0] >= 6) { 
		            flashTimer.stop();
		            showHurt = false;
		            repaint();
		            if (onComplete != null) onComplete.run();
		        }
		    });
		    flashTimer.start();
		}

		void playExplosionEffect(Runnable onComplete) {
		    int cx = getWidth() / 2;
		    int cy = getHeight() / 2;
		    int count = 80;
		    for (int i = 0; i < count; i++) {
		        float px = cx + (float)(Math.random() * 200 - 100);
		        float py = cy + (float)(Math.random() * 200 - 100);
		        SpawnParticle p = new SpawnParticle(px, py);
		        int type = (int)(Math.random() * 3);
		        if (type == 0) p.color = new Color(80, 200, 80);       // 초록
		        else if (type == 1) p.color = new Color(200, 200, 60); // 노랑
		        else p.color = new Color(240, 240, 240);                // 흰색
		        p.vx *= 2.5f; 
		        p.vy *= 2.5f;
		        p.size += 4;
		        spawnParticles.add(p);
		    }
		    if (!particleTimer.isRunning()) particleTimer.start();
		    playShake();

		    javax.swing.Timer wait = new javax.swing.Timer(600, e -> {
		        if (onComplete != null) onComplete.run();
		    });
		    wait.setRepeats(false);
		    wait.start();
		}

		void setMobAlpha(float alpha) {
			this.mobAlpha = alpha;
			repaint();
		}

		boolean hasSpawnedParticles() {
			return spawnedParticles;
		}

		MobBattlePanel(List<Image[]> images) {
			this.mobImages = images;
			setOpaque(false);

			animTimer = new javax.swing.Timer(30, e -> {
				for (DamageNumber d : dmgNumbers) {
					d.y -= 1.8f;
					d.alpha = Math.max(0f, d.alpha - 0.018f);
				}
				dmgNumbers.removeIf(d -> d.alpha <= 0f);
				repaint();
				if (dmgNumbers.isEmpty())
					animTimer.stop();
			});

			particleTimer = new javax.swing.Timer(30, e -> {
				for (SpawnParticle p : spawnParticles) {
					p.x += p.vx;
					p.y += p.vy;
					p.vy += 0.08f; 
					p.alpha -= 0.022f;
					p.size = Math.max(1, p.size);
				}
				spawnParticles.removeIf(p -> p.alpha <= 0f);
				repaint();
				if (spawnParticles.isEmpty())
					particleTimer.stop();
			});

			addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
				@Override
				public void mouseMoved(java.awt.event.MouseEvent e) {
					int prev = hoveredEffectIdx;
					hoveredEffectIdx = -1;

					for (int i = 0; i < effectBounds.size(); i++) {
						if (effectBounds.get(i).contains(e.getPoint())) {
							hoveredEffectIdx = i;
							break;
						}
					}
					if (hoveredEffectIdx != prev) {
						repaint();
					}
				}
			});

			addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					hoveredEffectIdx = -1;
					repaint();
				}
			});
		}

		void playSpawnParticles(int mobCenterX, int mobCenterY, int mobW, int mobH) {
			spawnedParticles = true;
			int count = 40;
			for (int i = 0; i < count; i++) {
				float px = mobCenterX + (float) (Math.random() * mobW - mobW / 2f);
				float py = mobCenterY + (float) (Math.random() * mobH - mobH / 2f);
				spawnParticles.add(new SpawnParticle(px, py));
			}
			if (!particleTimer.isRunning())
				particleTimer.start();
		}

		void setCardTopY(int cardTopY) {
			this.cardTopY = cardTopY;
		}

		void setImages(Image normal, Image hurt) {
			if (!mobImages.isEmpty()) {
				mobImages.set(currentMobIndex, new Image[] { normal, hurt });
			}
			repaint();
		}

		void setMobIndex(int idx) {
			this.currentMobIndex = idx;
			this.isDead = false;
			this.deadAlpha = 1.0f;
			this.deadDropY = 0;
			repaint();
		}

		void updateImages(List<Image[]> images) {
			this.mobImages = images;
			repaint();
		}

		void showDamage(int dmg, boolean isPlayerAttack) {
			float cx = getWidth() * 0.5f + (float) (Math.random() * 60 - 30);
			float cy = getHeight() * 0.40f + (float) (Math.random() * 30 - 15);
			Color c = isPlayerAttack ? new Color(255, 80, 80) : new Color(255, 220, 60);
			dmgNumbers.add(new DamageNumber("-" + dmg, c, cx, cy));
			if (!animTimer.isRunning())
				animTimer.start();
		}

		void showDamage(int dmg, Color color) {
			float cx = getWidth() * 0.5f + (float) (Math.random() * 60 - 30);
			float cy = getHeight() * 0.40f + (float) (Math.random() * 30 - 15);
			dmgNumbers.add(new DamageNumber("-" + dmg, color, cx, cy));
			if (!animTimer.isRunning())
				animTimer.start();
		}

		void playHitFlash() {
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
			int[] offsets = { -10, 10, -7, 7, -4, 4, -2, 2, 0 };
			int[] idx = { 0 };
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
					if (onComplete != null)
						SwingUtilities.invokeLater(onComplete);
				}
			});
			deathTimer.start();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();

			effectBounds.clear();
			renderedEffectNames.clear();

			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			int pw = getWidth();
			int topLimit = 46;
			int groundY = Math.max(topLimit + 90, cardTopY - 16);
			int availableH = groundY - topLimit;
			int count = mobImages.size();

			if (count == 0) {
				g2.setFont(new Font("Dialog", Font.BOLD, 62));
				g2.setColor(Color.WHITE);
				drawCentered(g2, mob.getName(), 0, getHeight() / 2, pw);
			} else {
				boolean boss = "엔더드래곤".equals(mob.getName());
				double heightRatio = boss ? 1.08 : 0.92;
				double widthRatio = boss ? 0.58 : (count > 1 ? 0.28 : 0.44);

				int targetDx = -1, targetDy = -1, targetDw = 0, targetDh = 0;

				for (int i = 0; i < count; i++) {
					Mob drawnMob = i < waveMobs.size() ? waveMobs.get(i) : mob;
					Image[] pair = mobImages.get(i);
					Image img = (showHurt && i == currentMobIndex && pair[1] != null) ? pair[1] : pair[0];
					if (img == null)
						continue;

					int iw = img.getWidth(null), ih = img.getHeight(null);
					if (iw <= 0 || ih <= 0)
						continue;

					double scale = (availableH * heightRatio) / ih;
					if (iw * scale > pw * widthRatio)
						scale = (pw * widthRatio) / iw;
					int dw = (int) (iw * scale);
					int dh = (int) (ih * scale);
					int maxH = Math.max(90, groundY - 18);
					if (dh > maxH) {
						double fit = (double) maxH / dh;
						dw = (int) (dw * fit);
						dh = maxH;
					}

					int slotW = pw / count;
					int leftMargin = count >= 3 ? scaleX(getWidth(), 40) : 0;
					int usableW = pw - leftMargin;
					int dx = leftMargin + (usableW / count) * i + ((usableW / count) - dw) / 2
							+ BATTLE_CONTENT_X_OFFSET;
					int dy = groundY - dh;

					if (count == 2) {
						int offset = 70;
						if (i == 0) {
							dx += offset;
						} else {
							dx -= offset;
						}
					}

					boolean dyingNow = isDead && i == currentMobIndex;
					boolean alreadyDead = !drawnMob.isAlive() && !dyingNow;

					if (alreadyDead) {
						continue;
					} else if (dyingNow) {
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, deadAlpha)));
						dy += deadDropY;
					} else {
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mobAlpha));
					}

					if (i == currentMobIndex && !isDead) {
						targetDx = dx;
						targetDy = dy;
						targetDw = dw;
						targetDh = dh;
					}

					g2.drawImage(img, dx, dy, dw, dh, null);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mobAlpha));
					if (!dyingNow) {
						drawMobHud(g2, drawnMob, dx, dy, dw);
					}
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}

				if (targetDx >= 0 && scopeImage != null) {
					int scopeW = 100;
					int scopeH = 100;
					int scopeOffsetX = 0;
					if ("스켈레톤".equals(mob.getName())) {
						scopeOffsetX = -15;
					} else if ("위더스켈레톤".equals(mob.getName())) {
						scopeOffsetX = +15;
					}
					int scopeX = targetDx + (targetDw - scopeW) / 2 + scopeOffsetX;
					int scopeY = targetDy + (targetDh - scopeH) / 2;

					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mobAlpha));
					g2.drawImage(scopeImage, scopeX, scopeY, scopeW, scopeH, null);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}
			}

			if (hoveredEffectIdx >= 0 && hoveredEffectIdx < renderedEffectNames.size()) {
				String effectName = renderedEffectNames.get(hoveredEffectIdx);
				String tooltipText = "";

				if ("Stun".equalsIgnoreCase(effectName)) {
					tooltipText = "스턴: 적이 한 턴 동안 공격하지 못함";
				} else if ("Burn".equalsIgnoreCase(effectName)) {
					tooltipText = "화상: 턴마다 추가 피해";
				}

				if (!tooltipText.isEmpty()) {
					Point mousePos = getMousePosition();
					if (mousePos != null) {
						int tx = mousePos.x + 12;
						int ty = mousePos.y + 12;

						g2.setFont(new Font("Dialog", Font.BOLD, 12));
						FontMetrics fm = g2.getFontMetrics();
						int tw = fm.stringWidth(tooltipText) + 12;
						int th = fm.getHeight() + 8;

						if (tx + tw > getWidth())
							tx = mousePos.x - tw - 4;
						if (ty + th > getHeight())
							ty = mousePos.y - th - 4;

						g2.setColor(new Color(16, 0, 16, 230));
						g2.fillRect(tx, ty, tw, th);

						g2.setColor(new Color(32, 1, 71));
						g2.drawRect(tx, ty, tw, th);

						g2.setColor(Color.WHITE);
						g2.drawString(tooltipText, tx + 6, ty + fm.getAscent() + 4);
					}
				}
			}

			// 파티클 렌더링
			for (SpawnParticle p : new ArrayList<>(spawnParticles)) {
				if (p.alpha <= 0f)
					continue;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(1f, p.alpha)));
				g2.setColor(p.color);
				g2.fillRect((int) p.x, (int) p.y, p.size, p.size);
			}
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

			g2.dispose();
		}

		private void drawMobHud(Graphics2D g2, Mob targetMob, int mobX, int mobY, int mobW) {
			int barW = Math.min(150, Math.max(118, mobW + 12));
			int barH = 14;
			int centerX = mobX + mobW / 2;
			int barX = centerX - barW / 2;

			List<StatusBadge> badges = getMobStatusBadges(targetMob);
			int statusH = 28;
			int totalH = 18 + 14 + 6 + barH + 8 + statusH;
			int hudY = Math.max(4, mobY - totalH - 18);
			int statBaseline = hudY + 14;
			int nameBaseline = hudY + 31;
			int barY = hudY + 38;
			int statusY = barY + barH + 8;

			drawMobStatRow(g2, targetMob, centerX, statBaseline);
			drawMobName(g2, targetMob.getName(), barX, nameBaseline, barW);
			drawMobHealthBar(g2, targetMob, barX, barY, barW, barH);
			if (!badges.isEmpty()) {
				drawMobStatusBadges(g2, badges, barX, statusY);
			}
		}

		private void drawMobName(Graphics2D g2, String name, int x, int baseline, int w) {
			g2.setFont(uiPixelFont(11));
			String text = trimToWidth(g2, name, w);
			FontMetrics fm = g2.getFontMetrics();
			int tx = x + (w - fm.stringWidth(text)) / 2;
			g2.setColor(new Color(0, 0, 0, 230));
			g2.drawString(text, tx + 2, baseline + 2);
			g2.setColor(Color.WHITE);
			g2.drawString(text, tx, baseline);
		}

		private void drawMobStatRow(Graphics2D g2, Mob targetMob, int centerX, int baseline) {
			int rowW = 116;
			int x = centerX - rowW / 2;
			drawMobStat(g2, x, baseline, targetMob.getAttackPower(), true);
			drawMobStat(g2, x + 62, baseline, targetMob.getDefencePower(), false);
		}

		private void drawMobStat(Graphics2D g2, int x, int baseline, int value, boolean attack) {
			ImageIcon icon = attack ? atkIcon : defIcon;
			drawIcon(g2, icon, x, baseline - 17, 18, 18);
			g2.setFont(uiPixelFont(11));
			g2.setColor(new Color(0, 0, 0, 210));
			g2.drawString(String.valueOf(value), x + 24, baseline + 2);
			g2.setColor(new Color(255, 226, 120));
			g2.drawString(String.valueOf(value), x + 23, baseline + 1);
		}

		private void drawMobHealthBar(Graphics2D g2, Mob targetMob, int x, int y, int w, int h) {
			double ratio = targetMob.getMaxHealth() <= 0 ? 0
					: Math.max(0, Math.min(1, (double) targetMob.getHealth() / targetMob.getMaxHealth()));
			int cellCount = 12;
			int gap = 2;
			int framePad = 3;
			int cellsX = x + framePad;
			int cellsY = y + framePad;
			int cellsH = h - framePad * 2;
			int cellW = Math.max(5, (w - framePad * 2 - gap * (cellCount - 1)) / cellCount);
			int frameW = framePad * 2 + cellCount * cellW + (cellCount - 1) * gap;

			g2.setColor(new Color(0, 0, 0, 230));
			g2.fillRect(x - 1, y - 1, frameW + 2, h + 2);
			g2.setColor(new Color(94, 45, 31));
			g2.drawRect(x, y, frameW - 1, h - 1);
			g2.setColor(new Color(12, 8, 8));
			g2.fillRect(cellsX, cellsY, frameW - framePad * 2, cellsH);

			for (int i = 0; i < cellCount; i++) {
				double cellRatio = Math.max(0, Math.min(1, ratio * cellCount - i));
				int cx = cellsX + i * (cellW + gap);
				g2.setColor(new Color(42, 6, 6));
				g2.fillRect(cx, cellsY, cellW, cellsH);
				if (cellRatio > 0) {
					int fillW = Math.max(1, (int) Math.round(cellW * cellRatio));
					g2.setColor(new Color(190, 0, 0));
					g2.fillRect(cx, cellsY, fillW, cellsH);
					g2.setColor(new Color(255, 56, 44));
					g2.fillRect(cx, cellsY, fillW, Math.max(2, cellsH / 3));
				}
				g2.setColor(new Color(0, 0, 0, 140));
				g2.drawRect(cx, cellsY, cellW - 1, cellsH - 1);
			}

			String hpText = targetMob.getHealth() + " / " + targetMob.getMaxHealth();
			g2.setFont(uiPixelFont(10));
			FontMetrics fm = g2.getFontMetrics();
			int tx = x + frameW + 8;
			int ty = y + h / 2 + fm.getAscent() / 2 - 2;
			g2.setColor(new Color(0, 0, 0, 220));
			g2.drawString(hpText, tx + 2, ty + 2);
			g2.setColor(Color.WHITE);
			g2.drawString(hpText, tx, ty);
		}

		private void drawMobStatusBadges(Graphics2D g2, List<StatusBadge> badges, int x, int y) {
			int iconSize = 24;
			int gap = 5;
			for (int i = 0; i < badges.size(); i++) {
				StatusBadge badge = badges.get(i);
				int ix = x + i * (iconSize + gap);
				drawCroppedImage(g2, badge.icon, badge.cropX, badge.cropY, badge.cropW, badge.cropH, ix, y, iconSize,
						iconSize);
				drawBadgeTurnText(g2, String.valueOf(badge.remainingTurns), ix, y, iconSize);

				effectBounds.add(new Rectangle(ix, y, iconSize, iconSize));

				if (badge.icon == effectBurnIcon) {
					renderedEffectNames.add("Burn");
				} else if (badge.icon == effectStunIcon) {
					renderedEffectNames.add("Stun");
				} else {
					renderedEffectNames.add("Unknown");
				}
			}
		}

		private void drawBadgeTurnText(Graphics2D g2, String text, int x, int y, int size) {
			g2.setFont(uiPixelFont(9));
			FontMetrics fm = g2.getFontMetrics();
			int tx = x + size - fm.stringWidth(text) - 2;
			int ty = y + size - 3;
			g2.setColor(new Color(0, 0, 0, 230));
			g2.drawString(text, tx + 1, ty + 1);
			g2.setColor(Color.WHITE);
			g2.drawString(text, tx, ty);
		}
	}

	enum CardType {
		ATTACK, GUARD, AOE, ICE, FIRE
	}

	enum CardAction {
		ATTACK, GUARD, AOE_SLASH, ACTIVE_SKILL
	}

	static class Card {
		String name, type, description, tooltip;
		int cost;
		Color color;
		CardType cardType;
		CardAction action;
		ActiveSkill activeSkill;
		Rectangle bounds;

		Card(String name, String type, int cost, Color color, String description, CardType cardType,
				CardAction action) {
			this(name, type, cost, color, description, null, cardType, action);
		}

		Card(String name, String type, int cost, Color color, String description, String tooltip, CardType cardType,
				CardAction action) {
			this.name = name;
			this.type = type;
			this.cost = cost;
			this.color = color;
			this.description = description;
			this.tooltip = tooltip;
			this.cardType = cardType;
			this.action = action;
		}

		Card(String name, String type, int cost, Color color, String description, CardType cardType,
				ActiveSkill activeSkill) {
			this(name, type, cost, color, description, null, cardType, activeSkill);
		}

		Card(String name, String type, int cost, Color color, String description, String tooltip, CardType cardType,
				ActiveSkill activeSkill) {
			this(name, type, cost, color, description, tooltip, cardType, CardAction.ACTIVE_SKILL);
			this.activeSkill = activeSkill;
		}
	}

	static class CardStyle {
		static final int WIDTH = 96;
		static final int HEIGHT = 136;
		static final int GAP = 10;
	}

	private class CardPanel extends JPanel {
		private List<Card> deckCards = new ArrayList<>();
		private List<Card> handCards = new ArrayList<>();
		private Map<Card, Float> disappearingCards = new HashMap<>();
		private Card hoveredCard = null;
		private Card selectedCard = null;
		private int mouseX, mouseY;
		private javax.swing.Timer discardTimer;

		private ImageIcon cardFrame;

		CardPanel() {

			cardFrame = loadScaledIcon("resources/ui/card_frame_v3.png", CardStyle.WIDTH, CardStyle.HEIGHT);

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
					if (hoveredCard != prev)
						repaint();
				}
			});

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (inputLocked)
						return;
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
			deckCards.add(new Card("공격", "Basic", 0, new Color(0x8B2020),
					"물리 피해 **" + steve.getTotalAttackPower() + "** 입힘", CardType.ATTACK, CardAction.ATTACK));
			deckCards.add(
					new Card("방어", "Basic", 0, new Color(0x1A3A8A), "다음 공격 **방어**", CardType.GUARD, CardAction.GUARD));
			Card aoeSlash = new Card("광역베기", "Skill", 0, new Color(0x7A3A0A),
					"광역 피해 **" + steve.getTotalAttackPower() + "** 입힘", CardType.AOE, CardAction.AOE_SLASH);
			if (isCardAvailable(aoeSlash))
				deckCards.add(aoeSlash);

			ActiveSkill[] skills = steve.getActiveSkills();
			if (skills != null) {
				for (ActiveSkill skill : skills) {
					if (skill == null)
						continue;
					String skillId = skill.getId();
					if ("SnowBall".equals(skillId)) {
						Card snowBall = new Card(skill.getName(), "Skill", 0, new Color(0x1D5F8F), "적 대상 **스턴**", "",
								CardType.ICE, skill);
						if (isCardAvailable(snowBall))
							deckCards.add(snowBall);
					} else if ("FireCharge".equals(skillId)) {
						Card fireCharge = new Card(skill.getName(), "Skill", 0, new Color(0xA63D16), "적 대상 **화상**", "",
								CardType.FIRE, skill);
						if (isCardAvailable(fireCharge))
							deckCards.add(fireCharge);
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
			if (!discardTimer.isRunning())
				discardTimer.start();
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
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			drawCards(g2);
			if (hoveredCard != null)
				drawTooltip(g2, hoveredCard);
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

				java.awt.geom.AffineTransform oldTransform = g2.getTransform();
				if (lifted) {
					double scaleX = (double) drawW / cardW;
					double scaleY = (double) drawH / cardH;
					g2.translate(drawX, cy - (int) (28 * discardProgress));
					g2.scale(scaleX, scaleY);
					drawCard(g2, c, 0, 0, cardW, cardH, lifted, 1f - discardProgress);
				} else {
					drawCard(g2, c, drawX, cy - (int) (28 * discardProgress), cardW, cardH, lifted,
							1f - discardProgress);
				}
				g2.setTransform(oldTransform);
			}
		}

		private void drawCard(Graphics2D g2, Card card, int x, int y, int w, int h, boolean highlighted, float alpha) {
			Composite oldComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));

			ImageIcon frameIcon = getCardFrameIcon(card);
			if (frameIcon != null && frameIcon.getIconWidth() > 0) {
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(frameIcon.getImage(), x, y, w, h, null);
			} else {
				drawHotbarSlot(g2, x, y, w, h);
			}

			int bx = x + (int) (w * 0.01);
			int by = y + (int) (h * 0.01);
			int bw = (int) (w * 0.22);
			int bh = (int) (h * 0.19);

			int cooldown = getCardCooldown(card);
			g2.setFont(pixelFont(11));
			String badge = String.valueOf(cooldown);
			FontMetrics badgeMetrics = g2.getFontMetrics();
			int badgeCenterX = bx + bw / 2 - badgeMetrics.stringWidth(badge) / 2 + 5;
			int badgeCenterY = by + bh / 2 + badgeMetrics.getAscent() / 2;
			g2.setColor(new Color(0, 0, 0, 160));
			g2.drawString(badge, badgeCenterX + 1, badgeCenterY + 1);
			g2.setColor(Color.WHITE);
			g2.drawString(badge, badgeCenterX, badgeCenterY);

			g2.setFont(pixelFont(9));
			FontMetrics nameFm = g2.getFontMetrics();
			int nameX = x + (w - nameFm.stringWidth(card.name)) / 2;
			int nameY = by + bh / 2 + nameFm.getAscent() / 2 + 2;

			g2.setColor(new Color(0, 0, 0, 160));
			g2.drawString(card.name, nameX + 1, nameY + 1);
			g2.setColor(Color.WHITE);
			g2.drawString(card.name, nameX, nameY);

			int ix = x + (int) (w * 0.12);
			int iy = y + (int) (h * 0.16);
			int iw = (int) (w * 0.76);
			int ih = (int) (h * 0.52);
			drawCardArt(g2, card, ix, iy, iw, ih);

			int nameBarCenterY = y + (int) (h * 0.756);
			g2.setFont(pixelFont(10));
			g2.setColor(Color.WHITE);
			drawShadowedCentered(g2, card.type, x + 5, nameBarCenterY, w - 10);

			int descY = y + (int) (h * 0.885);
			g2.setFont(FontManager.getNeoDgm(8));
			drawMarkedWrappedCentered(g2, card.description, x + 8, descY, w - 16, 9, 2);

			if (highlighted) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				int pad = 6;
				for (int i = pad; i >= 1; i--) {
					float t = (pad - i) / (float) pad;
					int ga = Math.min(255, Math.max(0, (int) (t * 160)));
					int gr = Math.min(255, Math.max(0, (int) (180 + t * 30)));
					int gg = Math.min(255, Math.max(0, (int) (160 + t * 25)));
					int gb = Math.min(255, Math.max(0, (int) (60 + t * 20)));
					g2.setStroke(new BasicStroke(1.2f));
					g2.setColor(new Color(gr, gg, gb, ga));
					g2.drawRect(x - i, y - i, w + i * 2 - 1, h + i * 2 - 1);
				}
				g2.setStroke(new BasicStroke(1f));
				g2.setColor(new Color(220, 195, 85, 200));
				g2.drawRect(x, y, w - 1, h - 1);
			}

			g2.setComposite(oldComposite);
		}

		private ImageIcon getCardFrameIcon(Card card) {
			return cardFrame;
		}

		private void drawCardArt(Graphics2D g2, Card card, int x, int y, int w, int h) {
			int mx = x + w / 2, my = y + h / 2;
			if (card.cardType == CardType.ATTACK) {
				drawIcon(g2, atkIcon, x + 4, y + 2, w - 8, h - 4);
				return;
			}
			if (card.cardType == CardType.GUARD) {
				drawIcon(g2, defIcon, x + 4, y + 2, w - 8, h - 4);
				return;
			}
			if (card.cardType == CardType.AOE) {
				drawIcon(g2, aoeSlashIcon, x + 4, y + 2, w - 8, h - 4);
				return;
			}
			if (card.cardType == CardType.ICE && snowBallIcon != null) {
				drawIcon(g2, snowBallIcon, x + 4, y + 2, w - 8, h - 4);
				return;
			}
			if (card.cardType == CardType.FIRE && fireChargeIcon != null) {
				drawIcon(g2, fireChargeIcon, x + 4, y + 2, w - 8, h - 4);
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
				GradientPaint flame = new GradientPaint(mx, y, new Color(255, 225, 72), mx, y + h,
						new Color(210, 54, 24));
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
			if (card.tooltip == null || card.tooltip.isEmpty() || card.bounds == null)
				return;
			int tw = 220, th = 34;
			int tx = card.bounds.x + card.bounds.width + 8;
			if (tx + tw > getWidth()) {
				tx = card.bounds.x - tw - 8;
			}
			int ty = card.bounds.y + 6;
			if (ty < 4)
				ty = 4;

			g2.setColor(new Color(12, 12, 12, 232));
			g2.fillRect(tx, ty, tw, th);
			g2.setColor(new Color(235, 235, 235, 160));
			g2.drawRect(tx, ty, tw - 1, th - 1);
			g2.setFont(pixelFont(10));
			g2.setColor(Color.WHITE);
			g2.drawString(trimToWidth(g2, card.tooltip, tw - 18), tx + 9, ty + 22);
		}

		private Font pixelFont(int size) {
			return FontManager.getNeoDgm(size);
		}
	}

	private class PlayerSidebarPanel extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.dispose();
		}
	}

	private class ItemPanel extends JPanel {
		private final List<ItemSlot> slots = new ArrayList<>();

		ItemPanel() {
			setOpaque(false);
			setPreferredSize(new Dimension(106, 120));
			setMaximumSize(new Dimension(106, 120));
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (inputLocked)
						return;
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
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			drawTitleText(g2, "ITEMS", getWidth() / 2, 27, 11);
			slots.clear();
			drawConsumableSlot(g2, "HealPotion", healPotionIcon, 7, 28);
			drawConsumableSlot(g2, "AttackPotion", attackPotionIcon, 7, 73);
			g2.dispose();
		}

		private void drawConsumableSlot(Graphics2D g2, String className, ImageIcon icon, int x, int y) {
			int qty = getConsumableQuantity(className);
			Rectangle bounds = new Rectangle(x, y, 96, 44);
			slots.add(new ItemSlot(className, bounds));

			drawIcon(g2, icon, x + 5, y + 5, 34, 34);
			drawPanelValue(g2, "x" + qty, x + 58, y + 29, Color.WHITE, 13);

			if (qty <= 0) {
				g2.setColor(new Color(0, 0, 0, 110));
				g2.fillRect(x + 5, y + 5, 34, 34);
			}
		}
	}

	private class EquipmentPanel extends JPanel {
		EquipmentPanel() {
			setOpaque(false);
			setPreferredSize(new Dimension(58, 58));
			setMaximumSize(new Dimension(58, 58));
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			String weaponName = steve.getWeapon() == null ? "None" : steve.getWeapon().getName();
			Rectangle weaponBounds = new Rectangle(7, 2, 44, 44);
			if (weaponBounds.contains(event.getPoint()))
				return weaponName + "  공격력 +" + getWeaponAttackBonus();
			return null;
		}

		@Override
		public Point getToolTipLocation(MouseEvent event) {
			return new Point(54, 2);
		}

		@Override
		public JToolTip createToolTip() {
			return createHotbarToolTip(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int x = 7;
			int y = 2;

			String weaponId = steve.getWeapon() == null ? "None" : steve.getWeapon().getId();
			ImageIcon weaponIcon = BattleView.loadScaledIcon("resources/shop/" + weaponId + ".png", 34, 34);
			drawHotbarSlot(g2, x, y, 44, 44);
			drawIcon(g2, weaponIcon, x + 5, y + 5, 34, 34);

			g2.dispose();
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
		private static final int STATS_TITLE_BASELINE = 32;
		private static final int STATS_BASE_X = 7;
		private static final int STATS_FIRST_ROW_Y = 34;
		private static final int STATS_ROW_GAP = 42;
		private static final int STATS_DEF_ROW_EXTRA_Y = 4;
		private static final int STATS_HP_ROW_EXTRA_Y = 0;

		private static final int STATS_ROW_WIDTH = 96;
		private static final int STATS_ROW_HEIGHT = 44;

		private static final int STATS_ICON_X_OFFSET = 5;
		private static final int STATS_ATK_ICON_Y_OFFSET = 3;
		private static final int STATS_DEF_ICON_Y_OFFSET = 3;
		private static final int STATS_ICON_SIZE = 34;

		private static final int STATS_TEXT_X_OFFSET = 49;
		private static final int STATS_LABEL_Y_OFFSET = 20;
		private static final int STATS_VALUE_Y_OFFSET = 40;
		private static final int STATS_LABEL_FONT_SIZE = 9;

		private static final int STATS_HEART_X_OFFSET = 10;
		private static final int STATS_HEART_Y_OFFSET = 18;
		private static final int STATS_HEART_SIZE = 30;
		private static final int STATS_TOOLTIP_X = 54;

		HeartPanel() {
			setOpaque(false);
			setPreferredSize(new Dimension(106, 166));
			setMaximumSize(new Dimension(106, 166));
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			if (statsRowBounds(0).contains(p))
				return "공격력 " + steve.getTotalAttackPower();
			if (statsRowBounds(1).contains(p))
				return "방어력 " + steve.getDefencePower();
			if (statsRowBounds(2).contains(p))
				return "최대체력 " + steve.getMaxHealth();
			return null;
		}

		@Override
		public Point getToolTipLocation(MouseEvent event) {
			Point p = event.getPoint();
			for (int row = 0; row < 3; row++) {
				Rectangle bounds = statsRowBounds(row);
				if (bounds.contains(p))
					return new Point(STATS_TOOLTIP_X, bounds.y);
			}
			return new Point(STATS_TOOLTIP_X, 12);
		}

		@Override
		public JToolTip createToolTip() {
			return createHotbarToolTip(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			drawTitleText(g2, "STATS", getWidth() / 2, STATS_TITLE_BASELINE, 11);
			int x = STATS_BASE_X;
			int atkY = statsRowY(0);
			int defY = statsRowY(1);
			int hpY = statsRowY(2);
			int iconX = x + STATS_ICON_X_OFFSET;
			int textX = x + STATS_TEXT_X_OFFSET;

			drawIcon(g2, atkIcon, iconX, atkY + STATS_ATK_ICON_Y_OFFSET, STATS_ICON_SIZE, STATS_ICON_SIZE);
			drawPanelLabel(g2, "ATK", textX, atkY + STATS_LABEL_Y_OFFSET, STATS_LABEL_FONT_SIZE);
			drawPanelValue(g2, String.valueOf(steve.getTotalAttackPower()), textX, atkY + STATS_VALUE_Y_OFFSET,
					new Color(255, 116, 72), 13);

			drawIcon(g2, defIcon, iconX, defY + STATS_DEF_ICON_Y_OFFSET, STATS_ICON_SIZE, STATS_ICON_SIZE);
			drawPanelLabel(g2, "DEF", textX, defY + STATS_LABEL_Y_OFFSET, STATS_LABEL_FONT_SIZE);
			drawPanelValue(g2, String.valueOf(steve.getDefencePower()), textX, defY + STATS_VALUE_Y_OFFSET,
					new Color(124, 214, 255), 13);

			drawHeartSlot(g2, x, hpY);
			g2.dispose();
		}

		private int statsRowY(int row) {
			int y = STATS_FIRST_ROW_Y + STATS_ROW_GAP * row;
			if (row == 1)
				return y + STATS_DEF_ROW_EXTRA_Y;
			if (row == 2)
				return y + STATS_HP_ROW_EXTRA_Y;
			return y;
		}

		private Rectangle statsRowBounds(int row) {
			return new Rectangle(STATS_BASE_X, statsRowY(row), STATS_ROW_WIDTH, STATS_ROW_HEIGHT);
		}

		private double getHealthRatio() {
			if (steve.getMaxHealth() <= 0)
				return 0;
			return Math.max(0, Math.min(1, (double) steve.getHealth() / steve.getMaxHealth()));
		}

		private void drawHeartSlot(Graphics2D g2, int x, int y) {
			int textX = x + STATS_TEXT_X_OFFSET;
			drawPixelHeart(g2, x + STATS_HEART_X_OFFSET, y + STATS_HEART_Y_OFFSET, STATS_HEART_SIZE, getHealthRatio());
			drawPanelLabel(g2, "HP", textX, y + STATS_LABEL_Y_OFFSET + 8, STATS_LABEL_FONT_SIZE);
			drawPanelValue(g2, String.valueOf(steve.getHealth()), textX, y + STATS_VALUE_Y_OFFSET + 8,
					new Color(255, 70, 70), 13);
		}

		private void drawPixelHeart(Graphics2D g2, int x, int y, int size, double fillRatio) {
			int unit = Math.max(1, size / 8);
			int[][] pixels = { { 1, 0 }, { 2, 0 }, { 5, 0 }, { 6, 0 }, { 0, 1 }, { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 },
					{ 5, 1 }, { 6, 1 }, { 7, 1 }, { 0, 2 }, { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 5, 2 }, { 6, 2 },
					{ 7, 2 }, { 1, 3 }, { 2, 3 }, { 3, 3 }, { 4, 3 }, { 5, 3 }, { 6, 3 }, { 2, 4 }, { 3, 4 }, { 4, 4 },
					{ 5, 4 }, { 3, 5 }, { 4, 5 } };

			g2.setColor(new Color(70, 70, 70));
			for (int[] p : pixels) {
				g2.fillRect(x + p[0] * unit, y + p[1] * unit, unit, unit);
			}

			int fillColumns = (int) Math.ceil(8 * fillRatio);
			g2.setColor(new Color(224, 34, 34));
			for (int[] p : pixels) {
				if (p[0] < fillColumns) {
					g2.fillRect(x + p[0] * unit, y + p[1] * unit, unit, unit);
				}
			}

			g2.setColor(new Color(122, 0, 0));
			for (int[] p : pixels) {
				g2.drawRect(x + p[0] * unit, y + p[1] * unit, unit, unit);
			}
			if (fillRatio > 0) {
				g2.setColor(new Color(255, 136, 136));
				g2.fillRect(x + 1 * unit, y + unit, unit, unit);
			}
		}
	}

	private static class CoinLabel extends JLabel {
		private final ImageIcon coinIcon;

		CoinLabel() {
			super("", SwingConstants.RIGHT);
			setFont(new Font("Monospaced", Font.BOLD, 18));
			setForeground(new Color(255, 224, 82));
			setOpaque(false);
			coinIcon = loadScaledIcon(COIN_ICON_PATH, 18, 18);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			FontMetrics fm = g2.getFontMetrics(getFont());
			int textW = fm.stringWidth(getText());
			int icon = 24;
			int gap = 6;
			int totalW = icon + gap + textW;
			int x = Math.max(0, (getWidth() - totalW) / 2);
			int y = (getHeight() - icon) / 2;

			if (coinIcon != null) {
				g2.drawImage(coinIcon.getImage(), x, y, icon, icon, this);
			}
			g2.setFont(getFont());
			int baseline = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
			g2.setColor(new Color(0, 0, 0, 210));
			g2.drawString(getText(), x + icon + gap + 2, baseline + 2);
			g2.setColor(getForeground());
			g2.drawString(getText(), x + icon + gap, baseline);
			g2.dispose();
		}
	}

	static ImageIcon loadScaledIcon(String path, int width, int height) {
		if (path.startsWith("resources/shop/") && path.endsWith(".png")) {
			String cleanPath = path.substring(0, path.length() - 4) + "_clean.png";
			if (new java.io.File(cleanPath).exists()) {
				path = cleanPath;
			}
		}
		String cacheKey = path + "#" + width + "x" + height;
		ImageIcon cached = SCALED_ICON_CACHE.get(cacheKey);
		if (cached != null)
			return cached;

		ImageIcon icon = loadIcon(path);
		if (icon.getIconWidth() <= 0)
			return null;

		Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		ImageIcon scaledIcon = new ImageIcon(scaled);
		SCALED_ICON_CACHE.put(cacheKey, scaledIcon);
		return scaledIcon;
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
			Color base = getModel().isPressed() ? new Color(50, 45, 65)
					: getModel().isRollover() ? new Color(118, 108, 132) : new Color(78, 72, 94);
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
		effectBurnIcon = loadIcon("resources/ui/effect_burn.png");
		effectStunIcon = loadIcon("resources/ui/effect_stun.png");

		this.scopeImage = loadIcon("resources/ui/target_v2.png").getImage();

		String[] paths = MOB_IMAGE_MAP.get(mob.getName());
		if (paths != null) {
			mobNormalIcon = loadIcon(paths[0]);
			mobHurtIcon = loadIcon(paths[1]);
		} else {
			mobNormalIcon = null;
			mobHurtIcon = null;
		}
	}

	private static ImageIcon loadIcon(String path) {
		ImageIcon cached = ICON_CACHE.get(path);
		if (cached != null)
			return cached;
		try {
			ImageIcon ic = new ImageIcon(path);
			if (ic.getIconWidth() > 0) {
				ICON_CACHE.put(path, ic);
				return ic;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	private void updateDragonPhaseImage(boolean animate) {
		if (!"\uC5D4\uB354\uB4DC\uB798\uACE4".equals(mob.getName()))
			return;

		double hpRatio = mob.getMaxHealth() <= 0 ? 0 : (double) mob.getHealth() / mob.getMaxHealth();
		int nextPhase;
		if (hpRatio > 0.75) {
			nextPhase = 0;
		} else if (hpRatio > 0.45) {
			nextPhase = 1;
		} else {
			nextPhase = 2;
		}

		if (nextPhase == dragonPhase)
			return;
		dragonPhase = nextPhase;

		mobNormalIcon = loadIcon("resources/monster/enderdragon_phase" + dragonPhase + "_clean.png");
		mobHurtIcon = mobNormalIcon;
		if (mobBattlePanel != null) {
			mobBattlePanel.setImages(mobNormalIcon != null ? mobNormalIcon.getImage() : null,
					mobHurtIcon != null ? mobHurtIcon.getImage() : null);
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
			if (mobBattlePanel != null)
				mobBattlePanel.repaint();
			if (phaseFlashAlpha <= 0f) {
				phaseFlashTimer.stop();
			}
		});
		phaseFlashTimer.start();
		if (mobBattlePanel != null) {
			mobBattlePanel.playShake();

			int cx = mobBattlePanel.getWidth() / 2;
			int cy = mobBattlePanel.getHeight() / 2;
			int mobW = (int) (mobBattlePanel.getWidth() * 0.4);
			int mobH = (int) (mobBattlePanel.getHeight() * 0.5);
			mobBattlePanel.playSpawnParticles(cx, cy, mobW, mobH);
		}
		showMessage("엔더드래곤 " + (dragonPhase + 1) + "페이즈 돌입!");
		System.out.println("[엔더드래곤] 페이즈 " + (dragonPhase + 1) + "로 전환되었습니다!");
	}

	private static void drawIcon(Graphics2D g2, ImageIcon icon, int x, int y, int w, int h) {
		if (icon == null || icon.getIconWidth() <= 0)
			return;

		int iw = icon.getIconWidth();
		int ih = icon.getIconHeight();

		double scale = Math.min((double) w / iw, (double) h / ih);
		int dw = Math.max(1, (int) (iw * scale));
		int dh = Math.max(1, (int) (ih * scale));
		int dx = x + (w - dw) / 2;
		int dy = y + (h - dh) / 2;

		Object oldHint = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(icon.getImage(), dx, dy, dw, dh, null);
		if (oldHint != null) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
		}
	}

	private static void drawHotbarSlot(Graphics2D g2, int x, int y, int w, int h) {
		g2.setColor(new Color(0, 0, 0, 72));
		g2.fillRect(x + 2, y + 2, w, h);
		g2.setColor(HOTBAR_SLOT);
		g2.fillRect(x, y, w, h);
		g2.setColor(HOTBAR_INNER);
		g2.fillRect(x + 4, y + 4, Math.max(0, w - 8), Math.max(0, h - 8));
		g2.setColor(new Color(0, 0, 0, 220));
		g2.drawRect(x, y, w - 1, h - 1);
		g2.setColor(new Color(255, 255, 255, 118));
		g2.drawRect(x + 1, y + 1, Math.max(0, w - 3), Math.max(0, h - 3));
		g2.setColor(new Color(0, 0, 0, 128));
		g2.drawRect(x + 3, y + 3, Math.max(0, w - 7), Math.max(0, h - 7));
		g2.setColor(new Color(255, 255, 255, 42));
		g2.drawRect(x + 5, y + 5, Math.max(0, w - 11), Math.max(0, h - 11));
	}

	private static Font uiPixelFont(int size) {
		return uiPixelFont(Font.BOLD, size);
	}

	private static Font uiPixelFont(int style, int size) {
		if (pixelFontBase == null) {
			try {
				pixelFontBase = Font.createFont(Font.TRUETYPE_FONT, new File(PIXEL_FONT_PATH));
				GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(pixelFontBase);
			} catch (Exception e) {
				pixelFontBase = new Font("Monospaced", Font.BOLD, 12);
			}
		}
		return pixelFontBase.deriveFont(style, (float) size);
	}

	private static Font koreanPixelFont(int size) {
		return FontManager.getNeoDgm(size);
	}

	private static void drawPanelLabel(Graphics2D g2, String text, int x, int baseline) {
		drawPanelLabel(g2, text, x, baseline, 10);
	}

	private static void drawPanelLabel(Graphics2D g2, String text, int x, int baseline, int size) {
		g2.setFont(uiPixelFont(size));
		g2.setColor(new Color(0, 0, 0, 190));
		g2.drawString(text, x + 2, baseline + 2);
		g2.setColor(new Color(150, 150, 150));
		g2.drawString(text, x, baseline);
	}

	private static void drawPanelValue(Graphics2D g2, String text, int x, int baseline, Color color, int size) {
		g2.setFont(uiPixelFont(size));
		g2.setColor(new Color(0, 0, 0, 210));
		g2.drawString(text, x + 2, baseline + 2);
		g2.setColor(color);
		g2.drawString(text, x, baseline);
	}

	private static void drawTitleText(Graphics2D g2, String text, int centerX, int baseline, int size) {
		g2.setFont(uiPixelFont(size));
		FontMetrics fm = g2.getFontMetrics();
		int x = centerX - fm.stringWidth(text) / 2;
		g2.setColor(new Color(0, 0, 0, 220));
		g2.drawString(text, x + 2, baseline + 2);
		g2.setColor(new Color(255, 202, 92));
		g2.drawString(text, x, baseline);
	}

	private static JToolTip createHotbarToolTip(JComponent owner) {
		JToolTip tip = new JToolTip() {
			@Override
			public Dimension getPreferredSize() {
				Dimension d = super.getPreferredSize();
				return new Dimension(Math.min(170, Math.max(96, d.width + 8)), 34);
			}
		};
		tip.setComponent(owner);
		tip.setFont(uiPixelFont(11));
		tip.setForeground(Color.WHITE);
		tip.setBackground(new Color(18, 18, 18, 232));
		tip.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(235, 235, 235, 150), 1),
						BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		tip.setOpaque(true);
		return tip;
	}

	private JPanel buildTopHud() {
		JPanel panel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				drawTitleText(g2, "LV " + steve.getLevel(), getWidth() / 2 + TOP_HUD_LEVEL_X_OFFSET,
						Math.round(getHeight() * 0.535f), 12);
				g2.dispose();
			}
		};
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(0, 64));

		expLabel = new JLabel("", SwingConstants.RIGHT);
		expLabel.setFont(uiPixelFont(11));
		expLabel.setForeground(new Color(126, 255, 70));
		expLabel.setOpaque(false);

		expBar = new JProgressBar(0, 100) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				int w = getWidth();
				int barH = getHeight();
				int y = 0;

				double ratio = getMaximum() == 0 ? 0 : (double) getValue() / getMaximum();
				int fillW = (int) (w * ratio);
				g2.setColor(new Color(54, 146, 36));
				g2.fillRect(0, y, fillW, barH);
				g2.setColor(XP_GREEN);
				g2.fillRect(0, y, fillW, 2);

				int segments = 20;
				for (int i = 1; i < segments; i++) {
					int sx = Math.round(w * i / (float) segments);
					g2.setColor(new Color(0, 0, 0, 190));
					g2.fillRect(sx - 1, y, 2, barH);
					g2.setColor(new Color(170, 255, 110, 80));
					g2.fillRect(sx + 1, y, 1, Math.max(1, barH - 1));
				}
				g2.dispose();
			}
		};
		expBar.setPreferredSize(new Dimension(0, 12));
		expBar.setBorderPainted(false);
		expBar.setStringPainted(false);
		expBar.setOpaque(false);

		steveCoinLabel = new CoinLabel();

		panel.add(expBar);
		panel.add(expLabel);
		panel.add(steveCoinLabel);
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int w = panel.getWidth();
				int h = panel.getHeight();
				expBar.setBounds(scaleX(w, 156), Math.round(h * 0.54f), scaleX(w, 420), 10);
				expLabel.setBounds(scaleX(w, 558), Math.round(h * 0.47f), scaleX(w, 88), 22);
				steveCoinLabel.setBounds(scaleX(w, 718), Math.round(h * 0.47f), scaleX(w, 86), 28);
			}
		});
		return panel;
	}

	private JPanel buildPlayerSidebar() {
		JPanel panel = new PlayerSidebarPanel();
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(74, 0));
		panel.setLayout(null);

		itemPanel = new ItemPanel();
		heartPanel = new HeartPanel();

		panel.add(heartPanel);
		panel.add(itemPanel);
		panel.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int w = panel.getWidth();
				int h = Math.max(1, panel.getHeight());
				heartPanel.setBounds(Math.round(w * 28 / 146f), scaleY(h, 124), Math.round(w * 106 / 146f),
						scaleY(h, 176));
				itemPanel.setBounds(Math.round(w * 28 / 146f), scaleY(h, 326), Math.round(w * 106 / 146f),
						scaleY(h, 124));
			}
		});
		return panel;
	}

	private int getConsumableQuantity(String className) {
		ConsumableSkill[] consumables = steve.getConsumables();
		if (consumables == null)
			return 0;
		for (ConsumableSkill item : consumables) {
			if (item != null && className.equals(item.getId())) {
				return item.getQuantity();
			}
		}
		return 0;
	}

	private ConsumableSkill findConsumable(String className) {
		ConsumableSkill[] consumables = steve.getConsumables();
		if (consumables == null)
			return null;
		for (ConsumableSkill item : consumables) {
			if (item != null && className.equals(item.getId())) {
				return item;
			}
		}
		return null;
	}

	private void useConsumable(String className) {
		ConsumableSkill item = findConsumable(className);
		if (item == null || !item.hasStock()) {
			showMessage("남은 아이템이 없습니다.");
			System.out.println("[아이템] " + className + " 아이템이 남아있지 않습니다.");
			return;
		}

		if ("HealPotion".equals(className)) {
			item.use(steve);
			showMessage("회복 포션을 사용했습니다.");
			System.out.println("[아이템] 힐 포션을 사용했습니다. 현재 체력: " + steve.getHealth() + " / " + steve.getMaxHealth());
		} else if ("AttackPotion".equals(className)) {
			item.setQuantity(item.getQuantity() - 1);
			attackPotionReady = true;
			showMessage("공격 포션 사용! 다음 공격이 2배 피해를 줍니다.");
			System.out.println("[아이템] 공격 포션을 사용했습니다. 다음 공격이 2배의 피해를 입힙니다.");
		}

		refreshUI();
	}

	private boolean handleCardAction(Card card) {
		if (inputLocked)
			return false;
		if (card == null)
			return false;

		switch (card.action) {
		case ATTACK:
			playerAttack();
			break;
		case GUARD:
			steve.block();
			guardReady = true;
			showMessage(steve.getName() + " 방어!");
			System.out.println("[방어] " + steve.getName() + iGa(steve.getName()) + " 방어 태세를 취했습니다.");
			refreshUI();
			break;
		case AOE_SLASH:
			if (!useAoeSlash())
				return false;
			break;
		case ACTIVE_SKILL:
			if (!useActiveSkill(card.activeSkill))
				return false;
			if (card.activeSkill != null && card.activeSkill.skipsMobTurn()) {
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
		
		if (!creepExploding && !mob.isAlive()) {
		    handleMobDead();
		    return true;
		}
		if (!creepExploding && !steve.isAlive()) {
		    handleSteveDead();
		    return true;
		}
		
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
		showMessage(steve.getName() + "의 공격! " + mob.getName() + "에게 " + actualDmg + " 피해!");
		System.out.println("[공격] " + steve.getName() + iGa(steve.getName()) + " " + mob.getName()
				+ eulReul(mob.getName()) + " 공격했습니다! " + mob.getName() + iGa(mob.getName()) + " " + actualDmg
				+ "의 피해를 입었습니다. (남은 체력: " + mob.getHealth() + ")");
	}

	private boolean useAoeSlash() {
		AoeSlash aoeSlash = getEquippedAoeSlash();
		if (aoeSlash == null) {
			showMessage("광역 베기는 검을 장착해야 사용할 수 있습니다.");
			System.out.println("[AoeSlash] 검이 없어 AoeSlash를 사용할 수 없습니다.");
			return false;
		}

		if (!aoeSlash.isReady()) {
			showMessage("광역 베기는 아직 재사용 대기 중입니다.");
			System.out.println("[AoeSlash] 쿨다운 중입니다. 아직 사용할 수 없습니다.");
			return false;
		}

		List<Mob> targets = new ArrayList<>();
		for (Mob m : waveMobs) {
			if (m.isAlive())
				targets.add(m);
		}

		int before = mob.getHealth();
		for (Mob target : targets) {
		    aoeSlash.use(steve, target);
		}
		updateDragonPhaseImage(true);
		int actualDmg = before - mob.getHealth();

		StringBuilder sb = new StringBuilder("광역 베기! 피해 대상: ");
		StringBuilder sbConsole = new StringBuilder("[AoeSlash] 광역 공격! 피해 대상: ");
		for (int i = 0; i < targets.size(); i++) {
			if (i > 0) {
				sb.append(", ");
				sbConsole.append(", ");
			}
			sb.append(targets.get(i).getName());
			sbConsole.append(targets.get(i).getName()).append("(남은 체력: ").append(targets.get(i).getHealth())
					.append(")");
		}
		showMessage(sb.toString());
		System.out.println(sbConsole.toString());

		mobBattlePanel.showDamage(actualDmg, true);
		mobBattlePanel.playHitFlash();
		mobBattlePanel.playShake();

		removeDeadMobs();
		return true;
	}

	private void removeDeadMobs() {
		List<Mob> toRemove = new ArrayList<>();

		for (Mob currentMob : waveMobs) {
			if (currentMob.getHealth() <= 0) {
				toRemove.add(currentMob);
			}
		}

		for (Mob dead : toRemove) {
			waveManager.removeMob(dead);
			if (dead != mob) {
				System.out.println("[처치] " + dead.getName() + "이(가) 쓰러졌습니다!");
			}
		}

		if (!waveMobs.isEmpty() && !waveMobs.contains(this.mob)) {
			this.mobIndex = 0;
			this.mob = waveMobs.get(0);
		}
	}

	private AoeSlash getEquippedAoeSlash() {
		Weapon weapon = steve.getWeapon();
		return weapon == null ? null : weapon.getAoeSlash();
	}

	private boolean useActiveSkill(ActiveSkill skill) {
		if (skill == null)
			return false;
		if (!skill.isReady()) {
			showMessage(activeSkillName(skill) + "은(는) 아직 재사용 대기 중입니다.");
			System.out.println("[스킬] " + skill.getName() + "이/가 쿨다운 중입니다. 아직 사용할 수 없습니다.");
			return false;
		}

		int before = mob.getHealth();
		skill.use(steve, mob);
		updateDragonPhaseImage(true);
		int actualDmg = before - mob.getHealth();
		showMessage(activeSkillName(skill) + "을(를) 사용했습니다.");
		System.out.println("[스킬] " + skill.getName() + eulReul(skill.getName()) + " 사용했습니다. " + mob.getName()
				+ iGa(mob.getName()) + " " + actualDmg + "의 피해를 입었습니다. (남은 체력: " + mob.getHealth() + ")");
		if (actualDmg > 0) {
			mobBattlePanel.showDamage(actualDmg, true);
			mobBattlePanel.playHitFlash();
			mobBattlePanel.playShake();
		}
		return true;
	}

	private String activeSkillName(ActiveSkill skill) {
		if (skill == null)
			return "스킬";
		return skill.getName();
	}

	private void mobTurn(boolean playerBlocked) {
		int effectHpBefore = mob.getHealth();
		mob.processEffects();
		int effectDmg = effectHpBefore - mob.getHealth();
		if (effectDmg > 0) {
			mobBattlePanel.showDamage(effectDmg, new Color(255, 128, 32));
			mobBattlePanel.playHitFlash();
			showMessage(mob.getName() + "이(가) 화상으로 " + effectDmg + " 피해를 입었습니다.");
			System.out.println("[화상] " + mob.getName() + iGa(mob.getName()) + " 화상으로 " + effectDmg
					+ "의 피해를 입었습니다. (남은 체력: " + mob.getHealth() + ")");
		}
		updateDragonPhaseImage(true);
		refreshUI();
		if (!mob.isAlive()) {
			handleMobDead();
			return;
		}

		if (mob.isStunned()) {
			showMessage(mob.getName() + "은(는) 기절해서 움직일 수 없습니다.");
			System.out.println("[스턴] " + mob.getName() + iGa(mob.getName()) + " 스턴 상태로 이번 턴 공격하지 못합니다.");
			mob.setStunned(false);
			steve.onTurnEnd();
			decrementAoeSlashCooldown();
			refreshUI();
			return;
		}

		List<Mob> attackers = new ArrayList<>();
		for (Mob m : waveMobs) {
			if (m.isAlive() && m != mob) { 
				attackers.add(m);
			}
		}

		if (playerBlocked) {
			showMessage(steve.getName() + "이(가) 방어 자세를 취해 적들의 공격을 대비합니다!");
			System.out.println("[방어] " + steve.getName() + "이(가) 방어 자세를 취합니다.");
			
			int originalDef = steve.getDefencePower();
			steve.setDefencePower(originalDef + 15);
			
			for (Mob attacker : attackers) {
				int hpBefore = steve.getHealth();
				attacker.act(steve);
				int dmg = hpBefore - steve.getHealth(); 
				
				showMessage(attacker.getName() + "의 공격! " + steve.getName() + "이(가) " + dmg + " 피해를 입었습니다.");
				System.out.println("[몹 공격] " + attacker.getName() + iGa(attacker.getName()) + " " + steve.getName()
						+ eulReul(steve.getName()) + " 공격했습니다! " + dmg + "의 피해를 입었습니다. (남은 체력: " + steve.getHealth()
						+ ")");
				mobBattlePanel.showDamage(dmg, false);
			}
			
			steve.setDefencePower(originalDef);
			
		} else {
			int steveHpBefore = steve.getHealth();
			mob.act(steve);
			int actualDmg = steveHpBefore - steve.getHealth();
			
			showMessage(mob.getName() + "의 공격! " + steve.getName() + "이(가) " + actualDmg + " 피해를 입었습니다.");
			System.out.println("[몹 공격] " + mob.getName() + iGa(mob.getName()) + " " + steve.getName()
					+ eulReul(steve.getName()) + " 공격했습니다! " + steve.getName() + iGa(steve.getName()) + " " + actualDmg
					+ "의 피해를 입었습니다. (남은 체력: " + steve.getHealth() + ")");
			mobBattlePanel.showDamage(actualDmg, false);

			if ("크리퍼".equals(mob.getName())) {
			    int remaining = getCreepRemainingTurns(mob);
			    if (!mob.isAlive()) {
			        inputLocked = true;
			        creepExploding = true;  
			        mobBattlePanel.playExplosionEffect(() -> {
			            creepExploding = false;  
			            inputLocked = false;
			            handleMobDead();
			        });
			        return;
			    } else if (remaining == 0) {
			        mobBattlePanel.playCreepFlash(null);
			        showMessage("크리퍼가 부풀어 오르고 있다!");
			    }
			}
			
			for (Mob attacker : attackers) {
				int hpBefore = steve.getHealth();
				attacker.act(steve);
				int dmg = hpBefore - steve.getHealth();
				
				showMessage(attacker.getName() + "의 공격! " + steve.getName() + "이(가) " + dmg + " 피해를 입었습니다.");
				System.out.println("[몹 공격] " + attacker.getName() + iGa(attacker.getName()) + " " + steve.getName()
						+ eulReul(steve.getName()) + " 공격했습니다! " + dmg + "의 피해를 입었습니다. (남은 체력: " + steve.getHealth()
						+ ")");
				mobBattlePanel.showDamage(dmg, false);
				
				if ("크리퍼".equals(attacker.getName())) {
				    int remaining = getCreepRemainingTurns(attacker);
				    if (!attacker.isAlive()) {
				        inputLocked = true;
				        creepExploding = true; 
				        mobBattlePanel.playExplosionEffect(() -> {
				            creepExploding = false;  
				            inputLocked = false;
				        });
				    } else if (remaining == 0) {
				        mobBattlePanel.playCreepFlash(null);
				        showMessage("크리퍼가 부풀어 오르고 있다!");
				    }
				}
			}
		}

		steve.onTurnEnd();
		decrementAoeSlashCooldown();
		refreshUI();
		if (!mob.isAlive()) {
			handleMobDead();
			return;
		}
		if (!steve.isAlive()) {
			handleSteveDead();
			return;
		}

	}

	private int getCreepRemainingTurns(Mob m) {
	    Mobability ability = m.getAbility();
	    if (ability instanceof Explode) {
	        return ((Explode) ability).getBeforeExplosionTurns();
	    }
	    return -1;
	}

	private void playCreepExplosionEffect() {
	    JPanel flash = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            g.setColor(new Color(80, 255, 80, 180));
	            g.fillRect(0, 0, getWidth(), getHeight());
	        }
	    };
	    flash.setOpaque(false);
	    flash.setBounds(0, 0, getWidth(), getHeight());
	    add(flash);
	    setComponentZOrder(flash, 0);
	    repaint();

	    mobBattlePanel.showDamage(0, new Color(80, 255, 80)); // 폭발 위치에 이펙트용
	    mobBattlePanel.playShake();

	    javax.swing.Timer removeFlash = new javax.swing.Timer(300, e -> {
	        remove(flash);
	        repaint();
	    });
	    removeFlash.setRepeats(false);
	    removeFlash.start();
	}

	private void decrementAoeSlashCooldown() {
		AoeSlash aoeSlash = getEquippedAoeSlash();
		if (aoeSlash != null)
			aoeSlash.decrementCooldown();
	}

	private void resetSkillCooldownsForWave() {
		AoeSlash aoeSlash = getEquippedAoeSlash();
		if (aoeSlash != null)
			aoeSlash.resetCooldown();

		ActiveSkill[] skills = steve.getActiveSkills();
		if (skills == null)
			return;
		for (ActiveSkill skill : skills) {
			if (skill != null) {
				writeIntField(skill, "currentCooldown", 0);
			}
		}
	}

	private void handleMobDead() {

		if (inputLocked)
			return;
		inputLocked = true;

		int coin = mob.getDropCoin();
		int exp = mob.getDropExp();
		steve.gainCoin(coin);
		steve.gainExp(exp);

		if (steve.hasPendingLevelUp())
			showLevelUpDialog();

		showMessage(mob.getName() + " 처치!");
		System.out.println("[처치] " + mob.getName() + eulReul(mob.getName()) + " 처치했습니다! 코인 +" + coin + ", 경험치 +" + exp
				+ " (현재 코인: " + steve.getCoin() + ", 경험치: " + steve.getExp() + ")");
		refreshUI();

		waveManager.removeMob(mob);

		mobBattlePanel.playDeathAnimation(() -> {
			mobIndex++;

			while (mobIndex < waveMobs.size() && !waveMobs.get(mobIndex).isAlive()) {
				mobIndex++;
			}

			if (mobIndex < waveMobs.size()) {
				mob = waveMobs.get(mobIndex);
				mobBattlePanel.setMobIndex(mobIndex);


				loadImages();
				showMessage("다음 적 등장: " + mob.getName() + "!");
				inputLocked = false;
				refreshUI();

			} else {
				mobBattlePanel.setMobIndex(mobIndex);
				javax.swing.Timer t = new javax.swing.Timer(800, e -> {
					if (waveManager.isLastWave()) {
						long elapsed = System.currentTimeMillis() - gameFrame.getStartTime();
						gameFrame.showEnding(steve, elapsed);
					} else {
						showVictoryDialog(coin, exp); 
					}
				});
				t.setRepeats(false);
				t.start();
			}
		});
	}

	private void handleSteveDead() {
		if (inputLocked)
			return;
		inputLocked = true;

		showMessage(steve.getName() + "이(가) 쓰러졌습니다.");
		System.out.println("[게임 오버] " + steve.getName() + iGa(steve.getName()) + " 쓰러졌습니다. 게임이 종료됩니다.");
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
		expLabel.setText("EXP " + exp + " / " + expBar.getMaximum());
		steveCoinLabel.setText(String.valueOf(steve.getCoin()));

		if (heartPanel != null)
			heartPanel.repaint();
		if (itemPanel != null)
			itemPanel.repaint();
		if (equipmentPanel != null)
			equipmentPanel.repaint();
		if (mobBattlePanel != null)
			mobBattlePanel.repaint();
		if (cardPanel != null)
			cardPanel.repaint();

		this.revalidate();
		this.repaint();
	}

	private void showLevelUpDialog() {
		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Level Up",
				Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true);

		JPanel root = new JPanel(new BorderLayout(0, 5)) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				ImageIcon frameIcon = new ImageIcon("resources/ui/levelup_frame.png");
				g2.drawImage(frameIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
				g2.dispose();
			}
		};
		root.setOpaque(false);
		root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
				.put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "close");
		root.getActionMap().put("close", new javax.swing.AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				dialog.dispose();
			}
		});

		JPanel titlePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				int cx = getWidth() / 2;

				ImageIcon titleIcon = new ImageIcon("resources/ui/levelup_title.png");
				if (titleIcon.getIconWidth() > 0) {
					int iw = titleIcon.getIconWidth();
					int ih = titleIcon.getIconHeight();
					int drawW = (int) (getWidth() * 0.45);
					int drawH = (int) (drawW * ((double) ih / iw));
					int drawX = (getWidth() - drawW) / 2;
					int drawY = 2;
					g2.drawImage(titleIcon.getImage(), drawX, drawY, drawW, drawH, this);
				}

				String sub = "보상을 하나 선택하세요!";
				g2.setFont(koreanPixelFont(12));
				FontMetrics fm = g2.getFontMetrics();
				g2.setColor(new Color(0, 0, 0, 160));
				g2.drawString(sub, cx - fm.stringWidth(sub) / 2 + 1, 61 + 1);
				g2.setColor(Color.WHITE);
				g2.drawString(sub, cx - fm.stringWidth(sub) / 2, 61);

				g2.dispose();
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(0, 65);
			}
		};
		titlePanel.setOpaque(false);

		JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
		cardsPanel.setOpaque(false);
		cardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

		ImageIcon heartIc = loadScaledIcon("resources/icon/Heart.png", 54, 54);
		ImageIcon atkIc = loadScaledIcon("resources/icon/ATK.png", 54, 54);
		ImageIcon defIc = loadScaledIcon("resources/icon/DEF.png", 54, 54);

		cardsPanel.add(buildLevelUpCard("HP 강화", "최대 체력 +10", "체력을 올리고\n전부 회복합니다.", heartIc,
				"resources/ui/levelup_card_red.png", new Color(180, 40, 40), () -> {
					steve.applyLevelUpChoice(1);
					dialog.dispose();
					refreshUI();
					if (steve.hasPendingLevelUp())
						showLevelUpDialog();
				}));

		cardsPanel.add(buildLevelUpCard("공격 강화", "공격력 +2", "기본 공격력이\n증가합니다.", atkIc,
				"resources/ui/levelup_card_orange.png", new Color(210, 110, 30), () -> {
					steve.applyLevelUpChoice(2);
					dialog.dispose();
					refreshUI();
					if (steve.hasPendingLevelUp())
						showLevelUpDialog();
				}));

		cardsPanel.add(buildLevelUpCard("방어 강화", "방어력 +1", "받는 피해를\n줄입니다.", defIc, "resources/ui/levelup_card_blue.png",
				new Color(60, 120, 210), () -> {
					steve.applyLevelUpChoice(3);
					dialog.dispose();
					refreshUI();
					if (steve.hasPendingLevelUp())
						showLevelUpDialog();
				}));

		root.add(titlePanel, BorderLayout.NORTH);
		root.add(cardsPanel, BorderLayout.CENTER);

		dialog.setContentPane(root);
		dialog.setSize(480, 320);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}

	private void showVictoryDialog(int gainedCoin, int gainedExp) {
		inputLocked = true;

		ImageIcon frameIcon = new ImageIcon("resources/ui/victory_frame.png");
		int frameW = 480;
		int frameH = (int) (frameIcon.getIconHeight() * (480.0 / frameIcon.getIconWidth()));

		JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Victory",
				Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true);

		// 배경 프레임 패널
		JPanel root = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.drawImage(frameIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
				g2.dispose();
			}
		};
		root.setPreferredSize(new Dimension(frameW, frameH));

		JPanel contentPanel = new JPanel(null) {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				int w = getWidth();
				FontMetrics fm;

				// 타이틀 이미지
				ImageIcon titleIcon = new ImageIcon("resources/ui/victory_title.png");
				if (titleIcon.getIconWidth() > 0) {
					int iw = titleIcon.getIconWidth();
					int ih = titleIcon.getIconHeight();
					int drawW = (int) (w * 0.16);
					int drawH = (int) (drawW * ((double) ih / iw));
					int drawX = (w - drawW) / 2;
					int drawY = 30;
					g2.drawImage(titleIcon.getImage(), drawX, drawY, drawW, drawH, this);

					String subTitle = "획득 전리품을 확인하세요!";
					g2.setFont(koreanPixelFont(11));
					fm = g2.getFontMetrics();
					g2.setColor(new Color(0, 0, 0, 180));
					g2.drawString(subTitle, (w - fm.stringWidth(subTitle)) / 2 + 1, drawY + drawH + 14);
					g2.setColor(new Color(200, 200, 200));
					g2.drawString(subTitle, (w - fm.stringWidth(subTitle)) / 2, drawY + drawH + 13);
				}

				g2.dispose();
			}
		};
		contentPanel.setOpaque(false);
		contentPanel.setBounds(0, 0, frameW, frameH);

		JPanel expCard = buildVictoryCard("경험치 획득", "+" + gainedExp + " EXP",
				"[현재 EXP: " + steve.getExp() + " / " + getSteveMaxExp() + "]", new Color(120, 255, 120));

		JPanel coinCard = buildVictoryCard("코인 획득", "+" + gainedCoin + " COIN", "[현재 코인: " + steve.getCoin() + "]",
				new Color(255, 225, 100));

		// x, y, w, h — 프레임 이미지 카드 위치에 맞게 조절
		expCard.setBounds(103, 102, 107, 148);
		coinCard.setBounds(270, 102, 107, 148);

		contentPanel.add(expCard);
		contentPanel.add(coinCard);

		JButton btnShop = new JButton("상점으로");
		btnShop.setFont(koreanPixelFont(13));
		btnShop.setForeground(Color.WHITE); // 글씨 색상
		btnShop.setBounds(103, 266, 127, 32);

		btnShop.setBorderPainted(false);
		btnShop.setContentAreaFilled(false);
		btnShop.setFocusPainted(false);
		btnShop.setOpaque(false);

		JButton btnNext = new JButton("다음 웨이브 ▶");
		btnNext.setFont(koreanPixelFont(13));
		btnNext.setForeground(Color.WHITE);
		btnNext.setBounds(253, 266, 127, 31);

		btnNext.setBorderPainted(false);
		btnNext.setContentAreaFilled(false);
		btnNext.setFocusPainted(false);
		btnNext.setOpaque(false);

		contentPanel.add(btnShop);
		contentPanel.add(btnNext);

		// 상점 버튼
		btnShop.addActionListener(e -> {
			dialog.dispose();
			inputLocked = false;
			if (gameFrame != null) {
				gameFrame.showShop(steve, waveManager, wave);
			}
		});

		// 다음 웨이브 버튼
		btnNext.addActionListener(e -> {
			dialog.dispose();
			inputLocked = false;
			if (gameFrame != null && waveManager != null) {
				waveManager.nextWave();
				int nextWave = waveManager.getCurrentWave();
				Mob nextMob = waveManager.getAliveMobs().get(0);
				gameFrame.showEncounter(steve, waveManager, nextMob, nextWave);
			}
		});

		root.add(contentPanel);

		dialog.setContentPane(root);
		dialog.setSize(frameW, frameH);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.setVisible(true);

	}

	private String formatElapsed(long ms) {
		long sec = ms / 1000;
		long min = sec / 60;
		sec = sec % 60;
		return String.format("%d분 %02d초", min, sec);
	}

	private JPanel buildVictoryCard(String title, String value, String status, Color valueColor) {
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setOpaque(false);

		// Title Label
		JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setFont(koreanPixelFont(11));
		titleLabel.setForeground(new Color(200, 200, 200));
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		Dimension titleSize = new Dimension(200, 20);
		titleLabel.setPreferredSize(titleSize);
		titleLabel.setMinimumSize(titleSize);
		titleLabel.setMaximumSize(titleSize);

		// Value Label
		JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
		valueLabel.setFont(uiPixelFont(16));
		valueLabel.setForeground(valueColor);
		valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		Dimension valueSize = new Dimension(200, 20);
		valueLabel.setPreferredSize(valueSize);
		valueLabel.setMinimumSize(valueSize);
		valueLabel.setMaximumSize(valueSize);

		// Status Label
		JLabel statusLabel = new JLabel(status, SwingConstants.CENTER);
		statusLabel.setFont(koreanPixelFont(9));
		statusLabel.setForeground(new Color(160, 160, 160));
		statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		statusLabel.setVerticalAlignment(SwingConstants.TOP);

		Dimension statusSize = new Dimension(200, 12);
		statusLabel.setPreferredSize(statusSize);
		statusLabel.setMinimumSize(statusSize);
		statusLabel.setMaximumSize(statusSize);

		// 컴포넌트 배치 및 간격 조절
		card.add(Box.createVerticalStrut(5));
		card.add(titleLabel);
		card.add(Box.createVerticalStrut(75));
		card.add(valueLabel);
		card.add(Box.createVerticalStrut(3));
		card.add(statusLabel);
		card.add(Box.createVerticalGlue());

		return card;
	}

	private JPanel buildLevelUpCard(String title, String statLine, String desc, ImageIcon icon, String framePath,
			Color statColor, Runnable onClick) {

		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				int w = getWidth(), h = getHeight();

				ImageIcon frameIcon = new ImageIcon(framePath);
				if (frameIcon.getIconWidth() > 0) {
					g2.drawImage(frameIcon.getImage(), 0, 0, w, h, this);
				} else {
					paintLevelUpCardFallback(g2, w, h, statColor);
				}

				int headerCenterY = (int) (h * 0.12);
				g2.setFont(koreanPixelFont(12));
				FontMetrics fm = g2.getFontMetrics();
				int tx = (w - fm.stringWidth(title)) / 2;
				g2.setColor(new Color(0, 0, 0, 160));
				g2.drawString(title, tx + 1, headerCenterY + 1);
				g2.setColor(Color.WHITE);
				g2.drawString(title, tx, headerCenterY);

				int iconX = (int) (w * 0.15);
				int iconY = (int) (h * 0.18);
				int iconW = (int) (w * 0.70);
				int iconH = (int) (h * 0.35);
				if (icon != null && icon.getIconWidth() > 0) {
					int iw = icon.getIconWidth(), ih = icon.getIconHeight();
					double scale = Math.min((double) iconW / iw, (double) iconH / ih);
					int dw = (int) (iw * scale);
					int dh = (int) (ih * scale);
					int dx = iconX + (iconW - dw) / 2;
					int dy = iconY + (iconH - dh) / 2;
					g2.drawImage(icon.getImage(), dx, dy, dw, dh, null);
				}

				int barCenterY = (int) (h * 0.60);
				g2.setFont(koreanPixelFont(10));
				fm = g2.getFontMetrics();
				tx = (w - fm.stringWidth(statLine)) / 2;
				g2.setColor(new Color(0, 0, 0, 160));
				g2.drawString(statLine, tx + 1, barCenterY + 1);
				g2.setColor(statColor);
				g2.drawString(statLine, tx, barCenterY);

				int lineY = (int) (h * 0.63);
				g2.setColor(new Color(100, 80, 50));
				g2.drawLine((int) (w * 0.2), lineY, (int) (w * 0.8), lineY);

				int descY = (int) (h * 0.70);
				g2.setFont(koreanPixelFont(9));
				fm = g2.getFontMetrics();
				g2.setColor(new Color(200, 200, 200));
				String[] lines = desc.split("\n");
				for (String line : lines) {
					g2.drawString(line, (w - fm.stringWidth(line)) / 2, descY);
					descY += 14;
				}

				g2.dispose();
			}
		};

		card.setLayout(null);
		card.setOpaque(false);
		card.setPreferredSize(new Dimension(150, 185));

		JButton selectBtn = new JButton("선택") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2.setFont(koreanPixelFont(11));
				FontMetrics fm = g2.getFontMetrics();
				int tx = (getWidth() - fm.stringWidth("선택")) / 2;
				int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
				g2.setColor(Color.WHITE);
				g2.drawString("선택", tx, ty);
				if (getModel().isRollover()) {
					g2.drawLine(tx, ty + 3, tx + fm.stringWidth("선택"), ty + 3);
				}
			}
		};

		selectBtn.setOpaque(false);
		selectBtn.setContentAreaFilled(false);
		selectBtn.setBorderPainted(false);
		selectBtn.setFocusPainted(false);
		selectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		selectBtn.setBounds(18, 162, 100, 26);

		selectBtn.addActionListener(e -> onClick.run());

		card.add(selectBtn);
		return card;
	}

	private static void paintLevelUpCardFallback(Graphics2D g2, int w, int h, Color accent) {
		g2.setColor(new Color(32, 26, 30, 235));
		g2.fillRect(0, 0, w, h);
		g2.setColor(accent.darker());
		g2.fillRect(0, 0, w, 25);
		g2.setColor(new Color(255, 255, 255, 70));
		g2.drawRect(1, 1, Math.max(0, w - 3), Math.max(0, h - 3));
		g2.setColor(new Color(0, 0, 0, 170));
		g2.drawRect(0, 0, Math.max(0, w - 1), Math.max(0, h - 1));
	}

	private int getMobDefense(Mob targetMob) {
		return readIntMethod(targetMob, 0, "getDefensePower", "getDefense", "getArmor");
	}

	private int getWeaponAttackBonus() {
		return steve.getWeapon() == null ? 0 : steve.getWeapon().getAttackBonus();
	}

	private int getSteveMaxExp() {
		return readIntMethod(steve, 100, "getMaxExp", "getRequiredExp", "getExpToNextLevel");
	}

	private boolean isCardAvailable(Card card) {
		return getCurrentCooldown(card) == 0;
	}

	private int getCardCooldown(Card card) {
		if (card == null)
			return 0;
		if (card.action == CardAction.AOE_SLASH) {
			AoeSlash aoeSlash = getEquippedAoeSlash();
			if (aoeSlash == null)
				return 0;
			int cooldown = aoeSlash.getCooldown();
			return cooldown <= 0 ? 0 : Math.max(1, cooldown - 1);
		}
		if (card.action == CardAction.ACTIVE_SKILL && card.activeSkill != null) {
			int cooldown = readIntField(card.activeSkill, 0, "cooldown");
			return cooldown <= 0 ? 0 : Math.max(1, cooldown - 1);
		}
		return 0;
	}

	private int getCurrentCooldown(Card card) {
		if (card == null)
			return 0;
		if (card.action == CardAction.AOE_SLASH) {
			AoeSlash aoeSlash = getEquippedAoeSlash();
			if (aoeSlash == null)
				return 0;
			return Math.max(0, aoeSlash.getCurrentCooldown());
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
				if (value instanceof Number)
					return ((Number) value).intValue();
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
				if (value instanceof Number)
					return ((Number) value).intValue();
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

	private void showMessage(String msg) {
		messageLabel.setText(msg);
		if (messageLabel.getParent() != null) {
			messageLabel.getParent().revalidate();
		}
	}

	private static void drawCentered(Graphics2D g2, String s, int x, int y, int w) {
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(s, x + (w - fm.stringWidth(s)) / 2, y);
	}

	private static void drawShadowedCentered(Graphics2D g2, String s, int x, int y, int w) {
		FontMetrics fm = g2.getFontMetrics();
		int tx = x + (w - fm.stringWidth(s)) / 2;
		Color old = g2.getColor();
		g2.setColor(new Color(48, 48, 48));
		g2.drawString(s, tx + 1, y + 1);
		g2.setColor(old);
		g2.drawString(s, tx, y);
	}

	private static void drawWrappedCentered(Graphics2D g2, String s, int x, int y, int w, int lineHeight) {
		drawWrappedCentered(g2, s, x, y, w, lineHeight, Integer.MAX_VALUE);
	}

	private static void drawWrappedCentered(Graphics2D g2, String s, int x, int y, int w, int lineHeight,
			int maxLines) {
		FontMetrics fm = g2.getFontMetrics();
		String[] words = s.split(" ");
		String line = "";
		int lineY = y;
		int linesDrawn = 0;
		for (String word : words) {
			String next = line.isEmpty() ? word : line + " " + word;
			if (fm.stringWidth(next) > w && !line.isEmpty()) {
				linesDrawn++;
				if (linesDrawn >= maxLines) {
					drawCentered(g2, trimToWidth(g2, line + "...", w), x, lineY, w);
					return;
				}
				drawCentered(g2, trimToWidth(g2, line, w), x, lineY, w);
				line = word;
				lineY += lineHeight;
			} else {
				line = next;
			}
		}
		if (!line.isEmpty())
			drawCentered(g2, trimToWidth(g2, line, w), x, lineY, w);
	}

	private static void drawMarkedWrappedCentered(Graphics2D g2, String s, int x, int y, int w, int lineHeight,
			int maxLines) {
		FontMetrics fm = g2.getFontMetrics();
		String[] words = s.split(" ");
		List<String> lines = new ArrayList<>();
		String line = "";
		for (String word : words) {
			String next = line.isEmpty() ? word : line + " " + word;
			if (fm.stringWidth(stripMarks(next)) > w && !line.isEmpty()) {
				lines.add(line);
				line = word;
				if (lines.size() == maxLines - 1)
					break;
			} else {
				line = next;
			}
		}
		if (!line.isEmpty() && lines.size() < maxLines)
			lines.add(line);

		for (int i = 0; i < lines.size(); i++) {
			drawMarkedCenteredLine(g2, lines.get(i), x, y + i * lineHeight, w);
		}
	}

	private static void drawMarkedCenteredLine(Graphics2D g2, String s, int x, int y, int w) {
		List<TextRun> runs = parseMarkedRuns(s);
		FontMetrics fm = g2.getFontMetrics();
		int totalW = 0;
		for (TextRun run : runs)
			totalW += fm.stringWidth(run.text);
		int tx = x + (w - totalW) / 2;
		for (TextRun run : runs) {
			g2.setColor(run.marked ? new Color(255, 223, 82) : new Color(255, 255, 245));
			g2.drawString(run.text, tx, y);
			tx += fm.stringWidth(run.text);
		}
	}

	private static String stripMarks(String s) {
		return s.replace("**", "");
	}

	private static List<TextRun> parseMarkedRuns(String s) {
		List<TextRun> runs = new ArrayList<>();
		boolean marked = false;
		StringBuilder current = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			if (i + 1 < s.length() && s.charAt(i) == '*' && s.charAt(i + 1) == '*') {
				if (current.length() > 0) {
					runs.add(new TextRun(current.toString(), marked));
					current.setLength(0);
				}
				marked = !marked;
				i++;
			} else {
				current.append(s.charAt(i));
			}
		}
		if (current.length() > 0)
			runs.add(new TextRun(current.toString(), marked));
		return runs;
	}

	private static class TextRun {
		String text;
		boolean marked;

		TextRun(String text, boolean marked) {
			this.text = text;
			this.marked = marked;
		}
	}

	private static String trimToWidth(Graphics2D g2, String text, int maxW) {
		FontMetrics fm = g2.getFontMetrics();
		if (fm.stringWidth(text) <= maxW)
			return text;
		String suffix = ".";
		String trimmed = text;
		while (trimmed.length() > 1 && fm.stringWidth(trimmed + suffix) > maxW) {
			trimmed = trimmed.substring(0, trimmed.length() - 1);
		}
		return trimmed + suffix;
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		ToolTipManager.sharedInstance().setEnabled(false);
		ToolTipManager.sharedInstance().setEnabled(true);
	}
}
