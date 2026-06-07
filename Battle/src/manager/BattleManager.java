package manager;

import entity.*;
import entity.mob.*;
import skill.active.*;

import java.util.*;

public class BattleManager {
	private static final int BURN_DAMAGE = 3; // 화상 데미지 (추후 수정)

	private GameState gameState;
	private Steve steve;
	private WaveManager waveManager;
	private ShopManager shopManager;
	private Mob currentMob;
	private boolean isBlocking = false;

	private final Scanner scanner = new Scanner(System.in);

	public BattleManager() {
	}

	public BattleManager(Steve steve) {
		this.steve = steve;
		this.waveManager = new WaveManager();
		this.shopManager = new ShopManager(steve);
		this.gameState = GameState.BATTLE;
	}

	public void startGame() {
		System.out.println("=== 마인크래프트 RPG 시작 ===");
		System.out.println("플레이어: " + steve.getName());
		startWave();
	}

	public void startWave() {
		waveManager.loadCurrentWave();
		List<Mob> mobs = new ArrayList<>(waveManager.getAliveMobs());

		System.out.println("\n=== 웨이브 " + waveManager.getCurrentWave() + " 시작 ===");

		for (Mob mob : mobs) {
			System.out.println("- " + mob.getName() + " 등장!");
		}

		for (Mob mob : mobs) {
			if (mob.isAlive()) {
				currentMob = mob;
				runBattle(mob);

				if (!steve.isAlive()) {
					handleDeath();
					return;
				}
			}
		}

		handleWaveClear();
	}

	private void runBattle(Mob mob) {
		System.out.println("\n--- " + mob.getName() + "와(과) 전투 시작 ---");

		while (mob.isAlive() && steve.isAlive()) {
			processTurn(mob);
		}
	}

	public void processTurn(Mob mob) {
		processPlayerTurn(mob);

		if (!mob.isAlive()) {
			handleMobDeath(mob);
			return;
		}

		processMobTurn(mob);
		steve.onTurnEnd(); // 턴 종료: 스킬 쿨타임 감소
	}

	public void processPlayerTurn(Mob mob) {
		System.out.println("\n[내 턴] HP: " + steve.getHealth() + " / " + steve.getMaxHealth());
		System.out.println("[1] 공격  [2] 막기  [3] 스킬 [4] 포션");

		int input = scanner.nextInt();

		switch (input) {
		case 1 -> {
			steve.attack(mob);
			System.out.println(mob.getName() + " HP: " + mob.getHealth());
		}
		case 2 -> {
			steve.block();
			isBlocking = true; // 막기 플래그 on
			System.out.println("막기 자세를 취했다!");
		}
		case 3 -> { //스킬 사용
			steve.useSkill(steve, mob);
			processPlayerTurn(mob);
		}
		case 4 -> { //포션 사용
			steve.usePotion(steve);
			processPlayerTurn(mob);
		}
		default -> {
			System.out.println("잘못된 입력");
			processPlayerTurn(mob);
		}

		}
	}

	public void processMobTurn(Mob mob) {
		System.out.println("\n[" + mob.getName() + "의 턴]");

		mob.processEffects();

		if (mob.isStunned()) {
			System.out.println(mob.getName() + "은(는) 스턴 상태! 행동 불가");
			mob.setStunned(false);
			isBlocking = false;
			return;
		}

		if (!(mob instanceof Creeper) && isBlocking) { // 크리퍼는 막기 무시
			System.out.println(mob.getName() + "이 공격했지만 막혔다!");
			isBlocking = false; // 막기 해제
			return;
		}

		mob.act(steve);
		System.out.println("스티브 HP: " + steve.getHealth());
	}

	public void handleMobDeath(Mob mob) {
		System.out.println(mob.getName() + "을(를) 처치했다!");

		steve.gainExp(mob.getDropExp());
		steve.gainCoin(mob.getDropCoin());
		waveManager.removeMob(mob);

		System.out.println("EXP +" + mob.getDropExp() + " / 코인 +" + mob.getDropCoin());
	}

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

	public void handleWaveClear() {
		setGameState(GameState.WAVE_CLEAR);
		int bonus = waveManager.getCurrentWave() * 20;
		steve.gainCoin(bonus);
		System.out.println("\n=== 웨이브 " + waveManager.getCurrentWave() + " 클리어! ===");
		System.out.println("클리어 보너스 코인 +" + bonus);

		if (waveManager.isLastWave()) {
			handleVictory();
			return;
		}

		setGameState(GameState.SHOP);
		shopManager.enterShop(GameState.WAVE_CLEAR);

		waveManager.announceMobs();
		waveManager.nextWave();

		setGameState(GameState.BATTLE);
		startWave();
	}

	public void handleDeath() {
		setGameState(GameState.DEAD);
		System.out.println("\n=== 사망했습니다 ===");

		steve = steve.resetAfterDeath(); // 코인/무기/스킬 유지하고 새 Steve 객체로 교체
		shopManager.setSteve(steve);
		shopManager.enterShop(GameState.DEAD);
		shopManager.showRestartMenu();

		this.waveManager = new WaveManager();

		setGameState(GameState.BATTLE);
		startGame();
	}

	public void handleVictory() {
		setGameState(GameState.VICTORY);
		System.out.println("\n=== 엔더드래곤을 처치했다! ===");
		System.out.println("축하합니다, " + steve.getUsername() + "! 모든 웨이브를 클리어했습니다!");
	}

	public void checkPhase() {
		if (currentMob instanceof EnderDragon dragon) {
			// TODO: EnderDragon 구현 후 활성화
			// dragon.updatePhase();
		}
	}

	public void setGameState(GameState state) {
		this.gameState = state;
	}

	public GameState getGameState() {
		return gameState;
	}
}