package sim.strategy.quarantine;

import java.util.ArrayList;
import java.util.Collection;

import sim.area.Area;
import sim.strategy.StrategyBase;
import sim.substance.Population;
import sim.substance.PopulationList;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.OnsetStage;

public class AreaQuarantineStrategy extends StrategyBase implements IAreaQuarantineStrategy {
    public AreaQuarantineStrategy() {
        super("根据政策隔离");
    }

    @Override
    public void getAreaTransferBasePopulationNum(Area oneArea, Float[] populationNums)
    {
        Collection<Area> childAreas = oneArea.getChildAreas();
        if (!childAreas.isEmpty())
        {
            //有子区域，那么去子区域计算并累计
            for (Float fValue : populationNums)
            {
                fValue = 0f;
            }
            for (Area subArea : childAreas)
            {
                Float[] subAreaPopulationNums = new Float[]{0f,0f,0f,0f};
                subArea.getAreaTransferBasePopulationNum(subAreaPopulationNums);
                for (int i=0; i<populationNums.length; i++)
                {
                    populationNums[i] += subAreaPopulationNums[i];
                }
            }
        }
        else
        {
            //已经是最底层的区域了，那么遍历人群进行累计
            //将通过策略和政策来决定是否参与人群的流动，包括流出和流入
            long lHealthResult = 0;
            long lIncubationResult = 0;
            long lOnsetResult = 0;
            long lImmuneResult = 0;
            Collection<Population> areaPopulations = oneArea.getPopulations();
            for (Population onePopulation : areaPopulations)
            {
                lHealthResult += onePopulation.m_nPopulation;
                lIncubationResult += onePopulation.getStageNums(IncubationStage.class);
                lOnsetResult += onePopulation.getStageNums(OnsetStage.class);
                lImmuneResult += onePopulation.getStageNums(ImmuneStage.class);
            }
            float fTransferRate = oneArea.getAreaRealTransferRate();

            populationNums[m_HealthIndex] = lHealthResult*fTransferRate;
            populationNums[m_IncubationIndex] = lIncubationResult*fTransferRate;
            populationNums[m_OnsetIndex] = lOnsetResult*fTransferRate;
            populationNums[m_ImmuneIndex] = lImmuneResult*fTransferRate;
        }
    }

    @Override
    public void calcTransferOutInfo(Area oneArea)
    {
        Collection<Area> childAreas = oneArea.getChildAreas();
        if (childAreas.isEmpty())
        {
            //已经在getTransferOutPopulationListLeaf中计算过
            //calcTransferInfoLeaf(oneArea);
        }
        else
        {
            //累加所有子节点的各项人数即可
            float[] populationNums = oneArea.getTransferPopNums();
            populationNums[m_HealthIndex] = 0;
            populationNums[m_IncubationIndex] = 0;
            populationNums[m_OnsetIndex] = 0;
            populationNums[m_ImmuneIndex] = 0;
            for (Area subArea : childAreas)
            {
                float[] subAreaPopulationNums = subArea.getTransferPopNums();

                populationNums[m_HealthIndex] += subAreaPopulationNums[m_HealthIndex];
                populationNums[m_IncubationIndex] += subAreaPopulationNums[m_IncubationIndex];
                populationNums[m_OnsetIndex] += subAreaPopulationNums[m_OnsetIndex];
                populationNums[m_ImmuneIndex] += subAreaPopulationNums[m_ImmuneIndex];
            }

        }

    }

    @Override
    public void doTransferInside(Area oneArea, PopulationList populationList)
    {
        Collection<Area> childAreas = oneArea.getChildAreas();
        if (childAreas.isEmpty())
        {
            oneArea.getPopulationList().mergePopulationList(populationList);
            return;
        }

        Float[] fsplitArray = new Float[childAreas.size()];
        int nIndex = 0;
        //先计算各地区比例
        for (Area subArea : childAreas)
        {
            float[] populationNums = subArea.getTransferPopNums();
            fsplitArray[nIndex] = 0f;
            for(float fpop:populationNums)
            {
                fsplitArray[nIndex] += fpop;
            }
            nIndex++;
        }

        //根据比例分割人群
        ArrayList<PopulationList> splitResult = populationList.splitPopulationList(fsplitArray);

        //实行分配
        nIndex = 0;
        for (Area subArea : childAreas)
        {
            subArea.getQuarantineStrategy().doTransferInside(subArea, splitResult.get(nIndex));
            nIndex++;
        }

    }

    @Override
    public PopulationList getTransferOutPopulationList(Area oneArea)
    {
        PopulationList result = null;
        Collection<Area> childAreas = oneArea.getChildAreas();
        if (childAreas.isEmpty())
        {
            result = getTransferOutPopulationListLeaf(oneArea);
        }
        else
        {

        }
        return result;
    }

    //对叶子结点，根据输出的比例来收集各个人群的病人
    private PopulationList getTransferOutPopulationListLeaf(Area oneArea)
    {
        float fTransferRate = oneArea.getAreaRealTransferRate();

        PopulationList result = oneArea.getPopulationList().splitPopulationList(fTransferRate);

        float[] populationNums = oneArea.getTransferPopNums();
        populationNums[m_HealthIndex] = 0;
        populationNums[m_IncubationIndex] = 0;
        populationNums[m_OnsetIndex] = 0;
        populationNums[m_ImmuneIndex] = 0;

        for (Population pop :result.getPopulations())
        {
            populationNums[m_HealthIndex] += pop.m_nPopulation;
            populationNums[m_IncubationIndex] += pop.getStageNums(IncubationStage.class);
            populationNums[m_OnsetIndex] += pop.getStageNums(OnsetStage.class);
            populationNums[m_ImmuneIndex] += pop.getStageNums(ImmuneStage.class);
        }
        return  result;
    }


    private void calcTransferInfoLeaf(Area oneArea)
    {
        long lHealthResult = 0;
        long lIncubationResult = 0;
        long lOnsetResult = 0;
        long lImmuneResult = 0;
        Collection<Population> areaPopulations = oneArea.getPopulations();
        for (Population onePopulation : areaPopulations)
        {
            lHealthResult += onePopulation.m_nPopulation;
            lIncubationResult += onePopulation.getStageNums(IncubationStage.class);
            lOnsetResult += onePopulation.getStageNums(OnsetStage.class);
            lImmuneResult += onePopulation.getStageNums(ImmuneStage.class);
        }
        float fTransferRate = oneArea.getAreaRealTransferRate();

        float[] populationNums = oneArea.getTransferPopNums();


        populationNums[m_HealthIndex] = lHealthResult*fTransferRate;
        populationNums[m_IncubationIndex] = lIncubationResult*fTransferRate;
        populationNums[m_OnsetIndex] = lOnsetResult*fTransferRate;
        populationNums[m_ImmuneIndex] = lImmuneResult*fTransferRate;

    }
}
