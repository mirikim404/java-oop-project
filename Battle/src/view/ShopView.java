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
import java.util.ArrayList;
import java.util.List;

public class ShopView extends JPanel {

	private GameFrame gameFrame;
	private Steve steve;
	private WaveManager waveManager;
	private ShopManager shopManager;
	private int wave;

	private Image bgImage;
	private Image tabActiveImg;

	private static final Color MC_TEXT = new Color(209, 213, 219);
	private static final Color MC_TEXT_DARK = new Color(63, 63, 63);
	private static final Color MC_GOLD = new Color(255, 215, 0);

	private int selectedTab = 0;
	private static final String[] TAB_NAMES = { "무기", "스킬", "포션" };

	private static final String[] TAB_ICON_PATHS = { "resources/icon/shop_tab_weapon.png",
			"resources/icon/shop_tab_skill.png", "resources/icon/shop_tab_potion.png" };

	private int hoveredSlot = -1;
	private int selectedSlot = -1;

	private static class ShopItem {
		String imageName, name, desc;
		int atk, def, price;
		Rarity rarity;
		Runnable onBuy;

		ShopItem(String imageName, String name, String desc, Rarity rarity, int atk, int def, int price,
				Runnable onBuy) {
			this.imageName = imageName;
			this.name = name;
			this.desc = desc;
			this.rarity = rarity;
			this.atk = atk;
			this.def = def;
			this.price = price;
			this.onBuy = onBuy;
		}
	}

	private List<ShopItem>[] tabItems;
	private List<Rectangle> slotBounds = new ArrayList<>();

	private String message = "";
	private String lastBoughtImageName = "";
	private javax.swing.Timer msgTimer;

	enum Rarity {
		COMMON("일반", new Color(200, 205, 212)), UNCOMMON("고급", new Color(122, 201, 67)),
		RARE("희귀", new Color(77, 163, 255)), EPIC("영웅", new Color(179, 136, 255)),
		LEGENDARY("전설", new Color(255, 179, 71));

		final String label;
		final Color color;

		Rarity(String label, Color color) {
			this.label = label;
			this.color = color;
		}
	}

	@SuppressWarnings("unchecked")
	public ShopView(GameFrame gameFrame, Steve steve, WaveManager waveManager, int wave) {
		this.gameFrame = gameFrame;
		this.steve = steve;
		this.waveManager = waveManager;
		this.shopManager = new ShopManager(steve);
		this.wave = wave;

		tabItems = new List[3];
		buildTabItems();

		bgImage = new ImageIcon("resources/ui/shop_bg_v2.png").getImage();
		tabActiveImg = new ImageIcon("resources/ui/tab_active.png").getImage();

		setLayout(new BorderLayout());
		setOpaque(false);

		ShopPanel shopPanel = new ShopPanel();
		add(shopPanel, BorderLayout.CENTER);

		msgTimer = new javax.swing.Timer(1500, e -> {
			message = "";
			repaint();
		});
		msgTimer.setRepeats(false);
	}

	@SuppressWarnings("unchecked")
	private void buildTabItems() {
		tabItems[0] = new ArrayList<>();
		tabItems[1] = new ArrayList<>();
		tabItems[2] = new ArrayList<>();

		tabItems[0].add(new ShopItem("StoneSword", "돌 검", "돌로 만들어진 검입니다.\n가장 기본적인 무기입니다.", Rarity.COMMON, 8, 0, 40,
				() -> tryBuy(() -> shopManager.buyWeapon(new StoneSword()))));
		tabItems[0].add(new ShopItem("IronSword", "철 검", "철로 만들어진 검입니다.\n적당한 강도를 자랑합니다.", Rarity.UNCOMMON, 16, 0, 75,
				() -> tryBuy(() -> shopManager.buyWeapon(new IronSword()))));
		tabItems[0].add(new ShopItem("DiamondSword", "다이아몬드 검", "다이아몬드로 만들어진 검입니다.\n균형 잡힌 성능을 자랑합니다.", Rarity.RARE, 24,
				0, 115, () -> tryBuy(() -> shopManager.buyWeapon(new DiamondSword()))));
		tabItems[0].add(new ShopItem("NetheriteSword", "네더라이트 검", "지옥의 금속으로 만든 검입니다.\n최강의 무기입니다.", Rarity.EPIC, 32, 0,
				160, () -> tryBuy(() -> shopManager.buyWeapon(new NetheriteSword()))));

		tabItems[1].add(new ShopItem("SnowBall", "눈덩이", "적을 스턴 상태로 만듭니다.\n쿨타임 3턴", Rarity.COMMON, 10, 0, 45,
				() -> tryBuy(() -> shopManager.buySkill(new SnowBall()))));
		tabItems[1].add(new ShopItem("FireCharge", "화염구", "적에게 화상을 입힙니다.\n화상 2턴, 쿨타임 3턴", Rarity.UNCOMMON, 15, 0, 65,
				() -> tryBuy(() -> shopManager.buySkill(new FireCharge()))));

		tabItems[2].add(new ShopItem("AttackPotion", "공격 포션", "다음 공격의 데미지를\n2배로 만듭니다.", Rarity.COMMON, 0, 0, 18,
				() -> tryBuy(() -> shopManager.buyPotion(new AttackPotion()))));
		tabItems[2].add(new ShopItem("HealPotion", "회복 포션", "체력을 일정량\n회복합니다.", Rarity.COMMON, 0, 0, 15,
				() -> tryBuy(() -> shopManager.buyPotion(new HealPotion()))));
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

	private class ShopPanel extends JPanel {

		private List<Rectangle> tabBounds = new ArrayList<>();
		private Rectangle buyBtnBounds;
		private Rectangle nextBtnBounds;

		private static final double REF_W = 1536.0;
		private static final double REF_H = 1024.0;

		private static final int TITLE_X = 88;
		private static final int TITLE_Y = 43;
		private static final int TITLE_W = 278;
		private static final int TITLE_H = 84;
		private static final int TITLE_FONT_SIZE = 32;

		private static final int COIN_FONT_SIZE = 22;
		private static final int COIN_ICON_SIZE = 28;
		private static final int COIN_AREA_X_ANCHOR = 1400;
		private static final int COIN_AREA_Y = 115;
		private static final int COIN_GAP_X = 12;
		private static final int COIN_GAP_Y = 23;

		private static final int TAB_X = 88;
		private static final int TAB_Y = 171;
		private static final int TAB_W = 153;
		private static final int TAB_H = 85;
		private static final int TAB_GAP_Y = 10;
		private static final int TAB_ICON_Y_OFFSET = 4;
		private static final int TAB_TEXT_Y_OFFSET = 50;
		private static final int TAB_FONT_SIZE = 14;
		private static final int TAB_ICON_SIZE = 52;
		private static final int TAB_LABEL_GAP = 6;
		private static final int TAB_LEFT_OFFSET = 10;

		private static final int GRID_X = 276;
		private static final int GRID_Y = 170;
		private static final double SLOT_W_PRECISE = 170.25;
		private static final double SLOT_GAP_X_PRECISE = 15.55;
		private static final int SLOT_GAP_Y = 12;
		private static final int[] ROW_HEIGHTS = { 270, 257 };

		private static final int SLOT_NAME_FONT_SIZE = 18;
		private static final int SLOT_STAT_ICON_SIZE = 24;
		private static final int SLOT_STAT_FONT_SIZE = 12;
		private static final int SLOT_STAT_Y_OFFSET_FROM_BOTTOM = 65;
		private static final int SLOT_PRICE_FONT_SIZE = 22;
		private static final int SLOT_PRICE_ICON_SIZE = 24;
		private static final int SLOT_PRICE_Y_OFFSET_FROM_BOTTOM = 15;

		private static final int DETAIL_X = 1038;
		private static final int DETAIL_Y = 149;
		private static final int DETAIL_W = 388;
		private static final int DETAIL_H = 580;

		private static final int DETAIL_NAME_FONT = 24;
		private static final int DETAIL_RARITY_FONT = 11;
		private static final int DETAIL_IMG_Y_OFFSET = 85;
		private static final int DETAIL_IMG_SIZE = 170;

		private static final int DETAIL_STAT_FONT = 18;
		private static final int DETAIL_STAT_ICON_SIZE = 22;
		private static final int DETAIL_STAT_ICON_GAP = 30;
		private static final int DETAIL_STAT_TEXT_Y_OFFSET = 17;
		private static final int DETAIL_STAT_START_Y = 275;
		private static final int DETAIL_STAT_LINE_SPACING = 28;

		private static final int DETAIL_DESC_FONT = 18;
		private static final int DETAIL_DESC_LINE_SPACING = 24;
		private static final int DETAIL_DESC_START_Y_OFFSET = 50;

		private static final int DETAIL_PRICE_FONT = 22;
		private static final int DETAIL_PRICE_ICON_SIZE = 20;
		private static final int DETAIL_PRICE_GAP = 6;
		private static final int DETAIL_PRICE_Y_OFFSET_FROM_BOTTOM = 110;
		private static final int DETAIL_PRICE_ICON_Y_OFFSET = 5;
		private static final int DETAIL_PRICE_TEXT_Y_OFFSET = 22;

		private static final int BUY_BTN_X = 1072;
		private static final int BUY_BTN_Y = 664;
		private static final int BUY_BTN_W = 340;
		private static final int BUY_BTN_H = 42;
		private static final int BUY_BTN_FONT_SIZE = 20;

		private static final int NEXT_BTN_X = 1118;
		private static final int NEXT_BTN_Y = 765;
		private static final int NEXT_BTN_W = 295;
		private static final int NEXT_BTN_H = 76;
		private static final int NEXT_BTN_FONT_SIZE = 20;

		private static final int INV_X = 113;
		private static final int INV_Y = 767;
		private static final int INV_SLOT_W = 78;
		private static final int INV_SLOT_H = 75;
		private static final int INV_GAP = 11;
		private static final int INV_TITLE_FONT_SIZE = 13;
		private static final int INV_QTY_FONT_SIZE = 10;

		private static final int TOAST_W = 320;
		private static final int TOAST_H = 38;
		private static final int TOAST_Y = 45;
		private static final int TOAST_ICON_SIZE = 18;
		private static final int TOAST_FONT_SIZE = 15;

		ShopPanel() {
			setOpaque(false);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					for (int i = 0; i < tabBounds.size(); i++) {
						if (tabBounds.get(i).contains(e.getPoint())) {
							selectedTab = i;
							selectedSlot = -1;
							hoveredSlot = -1;
							repaint();
							return;
						}
					}
					for (int i = 0; i < slotBounds.size(); i++) {
						if (slotBounds.get(i).contains(e.getPoint())) {
							List<ShopItem> currentItems = tabItems[selectedTab];
							if (i < currentItems.size()) {
								selectedSlot = i;
								repaint();
							}
							return;
						}
					}
					if (buyBtnBounds != null && buyBtnBounds.contains(e.getPoint())) {
						if (selectedSlot >= 0 && selectedSlot < tabItems[selectedTab].size()) {
							ShopItem item = tabItems[selectedTab].get(selectedSlot);
							String status = getItemStatus(item, TAB_NAMES[selectedTab]);

							if (status.equals("AVAILABLE")) {
								item.onBuy.run(); // 내부에서 코인 부족하면 토스트 알림 띄움
							} else {
								String reason = switch (status) {
									case "OWNED" -> "이미 보유 중이거나 상위 티어 장비를 가졌습니다.";
									case "LOCKED" -> "이전 단계 무기를 먼저 구매해야 합니다.";
									case "SLOT_FULL" -> "가방 슬롯이 가득 차서 공간이 없습니다!";
									default -> "구매할 수 없는 상태입니다.";
								};
								showMsg("§c" + reason);
							}
						}
						return;
					}
					if (nextBtnBounds != null && nextBtnBounds.contains(e.getPoint())) {
						Mob nextMob = waveManager.getAliveMobs().get(0);
						gameFrame.showEncounter(steve, waveManager, nextMob, wave);
						return;
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					hoveredSlot = -1;
					repaint();
				}
			});

			addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {
					int prev = hoveredSlot;
					hoveredSlot = -1;
					List<ShopItem> currentItems = tabItems[selectedTab];
					for (int i = 0; i < slotBounds.size(); i++) {
						if (slotBounds.get(i).contains(e.getPoint())) {
							if (i < currentItems.size()) {
								hoveredSlot = i;
							}
							break;
						}
					}
					if (hoveredSlot != prev)
						repaint();
				}
			});
		}

		private String getItemStatus(ShopItem item, String tabName) {
			if (tabName.equals("무기")) {
				String current = steve.getWeapon().getClass().getSimpleName();
				int curTier = getWeaponTier(current);
				int targetTier = getWeaponTier(item.imageName);

				if (curTier >= targetTier) return "OWNED";      
				if (targetTier == curTier + 1) return "AVAILABLE"; 
				return "LOCKED";                               
			} else if (tabName.equals("스킬")) {
				boolean hasSkill = false;
				boolean hasEmptySlot = false;
				for (skill.active.ActiveSkill s : steve.getActiveSkills()) {
					if (s != null) {
						if (s.getClass().getSimpleName().equals(item.imageName)) hasSkill = true;
					} else {
						hasEmptySlot = true;
					}
				}
				if (hasSkill) return "OWNED";
				if (!hasEmptySlot) return "SLOT_FULL";
				return "AVAILABLE";
			} else if (tabName.equals("포션")) {
				boolean hasPotion = false;
				boolean hasEmptySlot = false;
				for (skill.consumable.ConsumableSkill c : steve.getConsumables()) {
					if (c != null) {
						if (c.getClass().getSimpleName().equals(item.imageName)) hasPotion = true;
					} else {
						hasEmptySlot = true;
					}
				}
				if (hasPotion) return "AVAILABLE"; 
				if (!hasEmptySlot) return "SLOT_FULL"; 
				return "AVAILABLE";
			}
			return "AVAILABLE";
		}

		private int getWeaponTier(String name) {
			return switch (name) {
				case "WoodSword" -> 0;
				case "StoneSword" -> 1;
				case "IronSword" -> 2;
				case "DiamondSword" -> 3;
				case "NetheriteSword" -> 4;
				default -> -1;
			};
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

			int W = getWidth();
			int H = getHeight();

			if (bgImage != null) {
				g2.drawImage(bgImage, 0, 0, W, H, this);
			} else {
				g2.setColor(new Color(15, 15, 20));
				g2.fillRect(0, 0, W, H);
			}

			tabBounds.clear();
			slotBounds.clear();

			double sx = W / REF_W;
			double sy = H / REF_H;

			int titleBoxX = sc(sx, TITLE_X);
			int titleBoxY = sc(sy, TITLE_Y);
			int titleBoxW = sc(sx, TITLE_W);
			int titleBoxH = sc(sy, TITLE_H);

			g2.setFont(mcFontEN(sc(sx, TITLE_FONT_SIZE)));
			FontMetrics titleFm = g2.getFontMetrics();
			int titleX = titleBoxX + (titleBoxW - titleFm.stringWidth("SHOP")) / 2;
			int titleY = titleBoxY + (titleBoxH + titleFm.getAscent() - titleFm.getDescent()) / 2 + sc(sy, 1);

			g2.setColor(MC_TEXT_DARK);
			g2.drawString("SHOP", titleX + 2, titleY + 2);
			g2.setColor(MC_TEXT);
			g2.drawString("SHOP", titleX, titleY);

			String coinStr = String.valueOf(steve.getCoin());
			g2.setFont(mcFont(sc(sx, COIN_FONT_SIZE)));
			FontMetrics coinFm = g2.getFontMetrics();
			int coinAreaX = sc(sx, COIN_AREA_X_ANCHOR) - coinFm.stringWidth(coinStr);
			int coinAreaY = sc(sy, COIN_AREA_Y);

			g2.setColor(MC_TEXT_DARK);
			g2.drawString(coinStr, coinAreaX + 1, coinAreaY + 1);
			g2.setColor(MC_GOLD);
			g2.drawString(coinStr, coinAreaX, coinAreaY);

			int coinIconSize = sc(sx, COIN_ICON_SIZE);
			drawMCIcon(g2, "resources/icon/Coin.png", coinAreaX - coinIconSize - sc(sx, COIN_GAP_X),
					coinAreaY - sc(sy, COIN_GAP_Y), coinIconSize, coinIconSize);

			int tabX = sc(sx, TAB_X);
			int tabY = sc(sy, TAB_Y);
			int tabW = sc(sx, TAB_W);
			int tabH = sc(sy, TAB_H);
			int tabGap = sc(sy, TAB_GAP_Y);

			for (int i = 0; i < TAB_NAMES.length; i++) {
				int ty = tabY + i * (tabH + tabGap);
				tabBounds.add(new Rectangle(tabX, ty, tabW, tabH));
				drawTabButton(g2, tabX, ty, tabW, tabH, TAB_NAMES[i], TAB_ICON_PATHS[i], selectedTab == i, sx, sy);
			}

			double preciseSlotW = SLOT_W_PRECISE * sx;
			double preciseGapX = SLOT_GAP_X_PRECISE * sx;
			int slotGapY = sc(sy, SLOT_GAP_Y);
			int gridX = sc(sx, GRID_X);
			int gridY = sc(sy, GRID_Y);
			int[] rowHeights = new int[] { sc(sy, ROW_HEIGHTS[0]), sc(sy, ROW_HEIGHTS[1]) };

			List<ShopItem> items = tabItems[selectedTab];

			for (int i = 0; i < 8; i++) {
				int row = i / 4;
				int col = i % 4;
				int currentSlotH = rowHeights[row];
				int posY = (row == 0) ? gridY : (gridY + rowHeights[0] + slotGapY);
				int posX = (int) (gridX + col * (preciseSlotW + preciseGapX));

				Rectangle sr = new Rectangle(posX, posY, (int) preciseSlotW, currentSlotH);
				slotBounds.add(sr);

				boolean hovered = (hoveredSlot == i);
				boolean selected = (selectedSlot == i);
				ShopItem item = (i < items.size()) ? items.get(i) : null;

				drawItemSlot(g2, posX, posY, (int) preciseSlotW, currentSlotH, item, hovered, selected, sx, sy);
			}

			int detailX = sc(sx, DETAIL_X);
			int detailY = sc(sy, DETAIL_Y);
			int detailW = sc(sx, DETAIL_W);
			int detailH = sc(sy, DETAIL_H);

			ShopItem sel = (selectedSlot >= 0 && selectedSlot < items.size()) ? items.get(selectedSlot) : null;
			drawDetailPanel(g2, detailX, detailY, detailW, detailH, sel, sx, sy);

			int invX = sc(sx, INV_X);
			int invY = sc(sy, INV_Y);
			int invSlotW = sc(sx, INV_SLOT_W);
			int invSlotH = sc(sy, INV_SLOT_H);
			int invGap = sc(sx, INV_GAP);
			drawInventory(g2, invX, invY, invSlotW, invSlotH, invGap, sx, sy);

			int nbX = sc(sx, NEXT_BTN_X);
			int nbY = sc(sy, NEXT_BTN_Y);
			int nbW = sc(sx, NEXT_BTN_W);
			int nbH = sc(sy, NEXT_BTN_H);
			nextBtnBounds = new Rectangle(nbX, nbY, nbW, nbH);
			drawMCButton(g2, nbX, nbY, nbW, nbH, "다음 웨이브 ▶", NEXT_BTN_FONT_SIZE, true, sx, sy);

			if (!message.isEmpty()) {
				boolean isSuccess = message.startsWith("§a");
				String displayMsg = message.replace("§a", "").replace("§c", "");

				// 1. 폰트 셋팅 후 글자의 실제 가로 픽셀 길이를 계산
				g2.setFont(mcFont(sc(sx, TOAST_FONT_SIZE)));
				FontMetrics textFm = g2.getFontMetrics();
				int textW = textFm.stringWidth(displayMsg);

				// 2. 아이콘 크기와 여백을 더해 전체 너비를 동적으로 계산
				int iconSize = sc(sx, TOAST_ICON_SIZE);
				int paddingX = sc(sx, 18); // 좌우 안쪽 여백
				int gapX = sc(sx, 10);     // 아이콘과 글자 사이 간격
				
				int toastW = paddingX * 2 + iconSize + gapX + textW; // 글자 길이에 맞게 늘어남
				int toastH = sc(sy, TOAST_H);
				int toastX = (getWidth() - toastW) / 2; // 화면 중앙 정렬
				int toastY = sc(sy, TOAST_Y);

				Color bgColor = isSuccess ? new Color(14, 36, 14, 230) : new Color(36, 14, 14, 230);
				Color borderColor = isSuccess ? new Color(85, 255, 85) : new Color(255, 85, 85);

				// 배경 사각형 그리기
				g2.setColor(bgColor);
				g2.fillRect(toastX, toastY, toastW, toastH);

				// 테두리 선 그리기
				g2.setStroke(new BasicStroke(sc(sx, 2)));
				g2.setColor(borderColor);
				g2.drawRect(toastX, toastY, toastW, toastH);
				g2.setStroke(new BasicStroke(1));

				// 3. 아이콘 위치 잡기
				int iconX = toastX + paddingX;
				int iconY = toastY + (toastH - iconSize) / 2;

				// 원형 배경 그리기 (성공은 녹색, 실패는 빨간색)
				g2.setColor(borderColor);
				g2.fillOval(iconX, iconY, iconSize, iconSize);
				
				if (isSuccess) {
					// [성공] 픽셀-퍼펙트 체크(V) 기호 직접 선으로 그리기
					g2.setColor(Color.BLACK);
					g2.setStroke(new BasicStroke(sc(sx, 2))); // 선 도톰하게
					
					int startX = iconX + sc(sx, 5);
					int startY = iconY + sc(sy, 9);
					int midX   = iconX + sc(sx, 8);
					int midY   = iconY + sc(sy, 12);
					int endX   = iconX + sc(sx, 13);
					int endY   = iconY + sc(sy, 5);
					
					g2.drawLine(startX, startY, midX, midY); // \ 선
					g2.drawLine(midX, midY, endX, endY);     // / 선
					
					g2.setStroke(new BasicStroke(1)); // 두께 원상복구
				} else {
					// [실패] 깨지지 않는 아스키 코드 '!' 그리기
					g2.setColor(Color.BLACK);
					g2.setFont(mcFont(sc(sx, 13)));
					FontMetrics symFm = g2.getFontMetrics();
					String symbol = "!";
					
					int symX = iconX + (iconSize - symFm.stringWidth(symbol)) / 2 + sc(sx, 1);
					int symY = iconY + (iconSize + symFm.getAscent() - symFm.getDescent()) / 2 - sc(sy, 1);
					
					g2.drawString(symbol, symX, symY);
				}

				// 4. 텍스트 위치 잡기 (아이콘 바로 오른쪽 배치)
				int tx = iconX + iconSize + gapX;
				int ty = toastY + (toastH + textFm.getAscent() - textFm.getDescent()) / 2 - sc(sy, 1);

				g2.setFont(mcFont(sc(sx, TOAST_FONT_SIZE)));
				
				// 글자 그림자 효과
				g2.setColor(new Color(0, 0, 0, 200));
				g2.drawString(displayMsg, tx + 1, ty + 1);

				// 본래 글자 색상
				g2.setColor(borderColor);
				g2.drawString(displayMsg, tx, ty);
			}
			g2.dispose();
		}

		private void drawTabButton(Graphics2D g2, int x, int y, int w, int h, String label, String iconPath,
				boolean active, double sx, double sy) {
			int fontSize = TAB_FONT_SIZE;
			int iconSize = (int) sc(sx, TAB_ICON_SIZE);
			int gap = (int) sc(sx, TAB_LABEL_GAP);
			int leftOffset = (int) sc(sx, TAB_LEFT_OFFSET);

			if (active && tabActiveImg != null) {
				g2.drawImage(tabActiveImg, x, y, w, h, null);
			}

			Image tabIcon = new ImageIcon(iconPath).getImage();
			g2.setFont(mcFont(fontSize));
			FontMetrics fm = g2.getFontMetrics();

			int totalW = iconSize + gap + fm.stringWidth(label);
			int startX = x + (w - totalW) / 2 - leftOffset;

			int iconY = y + (h - iconSize) / 2 + sc(sy, TAB_ICON_Y_OFFSET);
			int ty = y + sc(sy, TAB_TEXT_Y_OFFSET);

			if (tabIcon != null) {
				g2.drawImage(tabIcon, startX, iconY, iconSize, iconSize, null);
			}

			int tx = startX + iconSize + gap;

			g2.setColor(new Color(0, 0, 0, 200));
			drawMixedString(g2, label, tx + 1, ty + 1, fontSize);
			g2.setColor(active ? MC_TEXT : new Color(120, 120, 120));
			drawMixedString(g2, label, tx, ty, fontSize);
		}

		private void drawItemSlot(Graphics2D g2, int x, int y, int w, int h, ShopItem item, boolean hovered,
				boolean selected, double sx, double sy) {
			int nameY = y + sc(sy, 35);
			int iconSize = (int) (Math.min(w, h) * 0.78);
			int iconX = x + (w - iconSize) / 2;
			int iconY = nameY + sc(sy, 13);
			int iconCenterX = iconX + iconSize / 2;
			int iconCenterY = iconY + iconSize / 2;

			if (selected) {
				g2.setColor(new Color(77, 163, 255, 15));
				g2.fillRect(x, y, w, h);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				GradientPaint glassGradient = new GradientPaint(x, y, new Color(255, 255, 255, 30), x + w / 2, y + h,
						new Color(255, 255, 255, 0));
				g2.setPaint(glassGradient);
				int[] px = { x, x + (w * 2 / 3), x + (w / 3), x };
				int[] py = { y, y, y + h, y + h };
				g2.fillPolygon(px, py, 4);

				int glowRadius = (int) (iconSize * 0.75);
				float[] fractions = { 0.0f, 0.25f, 1.0f };
				Color[] colors = { new Color(255, 255, 255, 90), new Color(77, 163, 255, 35),
						new Color(77, 163, 255, 0) };
				java.awt.RadialGradientPaint radialPaint = new java.awt.RadialGradientPaint(
						new java.awt.geom.Point2D.Float(iconCenterX, iconCenterY), glowRadius, fractions, colors);
				g2.setPaint(radialPaint);
				g2.fillOval(iconCenterX - glowRadius, iconCenterY - glowRadius, glowRadius * 2, glowRadius * 2);
			} else if (hovered) {
				g2.setColor(new Color(77, 163, 255, 6));
				g2.fillRect(x, y, w, h);
			}

			if (selected || hovered) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(sc(sx, 3)));
				g2.setColor(new Color(77, 163, 255, selected ? 35 : 15));
				g2.drawRect(x, y, w, h);
				g2.setStroke(new BasicStroke(sc(sx, 1)));
				g2.setColor(new Color(77, 163, 255, selected ? 150 : 80));
				g2.drawRect(x, y, w, h);
			}

			if (selected) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setStroke(new BasicStroke(sc(sx, 2)));
				g2.setColor(new Color(235, 250, 255, 240));
				int cLen = sc(sx, 9);
				g2.drawLine(x, y, x + cLen, y);
				g2.drawLine(x, y, x, y + cLen);
				g2.drawLine(x + w, y, x + w - cLen, y);
				g2.drawLine(x + w, y, x + w, y + cLen);
				g2.drawLine(x, y + h, x + cLen, y + h);
				g2.drawLine(x, y + h, x, y + h - cLen);
				g2.drawLine(x + w, y + h, x + w - cLen, y + h);
				g2.drawLine(x + w, y + h, x + w, y + h - cLen);
				g2.setStroke(new BasicStroke(1));
			}

			if (item == null)
				return;

			int nameFontSize = SLOT_NAME_FONT_SIZE;
			g2.setFont(mcFont(sc(sx, nameFontSize)));
			FontMetrics fm = g2.getFontMetrics();
			String name = item.name;
			while (fm.stringWidth(name) > w - 6 && name.length() > 4) {
				name = name.substring(0, name.length() - 1);
			}
			int nameX = x + (w - fm.stringWidth(name)) / 2;

			g2.setColor(new Color(0, 0, 0, 160));
			drawMixedString(g2, name, nameX + 1, nameY + 1, sc(sx, nameFontSize));
			g2.setColor(MC_TEXT);
			drawMixedString(g2, name, nameX, nameY, sc(sx, nameFontSize));

			drawMCIcon(g2, "resources/shop/" + item.imageName + ".png", iconX, iconY, iconSize, iconSize);

			int iconStatSize = sc(sx, SLOT_STAT_ICON_SIZE);
			int statFontSize = SLOT_STAT_FONT_SIZE;
			int textYOffset = sc(sy, 20);

			int statY = y + h - sc(sy, SLOT_STAT_Y_OFFSET_FROM_BOTTOM);

			int atkTextW = g2.getFontMetrics(mcFont(statFontSize)).stringWidth(String.valueOf(item.atk));
			int atkTotalW = iconStatSize + sc(sx, 4) + atkTextW;
			int atkStartX = x + (w / 2 - atkTotalW) / 2 + sc(sx, 4);
			drawMCIcon(g2, "resources/icon/ATK.png", atkStartX, statY, iconStatSize, iconStatSize);
			g2.setColor(new Color(220, 80, 80));
			drawMixedString(g2, String.valueOf(item.atk), atkStartX + iconStatSize + sc(sx, 4), statY + textYOffset,
					statFontSize);

			int defTextW = g2.getFontMetrics(mcFont(statFontSize)).stringWidth(String.valueOf(item.def));
			int defTotalW = iconStatSize + sc(sx, 4) + defTextW;
			int defStartX = x + w / 2 + (w / 2 - defTotalW) / 2 - sc(sx, 4);
			drawMCIcon(g2, "resources/icon/DEF.png", defStartX, statY, iconStatSize, iconStatSize);
			g2.setColor(new Color(100, 160, 220));
			drawMixedString(g2, String.valueOf(item.def), defStartX + iconStatSize + sc(sx, 4), statY + textYOffset,
					statFontSize);

			int priceFontSize = SLOT_PRICE_FONT_SIZE;
			int priceIconSize = sc(sx, SLOT_PRICE_ICON_SIZE);
			int iconYOffset = sc(sy, 20);

			String priceStr = String.valueOf(item.price);
			g2.setFont(mcFont(sc(sx, priceFontSize)));
			FontMetrics pFm = g2.getFontMetrics();
			int priceStartX = x + (w - (priceIconSize + sc(sx, 4) + pFm.stringWidth(priceStr))) / 2;
			int priceY = y + h - sc(sy, SLOT_PRICE_Y_OFFSET_FROM_BOTTOM);

			drawMCIcon(g2, "resources/icon/Coin.png", priceStartX, priceY - iconYOffset, priceIconSize, priceIconSize);
			g2.setColor(MC_GOLD);
			drawMixedString(g2, priceStr, priceStartX + priceIconSize + sc(sx, 4), priceY, sc(sx, priceFontSize));

			String status = getItemStatus(item, TAB_NAMES[selectedTab]);
			if (!status.equals("AVAILABLE")) {
				g2.setColor(new Color(0, 0, 0, 165)); 
				g2.fillRect(x, y, w, h);

				String overlayText = switch (status) {
					case "OWNED" -> "보유 중";
					case "LOCKED" -> "잠김";
					case "SLOT_FULL" -> "공간 없음";
					default -> "";
				};

				g2.setFont(mcFont(sc(sx, 16)));
				FontMetrics sfm = g2.getFontMetrics();
				int tx = x + (w - sfm.stringWidth(overlayText)) / 2;
				int ty = y + (h + sfm.getAscent() - sfm.getDescent()) / 2;

				g2.setColor(Color.BLACK);
				g2.drawString(overlayText, tx + 1, ty + 1);
				g2.setColor(status.equals("LOCKED") ? Color.LIGHT_GRAY : Color.YELLOW);
				g2.drawString(overlayText, tx, ty);
			}
		}

		private void drawDetailPanel(Graphics2D g2, int x, int y, int w, int h, ShopItem sel, double sx, double sy) {
			if (sel == null) {
				g2.setColor(new Color(120, 120, 120));
				drawMixedString(g2, "아이템을 선택하세요", x + sc(sx, 15), y + sc(sy, 30), sc(sx, 12));
				buyBtnBounds = null;
				return;
			}

			int nameFontSize = DETAIL_NAME_FONT;
			int rarityFontSize = DETAIL_RARITY_FONT;
			int imageYOffset = sc(sy, DETAIL_IMG_Y_OFFSET);
			int bigSize = sc(sx, DETAIL_IMG_SIZE);

			int statFontSize = DETAIL_STAT_FONT;
			int statIconSize = sc(sx, DETAIL_STAT_ICON_SIZE);
			int statIconGap = sc(sx, DETAIL_STAT_ICON_GAP);
			int statTextYOffset = sc(sy, DETAIL_STAT_TEXT_Y_OFFSET);
			int statStartYOffset = sc(sy, DETAIL_STAT_START_Y);
			int statLineSpacing = sc(sy, DETAIL_STAT_LINE_SPACING);

			int descFontSize = DETAIL_DESC_FONT;
			int descLineSpacing = sc(sy, DETAIL_DESC_LINE_SPACING);
			int descStartYOffset = sc(sy, DETAIL_DESC_START_Y_OFFSET);

			int priceFontSize = DETAIL_PRICE_FONT;
			int priceIconSize = sc(sx, DETAIL_PRICE_ICON_SIZE);
			int priceGap = sc(sx, DETAIL_PRICE_GAP);
			int priceY = y + h - sc(sy, DETAIL_PRICE_Y_OFFSET_FROM_BOTTOM);
			int priceIconYOffset = sc(sy, DETAIL_PRICE_ICON_Y_OFFSET);
			int priceTextYOffset = sc(sy, DETAIL_PRICE_TEXT_Y_OFFSET);

			int buyBtnX = sc(sx, BUY_BTN_X);
			int buyBtnY = sc(sy, BUY_BTN_Y);
			int buyBtnW = sc(sx, BUY_BTN_W);
			int buyBtnH = sc(sy, BUY_BTN_H);

			g2.setFont(mcFontEN(sc(sx, nameFontSize)));
			g2.setColor(new Color(0, 0, 0, 160));
			drawMixedString(g2, sel.name, x + sc(sx, 32), y + sc(sy, 47), sc(sx, nameFontSize));
			g2.setColor(sel.rarity.color);
			drawMixedString(g2, sel.name, x + sc(sx, 31), y + sc(sy, 46), sc(sx, nameFontSize));

			g2.setColor(sel.rarity.color);
			drawMixedString(g2, sel.rarity.label, x + sc(sx, 32), y + sc(sy, 72), sc(sx, rarityFontSize));

			drawMCIcon(g2, "resources/shop/" + sel.imageName + ".png", x + (w - bigSize) / 2, y + imageYOffset, bigSize,
					bigSize);

			int statX = x + sc(sx, 32);
			int statY = y + statStartYOffset;

			drawMCIcon(g2, "resources/icon/ATK.png", statX, statY, statIconSize, statIconSize);
			g2.setColor(new Color(200, 200, 200));
			drawMixedString(g2, "공격력", statX + statIconGap, statY + statTextYOffset, sc(sx, statFontSize));
			g2.setFont(mcFont(sc(sx, statFontSize)));
			FontMetrics fm = g2.getFontMetrics();
			g2.setColor(MC_TEXT);
			drawMixedString(g2, String.valueOf(sel.atk), x + w - sc(sx, 20) - fm.stringWidth(String.valueOf(sel.atk)),
					statY + statTextYOffset, sc(sx, statFontSize));

			statY += statLineSpacing;
			drawMCIcon(g2, "resources/icon/DEF.png", statX, statY, statIconSize, statIconSize);
			g2.setColor(new Color(200, 200, 200));
			drawMixedString(g2, "방어력", statX + statIconGap, statY + statTextYOffset, sc(sx, statFontSize));
			g2.setColor(MC_TEXT);
			drawMixedString(g2, String.valueOf(sel.def), x + w - sc(sx, 20) - fm.stringWidth(String.valueOf(sel.def)),
					statY + statTextYOffset, sc(sx, statFontSize));

			statY += descStartYOffset;
			g2.setColor(MC_TEXT);
			String[] descLines = sel.desc.split("\n");
			for (String line : descLines) {
				drawMixedString(g2, line, statX, statY, sc(sx, descFontSize));
				statY += descLineSpacing;
			}

			g2.setFont(mcFont(sc(sx, priceFontSize)));
			FontMetrics priceFm = g2.getFontMetrics();
			int priceTextW = priceFm.stringWidth(String.valueOf(sel.price));
			int priceTotalW = priceIconSize + priceGap + priceTextW;
			int priceStartX = x + (w - priceTotalW) / 2;

			drawMCIcon(g2, "resources/icon/Coin.png", priceStartX, priceY + priceIconYOffset, priceIconSize,
					priceIconSize);
			g2.setColor(MC_GOLD);
			drawMixedString(g2, String.valueOf(sel.price), priceStartX + priceIconSize + priceGap,
					priceY + priceTextYOffset, sc(sx, priceFontSize));

			// ★ [수정 영역] 코인 부족 관련 예외 블록을 지워 "구매하기" 상태가 상시 유지되도록 변경
			String status = getItemStatus(sel, TAB_NAMES[selectedTab]);
			String btnLabel = "구매하기";
			boolean isButtonActive = true;

			if (!status.equals("AVAILABLE")) {
				isButtonActive = false;
				btnLabel = switch (status) {
					case "OWNED" -> "이미 보유 중";
					case "LOCKED" -> "이전 무기 필요";
					case "SLOT_FULL" -> "슬롯 부족";
					default -> "구매 불가";
				};
			}

			buyBtnBounds = new Rectangle(buyBtnX, buyBtnY, buyBtnW, buyBtnH);
			drawMCButton(g2, buyBtnX, buyBtnY, buyBtnW, buyBtnH, btnLabel, BUY_BTN_FONT_SIZE, isButtonActive, sx, sy);
		}

		private void drawInventory(Graphics2D g2, int x, int y, int slotW, int slotH, int gap, double sx, double sy) {
			int titleFontSize = INV_TITLE_FONT_SIZE;
			int qtyFontSize = INV_QTY_FONT_SIZE;

			g2.setColor(new Color(200, 200, 200));
			g2.setFont(mcFontEN(sc(sx, titleFontSize)));
			g2.drawString("INVENTORY", x, y - sc(sy, 12));

			List<String[]> entries = buildHotbarEntries();

			for (int i = 0; i < 9; i++) {
				int slotX = x + i * (slotW + gap);
				if (i < entries.size() && entries.get(i)[0].equals(lastBoughtImageName)) {
					g2.setColor(new Color(180, 160, 60, 100));
					g2.fillRect(slotX, y, slotW, slotH);
				}
				if (i < entries.size()) {
					String[] entry = entries.get(i);
					int paddingX = sc(sx, 4);
					int paddingY = sc(sy, 4);
					drawMCIcon(g2, "resources/shop/" + entry[0] + ".png", slotX + paddingX, y + paddingY,
							slotW - (paddingX * 2), slotH - (paddingY * 2));

					if (!entry[1].isEmpty()) {
						g2.setColor(new Color(0, 0, 0, 180));
						drawMixedString(g2, entry[1], slotX + slotW - sc(sx, 13), y + slotH - sc(sy, 3),
								sc(sx, qtyFontSize));
						g2.setColor(Color.WHITE);
						drawMixedString(g2, entry[1], slotX + slotW - sc(sx, 14), y + slotH - sc(sy, 4),
								sc(sx, qtyFontSize));
					}
				}
			}
		}

		private List<String[]> buildHotbarEntries() {
			List<String[]> entries = new ArrayList<>();
			if (steve.getWeapon() != null)
				entries.add(new String[] { steve.getWeapon().getClass().getSimpleName(), "" });
			for (skill.active.ActiveSkill skill : steve.getActiveSkills())
				if (skill != null)
					entries.add(new String[] { skill.getClass().getSimpleName(), "" });
			for (skill.consumable.ConsumableSkill item : steve.getConsumables())
				if (item != null && item.getQuantity() > 0)
					entries.add(new String[] { item.getClass().getSimpleName(), String.valueOf(item.getQuantity()) });
			return entries;
		}

		private void drawMCButton(Graphics2D g2, int x, int y, int w, int h, String label, int fontSize, boolean active, double sx,
				double sy) {
			if (active) {
				g2.setColor(new Color(255, 255, 255, 15));
			} else {
				g2.setColor(new Color(45, 45, 45, 200)); 
			}
			g2.fillRect(x, y, w, h);

			g2.setFont(mcFont(sc(sx, fontSize)));
			FontMetrics fm = g2.getFontMetrics();
			int tx = x + (w - fm.stringWidth(label)) / 2;
			int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;

			g2.setColor(new Color(0, 0, 0, 180));
			drawMixedString(g2, label, tx + 1, ty + 1, sc(sx, fontSize));
			
			if (active) {
				g2.setColor(MC_TEXT);
			} else {
				g2.setColor(new Color(110, 110, 110)); 
			}
			drawMixedString(g2, label, tx, ty, sc(sx, fontSize));
		}

		private void drawMCIcon(Graphics2D g2, String path, int x, int y, int w, int h) {
			try {
				ImageIcon ic = new ImageIcon(path);
				if (ic.getIconWidth() <= 0)
					return;

				Image img = ic.getImage();
				int iw = ic.getIconWidth(), ih = ic.getIconHeight();
				double scale = Math.min((double) w / iw, (double) h / ih);
				int dw = (int) (iw * scale), dh = (int) (ih * scale);
				int dx = x + (w - dw) / 2, dy = y + (h - dh) / 2;

				Object old = g2.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(img, dx, dy, dw, dh, null);
				if (old != null)
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, old);
			} catch (Exception ignored) {
			}
		}

		private int sc(double scale, int val) {
			return (int) (scale * val);
		}

		private Font mcFont(int size) {
			return FontManager.getNeoDgm(size);
		}

		private Font mcFontEN(int size) {
			return FontManager.getNeoDgm(size);
		}

		private void drawMixedString(Graphics2D g2, String text, int x, int y, int size) {
			g2.setFont(mcFont(size));
			g2.drawString(text, x, y);
		}
	}
}