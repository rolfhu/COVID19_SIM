package sim.tags.stage;

import sim.substance.Patient;
import sim.tags.TagUtility;

//潜伏期，无传染力
public class IncubationStage extends Stage {
    public IncubationStage() {
        super(getShortName());
    }
    public IncubationStage(String strName) {
        super(strName);
    }

    static public String getShortName()
    {
        return new String("潜伏期");
    }

    static public String getFullName()
    {
        return new String(Stage.getFullName()+ TagUtility.TAG_SEPARATOR+getShortName());
    }

    @Override
    public boolean calcStage(Patient onePatient)
    {
        onePatient.m_IncubationDays++;
        if (onePatient.m_fVirusDensity >= onePatient.m_fVirusDensityToOnsetStage)
        {
            onePatient.changeToNewStage(OnsetStage.getFullName());
            return true;
        }

        return false;
    }

}
