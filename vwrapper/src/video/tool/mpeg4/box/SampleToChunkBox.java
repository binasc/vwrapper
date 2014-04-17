package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SampleToChunkBox extends FullBox
{
  private List<Triple> entries_;

  public void addSample(boolean needNewChunk)
  {
    if (this.entries_ == null) {
      this.entries_ = new LinkedList();
      this.entries_.add(new Triple(1, 1, 1));
      return;
    }
    ((Triple)this.entries_.get(0)).samplesPerChunk_ += 1;
  }

  public String toString() {
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
        this.entries_.add(new Triple(buff.getInt(), buff.getInt(), buff.getInt()));
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int count;
    int ret;
  }

  class Triple
  {
    int firstChunk_;
    int samplesPerChunk_;
    int sampleDescriptionIndex_;

    Triple(int firstChunk, int samplesPerChunk, int sampleDescriptionIndex)
    {
      this.firstChunk_ = firstChunk;
      this.samplesPerChunk_ = samplesPerChunk;
      this.sampleDescriptionIndex_ = sampleDescriptionIndex;
    }
  }
}