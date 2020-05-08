package sim.area;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import sim.app.AreaMgr;
import sim.app.PopulationMgr;
import sim.strategy.quarantine.AreaQuarantineStrategy;
import sim.strategy.quarantine.IAreaQuarantineStrategy;
import sim.substance.AreaHospital;
import sim.substance.Patient;
import sim.substance.Population;
import sim.substance.PopulationList;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.util.Tools;

public class Area {
    final static public String AREA_SEPARATOR = ".";

    private String m_strShortName = "";
    private String m_strFullName = "";

    //面积，平方公里
    private long m_nSpace = 0;

    //迁移的指数，本区域内参与迁出的人数和总人数的比例
    private float m_fTransferRate = 0;

    //往外迁移的指数，本区域内参与迁移的人数中，迁移到上级区域的人数占比，也就是说，剩余的人数是在自己和自己的平级区域之间迁移
    private float m_fTransferToParentRate = 0;

    private AreaHospital m_AreaHospital = null;

    private IAreaQuarantineStrategy m_AreaQuarantineStrategy = new AreaQuarantineStrategy();

    private Area m_ParentArea = null;

    private Map<String, Area> m_ChildAreaMap = new HashMap<>();

    //直属于本区域的人群列表
    //private Collection<Population> m_Populations = new HashSet<>();
    private PopulationList m_PopulationList = new PopulationList();


    //一些临时的数据，每日更新时计算使用的
    //当天的各类人群的迁出人数
    private float m_fTransferPopNums[] = null;

    public Area(String strAreaName)
    {
        setAreaShortName(strAreaName);
        m_fTransferPopNums = new float[IAreaQuarantineStrategy.m_PopTypeNum];
        for (float fValue : m_fTransferPopNums)
        {
            fValue = 0f;
        }
    }

    public String getAreaShortName()
    {
        return m_strShortName;
    }

    public void setAreaShortName(String strName)
    {
        m_strShortName = strName;
        if (m_ParentArea == null)
        {
            m_strFullName = getAreaShortName();
        }
        else
        {
            m_strFullName = m_ParentArea.getAreaFullName() + AREA_SEPARATOR + getAreaShortName();
        }
    }

    public String getAreaFullName()
    {
        return m_strFullName;
    }

    public void setParentArea(Area ParentArea)
    {
        m_ParentArea = ParentArea;
        m_strFullName = m_ParentArea.getAreaFullName()+AREA_SEPARATOR+getAreaShortName();
    }

    public Area getParentArea()
    {
        return m_ParentArea;
    }

    public Area getChildArea(String strAreaName)
    {
        return m_ChildAreaMap.get(strAreaName);
    }

    public Collection<Area> getChildAreas()
    {
        return m_ChildAreaMap.values();
    }

    public void addChildArea(Area ChildArea)
    {
        ChildArea.setParentArea(this);
        m_ChildAreaMap.put(ChildArea.getAreaShortName(), ChildArea);
    }

    //strHalfName不包括自己的名字
    public Area findAreaByHalfName(String strHalfName)
    {
        if (strHalfName.isEmpty())
        {
            return this;
        }

        String strAreaName = AreaMgr.getAreaFirstName(strHalfName);

        Area subArea = getChildArea(strAreaName);
        if (subArea == null)
        {
            return null;
        }
        String strAreaNextNames = AreaMgr.getAreaNextNames(strHalfName);
        return subArea.findAreaByHalfName(strAreaNextNames);
    }

    public void setSpace(long space) {
        this.m_nSpace = space;
    }

    public long getSpace() {
        return m_nSpace;
    }

    public void setTransferRate(float fTransferRate)
    {
        this.m_fTransferRate = fTransferRate;
    }

    public float getTransferRate()
    {
        return m_fTransferRate;
    }

    public void setTransferToParentRate(float fTransferToParentRate)
    {
        this.m_fTransferToParentRate = fTransferToParentRate;
    }

    public float getTransferToParentRate()
    {
        return m_fTransferToParentRate;
    }

    public float[] getTransferPopNums()
    {
        return m_fTransferPopNums;
    }

    public AreaHospital getAreaHospital()
    {
        return m_AreaHospital;
    }

    public void setAreaHospital(AreaHospital areaHospital)
    {
        this.m_AreaHospital = areaHospital;
    }

    public IAreaQuarantineStrategy getQuarantineStrategy()
    {
        return m_AreaQuarantineStrategy;
    }

    public void setAreaQuarantineStrategy(IAreaQuarantineStrategy quarantineStrategy)
    {
        this.m_AreaQuarantineStrategy = quarantineStrategy;
    }

    public void addPopulation(Population pop)
    {
        m_PopulationList.mergePopulation(pop);
        pop.setArea(this);
    }

    public void removePopulation(Population pop)
    {
        m_PopulationList.removePopulation(pop);
    }

    //将自己按照人数比例分割为指定的几个区域，以及切分后，每份的区域短名
    public void splitArea(Pair<Float, String>[] splitArray)
    {
        //创建子区域
        Float[] fArray = new Float[splitArray.length];
        int nIndex = 0;
        for (Pair<Float, String> areaPair: splitArray)
        {
            addChildArea(new Area(areaPair.second));
            fArray[nIndex] = areaPair.first;
            nIndex++;
        }

        //对每个人群均进行切分
        for (Population pop: m_PopulationList.getPopulations())
        {
            ArrayList<Population> resultArray = pop.splitPopulations(fArray, true);

            Iterator<Population> Iter = resultArray.iterator();

            for (Pair<Float, String> areaPair: splitArray)
            {
                Population pop2 = Iter.next();
                getChildArea(areaPair.second).addPopulation(pop2);
                PopulationMgr.getInstance().addPopulation(pop2);
            }

            removePopulation(pop);
            PopulationMgr.getInstance().deletePopulation(pop);
        }
    }

    //获取本区域下所有人群的健康人数总和
    public long getAllPopulationHealthyNums()
    {
        long lResult = 0;

        for (Population onePop : m_PopulationList.getPopulations())
        {
            lResult += onePop.m_nPopulation;
        }

        return lResult;
    }

    //获取本区域下所有人群的各类病程人数总和
    public long getAllPopulationNumsByStage(Class classType)
    {
        long lResult = 0;

        for (Population onePop : m_PopulationList.getPopulations())
        {
            lResult += onePop.getStageNums(classType);
        }

        return lResult;
    }

    public PopulationList getPopulationList() {
        return m_PopulationList;
    }

    public Collection<Population> getPopulations() {
        return m_PopulationList.getPopulations();
    }

    //对本区域进行人口流动操作
    public void doTransfer(PopulationList populationListTransferOut)
    {
        //是叶子节点，则只计算流出人数和流出人员列表
        if(m_ChildAreaMap.isEmpty())
        {
            PopulationList popList = m_AreaQuarantineStrategy.getTransferOutPopulationList(this);
            populationListTransferOut.mergePopulationList(popList);
            return;
        }

        //对每个子区域进行迁出操作，得到本区域内所有子区域的迁出列表
        PopulationList populationListTransferThisArea = new PopulationList();
        Collection<Area> childAreas = getChildAreas();
        for (Area subArea : childAreas)
        {
            PopulationList subAreaPopulationListTransferOut = new PopulationList();
            subArea.doTransfer(subAreaPopulationListTransferOut);
            populationListTransferThisArea.mergePopulationList(subAreaPopulationListTransferOut);
        }

        //将迁出的列表分成 本区域内流动和流动到上级区域两份
        PopulationList populationListTransferOutside = populationListTransferThisArea.splitPopulationList(m_fTransferToParentRate);
        if (populationListTransferOut != null)
        {
            populationListTransferOut.mergePopulationList(populationListTransferOutside);
        }
        PopulationList populationListTransferInside = populationListTransferThisArea;

        //填写本区域真正的流出部分
        Collection<Population> pops = null;
        if (populationListTransferOut != null)
        {
            pops = populationListTransferOut.getPopulations();
        }
        if(pops != null)
        {
            m_fTransferPopNums[IAreaQuarantineStrategy.m_HealthIndex] = 0;
            m_fTransferPopNums[IAreaQuarantineStrategy.m_IncubationIndex] = 0;
            m_fTransferPopNums[IAreaQuarantineStrategy.m_OnsetIndex] = 0;
            m_fTransferPopNums[IAreaQuarantineStrategy.m_ImmuneIndex] = 0;

            for (Population pop: pops)
            {
                m_fTransferPopNums[IAreaQuarantineStrategy.m_HealthIndex] += pop.m_nPopulation;

                m_fTransferPopNums[IAreaQuarantineStrategy.m_IncubationIndex] += pop.getStageNums(IncubationStage.class);
                m_fTransferPopNums[IAreaQuarantineStrategy.m_OnsetIndex] += pop.getStageNums(OnsetStage.class);
                m_fTransferPopNums[IAreaQuarantineStrategy.m_ImmuneIndex] += pop.getStageNums(ImmuneStage.class);
            }
        }

        //将populationListTransferInside分配到各个子区域(参考各个子区域的m_fTransferPopNums)
        m_AreaQuarantineStrategy.doTransferInside(this, populationListTransferInside);

        return;
    }

    public void getAreaTransferBasePopulationNum(Float[] populationNums)
    {
        m_AreaQuarantineStrategy.getAreaTransferBasePopulationNum(this, populationNums);
    }

    public float getAreaRealTransferRate()
    {
        //TODO 还需要通过隔离策略来算出真正的迁移指数
        return m_fTransferRate;
    }

    public void logOut() {
        if (m_ChildAreaMap.size() != 0)
        {
            for (Area subArea:m_ChildAreaMap.values())
            {
                subArea.logOut();
            }
            return;
        }
        int nPopulationNum = 0;
        long nPopulationHealthNum = 0;
        int nPopulationIncubationNum = 0;
        int nPopulationOnsetNum = 0;
        int nPopulationIntensiveNum = 0;
        int nPopulationImmuneNum = 0;
        int nPopulationDeadNum = 0;
        for (Population pop : m_PopulationList.getPopulations())
        {
            nPopulationNum++;
            nPopulationHealthNum+=pop.m_nPopulation;
            nPopulationIncubationNum+=pop.getStageNums(IncubationStage.class);
            nPopulationOnsetNum+=pop.getStageNums(OnsetStage.class);
            nPopulationIntensiveNum+=pop.getStageNums(IntensiveStage.class);
            nPopulationImmuneNum+=pop.getStageNums(ImmuneStage.class);
            nPopulationDeadNum+=pop.getStageNums(DeadStage.class);
        }

        String strLog = String.format("%s PopNum=%d 健康=%d 潜伏=%d 轻症=%d 重症=%d 康复=%d 死亡=%d",
                m_strFullName, nPopulationNum,
                nPopulationHealthNum, nPopulationIncubationNum,
                nPopulationOnsetNum, nPopulationIntensiveNum,
                nPopulationImmuneNum, nPopulationDeadNum);
        Log.i("area", strLog);

    }
}
