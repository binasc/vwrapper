package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class PixelAspectRatioBox extends Box
{
  private int hSpacing_;
  private int vSpacing_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nhSpacing: ").append(this.hSpacing_);
    ret.append("\nvSpacing: ").append(this.vSpacing_);
    return ret.toString();
  }

  public int getHSpacing() {
    return this.hSpacing_;
  }

  public int getVSpacing() {
    return this.vSpacing_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    ByteBuffer buff = ByteBuffer.allocate(8);
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      this.hSpacing_ = buff.getInt();
      this.vSpacing_ = buff.getInt();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}