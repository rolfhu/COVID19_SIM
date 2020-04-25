package sim.worlds.china;

import android.util.Pair;

import sim.app.PopulationMgr;
import sim.substance.Population;
import sim.area.Area;

public class AreaInit {

    public static void initArea(Area rootArea)
    {
        Population totalPop = PopulationMgr.getInstance().getTheOnlyPopulation();
        rootArea.setAreaShortName("湖北");
        rootArea.addPopulation(totalPop);

        sim.area.Area hubei = rootArea;

        Pair<Float, String> splitArray[] = new Pair[]{
                Pair.create(3.0f, "武汉"),
                Pair.create(2.0f, "黄冈"),
                Pair.create(1.0f, "孝感")
        };
        hubei.splitArea(splitArray);
    }
}
