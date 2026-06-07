package manager;

import entity.Steve;
import skill.active.ActiveSkill;
import skill.active.FireCharge;
import skill.active.SnowBall;
import skill.consumable.AttackPotion;
import skill.consumable.ConsumableSkill;
import skill.consumable.HealPotion;
import weapon.DiamondSword;
import weapon.IronSword;
import weapon.NetheriteSword;
import weapon.StoneSword;
import weapon.Sword;
import weapon.Weapon;

import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

public class ShopManager {

    private static final List<ShopEntry> WEAPONS = List.of(
            new WeaponEntry("StoneSword", "돌 검", "돌로 만들어진 검입니다.\n가장 기본적인 무기입니다.", Rarity.COMMON, 10, 0, 40,
                    StoneSword::new),
            new WeaponEntry("IronSword", "철 검", "철로 만들어진 검입니다.\n적당한 강도를 자랑합니다.", Rarity.UNCOMMON, 15, 0, 75,
                    IronSword::new),
            new WeaponEntry("DiamondSword", "다이아몬드 검", "다이아몬드로 만들어진 검입니다.\n균형 잡힌 성능을 자랑합니다.", Rarity.RARE, 20,
                    0, 115, DiamondSword::new),
            new WeaponEntry("NetheriteSword", "네더라이트 검", "지옥의 금속으로 만든 검입니다.\n최강의 무기입니다.", Rarity.EPIC, 25, 0,
                    160, NetheriteSword::new));

    private static final List<ShopEntry> SKILLS = List.of(
            new SkillEntry("SnowBall", "눈덩이", "적을 스턴 상태로 만듭니다.\n쿨타임 3턴", Rarity.COMMON, 10, 0, 45,
                    SnowBall::new),
            new SkillEntry("FireCharge", "화염구", "적에게 화상을 입힙니다.\n화상 2턴, 쿨타임 3턴", Rarity.UNCOMMON, 15, 0, 65,
                    FireCharge::new));

    private static final List<ShopEntry> POTIONS = List.of(
            new PotionEntry("AttackPotion", "공격 포션", "다음 공격의 데미지를\n2배로 만듭니다.", Rarity.COMMON, 0, 0, 18,
                    AttackPotion::new),
            new PotionEntry("HealPotion", "회복 포션", "체력을 일정량\n회복합니다.", Rarity.COMMON, 0, 0, 15,
                    HealPotion::new));

    private Steve steve;
    private final Scanner scanner = new Scanner(System.in);

    public ShopManager(Steve steve) {
        this.steve = steve;
    }

    public enum Category {
        WEAPON("무기"),
        SKILL("스킬"),
        POTION("포션");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum Rarity {
        COMMON("일반"),
        UNCOMMON("고급"),
        RARE("희귀"),
        EPIC("영웅");

        private final String label;

        Rarity(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum PurchaseStatus {
        AVAILABLE("구매하기", ""),
        OWNED("이미 보유 중", "이미 보유 중이거나 상위 티어 장비를 가졌습니다."),
        LOCKED("이전 무기 필요", "이전 단계 무기를 먼저 구매해야 합니다."),
        SLOT_FULL("슬롯 부족", "가방 슬롯이 가득 차서 공간이 없습니다.");

        private final String label;
        private final String message;

        PurchaseStatus(String label, String message) {
            this.label = label;
            this.message = message;
        }

        public String getLabel() {
            return label;
        }

        public String getMessage() {
            return message;
        }
    }

    public abstract static class ShopEntry {
        private final Category category;
        private final String id;
        private final String name;
        private final String description;
        private final Rarity rarity;
        private final int attack;
        private final int defense;
        private final int price;

        protected ShopEntry(Category category, String id, String name, String description, Rarity rarity, int attack,
                int defense, int price) {
            this.category = category;
            this.id = id;
            this.name = name;
            this.description = description;
            this.rarity = rarity;
            this.attack = attack;
            this.defense = defense;
            this.price = price;
        }

        public Category getCategory() {
            return category;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Rarity getRarity() {
            return rarity;
        }

        public int getAttack() {
            return attack;
        }

        public int getDefense() {
            return defense;
        }

        public int getPrice() {
            return price;
        }

        public abstract PurchaseStatus getStatus(ShopManager shop);

        protected abstract boolean buy(ShopManager shop);
    }

    private static class WeaponEntry extends ShopEntry {
        private final Supplier<? extends Sword> factory;

        WeaponEntry(String id, String name, String description, Rarity rarity, int attack, int defense, int price,
                Supplier<? extends Sword> factory) {
            super(Category.WEAPON, id, name, description, rarity, attack, defense, price);
            this.factory = factory;
        }

        @Override
        public PurchaseStatus getStatus(ShopManager shop) {
            Weapon current = shop.steve.getWeapon();
            Sword target = factory.get();
            int currentTier = current == null ? -1 : current.getTier();

            if (currentTier >= target.getTier())
                return PurchaseStatus.OWNED;
            if (target.getTier() == currentTier + 1)
                return PurchaseStatus.AVAILABLE;
            return PurchaseStatus.LOCKED;
        }

        @Override
        protected boolean buy(ShopManager shop) {
            Sword sword = factory.get();
            shop.steve.setCoin(shop.steve.getCoin() - getPrice());
            shop.steve.setWeapon(sword);
            System.out.println(sword.getName() + " 구매 완료! 남은 코인: " + shop.steve.getCoin());
            return true;
        }
    }

    private static class SkillEntry extends ShopEntry {
        private final Supplier<? extends ActiveSkill> factory;

        SkillEntry(String id, String name, String description, Rarity rarity, int attack, int defense, int price,
                Supplier<? extends ActiveSkill> factory) {
            super(Category.SKILL, id, name, description, rarity, attack, defense, price);
            this.factory = factory;
        }

        @Override
        public PurchaseStatus getStatus(ShopManager shop) {
            if (shop.hasActiveSkill(getId()))
                return PurchaseStatus.OWNED;
            if (!shop.hasEmptySkillSlot())
                return PurchaseStatus.SLOT_FULL;
            return PurchaseStatus.AVAILABLE;
        }

        @Override
        protected boolean buy(ShopManager shop) {
            ActiveSkill[] skills = shop.steve.getActiveSkills();
            ActiveSkill skill = factory.get();

            for (int i = 0; i < skills.length; i++) {
                if (skills[i] == null) {
                    skills[i] = skill;
                    shop.steve.setCoin(shop.steve.getCoin() - getPrice());
                    System.out.println(skill.getName() + " 해금 완료! 남은 코인: " + shop.steve.getCoin());
                    return true;
                }
            }
            return false;
        }
    }

    private static class PotionEntry extends ShopEntry {
        private final Supplier<? extends ConsumableSkill> factory;

        PotionEntry(String id, String name, String description, Rarity rarity, int attack, int defense, int price,
                Supplier<? extends ConsumableSkill> factory) {
            super(Category.POTION, id, name, description, rarity, attack, defense, price);
            this.factory = factory;
        }

        @Override
        public PurchaseStatus getStatus(ShopManager shop) {
            if (shop.hasPotion(getId()))
                return PurchaseStatus.AVAILABLE;
            if (!shop.hasEmptyPotionSlot())
                return PurchaseStatus.SLOT_FULL;
            return PurchaseStatus.AVAILABLE;
        }

        @Override
        protected boolean buy(ShopManager shop) {
            ConsumableSkill[] consumables = shop.steve.getConsumables();
            ConsumableSkill potion = factory.get();

            for (ConsumableSkill item : consumables) {
                if (item != null && item.getId().equals(potion.getId())) {
                    item.addQuantity(1);
                    shop.steve.setCoin(shop.steve.getCoin() - getPrice());
                    System.out.println(item.getName() + " 구매 완료! 남은 코인: " + shop.steve.getCoin());
                    return true;
                }
            }

            for (int i = 0; i < consumables.length; i++) {
                if (consumables[i] == null) {
                    potion.addQuantity(1);
                    consumables[i] = potion;
                    shop.steve.setCoin(shop.steve.getCoin() - getPrice());
                    System.out.println(potion.getName() + " 구매 완료! 남은 코인: " + shop.steve.getCoin());
                    return true;
                }
            }
            return false;
        }
    }

    public List<ShopEntry> getItems(Category category) {
        return switch (category) {
            case WEAPON -> WEAPONS;
            case SKILL -> SKILLS;
            case POTION -> POTIONS;
        };
    }

    public ShopEntry findItem(String id) {
        for (Category category : Category.values()) {
            for (ShopEntry item : getItems(category)) {
                if (item.getId().equals(id))
                    return item;
            }
        }
        return null;
    }

    public PurchaseStatus getStatus(ShopEntry item) {
        return item == null ? PurchaseStatus.LOCKED : item.getStatus(this);
    }

    public boolean buy(ShopEntry item) {
        if (item == null)
            return false;

        PurchaseStatus status = item.getStatus(this);
        if (status != PurchaseStatus.AVAILABLE) {
            System.out.println(status.getMessage());
            return false;
        }

        if (!canAfford(item.getPrice())) {
            System.out.println("코인이 부족합니다. (필요: " + item.getPrice() + ", 보유: " + steve.getCoin() + ")");
            return false;
        }

        return item.buy(this);
    }

    public boolean buyWeapon(Sword sword) {
        return sword != null && buy(findItem(sword.getId()));
    }

    public boolean buySkill(ActiveSkill skill) {
        return skill != null && buy(findItem(skill.getId()));
    }

    public boolean buyPotion(ConsumableSkill potion) {
        return potion != null && buy(findItem(potion.getId()));
    }

    public boolean canAfford(int price) {
        return steve.getCoin() >= price;
    }

    public boolean isWeaponUnlocked(Sword sword) {
        if (sword == null)
            return false;
        Weapon current = steve.getWeapon();
        int currentTier = current == null ? -1 : current.getTier();
        return sword.getTier() == currentTier + 1;
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
        System.out.println("\n보유 코인: " + steve.getCoin());
        System.out.println("--- 상점 ---");
        System.out.println("[1] 무기 구매");
        System.out.println("[2] 스킬 해금");
        System.out.println("[3] 포션 구매");
        System.out.println("[0] 나가기");

        int input = scanner.nextInt();
        switch (input) {
            case 1 -> showCategoryMenu(Category.WEAPON);
            case 2 -> showCategoryMenu(Category.SKILL);
            case 3 -> showCategoryMenu(Category.POTION);
            case 0 -> System.out.println("상점을 나갑니다.");
            default -> {
                System.out.println("잘못된 입력입니다.");
                showMenu();
            }
        }
    }

    private void showCategoryMenu(Category category) {
        List<ShopEntry> items = getItems(category);

        System.out.println("\n--- " + category.getLabel() + " ---");
        for (int i = 0; i < items.size(); i++) {
            ShopEntry item = items.get(i);
            System.out.println("[" + (i + 1) + "] " + item.getName() + " - " + item.getPrice() + " 코인");
        }
        System.out.println("[0] 뒤로");

        int input = scanner.nextInt();
        if (input == 0) {
            showMenu();
            return;
        }
        if (input >= 1 && input <= items.size()) {
            buy(items.get(input - 1));
        }
        showCategoryMenu(category);
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

    private boolean hasActiveSkill(String id) {
        for (ActiveSkill skill : steve.getActiveSkills()) {
            if (skill != null && skill.getId().equals(id))
                return true;
        }
        return false;
    }

    private boolean hasEmptySkillSlot() {
        for (ActiveSkill skill : steve.getActiveSkills()) {
            if (skill == null)
                return true;
        }
        return false;
    }

    private boolean hasPotion(String id) {
        for (ConsumableSkill item : steve.getConsumables()) {
            if (item != null && item.getId().equals(id))
                return true;
        }
        return false;
    }

    private boolean hasEmptyPotionSlot() {
        for (ConsumableSkill item : steve.getConsumables()) {
            if (item == null)
                return true;
        }
        return false;
    }
}
