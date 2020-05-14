package sim.worlds.china;

import java.util.Collection;

import sim.policy.PolicyBase;
import sim.policy.hospital.PolicyHospitalAcceptAll;
import sim.policy.quarantine.PolicyLockCity;
import sim.policy.quarantine.PolicyVillageQuarantine;

public class PolicyInit {

    public static void initPolicys(Collection<PolicyBase> policys)
    {
        PolicyBase policy1 = new PolicyHospitalAcceptAll();
        policy1.m_strUsableDate = "2020-2-10";
        policys.add(policy1);

        PolicyBase policy2 = new PolicyLockCity();
        policy2.m_strUsableDate = "2020-1-15";
        policys.add(policy2);

        PolicyBase policy3 = new PolicyVillageQuarantine();
        policy3.m_strUsableDate = "2020-1-30";
        policys.add(policy3);


    }
}
