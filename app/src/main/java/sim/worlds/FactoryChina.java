package sim.worlds;

import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import sim.area.Area;
import sim.policy.PolicyBase;
import sim.substance.Hospital;
import sim.substance.Population;
import sim.worlds.china.AreaInit;
import sim.worlds.china.PolicyInit;
import sim.worlds.china.PopulationInit;

public class FactoryChina extends FactoryBase {

    @Override
    public void initCountryProps() {
        m_fPopulationBedsRate = 4;
        m_fPopulationDoctorsRate = 10;
        m_fPopulationICUBedsRate = 0.35f;

        Calendar calendar = new GregorianCalendar(2019, 10, 27);
        m_StartDate = calendar;
    }

    @Override
    public void initPopulations(Collection<Population> populations) {
        PopulationInit.initPopulations(populations);
    }

    @Override
    public void initArea(Area rootArea) {
        AreaInit.initArea(rootArea);
    }

    @Override
    public void initHospitals(Collection<Hospital> hospitalList) {
        super.initHospitals(hospitalList);
    }

    @Override
    public void initPolicy(Collection<PolicyBase> policySet) {
        PolicyInit.initPolicys(policySet);
    }
}
