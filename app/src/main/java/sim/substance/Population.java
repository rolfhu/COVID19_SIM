package sim.substance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import sim.area.Area;
import sim.tags.ITagHost;
import sim.tags.TagBase;
import sim.tags.Tags;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.tags.stage.Stage;
import sim.util.Tools;

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

    private Area m_Area = null;

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
        //m_Area = other.m_Area;
        m_Tags = new Tags(other.m_Tags);
        m_Tags.setTagHost(this);

        copyPatientList(m_PatientSetIncubation, other.m_PatientSetIncubation);
        copyPatientList(m_PatientSetOnset, other.m_PatientSetOnset);
        copyPatientList(m_PatientSetIntensive, other.m_PatientSetIntensive);
        copyPatientList(m_PatientSetImmune, other.m_PatientSetImmune);
        copyPatientList(m_PatientSetDead, other.m_PatientSetDead);
    }

    public boolean haveOneTag(TagBase oneTag)
    {
        if (m_Tags.findTagByFullName(oneTag.getTagFullName()) == null)
        {
            return false;
        }
        return true;
    }

    public AreaHospital getAreaHospital()
    {
        if(m_Area == null)
        {
            return null;
        }

        return m_Area.getAreaHospital();
    }

    public void setArea(Area area)
    {
        m_Area = area;
    }

    public Area getArea()
    {
        return m_Area;
    }


    //将另一个与自己标签相同的人群合并进来，Population other会被清空
    public void mergePopulation(Population other)
    {
        m_nPopulation += other.m_nPopulation;
        other.m_nPopulation = 0;

        gainPatientOwner(other.m_PatientSetIncubation);
        gainPatientOwner(other.m_PatientSetOnset);
        gainPatientOwner(other.m_PatientSetIntensive);
        gainPatientOwner(other.m_PatientSetImmune);
        gainPatientOwner(other.m_PatientSetDead);

        m_PatientSetIncubation.addAll(other.m_PatientSetIncubation);
        m_PatientSetOnset.addAll(other.m_PatientSetOnset);
        m_PatientSetIntensive.addAll(other.m_PatientSetIntensive);
        m_PatientSetImmune.addAll(other.m_PatientSetImmune);
        m_PatientSetDead.addAll(other.m_PatientSetDead);

        other.m_PatientSetIncubation.clear();
        other.m_PatientSetOnset.clear();
        other.m_PatientSetIntensive.clear();
        other.m_PatientSetImmune.clear();
        other.m_PatientSetDead.clear();
    }

    private void gainPatientOwner(Collection<Patient> PatientSet)
    {
        for (Patient onePatient:PatientSet)
        {
            onePatient.setPopulation(this);
        }
    }

    //将另一个与自己标签相同的人群合并进来，不影响Population other
    public void mergePopulationNoClearSrc(Population other)
    {
        m_nPopulation += other.m_nPopulation;

        m_PatientSetIncubation.addAll(other.m_PatientSetIncubation);
        m_PatientSetOnset.addAll(other.m_PatientSetOnset);
        m_PatientSetIntensive.addAll(other.m_PatientSetIntensive);
        m_PatientSetImmune.addAll(other.m_PatientSetImmune);
        m_PatientSetDead.addAll(other.m_PatientSetDead);
    }

    private void copyPatientList(Collection<Patient> dstPatientSet, Collection<Patient> srcPatientSet)
    {
        dstPatientSet.addAll(srcPatientSet);
    }

    //将自己按照人数比例分割为指定的几份，同时转移其中的病人归属的人群
    public ArrayList<Population> splitPopulations(Float[] splitArray, boolean bSplitAll)
    {
        //计算总的比例份数
        float fTotal = 0;
        for (Float fValue:splitArray)
        {
            fTotal += fValue;
        }
        ArrayList<Population> resultArray = new ArrayList<>(splitArray.length);

        long nRestNum = m_nPopulation;
        for (Float fValue:splitArray)
        {
            Population population = new Population(this);
            resultArray.add(population);
            if (nRestNum>=1)
            {
                long nNum = (long) (m_nPopulation*(fValue/fTotal));
                population.m_nPopulation = nNum;
                nRestNum = nRestNum-nNum;
            }
        }

        splitPatientSet(m_PatientSetIncubation, resultArray, splitArray);
        splitPatientSet(m_PatientSetOnset, resultArray, splitArray);
        splitPatientSet(m_PatientSetImmune, resultArray, splitArray);
        if(bSplitAll)
        {
            splitPatientSet(m_PatientSetIntensive, resultArray, splitArray);
            splitPatientSet(m_PatientSetDead, resultArray, splitArray);
        }

        if (nRestNum != 0)
        {
            resultArray.get(resultArray.size()-1).m_nPopulation += nRestNum;
        }

        return resultArray;
    }

    private void splitPatientSet(Collection<Patient> patientSetRaw,
                                 ArrayList<Population> resultArray,
                                 Float[] splitArray)
    {
        ArrayList<Collection<Object>> resultPatientArray = Tools.splitCollection(splitArray, (Collection<Object>)(Object)patientSetRaw, true);

        for (int nIndex = 0; nIndex<resultPatientArray.size(); nIndex++)
        {
            Collection<Patient> splitPatientResult = (Collection<Patient>)(Object)resultPatientArray.get(nIndex);
            Population population = resultArray.get(nIndex);

            for (Patient onePat:splitPatientResult)
            {
                population.addPatient(onePat);
            }
        }
    }

    private void splitPatientSet(Collection<Patient> patientSetRaw,
                                 Population targetPopulation,
                                 Float splitRate)
    {
        Collection<Patient> splitPatientResult = (Collection<Patient>)(Object)Tools.splitCollection(splitRate, (Collection<Object>)(Object)patientSetRaw, true);

        for (Patient onePat:splitPatientResult)
        {
            targetPopulation.addPatient(onePat);
        }
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
        onePatient.setPopulation(this);
        Stage stage = onePatient.getStageTag();
        if(stage.getClass() == IncubationStage.class)
        {
            m_PatientSetIncubation.add(onePatient);
        }
        else if(stage.getClass() == OnsetStage.class)
        {
            m_PatientSetOnset.add(onePatient);
        }
        else if(stage.getClass() == IntensiveStage.class)
        {
            m_PatientSetIntensive.add(onePatient);
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

    public void removePatient(Patient onePatient, TagBase oneTag)
    {
        Stage stage = (Stage) oneTag;
        if(stage.getClass() == IncubationStage.class)
        {
            m_PatientSetIncubation.remove(onePatient);
        }
        else if(stage.getClass() == OnsetStage.class)
        {
            m_PatientSetOnset.remove(onePatient);
        }
        else if(stage.getClass() == IntensiveStage.class)
        {
            m_PatientSetIntensive.remove(onePatient);
        }
        else if(stage.getClass() == ImmuneStage.class)
        {
            m_PatientSetImmune.remove(onePatient);
        }
        else if(stage.getClass() == DeadStage.class)
        {
            m_PatientSetDead.remove(onePatient);
        }
    }

    public void gotoHospital()
    {
        //先收重症入院
        gotoHospital(m_PatientSetIntensive);
        gotoHospital(m_PatientSetOnset);
    }

    private void gotoHospital(Collection<Patient> patientList)
    {
        Iterator it = patientList.iterator();
        while(it.hasNext())
        {
            Patient onePatient = (Patient) it.next();
            if (onePatient.getHospital() != null)
                continue;
            onePatient.gotoHospital(getAreaHospital());
        }
    }

    public Population splitPopulations(Float splitRate)
    {
        Population population = new Population(this);
        long nNum = (long) (m_nPopulation*splitRate);
        population.m_nPopulation = nNum;
        m_nPopulation = m_nPopulation-nNum;

        splitPatientSet(m_PatientSetIncubation, population, splitRate);
        splitPatientSet(m_PatientSetOnset, population, splitRate);
        splitPatientSet(m_PatientSetImmune, population, splitRate);

        return population;
    }
}
