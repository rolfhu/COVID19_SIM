package sim.app;

import android.util.Log;

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
        TagMgr.getInstance().Init();
        PopulationMgr.getInstance().Init();
        PatientMgr.getInstance().Init();
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

        //对被感染的个人计算病程
        calcStages();

        //输出大致情况
        outputStatus();
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

        strText = String.format("----------------------------------------------------");
        Log.i("COVID19", strText);
    }
}
