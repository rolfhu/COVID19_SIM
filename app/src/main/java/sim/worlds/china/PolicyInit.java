package sim.worlds.china;

import java.util.Collection;

import sim.policy.PolicyBase;
import sim.policy.hospital.PolicyHospitalAcceptAll;

public class PolicyInit {

    public static void initPolicys(Collection<PolicyBase> policys)
    {
        PolicyHospitalAcceptAll policy1 = new PolicyHospitalAcceptAll();
        policy1.m_strUsableDate = "2020-2-10";
        policys.add(policy1);
    }
}
