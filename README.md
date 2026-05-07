# Java OOP Project
상속, 다형성, 클래스 설계를 학습하는 Java OOP 실습 레포입니다.

## 구성
| 폴더 | 설명 |
|------|------|
| `HunterAnimal/` | 교수님 예제 실습 (static, 상속, 다형성) |
| `Battle/` | 팀 프로젝트 - 플레이어 배틀 구현 |
| `docs/` | UML 및 설계 문서 |

## 프로젝트 구조
```
java-oop-project/
├── HunterAnimal/          # HunterAnimal 프로젝트
│   └── src/
│       ├── animal/
│       ├── common/
│       └── human/
├── Battle/                # Battle 프로젝트
│   └── src/
│       ├── myInterface/   (Attackable, Shootable, Hitable, Throoowable)
│       ├── player/        (Player.java + 하위클래스)
│       ├── weapon/        (Weapon.java + 하위클래스)
│       └── view/          (GameWindow 등)
├── docs/
└── README.md
```

## 개발 환경
- Java 8 (JRE 1.8)
- Eclipse IDE
- Git / GitHub

## 브랜치 전략
```
main          ← 최종 완성본만 (직접 push 금지)
dev           ← 통합 브랜치 (여기서 merge)
feature/player   ← player 패키지 담당
feature/weapon   ← weapon 패키지 담당
feature/view     ← view 패키지 담당
```
