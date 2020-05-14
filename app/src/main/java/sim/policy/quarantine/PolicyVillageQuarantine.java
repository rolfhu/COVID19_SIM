package sim.policy.quarantine;

import sim.policy.PolicyBase;

public class PolicyVillageQuarantine extends PolicyBase {
    public PolicyVillageQuarantine() {
        super("小区限制出行");
    }

    @Override
    public void onUsable() {
        setPolicyState(PolicyState.PS_Activate);
    }

    @Override
    public void onActivated() {

    }

    @Override
    public void onUnActivated() {

    }
}
