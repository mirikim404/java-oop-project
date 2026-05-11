
public class 헐크 extends Player {
	
	public 헐크(String name, int hp, int power, 바위 weapon) {
		super(name, power, hp);
		this.setWeapon(weapon);
	}

    public void 옷찢기(){
    }
    
    public void attack(Player target) {
    	System.out.println("맨손으로 때려!");
    	super.attack(target);
    }

    public void attack(Player target, Weapon weapon) {
        System.out.println("바위로 때려!");
        target.setHp(target.getHp() - (this.getPower() + weapon.getPower()));
    }

}
