package sim.substance;

import sim.app.Controller;
import sim.app.PolicyMgr;
import sim.app.TagMgr;
import sim.policy.quarantine.PolicyVillageQuarantine;
import sim.tags.stage.IntensiveStage;
import sim.worlds.FactoryMgr;
import sim.tags.ITagHost;
import sim.tags.TagBase;
import sim.tags.Tags;
import sim.tags.stage.OnsetStage;
import sim.tags.stage.Stage;

public class Patient implements ITagHost {

    private Tags        m_Tags = null;

    //自己所属的人群
    private Population  m_Population = null;

    private Stage       m_Stage = null;

    private Hospital    m_Hospital = null;

    //病程的进展计算过哪一天了
    //是和Controller.m_nSimDays对应的
    //由于病程的进展，病人会在数个病程的集合中转移，本变量是为了避免一天算两次
    private int         m_CalcedDay = 0;

    //发病期阶段预计需要几天
    //public float        m_OnsetStageDays = ConstValues.OnsetStageDays;

    //潜伏期阶段预计需要几天
    //public float        m_IncubationStageDays = ConstValues.IncubationStageDays;

    //在本阶段已经过了几天
    public int          m_DaysInStage = 0;


    //一些统计性的数据==========================
    //感染了多少个人
    public int          m_InfectNum = 0;
    //在潜伏期呆了几天
    public int          m_IncubationDays = 0;
    //在轻症期呆了几天
    public int          m_OnsetDays = 0;
    //在重症期呆了几天
    public int          m_IntensiveDays = 0;

    //以下是病人体质相关的一些参数，一般不会变=====================================
    //初始生命值，当m_fCurrentHP降为0后算死亡
    public float m_fInitHP;
    //多少生命值以下算从轻症变为重症
    public float m_fIntensiveHP;
    //多少生命值以上算从重症变回轻症
    public float m_fOnsetHP;
    //多少生命值以上算痊愈
    public float m_fHealedHP;
    //抵抗力的基础值
    public float m_fResistancePowerBase;
    //抵抗力提升的速度
    public float m_fResistancePowerGrowthSpeed;
    //体内病毒浓度，达到多少值后，从潜伏期转换成发病期
    public float m_fVirusDensityToOnsetStage;

    //以下是此人身上病毒的参数，一般不会变=====================================
    //病毒复制速度，影响m_fVirusDensity
    public float m_fVirusCopySpeed;
    //病毒的毒力，影响m_fCurrentHP的下降速度,值域为(0-1)
    public float m_fVirulence;

    //以下是病人的当前参数，根据病程进展会变化=========================================
    //当前的生命值
    public float m_fCurrentHP;
    //当前的抵抗力值,值域为(0-1)
    public float m_fCurrentResistancePower;
    //当前的体内病毒浓度，达到m_fVirusDensityToOnsetStage后，从潜伏期转换成发病期,值域为(0-10)
    public float m_fVirusDensity;


    public void Init(Tags tags)
    {
        m_Tags = new Tags(tags);
        m_Tags.setTagHost(this);
        m_Stage = (Stage) tags.findTagByBaseClass(Stage.class);

        FactoryMgr.getInstance().getFactory().initPaitent(this);

        m_fCurrentHP = m_fInitHP;
        m_fCurrentResistancePower = m_fResistancePowerBase;
        m_fVirusDensity = 0;
    }

    //ITagHost
    @Override
    public void onAddOneTag(TagBase oneTag)
    {
        if(oneTag instanceof Stage)
        {
            if (m_Population != null)
            {
                m_Population.addPatient(this);
            }
        }
    }

    @Override
    public void onRemoveOneTag(TagBase oneTag)
    {
        if(oneTag instanceof Stage)
        {
            m_Population.removePatient(this, oneTag);
        }
    }

    //获取今日传染力度，0为最低，1为100%传染1人，超过1为可能传染多人
    //传染力度是根据此人本身的属性、所感染的病毒、病程的阶段、发病的严重程度进行计算
    //不包括外部的干预政策
    private float getInfectionPower()
    {
        if (m_Stage == null)
        {
            return 0;
        }
        if (!(m_Stage instanceof OnsetStage))
        {
            return 0;
        }

        float fResult = 0.0f;
        fResult = m_fVirusDensity/20;
        if (fResult >= 3)
        {
            fResult = 3;
        }
        return fResult;
    }

    public float getInfectionPowerByPolicy()
    {
        float fInfectionPower = getInfectionPower();

        if (m_Hospital != null)
        {
            //住进医院认为不再传染
            return 0;
        }

        if (PolicyMgr.getInstance().isPolicyActive(PolicyVillageQuarantine.class))
        {
            fInfectionPower /= 5;
        }

        return fInfectionPower;
    }

    //计算病人有机会发生多少次传播
    public int calcInfectNum()
    {
        int nInfectNum = m_Stage.calcInfection(this);
        return nInfectNum;
    }

    public Stage getStageTag()
    {
        return m_Stage;
    }

    public Population getPopulation()
    {
        return m_Population;
    }

    public void setPopulation(Population pop)
    {
        m_Population = pop;
    }

    public void setHospital(Hospital hospital)
    {
        m_Hospital = hospital;
    }

    public Hospital getHospital()
    {
        return m_Hospital;
    }


    public void changeToNewStage(String strNewStageName)
    {
        m_Tags.deleteOneTag(m_Stage);
        m_Stage = (Stage) TagMgr.getInstance().findTagByFullName(strNewStageName);
        m_Tags.addTag(m_Stage);
        m_Stage.initPatient(this);
    }

    //对自己的病程进行一天的进展计算
    //返回值是病程的类型是否已经改变了
    public boolean calcStage()
    {
        //如果今天的已经算过了，那么跳过
        if(m_CalcedDay == Controller.getInstance().getSimDays())
        {
            return false;
        }

        m_CalcedDay = Controller.getInstance().getSimDays();
        m_DaysInStage++;

        if (m_DaysInStage >= 50)
        {
            int i=0;
            i++;
        }

        //先计算各项参数值的变化
        calcProps();

        return m_Stage.calcStage(this);
    }

    private void calcProps()
    {
        //计算抵抗力的变化
        m_Stage.calcResistance(this);

        //计算病毒浓度的变化
        m_fVirusDensity = m_fVirusDensity + m_fVirusCopySpeed;
        if (m_fVirusDensity>=100)
        {
            m_fVirusDensity = 100;
        }

        //计算生命值的变化
        if (m_Stage.getClass() == OnsetStage.class ||
                m_Stage.getClass() == IntensiveStage.class)
        {
            float fDecreaseHP = m_Stage.calcDecreaseHP(this);
            m_fCurrentHP = m_fCurrentHP - fDecreaseHP;
        }

    }

    public void gotoHospital(AreaHospital hospital)
    {
        //根据策略来决定是否能进入医院（住院）
        if (hospital == null)
        {
            return;
        }

        hospital.addPatient(this);
    }

    public void leaveHospital()
    {
        if (m_Hospital == null)
        {
            return;
        }

        m_Hospital.removePatient(this);
    }
}
