package sim.worlds;

import sim.app.ConstValues;
import sim.substance.Patient;

public class FactoryBase {

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
}
