package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class MPEG4BitRateBox extends Box
{
  private int bufferSizeDB_;
  private int maxBitrate_;
  private int avgBitrate_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    return ret.toString();
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);
    ByteBuffer buff = ByteBuffer.allocate((int)getSize() - getBoxSize());
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      this.bufferSizeDB_ = buff.getInt();
      this.maxBitrate_ = buff.getInt();
      this.avgBitrate_ = buff.getInt();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}