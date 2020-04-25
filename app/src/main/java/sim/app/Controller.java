package sim.app;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import sim.strategy.hospital.IntoHospitalAll;
import sim.worlds.FactoryMgr;

public class Controller {
    //开始模拟的天数计数
    private int m_nSimDays = 0;
    private Calendar m_StartDate = null;

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

        m_StartDate = FactoryMgr.getInstance().getFactory().m_StartDate;
    }

    public int getSimDays()
    {
        return m_nSimDays;
    }

    //根据日期（年-月-日）获取模拟的天数
    public int getSimDaysByDate(String strDate)
    {
        Calendar cal = (Calendar) m_StartDate.clone();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try{
            cal.setTime(format.parse(strDate));
        }catch(ParseException e) {
            e.printStackTrace();
        }

        long timeStart = m_StartDate.getTimeInMillis();
        long timeEnd = cal.getTimeInMillis();

        int between_days= (int) ((timeEnd-timeStart)/(1000*3600*24));

        return between_days;
    }

    public Calendar getToday()
    {
        Calendar today = (Calendar) m_StartDate.clone();
        today.add(Calendar.DATE, m_nSimDays);

        return today;
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

        if (m_nSimDays==getSimDaysByDate("2020-2-5"))
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
        Calendar cal = getToday();
        strText = String.format("%d-%02d-%02d day=%d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE),
                m_nSimDays);
        Log.i("COVID19", strText);

        PatientMgr.getInstance().logOut();

        HospitalMgr.getInstance().logOut();

        strText = String.format("----------------------------------------------------");
        Log.i("COVID19", strText);
    }
}
