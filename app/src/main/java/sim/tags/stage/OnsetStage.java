package sim.tags.stage;

import sim.app.PolicyMgr;
import sim.policy.quarantine.PolicyVillageQuarantine;
import sim.substance.Patient;
import sim.tags.TagUtility;
import sim.util.Tools;

//发病期，有传染力
public class OnsetStage extends Stage {
    public OnsetStage() {
        super(getShortName());
    }

    public OnsetStage(String strName) {
        super(strName);
    }

    static public String getShortName()
    {
        return new String("轻症期");
    }

    static public String getFullName()
    {
        return new String(Stage.getFullName()+ TagUtility.TAG_SEPARATOR+getShortName());
    }

    //计算某人今日能感染多少人，包含了外部政策的修正
    @Override
    public int calcInfection(Patient onePatient)
    {
        float fInfectionPower = onePatient.getInfectionPowerByPolicy();

        //根据几率来计算最后感染的人数
        float fRand = Tools.Random().nextFloat();
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
    public float calcDecreaseHP(Patient onePatient)
    {
        float fDecreaseHP = 0;
        if (onePatient.m_fCurrentResistancePower>0.8)
        {
            //回血
            fDecreaseHP = (float) -(10*(onePatient.m_fCurrentResistancePower-0.75));
        }
        else
        {
            //扣血
            fDecreaseHP = 20*(1-onePatient.m_fCurrentResistancePower)*onePatient.m_fVirulence;
        }
        return fDecreaseHP;
    }

    @Override
    public boolean calcStage(Patient onePatient)
    {
        onePatient.m_OnsetDays++;
        if (onePatient.m_fCurrentHP >= onePatient.m_fHealedHP &&
                onePatient.m_fCurrentResistancePower >= 0.8)
        {
            onePatient.changeToNewStage(ImmuneStage.getFullName());
            return true;
        }

        if (onePatient.m_fCurrentHP <= onePatient.m_fIntensiveHP)
        {
            onePatient.changeToNewStage(IntensiveStage.getFullName());
            return true;
        }
        return false;
    }

}
