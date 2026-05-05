package human;
import animal.Animal;
import common.ISendAnimalInfo;

public class ShelterManager extends Human implements ISendAnimalInfo {
	private String shelterName;
	private Animal[] shelterAnimals;

	public ShelterManager() {
	}

	public ShelterManager(String name, int age, String residence, String occupation, String shelterName) {
		super(name, age, residence, occupation);
		this.shelterName = shelterName;
		this.shelterAnimals = new Animal[10];
	}

	public void sendAnimalInfo(Animal animal) {
        System.out.println(getName() + " 보호소관리자가 동물원관리자에게 동물 정보를 전달합니다.");
        System.out.println("전달 정보: " + animal);

	}

	public void cure(Animal animal) {
        System.out.println(getName() + " 보호소관리자가 " + animal.getName() + "을/를 치료합니다.");
	}

}
