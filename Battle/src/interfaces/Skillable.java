package interfaces;

import entity.Steve;
import entity.mob.Mob;
import java.util.List;

public interface Skillable {

    void useSkill(Steve steve, Mob mob);
    
    void usePotion(Steve steve);
}
