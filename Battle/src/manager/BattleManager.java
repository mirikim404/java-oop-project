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
	public BattleManager() {
	}

	public BattleManager(Steve steve) {
		this.steve = steve;
		this.waveManager = new WaveManager();
		this.shopManager = new ShopManager(steve);
		this.gameState = GameState.BATTLE;
	}

	// 게임 초기화 + 웨이브 1 시작
	public void startGame() {
		System.out.println("=== 마인크래프트 RPG 시작 ===");
		System.out.println("플레이어: " + steve.getName());
		startWave();
	}

	// WaveManager에서 몹 목록 받아 전투 시작
	public void startWave() {
		waveManager.loadCurrentWave();
		List<Mob> mobs = waveManager.getAliveMobs();

		System.out.println("\n=== 웨이브 " + waveManager.getCurrentWave() + " 시작 ===");

		for (Mob mob : mobs) {
			System.out.println("- " + mob.getName() + " 등장!");
		}

		// 살아있는 몹 상대로 1:1 전투
		for (Mob mob : mobs) {
			if (mob.isAlive()) {
				currentMob = mob;
				runBattle(mob);

				// 스티브 사망 체크
				if (!steve.isAlive()) {
					handleDeath();
					return;
				}
			}
		}

		// 웨이브 몹 모두 처치
		handleWaveClear();
	}

	// 단일 몹과의 전투 루프
	private void runBattle(Mob mob) {
		System.out.println("\n--- " + mob.getName() + "와(과) 전투 시작 ---");

		while (mob.isAlive() && steve.isAlive()) {
			processTurn(mob);
		}
	}

	// 내 턴 → 몹 턴 순서로 턴 진행
	public void processTurn(Mob mob) {
		// 내 턴
		processPlayerTurn(mob);

		// 몹이 처치되면 몹 턴 스킵
		if (!mob.isAlive()) {
			handleMobDeath(mob);
			return;
		}

		// 몹 턴
		processMobTurn(mob);

		// 턴 종료: 스킬 쿨타임 감소
		steve.onTurnEnd();
	}

	// 공격 / 막기 / 스킬 선택 입력 처리
	public void processPlayerTurn(Mob mob) {
		System.out.println("\n[내 턴] HP: " + steve.getHealth() + " / " + steve.getMaxHealth());
		System.out.println("[1] 공격  [2] 막기  [3] 스킬");

		int input = scanner.nextInt();
		switch (input) {
			case 1 -> {
				steve.attack(mob);
				System.out.println(mob.getName() + " HP: " + mob.getHealth());
			}
			case 2 -> {
				steve.block();
				System.out.println("막기 자세를 취했다!");
			}
			case 3 -> steve.useSkill(mob, waveManager.getAliveMobs());
			default -> {
				System.out.println("잘못된 입력");
				processPlayerTurn(mob);
			}
		}
	}

	// 스턴/화상 체크 후 몹 행동 실행
	public void processMobTurn(Mob mob) {
		System.out.println("\n[" + mob.getName() + "의 턴]");

		// 상태이상 처리 (StatusEffect 목록 순회)
		mob.processEffects();

		// 스턴 체크
		if (mob.isStunned()) {
			System.out.println(mob.getName() + "은(는) 스턴 상태! 행동 불가");
			mob.setStunned(false);
			return;
		}

		// 몹 행동
		mob.act(steve);
		System.out.println("스티브 HP: " + steve.getHealth());
	}

	// EXP + 코인 지급, aliveMobs 제거, 레벨업 체크
	public void handleMobDeath(Mob mob) {
		System.out.println(mob.getName() + "을(를) 처치했다!");

		steve.gainExp(mob.getDropExp());
		steve.gainCoin(mob.getDropCoin());
		waveManager.removeMob(mob);

		System.out.println("EXP +" + mob.getDropExp() + " / 코인 +" + mob.getDropCoin());

		// 레벨업 체크는 gainExp 내부에서 처리
	}

	// 스탯 선택 → 적용 → 전투 재개
	public void handleLevelUp() {
		System.out.println("\n=== 레벨업! ===");
		System.out.println("[1] 공격력 +5  [2] 방어력 +3  [3] 최대 체력 +20");

		int input = scanner.nextInt();
		switch (input) {
			case 1 -> {
				steve.setAttackPower(steve.getAttackPower() + 5);
				System.out.println("공격력 증가!");
			}
			case 2 -> {
				steve.setDefencePower(steve.getDefencePower() + 3);
				System.out.println("방어력 증가!");
			}
			case 3 -> {
				steve.setMaxHealth(steve.getMaxHealth() + 20);
				System.out.println("최대 체력 증가!");
			}
			default -> {
				System.out.println("잘못된 입력");
				handleLevelUp();
			}
		}
	}

	// 웨이브 클리어 처리
	public void handleWaveClear() {
		setGameState(GameState.WAVE_CLEAR);
		int bonus = waveManager.getCurrentWave() * 20;
		steve.gainCoin(bonus);
		System.out.println("\n=== 웨이브 " + waveManager.getCurrentWave() + " 클리어! ===");
		System.out.println("클리어 보너스 코인 +" + bonus);

		// 마지막 웨이브면 승리
		if (waveManager.isLastWave()) {
			handleVictory();
			return;
		}

		// 상점 → 다음 웨이브 예고 → 다음 웨이브 시작
		setGameState(GameState.SHOP);
		shopManager.enterShop(GameState.WAVE_CLEAR);

		waveManager.announceMobs();
		waveManager.nextWave();

		setGameState(GameState.BATTLE);
		startWave();
	}

	// 사망 처리
	public void handleDeath() {
		setGameState(GameState.DEAD);
		System.out.println("\n=== 사망했습니다 ===");

		// 새 Steve 객체로 교체
	    steve = steve.resetAfterDeath();

		// 상점 진입
		shopManager.enterShop(GameState.DEAD);
		shopManager.showRestartMenu();

		// 재시작 선택 시 웨이브 1부터 다시
		setGameState(GameState.BATTLE);
		startGame();
	}

	// 승리 처리
	public void handleVictory() {
		setGameState(GameState.VICTORY);
		System.out.println("\n=== 엔더드래곤을 처치했다! ===");
		System.out.println("축하합니다, " + steve.getUsername() + "! 모든 웨이브를 클리어했습니다!");
	}

	// EnderDragon 체력 % 기준 페이즈 전환 체크
	public void checkPhase() {
		if (currentMob instanceof EnderDragon dragon) {
			// TODO: EnderDragon 구현 후 활성하ㅗ
			// dragon.updatePhase();
		}
	}

	// gameState 변경
	public void setGameState(GameState state) {
		this.gameState = state;
	}

	public GameState getGameState() {
		return gameState;
	}
}