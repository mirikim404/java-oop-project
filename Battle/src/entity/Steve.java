package entity;

public class Steve extends Entity implements Skillable {
    private int coin;
    private String gameMode;
    private int level;
    private int exp;
    private int maxHealth;
    private int health;
    private Weapon weapon;
    private ActiveSkill[] activeSkills;
    private ConsumableSkill[] consumables;

    public void attack(){
    }

    public void block(){
    }

    public void useSkill(){
    }

    public void gainExp(){
    }

    public void levelUp(){
    }

    public void gainCoin(){
    }

    public void onTurnEnd(){
    }

}
