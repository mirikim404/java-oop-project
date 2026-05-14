package entity.mob;

import ability.Mobability;
import effect.StatusEffect;
import entity.*;
import entity.mob.*;

public abstract class Mob extends Entity implements StatusEffect {
    
	private StatusEffect[] effects;
    private Mobability ability;
    protected boolean isStunned = false;
    protected int burnTurns = 0;
    
    private int dropExp;
    private int dropCoin;
    
    // 생성자 
    public Mob() {}
    public Mob(String name, int maxHealth, int attackPower, int defencePower) { //생성자
		super(name, maxHealth, attackPower, defencePower);
		this.dropExp = dropExp;
		this.dropCoin = dropCoin;
    }
	
    
    // 상태이상 순회 처리
    public void processEffects() {
    	if (effects != null) {
    		for (StatusEffect effect : effects) {
    			if (effect != null) {
    				effect.activate();
    			}
    		}
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
    
    @Override
    public void block() {
    	System.out.println(getName() + "이/가 피해를 받았습니다. ");
    }
    
    @Override
   	public void activate() {}
   		// TODO Auto-generated method stub
   		
    
    // getter, setter
    public Mobability getAbility() { return ability; }
    public void setAbility(Mobability ability) { this.ability = ability; }

    public StatusEffect[] getEffects() { return effects; }
    public void setEffects(StatusEffect[] effects) { this.effects = effects; }

    public boolean isStunned() { return isStunned; }
    public void setStunned(boolean isStunned) { this.isStunned = isStunned; }

    public int getBurnTurns() { return burnTurns; }
    public void setBurnTurns(int burnTurns) { this.burnTurns = burnTurns; }

    public int getDropExp() { return dropExp; }
    public int getDropCoin() { return dropCoin; }

	
    

}

