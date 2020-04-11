package sim.tags.stage;

import sim.tags.TagUtility;

public class DeadStage extends Stage {
    public DeadStage() {
        super(getShortName());
    }

    public DeadStage(String strName) {
        super(strName);
    }

    static public String getShortName()
    {
        return new String("已死亡");
    }

    static public String getFullName()
    {
        return new String(Stage.getFullName()+ TagUtility.TAG_SEPARATOR+getShortName());
    }
}
