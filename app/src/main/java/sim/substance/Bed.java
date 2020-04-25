package sim.substance;

//病床
public class Bed {

    //床位所在的医院
    public Hospital m_Hospital = null;

    public Patient m_Paitent = null;

    public Bed(Hospital hospital)
    {
        m_Hospital = hospital;
    }
}
