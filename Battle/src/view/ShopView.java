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

	// 이미지
	private Image bgImage;
	private Image tabActiveImg;

	// 색상 (배경 이미지가 보이도록 투명도 조정)
	private static final Color MC_TEXT = new Color(255, 255, 255);
	private static final Color MC_TEXT_DARK = new Color(63, 63, 63);
	private static final Color MC_GOLD = new Color(255, 215, 0);
	private static final Color MC_SLOT_BG = new Color(0, 0, 0, 0); // 배경 이미지 슬롯 활용 (투명)
	private static final Color MC_SLOT_SEL = new Color(80, 110, 160, 100); // 선택 시 옅은 하이라이트
	private static final Color MC_SLOT_HOV = new Color(255, 255, 255, 30); // 호버 시 옅은 하이라이트

	// 선택된 탭
	private int selectedTab = 0; // 0=무기, 1=스킬, 2=포션
	private static final String[] TAB_NAMES = { "무기", "스킬", "포션" };
	private static final String[] TAB_ICONS = { "⚔", "✨", "🧪" };

	// 선택된 슬롯
	private int hoveredSlot = -1;
	private int selectedSlot = -1;

	// 아이템 정의
	private static class ShopItem {
		String imageName, name, desc;
		int atk, def, price;
		Runnable onBuy;

		ShopItem(String imageName, String name, String desc, int atk, int def, int price, Runnable onBuy) {
			this.imageName = imageName;
			this.name = name;
			this.desc = desc;
			this.atk = atk;
			this.def = def;
			this.price = price;
			this.onBuy = onBuy;
		}
	}

	private List<ShopItem>[] tabItems;
	private List<Rectangle> slotBounds = new ArrayList<>();

	// 메시지
	private String message = "";
	private String lastBoughtImageName = "";
	private javax.swing.Timer msgTimer;

	@SuppressWarnings("unchecked")
	public ShopView(GameFrame gameFrame, Steve steve, WaveManager waveManager, int wave) {
		this.gameFrame = gameFrame;
		this.steve = steve;
		this.waveManager = waveManager;
		this.shopManager = new ShopManager(steve);
		this.wave = wave;

		tabItems = new List[3];
		buildTabItems();

		bgImage = new ImageIcon("resources/ui/shop_bg.png").getImage();
		tabActiveImg = new ImageIcon("resources/ui/tab_active.png").getImage();

		setLayout(new BorderLayout());
		setOpaque(false);

		ShopPanel shopPanel = new ShopPanel();
		add(shopPanel, BorderLayout.CENTER);

		msgTimer = new javax.swing.Timer(2500, e -> {
			message = "";
			repaint();
		});
		msgTimer.setRepeats(false);
	}

	@SuppressWarnings("unchecked")
	private void buildTabItems() {
		tabItems[0] = new ArrayList<>();
		tabItems[0].add(new ShopItem("WoodenSword", "Wooden Sword", "희귀도: 일반", 8, 0, 40,
				() -> tryBuy(() -> shopManager.buyWeapon(new StoneSword()))));
		tabItems[0].add(new ShopItem("IronSword", "Iron Sword", "희귀도: 고급", 16, 0, 75,
				() -> tryBuy(() -> shopManager.buyWeapon(new IronSword()))));
		tabItems[0].add(new ShopItem("DiamondSword", "Diamond Sword", "희귀 무기", 24, 0, 115,
				() -> tryBuy(() -> shopManager.buyWeapon(new DiamondSword()))));
		tabItems[0].add(new ShopItem("NetheriteSword", "Netherite Sword", "희귀도: 전설", 32, 0, 160,
				() -> tryBuy(() -> shopManager.buyWeapon(new NetheriteSword()))));

		tabItems[1] = new ArrayList<>();
		tabItems[1].add(new ShopItem("SnowBall", "눈덩이", "스턴 (쿨타임 3턴)", 10, 0, 45,
				() -> tryBuy(() -> shopManager.buySkill(new SnowBall()))));
		tabItems[1].add(new ShopItem("FireCharge", "화염구", "화상 2턴 (쿨타임 3턴)", 15, 0, 65,
				() -> tryBuy(() -> shopManager.buySkill(new FireCharge()))));

		tabItems[2] = new ArrayList<>();
		tabItems[2].add(new ShopItem("AttackPotion", "공격 포션", "다음 공격 2배", 0, 0, 18,
				() -> tryBuy(() -> shopManager.buyPotion(new AttackPotion()))));
		tabItems[2].add(new ShopItem("HealPotion", "회복 포션", "체력 회복", 0, 0, 15,
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

	// ═══════════════════════════════════════════════════
	// 메인 패널 (배경 + 전체 UI)
	// ═══════════════════════════════════════════════════
	private class ShopPanel extends JPanel {

		private List<Rectangle> tabBounds = new ArrayList<>();
		private Rectangle buyBtnBounds;
		private Rectangle nextBtnBounds;

		// 실제 배경 이미지 비율 기준 (1024x683)
		private static final double REF_W = 1024.0;
		private static final double REF_H = 683.0;

		ShopPanel() {
			setOpaque(false);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
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
					            
					            selectedSlot = (selectedSlot == i) ? -1 : i;
					            repaint();
					        }
					        return; 
					    }
					}
					if (buyBtnBounds != null && buyBtnBounds.contains(e.getPoint())) {
						if (selectedSlot >= 0 && selectedSlot < tabItems[selectedTab].size()) {
							tabItems[selectedTab].get(selectedSlot).onBuy.run();
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
					List<ShopItem> currentItems = tabItems[selectedTab]; // 현재 탭의 아이템 목록

					for (int i = 0; i < slotBounds.size(); i++) {
					    if (slotBounds.get(i).contains(e.getPoint())) {
					        // 아이템이 있을 때만 hoveredSlot을 업데이트
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

			// 1. SHOP 타이틀 + 코인 (좌측 상단 박스 내부)
			int titleX = sc(sx, 25);
			int titleY = sc(sy, 45);

			g2.setFont(mcFont(sc(sx, 22)));
			g2.setColor(MC_TEXT_DARK);
			g2.drawString("SHOP", titleX + 2, titleY + 2);
			g2.setColor(MC_TEXT);
			g2.drawString("SHOP", titleX, titleY);

			// 코인
			// 우측 정렬
			String coinStr = String.valueOf(steve.getCoin());
			g2.setFont(mcFont(sc(sx, 18)));
			FontMetrics coinFm = g2.getFontMetrics();
			int coinStrWidth = coinFm.stringWidth(coinStr);

			// 우측 끝 좌표 기준으로 위치 계산 (W = 화면 전체 너비)
			int coinBoxRightEnd = W - sc(sx, 30); // 우측 여백
			int coinAreaX = coinBoxRightEnd - coinStrWidth;
			int coinAreaY = sc(sy, 48); // 텍스트 기준 Y 좌표

			// 코인 텍스트
			g2.setColor(MC_TEXT_DARK);
			g2.drawString(coinStr, coinAreaX + 1, coinAreaY + 1);
			g2.setColor(MC_GOLD);
			g2.drawString(coinStr, coinAreaX, coinAreaY);

			// 코인 아이콘
			int coinIconSize = sc(sx, 22);
			drawMCIcon(g2, "resources/icon/Coin.png", coinAreaX - coinIconSize - sc(sx, 8), coinAreaY - sc(sy, 18),
					coinIconSize, coinIconSize);

			// 2. 탭 버튼 (왼쪽)
			int tabX = sc(sx, 18);
			int tabY = sc(sy, 107);
			int tabW = sc(sx, 130);
			int tabH = sc(sy, 64);
			int tabGap = sc(sy, 12);

			for (int i = 0; i < TAB_NAMES.length; i++) {
				int ty = tabY + i * (tabH + tabGap);
				Rectangle tb = new Rectangle(tabX, ty, tabW, tabH);
				tabBounds.add(tb);
				drawTabButton(g2, tabX, ty, tabW, tabH, TAB_ICONS[i] + " " + TAB_NAMES[i], selectedTab == i, sx);
			}

			// 3. 아이템 슬롯 그리드 (4열 2행)
			int gridX = sc(sx, 163);      // 전체 그리드의 시작 X 좌표 (왼쪽 여백)
			int gridY = sc(sy, 107);      // 전체 그리드의 시작 Y 좌표 (위쪽 여백)
			int slotSize = sc(sx, 130);   // 슬롯 1칸의 가로 너비
			int slotGapX = sc(sx, 13);     // 슬롯과 슬롯 사이의 가로 간격
			int slotGapY = sc(sy, 12);    // 슬롯과 슬롯 사이의 세로 간격
			
			//행별 슬롯 1칸의 세로 높이
			int [] rowHeights = {sc(sy, 214), sc(sy, 193)};

			List<ShopItem> items = tabItems[selectedTab];

			for (int i = 0; i < 8; i++) {
			    int row = i / 4; 
			    int col = i % 4; 
			    
			    // 현재 행에 맞는 높이 선택
			    int currentSlotH = rowHeights[row];
			    
			    // 행에 따른 y 좌표 계산 (1행은 gridY, 2행은 1행 시작점 + 1행 높이 + 간격)
			    int posY = (row == 0) ? gridY : (gridY + rowHeights[0] + slotGapY);
			    int posX = gridX + col * (slotSize + slotGapX);
			    
			    Rectangle sr = new Rectangle(posX, posY, slotSize, currentSlotH);
			    slotBounds.add(sr);

			    boolean hovered = (hoveredSlot == i);
			    boolean selected = (selectedSlot == i);
			    ShopItem item = (i < items.size()) ? items.get(i) : null;

			    // 수정된 posY와 currentSlotH를 전달
			    drawItemSlot(g2, posX, posY, slotSize, currentSlotH, item, hovered, selected, sx, sy);
			}

			// 4. 우측 상세 정보 패널
			int detailX = sc(sx, 746);
			int detailY = sc(sy, 107);
			int detailW = sc(sx, 259);
			int detailH = sc(sy, 405);

			ShopItem sel = (selectedSlot >= 0 && selectedSlot < items.size()) ? items.get(selectedSlot) : null;
			drawDetailPanel(g2, detailX, detailY, detailW, detailH, sel, sx, sy);

			// 5. 하단 인벤토리 (9칸)
			int invX = sc(sx, 16);
			int invY = sc(sy, 562);
			int invSlotSize = sc(sx, 57);
			int invGap = sc(sx, 8);
			drawInventory(g2, invX, invY, invSlotSize, invGap, sx, sy);

			// 다음 웨이브 버튼 (우측 하단)
			int nbX = sc(sx, 788);
			int nbY = sc(sy, 562);
			int nbW = sc(sx, 195);
			int nbH = sc(sy, 57);
			nextBtnBounds = new Rectangle(nbX, nbY, nbW, nbH);
			drawMCButton(g2, nbX, nbY, nbW, nbH, "다음 웨이브 ▶", false, sx, sy);

			// 메시지 표시
			if (!message.isEmpty()) {
				String displayMsg = message.replace("§a", "").replace("§c", "");
				Color msgColor = message.startsWith("§a") ? new Color(85, 255, 85) : new Color(255, 85, 85);
				g2.setFont(mcFont(sc(sx, 14)));
				g2.setColor(new Color(0, 0, 0, 200));
				g2.drawString(displayMsg, sc(sx, 166), sc(sy, 96));
				g2.setColor(msgColor);
				g2.drawString(displayMsg, sc(sx, 165), sc(sy, 95));
			}

			g2.dispose();
		}

		private void drawTabButton(Graphics2D g2, int x, int y, int w, int h, String label, boolean active, double sx) {
			// 탭 활성화 이미지 그리기
			if (active && tabActiveImg != null) {
				g2.drawImage(tabActiveImg, x, y, w, h, null);
			}

			g2.setFont(mcFont(sc(sx, 14)));
			FontMetrics fm = g2.getFontMetrics();
			int tx = x + (w - fm.stringWidth(label)) / 2;
			int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;

			// 텍스트 그림자
			g2.setColor(new Color(0, 0, 0, 200));
			g2.drawString(label, tx + 1, ty + 1);

			// 텍스트 색상 (활성화=흰색, 비활성화=회색)
			g2.setColor(active ? MC_TEXT : new Color(120, 120, 120));
			g2.drawString(label, tx, ty);
		}

		private void drawItemSlot(Graphics2D g2, int x, int y, int w, int h, ShopItem item, boolean hovered,
				boolean selected, double sx, double sy) {
			Color bg = selected ? MC_SLOT_SEL : hovered ? MC_SLOT_HOV : MC_SLOT_BG;
			g2.setColor(bg);
			g2.fillRect(x, y, w, h);

			if (item == null)
				return;

			// 1. 아이템 이름 (슬롯 맨 위)
			g2.setFont(mcFont(sc(sx, 11)));
			FontMetrics fm = g2.getFontMetrics();
			String name = item.name;
			while (fm.stringWidth(name) > w - 6 && name.length() > 4) {
				name = name.substring(0, name.length() - 1);
			}
			int nameX = x + (w - fm.stringWidth(name)) / 2;
			int nameY = y + sc(sy, 25); // 기존 115에서 상단 25로 이동
			g2.setColor(new Color(0, 0, 0, 160));
			g2.drawString(name, nameX + 1, nameY + 1);
			g2.setColor(MC_TEXT);
			g2.drawString(name, nameX, nameY);

			// 2. 검 아이콘 (이름 아래로 이동)
			int iconSize = (int) (Math.min(w, h) * 0.45);
			int iconX = x + (w - iconSize) / 2;
			int iconY = y + sc(sy, 45); // 약간 아래로 이동
			drawMCIcon(g2, "resources/shop/" + item.imageName + ".png", iconX, iconY, iconSize, iconSize);

			// 3. 공격력 / 방어력 스탯 (아이콘 아래)
			int statY = y + sc(sy, 135);
			if (item.atk >= 0) {
				drawMCIcon(g2, "resources/icon/Attack.png", x + sc(sx, 15), statY, sc(sx, 14), sc(sy, 14));
				g2.setFont(mcFont(sc(sx, 11)));
				g2.setColor(new Color(220, 80, 80));
				g2.drawString(String.valueOf(item.atk), x + sc(sx, 33), statY + sc(sy, 11));
			}
			if (item.def >= 0) { // 0이어도 표기되도록 >= 0 으로 수정
				drawMCIcon(g2, "resources/icon/Defense.png", x + w / 2 + sc(sx, 10), statY, sc(sx, 14), sc(sy, 14));
				g2.setFont(mcFont(sc(sx, 11)));
				g2.setColor(new Color(100, 160, 220));
				g2.drawString(String.valueOf(item.def), x + w / 2 + sc(sx, 28), statY + sc(sy, 11));
			}

			// 4. 코인 가격 (하단 중앙 정렬)
			String priceStr = String.valueOf(item.price);
			g2.setFont(mcFont(sc(sx, 12)));
			FontMetrics pFm = g2.getFontMetrics();
			int pStrWidth = pFm.stringWidth(priceStr);
			int pIconSize = sc(sx, 14);
			int pGap = sc(sx, 4);
			int totalCenterWidth = pIconSize + pGap + pStrWidth; // 아이콘+여백+텍스트 전체 너비

			int priceStartX = x + (w - totalCenterWidth) / 2; // 중앙 정렬 좌표
			int priceY = y + h - sc(sy, 15);

			drawMCIcon(g2, "resources/icon/Coin.png", priceStartX, priceY - sc(sy, 12), pIconSize, pIconSize);
			g2.setColor(MC_GOLD);
			g2.drawString(priceStr, priceStartX + pIconSize + pGap, priceY);
		}

		private void drawDetailPanel(Graphics2D g2, int x, int y, int w, int h, ShopItem sel, double sx, double sy) {
			if (sel == null) {
				g2.setFont(mcFont(sc(sx, 12)));
				g2.setColor(new Color(120, 120, 120));
				g2.drawString("아이템을 선택하세요", x + sc(sx, 15), y + sc(sy, 30));
				buyBtnBounds = null;
				return;
			}

			g2.setFont(mcFont(sc(sx, 18)));
			g2.setColor(MC_TEXT_DARK);
			g2.drawString(sel.name, x + sc(sx, 16), y + sc(sy, 31));
			g2.setColor(MC_TEXT);
			g2.drawString(sel.name, x + sc(sx, 15), y + sc(sy, 30));

			int bigSize = sc(sx, 90);
			int bigX = x + (w - bigSize) / 2;
			int bigY = y + sc(sy, 60);
			drawMCIcon(g2, "resources/shop/" + sel.imageName + ".png", bigX, bigY, bigSize, bigSize);

			int statX = x + sc(sx, 15);
			int statY = y + sc(sy, 175);
			drawMCIcon(g2, "resources/icon/Attack.png", statX, statY, sc(sx, 16), sc(sy, 16));
			g2.setFont(mcFont(sc(sx, 13)));
			g2.setColor(new Color(200, 200, 200));
			g2.drawString("공격력", statX + sc(sx, 22), statY + sc(sy, 13));
			g2.setColor(MC_TEXT);
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(String.valueOf(sel.atk), x + w - sc(sx, 20) - fm.stringWidth(String.valueOf(sel.atk)),
					statY + sc(sy, 13));

			statY += sc(sy, 28);
			drawMCIcon(g2, "resources/icon/Defense.png", statX, statY, sc(sx, 16), sc(sy, 16));
			g2.setColor(new Color(200, 200, 200));
			g2.drawString("방어력", statX + sc(sx, 22), statY + sc(sy, 13));
			g2.setColor(MC_TEXT);
			g2.drawString(String.valueOf(sel.def), x + w - sc(sx, 20) - fm.stringWidth(String.valueOf(sel.def)),
					statY + sc(sy, 13));

			statY += sc(sy, 35);
			g2.setFont(mcFont(sc(sx, 11)));
			g2.setColor(new Color(180, 180, 180));
			String[] descLines = wrapText(sel.desc, fm, w - sc(sx, 30));
			for (String line : descLines) {
				g2.drawString(line, statX, statY);
				statY += sc(sy, 16);
			}

			// 가격 표시 텍스트 영역 (UI 이미지의 위쪽 파란 박스 영역 매핑)
			int priceBtnW = sc(sx, 237);
			int priceBtnX = x + sc(sx, 11);
			int priceBtnY = y + sc(sy, 330);

			drawMCIcon(g2, "resources/icon/Coin.png", priceBtnX + sc(sx, 85), priceBtnY + sc(sy, 6), sc(sx, 20),
					sc(sy, 20));
			g2.setFont(mcFont(sc(sx, 16)));
			g2.setColor(MC_GOLD);
			g2.drawString(String.valueOf(sel.price), priceBtnX + sc(sx, 110), priceBtnY + sc(sy, 22));

			// 구매하기 버튼 (UI 이미지의 아래쪽 파란 박스 영역 매핑)
			int buyBtnH = sc(sy, 33);
			int buyBtnY = y + sc(sy, 372);
			buyBtnBounds = new Rectangle(priceBtnX, buyBtnY, priceBtnW, buyBtnH);
			drawMCButton(g2, priceBtnX, buyBtnY, priceBtnW, buyBtnH, "구매하기", false, sx, sy);
		}

		private void drawInventory(Graphics2D g2, int x, int y, int slotSize, int gap, double sx, double sy) {

			g2.setFont(mcFont(sc(sx, 13)));
			g2.setColor(new Color(200, 200, 200));
			g2.drawString("INVENTORY", x, y - sc(sy, 12));

			int slots = 9;
			List<String[]> entries = buildHotbarEntries();

			for (int i = 0; i < slots; i++) {
				int slotX = x + i * (slotSize + gap);
				boolean flash = i < entries.size() && entries.get(i)[0].equals(lastBoughtImageName);

				if (flash) {
					g2.setColor(new Color(180, 160, 60, 100));
					g2.fillRect(slotX, y, slotSize, slotSize);
				}

				if (i < entries.size()) {
					String[] entry = entries.get(i);
					drawMCIcon(g2, "resources/shop/" + entry[0] + ".png", slotX + sc(sx, 4), y + sc(sy, 4),
							slotSize - sc(sx, 8), slotSize - sc(sy, 8));
					if (!entry[1].isEmpty()) {
						g2.setFont(mcFont(sc(sx, 10)));
						g2.setColor(new Color(0, 0, 0, 180));
						g2.drawString(entry[1], slotX + slotSize - sc(sx, 13), y + slotSize - sc(sy, 3));
						g2.setColor(Color.WHITE);
						g2.drawString(entry[1], slotX + slotSize - sc(sx, 14), y + slotSize - sc(sy, 4));
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

		private void drawMCButton(Graphics2D g2, int x, int y, int w, int h, String label, boolean pressed, double sx,
				double sy) {
			// 버튼 배경은 투명하게 하고 마우스 오버/클릭 효과를 위한 연한 오버레이만 둠
			g2.setColor(new Color(255, 255, 255, 15));
			g2.fillRect(x, y, w, h);

			g2.setFont(mcFont(sc(sx, 14)));
			FontMetrics fm = g2.getFontMetrics();
			int tx = x + (w - fm.stringWidth(label)) / 2;
			int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
			g2.setColor(new Color(0, 0, 0, 180));
			g2.drawString(label, tx + 1, ty + 1);
			g2.setColor(MC_TEXT);
			g2.drawString(label, tx, ty);
		}

		private void drawMCIcon(Graphics2D g2, String path, int x, int y, int w, int h) {
			try {
				if (path.startsWith("resources/shop/") && path.endsWith(".png")) {
					String cleanPath = path.substring(0, path.length() - 4) + "_clean.png";
					if (new java.io.File(cleanPath).exists())
						path = cleanPath;
				}
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

		private String[] wrapText(String text, FontMetrics fm, int maxWidth) {
			if (fm.stringWidth(text) <= maxWidth)
				return new String[] { text };
			List<String> lines = new ArrayList<>();
			String[] words = text.split(" ");
			StringBuilder cur = new StringBuilder();
			for (String word : words) {
				String test = cur.length() == 0 ? word : cur + " " + word;
				if (fm.stringWidth(test) > maxWidth) {
					if (cur.length() > 0)
						lines.add(cur.toString());
					cur = new StringBuilder(word);
				} else {
					cur = new StringBuilder(test);
				}
			}
			if (cur.length() > 0)
				lines.add(cur.toString());
			return lines.toArray(new String[0]);
		}

		private int sc(double scale, int val) {
			return (int) (scale * val);
		}

		private Font mcFont(int size) {
			return new Font("Monospaced", Font.BOLD, Math.max(size, 8));
		}
	}
}