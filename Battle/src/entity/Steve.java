package entity;

import interfaces.Skillable;
import skill.active.ActiveSkill;
import skill.consumable.ConsumableSkill;
import weapon.Weapon;
import weapon.WoodSword;
import entity.mob.Mob;

public class Steve extends Entity implements Skillable {
    private int coin;
    private String gameMode;
    private int level;
    private int exp;
    private Weapon weapon;
    private ActiveSkill[] activeSkills;
    private ConsumableSkill[] consumables;
    
    // TODO: 기본 스탯 상수값 설정 - 상의 후 변경가능
    private static final int DEFAULT_MAX_HEALTH = 100;
    private static final int DEFAULT_ATTACK_POWER = 10;
    private static final int DEFAULT_DEFENCE_POWER = 3;
    private static final int BASE_REQUIRED_EXP = 100;
    
    public Steve() {}	// 기본 생성자
    
    public Steve(String name, String gameMode) {
        super(name, DEFAULT_MAX_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_DEFENCE_POWER);
        this.coin = 0;
        this.gameMode = gameMode;
        this.level = 1;
        this.exp = 0;
        this.weapon = new WoodSword();
        this.activeSkills = new ActiveSkill[2];
        this.consumables = new ConsumableSkill[2];

        System.out.println("Steve 생성 완료: " + name + ", 모드: " + gameMode);
    }
     

    @Override
	public void attack() {
        System.out.println(getName() + "이/가 기본 공격을 합니다. 공격력: " + getTotalAttackPower());

	}
    
    public void attack(Mob target) {
        int damage = getTotalAttackPower();
        System.out.println(getName() + "이/가 " + target.getName() + "을/를 공격합니다. 데미지: " + damage);
        target.takeDamage(damage);
    }

    
    @Override
	public void block() {
		super.block();
	}

    @Override
	public void useSkill(){
        System.out.println(getName() + "이/가 스킬을 사용합니다.");
    }

    public void gainExp(){
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

    public void levelUp(){
        level++;
        // 스탯 자동 증가 - 테스트 코딩용 
        //TODO: 추후 공격력/방어력/최대체력 중 선택으로 수정 필요
        setMaxHealth(getMaxHealth() + 10);
        setAttackPower(getAttackPower() + 2);
        setDefencePower(getDefencePower() + 1);
        
        setHealth(getMaxHealth());

        System.out.println(getName() + " 레벨업! 현재 레벨: " + level);
        System.out.println("최대체력 +10, 공격력 +2, 방어력 +1");	// 테스트 코딩용 (TODO: 수정 필요)
    }

    public void gainCoin(){
    	gainCoin(0);
    }
    
    public void gainCoin(int amount) {
    	if(amount <=0) {
    		System.out.println("획득한 코인이 없습니다.");
    		return;
    	}
    	
    	coin+=amount;
    	System.out.println(getName()+"이/가 코인 "+amount + "개를 획득했습니다. 현재 코인 : "+coin);
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
    
    public void resetAfterDeath() {
    	//TODO: 죽은 뒤 소모품, 무기, 게임모드, 활성스킬 리셋여부 상의 필요
    	
    	level = 1;
    	exp = 0;
    	
        setMaxHealth(DEFAULT_MAX_HEALTH);
        setAttackPower(DEFAULT_ATTACK_POWER);
        setDefencePower(DEFAULT_DEFENCE_POWER);
        setHealth(getMaxHealth());

        consumables = new ConsumableSkill[2];

        System.out.println(getName() + "이/가 사망했습니다.");
        System.out.println("레벨과 경험치는 초기화되고, 코인은 유지됩니다. 현재 코인: " + coin);
    }
    
    private int getRequiredExp() {	
    	//TODO: 논의 및 밸런스 패치 필요
        return BASE_REQUIRED_EXP * level;
    }
    
    public int getTotalAttackPower() {
        if (weapon == null) {	// 무기 없는 경우
            return getAttackPower();
        }

        return getAttackPower() + weapon.getAttackBonus();
    }
    
    
    //getter, setter
    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
    
    public String getGameMode() {
        return gameMode;
    }

    public void setGameMode(String gameMode) {
        this.gameMode = gameMode;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public ActiveSkill[] getActiveSkills() {
        return activeSkills;
    }

    public void setActiveSkills(ActiveSkill[] activeSkills) {
        this.activeSkills = activeSkills;
    }

    public ConsumableSkill[] getConsumables() {
        return consumables;
    }

    public void setConsumables(ConsumableSkill[] consumables) {
        this.consumables = consumables;
    }

}
