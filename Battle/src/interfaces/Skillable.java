package interfaces;

import entity.mob.Mob;
import java.util.List;

public interface Skillable {

    void useSkill(Mob target, List<Mob> aliveMobs);

}
