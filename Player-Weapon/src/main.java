import java.util.Random;

public class main {

    public static void main(String[] args) {
        
        // 팀 A
        Player[] teamA = {
            new 토르("토르토르", 100, 10, new 묠니르("망치", 40)),
            new 헐크("헐크헐크", 100, 20, new 바위("돌덩이", 30)),
            new 힐러("간호사A", 100, 5, new 물약("치료제", 25))
        };

        // 팀 B
        Player[] teamB = {
            new 토르("멋쟁이토르", 100, 10, new 묠니르("망치", 40)),
            new 헐크("초록헐크", 100, 20, new 바위("돌덩이", 20)),
            new 블랙위도우("블랙위도우", 100, 5, new 총("권총", 20))
        };

        int countA = teamA.length;
        int countB = teamB.length;

        Random r = new Random();

        System.out.println("=== 팀A ===");
        Player.showStatus(teamA, countA);
        System.out.println("=== 팀B ===");
        Player.showStatus(teamB, countB);

        while (true) {
            // 팀A에서 공격자 선택 → 팀B 타겟 공격
            int i = r.nextInt(countA);
            int j = r.nextInt(countB);

            Player attacker = teamA[i];
            Player target = teamB[j];

            attacker.attack(target);

            if (target.getHp() <= 0) {
                teamB[j] = teamB[countB - 1];
                countB--;
            }

            // 팀B에서 공격자 선택 → 팀A 타겟 공격
            if (countB > 0) {
                i = r.nextInt(countB);
                j = r.nextInt(countA);

                attacker = teamB[i];
                target = teamA[j];

                attacker.attack(target);

                if (target.getHp() <= 0) {
                    teamA[j] = teamA[countA - 1];
                    countA--;
                }
            }

            System.out.println("=== 팀A ===");
            Player.showStatus(teamA, countA);
            System.out.println("=== 팀B ===");
            Player.showStatus(teamB, countB);

            if (countA <= 0) {
                System.out.println("팀B 승리!");
                break;
            }
            if (countB <= 0) {
                System.out.println("팀A 승리!");
                break;
            }
        }
    }
}