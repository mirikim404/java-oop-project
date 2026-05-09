package entity.mob;
import ability.Mobability;
import effect.StatusEffect;
import entity.Entity;

public abstract class Mob extends Entity implements StatusEffect {
    private StatusEffect[] effects;
    private Mobability ability;
    protected boolean isStunned = false;
    protected int burnTurns = 0;
    
    public Mob() {} // 생성자
    public Mob(String name, int health, int attackPower, int defencePower) { //생성자
		super(name, health, attackPower, defencePower);
    }
	@Override
	public void activate() {
		// TODO Auto-generated method stub
		
	}

	
    

}

