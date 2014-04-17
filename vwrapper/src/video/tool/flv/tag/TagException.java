package video.tool.flv.tag;

public class TagException extends Exception
{
  private static final long serialVersionUID = -6731165057249248510L;
  public static final int ERROR = 0;
  public static final int WARNING = 1;
  private int type_ = 0;

  public boolean isWarning() {
    return this.type_ == 1;
  }

  public TagException()
  {
  }

  public TagException(int type, String s) {
    super(s);
    this.type_ = (type == 0 ? type : 1);
  }
}