package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SampleEntry extends Box
{
  private byte[] reserved_ = new byte[6];
  private short dataReferenceIndex_;

  public int getBoxSize()
  {
    return super.getBoxSize() + 8;
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append(" <Sample Entry ref: ").append(getDataReferenceIndex());
    return ">";
  }

  public short getDataReferenceIndex() {
    return this.dataReferenceIndex_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);
    ByteBuffer buff = ByteBuffer.allocate(8);
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      buff.get(this.reserved_);
      this.dataReferenceIndex_ = buff.getShort();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}