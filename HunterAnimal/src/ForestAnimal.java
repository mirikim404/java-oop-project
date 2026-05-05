public class ForestAnimal extends Animal implements IFindingFood {
	public ForestAnimal() {
		
	}
	
    public ForestAnimal(String name, int age, String residence, String diet) {
        super(name, age, residence, diet);
    }
    
    public void findingfood(){
        if (getDiet().equals("육식")) {
            System.out.println(getName() + "은/는 숲속에서 사냥을 해서 먹이를 구합니다.");
        } else if (getDiet().equals("초식")) {
            System.out.println(getName() + "은/는 숲속에서 풀이나 열매를 찾아 먹이를 구합니다.");
        } else if (getDiet().equals("잡식")) {
            System.out.println(getName() + "은/는 숲속에서 사냥도 하고 식물도 찾아 먹이를 구합니다.");
        } else {
            System.out.println(getName() + "은/는 숲속에서 스스로 먹이를 구합니다.");
        }
    }

}
