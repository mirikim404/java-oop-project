package entity;

import interfaces.Skillable;
import skill.active.ActiveSkill;
import skill.consumable.ConsumableSkill;
import entity.mob.Mob;
import weapon.Weapon;
import weapon.WoodSword;
import java.util.Scanner;
import java.util.List;

public class Steve extends Entity implements Skillable {
	private int coin; // static 제거
	private int level;
	private int exp;
	private Weapon weapon;
	private ActiveSkill[] activeSkills;
	private ConsumableSkill[] consumables;

	private static final int DEFAULT_MAX_HEALTH = 100;
	private static final int DEFAULT_ATTACK_POWER = 10;
	private static final int DEFAULT_DEFENCE_POWER = 3;
	private static final int BASE_REQUIRED_EXP = 100;

	public Steve() {
		this("Steve");
	}

	public Steve(String name) {
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
		this.level = 1;
		this.exp = 0;
		this.coin = 0;
		this.weapon = new WoodSword();
		this.activeSkills = new ActiveSkill[2];
		this.consumables = new ConsumableSkill[2];
		System.out.println("Steve 생성 완료: " + name);
	}

	@Override
	public void attack(Entity target) {
		int damage = getTotalAttackPower();
		System.out.println(getName() + "이/가 " + target.getName() + "을/를 공격합니다. 데미지: " + damage);
		target.takeDamage(damage);
	}

	@Override
	public void block() {
		System.out.println(getName() + "이/가 방어 자세를 취합니다.");
		// TODO: 실제 피해 무효화는 BattleManager에서 처리
	}

	@Override
	public void useSkill(Mob target, List<Mob> aliveMobs) {
		System.out.println(getName() + "이/가 스킬을 사용합니다.");
		// TODO: 스킬 선택 로직 필요
	}

	public void gainExp() {
		gainExp(0);
	}

	public void gainExp(int amount) {
		if (amount <= 0) {
			System.out.println("획득한 경험치가 없습니다.");
			return;
		}
		exp += amount;
		System.out.println(getName() + "이/가 경험치 " + amount + "을/를 획득했습니다. 현재 EXP: " + exp);

		while (exp >= getRequiredExp()) {
			exp -= getRequiredExp();
			levelUp();
		}
	}

	public void levelUp() {
		level++;
		Scanner sc = new Scanner(System.in);
		System.out.println(getName() + " 레벨업! 현재 레벨: " + level);
		System.out.println("증가시킬 스탯을 선택하세요.");
		System.out.println("1. 최대체력 +10");
		System.out.println("2. 공격력 +2");
		System.out.println("3. 방어력 +1");
		System.out.print("선택: ");

		int choice = sc.nextInt();
		switch (choice) {
			case 1:
				setMaxHealth(getMaxHealth() + 10);
				setHealth(getMaxHealth());
				System.out.println("최대체력이 증가했습니다. 현재 최대체력: " + getMaxHealth());
				break;
			case 2:
				setAttackPower(getAttackPower() + 2);
				System.out.println("공격력이 증가했습니다. 현재 공격력: " + getAttackPower());
				break;
			case 3:
				setDefencePower(getDefencePower() + 1);
				System.out.println("방어력이 증가했습니다. 현재 방어력: " + getDefencePower());
				break;
			default:
				System.out.println("잘못된 입력입니다. 기본값으로 최대체력을 증가시킵니다.");
				setMaxHealth(getMaxHealth() + 10);
				setHealth(getMaxHealth());
				break;
		}
	}

	public void gainCoin() {
		gainCoin(0);
	}

	public void gainCoin(int amount) {
		if (amount <= 0) {
			System.out.println("획득한 코인이 없습니다.");
			return;
		}
		this.coin += amount;
		System.out.println(getName() + "이/가 코인 " + amount + "개를 획득했습니다. 현재 코인: " + this.coin);
	}

	public void onTurnEnd() {
		if (activeSkills != null) {
			for (ActiveSkill skill : activeSkills) {
				if (skill != null) {
					skill.decrementCooldown();
				}
			}
		}
		System.out.println(getName() + "의 턴이 종료되었습니다.");
	}

	public Steve resetAfterDeath() {
		Steve newSteve = new Steve(getName());
		newSteve.setCoin(this.coin); // 코인 유지 핵심!
		newSteve.setWeapon(this.weapon);
		newSteve.setActiveSkills(this.activeSkills);
		newSteve.setConsumables(this.consumables);
		System.out.println(getName() + "이/가 사망했습니다. 새 Steve 객체로 다시 시작합니다.");
		System.out.println("coin, weapon, activeSkills, consumables는 유지됩니다. 현재 coin: " + this.coin);
		return newSteve;
	}

	private int getRequiredExp() {
		return BASE_REQUIRED_EXP * level;
	}

	public int getTotalAttackPower() {
		if (weapon == null) {
			return getAttackPower();
		}
		return getAttackPower() + weapon.getAttackBonus();
	}

	// getter, setter
	public int getCoin() { return coin; }
	public void setCoin(int coin) { this.coin = coin; }

	public int getLevel() { return level; }
	public int getExp() { return exp; }

	public Weapon getWeapon() { return weapon; }
	public void setWeapon(Weapon weapon) { this.weapon = weapon; }

	public ActiveSkill[] getActiveSkills() { return activeSkills; }
	public void setActiveSkills(ActiveSkill[] activeSkills) { this.activeSkills = activeSkills; }

	public ConsumableSkill[] getConsumables() { return consumables; }
	public void setConsumables(ConsumableSkill[] consumables) { this.consumables = consumables; }

	public String getUsername() { return getName(); }
}