package test;

import entity.Steve;

public class SteveTest {
    public static void main(String[] args) {
        System.out.println("===== A파트 Steve 테스트 시작 =====");

        Steve steve = new Steve("Steve");
        Steve target = new Steve("Target");

        System.out.println("[생성 확인 - steve]");
        System.out.println("이름: " + steve.getName());
        System.out.println("체력: " + steve.getHealth() + "/" + steve.getMaxHealth());
        System.out.println("공격력: " + steve.getAttackPower());
        System.out.println("방어력: " + steve.getDefencePower());
        System.out.println("레벨: " + steve.getLevel());
        System.out.println("경험치: " + steve.getExp());
        System.out.println("코인: " + steve.getCoin());
        System.out.println("총 공격력: " + steve.getTotalAttackPower());

        System.out.println();

        System.out.println("[공격/방어 테스트]");
        steve.attack(target);
        steve.block();

        System.out.println();

        System.out.println("[데미지 테스트]");
        steve.takeDamage(30);
        System.out.println("생존 여부: " + steve.isAlive());

        System.out.println();

        System.out.println("[회복 테스트]");
        steve.heal(10);

        System.out.println();

        System.out.println("[경험치/레벨업 테스트]");
        steve.gainExp(120);

        System.out.println();

        System.out.println("[코인 테스트]");
        steve.gainCoin(50);

        System.out.println();

        System.out.println("[턴 종료 테스트]");
        steve.onTurnEnd();

        System.out.println();

        System.out.println("[사망 후 초기화 테스트]");
        steve = steve.resetAfterDeath();
        System.out.println("사망 후 레벨: " + steve.getLevel());
        System.out.println("사망 후 경험치: " + steve.getExp());
        System.out.println("사망 후 코인: " + steve.getCoin());
        System.out.println("사망 후 체력: " + steve.getHealth() + "/" + steve.getMaxHealth());

        System.out.println("===== A파트 Steve 테스트 종료 =====");
    }
}