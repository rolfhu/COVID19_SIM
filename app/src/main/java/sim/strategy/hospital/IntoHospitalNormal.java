package sim.strategy.hospital;

import sim.strategy.StrategyBase;
import sim.substance.Hospital;
import sim.substance.Patient;
import sim.tags.stage.IntensiveStage;

//普通策略，收治重症，拒绝轻症
public class IntoHospitalNormal extends StrategyBase implements IAcceptIntoHospital {

    public IntoHospitalNormal() {
        super("收重拒轻");
    }

    @Override
    public boolean checkPatient(Hospital hospital, Patient patient)
    {
        if(patient.getStageTag().getClass() == IntensiveStage.class)
        {
            return true;
        }
        return false;
    }
}
