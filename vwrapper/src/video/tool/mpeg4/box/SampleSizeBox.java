package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SampleSizeBox extends FullBox
{
  private int sampleSize_;
  private List<Integer> entrySizes_;

  public void addSampleSize(int size)
  {
    this.sampleSize_ = 0;
    if (this.entrySizes_ == null) {
      this.entrySizes_ = new LinkedList();
    }
    this.entrySizes_.add(Integer.valueOf(size));
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append(" sample size: ").append(this.sampleSize_);
    if (this.entrySizes_ != null) {
      ret.append(" entry count: ").append(this.entrySizes_.size());
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
      this.sampleSize_ = buff.getInt();
      int count = buff.getInt();
      if (this.sampleSize_ == 0) {
        this.entrySizes_ = new ArrayList(count);
        for (int i = 0; i < count; i++)
          this.entrySizes_.add(Integer.valueOf(buff.getInt()));
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int count;
    int ret;
  }
}