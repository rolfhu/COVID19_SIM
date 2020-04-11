package sim.tags;

public interface ITagHost {

    //TagHost加入了某个标签
    void onAddOneTag(TagBase oneTag);

    //TagHost去掉了某个标签
    void onRemoveOneTag(TagBase oneTag);
}
