package sim.util;

public class StatisticsAverage {
    private String m_strName;
    private long m_lSummary = 0;
    private double m_dSummary = 0;
    private int m_nCount = 0;

    public StatisticsAverage(String strName)
    {
        m_strName = strName;
    }

    public void addIntData(int nData)
    {
        m_nCount++;
        m_lSummary += nData;
    }

    public void addFloatData(float fData)
    {
        m_nCount++;
        m_dSummary += fData;
    }

    private float getAverage()
    {
        float fAverage = 0;
        if(m_nCount != 0) {
            if (m_lSummary != 0) {
                fAverage = ((float) m_lSummary) / m_nCount;
            } else {
                fAverage = (float) (m_dSummary / m_nCount);
            }
        }
        return fAverage;
    }

    public String getResult()
    {

        String strResult = String.format("%s=%.5f", m_strName, getAverage());
        return strResult;
    }

    public String getResultByPercent()
    {
        String strResult = String.format("%s=%.2f%%", m_strName, getAverage()*100);
        return strResult;
    }
}
