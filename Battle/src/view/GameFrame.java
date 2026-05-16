package view;

import entity.Steve;
import entity.mob.Mob;
import manager.WaveManager;

import javax.swing.*;

public class GameFrame extends JFrame {

    private Steve steve;
    private WaveManager waveManager;

    public GameFrame() {
        setTitle("Minecraft RPG");
        setSize(854, 560);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        showStart();

        setVisible(true);
    }

    private void changeScreen(JPanel panel) {
        setContentPane(panel);
        revalidate();
        repaint();
        panel.requestFocusInWindow();
    }

    public void showStart() {
        changeScreen(new StartView(this));
    }

    public void startNewGame(String name) {
        steve = new Steve(name);
        waveManager = new WaveManager();
        waveManager.loadCurrentWave();

        Mob firstMob = waveManager.getAliveMobs().get(0);
        showEncounter(steve, waveManager, firstMob, 1);
    }

    public void showEncounter(Steve steve, WaveManager waveManager, Mob mob, int wave) {
        this.steve = steve;
        this.waveManager = waveManager;
        changeScreen(new EncounterView(this, steve, waveManager, mob, wave));
    }

    public void showBattle(Steve steve, WaveManager waveManager, Mob mob, int wave) {
        this.steve = steve;
        this.waveManager = waveManager;
        changeScreen(new BattleView(this, steve, waveManager, mob, wave));
    }

    public void showVictory(Steve steve, WaveManager waveManager, int wave) {
        this.steve = steve;
        this.waveManager = waveManager;
        changeScreen(new VictoryView(this, steve, waveManager, wave));
    }

    public void showShop(Steve steve, WaveManager waveManager, int wave) {
        this.steve = steve;
        this.waveManager = waveManager;
        changeScreen(new ShopView(this, steve, waveManager, wave));
    }

    public void showDead(Steve steve, WaveManager waveManager) {
        this.steve = steve;
        this.waveManager = waveManager;
        changeScreen(new DeadView(this, steve, waveManager));
    }

    public void restartAfterDeath(Steve oldSteve) {
        steve = oldSteve.resetAfterDeath();
        waveManager = new WaveManager();
        waveManager.loadCurrentWave();

        Mob firstMob = waveManager.getAliveMobs().get(0);
        showEncounter(steve, waveManager, firstMob, 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameFrame::new);
    }
}