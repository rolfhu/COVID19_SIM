package sim.app;

import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import sim.area.Area;
import sim.substance.Patient;
import sim.substance.Population;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.util.StatisticsAverage;
import sim.util.Tools;

public class PatientMgr {

    //几种不同阶段的病人的列表
    private Collection<Patient> m_PatientListIncubation = new HashSet<>();
    private Collection<Patient> m_PatientListOnset = new HashSet<>();
    private Collection<Patient> m_PatientListIntensive = new HashSet<>();
    private Collection<Patient> m_PatientListImmune = new HashSet<>();
    private Collection<Patient> m_PatientListDead = new HashSet<>();

    //各个阶段的计算速度不同，让进度更加线性化
    private float m_fIncubationSpeed = 1f;
    private float m_fOnsetSpeed = 1f;
    private float m_fIntensiveSpeed = 1f;

    private PatientMgr() {}
    private static PatientMgr s_single=null;
    public static PatientMgr getInstance() {
        if (s_single == null) {
            s_single = new PatientMgr();
        }
        return s_single;
    }

    public void init()
    {
        //产生零号病人
        Area area = AreaMgr.getInstance().findAreaByFullName("中国.湖北.武汉");
        Collection<Population> pops = area.getPopulations();
        Patient oneInfectPatient = PopulationMgr.getInstance().infectOnePopulation(pops);
        m_PatientListIncubation.add(oneInfectPatient);
    }

    public void infectPopulations()
    {
        int nOnsetNum = m_PatientListOnset.size();
        int nIntensiveNum = m_PatientListIntensive.size();
        int nTotalNum = (int) (nOnsetNum + nIntensiveNum);
        Controller.getInstance().startProgress("模拟感染的过程", nTotalNum);

        infectPopulations(m_PatientListOnset);
        infectPopulations(m_PatientListIntensive);
    }

    //每个病人对其所在区域的人群进行感染
    private void infectPopulations(Collection<Patient> patientList)
    {
        int nNum = 0;
        for (Patient onePatient : patientList)
        {
            nNum++;
            if (nNum == 1000)
            {
                Controller.getInstance().addProgress(nNum);
                nNum = 0;
            }

            int nInfectNum = onePatient.calcInfectNum();

            //得到传播次数后，根据人群中的免疫人群比例，计算出实际应该感染多少人
            if (nInfectNum == 0)
            {
                continue;
            }
            //先根据区域获取到本区域所有的人群列表
            Area area = onePatient.getPopulation().getArea();
            long lHealthyNum = area.getAllPopulationHealthyNums();
            long lIncubationNum = area.getAllPopulationNumsByStage(IncubationStage.class);
            long lOnsetNum = area.getAllPopulationNumsByStage(OnsetStage.class);
            long lIntensiveNum = area.getAllPopulationNumsByStage(IntensiveStage.class);
            long lImmuneNum = area.getAllPopulationNumsByStage(ImmuneStage.class);

            long lTotalNum = lHealthyNum+lIncubationNum+lOnsetNum+lIntensiveNum+lImmuneNum;
            float fInfectRate = (float)lHealthyNum/lTotalNum;

            for (int i=0; i<nInfectNum; i++)
            {
                float fRand = Tools.Random().nextFloat();
                if (fRand < fInfectRate)
                {
                    onePatient.m_InfectNum++;
                    Collection<Population> pops = area.getPopulations();
                    Patient oneInfectPatient = PopulationMgr.getInstance().infectOnePopulation(pops);
                    m_PatientListIncubation.add(oneInfectPatient);
                }
            }
        }
        Controller.getInstance().addProgress(nNum);
    }

    public void calcStages()
    {
        int nIncubationNum = m_PatientListIncubation.size();
        int nOnsetNum = m_PatientListOnset.size();
        int nIntensiveNum = m_PatientListIntensive.size();
        int nTotalNum = (int) (nIncubationNum/m_fIncubationSpeed + nOnsetNum/m_fOnsetSpeed + nIntensiveNum/m_fIntensiveSpeed);
        Controller.getInstance().startProgress("计算病程", nTotalNum);
        long time1 = System.currentTimeMillis();
        calcStages(m_PatientListIncubation, m_fIncubationSpeed);
        long time2 = System.currentTimeMillis();
        calcStages(m_PatientListOnset, m_fOnsetSpeed);
        long time3 = System.currentTimeMillis();
        calcStages(m_PatientListIntensive, m_fIntensiveSpeed);
        long time4 = System.currentTimeMillis();

        long lcalcStageIncubationCost = time2 - time1;
        long lcalcStageOnsetCost = time3 - time2;
        long lalcStageIntensiveCost = time4 - time3;

        lcalcStageIncubationCost = Math.max(lcalcStageIncubationCost, 1);
        lcalcStageOnsetCost = Math.max(lcalcStageOnsetCost, 1);
        lalcStageIntensiveCost = Math.max(lalcStageIntensiveCost, 1);

        float fIncubationSpeed = (float)nIncubationNum/lcalcStageIncubationCost;
        float fOnsetSpeed = (float)nOnsetNum/lcalcStageOnsetCost;
        float fIntensiveSpeed = (float)nIntensiveNum/lalcStageIntensiveCost;

        fIncubationSpeed = Math.max(fIncubationSpeed, 1f);
        fOnsetSpeed = Math.max(fOnsetSpeed, 1f);
        fIntensiveSpeed = Math.max(fIntensiveSpeed, 1f);

        while(true)
        {
            if (fIncubationSpeed>10 && fOnsetSpeed>10 && fIntensiveSpeed>10)
            {
                fIncubationSpeed /= 10;
                fOnsetSpeed /= 10;
                fIntensiveSpeed /= 10;
            }
            else
            {
                break;
            }
        }

        m_fIncubationSpeed = fIncubationSpeed;
        m_fOnsetSpeed = fOnsetSpeed;
        m_fIntensiveSpeed = fIntensiveSpeed;
    }

    private void calcStages(Collection<Patient> patientList, float fSpeed)
    {
        int nNum = 0;
        float fEndValue = 0f;
        Iterator it = patientList.iterator();
        while(it.hasNext())
        {
            nNum++;
            if (nNum == 1000)
            {
                int nSend = (int) (nNum/fSpeed+fEndValue);
                fEndValue =  (nNum/fSpeed)+fEndValue - nSend;
                Controller.getInstance().addProgress(nSend);
                nNum = 0;

            }
            Patient onePatient = (Patient) it.next();
            boolean bStageChanged = onePatient.calcStage();
            if (bStageChanged)
            {
                it.remove();
                addPatientToProperList(onePatient);
            }
        }

        Controller.getInstance().addProgress((int) (nNum/fSpeed));
    }

    private void addPatientToProperList(Patient onePatient)
    {
        boolean bNeedLeaveHospital = false;
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
            bNeedLeaveHospital = true;
            m_PatientListImmune.add(onePatient);
        }
        else if (classStage == DeadStage.class)
        {
            bNeedLeaveHospital = true;
            m_PatientListDead.add(onePatient);
        }

        if (bNeedLeaveHospital)
        {
            onePatient.leaveHospital();
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
