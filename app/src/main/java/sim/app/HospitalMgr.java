package sim.app;

import java.util.Collection;
import java.util.HashSet;

import sim.strategy.StrategyBase;
import sim.strategy.hospital.IAcceptIntoHospital;
import sim.strategy.hospital.IntoHospitalNormal;
import sim.substance.AreaHospital;
import sim.substance.Hospital;
import sim.worlds.FactoryMgr;

public class HospitalMgr {

    //所有医院列表
    private Collection<AreaHospital> m_AreaHospitalList = new HashSet<>();
    private Collection<Hospital> m_HospitalList = new HashSet<>();

    //所有与医院有关的策略列表
    private Collection<StrategyBase> m_StrategyList = new HashSet<>();

    public IAcceptIntoHospital m_IntoHospitalStrategy = null;

    private HospitalMgr() {}
    private static HospitalMgr s_single=null;
    public static HospitalMgr getInstance() {
        if (s_single == null) {
            s_single = new HospitalMgr();
        }
        return s_single;
    }

    public void init()
    {
        FactoryMgr.getInstance().getFactory().initHospitals(m_HospitalList);

        changeStrategy(new IntoHospitalNormal());
    }

    public void logOut()
    {
        for (Hospital oneHospital : m_HospitalList)
        {
            if (oneHospital.isHospitalEmpty())
            {
                continue;
            }

            oneHospital.logOut();
        }
    }

    //替换具有唯一性的策略
    public void changeStrategy(StrategyBase newStrategy)
    {
        if (newStrategy instanceof IAcceptIntoHospital)
        {
            if (m_IntoHospitalStrategy != null)
            {
                m_StrategyList.remove(m_IntoHospitalStrategy);
            }

            m_IntoHospitalStrategy = (IAcceptIntoHospital) newStrategy;
            m_StrategyList.add(newStrategy);
        }

    }
}
