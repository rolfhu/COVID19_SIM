package sim.policy.quarantine;

import sim.policy.PolicyBase;

public class PolicyLockCity extends PolicyBase {
    public PolicyLockCity() {
        super("封城");
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
