package sim.strategy;

public abstract class StrategyBase {

    private String m_strStrategyName = "";

    public StrategyBase(String strStrategyName)
    {
        m_strStrategyName = strStrategyName;
    }

    public String getStrategyName()
    {
        return m_strStrategyName;
    }
}
