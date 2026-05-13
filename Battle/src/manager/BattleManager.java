package manager;

import entity.*;
import entity.mob.*;
import java.util.*;

public class BattleManager {
	private GameState gameState;
	private Steve steve;
	private WaveManager waveManager;
	private ShopManager shopManager;
	private Mob currentMob;
	
	private final Scanner scanner = new Scanner(System.in);
	
	// 생성자 -----------------------------
	public BattleManager() {}
	
	public BattleManager(Steve steve) {
		this.steve = steve;
		this.waveManager = new WaveManager();
		this.shopManager = new ShopManager(steve);
		this.gameState = GameState.BATTLE;
	}
	
	
	// 게임 초기화 + 웨이브 1 시작
	public void startGame() {
		System.out.println("=== 마인크래프트 RPG 시작 ===");
		System.out.println("플레이어: " + steve.getUsername());
		startWave();
	}
	
	// WaveManager에서 몹 목록 받아 전투 시작
	

}
