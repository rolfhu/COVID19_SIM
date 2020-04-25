package sim.strategy.hospital;

import sim.substance.Hospital;
import sim.substance.Patient;

//决策是否允许收治入院
public interface IAcceptIntoHospital {
    public boolean checkPatient(Hospital hospital, Patient patient);
}
