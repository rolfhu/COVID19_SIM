package sim.app;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import sim.substance.Patient;
import sim.substance.Population;
import sim.tags.TagBase;
import sim.tags.area.Area;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.util.StatisticsAverage;

public class PatientMgr {

    //几种不同阶段的病人的列表
    private Collection<Patient> m_PatientListIncubation = new HashSet<>();
    private Collection<Patient> m_PatientListOnset = new HashSet<>();
    private Collection<Patient> m_PatientListIntensive = new HashSet<>();
    private Collection<Patient> m_PatientListImmune = new HashSet<>();
    private Collection<Patient> m_PatientListDead = new HashSet<>();

    private PatientMgr() {}
    private static PatientMgr s_single=null;
    public static PatientMgr getInstance() {
        if (s_single == null) {
            s_single = new PatientMgr();
        }
        return s_single;
    }

    public void Init()
    {
        //产生零号病人
        TagBase areaTag = TagMgr.getInstance().findTagByFullName("湖北.武汉");
        Collection<Population> pops = PopulationMgr.getInstance().getPopulationsByTag(areaTag);
        Patient onePatient = PopulationMgr.getInstance().infectOnePopulation(pops);
        m_PatientListIncubation.add(onePatient);
    }

    public void infectPopulations()
    {
        infectPopulations(m_PatientListOnset);
        infectPopulations(m_PatientListIntensive);
    }

    //每个病人对其所在区域的人群进行感染
    private void infectPopulations(Collection<Patient> patientList)
    {
        for (Patient onePatient : patientList)
        {
            int nInfectNum = onePatient.calcInfectNum();
            //得到传播次数后，根据人群中的免疫人群比例，计算出实际应该感染多少人

            if (nInfectNum == 0)
            {
                continue;
            }
            //先根据区域获取到本区域所有的人群列表
            Area areaTag = onePatient.getAreaTag();
            long lHealthyNum = areaTag.getAllPopulationHealthyNums();
            long lIncubationNum = areaTag.getAllPopulationNumsByStage(IncubationStage.class);
            long lOnsetNum = areaTag.getAllPopulationNumsByStage(OnsetStage.class);
            long lIntensiveNum = areaTag.getAllPopulationNumsByStage(IntensiveStage.class);
            long lImmuneNum = areaTag.getAllPopulationNumsByStage(ImmuneStage.class);

            long lTotalNum = lHealthyNum+lIncubationNum+lOnsetNum+lIntensiveNum+lImmuneNum;
            float fInfectRate = (float)lHealthyNum/lTotalNum;

            for (int i=0; i<nInfectNum; i++)
            {
                float fRand = ConstValues.s_Random.nextFloat();
                if (fRand < fInfectRate)
                {
                    onePatient.m_InfectNum++;
                    Collection<Population> pops = PopulationMgr.getInstance().getPopulationsByTag(areaTag);
                    Patient oneInfectPatient = PopulationMgr.getInstance().infectOnePopulation(pops);
                    m_PatientListIncubation.add(oneInfectPatient);
                }
            }
        }
    }

    public void calcStages()
    {
        calcStages(m_PatientListIncubation);
        calcStages(m_PatientListOnset);
        calcStages(m_PatientListIntensive);
    }

    private void calcStages(Collection<Patient> patientList)
    {
        Iterator it = patientList.iterator();
        while(it.hasNext())
        {
            Patient onePatient = (Patient) it.next();
            boolean bStageChanged = onePatient.calcStage();
            if (bStageChanged)
            {
                it.remove();
                addPatientToProperList(onePatient);
            }
        }
    }

    private void addPatientToProperList(Patient onePatient)
    {
        Class classStage = onePatient.getStageTag().getClass();
        if (classStage == IncubationStage.class)
        {
            m_PatientListIncubation.add(onePatient);
        }
        else if (classStage == OnsetStage.class)
        {
            m_PatientListOnset.add(onePatient);
        }
        else if (classStage == IntensiveStage.class)
        {
            m_PatientListIntensive.add(onePatient);
        }
        else if (classStage == ImmuneStage.class)
        {
            m_PatientListImmune.add(onePatient);
        }
        else if (classStage == DeadStage.class)
        {
            m_PatientListDead.add(onePatient);
        }
    }

    public void logOut()
    {
        String strText = "";
        strText = String.format("潜伏期=%d", m_PatientListIncubation.size());
        Log.i("summary", strText);

        int nTotalOnset = m_PatientListOnset.size();
        int nTotalIntensive = m_PatientListIntensive.size();
        int nTotalImmune = m_PatientListImmune.size();
        int nTotalDead = m_PatientListDead.size();
        int nTotalPatient = nTotalOnset + nTotalIntensive + nTotalImmune + nTotalDead;

        float fIntensiveRate = 0;
        if (nTotalIntensive != 0)
        {
            fIntensiveRate = (nTotalIntensive*100f)/(nTotalOnset+nTotalIntensive);
        }
        strText = String.format("发病=%d 重症=%d(重症率=%.2f%%)", nTotalOnset+nTotalIntensive, nTotalIntensive, fIntensiveRate);
        Log.i("summary", strText);

        strText = String.format("康复=%d(康复率=%.2f%%)", nTotalImmune, (nTotalImmune*100f)/nTotalPatient);
        Log.i("summary", strText);

        strText = String.format("死亡=%d(死亡率=%.2f%%)", nTotalDead, (nTotalDead*100f)/nTotalPatient);
        Log.i("summary", strText);

        StatisticsAverage saR0 = new StatisticsAverage("R0");
        StatisticsAverage saIncubationDays = new StatisticsAverage("潜伏期天数");
        StatisticsAverage saIntensiveRate = new StatisticsAverage("总重症率");
        StatisticsAverage saDeadDays = new StatisticsAverage("死者病程天数");
        StatisticsAverage saImmuneDays = new StatisticsAverage("康复者病程天数");
        StatisticsAverage saImmuneOnsetDays = new StatisticsAverage("康复者轻症天数");
        StatisticsAverage saImmuneIntensiveDays = new StatisticsAverage("康复者重症天数");
        StatisticsAverage saImmuneIntensiveRate = new StatisticsAverage("康复者重症率");

        for (Patient onePatient : m_PatientListImmune)
        {
            saR0.addIntData(onePatient.m_InfectNum);
            saIncubationDays.addIntData(onePatient.m_IncubationDays);
            if (onePatient.m_IntensiveDays>0)
            {
                saIntensiveRate.addIntData(1);
                saImmuneIntensiveRate.addIntData(1);
            }
            else
            {
                saIntensiveRate.addIntData(0);
                saImmuneIntensiveRate.addIntData(0);
            }

            saImmuneDays.addIntData(onePatient.m_OnsetDays+onePatient.m_IntensiveDays);
            saImmuneOnsetDays.addIntData(onePatient.m_OnsetDays);
            saImmuneIntensiveDays.addIntData(onePatient.m_IntensiveDays);
        }

        for (Patient onePatient : m_PatientListDead)
        {
            saR0.addIntData(onePatient.m_InfectNum);
            saIncubationDays.addIntData(onePatient.m_IncubationDays);
            saIntensiveRate.addIntData(1);
            saDeadDays.addIntData(onePatient.m_OnsetDays+onePatient.m_IntensiveDays);
        }

        Log.i("statistics", saR0.getResult());
        Log.i("statistics", saIncubationDays.getResult());
        Log.i("statistics", saIntensiveRate.getResultByPercent());
        Log.i("statistics", saImmuneIntensiveRate.getResultByPercent());
        Log.i("statistics", saDeadDays.getResult());
        Log.i("statistics", saImmuneDays.getResult());
        Log.i("statistics", saImmuneOnsetDays.getResult());
        Log.i("statistics", saImmuneIntensiveDays.getResult());
    }
}
