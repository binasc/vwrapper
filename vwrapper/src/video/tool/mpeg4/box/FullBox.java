package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class FullBox extends Box
{
  private int versionAndFlags_;

  public FullBox()
  {
    setSize(super.getBoxSize() + 4);
  }

  public int getBoxSize() {
    return super.getBoxSize() + 4;
  }

  public void setVersion(int version) {
    this.versionAndFlags_ = (version << 24 & 0xFF000000 | getFlags());
  }

  public int getVersion() {
    return this.versionAndFlags_ >>> 24 & 0xFF;
  }

  public void setFlags(int flags) {
    this.versionAndFlags_ = (getVersion() << 24 & 0xFF000000 | flags & 0xFFFFFF);
  }

  public int getFlags()
  {
    return this.versionAndFlags_ & 0xFFFFFF;
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append(" <FullBox ver: ").append(getVersion());
    ret.append(" Flags: 0x").append(Integer.toHexString(getFlags()));
    return ">";
  }

  protected void parseBox(InputStream in) throws BoxException
  {
    ByteBuffer buff = ByteBuffer.allocate(4);
    try
    {
      int ret = in.read(buff.array());
      if (ret != 4) {
        throw new BoxException();
      }
      this.versionAndFlags_ = buff.getInt();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }int ret;
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(4);
    buff.putInt(this.versionAndFlags_);
    try {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}