package entity;

import interfaces.Skillable;
import effect.StatusEffect;
import java.util.ArrayList;
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
	private List<StatusEffect> effects = new ArrayList<>();
	private int pendingLevelUps;
	private boolean isUsed = false;
	
	Scanner input = new Scanner(System.in);
	
	private static final int DEFAULT_MAX_HEALTH = 100;
	private static final int DEFAULT_ATTACK_POWER = 24;
	private static final int DEFAULT_DEFENCE_POWER = 6;
	private static final int BASE_REQUIRED_EXP = 45;

	public Steve() {
		this("Steve");
	}

	public Steve(String name) {
		super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
		this.level = 1;
		this.exp = 0;
		this.coin = 0;
		this.pendingLevelUps = 0;
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
	public void useSkill(Steve steve, Mob mob) {
		ActiveSkill[] skills = steve.getActiveSkills();
		isUsed = false;
		
		if (skills[0] == null) { // 사용할 수 있는 스킬이 없으면
			System.out.println("사용할 수 있는 스킬이 없습니다");
			return; 
		}
		System.out.println(getName() + "이/가 스킬을 사용합니다.");
		System.out.println("\n === 사용 가능한 스킬 목록 === ");

		for (int i = 0; i < skills.length; i++) {
			if (skills[i] != null) {
				//가지고 있는 스킬 꺼내서 보여줌 
				String readyText = skills[i].isReady() ? "사용 가능" : "사용 불가능";
				System.out.printf("[%d] %s (쿨타임 %d턴 남음 / %s)\n", i+1, skills[i].getClass().getSimpleName(), skills[i].getCurrentCooldown() , readyText);
			}
			
		}
			System.out.println("\n === 사용할 스킬을 선택하세요. ===");
			
			int ans = input.nextInt();
			ActiveSkill chosenSkill = skills[ans-1];
			if (chosenSkill.isReady() == true) {
				chosenSkill.use(steve, mob);
				isUsed = true; // 뭔가 선택되었는가?
			}
			else System.out.println("==아직 준비되지 않은 스킬입니다. === ");
	}

	@Override
	public void usePotion(Steve steve) {
		ConsumableSkill[] potions = this.getConsumables();
		isUsed = false; //
		if (potions[0] == null) { // 사용할 수 있는 포션이 없으면
			System.out.println("사용할 수 있는 포션이 없습니다");
			return; 
		} 
		System.out.println(getName() + "이/가 포션을 사용합니다.");
		System.out.println("\n --- 사용 가능한 포션 목록 --- ");
		
		
		for (int i = 0; i < potions.length; i++) {
			if (potions[i] != null) {
				System.out.printf("[%d] %s %d개 ", i+1, potions[i].getClass().getSimpleName(), potions[i].getQuantity());
			}
			
		}
		System.out.println("\n === 사용할 스킬을 선택하세요. ===");
		
		int ans = input.nextInt();
		ConsumableSkill chosenPotion = potions[ans-1];
		if (chosenPotion.hasStock() == true) {
			chosenPotion.use(steve);
			isUsed = true;
		}
		else System.out.println("=== 보유하지 않은 포션입니다. === ");
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
	    pendingLevelUps++;
	    System.out.println(getName() + " 레벨업! 현재 레벨: " + level);
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
	    processEffects();  // ← 추가
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

	public int getRequiredExp() {
		return BASE_REQUIRED_EXP * level;
	}

	public int getTotalAttackPower() {
		if (weapon == null) {
			return getAttackPower();
		}
		return getAttackPower() + weapon.getAttackBonus();
	}
	
	public void processEffects() {
	    effects.removeIf(effect -> {
	        effect.activate(this);
	        return effect.isExpired();
	    });
	}
	
	public boolean hasPendingLevelUp() {
	    return pendingLevelUps > 0;
	}

	public void applyLevelUpChoice(int choice) {
	    if (pendingLevelUps <= 0) return;

	    switch (choice) {
	        case 1:
	            setMaxHealth(getMaxHealth() + 20);
	            setHealth(getMaxHealth());
	            break;
	        case 2:
	            setAttackPower(getAttackPower() + 5);
	            break;
	        case 3:
	            setDefencePower(getDefencePower() + 2);
	            break;
	        default:
	            setMaxHealth(getMaxHealth() + 10);
	            setHealth(getMaxHealth());
	            break;
	    }

	    pendingLevelUps--;
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
	
	public List<StatusEffect> getEffects() { return effects; }

	public boolean getIsUsed() { return isUsed; }
	public void setIsUsed(boolean isUsed) { this.isUsed = isUsed; }

	
}
