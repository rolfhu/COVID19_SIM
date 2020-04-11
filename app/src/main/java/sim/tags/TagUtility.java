package sim.tags;

public class TagUtility {

    final static public String TAG_SEPARATOR = ".";
    
    //根据标签的全名或半名来获取当前最前面的一级标签名
    static public String getTagFirstName(String strTagName)
    {
        int nPos = strTagName.indexOf(TAG_SEPARATOR);
        if (nPos == -1)
        {
            return strTagName;
        }

        return strTagName.substring(0, nPos);
    }

    //根据标签的全名或半名来获取当前除了最前面的其余标签名
    static public String getTagNextNames(String strTagName)
    {
        int nPos = strTagName.indexOf(TAG_SEPARATOR);
        if (nPos == -1)
        {
            return "";
        }

        return strTagName.substring(nPos+1, strTagName.length());
    }
}
