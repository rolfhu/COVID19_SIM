package sim.app;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import me.sim.COVID19.R;
import me.sim.COVID19.ui.home.HomeFragment;
import me.sim.COVID19.ui.home.HomeViewModel;
import sim.worlds.FactoryMgr;

public class Controller {
    //开始模拟的天数计数
    private int m_nSimDays = 0;
    private Calendar m_StartDate = null;

    private HandlerThread m_MainSimThread = null;
    private Handler m_MainSimThreadHandler = null;

    public Activity m_context = null;
    private Controller() {}
    private static Controller s_single=null;
    public static Controller getInstance() {
        if (s_single == null) {
            s_single = new Controller();
        }
        return s_single;
    }

    public void Init(Activity context)
    {
        m_context = context;

        TagMgr.getInstance().init();
        PopulationMgr.getInstance().init();
        //Area需要在Population之后初始化
        AreaMgr.getInstance().init();
        HospitalMgr.getInstance().init();
        PatientMgr.getInstance().init();
        PolicyMgr.getInstance().init();

        m_StartDate = FactoryMgr.getInstance().getFactory().m_StartDate;

        m_MainSimThread = (new HandlerThread("MainSimThread"));
        m_MainSimThread.start();

        m_MainSimThreadHandler = new Handler( m_MainSimThread.getLooper() ){
            @Override
            public void handleMessage(Message msg)
            {
                for (int i=0;i<msg.arg1;i++)
                {
                    runOneDay();
                }

                m_context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SimDaysDone();
                    }
                });
            }
        };
    }

    private void SimDaysDone()
    {

    }

    public void postRunDays(int days)
    {
        Message msg = Message.obtain();
        msg.what = 1;
        msg.arg1 = days;
        m_MainSimThreadHandler.sendMessage(msg);
    }

    public int getSimDays()
    {
        return m_nSimDays;
    }

    //是否已经到达指定的日期
    public boolean isDateReached(String strDate)
    {
        int nDate = getSimDaysByDate(strDate);
        return getSimDays() >= nDate;
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

        m_context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimDayStart();
            }
        });

        //每天初始化的部分
        PolicyMgr.getInstance().onDayStart();

        setProgress("模拟人群的流动", 0);
        //模拟人群的流动
        doTransfer();

        //模拟感染的过程，更新各种人群的人数
        if (m_nSimDays<=100)
        {
            //某个时刻停止传染，用于查看长期死亡率
            setProgress("模拟感染的过程", 0);
            infectPopulations();
        }

//        if (m_nSimDays==getSimDaysByDate("2020-2-5"))
//        {
//            //开始应收尽收
//            HospitalMgr.getInstance().changeStrategy(new IntoHospitalAll());
//        }

        //对被感染的个人计算病程
        setProgress("对被感染的个人计算病程", 0);
        calcStages();

        //模拟进入医院的过程
        setProgress("模拟进入医院的过程", 0);
        gotoHospital();

        //输出大致情况
        setProgress("输出日志", 0);
        outputStatus();

        //收集图表数据
        setProgress("收集图表数据", 0);
        collectDataForChart();

        m_context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimDayDone();
            }
        });
    }

    private void setProgress(final String strPeriod, final int progressPercent)
    {
        m_context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SimDayProgress(strPeriod, progressPercent);
            }
        });
    }

    private void setSimDayUIProgressText(String strText)
    {
        FragmentManager fragmentManager = ((FragmentActivity) m_context).getSupportFragmentManager();
        Fragment fNav = fragmentManager.findFragmentById(R.id.nav_host_fragment);
        FragmentManager fNavManager = fNav.getChildFragmentManager();
        List<Fragment> fragmentList = fNavManager.getFragments();
        for (Fragment frag : fragmentList)
        {
            if (frag instanceof HomeFragment)
            {
                ViewModelProviders.of(frag).get(HomeViewModel.class).setProgressText(strText);
            }
        }
    }

    private void SimDayDone()
    {
        String strText = "";
        Calendar cal = getToday();
        strText = String.format("%d-%02d-%02d 完成计算",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
        setSimDayUIProgressText(strText);
    }

    private void SimDayStart()
    {
        String strText = "";
        Calendar cal = getToday();
        strText = String.format("%d-%02d-%02d 开始计算",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE));
        setSimDayUIProgressText(strText);
    }

    private void SimDayProgress(String strPeriod, int progressPercent)
    {
        String strText = "";
        Calendar cal = getToday();
        strText = String.format("%d-%02d-%02d %s %d%%",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DATE),
                strPeriod, progressPercent);

        setSimDayUIProgressText(strText);
    }

    private void doTransfer()
    {
        AreaMgr.getInstance().m_RootArea.doTransfer(null);
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

        AreaMgr.getInstance().logOut();

        strText = String.format("----------------------------------------------------");
        Log.i("COVID19", strText);
    }

    private void collectDataForChart() {
        CollectDataMgr.getInstance().collectTodayData();
    }

}
