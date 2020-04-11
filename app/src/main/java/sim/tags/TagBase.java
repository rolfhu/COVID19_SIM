package sim.tags;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import sim.substance.Population;

//标签名有多个层级，本标签的全名是"父标签的全名.本标签的名字"
//本标签的名字是m_TagName
public class TagBase {
    private String m_TagShortName = "";
    private String m_TagFullName = "";

    private TagBase m_ParentTag = null;

    //Key是标签的ShortName
    private Map<String, TagBase> m_SubTags = new HashMap<>();

    //所有含有本标签作为叶子结点的Population集合
    private Set<Population> m_PopulationsSet = new HashSet<>();

    public TagBase()
    {
    }

    public TagBase(String strName)
    {
        setTagShortName(strName);
    }

    public void setTagShortName(String strName)
    {
        m_TagShortName = strName;
        if (m_ParentTag == null)
        {
            m_TagFullName = getTagShortName();
        }
        else
        {
            m_TagFullName = m_ParentTag.getTagFullName() + TagUtility.TAG_SEPARATOR + getTagShortName();
        }
    }

    public String getTagShortName()
    {
        return m_TagShortName;
    }

    public String getTagFullName()
    {
        return m_TagFullName;
    }

    public void setParentTag(TagBase ParentTag)
    {
        m_ParentTag = ParentTag;
        m_TagFullName = m_ParentTag.getTagFullName()+TagUtility.TAG_SEPARATOR+getTagShortName();
    }

    public TagBase getParentTag()
    {
        return m_ParentTag;
    }

    public void addSubTag(TagBase SubTag)
    {
        SubTag.setParentTag(this);
        m_SubTags.put(SubTag.getTagShortName(), SubTag);
    }

    //根据本级以下的标签名来寻找，而非全名
    public TagBase findTagByHalfName(String strHalfName)
    {
        String strTagName = TagUtility.getTagFirstName(strHalfName);

        if (m_SubTags.containsKey(strTagName))
        {
            TagBase subTag = m_SubTags.get(strTagName);
            String strNextNames = TagUtility.getTagNextNames(strHalfName);
            if (strNextNames.isEmpty())
            {
                return subTag;
            }
            else
            {
                return subTag.findTagByHalfName(strNextNames);
            }
        }
        else
        {
            return null;
        }
    }

    //当一个人群新增了本标签时，要将该人群加入本标签的人群集合
    public void addPopulation(Population pop)
    {
        m_PopulationsSet.add(pop);
    }

    //当一个人群根据本标签分裂后，要将该人群从本标签的人群集合中移除
    public void removePopulation(Population pop)
    {
        m_PopulationsSet.remove(pop);
    }

    //当一个人群根据其他标签分裂后，要将该人群从该标签的人群集合中移除，同时将分裂成的几个人群加入到本标签的人群集合中来
    public void replacePopulation(Population popOld, ArrayList<Population> popsNew)
    {
        m_PopulationsSet.remove(popOld);
        m_PopulationsSet.addAll(popsNew);
    }

    //获取本标签下所有人群的健康人数总和
    public long getAllPopulationHealthyNums()
    {
        long lResult = 0;

        for (Population onePop :m_PopulationsSet)
        {
            lResult += onePop.m_nPopulation;
        }

        return lResult;
    }

    //获取本标签下所有人群的各类病程人数总和
    public long getAllPopulationNumsByStage(Class classType)
    {
        long lResult = 0;

        for (Population onePop :m_PopulationsSet)
        {
            lResult += onePop.getStageNums(classType);
        }

        return lResult;
    }
}
