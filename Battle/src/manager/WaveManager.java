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
    }

    // 웨이브별 몹 구성 초기화
    private void initWaveTable() {
        Map<Integer, List<Mob>> table = new HashMap<>();

        List<Mob> wave1 = new ArrayList<>();
        // 테스트용 임시 교체
        wave1.add(new ZombieTest());
        table.put(1, wave1);

        List<Mob> wave2 = new ArrayList<>();
        wave2.add(new Skeleton());
        table.put(2, wave2);

        List<Mob> wave3 = new ArrayList<>();
        wave3.add(new Witch());
        wave3.add(new Creeper());
        table.put(3, wave3);

        List<Mob> wave4 = new ArrayList<>();
        wave4.add(new WitherSkeleton());
        table.put(4, wave4);

        List<Mob> wave5 = new ArrayList<>();
        wave5.add(new Piglin());
        table.put(5, wave5);

        List<Mob> wave6 = new ArrayList<>();
        wave6.add(new EnderDragon());
        table.put(6, wave6);

        this.waveTable = table;
    }

    // 해당 웨이브의 몹 목록 반환
    public List<Mob> getMobsForWave(int wave) {
        return new ArrayList<>(waveTable.getOrDefault(wave, new ArrayList<>()));
    }

    // 처치된 몹을 aliveMobs에서 제거
    public void removeMob(Mob mob) {
        aliveMobs.remove(mob);
    }

    // aliveMobs가 비어있으면 true
    public boolean isWaveCleared() {
        return aliveMobs.isEmpty();
    }

    // currentWave++ 후 다음 몹 목록 세팅
    public void nextWave() {
        currentWave++;
        aliveMobs = getMobsForWave(currentWave);
    }

    // 현재 웨이브가 마지막(엔더드래곤)인지 확인
    public boolean isLastWave() {
        return currentWave == 6;
    }

    // 다음 웨이브 등장 몹 예고 출력
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

    // 현재 웨이브에서 살아있는 몹 목록 세팅 (startWave 시 호출)
    public void loadCurrentWave() {
        aliveMobs = getMobsForWave(currentWave);
    }

    // getter
    public int getCurrentWave() { return currentWave; }
    public List<Mob> getAliveMobs() { return aliveMobs; }
}