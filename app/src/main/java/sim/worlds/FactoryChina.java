package sim.worlds;

import java.util.Collection;

import sim.area.Area;
import sim.substance.Hospital;
import sim.substance.Population;
import sim.worlds.china.AreaInit;
import sim.worlds.china.PopulationInit;

public class FactoryChina extends FactoryBase {

    @Override
    public void initCountryProps() {
        m_fPopulationBedsRate = 4;
        m_fPopulationDoctorsRate = 10;
        m_fPopulationICUBedsRate = 0.35f;
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
}
