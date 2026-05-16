package entity.mob;

import ability.Mobability;
import effect.Burn;
import effect.StatusEffect;

import entity.*;
import entity.mob.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Mob extends Entity {

	private List<StatusEffect> effects = new ArrayList<>();
	private Mobability ability;
	protected boolean isStunned = false;
	protected boolean isBlock = false; // Block 판별 
	private int dropExp;
	private int dropCoin;
	private int burnTurns;

	public int getDropExp() {
		return dropExp;
	}

	public int getDropCoin() {
		return dropCoin;
	}

	public Mobability getAbility() {
		return ability;
	}

	public void setAbility(Mobability ability) {
		this.ability = ability;
	}

	public List<StatusEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<StatusEffect> effects) {
		this.effects = effects;
	}

	public boolean isStunned() {
		return isStunned;
	}

	public void setStunned(boolean isStunned) {
		this.isStunned = isStunned;
	}

	public int getBurnTurns() {
		return burnTurns;
	}

	public void setBurnTurns(int burnTurns) {
		this.burnTurns = burnTurns;
	}

	public Mob() {
	} // 생성자

	public Mob(String name, int maxHealth, int attackPower, int defencePower, int dropExp, int dropCoin) { // 생성자
		super(name, maxHealth, attackPower, defencePower);
		this.dropExp = dropExp;
		this.dropCoin = dropCoin;

	}

	// 상태이상 순회 처리
	public void processEffects() {
		if (effects != null) {
			effects.removeIf(effect -> {
				effect.activate(this);
				return effect.isExpired();
			});
		}
	}

	// Mob 행동
	public void act(Entity target) {
		if (ability != null) {
			ability.use(this, (Steve) target);
		} else {
			attack(target);
		}
	}

	public void attack() {
		System.out.println("공격, 플레이어(Steve)를 지정하세요");
	}


//	public void attack(Steve player) {
//		System.out.println(getName() + "이 플레이어 " + player.getName() + "을 공격합니다.");

	
	@Override
	public void attack(Entity player) {
		System.out.println(getName()+ "이"+ player.getName() +"을/를 공격합니다.");
		player.takeDamage(getAttackPower());
	}

	@Override
	public void block() {
		
		System.out.println(getName() + "이/가 공격을 막았습니다. ");
		isBlock = true;
		
	}

}
