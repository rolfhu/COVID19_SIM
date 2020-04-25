package sim.worlds.china;


import java.util.Collection;

import sim.substance.Population;

public class PopulationInit {
    public static void initPopulations(Collection<Population> populations) {

        Population total = new Population(300*10000);
        //total.addTag(TagMgr.getInstance().findTagByFullName("湖北"));
        populations.add(total);

        //InitAreaTags(populations);
    }

    private static void InitAreaTags(Collection<Population> populations)
    {
/*        Pair<Float, TagBase> splitArray[] = new Pair[]{
                Pair.create(3.0f, TagMgr.getInstance().findTagByFullName("湖北.武汉")),
                Pair.create(2.0f, TagMgr.getInstance().findTagByFullName("湖北.黄冈")),
                Pair.create(1.0f, TagMgr.getInstance().findTagByFullName("湖北.孝感"))
        };
        Collection<Population> popsNeedAdd = new ArrayList<>();
        Collection<Population> popsNeedRemove = new ArrayList<>();

        for (Population pop:populations)
        {
            //首先该Population需要有这些标签的父标签才能分割
            if(!pop.haveOneTag(splitArray[0].second.getParentTag()))
            {
                continue;
            }
            Collection<Population> pops = pop.splitPopulations(splitArray);
            popsNeedAdd.addAll(pops);
            popsNeedRemove.add(pop);
            pop.deleteMe();
        }
        populations.removeAll(popsNeedRemove);
        populations.addAll(popsNeedAdd);*/
    }
}
