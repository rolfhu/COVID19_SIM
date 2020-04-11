package sim.app;

import java.util.HashMap;
import java.util.Map;

import sim.tags.TagBase;
import sim.tags.TagUtility;
import sim.tags.Tags;
import sim.tags.area.Area;
import sim.tags.stage.DeadStage;
import sim.tags.stage.ImmuneStage;
import sim.tags.stage.IncubationStage;
import sim.tags.stage.IntensiveStage;
import sim.tags.stage.OnsetStage;
import sim.tags.stage.Stage;

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

    public void Init()
    {
        InitAreaTags();
        InitStageTags();
    }

    public void InitAreaTags()
    {
        TagBase rootArea = new Area("湖北");
        m_TagsTree.put(rootArea.getTagFullName(), rootArea);

        TagBase SubArea = new Area("武汉");
        rootArea.addSubTag(SubArea);

        SubArea = new Area("黄冈");
        rootArea.addSubTag(SubArea);

        SubArea = new Area("孝感");
        rootArea.addSubTag(SubArea);
    }

    private void InitStageTags()
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


}
