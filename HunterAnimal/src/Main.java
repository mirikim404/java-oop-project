import animal.AdoptedAnimal;
import animal.ForestAnimal;
import animal.StrayAnimal;
import animal.ZooAnimal;
import human.Owner;
import human.Rescuer;
import human.ShelterManager;
import human.ZooManager;

public class Main {
	public static void main(String[] args) {

		System.out.println("===== 테스트 시작 =====");

		ForestAnimal wolf = new ForestAnimal("늑대", 5, "숲속", "육식");
		ForestAnimal zebra = new ForestAnimal("얼룩말", 4, "숲속", "초식");
		ForestAnimal monkey = new ForestAnimal("원숭이", 3, "숲속", "잡식");

		wolf.findingfood();
		zebra.findingfood();
		monkey.findingfood();

		System.out.println();

		StrayAnimal cat = new StrayAnimal("고양이", 2, "길거리", "잡식", "이화여대 앞", "2026-05-05");
		Rescuer rescuer = new Rescuer("김구조", 35, "서울", "구조대원", "동물구조협회");

		rescuer.rescue(cat);
		rescuer.sendAnimalInfo(cat);

		System.out.println();

		ShelterManager shelterManager = new ShelterManager("박보호", 42, "서울", "보호소관리자", "행복보호소");
		shelterManager.cure(cat);
		shelterManager.feed(cat);
		shelterManager.sendAnimalInfo(cat);

		System.out.println();

		ZooAnimal panda = new ZooAnimal("판다", 4, "동물원", "초식", "판다존", 0);
		ZooManager zooManager = new ZooManager("최동물", 39, "서울", "동물원관리자", "판다존");

		zooManager.monitor();
		zooManager.care(panda);
		panda.escape();

		System.out.println();

		AdoptedAnimal dog = new AdoptedAnimal("강아지", 1, "집", "잡식", "2026-05-05");
		Owner owner = new Owner("이보호", 29, "서울", "보호자", "아파트");

		owner.adopt(dog);
		owner.feed(dog);
		dog.actCute();

		System.out.println("===== 테스트 종료 =====");
	}
}