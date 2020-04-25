package sim.area;

import android.util.Pair;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import sim.app.AreaMgr;
import sim.app.PopulationMgr;
import sim.substance.AreaHospital;
import sim.substance.Population;

public class Area {
    final static public String AREA_SEPARATOR = ".";

    private String m_strShortName = "";
    private String m_strFullName = "";

    private AreaHospital m_AreaHospital = null;

    private Area m_ParentArea = null;

    private Map<String, Area> m_ChildAreaMap = new HashMap<>();

    //直属于本区域的人群
    private Collection<Population> m_Populations = new HashSet<>();

    public Area(String strAreaName)
    {
        setAreaShortName(strAreaName);
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

    //
    public Area findAreaByHalfName(String strHalfName)
    {
        String strAreaName = AreaMgr.getAreaFirstName(strHalfName);
        if (strAreaName.equals(m_strShortName))
        {
            String strAreaNextNames = AreaMgr.getAreaNextNames(strHalfName);
            if (strAreaNextNames.isEmpty())
            {
                return this;
            }
            String strAreaNextName = AreaMgr.getAreaFirstName(strAreaNextNames);

            Area subArea = getChildArea(strAreaNextName);

            if (subArea != null)
            {
                return subArea.findAreaByHalfName(strAreaNextNames);
            }
        }
        return null;
    }

    public AreaHospital getAreaHospital()
    {
        return m_AreaHospital;
    }

    public void setAreaHospital(AreaHospital areaHospital)
    {
        this.m_AreaHospital = areaHospital;
    }

    public void addPopulation(Population pop)
    {
        m_Populations.add(pop);
        pop.setArea(this);
    }

    public void removePopulation(Population pop)
    {
        m_Populations.remove(pop);
    }

    //将自己按照人数比例分割为指定的几个区域，以及切分后，每份的区域短名
    public void splitArea(Pair<Float, String>[] splitArray)
    {
        //创建子区域
        Float[] fArray = new Float[splitArray.length];
        int nIndex = 0;
        for (Pair<Float, String> areaPair: splitArray)
        {
            addChildArea(new sim.area.Area(areaPair.second));
            fArray[nIndex] = areaPair.first;
            nIndex++;
        }

        //对每个人群均进行切分
        for (Population pop: m_Populations)
        {
            Collection<Population> resultArray = pop.splitPopulations(fArray);

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

        for (Population onePop :m_Populations)
        {
            lResult += onePop.m_nPopulation;
        }

        return lResult;
    }

    //获取本区域下所有人群的各类病程人数总和
    public long getAllPopulationNumsByStage(Class classType)
    {
        long lResult = 0;

        for (Population onePop :m_Populations)
        {
            lResult += onePop.getStageNums(classType);
        }

        return lResult;
    }

    public Collection<Population> getPopulations() {
        return m_Populations;
    }
}
