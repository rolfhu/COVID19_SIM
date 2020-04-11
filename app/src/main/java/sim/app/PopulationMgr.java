package sim.app;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;

import sim.substance.Patient;
import sim.substance.Population;
import sim.tags.TagBase;
import sim.tags.Tags;
import sim.tags.stage.IncubationStage;

public class PopulationMgr {

    private Collection<Population> m_Populations = new ArrayList<>();

    private PopulationMgr() {}
    private static PopulationMgr s_single=null;
    public static PopulationMgr getInstance() {
        if (s_single == null) {
            s_single = new PopulationMgr();
        }
        return s_single;
    }

    public void Init()
    {
        Population total = new Population(300*10000);
        total.addTag(TagMgr.getInstance().findTagByFullName("湖北"));
        m_Populations.add(total);

        InitAreaTags();
    }

    private void InitAreaTags()
    {
        Pair<Float, TagBase> splitArray[] = new Pair[]{
                Pair.create(3.0f, TagMgr.getInstance().findTagByFullName("湖北.武汉")),
                Pair.create(2.0f, TagMgr.getInstance().findTagByFullName("湖北.黄冈")),
                Pair.create(1.0f, TagMgr.getInstance().findTagByFullName("湖北.孝感"))
        };
        ArrayList<Population> popsNeedAdd = new ArrayList<>();
        ArrayList<Population> popsNeedRemove = new ArrayList<>();

        for (Population pop:m_Populations)
        {
            //首先该Population需要有这些标签的父标签才能分割
            if(!pop.haveOneTag(splitArray[0].second.getParentTag()))
            {
                continue;
            }
            ArrayList<Population> pops = pop.splitPopulations(splitArray);
            popsNeedAdd.addAll(pops);
            popsNeedRemove.add(pop);
            deletePopulation(pop);
        }
        m_Populations.removeAll(popsNeedRemove);
        m_Populations.addAll(popsNeedAdd);
    }

    private void deletePopulation(Population pop)
    {
        pop.deleteMe();
    }

    //从所有人群中找出包含了某个标签的人群列表
    public Collection<Population> getPopulationsByTag(TagBase oneTag)
    {
        return getPopulationsByTag(m_Populations, oneTag);
    }

    //从给定的人群列表中找出包含了某个标签的人群列表
    public Collection<Population> getPopulationsByTag(Collection<Population> pops, TagBase oneTag)
    {
        Collection<Population> retArray = new ArrayList<>();
        for (Population pop : pops)
        {
            if(pop.haveOneTag(oneTag))
            {
                retArray.add(pop);
            }
        }
        return retArray;
    }

    //在给定的人群列表中找出一个人群来感染其中一个
    public Population findOnePopulationToInfect(Collection<Population> pops)
    {
        //目前简单使用第一个
        if(pops.size() == 0)
        {
            return null;
        }

        return pops.iterator().next();
    }

    //将给定的一个人群来感染其中一个人
    public Patient infectOnePopulation(Population pop)
    {
        if (pop.m_nPopulation == 0)
        {
            return null;
        }
        Tags tags = pop.cloneTags();
        TagBase stageTag = TagMgr.getInstance().findTagByFullName(IncubationStage.getFullName());
        tags.addTag(stageTag);
        Patient onePatient = new Patient();
        onePatient.Init(tags);

        pop.m_nPopulation--;
        pop.addPatient(onePatient);

        return onePatient;
    }

    //在给定的人群列表中找出一个人群并感染其中一个人
    public Patient infectOnePopulation(Collection<Population> pops)
    {
        Population pop = findOnePopulationToInfect(pops);
        if (pop == null)
        {
            return null;
        }
        Patient onePatient = infectOnePopulation(pop);

        return onePatient;
    }
}
