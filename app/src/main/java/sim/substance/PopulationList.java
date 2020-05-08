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

    //将一个人群合并进来
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

    //将一个人群列表合并进来
    public void mergePopulationList(PopulationList popList)
    {
        for (Population onePop:popList.m_Populations)
        {
            mergePopulation(onePop);
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
