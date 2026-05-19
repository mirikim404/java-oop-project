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
	private Image scopeImage;
	private boolean attackPotionReady = false;
	private boolean guardReady = false;
	private int dragonPhase = -1;
	private float phaseFlashAlpha = 0f;
	private javax.swing.Timer phaseFlashTimer;
	private boolean inputLocked = false;

	private static final Color GOLD = new Color(230, 184, 76);
	private static final Color PANEL_DARK = new Color(12, 12, 20, 210);
	private static final Color PANEL_EDGE = new Color(52, 46, 72);
	private static final Color MC_PANEL = new Color(198, 198, 198, 236);
	private static final Color MC_PANEL_DARK = new Color(82, 82, 82, 235);
	private static final Color MC_SLOT = new Color(139, 139, 139, 235);
	private static final Color MC_SLOT_DARK = new Color(55, 55, 55, 235);
	private static final Color MC_HIGHLIGHT = new Color(255, 255, 255, 210);
	private static final Color MC_SHADOW = new Color(45, 45, 45, 230);
	private static final Color MC_TEXT = new Color(46, 46, 46);
	private static final Color HOTBAR_SLOT = new Color(118, 118, 118, 82);
	private static final Color HOTBAR_INNER = new Color(210, 210, 210, 34);
	private static final Color HOTBAR_LIGHT = new Color(255, 255, 255, 122);
	private static final Color HOTBAR_DARK = new Color(0, 0, 0, 168);
	private static final Color XP_GREEN = new Color(116, 255, 70);
	private static final String COIN_ICON_PATH = "resources/icon/Coin.png";

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

	// ─── 한국어 조사 헬퍼 ───────────────────────────────────────────────────────
	/** 이름 마지막 글자 받침 여부에 따라 은/는 반환 */
	private static String eunNun(String name) {
		if (name == null || name.isEmpty()) return "은/는";
		char last = name.charAt(name.length() - 1);
		if (last < 0xAC00 || last > 0xD7A3) return "은/는";
		return (last - 0xAC00) % 28 == 0 ? "는" : "은";
	}
	/** 이/가 */
	private static String iGa(String name) {
		if (name == null || name.isEmpty()) return "이/가";
		char last = name.charAt(name.length() - 1);
		if (last < 0xAC00 || last > 0xD7A3) return "이/가";
		return (last - 0xAC00) % 28 == 0 ? "가" : "이";
	}
	/** 을/를 */
	private static String eulReul(String name) {
		if (name == null || name.isEmpty()) return "을/를";
		char last = name.charAt(name.length() - 1);
		if (last < 0xAC00 || last > 0xD7A3) return "을/를";
		return (last - 0xAC00) % 28 == 0 ? "를" : "을";
	}
	/** 으로/로 */
	private static String euroRo(String name) {
		if (name == null || name.isEmpty()) return "으로/로";
		char last = name.charAt(name.length() - 1);
		if (last < 0xAC00 || last > 0xD7A3) return "으로/로";
		int jongseong = (last - 0xAC00) % 28;
		if (jongseong == 0) return "로";
		if (jongseong == 8) return "로"; // ㄹ 받침
		return "으로";
	}
	// ────────────────────────────────────────────────────────────────────────────

	public BattleView(GameFrame gameFrame, Steve steve, WaveManager waveManager, Mob mob, int wave) {
		this.gameFrame = gameFrame;
		this.steve = steve;
		this.wave = wave;
		this.waveManager = waveManager;

		this.waveMobs = waveManager.getMobsForWave(wave);
		this.mobIndex = 0;
		this.mob = waveMobs.isEmpty() ? mob : waveMobs.get(0);

		loadImages();
		updateDragonPhaseImage(false);
		resetSkillCooldownsForWave();

		BackgroundPanel root = new BackgroundPanel(getBackgroundPath());
		root.setLayout(new BorderLayout());
		root.setBorder(BorderFactory.createEmptyBorder(8, 10, 10, 10));

		setLayout(new BorderLayout());
		add(root, BorderLayout.CENTER);

		root.add(buildTopHud(), BorderLayout.NORTH);
		root.add(buildPlayerSidebar(), BorderLayout.WEST);

		mobBattlePanel = new MobBattlePanel(buildMobImageList());

		cardPanel = new CardPanel();
		root.add(new BattleLayerPanel(), BorderLayout.CENTER);

		refreshUI();

		if (waveMobs.size() > 1) {
			StringBuilder sb = new StringBuilder("Wave " + wave + " - 등장: ");
			for (int i = 0; i < waveMobs.size(); i++) {
				if (i > 0) sb.append(", ");
				sb.append(waveMobs.get(i).getName());
			}
			showMessage(sb.toString());
			System.out.println("[웨이브 " + wave + "] " + sb.toString());
		} else if (!waveMobs.isEmpty()) {
			showMessage("Wave " + wave + " - " + this.mob.getName() + " 등장!");
			System.out.println("[웨이브 " + wave + "] " + this.mob.getName() + iGa(this.mob.getName()) + " 등장했습니다!");
		}
	}

	private List<Image[]> buildMobImageList() {
		List<Image[]> images = new ArrayList<>();
		for (Mob m : waveMobs) {
			String[] paths = MOB_IMAGE_MAP.get(m.getName());
			Image normal = null, hurt = null;
			if (paths != null) {
				ImageIcon ni = loadIcon(paths[0]);
				ImageIcon hi = loadIcon(paths[1]);
				if (ni != null) normal = ni.getImage();
				if (hi != null) hurt = hi.getImage();
			}
			images.add(new Image[] { normal, hurt });
		}
		return images;
	}

	private String getBackgroundPath() {
		if (wave <= 2) return "resources/bg/battle_easy.png";
		if (wave == 3) return "resources/bg/battle_normal.png";
		if (wave <= 5) return "resources/bg/battle_hard.png";
		return "resources/bg/battle_boss.png";
	}

	private static class BackgroundPanel extends JPanel {
		private Image bgImage;

		BackgroundPanel(String imagePath) {
			setOpaque(true);
			try {
				ImageIcon ic = new ImageIcon(imagePath);
				if (ic.getIconWidth() > 0) bgImage = ic.getImage();
			} catch (Exception ignored) {}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			if (bgImage != null) {
				g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
			} else {
				GradientPaint gp = new GradientPaint(0, 0, new Color(31, 27, 44), 0, getHeight(), new Color(13, 13, 20));
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

			int targetY = cardY - 28;

			if (targetY < 200) {
				messageLabel.setVisible(false);
			} else {
				messageLabel.setVisible(true);
				messageLabel.setBounds(0, targetY, w, 24);
			}

			cardPanel.setBounds(cardX, cardY, cardW, cardH);
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
				if (dmgNumbers.isEmpty()) animTimer.stop();
			});
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
			if (!animTimer.isRunning()) animTimer.start();
		}

		void showDamage(int dmg, Color color) {
			float cx = getWidth() * 0.5f + (float) (Math.random() * 60 - 30);
			float cy = getHeight() * 0.40f + (float) (Math.random() * 30 - 15);
			dmgNumbers.add(new DamageNumber("-" + dmg, color, cx, cy));
			if (!animTimer.isRunning()) animTimer.start();
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
				boolean boss = "\uC5D4\uB354\uB4DC\uB798\uACE4".equals(mob.getName());
				double heightRatio = boss ? 1.08 : 0.92;
				double widthRatio = boss ? 0.58 : (count > 1 ? 0.28 : 0.44);

				int targetDx = -1, targetDy = -1, targetDw = 0, targetDh = 0;

				for (int i = 0; i < count; i++) {
					Image[] pair = mobImages.get(i);
					Image img = (showHurt && i == currentMobIndex && pair[1] != null) ? pair[1] : pair[0];
					if (img == null) continue;

					int iw = img.getWidth(null), ih = img.getHeight(null);
					if (iw <= 0 || ih <= 0) continue;

					double scale = (availableH * heightRatio) / ih;
					if (iw * scale > pw * widthRatio) scale = (pw * widthRatio) / iw;
					int dw = (int) (iw * scale);
					int dh = (int) (ih * scale);
					int maxH = Math.max(90, groundY - 18);
					if (dh > maxH) {
						double fit = (double) maxH / dh;
						dw = (int) (dw * fit);
						dh = maxH;
					}

					int slotW = pw / count;
					int dx = slotW * i + (slotW - dw) / 2;
					int dy = groundY - dh;
					
					if (count > 1) {
						int offset = 70; // 좁히고 싶은 픽셀 값 
						if (i == 0) {
							dx += offset; 
						} else if (i == 1) {
							dx -= offset; 
						}
					}

					boolean alreadyDead = i < currentMobIndex;
					boolean dyingNow = isDead && i == currentMobIndex;

					if (alreadyDead) {
						continue; 
					} else if (dyingNow) {
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, deadAlpha)));
						dy += deadDropY;
					} else {
						g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
					}

					if (i == currentMobIndex && !isDead) {
						targetDx = dx;
						targetDy = dy;
						targetDw = dw;
						targetDh = dh;
					}

					g2.drawImage(img, dx, dy, dw, dh, null);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
				}

				// 현재 타겟 몹 중앙에 스코프 이미지 포커스 표시
				if (targetDx >= 0 && scopeImage != null) {
				    int scopeW = 100;
				    int scopeH = 100;

				    // 몹별 offset 예외처리
				    int scopeOffsetX = 0;
				    if ("스켈레톤".equals(mob.getName())) {
				        scopeOffsetX = -15;
				    } else if ("위더스켈레톤".equals(mob.getName())) {
				        scopeOffsetX = +15;
				    }

				    int scopeX = targetDx + (targetDw - scopeW) / 2 + scopeOffsetX;
				    int scopeY = targetDy + (targetDh - scopeH) / 2;

				    g2.drawImage(scopeImage, scopeX, scopeY, scopeW, scopeH, null);
				}
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
		String name, type, description, tooltip;
		int cost;
		Color color;
		CardType cardType;
		CardAction action;
		ActiveSkill activeSkill;
		Rectangle bounds;

		Card(String name, String type, int cost, Color color, String description, CardType cardType, CardAction action) {
			this(name, type, cost, color, description, null, cardType, action);
		}

		Card(String name, String type, int cost, Color color, String description, String tooltip, CardType cardType, CardAction action) {
			this.name = name;
			this.type = type;
			this.cost = cost;
			this.color = color;
			this.description = description;
			this.tooltip = tooltip;
			this.cardType = cardType;
			this.action = action;
		}

		Card(String name, String type, int cost, Color color, String description, CardType cardType, ActiveSkill activeSkill) {
			this(name, type, cost, color, description, null, cardType, activeSkill);
		}

		Card(String name, String type, int cost, Color color, String description, String tooltip, CardType cardType, ActiveSkill activeSkill) {
			this(name, type, cost, color, description, tooltip, cardType, CardAction.ACTIVE_SKILL);
			this.activeSkill = activeSkill;
		}
	}

	static class CardStyle {
		static final int WIDTH = 96;
		static final int HEIGHT = 136;
		static final int GAP = 10;
		static final int ARC = 0;
		static final int HEADER_H = 22;
		static final int ART_X = 13;
		static final int ART_Y = 32;
		static final int ART_H = 54;
		static final int DESC_Y = 92;
		static final int DESC_H = 37;
		static final Color SHADOW = new Color(0, 0, 0, 80);
		static final Color ART_BG = new Color(112, 112, 112);
		static final Color DESC_BG = new Color(170, 170, 170);
		static final Color BADGE = new Color(76, 151, 48);
		static final Color BORDER = new Color(50, 50, 50);
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
			
			cardFrame = loadIcon("resources/ui/card_frame_dec.png");

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
			deckCards.add(new Card("Attack", "Basic", 0, new Color(0x8B2020),
					"물리 피해 **" + steve.getTotalAttackPower() + "** 입힘", CardType.ATTACK, CardAction.ATTACK));
			deckCards.add(new Card("Guard", "Basic", 0, new Color(0x1A3A8A),
					"다음 공격 **방어**", CardType.GUARD, CardAction.GUARD));
			Card aoeSlash = new Card("AoeSlash", "Skill", 0, new Color(0x7A3A0A),
					"광역 피해 **" + steve.getTotalAttackPower() + "** 입힘", CardType.AOE, CardAction.AOE_SLASH);
			if (isCardAvailable(aoeSlash)) deckCards.add(aoeSlash);

			ActiveSkill[] skills = steve.getActiveSkills();
			if (skills != null) {
				for (ActiveSkill skill : skills) {
					if (skill == null) continue;
					String simpleName = skill.getClass().getSimpleName();
					if ("SnowBall".equals(simpleName)) {
						Card snowBall = new Card("SnowBall", "Skill", 0, new Color(0x1D5F8F),
								"적 대상 **스턴**", "스턴: 적이 한 턴 동안 공격하지 못함", CardType.ICE, skill);
						if (isCardAvailable(snowBall)) deckCards.add(snowBall);
					} else if ("FireCharge".equals(simpleName)) {
						Card fireCharge = new Card("FireCharge", "Skill", 0, new Color(0xA63D16),
								"적 대상 **화상**", "화상: 턴마다 추가 피해", CardType.FIRE, skill);
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
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			int bandY = 74;
			g2.setColor(new Color(0, 0, 0, 38));
			g2.fillRect(16, bandY + 4, getWidth() - 32, getHeight() - bandY - 8);

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

				java.awt.geom.AffineTransform oldTransform = g2.getTransform();
				if (lifted) {
					double scaleX = (double) drawW / cardW;
					double scaleY = (double) drawH / cardH;
					g2.translate(drawX, cy - (int) (28 * discardProgress));
					g2.scale(scaleX, scaleY);
					drawCard(g2, c, 0, 0, cardW, cardH, lifted, 1f - discardProgress);
				} else {
					drawCard(g2, c, drawX, cy - (int) (28 * discardProgress), cardW, cardH, lifted, 1f - discardProgress);
				}
				g2.setTransform(oldTransform);
			}
		}

		private void drawCard(Graphics2D g2, Card card, int x, int y, int w, int h, boolean highlighted, float alpha) {
		    Composite oldComposite = g2.getComposite();
		    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, Math.min(1f, alpha))));
		 
		    // ── 1. 카드 프레임 이미지 ──────────────────────────────
		    ImageIcon frameIcon = getCardFrameIcon(card);
		    if (frameIcon != null && frameIcon.getIconWidth() > 0) {
		        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		        g2.drawImage(frameIcon.getImage(), x, y, w, h, null);
		    } else {
		        // 이미지 없을 때 fallback — 기존 슬롯 그리기
		        drawHotbarSlot(g2, x, y, w, h);
		    }
		 
		    // ── 2.최상단 헤더 ─────────────────────
		    // 이미지 기준 비율: x=1%, y=1%, w=22%, h=19%
		    int bx = x + (int)(w * 0.01);
		    int by = y + (int)(h * 0.01);
		    int bw = (int)(w * 0.22);
		    int bh = (int)(h * 0.19);
		 
		    int cooldown = getCardCooldown(card);
		    g2.setFont(pixelFont(11));
		    String badge = String.valueOf(cooldown);
		    FontMetrics badgeMetrics = g2.getFontMetrics();
		    // 좌상단 뱃지
		    int badgeCenterX = bx + bw / 2 - badgeMetrics.stringWidth(badge) / 2 + 5;
		    int badgeCenterY = by + bh / 2 + badgeMetrics.getAscent() / 2;
		    g2.setColor(new Color(0, 0, 0, 160));
		    g2.drawString(badge, badgeCenterX + 1, badgeCenterY + 1);
		    g2.setColor(Color.WHITE);
		    g2.drawString(badge, badgeCenterX, badgeCenterY);
		    // 우상단 카드 이름
		    int badgeRightX = bx + bw;           
		    int cardRightX  = x + w;            
		    int remainWidth = cardRightX - badgeRightX;  

		    g2.setFont(pixelFont(9));
		    FontMetrics nameFm = g2.getFontMetrics();
		    int nameX = badgeRightX + (remainWidth - nameFm.stringWidth(card.name)) / 2;  // 중앙정렬
		    int nameY = by + bh / 2 + nameFm.getAscent() / 2 +2;  // 뱃지 수직 중앙

		    g2.setColor(new Color(0, 0, 0, 160));
		    g2.drawString(card.name, nameX + 1, nameY + 1);
		    g2.setColor(Color.WHITE);
		    g2.drawString(card.name, nameX, nameY);
		 
		    // ── 3. 중앙 아이콘 영역 ───────────────────────────────
		    // 이미지 기준 비율: x=12%, y=16%, w=76%, h=52%
		    int ix = x + (int)(w * 0.12);
		    int iy = y + (int)(h * 0.16);
		    int iw = (int)(w * 0.76);
		    int ih = (int)(h * 0.52);
		    drawCardArt(g2, card, ix, iy, iw, ih);
		 
		    // ── 4. 하단 이름 바 텍스트 ───────────────────────────
		    // 이미지 기준: y=73~85%, 가로폭 중앙
		    int nameBarCenterY = y + (int)(h * 0.756); // 바의 수직 중앙
		    g2.setFont(pixelFont(10));
		    g2.setColor(Color.WHITE);
		    drawShadowedCentered(g2, card.type, x + 5, nameBarCenterY, w - 10);
		 
		    // ── 5. 하단 설명 텍스트 ──────────────────────────────
		    // 이미지 기준: y=88% 이하 (바 아래 작은 영역)
		    int descY = y + (int)(h * 0.885);
		    g2.setFont(pixelFont(8));
		    drawMarkedWrappedCentered(g2, card.description, x + 8, descY, w - 16, 9, 2);
		 
		    // ── 6. hover 테두리 강조 ─────────────────────────────
		    if (highlighted) {
		        g2.setStroke(new BasicStroke(3f));
		        g2.setColor(new Color(255, 255, 80));
		        g2.drawRect(x - 2, y - 2, w + 3, h + 3);
		    }
		 
		    g2.setComposite(oldComposite);
		}
		
		private ImageIcon getCardFrameIcon(Card card) {
		    return cardFrame; // ATTACK + 기본
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
			if (card.tooltip == null || card.tooltip.isEmpty() || card.bounds == null) return;
			int tw = 220, th = 34;
			int tx = card.bounds.x + card.bounds.width + 8;
		    if (tx + tw > getWidth()) {
		        tx = card.bounds.x - tw - 8; 
		    }
			int ty = card.bounds.y + 6;
			if (ty < 4) ty = 4;

			g2.setColor(new Color(12, 12, 12, 232));
			g2.fillRect(tx, ty, tw, th);
			g2.setColor(new Color(235, 235, 235, 160));
			g2.drawRect(tx, ty, tw - 1, th - 1);
			g2.setFont(pixelFont(10));
			g2.setColor(Color.WHITE);
			g2.drawString(trimToWidth(g2, card.tooltip, tw - 18), tx + 9, ty + 22);
		}

		private Font pixelFont(int size) {
			return new Font("Monospaced", Font.BOLD, size);
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
			setPreferredSize(new Dimension(58, 90));
			setMaximumSize(new Dimension(58, 90));
			setToolTipText("");
			ToolTipManager.sharedInstance().registerComponent(this);
			ToolTipManager.sharedInstance().setInitialDelay(120);
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
			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					if (getToolTipText(e) == null) {
						ToolTipManager.sharedInstance().setEnabled(false);
						ToolTipManager.sharedInstance().setEnabled(true);
					}
				}
			});
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			for (ItemSlot slot : slots) {
				if (slot.bounds.contains(event.getPoint())) {
					return slot.className + "  x" + getConsumableQuantity(slot.className);
				}
			}
			return null;
		}

		@Override
		public Point getToolTipLocation(MouseEvent event) {
			for (ItemSlot slot : slots) {
				if (slot.bounds.contains(event.getPoint())) {
					return new Point(54, slot.bounds.y);
				}
			}
			return new Point(54, 0);
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

			slots.clear();
			drawConsumableSlot(g2, "HealPotion", healPotionIcon, 7, 0);
			drawConsumableSlot(g2, "AttackPotion", attackPotionIcon, 7, 44);
			g2.dispose();
		}

		private void drawConsumableSlot(Graphics2D g2, String className, ImageIcon icon, int x, int y) {
			int qty = getConsumableQuantity(className);
			Rectangle bounds = new Rectangle(x, y, 44, 44);
			slots.add(new ItemSlot(className, bounds));

			drawHotbarSlot(g2, bounds.x, bounds.y, bounds.width, bounds.height);
			drawIcon(g2, icon, bounds.x + 5, bounds.y + 5, 34, 34);
			g2.setFont(new Font("Monospaced", Font.BOLD, 11));
			g2.setColor(Color.WHITE);
			drawItemCount(g2, String.valueOf(qty), bounds.x + bounds.width - 5, bounds.y + bounds.height - 5);

			if (qty <= 0) {
				g2.setColor(new Color(0, 0, 0, 120));
				g2.fillRect(bounds.x + 3, bounds.y + 3, bounds.width - 6, bounds.height - 6);
			}
		}
	}

	private class EquipmentPanel extends JPanel {
		EquipmentPanel() {
			setOpaque(false);
			setPreferredSize(new Dimension(58, 58));
			setMaximumSize(new Dimension(58, 58));
			setToolTipText("");
			ToolTipManager.sharedInstance().registerComponent(this);
			ToolTipManager.sharedInstance().setInitialDelay(120);

			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					if (getToolTipText(e) == null) {
						ToolTipManager.sharedInstance().setEnabled(false);
						ToolTipManager.sharedInstance().setEnabled(true);
					}
				}
			});
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			String weaponName = steve.getWeapon() == null ? "None" : steve.getWeapon().getClass().getSimpleName();
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

			String weaponName = steve.getWeapon() == null ? "None" : steve.getWeapon().getClass().getSimpleName();
			ImageIcon weaponIcon = BattleView.loadScaledIcon("resources/shop/" + weaponName + ".png", 34, 34);
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
		HeartPanel() {
			setOpaque(false);
			setPreferredSize(new Dimension(58, 134));
			setMaximumSize(new Dimension(58, 134));
			setToolTipText("");
			ToolTipManager.sharedInstance().registerComponent(this);
			ToolTipManager.sharedInstance().setInitialDelay(120);

			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					if (getToolTipText(e) == null) {
						ToolTipManager.sharedInstance().setEnabled(false);
						ToolTipManager.sharedInstance().setEnabled(true);
					}
				}
			});
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			Point p = event.getPoint();
			if (new Rectangle(7, 0, 44, 44).contains(p)) return "공격력 " + steve.getTotalAttackPower();
			if (new Rectangle(7, 44, 44, 44).contains(p)) return "방어력 " + steve.getDefencePower();
			if (new Rectangle(7, 88, 44, 44).contains(p)) return "최대체력 " + steve.getMaxHealth();
			return null;
		}

		@Override
		public Point getToolTipLocation(MouseEvent event) {
			Point p = event.getPoint();
			if (new Rectangle(7, 0, 44, 44).contains(p)) return new Point(54, 0);
			if (new Rectangle(7, 44, 44, 44).contains(p)) return new Point(54, 44);
			if (new Rectangle(7, 88, 44, 44).contains(p)) return new Point(54, 88);
			return new Point(54, 12);
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
			int y = 0;
			drawHotbarSlot(g2, x, y, 44, 44);
			drawIcon(g2, atkIcon, x + 5, y + 5, 34, 34);
			drawItemCount(g2, String.valueOf(steve.getTotalAttackPower()), x + 39, y + 39);

			drawHotbarSlot(g2, x, y + 44, 44, 44);
			drawIcon(g2, defIcon, x + 5, y + 49, 34, 34);
			drawItemCount(g2, String.valueOf(steve.getDefencePower()), x + 39, y + 83);

			drawHeartSlot(g2, x, y + 88);
			g2.dispose();
		}

		private double getHealthRatio() {
			if (steve.getMaxHealth() <= 0) return 0;
			return Math.max(0, Math.min(1, (double) steve.getHealth() / steve.getMaxHealth()));
		}

		private void drawHeartSlot(Graphics2D g2, int x, int y) {
			drawHotbarSlot(g2, x, y, 44, 44);
			drawPixelHeart(g2, x + 10, y + 8, 24, getHealthRatio());
			drawItemCount(g2, String.valueOf(steve.getHealth()), x + 39, y + 39);
		}

		private void drawPixelHeart(Graphics2D g2, int x, int y, int size, double fillRatio) {
			int unit = Math.max(1, size / 8);
			int[][] pixels = {
				{ 1, 0 }, { 2, 0 }, { 5, 0 }, { 6, 0 },
				{ 0, 1 }, { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 },
				{ 0, 2 }, { 1, 2 }, { 2, 2 }, { 3, 2 }, { 4, 2 }, { 5, 2 }, { 6, 2 }, { 7, 2 },
				{ 1, 3 }, { 2, 3 }, { 3, 3 }, { 4, 3 }, { 5, 3 }, { 6, 3 },
				{ 2, 4 }, { 3, 4 }, { 4, 4 }, { 5, 4 },
				{ 3, 5 }, { 4, 5 }
			};

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
			setFont(new Font("Monospaced", Font.BOLD, 12));
			setForeground(new Color(255, 224, 82));
			setOpaque(false);
			coinIcon = loadScaledIcon(COIN_ICON_PATH, 14, 14);
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			FontMetrics fm = g2.getFontMetrics(getFont());
			int textW = fm.stringWidth(getText());
			int icon = 14;
			int gap = 5;
			int totalW = icon + gap + textW;
			int x = getWidth() - totalW;
			int y = (getHeight() - icon) / 2 + 1;

			if (coinIcon != null) {
				g2.drawImage(coinIcon.getImage(), x, y, icon, icon, this);
			}
			g2.setFont(getFont());
			g2.setColor(getForeground());
			g2.drawString(getText(), x + icon + gap, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
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
		ImageIcon icon = new ImageIcon(path);
		if (icon.getIconWidth() <= 0) return null;

		Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
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

		mobNormalIcon = loadIcon("resources/monster/enderdragon_phase" + dragonPhase + "_clean.png");
		mobHurtIcon = mobNormalIcon;
		if (mobBattlePanel != null) {
			mobBattlePanel.setImages(
					mobNormalIcon != null ? mobNormalIcon.getImage() : null,
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
		System.out.println("[엔더드래곤] 페이즈 " + (dragonPhase + 1) + "로 전환되었습니다!");
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
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(icon.getImage(), dx, dy, dw, dh, null);
		if (oldHint != null) {
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldHint);
		}
	}

	private static void drawMinecraftWindow(Graphics2D g2, int x, int y, int w, int h) {
		g2.setColor(MC_PANEL);
		g2.fillRect(x, y, w, h);
		g2.setColor(MC_HIGHLIGHT);
		g2.fillRect(x, y, w, 3);
		g2.fillRect(x, y, 3, h);
		g2.setColor(MC_SHADOW);
		g2.fillRect(x, y + h - 4, w, 4);
		g2.fillRect(x + w - 4, y, 4, h);
		g2.setColor(new Color(18, 18, 18));
		g2.drawRect(x, y, w - 1, h - 1);
	}

	private static void drawMinecraftSlot(Graphics2D g2, int x, int y, int w, int h) {
		g2.setColor(MC_SLOT_DARK);
		g2.fillRect(x, y, w, h);
		g2.setColor(new Color(42, 42, 42));
		g2.fillRect(x, y, w, 3);
		g2.fillRect(x, y, 3, h);
		g2.setColor(MC_HIGHLIGHT);
		g2.fillRect(x + 3, y + h - 3, w - 3, 3);
		g2.fillRect(x + w - 3, y + 3, 3, h - 3);
		g2.setColor(MC_SLOT);
		g2.fillRect(x + 4, y + 4, Math.max(0, w - 8), Math.max(0, h - 8));
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

	private static void drawItemCount(Graphics2D g2, String text, int rightX, int baseline) {
		g2.setFont(new Font("Monospaced", Font.BOLD, 13));
		FontMetrics fm = g2.getFontMetrics();
		int x = rightX - fm.stringWidth(text);
		g2.setColor(new Color(0, 0, 0, 210));
		g2.drawString(text, x + 2, baseline + 2);
		g2.setColor(new Color(45, 45, 45, 170));
		g2.drawString(text, x + 1, baseline + 1);
		g2.setColor(Color.WHITE);
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
		tip.setFont(new Font("Monospaced", Font.BOLD, 11));
		tip.setForeground(Color.WHITE);
		tip.setBackground(new Color(18, 18, 18, 232));
		tip.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(235, 235, 235, 150), 1),
				BorderFactory.createEmptyBorder(5, 8, 5, 8)));
		tip.setOpaque(true);
		return tip;
	}

	private JPanel buildTopHud() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(0, 64));
		panel.setBorder(BorderFactory.createEmptyBorder(4, 96, 4, 24));

		JPanel expWrap = new JPanel(new BorderLayout(0, 3));
		expWrap.setOpaque(false);
		expLabel = new JLabel("", SwingConstants.CENTER);
		expLabel.setFont(new Font("Dialog", Font.BOLD, 11));
		expLabel.setForeground(Color.WHITE);

		expBar = new JProgressBar(0, 100) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				int w = getWidth();
				int barH = 10;
				int y = (getHeight() - barH) / 2;

				g2.setColor(new Color(0, 0, 0, 190));
				g2.fillRect(0, y, w, barH);
				g2.setColor(new Color(31, 46, 29, 230));
				g2.fillRect(2, y + 2, Math.max(0, w - 4), barH - 4);

				double ratio = getMaximum() == 0 ? 0 : (double) getValue() / getMaximum();
				int fillW = (int) ((w - 4) * ratio);
				g2.setColor(new Color(54, 146, 36));
				g2.fillRect(2, y + 2, fillW, barH - 4);
				g2.setColor(XP_GREEN);
				g2.fillRect(2, y + 2, fillW, 2);
				g2.setColor(new Color(5, 16, 5, 190));
				for (int sx = 2; sx < w - 2; sx += 14) {
					g2.drawLine(sx, y + 2, sx, y + barH - 3);
				}
				g2.setColor(new Color(0, 0, 0, 230));
				g2.drawRect(0, y, w - 1, barH - 1);
				g2.dispose();
			}
		};
		expBar.setPreferredSize(new Dimension(0, 12));
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
		JPanel panel = new PlayerSidebarPanel();
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(74, 0));
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));

		itemPanel = new ItemPanel();
		equipmentPanel = new EquipmentPanel();
		heartPanel = new HeartPanel();

		panel.add(equipmentPanel);
		panel.add(Box.createVerticalStrut(18));
		panel.add(heartPanel);
		panel.add(Box.createVerticalStrut(18));
		panel.add(itemPanel);
		panel.add(Box.createVerticalGlue());
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
			System.out.println("[아이템] " + className + " 아이템이 남아있지 않습니다.");
			return;
		}

		if ("HealPotion".equals(className)) {
			item.use(steve);
			showMessage("HealPotion used.");
			System.out.println("[아이템] 힐 포션을 사용했습니다. 현재 체력: " + steve.getHealth() + " / " + steve.getMaxHealth());
		} else if ("AttackPotion".equals(className)) {
			item.setQuantity(item.getQuantity() - 1);
			attackPotionReady = true;
			showMessage("AttackPotion ready. Next attack deals double damage.");
			System.out.println("[아이템] 공격 포션을 사용했습니다. 다음 공격이 2배의 피해를 입힙니다.");
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
				System.out.println("[방어] " + steve.getName() + iGa(steve.getName()) + " 방어 태세를 취했습니다. 다른 카드를 선택하세요.");
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
		System.out.println("[공격] " + steve.getName() + iGa(steve.getName()) + " " + mob.getName() + eulReul(mob.getName()) + " 공격했습니다! "
				+ mob.getName() + iGa(mob.getName()) + " " + actualDmg + "의 피해를 입었습니다. (남은 체력: " + mob.getHealth() + ")");
	}

	private boolean useAoeSlash() {
		if (!(steve.getWeapon() instanceof Sword)) {
			showMessage("AoeSlash requires a sword.");
			System.out.println("[AoeSlash] 검이 없어 AoeSlash를 사용할 수 없습니다.");
			return false;
		}

		Sword sword = (Sword) steve.getWeapon();
		if (sword.getAoeSlash() == null || !sword.getAoeSlash().isReady()) {
			showMessage("AoeSlash is on cooldown.");
			System.out.println("[AoeSlash] 쿨다운 중입니다. 아직 사용할 수 없습니다.");
			return false;
		}

		List<Mob> targets = new ArrayList<>();
		for (Mob m : waveMobs) {
			if (m.isAlive()) targets.add(m);
		}

		int before = mob.getHealth();
		sword.getAoeSlash().use(steve, targets, sword);
		updateDragonPhaseImage(true);
		int actualDmg = before - mob.getHealth();

		StringBuilder sb = new StringBuilder("AoeSlash! 피해 대상: ");
		StringBuilder sbConsole = new StringBuilder("[AoeSlash] 광역 공격! 피해 대상: ");
		for (int i = 0; i < targets.size(); i++) {
			if (i > 0) { sb.append(", "); sbConsole.append(", "); }
			sb.append(targets.get(i).getName());
			sbConsole.append(targets.get(i).getName()).append("(남은 체력: ").append(targets.get(i).getHealth()).append(")");
		}
		showMessage(sb.toString());
		System.out.println(sbConsole.toString());

		mobBattlePanel.showDamage(actualDmg, true);
		mobBattlePanel.playHitFlash();
		mobBattlePanel.playShake();
		return true;
	}

	private boolean useActiveSkill(ActiveSkill skill) {
		if (skill == null) return false;
		if (!skill.isReady()) {
			showMessage(skill.getClass().getSimpleName() + " is on cooldown.");
			System.out.println("[스킬] " + skill.getClass().getSimpleName() + "이/가 쿨다운 중입니다. 아직 사용할 수 없습니다.");
			return false;
		}

		int before = mob.getHealth();
		skill.use(steve, mob);
		updateDragonPhaseImage(true);
		int actualDmg = before - mob.getHealth();
		showMessage(skill.getClass().getSimpleName() + " used.");
		System.out.println("[스킬] " + skill.getClass().getSimpleName() + eulReul(skill.getClass().getSimpleName())
				+ " 사용했습니다. " + mob.getName() + iGa(mob.getName()) + " " + actualDmg
				+ "의 피해를 입었습니다. (남은 체력: " + mob.getHealth() + ")");
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
			System.out.println("[화상] " + mob.getName() + iGa(mob.getName()) + " 화상으로 " + effectDmg + "의 피해를 입었습니다. (남은 체력: " + mob.getHealth() + ")");
		}
		updateDragonPhaseImage(true);
		refreshUI();
		if (!mob.isAlive()) {
			handleMobDead();
			return;
		}

		if (mob.isStunned()) {
			showMessage(mob.getName() + " is stunned.");
			System.out.println("[스턴] " + mob.getName() + iGa(mob.getName()) + " 스턴 상태로 이번 턴 공격하지 못합니다.");
			mob.setStunned(false);
			steve.onTurnEnd();
			decrementAoeSlashCooldown();
			refreshUI();
			return;
		}

		if (playerBlocked) {
			showMessage(mob.getName() + "'s attack was blocked.");
			System.out.println("[방어] " + mob.getName() + "의 공격이 " + steve.getName() + "에 의해 막혔습니다.");
		} else {
			int rawDmg = mob.getAttackPower();
			int steveHpBefore = steve.getHealth();
			steve.takeDamage(rawDmg);
			int actualDmg = steveHpBefore - steve.getHealth();
			showMessage(mob.getName() + " attacks! " + steve.getName() + " takes " + actualDmg + " damage.");
			System.out.println("[몹 공격] " + mob.getName() + iGa(mob.getName()) + " " + steve.getName() + eulReul(steve.getName())
					+ " 공격했습니다! " + steve.getName() + iGa(steve.getName()) + " " + actualDmg
					+ "의 피해를 입었습니다. (남은 체력: " + steve.getHealth() + ")");
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

		int coin = mob.getDropCoin();
		int exp = mob.getDropExp();
		steve.gainCoin(coin);
		steve.gainExp(exp);

		if (steve.hasPendingLevelUp()) showLevelUpDialog();

		showMessage(mob.getName() + " defeated! Coin +" + coin + "  EXP +" + exp);
		System.out.println("[처치] " + mob.getName() + eulReul(mob.getName()) + " 처치했습니다! 코인 +" + coin + ", 경험치 +" + exp
				+ " (현재 코인: " + steve.getCoin() + ", 경험치: " + steve.getExp() + ")");
		refreshUI();

		mobBattlePanel.playDeathAnimation(() -> {
			mobIndex++;
			while (mobIndex < waveMobs.size() && !waveMobs.get(mobIndex).isAlive()) {
				mobIndex++;
			}

			if (mobIndex < waveMobs.size()) {
				mob = waveMobs.get(mobIndex);
				loadImages();
				mobBattlePanel.setMobIndex(mobIndex);
				showMessage("다음 적 등장: " + mob.getName() + "!");
				System.out.println("[다음 적] " + mob.getName() + iGa(mob.getName()) + " 등장했습니다! (체력: " + mob.getHealth() + " / " + mob.getMaxHealth() + ")");
				inputLocked = false;
				refreshUI();
			} else {
				javax.swing.Timer t = new javax.swing.Timer(800, e -> {
					gameFrame.showVictory(steve, waveManager, wave);
				});
				t.setRepeats(false);
				t.start();
			}
		});
	}

	private void handleSteveDead() {
		if (inputLocked) return;
		inputLocked = true;
		setGlassPaneBlocking(true);

		showMessage(steve.getName() + " has fallen.");
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
		expLabel.setText("Lv " + steve.getLevel() + "   EXP " + exp + " / " + expBar.getMaximum());
		steveCoinLabel.setText(String.valueOf(steve.getCoin()));

		if (heartPanel != null) heartPanel.repaint();
		if (itemPanel != null) itemPanel.repaint();
		if (equipmentPanel != null) equipmentPanel.repaint();
		if (mobBattlePanel != null) mobBattlePanel.repaint();
		if (cardPanel != null) cardPanel.repaint();

		this.revalidate();
		this.repaint();
	}

	private void showLevelUpDialog() {
		JDialog dialog = new JDialog(
				SwingUtilities.getWindowAncestor(this),
				"Level Up",
				Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setUndecorated(true);

		JPanel root = new JPanel(new BorderLayout(0, 5)) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				int w = getWidth(), h = getHeight();
				drawMinecraftWindow(g2, 0, 0, w, h);
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

				String main = "LEVEL UP!";
				g2.setFont(new Font("Dialog", Font.BOLD, 30));
				FontMetrics fm = g2.getFontMetrics();
				int tx = cx - fm.stringWidth(main) / 2;
				g2.setColor(new Color(100, 100, 100));
				g2.drawString(main, tx + 2, 34 + 2);
				g2.setColor(new Color(50, 50, 50));
				g2.drawString(main, tx, 34);

				String sub = "보상을 하나 선택하세요!";
				g2.setFont(new Font("Dialog", Font.BOLD, 14));
				fm = g2.getFontMetrics();
				g2.setColor(new Color(120, 120, 120));
				g2.drawString(sub, cx - fm.stringWidth(sub) / 2 + 1, 58 + 1);
				g2.setColor(new Color(50, 50, 50));
				g2.drawString(sub, cx - fm.stringWidth(sub) / 2, 58);

				g2.dispose();
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(0, 65);
			}
		};
		titlePanel.setOpaque(false);

		JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
		cardsPanel.setOpaque(false);
		cardsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 15, 10));

		ImageIcon heartIc = loadScaledIcon("resources/icon/Heart.png", 54, 54);
		ImageIcon atkIc = loadScaledIcon("resources/icon/ATK.png", 54, 54);
		ImageIcon defIc = loadScaledIcon("resources/icon/DEF.png", 54, 54);

		cardsPanel.add(buildLevelUpCard("HP 강화", "최대 체력 +10", "체력을 올리고\n전부 회복합니다.",
				heartIc, new Color(180, 20, 20), new Color(180, 20, 20), () -> {
					steve.applyLevelUpChoice(1);
					dialog.dispose();
					refreshUI();
					if (steve.hasPendingLevelUp()) showLevelUpDialog();
				}));
		cardsPanel.add(buildLevelUpCard("공격 강화", "공격력 +2", "기본 공격력이\n증가합니다.",
				atkIc, new Color(139, 69, 19), new Color(50, 50, 50), () -> {
					steve.applyLevelUpChoice(2);
					dialog.dispose();
					refreshUI();
					if (steve.hasPendingLevelUp()) showLevelUpDialog();
				}));
		cardsPanel.add(buildLevelUpCard("방어 강화", "방어력 +1", "받는 피해를\n줄입니다.",
				defIc, new Color(30, 90, 180), new Color(30, 90, 180), () -> {
					steve.applyLevelUpChoice(3);
					dialog.dispose();
					refreshUI();
					if (steve.hasPendingLevelUp()) showLevelUpDialog();
				}));

		root.add(titlePanel, BorderLayout.NORTH);
		root.add(cardsPanel, BorderLayout.CENTER);

		dialog.setContentPane(root);
		dialog.setSize(580, 360);
		dialog.setLocationRelativeTo(this);
		dialog.setResizable(false);
		dialog.setVisible(true);
	}

	private JPanel buildLevelUpCard(String title, String statLine, String desc,
			ImageIcon icon, Color headerColor, Color statColor, Runnable onClick) {

		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

				int w = getWidth(), h = getHeight();

				g2.setColor(new Color(198, 198, 198));
				g2.fillRect(0, 0, w, h);
				g2.setColor(Color.BLACK);
				g2.drawRect(0, 0, w - 1, h - 1);
				g2.setColor(Color.WHITE);
				g2.drawLine(1, 1, w - 2, 1);
				g2.drawLine(1, 1, 1, h - 2);
				g2.setColor(new Color(110, 110, 110));
				g2.drawLine(1, h - 2, w - 2, h - 2);
				g2.drawLine(w - 2, 1, w - 2, h - 2);

				int headerY = 8;
				int headerH = 28;
				g2.setColor(Color.BLACK);
				g2.drawRect(6, headerY, w - 13, headerH);
				g2.setColor(headerColor);
				g2.fillRect(7, headerY + 1, w - 14, headerH - 1);

				g2.setFont(new Font("Dialog", Font.PLAIN, 13));
				FontMetrics fm = g2.getFontMetrics();
				int tx = (w - fm.stringWidth(title)) / 2;
				g2.setColor(new Color(0, 0, 0, 180));
				g2.drawString(title, tx + 1, headerY + 19 + 1);
				g2.setColor(Color.WHITE);
				g2.drawString(title, tx, headerY + 19);

				if (icon != null && icon.getIconWidth() > 0) {
					int iw = icon.getIconWidth(), ih = icon.getIconHeight();
					int dx = (w - iw) / 2;
					int dy = 55;
					g2.setColor(new Color(0, 0, 0, 50));
					g2.drawImage(icon.getImage(), dx + 2, dy + 2, iw, ih, null);
					g2.drawImage(icon.getImage(), dx, dy, iw, ih, null);
				}

				int textY = 145;
				g2.setFont(new Font("Dialog", Font.PLAIN, 13));
				fm = g2.getFontMetrics();
				tx = (w - fm.stringWidth(statLine)) / 2;
				g2.setColor(statColor);
				g2.drawString(statLine, tx, textY);

				textY += 12;
				g2.setColor(new Color(130, 130, 130));
				g2.drawLine(15, textY, w - 15, textY);
				g2.setColor(new Color(230, 230, 230));
				g2.drawLine(15, textY + 1, w - 15, textY + 1);

				textY += 22;
				g2.setFont(new Font("Dialog", Font.PLAIN, 12));
				fm = g2.getFontMetrics();
				g2.setColor(new Color(50, 50, 50));
				String[] lines = desc.split("\n");
				for (String line : lines) {
					g2.drawString(line, (w - fm.stringWidth(line)) / 2, textY);
					textY += 16;
				}

				g2.dispose();
			}
		};

		card.setLayout(null);
		card.setOpaque(false);
		card.setPreferredSize(new Dimension(160, 250));

		JButton selectBtn = new JButton("선택") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				Color base = getModel().isPressed() ? new Color(130, 130, 130)
						: getModel().isRollover() ? new Color(170, 170, 170) : new Color(150, 150, 150);
				g2.setColor(base);
				g2.fillRect(0, 0, getWidth(), getHeight());
				g2.setColor(Color.BLACK);
				g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
				g2.setColor(Color.WHITE);
				g2.drawLine(1, 1, getWidth() - 2, 1);
				g2.drawLine(1, 1, 1, getHeight() - 2);
				g2.setColor(new Color(85, 85, 85));
				g2.drawLine(1, getHeight() - 2, getWidth() - 2, getHeight() - 2);
				g2.drawLine(getWidth() - 2, 1, getWidth() - 2, getHeight() - 2);

				FontMetrics fm = g2.getFontMetrics();
				int tx = (getWidth() - fm.stringWidth(getText())) / 2;
				int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
				g2.setColor(new Color(80, 80, 80));
				g2.drawString(getText(), tx + 1, ty + 1);
				g2.setColor(Color.WHITE);
				g2.drawString(getText(), tx, ty);
				g2.dispose();
			}
		};

		selectBtn.setFocusPainted(false);
		selectBtn.setBorderPainted(false);
		selectBtn.setContentAreaFilled(false);
		selectBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		selectBtn.addActionListener(e -> onClick.run());

		int btnW = 120;
		int btnH = 28;

		card.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				int cw = card.getWidth();
				int ch = card.getHeight();
				selectBtn.setBounds((cw - btnW) / 2, ch - btnH - 12, btnW, btnH);
			}
		});

		card.add(selectBtn);
		return card;
	}

	private int getMobDefense() {
		return readIntMethod(mob, 0, "getDefensePower", "getDefense", "getArmor");
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
			} catch (Exception ignored) {}
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
			} catch (Exception ignored) {}
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
			} catch (Exception ignored) {}
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

	private static void drawCenteredTrimmed(Graphics2D g2, String s, int x, int y, int w) {
		drawCentered(g2, trimToWidth(g2, s, w), x, y, w);
	}

	private static void drawRightAligned(Graphics2D g2, String s, int rightX, int y) {
		FontMetrics fm = g2.getFontMetrics();
		g2.drawString(s, rightX - fm.stringWidth(s), y);
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

	private static void drawWrappedCentered(Graphics2D g2, String s, int x, int y, int w, int lineHeight, int maxLines) {
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
		if (!line.isEmpty()) drawCentered(g2, trimToWidth(g2, line, w), x, lineY, w);
	}

	private static void drawMarkedWrappedCentered(Graphics2D g2, String s, int x, int y, int w, int lineHeight, int maxLines) {
		FontMetrics fm = g2.getFontMetrics();
		String[] words = s.split(" ");
		List<String> lines = new ArrayList<>();
		String line = "";
		for (String word : words) {
			String next = line.isEmpty() ? word : line + " " + word;
			if (fm.stringWidth(stripMarks(next)) > w && !line.isEmpty()) {
				lines.add(line);
				line = word;
				if (lines.size() == maxLines - 1) break;
			} else {
				line = next;
			}
		}
		if (!line.isEmpty() && lines.size() < maxLines) lines.add(line);

		for (int i = 0; i < lines.size(); i++) {
			drawMarkedCenteredLine(g2, lines.get(i), x, y + i * lineHeight, w);
		}
	}

	private static void drawMarkedCenteredLine(Graphics2D g2, String s, int x, int y, int w) {
		List<TextRun> runs = parseMarkedRuns(s);
		FontMetrics fm = g2.getFontMetrics();
		int totalW = 0;
		for (TextRun run : runs) totalW += fm.stringWidth(run.text);
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
		if (current.length() > 0) runs.add(new TextRun(current.toString(), marked));
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
		if (fm.stringWidth(text) <= maxW) return text;
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