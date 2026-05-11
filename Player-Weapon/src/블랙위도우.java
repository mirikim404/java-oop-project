
public class 블랙위도우 extends Player {
	
	public 블랙위도우(String name, int power, int hp, 총 weapon) {
		super(name, power, hp);
		this.setWeapon(weapon);
	}
	

    public void 총쏘기(){
    }

    public void 잠입하기(){
    }
    
    public void 전략짜기(){
    	System.out.println("블랙위도우가 열심히 머리를 굴립니다...");
    }
    
    public void attack(Player target) {
        System.out.println(this.getName() + "이/가 " + target.getName() + "을 총으로 공격합니다.");
        target.setHp(target.getHp() - this.getPower());
    }

    public void attack(Player target, Weapon weapon) {
        System.out.println("권총으로 쏘기!");
        target.setHp(target.getHp() - (this.getPower() + weapon.getPower()));
    }


}
