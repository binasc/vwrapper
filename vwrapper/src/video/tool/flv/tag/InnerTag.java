package video.tool.flv.tag;

public abstract class InnerTag
{
  private Tag parent_;

  public void setParent(Tag tag)
  {
    this.parent_ = tag;
  }

  public Tag getParent() {
    return this.parent_;
  }

  public abstract int size();
}