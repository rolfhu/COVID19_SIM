package sim.substance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import sim.tags.Tags;

public class PopulationList {

    private Collection<Population> m_Populations = new HashSet<>();

    public Collection<Population> getPopulations()
    {
        return m_Populations;
    }

    //对另一个popList不会有影响
    public void clonePopulationList(PopulationList popList)
    {
        for (Population onePop:popList.m_Populations)
        {
            Population newPop = new Population(onePop, true);
            m_Populations.add(newPop);
        }
    }

    //将一个人群合并进来，Population pop会被清空
    public void mergePopulation(Population pop)
    {
        Tags tagsPop = pop.cloneTags();
        for (Population onePop:m_Populations)
        {
            Tags tagsOnePop = onePop.cloneTags();

            if (tagsPop.equals(tagsOnePop))
            {
                onePop.mergePopulation(pop);
                return;
            }
        }
        m_Populations.add(pop);
    }

    //将一个人群列表合并进来，PopulationList popList会被清空
    public void mergePopulationList(PopulationList popList)
    {
        for (Population onePop:popList.m_Populations)
        {
            mergePopulation(onePop);
        }
    }

    //将一个人群合并进来，Population pop不会受影响
    public void mergePopulationNoClearSrc(Population pop)
    {
        Tags tagsPop = pop.cloneTags();
        for (Population onePop:m_Populations)
        {
            Tags tagsOnePop = onePop.cloneTags();

            if (tagsPop.equals(tagsOnePop))
            {
                onePop.mergePopulationNoClearSrc(pop);
                return;
            }
        }
        //没有找到同样标签的人群，那么要克隆一个人群出来加进去
        Population popnew = new Population(pop, true);
        m_Populations.add(popnew);
    }

    //将一个人群列表合并进来，PopulationList popList不会受影响
    public void mergePopulationListNoClearSrc(PopulationList popList)
    {
        for (Population onePop:popList.m_Populations)
        {
            mergePopulationNoClearSrc(onePop);
        }
    }

    public void removePopulation(Population pop)
    {
        m_Populations.remove(pop);
    }

    //分割一部分出来，避免不分割的元素做大量的操作
    public PopulationList splitPopulationList(Float splitRate)
    {
        PopulationList result = new PopulationList();

        for (Population onePop:m_Populations)
        {
            Population PopulationSplit = onePop.splitPopulations(splitRate);

            result.mergePopulation(PopulationSplit);
        }

        return result;
    }

    //根据分割系数，将本对象分割成多个人群列表，同时转移其中的病人归属的人群
    public ArrayList<PopulationList> splitPopulationList(Float[] splitArray)
    {
        ArrayList<PopulationList> resultArray = new ArrayList<>(splitArray.length);

        for (int i=0;i<splitArray.length;i++)
        {
            resultArray.add(new PopulationList());
        }

        for (Population onePop:m_Populations)
        {
            ArrayList<Population> PopulationSplitArray = (ArrayList<Population>) onePop.splitPopulations(splitArray, false);

            for (int i=0;i<splitArray.length;i++)
            {
                resultArray.get(i).mergePopulation(PopulationSplitArray.get(i));
            }
        }

        return resultArray;
    }

}
