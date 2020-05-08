package sim.strategy.quarantine;

import java.util.Collection;

import sim.area.Area;
import sim.substance.Patient;
import sim.substance.PopulationList;

//每个区域的隔离策略
public interface IAreaQuarantineStrategy {

    final public int m_PopTypeNum = 4;
    final public int m_HealthIndex = 0;
    final public int m_IncubationIndex = 1;
    final public int m_OnsetIndex = 2;
    final public int m_ImmuneIndex = 3;

    //将废弃
    void getAreaTransferBasePopulationNum(Area oneArea, Float[] PopulationNums);

    //计算这个区域各个阶段的迁出人数
    void calcTransferOutInfo(Area oneArea);

    //对这个区域的子区域报上来的人群中内部迁移的部分，分配到本区域内的各个子区域中
    void doTransferInside(Area oneArea, PopulationList populationList);

    //计算这个区域的迁出病人列表 通过PopulationList的方式输出结果
    PopulationList getTransferOutPopulationList(Area oneArea);
}
