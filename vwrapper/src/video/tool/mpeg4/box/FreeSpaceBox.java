package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;

public class FreeSpaceBox extends Box
{
  private byte[] data_;

  public String toString()
  {
    return super.toString() + " free space: 0x" + Integer.toHexString(this.data_.length);
  }

  protected void parseBox(InputStream in) throws BoxException
  {
    this.data_ = new byte[(int)getSize() - super.getBoxSize()];
    try {
      int ret = in.read(this.data_);
      if (ret != (int)getSize() - super.getBoxSize())
        throw new BoxException();
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}