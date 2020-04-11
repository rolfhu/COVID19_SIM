package sim.substance;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import sim.tags.ITagHost;
import sim.tags.TagBase;
import sim.tags.Tags;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.tags.stage.Stage;

public class Population implements ITagHost {

    //健康人的数量（未被感染过的）
    public long m_nPopulation = 0;

    //标签存放的是所有叶子节点的标签，而不会包含一整条路径的父节点
    private Tags m_Tags = new Tags();

    //本人群中各种病程的人的集合
    private Collection<Patient> m_PatientSetIncubation = new HashSet<>();
    private Collection<Patient> m_PatientSetOnset = new HashSet<>();
    private Collection<Patient> m_PatientSetIntensive = new HashSet<>();
    private Collection<Patient> m_PatientSetImmune = new HashSet<>();
    private Collection<Patient> m_PatientSetDead = new HashSet<>();

    //ITagHost
    @Override
    public void onAddOneTag(TagBase oneTag)
    {
        oneTag.addPopulation(this);
    }

    @Override
    public void onRemoveOneTag(TagBase oneTag)
    {
        oneTag.removePopulation(this);
    }

    public Population(long nPopulation)
    {
        m_nPopulation = nPopulation;
        m_Tags.setTagHost(this);
    }

    public Population(Population other)
    {
        m_nPopulation = other.m_nPopulation;
        m_Tags = new Tags(other.m_Tags);
        m_Tags.setTagHost(this);
    }

    public boolean haveOneTag(TagBase oneTag)
    {
        if (m_Tags.findTagByFullName(oneTag.getTagFullName()) == null)
        {
            return false;
        }
        return true;
    }

    //将自己按照人数比例分割为指定的几份，以及切分后，每份的标签
    public ArrayList<Population> splitPopulations(Pair<Float, TagBase>[] splitArray)
    {
        //
        float fTotal = 0;
        for (Pair pairValue:splitArray)
        {
            fTotal += (Float)pairValue.first;
        }
        ArrayList<Population> resultArray = new ArrayList<>(splitArray.length);

        long nRestNum = m_nPopulation;
        for (Pair pairValue:splitArray)
        {
            float fValue = (Float)pairValue.first;
            if (nRestNum>=1)
            {
                long nNum = (long) (m_nPopulation*(fValue/fTotal));
                Population population = new Population(this);
                population.m_nPopulation = nNum;
                population.addTag((TagBase) pairValue.second);
                resultArray.add(population);
                nRestNum = nRestNum-nNum;
            }
            else
            {
                break;
            }
        }

        if (nRestNum != 0)
        {
            resultArray.get(resultArray.size()-1).m_nPopulation += nRestNum;
        }

        return resultArray;
    }

    public Patient createPatient()
    {
        if (m_nPopulation == 0)
        {
            return null;
        }
        Patient onePatient = new Patient();

        onePatient.Init(m_Tags);

        return onePatient;
    }

    public void addTag(TagBase oneTag)
    {
        m_Tags.addTag(oneTag);
    }

    //自己要被删除了，那么将m_Tags中的标签都删掉，以便去掉各个标签对自己的引用
    public void deleteMe()
    {
        m_Tags.deleteAllTags();
    }

    public Tags cloneTags()
    {
        Tags tags = new Tags(m_Tags);

        return tags;
    }

    //获取本人群的某个病程人数
    public long getStageNums(Class classType)
    {
        if(classType == IncubationStage.class)
        {
            return m_PatientSetIncubation.size();
        }
        else if(classType == OnsetStage.class)
        {
            return m_PatientSetOnset.size();
        }
        else if(classType == IntensiveStage.class)
        {
            return m_PatientSetIntensive.size();
        }
        else if(classType == ImmuneStage.class)
        {
            return m_PatientSetImmune.size();
        }
        else if(classType == DeadStage.class)
        {
            return m_PatientSetDead.size();
        }
        return 0;
    }

    public void addPatient(Patient onePatient)
    {
        Stage stage = onePatient.getStageTag();
        if(stage.getClass() == IncubationStage.class)
        {
            m_PatientSetIncubation.add(onePatient);
        }
        else if(stage.getClass() == OnsetStage.class)
        {
            m_PatientSetOnset.add(onePatient);
        }
        else if(stage.getClass() == ImmuneStage.class)
        {
            m_PatientSetImmune.add(onePatient);
        }
        else if(stage.getClass() == DeadStage.class)
        {
            m_PatientSetDead.add(onePatient);
        }
    }
}
