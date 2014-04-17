package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class VideoMediaHeaderBox extends FullBox
{
  private short graphicsmode_ = 0;
  private short[] opcolor_ = new short[3];

  public String toString() {
    String ret = super.toString();
    ret = ret + " graphics mode: " + this.graphicsmode_;
    ret = ret + " opcolor_: " + this.opcolor_[0] + ":" + this.opcolor_[1] + ":" + this.opcolor_[2];
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
      this.graphicsmode_ = buff.getShort();
      this.opcolor_[0] = buff.getShort();
      this.opcolor_[1] = buff.getShort();
      this.opcolor_[2] = buff.getShort();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }int ret;
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(8);
    buff.putShort(this.graphicsmode_);
    buff.putShort(this.opcolor_[0]);
    buff.putShort(this.opcolor_[1]);
    buff.putShort(this.opcolor_[2]);
    try {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static VideoMediaHeaderBox build() {
    VideoMediaHeaderBox box = new VideoMediaHeaderBox();
    box.setVersion(0);
    box.setFlags(1);
    box.setSize(box.getBoxSize() + 8);
    return box;
  }
}