package sim.strategy.hospital;

import sim.strategy.StrategyBase;
import sim.substance.Hospital;
import sim.substance.Patient;
import sim.tags.stage.IntensiveStage;

public class IntoHospitalAll extends StrategyBase implements IAcceptIntoHospital {

    public IntoHospitalAll() {
        super("应收尽收");
    }

    @Override
    public boolean checkPatient(Hospital hospital, Patient patient)
    {
        return true;
    }
}
