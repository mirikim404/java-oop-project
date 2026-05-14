
import entity.Steve;
import manager.BattleManager;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== 마인크래프트 RPG ===");
        System.out.print("닉네임을 입력하세요: ");
        String username = scanner.nextLine();

        Steve steve = new Steve(username);
        BattleManager battleManager = new BattleManager(steve);
        battleManager.startGame();

        scanner.close();
    }
}