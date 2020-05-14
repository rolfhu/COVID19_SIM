package sim.app;

import java.util.Collection;
import java.util.HashSet;

import sim.policy.PolicyBase;
import sim.worlds.FactoryMgr;

public class PolicyMgr {

    //所有政策的集合
    private Collection<PolicyBase> m_PolicySet = new HashSet<>();

    private PolicyMgr() {}
    private static PolicyMgr s_single=null;
    public static PolicyMgr getInstance() {
        if (s_single == null) {
            s_single = new PolicyMgr();
        }
        return s_single;
    }

    public void init()
    {
        FactoryMgr.getInstance().getFactory().initPolicy(m_PolicySet);
    }

    public void onDayStart()
    {
        //检测每个政策是否变为可用了
        for (PolicyBase onePolicy : m_PolicySet)
        {
            onePolicy.checkUsable();
        }
    }

    public boolean isPolicyActive(Class policyClass)
    {
        PolicyBase policy = getOnePolicy(policyClass);

        if (policy == null)
        {
            return false;
        }

        return policy.isActive();
    }

    public PolicyBase getOnePolicy(Class policyClass)
    {
        for (PolicyBase onePolicy : m_PolicySet)
        {
            if (onePolicy.getClass() == policyClass)
            {
                return onePolicy;
            }
        }
        return null;
    }
}
