package sim.tags.stage;

import sim.substance.Patient;
import sim.tags.TagUtility;

public class ImmuneStage extends Stage {
    public ImmuneStage() {
        super(getShortName());
    }
    public ImmuneStage(String strName) {
        super(strName);
    }

    static public String getShortName()
    {
        return new String("已免疫");
    }

    static public String getFullName()
    {
        return new String(Stage.getFullName()+ TagUtility.TAG_SEPARATOR+getShortName());
    }

    @Override
    public int calcInfection(Patient onePatient)
    {
        return 0;
    }

}
