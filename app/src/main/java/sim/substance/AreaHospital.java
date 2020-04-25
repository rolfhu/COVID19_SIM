package sim.substance;

import java.util.Collection;
import java.util.HashSet;

import sim.tags.ITagHost;
import sim.tags.TagBase;
import sim.tags.Tags;

//是一个抽象的医院，包括本区域的子医院
public class AreaHospital implements ITagHost {

    private Tags m_Tags = new Tags();

    private Collection<Hospital> m_HospitalList = new HashSet<>();

    public void addTag(TagBase oneTag)
    {
        m_Tags.addTag(oneTag);
    }

    @Override
    public void onAddOneTag(TagBase oneTag) {

    }

    @Override
    public void onRemoveOneTag(TagBase oneTag) {

    }

    public void addHospital(Hospital hospital)
    {
        m_HospitalList.add(hospital);
    }

    public void addPatient(Patient patient)
    {
        Hospital hospitalFound = findHospitalForPatient(patient);

        if (hospitalFound == null)
        {
            return;
        }

        hospitalFound.addPatient(patient);
    }

    //需要根据具体的策略来选择是否入院
    private Hospital findHospitalForPatient(Patient patient)
    {
        Hospital hospitalFound = null;

        for(Hospital oneHospital:m_HospitalList)
        {
            if (oneHospital.canReceivePatient(patient))
            {
                return oneHospital;
            }

        }

        return hospitalFound;
    }
}
