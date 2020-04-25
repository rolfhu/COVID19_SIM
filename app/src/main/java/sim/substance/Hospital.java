package sim.substance;

import android.util.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import sim.app.HospitalMgr;
import sim.tags.ITagHost;
import sim.tags.TagBase;
import sim.tags.Tags;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;

//医院，主要用于管理医生、病床，计算病人的变化
public class Hospital implements ITagHost {

    public String m_strHospitalName = "";

    private Tags m_Tags = new Tags();

    //可用的健康医生数量
    public long m_nDoctorNum = 0;

    private Collection<Bed> m_FreeBeds = new HashSet<>();
    private Collection<Bed> m_UsingBeds = new HashSet<>();

    //康复出院的人数和院内死亡的人数
    private long m_HealthNum = 0;
    private long m_DeadNum = 0;

    public void initHospital(int nBedNum, int nDoctorNum) {
        m_nDoctorNum = nDoctorNum;
        for (int i = 0; i < nBedNum; i++) {
            Bed oneBed = new Bed(this);
            m_FreeBeds.add(oneBed);
        }
        m_Tags.setTagHost(this);
    }

    public void addTag(TagBase oneTag) {
        m_Tags.addTag(oneTag);
    }

    @Override
    public void onAddOneTag(TagBase oneTag) {

    }

    @Override
    public void onRemoveOneTag(TagBase oneTag) {

    }

    public boolean isHospitalEmpty()
    {
        return m_UsingBeds.isEmpty();
    }

    public boolean isHospitalFull()
    {
        return m_FreeBeds.isEmpty();
    }

    public void addPatient(Patient patient)
    {
        if (m_FreeBeds.isEmpty())
        {
            return;
        }
        Iterator<Bed> iter = m_FreeBeds.iterator();
        Bed oneBed = iter.next();
        iter.remove();
        m_UsingBeds.add(oneBed);
        oneBed.m_Paitent = patient;
        patient.setHospital(this);
    }

    public boolean canReceivePatient(Patient patient)
    {
        if (isHospitalFull())
        {
            return false;
        }
        if(HospitalMgr.getInstance().m_IntoHospitalStrategy.checkPatient(this, patient))
        {
            return true;
        }
        return false;
    }

    public void removePatient(Patient patient)
    {
        for (Bed oneBed:m_UsingBeds)
        {
            if(oneBed.m_Paitent == patient)
            {
                m_FreeBeds.add(oneBed);
                m_UsingBeds.remove(oneBed);
                oneBed.m_Paitent = null;
                patient.setHospital(null);
                if(patient.getStageTag().getClass() == ImmuneStage.class)
                {
                    m_HealthNum++;
                }
                else if(patient.getStageTag().getClass() == DeadStage.class)
                {
                    m_DeadNum++;
                }
                break;
            }
        }
    }

    public void logOut()
    {
        String strLog = String.format("%s Using=%d Free=%d 康复=%d 死亡=%d",
                m_strHospitalName, m_UsingBeds.size(), m_FreeBeds.size(),
                m_HealthNum, m_DeadNum);
        Log.i("hospital", strLog);
    }
}
