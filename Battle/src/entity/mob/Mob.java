package entity.mob;
import ability.Mobability;
import effect.StatusEffect;
import entity.Entity;

public abstract class Mob extends Entity implements StatusEffect {
    private StatusEffect[] effects;
    private Mobability ability;
    protected boolean isStunned = false;
    protected int burnTurns = 0;
    
    
    
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
	public Mob() {} // 생성자
    public Mob(String name, int maxHealth, int attackPower, int defencePower) { //생성자
		super(name, maxHealth, attackPower, defencePower);
    }
	
    
 
    
    @Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	
    

}

