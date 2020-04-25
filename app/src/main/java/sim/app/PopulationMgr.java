package sim.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import sim.substance.Patient;
import sim.substance.Population;
import sim.tags.TagBase;
import sim.tags.Tags;
import sim.tags.stage.IncubationStage;
import sim.worlds.FactoryMgr;

public class PopulationMgr {

    private Collection<Population> m_Populations = new HashSet<>();

    private PopulationMgr() {}
    private static PopulationMgr s_single=null;
    public static PopulationMgr getInstance() {
        if (s_single == null) {
            s_single = new PopulationMgr();
        }
        return s_single;
    }

    public void init()
    {
        FactoryMgr.getInstance().getFactory().initPopulations(m_Populations);
    }

    public Population getTheOnlyPopulation()
    {
        if (m_Populations.size() == 1)
        {
            return m_Populations.iterator().next();
        }
        return null;
    }

    public void addPopulation(Population pop)
    {
        m_Populations.add(pop);
    }

    public void deletePopulation(Population pop)
    {
        m_Populations.remove(pop);
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

    //将各个人群中的病人送入医院
    public void gotoHospital()
    {
        for (Population pop : m_Populations)
        {
            pop.gotoHospital();
        }
    }
}
