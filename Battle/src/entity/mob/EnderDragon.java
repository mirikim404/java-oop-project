package entity.mob;
public class EnderDragon extends Mob {
    private int MAX_HEALTH;
    private int phase;

    public EnderDragon() {} // 기본 생성자
    
	public EnderDragon (String name, int health, int attackPower, int defencePower) { //생성자
		super(name, health, attackPower, defencePower);
	}
	
    public void breathFire(){
    }

    public void fly(){
    }

    public void chargeAttack(){
    }

	@Override
	public void attack() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void block() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void takeDamage() {
		// TODO Auto-generated method stub
		
	}

}
