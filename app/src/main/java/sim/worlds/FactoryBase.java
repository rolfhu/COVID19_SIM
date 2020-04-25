package sim.worlds;

import java.util.Collection;

import sim.app.AreaMgr;
import sim.app.ConstValues;
import sim.area.Area;
import sim.substance.AreaHospital;
import sim.substance.Hospital;
import sim.substance.Patient;
import sim.substance.Population;

public abstract class FactoryBase {

    //每万人普通床位数
    public float m_fPopulationBedsRate = 0;
    //每万人医生数
    public float m_fPopulationDoctorsRate = 0;
    //每万人ICU床数
    public float m_fPopulationICUBedsRate = 0;

    //初始化国家相关属性的参数
    public abstract void initCountryProps();

    //初始化病人的一些参数，比如体质等
    public void initPaitent(Patient onePaitent)
    {
        onePaitent.m_fInitHP = (float) (180+10*ConstValues.s_Random.nextGaussian());

        onePaitent.m_fIntensiveHP = onePaitent.m_fInitHP*0.2f;
        onePaitent.m_fOnsetHP = onePaitent.m_fInitHP*0.3f;
        onePaitent.m_fHealedHP = onePaitent.m_fInitHP*0.8f;

        onePaitent.m_fResistancePowerBase = (float) (0.1+0.01*ConstValues.s_Random.nextGaussian());
        onePaitent.m_fResistancePowerGrowthSpeed = (float) (0.04+0.02*ConstValues.s_Random.nextGaussian());

        //这两个病毒的参数实际应该由传染给他的人的病毒值来计算变异的
        onePaitent.m_fVirusCopySpeed = (float) (0.8+0.2*ConstValues.s_Random.nextGaussian());
        if (onePaitent.m_fVirusCopySpeed <= 0.2)
        {
            onePaitent.m_fVirusCopySpeed = 0.2f;
        }
        onePaitent.m_fVirulence = (float) (1+0.1*ConstValues.s_Random.nextGaussian());

        onePaitent.m_fVirusDensityToOnsetStage = 5f;
    }

    public abstract void initPopulations(Collection<Population> populations);

    public abstract void initArea(sim.area.Area rootArea);

    //根据国家参数先创建默认的医院和病床等
    public void initHospitals(Collection<Hospital> hospitalList)
    {
        initHospitals(AreaMgr.getInstance().m_RootArea, hospitalList);
    }

    public void initHospitals(Area area, Collection<Hospital> hospitalList)
    {
        Collection<Area> Areas = area.getChildAreas();

        if(Areas.isEmpty())
        {
            AreaHospital oneAreaHospital = new AreaHospital();
            area.setAreaHospital(oneAreaHospital);

            Collection<Population> pops = area.getPopulations();

            Hospital oneHospital = new Hospital();
            long lPopulationNum = 0;
            for (Population pop : pops)
            {
                lPopulationNum += pop.m_nPopulation;
                //pop.setAreaHospital(oneHospital);
            }
            int nDoctorNum = (int) (lPopulationNum*m_fPopulationDoctorsRate/10000);
            int nBedNum = (int) (lPopulationNum*m_fPopulationBedsRate/10000);
            int nICUBedNum = (int) (lPopulationNum*m_fPopulationICUBedsRate/10000);

            oneHospital.initHospital(nBedNum, nDoctorNum);
            oneHospital.m_strHospitalName = area.getAreaFullName()+"-人民医院";

            oneAreaHospital.addHospital(oneHospital);

            hospitalList.add(oneHospital);
        }

        for (Area oneArea : Areas)
        {
            initHospitals(oneArea, hospitalList);
        }
    }

}
