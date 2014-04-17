package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;

public class MediaDataBox extends Box
{
  private long length_;

  public String toString()
  {
    return super.toString() + " media data: 0x" + Long.toHexString(this.length_);
  }

  protected void parseBox(InputStream in) throws BoxException
  {
    this.length_ = (getSize() - super.getBoxSize());
    try {
      long ret = in.skip(this.length_);
      if (ret != getSize() - super.getBoxSize())
        throw new BoxException();
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    long ret;
  }
}