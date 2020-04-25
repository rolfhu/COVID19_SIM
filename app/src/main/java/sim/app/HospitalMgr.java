package sim.app;

import java.util.Collection;
import java.util.HashSet;

import sim.substance.AreaHospital;
import sim.substance.Hospital;
import sim.substance.Patient;
import sim.worlds.FactoryMgr;

public class HospitalMgr {

    //所有医院列表
    private Collection<AreaHospital> m_AreaHospitalList = new HashSet<>();
    private Collection<Hospital> m_HospitalList = new HashSet<>();

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
}
