package manager;

import entity.mob.*;
import java.util.*;


public class WaveManager {

    private int currentWave;
    private Map<Integer, List<Mob>> waveTable;
    private List<Mob> aliveMobs;

    public WaveManager() {
        this.currentWave = 1;
        this.waveTable = new HashMap<>();
        this.aliveMobs = new ArrayList<>();
        initWaveTable();
        loadCurrentWave();
    }

    private void initWaveTable() {
        Map<Integer, List<Mob>> table = new LinkedHashMap<>();

        List<Mob> wave1 = new ArrayList<>();
        wave1.add(new Zombie()); 
        table.put(1, wave1);

        List<Mob> wave2 = new ArrayList<>();
        wave2.add(new Skeleton());
        table.put(2, wave2);

        List<Mob> wave3 = new ArrayList<>();
        wave3.add(new Witch());
        wave3.add(new Witch());
        wave3.add(new Creeper());
        table.put(3, wave3);

        List<Mob> wave4 = new ArrayList<>();
        wave4.add(new WitherSkeleton());
        wave4.add(new WitherSkeleton());
        table.put(4, wave4);

        List<Mob> wave5 = new ArrayList<>();
        wave5.add(new Piglin());
        wave5.add(new Piglin()); 
        wave5.add(new Piglin()); 
        table.put(5, wave5);

        List<Mob> wave6 = new ArrayList<>();
        wave6.add(new EnderDragon());
        table.put(6, wave6);

        this.waveTable = table;
    }

    public List<Mob> getMobsForWave(int wave) {
    	return waveTable.getOrDefault(wave, new ArrayList<>());
    }

    public void removeMob(Mob mob) {
        aliveMobs.remove(mob);
    }

    public boolean isWaveCleared() {
        return aliveMobs.isEmpty();
    }

    public void nextWave() {
        currentWave++;
        loadCurrentWave();
    }

    public boolean isLastWave() {
        return currentWave == 6;
    }

    public void announceMobs() {
        if (isLastWave()) {
            System.out.println("=== 최종 보스 등장 ===");
            System.out.println("[ 엔더드래곤이 나타났다! ]");
        } else {
            int nextWave = currentWave + 1;
            List<Mob> nextMobs = getMobsForWave(nextWave);
            System.out.println("=== 웨이브 " + nextWave + " 예고 ===");
            for (Mob mob : nextMobs) {
                System.out.println("- " + mob.getName());
            }
        }
    }

    public void loadCurrentWave() {
    	aliveMobs.clear();
        aliveMobs.addAll(getMobsForWave(currentWave));
    }

    // getter
    public int getCurrentWave() { return currentWave; }
    public List<Mob> getAliveMobs() { return aliveMobs; }
}