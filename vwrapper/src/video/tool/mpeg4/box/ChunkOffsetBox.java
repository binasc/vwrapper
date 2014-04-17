package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ChunkOffsetBox extends FullBox
{
  private List<Integer> chunkOffsets_;

  public void addChunkOffset(int offset)
  {
    if (this.chunkOffsets_ == null) {
      this.chunkOffsets_ = new LinkedList();
    }
    this.chunkOffsets_.add(Integer.valueOf(offset));
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    if (this.chunkOffsets_ != null) {
      ret.append(" entry count: ").append(this.chunkOffsets_.size());
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
      this.chunkOffsets_ = new ArrayList(count);
      for (int i = 0; i < count; i++)
        this.chunkOffsets_.add(Integer.valueOf(buff.getInt()));
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int count;
    int ret;
  }
}