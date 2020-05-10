package sim.app;

import sim.area.Area;
import sim.worlds.FactoryMgr;

public class AreaMgr {

    //根据区域的全名或半名来获取当前最前面的一级标签名
    static public String getAreaFirstName(String strTagName)
    {
        int nPos = strTagName.indexOf(Area.AREA_SEPARATOR);
        if (nPos == -1)
        {
            return strTagName;
        }

        return strTagName.substring(0, nPos);
    }

    //根据区域的全名或半名来获取当前除了最前面的其余标签名
    static public String getAreaNextNames(String strTagName)
    {
        int nPos = strTagName.indexOf(Area.AREA_SEPARATOR);
        if (nPos == -1)
        {
            return "";
        }

        return strTagName.substring(nPos+1, strTagName.length());
    }

    public Area m_RootArea = null;

    private AreaMgr() {}
    private static AreaMgr s_single=null;
    public static AreaMgr getInstance() {
        if (s_single == null) {
            s_single = new AreaMgr();
        }
        return s_single;
    }

    public void init()
    {
        m_RootArea = new Area("");
        FactoryMgr.getInstance().getFactory().initArea(m_RootArea);
    }

    public Area findAreaByFullName(String strFullName)
    {
        String strAreaName = AreaMgr.getAreaFirstName(strFullName);
        if (strAreaName.equals(m_RootArea.getAreaShortName()))
        {
            if (strAreaName.equals(strFullName))
            {
                return m_RootArea;
            }
            else
            {
                String strAreaNextNames = AreaMgr.getAreaNextNames(strFullName);

                return m_RootArea.findAreaByHalfName(strAreaNextNames);
            }
        }
        return null;
    }

    public void logOut()
    {
        m_RootArea.logOut();
    }
}
