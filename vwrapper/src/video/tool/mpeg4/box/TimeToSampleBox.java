package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TimeToSampleBox extends FullBox
{
  private List<Pair> entries_;

  public void addDelta(int delta)
  {
    if (this.entries_ == null) {
      this.entries_ = new LinkedList();
      this.entries_.add(new Pair(1, delta));
      return;
    }
    Pair pair = (Pair)this.entries_.get(this.entries_.size() - 1);
    if (pair.sampleDelta_ == delta) {
      pair.sampleCount_ += 1;
    }
    else
      this.entries_.add(new Pair(1, delta));
  }

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    if (this.entries_ != null) {
      ret.append(" entry count: ").append(this.entries_.size());
    }
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
      this.entries_ = new ArrayList(count);
      for (int i = 0; i < count; i++)
        this.entries_.add(new Pair(buff.getInt(), buff.getInt()));
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int count;
    int ret;
  }

  class Pair
  {
    int sampleCount_;
    int sampleDelta_;

    Pair(int c, int d)
    {
      this.sampleCount_ = c;
      this.sampleDelta_ = d;
    }
  }
}