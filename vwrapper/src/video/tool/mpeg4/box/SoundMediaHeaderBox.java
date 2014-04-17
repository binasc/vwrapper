package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class SoundMediaHeaderBox extends FullBox
{
  private short balance_;
  private short reserved_;

  public String toString()
  {
    String ret = super.toString();
    ret = ret + " balance: " + this.balance_;
    ret = ret + " reserved: " + this.reserved_;
    return ret;
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
      this.balance_ = buff.getShort();
      this.reserved_ = buff.getShort();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }int ret;
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(4);
    buff.putShort(this.balance_);
    buff.putShort(this.reserved_);
    try {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static SoundMediaHeaderBox build() {
    SoundMediaHeaderBox box = new SoundMediaHeaderBox();
    box.setVersion(0);
    box.setFlags(0);
    box.setSize(box.getBoxSize() + 4);
    return box;
  }
}