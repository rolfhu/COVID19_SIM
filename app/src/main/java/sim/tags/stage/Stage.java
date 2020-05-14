package sim.tags.stage;

import sim.app.PolicyMgr;
import sim.policy.quarantine.PolicyVillageQuarantine;
import sim.substance.Patient;
import sim.tags.TagBase;


public class Stage extends TagBase {
    public Stage() {
        super(getShortName());
    }

    public Stage(String strName) {
        super(strName);
    }

    static public String getShortName()
    {
        return new String("病程");
    }

    static public String getFullName()
    {
        return new String(getShortName());
    }

    //计算某人今日能感染多少人
    public int calcInfection(Patient onePatient)
    {
        return 0;
    }

    //计算抵抗力的变化
    public void calcResistance(Patient onePatient)
    {
        float fResistancePowerGrowthSpeedRateByHospital = 1.0f;
        if (onePatient.getHospital() != null)
        {
            fResistancePowerGrowthSpeedRateByHospital = 2.0f;
        }
        onePatient.m_fCurrentResistancePower = onePatient.m_fCurrentResistancePower+onePatient.m_fResistancePowerGrowthSpeed*fResistancePowerGrowthSpeedRateByHospital;
        if (onePatient.m_fCurrentResistancePower >= 0.9)
        {
            onePatient.m_fCurrentResistancePower = 0.9f;
        }
    }
    //计算HP的变化值
    public float calcDecreaseHP(Patient onePatient)
    {
        return 0;
    }

    //返回值表示病程的类型是否已经改变了
    public boolean calcStage(Patient onePatient)
    {
        return false;
    }

    public void initPatient(Patient onePatient)
    {
        onePatient.m_DaysInStage = 0;
    }
}
