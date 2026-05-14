package entity.mob;

import ability.Mobability;
import effect.Burn;
import effect.StatusEffect;
import entity.*;
import entity.mob.*;

import java.util.ArrayList;
import java.util.List;

public abstract class Mob extends Entity implements StatusEffect {
    
	private List<StatusEffect> effects = new ArrayList<>();
    private Mobability ability;
    protected boolean isStunned = false;
    
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
            effects.removeIf(effect -> {
                effect.activate(this);
                return effect instanceof Burn && ((Burn) effect).isExpired();
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
    
    @Override
    public void block() {
    	System.out.println(getName() + "이/가 피해를 받았습니다. ");
    }
    
    @Override
    public void activate(Mob mob) {
        // TODO: 
    }
   		
    
    // getter, setter
    public Mobability getAbility() { return ability; }
    public void setAbility(Mobability ability) { this.ability = ability; }

    public List<StatusEffect> getEffects() { return effects; }
    public void setEffects(List<StatusEffect> effects) { this.effects = effects; }

    public boolean isStunned() { return isStunned; }
    public void setStunned(boolean isStunned) { this.isStunned = isStunned; }

    public int getDropExp() { return dropExp; }
    public int getDropCoin() { return dropCoin; }

	
    

}

