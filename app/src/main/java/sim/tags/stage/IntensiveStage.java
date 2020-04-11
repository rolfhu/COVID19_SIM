package sim.tags.stage;

import sim.app.ConstValues;
import sim.substance.Patient;
import sim.tags.TagUtility;

//重症期，有传染力
public class IntensiveStage extends Stage {
    public IntensiveStage() {
        super(getShortName());
    }

    public IntensiveStage(String strName) {
        super(strName);
    }

    static public String getShortName()
    {
        return new String("重症期");
    }

    static public String getFullName()
    {
        return new String(Stage.getFullName()+ TagUtility.TAG_SEPARATOR+getShortName());
    }

    //计算某人今日能感染多少人，包含了外部政策的修正
    @Override
    public int calcInfection(Patient onePatient)
    {
        float fInfectionPower = onePatient.getInfectionPower();

        //根据几率来计算最后感染的人数
        float fRand = ConstValues.s_Random.nextFloat();
        int nRet = 0;
        if (fInfectionPower > 1)
        {
            nRet = (int) fInfectionPower;
            fInfectionPower -= nRet;
        }
        if (fRand < fInfectionPower)
        {
            nRet++;
        }

        return nRet;
    }

    @Override
    public void calcResistance(Patient onePatient)
    {
        onePatient.m_fCurrentResistancePower = onePatient.m_fCurrentResistancePower+onePatient.m_fResistancePowerGrowthSpeed/3.0f;
        if (onePatient.m_fCurrentResistancePower >= 0.9)
        {
            onePatient.m_fCurrentResistancePower = 0.9f;
        }
    }

    @Override
    public float calcDecreaseHP(Patient onePatient)
    {
        float fDecreaseHP = 0;
        if (onePatient.m_fCurrentResistancePower>0.8)
        {
            //回血
            fDecreaseHP = (float) -(3*(onePatient.m_fCurrentResistancePower-0.75));
        }
        else
        {
            //扣血
            fDecreaseHP = 3*(1-onePatient.m_fCurrentResistancePower)*onePatient.m_fVirulence;
        }
        return fDecreaseHP;
    }

    @Override
    public boolean calcStage(Patient onePatient)
    {
        onePatient.m_IntensiveDays++;
        if (onePatient.m_fCurrentHP >= onePatient.m_fOnsetHP)
        {
            onePatient.changeToNewStage(OnsetStage.getFullName());
            return true;
        }

        if (onePatient.m_fCurrentHP <= 0)
        {
            onePatient.changeToNewStage(DeadStage.getFullName());
            return true;
        }
        return false;
    }
}
