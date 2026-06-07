# ⚔️ Minecraft RPG

> 마인크래프트 세계관의 턴제 텍스트 RPG — 웨이브를 돌파하고 엔더드래곤을 처치하라.

A turn-based text RPG set in the Minecraft universe.  
Survive waves 1–6 and defeat the Ender Dragon.

---

## Features

- 웨이브 1~6 + 최종 보스 **엔더드래곤** (3페이즈)
- 검 5종 업그레이드 시스템 (Wood → Netherite)
- 스킬 시스템 — 광역베기 / 눈덩이 / 화염구 / 포션
- EXP 레벨업 + 코인 상점 (사망 후에도 코인 유지)
- 로그라이크 구조 — 죽어도 코인으로 장비 강화 후 재도전

---

## Package Structure

```
src/main/java/com/minecraftrpg/
│
├── entity/                  # 플레이어 & 몹
│   ├── Entity.java
│   ├── Steve.java
│   └── mob/
│       ├── Mob.java
│       ├── Zombie.java
│       ├── Skeleton.java
│       ├── Creeper.java
│       ├── Witch.java
│       ├── WitherSkeleton.java
│       ├── Piglin.java
│       └── EnderDragon.java
│
├── weapon/                  # 무기
│   ├── Weapon.java
│   ├── Sword.java
│   ├── WoodSword.java
│   ├── StoneSword.java
│   ├── IronSword.java
│   ├── DiamondSword.java
│   └── NetheriteSword.java
│
├── skill/                   # 스킬
│   ├── Skill.java
│   ├── weapon/
│   │   ├── WeaponSkill.java
│   │   └── AoeSlash.java
│   ├── active/
│   │   ├── ActiveSkill.java
│   │   ├── SnowBall.java
│   │   └── FireCharge.java
│   └── consumable/
│       ├── ConsumableSkill.java
│       ├── AttackPotion.java
│       └── HealPotion.java
│
├── ability/                 # 몹 고유 능력
│   ├── MobAbility.java
│   ├── Shoot.java
│   ├── Explode.java
│   ├── Heal.java
│   ├── WitherDmg.java
│   ├── DoubleAttack.java
│   └── ThrowPotion.java
│
├── effect/                  # 상태이상
│   ├── StatusEffect.java
│   ├── Burn.java
│   ├── Stun.java
│   └── Wither.java
│
├── interfaces/              # 공통 인터페이스
│   ├── Blockable.java
│   ├── Attackable.java
│   └── Skillable.java
│
├── shop/
│   └── Shop.java
│
└── game/
    └── Main.java
```

---

## Team

| 역할 | 담당 |
|------|------|
| 김새미 | Entity / Mob / Steve |
| 임민경 | 전투 흐름 / Shop / Game |
| 김미리 | Weapon / Skill / Main 테스트 |
