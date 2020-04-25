package sim.policy.hospital;

import sim.app.HospitalMgr;
import sim.policy.PolicyBase;
import sim.strategy.hospital.IntoHospitalAll;
import sim.strategy.hospital.IntoHospitalNormal;

public class PolicyHospitalAcceptAll extends PolicyBase {
    public PolicyHospitalAcceptAll() {
        super("应收尽收");
    }

    @Override
    public void onUsable() {
        setPolicyState(PolicyState.PS_Activate);
    }

    @Override
    public void onActivated() {
        HospitalMgr.getInstance().changeStrategy(new IntoHospitalAll());
    }

    @Override
    public void onUnActivated() {
        HospitalMgr.getInstance().changeStrategy(new IntoHospitalNormal());
    }
}
