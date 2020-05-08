package sim.worlds.china;

import android.util.Pair;

import sim.app.PopulationMgr;
import sim.substance.Population;
import sim.area.Area;

public class AreaInit {

    public static void initArea(Area rootArea)
    {
        Population totalPop = PopulationMgr.getInstance().getTheOnlyPopulation();
        rootArea.setAreaShortName("中国");
        rootArea.addPopulation(totalPop);

        Area china = rootArea;
        china.setSpace(960*10000);
        //china.setTransferRate(0.01f);

        Pair<Float, String> splitArray1[] = new Pair[]{
                Pair.create(6000f, "湖北"),
                Pair.create((14*10000-6000f), "除湖北外的诸省")
        };
        china.splitArea(splitArray1);

        Area hubei = china.findAreaByHalfName("湖北");
        Area except_hubei = china.findAreaByHalfName("除湖北外的诸省");
        hubei.setSpace((long) (18.59*10000));
        hubei.setTransferRate(0.01f);
        hubei.setTransferToParentRate(0.3f);
        except_hubei.setSpace((long) (960*10000-18.59*10000));
        except_hubei.setTransferRate(0.01f);

        Pair<Float, String> splitArray2[] = new Pair[]{
                Pair.create(1500f, "武汉"),
                Pair.create(4500f, "除武汉外的诸市")
        };
        hubei.splitArea(splitArray2);

        Area wuhan = china.findAreaByHalfName("湖北.武汉");
        wuhan.setSpace(8494);
        wuhan.setTransferRate(0.01f);
        Area except_wuhan = china.findAreaByHalfName("湖北.除武汉外的诸市");
        except_wuhan.setSpace((long) (18.59*10000-8494));
        except_wuhan.setTransferRate(0.01f);
    }
}
