package sim.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import sim.tags.TagBase;
import sim.tags.TagUtility;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.tags.stage.Stage;
import sim.worlds.FactoryMgr;

//管理所有Tag之间的关系
public class TagMgr {

    //Key为标签的ShortName，但因为是存放第一层的标签，所以也算是FullName
    private Map<String, TagBase> m_TagsTree = new HashMap<>();

    private TagMgr() {}
    private static TagMgr s_single=null;
    public static TagMgr getInstance() {
        if (s_single == null) {
            s_single = new TagMgr();
        }
        return s_single;
    }

    public void init()
    {
        initStageTags();
    }

    private void initStageTags()
    {
        TagBase rootStage = new Stage();
        m_TagsTree.put(rootStage.getTagFullName(), rootStage);

        TagBase SubStage = new IncubationStage();
        rootStage.addSubTag(SubStage);

        SubStage = new OnsetStage();
        rootStage.addSubTag(SubStage);

        SubStage = new IntensiveStage();
        rootStage.addSubTag(SubStage);

        SubStage = new DeadStage();
        rootStage.addSubTag(SubStage);

        SubStage = new ImmuneStage();
        rootStage.addSubTag(SubStage);
    }

    public TagBase findTagByFullName(String strFullName)
    {
        String strTagName = TagUtility.getTagFirstName(strFullName);

        if (m_TagsTree.containsKey(strTagName))
        {
            TagBase subTag = m_TagsTree.get(strTagName);
            String strNextNames = TagUtility.getTagNextNames(strFullName);
            if (strNextNames.isEmpty())
            {
                return subTag;
            }
            else
            {
                return subTag.findTagByHalfName(strNextNames);
            }
        }
        else
        {
            return null;
        }
    }

    //只要是从这个类型派生的都找出来
    public Collection<TagBase> findTagsByBaseClass(Class classType)
    {
        Collection<TagBase> resultTagsList = new ArrayList<>();

        for (Map.Entry<String, TagBase> entry : m_TagsTree.entrySet())
        {
            resultTagsList.addAll(entry.getValue().findTagsByBaseClass(classType));
        }

        return resultTagsList;
    }

}
