package sim.app;

import android.util.Log;

import sim.strategy.hospital.IntoHospitalAll;
import sim.strategy.hospital.IntoHospitalNormal;
import sim.substance.Population;

public class Controller {
    //开始模拟的天数计数
    private int m_nSimDays = 0;

    private Controller() {}
    private static Controller s_single=null;
    public static Controller getInstance() {
        if (s_single == null) {
            s_single = new Controller();
        }
        return s_single;
    }

    public void Init()
    {
        TagMgr.getInstance().init();
        PopulationMgr.getInstance().init();
        //Area需要在Population之后初始化
        AreaMgr.getInstance().init();
        HospitalMgr.getInstance().init();
        PatientMgr.getInstance().init();
    }

    public int getSimDays()
    {
        return m_nSimDays;
    }

    public void runOneDay()
    {
        m_nSimDays++;
        //模拟感染的过程，更新各种人群的人数
        if (m_nSimDays<=100)
        {
            //某个时刻停止传染，用于查看长期死亡率
            infectPopulations();
        }

        if (m_nSimDays==80)
        {
            //开始应收尽收
            HospitalMgr.getInstance().changeStrategy(new IntoHospitalAll());
        }

        //对被感染的个人计算病程
        calcStages();

        //模拟进入医院的过程
        gotoHospital();

        //输出大致情况
        outputStatus();
    }

    private void gotoHospital()
    {
        PopulationMgr.getInstance().gotoHospital();
    }

    private void infectPopulations()
    {
        PatientMgr.getInstance().infectPopulations();
    }

    private void calcStages()
    {
        PatientMgr.getInstance().calcStages();
    }

    private void outputStatus()
    {
        String strText = "";
        strText = String.format("day=%d", m_nSimDays);
        Log.i("COVID19", strText);

        PatientMgr.getInstance().logOut();

        HospitalMgr.getInstance().logOut();

        strText = String.format("----------------------------------------------------");
        Log.i("COVID19", strText);
    }
}
