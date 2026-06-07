public class Weapon {
    private int power;
    private String name;

    public Weapon() {}
    public Weapon(String name, int power) {
        this.name = name;
        this.power = power;
    }

    public String getName() { return name; }     
    public int getPower() { return power; }       
    public void setPower(int power) { this.power = power; }
    public void setName(String name) { this.name = name; }
}