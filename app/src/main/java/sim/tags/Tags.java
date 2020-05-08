package sim.tags;


import java.util.HashMap;
import java.util.Map;

//用于存放一个群体的所有标签，只有最终的叶子结点
public class Tags {

    //Key为标签的FullName
    private Map<String, TagBase> m_SubTags = new HashMap<>();

    private ITagHost m_tagHost = null;

    public Tags()
    {

    }

    public Tags(Tags other)
    {
        m_SubTags = new HashMap<>();
        m_SubTags.putAll(other.m_SubTags);
        if (m_tagHost != null)
        {
            for (Map.Entry<String, TagBase> entry : m_SubTags.entrySet())
            {
                m_tagHost.onAddOneTag(entry.getValue());
            }
        }
    }

    public void setTagHost(ITagHost tagHost)
    {
        m_tagHost = tagHost;
        for (Map.Entry<String, TagBase> entry : m_SubTags.entrySet())
        {
            m_tagHost.onAddOneTag(entry.getValue());
        }
    }

    //在列表上增加一个标签，若该标签的父节点已经在列表中了，那么删除之
    public void addTag(TagBase oneTag)
    {
        if (oneTag == null)
        {
            return;
        }
        deleteTag(oneTag.getParentTag());
        m_SubTags.put(oneTag.getTagFullName(), oneTag);
        if (m_tagHost != null)
        {
            m_tagHost.onAddOneTag(oneTag);
        }
    }

    //删除一个标签，若没找到该标签，那么继续找父标签，直到到达根节点为止
    private void deleteTag(TagBase oneTag)
    {
        if(oneTag == null)
        {
            return;
        }
        if (m_SubTags.containsValue(oneTag))
        {
            deleteOneTag(oneTag);
        }
        else
        {
            deleteTag(oneTag.getParentTag());
        }
    }

    //删除一个确定存在的标签
    public void deleteOneTag(TagBase oneTag)
    {
        m_SubTags.remove(oneTag.getTagFullName());
        if (m_tagHost != null)
        {
            m_tagHost.onRemoveOneTag(oneTag);
        }
    }

    //删除所有标签
    public void deleteAllTags()
    {
        for (Map.Entry<String, TagBase> entry : m_SubTags.entrySet())
        {
            m_tagHost.onRemoveOneTag(entry.getValue());
        }
        m_SubTags.clear();
    }

    //从FullName查找有没有某个标签
    public TagBase findTagByFullName(String strTagFullName)
    {
        if (m_SubTags.containsKey(strTagFullName))
        {
            return m_SubTags.get(strTagFullName);
        }
        return null;
    }

    //根据类来查找有没有某个标签，只要找到从该类派生的标签即可
    public TagBase findTagByBaseClass(Class classType)
    {
        for (Map.Entry<String, TagBase> entry : m_SubTags.entrySet())
        {
            Class c = entry.getValue().getClass();
            if (classType.isAssignableFrom(c))
            {
                return entry.getValue();
            }
        }

        return null;
    }

    public boolean equals(Tags other)
    {
        if (this == other)
        {
            return true;
        }

        if (other.m_SubTags.size() != m_SubTags.size())
        {
            return false;
        }
        for (Map.Entry<String, TagBase> entry : m_SubTags.entrySet())
        {
            String strTagName = entry.getKey();
            TagBase tagValue = entry.getValue();

            TagBase tagValueOther = other.findTagByFullName(strTagName);
            if (tagValue != tagValueOther)
            {
                return false;
            }
        }
        return true;
    }
}
