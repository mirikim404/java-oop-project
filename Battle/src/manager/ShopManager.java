package manager;

import entity.Steve;
import skill.active.*;
import skill.consumable.*;
import weapon.*;
import java.util.*;


public class ShopManager {

    private Steve steve;
    private Map<String, Integer> weaponPrices;
    private Map<String, Integer> skillPrices;
    private Map<String, Integer> potionPrices;

    private final Scanner scanner = new Scanner(System.in);

    public ShopManager(Steve steve) {
        this.steve = steve;
        initPrices();
    }

    private void initPrices() {
        weaponPrices = new HashMap<>();
        weaponPrices.put("StoneSword",     30);
        weaponPrices.put("IronSword",      60);
        weaponPrices.put("DiamondSword",  100);
        weaponPrices.put("NetheriteSword",150);

        skillPrices = new HashMap<>();
        skillPrices.put("SnowBall",   40);
        skillPrices.put("FireCharge", 60);

        potionPrices = new HashMap<>();
        potionPrices.put("AttackPotion", 20);
        potionPrices.put("HealPotion",   15);
    }

    public void enterShop(GameState state) {
        if (state == GameState.DEAD) {
            System.out.println("\n=== 사망했습니다. 코인은 유지됩니다 ===");
        } else if (state == GameState.WAVE_CLEAR) {
            System.out.println("\n=== 웨이브 클리어! 상점에 입장합니다 ===");
        }
        showMenu();
    }

    public void showMenu() {
        System.out.println("\n보유 코인: " + steve.getCoin()); // Steve.coin → steve.getCoin()
        System.out.println("--- 상점 ---");
        System.out.println("[1] 무기 구매");
        System.out.println("[2] 스킬 해금");
        System.out.println("[3] 포션 구매");
        System.out.println("[0] 나가기");

        int input = scanner.nextInt();
        switch (input) {
            case 1 -> showWeaponMenu();
            case 2 -> showSkillMenu();
            case 3 -> showPotionMenu();
            case 0 -> System.out.println("상점을 나갑니다.");
            default -> { System.out.println("잘못된 입력입니다."); showMenu(); }
        }
    }

    private void showWeaponMenu() {
        System.out.println("\n--- 무기 ---");
        System.out.println("[1] 돌 검        - " + weaponPrices.get("StoneSword")     + " 코인");
        System.out.println("[2] 철 검        - " + weaponPrices.get("IronSword")      + " 코인");
        System.out.println("[3] 다이아몬드 검 - " + weaponPrices.get("DiamondSword")   + " 코인");
        System.out.println("[4] 네더라이트 검 - " + weaponPrices.get("NetheriteSword") + " 코인");
        System.out.println("[0] 뒤로");

        int input = scanner.nextInt();
        switch (input) {
            case 1 -> {buyWeapon(new StoneSword()); showWeaponMenu();}
            case 2 -> {buyWeapon(new IronSword()); showWeaponMenu();}
            case 3 -> {buyWeapon(new DiamondSword()); showWeaponMenu();}
            case 4 -> {buyWeapon(new NetheriteSword()); showWeaponMenu();}
            case 0 -> showMenu();
        }
    }

    private void showSkillMenu() {
        System.out.println("\n--- 스킬 ---");
        System.out.println("[1] 눈덩이 (단일 스턴) - " + skillPrices.get("SnowBall")   + " 코인");
        System.out.println("[2] 화염구 (화상 2턴)  - " + skillPrices.get("FireCharge") + " 코인");
        System.out.println("[0] 뒤로");

        int input = scanner.nextInt();
        switch (input) {
            case 1 -> {buySkill(new SnowBall()); showSkillMenu();}
            case 2 -> {buySkill(new FireCharge()); showSkillMenu();}
            case 0 -> showMenu();
        }
    }

    private void showPotionMenu() {
        System.out.println("\n--- 포션 ---");
        System.out.println("[1] 공격 포션 (1턴 공격력 2배) - " + potionPrices.get("AttackPotion") + " 코인");
        System.out.println("[2] 회복 포션 (체력 회복)      - " + potionPrices.get("HealPotion")   + " 코인");
        System.out.println("[0] 뒤로");

        int input = scanner.nextInt();
        switch (input) {
            case 1 -> {buyPotion(new AttackPotion()); showPotionMenu();}
            case 2 -> {buyPotion(new HealPotion()); showPotionMenu();}
            case 0 -> showMenu();
        }
    }

    public boolean buyWeapon(Sword sword) {
        String swordName = sword.getClass().getSimpleName();

        if (!isWeaponUnlocked(sword)) {
            System.out.println("이전 무기를 먼저 구매해야 합니다.");
            return false;
        }
        int price = weaponPrices.getOrDefault(swordName, 0);
        if (!canAfford(price)) {
            System.out.println("코인이 부족합니다. (필요: " + price + ", 보유: " + steve.getCoin() + ")");
            return false;
        }
        steve.setCoin(steve.getCoin() - price); // Steve.coin -= price → setCoin
        steve.setWeapon(sword);
        System.out.println(swordName + " 구매 완료! 남은 코인: " + steve.getCoin());
        return true;
    }

    public boolean buySkill(ActiveSkill skill) {
        String skillName = skill.getClass().getSimpleName();
        int price = skillPrices.getOrDefault(skillName, 0);

        if (!canAfford(price)) {
            System.out.println("코인이 부족합니다. (필요: " + price + ", 보유: " + steve.getCoin() + ")");
            return false;
        }
        steve.setCoin(steve.getCoin() - price);

        // 배열에서 빈 슬롯 찾아서 추가
        ActiveSkill[] skills = steve.getActiveSkills();
        for (int i = 0; i < skills.length; i++) {
            if (skills[i] == null) {
                skills[i] = skill;
                break;
            }
        }
        System.out.println(skillName + " 해금 완료! 남은 코인: " + steve.getCoin());
        return true;
    }

    public boolean buyPotion(ConsumableSkill potion) {
        String potionName = potion.getClass().getSimpleName();
        int price = potionPrices.getOrDefault(potionName, 0);

        if (!canAfford(price)) {
            System.out.println("코인이 부족합니다. (필요: " + price + ", 보유: " + steve.getCoin() + ")");
            return false;
        }
        steve.setCoin(steve.getCoin() - price);

        // 배열에서 같은 타입 포션 있으면 수량만 증가, 없으면 빈 슬롯에 추가
        ConsumableSkill[] consumables = steve.getConsumables();
        for (ConsumableSkill c : consumables) {
            if (c != null && c.getClass().equals(potion.getClass())) {
                c.addQuantity(1);
                System.out.println(potionName + " 구매 완료! 남은 코인: " + steve.getCoin());
                return true;
            }
        }
        for (int i = 0; i < consumables.length; i++) {
            if (consumables[i] == null) {
                consumables[i] = potion;
                break;
            }
        }
        System.out.println(potionName + " 구매 완료! 남은 코인: " + steve.getCoin());
        return true;
    }

    public boolean canAfford(int price) {
        return steve.getCoin() >= price; // Steve.coin → steve.getCoin()
    }

    public boolean isWeaponUnlocked(Sword sword) {
        String current = steve.getWeapon().getClass().getSimpleName();
        String target  = sword.getClass().getSimpleName();

        return switch (target) {
            case "StoneSword"     -> current.equals("WoodSword");
            case "IronSword"      -> current.equals("StoneSword");
            case "DiamondSword"   -> current.equals("IronSword");
            case "NetheriteSword" -> current.equals("DiamondSword");
            default -> false;
        };
    }

    public void showRestartMenu() {
        System.out.println("\n[1] 재시작  [0] 종료");
        int input = scanner.nextInt();
        if (input == 1) {
            System.out.println("재시작합니다...");
        } else {
            System.out.println("게임을 종료합니다.");
            System.exit(0);
        }
    }

    public void setSteve(Steve steve) {
        this.steve = steve;
    }
}