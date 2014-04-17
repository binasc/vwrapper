package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class SyncSampleBox extends FullBox
{
  private List<Integer> sampleNumbers_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append(" entry count: ").append(this.sampleNumbers_.size());
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
      int count = buff.getInt();
      this.sampleNumbers_ = new ArrayList(count);
      for (int i = 0; i < count; i++)
        this.sampleNumbers_.add(Integer.valueOf(buff.getInt()));
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int count;
    int ret;
  }
}