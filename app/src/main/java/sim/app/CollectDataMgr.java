package sim.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import sim.area.Area;
import sim.substance.Population;
import sim.substance.PopulationList;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;

//收集每天的统计数据
public class CollectDataMgr {

    public class OneDayData
    {
        public int m_nDay = 0;

        private HashMap<String, Object> m_DataMap = new HashMap<>();

        private void AddData(String strDataName, Object objectValue)
        {
            m_DataMap.put(strDataName, objectValue);
        }

        public Object getData(String strDataName)
        {
            if(m_DataMap.containsKey(strDataName))
            {
                return m_DataMap.get(strDataName);
            }
            else
            {
                return null;
            }
        }

        public void collectData() {
            m_nDay = Controller.getInstance().getSimDays();

            HashSet<Area> Areas = AreaMgr.getInstance().m_RootArea.getAreaSets();

            for (Area oneArea:Areas)
            {
                PopulationList oneAreaPopulationList = oneArea.getPopulationListWithAllSubArea();

                Collection<Population> pops = oneAreaPopulationList.getPopulations();

                int nIncubationNum = 0;
                int nIntensiveNum = 0;
                int nOnsetNum = 0;
                int nImmuneNum = 0;
                int nDeadNum = 0;

                for (Population onePop : pops)
                {
                    nIncubationNum += onePop.getStageNums(IncubationStage.class);
                    nIntensiveNum += onePop.getStageNums(IntensiveStage.class);
                    nOnsetNum += onePop.getStageNums(OnsetStage.class);
                    nImmuneNum += onePop.getStageNums(ImmuneStage.class);
                    nDeadNum += onePop.getStageNums(DeadStage.class);
                }

                AddData("Area_"+oneArea.getAreaFullName()+"_"+IncubationStage.getFullName(), new Integer(nIncubationNum));
                AddData("Area_"+oneArea.getAreaFullName()+"_"+IntensiveStage.getFullName(), new Integer(nIntensiveNum));
                AddData("Area_"+oneArea.getAreaFullName()+"_"+OnsetStage.getFullName(), new Integer(nOnsetNum));
                AddData("Area_"+oneArea.getAreaFullName()+"_"+ImmuneStage.getFullName(), new Integer(nImmuneNum));
                AddData("Area_"+oneArea.getAreaFullName()+"_"+DeadStage.getFullName(), new Integer(nDeadNum));
            }
        }
    }

    //每天的数据，下标是天数
    private ArrayList<OneDayData> m_AllDataArray = new ArrayList<>();

    private CollectDataMgr() {
        m_AllDataArray.add(new OneDayData());
    }
    private static CollectDataMgr s_single=null;
    public static CollectDataMgr getInstance() {
        if (s_single == null) {
            s_single = new CollectDataMgr();
        }
        return s_single;
    }

    //每天结束时，计算当天的数据并保存起来
    public void collectTodayData() {
        int nToday = Controller.getInstance().getSimDays();
        if (m_AllDataArray.size() > nToday)
        {
            return;
        }
        OneDayData todayData = new OneDayData();

        todayData.collectData();

        m_AllDataArray.add(todayData);
    }

    //获取某一项数据的每天的值的数组
    public ArrayList<Object> getDataListByName(String strDataName) {

        ArrayList<Object> result = new ArrayList<Object>();
        for (OneDayData oneDay:m_AllDataArray)
        {
            Object value = oneDay.getData(strDataName);
            result.add(oneDay.m_nDay, value);
        }

        return result;
    }

}
