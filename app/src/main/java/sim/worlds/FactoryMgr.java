package sim.worlds;

public class FactoryMgr {

    private FactoryMgr() {}
    private static FactoryMgr s_single=null;
    public static FactoryMgr getInstance() {
        if (s_single == null) {
            s_single = new FactoryMgr();
        }
        return s_single;
    }

    private FactoryBase m_factoryBase = null;

    public void createFactory(String strFactoryName)
    {
        FactoryBase factory = null;

        if (strFactoryName.compareToIgnoreCase("China") == 0)
        {
            factory = new FactoryChina();
        }

        m_factoryBase = factory;
    }

    public FactoryBase getFactory()
    {
        return m_factoryBase;
    }

}
