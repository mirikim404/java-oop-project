package animal;
public class ZooAnimal extends Animal {
    private String displayZone;
    private int escapeCount;

    public ZooAnimal() {
    }

    public ZooAnimal(String name, int age, String residence, String diet, String displayZone, int escapeCount) {
        super(name, age, residence, diet);
        this.displayZone = displayZone;
        this.escapeCount = escapeCount;
    }

    
    public void escape(){
        escapeCount++;
        System.out.println(getName() + "이/가 동물원에서 탈출했습니다. (탈출 횟수: " + escapeCount+")");
    }

}
