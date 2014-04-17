package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class VisualSampleEntry extends SampleEntry
{
  public static final int VisualSampleEntryHeaderSize = 70;
  private short preDefined_ = 0;
  private short reserved_ = 0;
  private int[] preDefined1_ = new int[3];
  private short width_;
  private short height_;
  private int horizresolution_ = 4718592;
  private int vertresolution_ = 4718592;
  private int reserved1_ = 0;
  private short frameCount_ = 1;
  private String compressorname;
  private short depth_ = 24;
  private short preDefined2_ = -1;

  public int getBoxSize() {
    return super.getBoxSize() + 70;
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);
    ByteBuffer buff = ByteBuffer.allocate(70);
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      buff.get(this.reserved_);
      this.preDefined_ = buff.getShort();
      this.reserved_ = buff.getShort();
      this.preDefined1_[0] = buff.getInt();
      this.preDefined1_[1] = buff.getInt();
      this.preDefined1_[2] = buff.getInt();
      this.width_ = buff.getShort();
      this.height_ = buff.getShort();
      this.horizresolution_ = buff.getInt();
      this.vertresolution_ = buff.getInt();
      this.reserved1_ = buff.getInt();
      this.frameCount_ = buff.getShort();
      byte[] name = new byte[33];
      buff.get(name, 0, 32);
      name[name[0]] = 0;
      this.compressorname = new String(name, 1, 31);
      this.depth_ = buff.getShort();
      this.preDefined2_ = buff.getShort();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}