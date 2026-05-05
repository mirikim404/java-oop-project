public class AdoptedAnimal extends Animal {
    private String adoptionDate;

    public AdoptedAnimal() {
    }

    public AdoptedAnimal(String name, int age, String residence, String diet, String adoptionDate) {
        super(name, age, residence, diet);
        this.adoptionDate = adoptionDate;
    }

    public void actCute(){
        System.out.println(getName() + "이/가 보호자에게 애교를 부립니다.");
    }

}
