package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class DataEntryUrlBox extends FullBox
{
  private String location_ = "";

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append(" location: ").append(this.location_);
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
      this.location_ = new String(buff.array(), "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }int ret;
  }

  public void write(OutputStream out) {
    super.write(out);
    try {
      out.write(this.location_.getBytes("UTF-8"));
      out.write(0);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static DataEntryUrlBox build() {
    DataEntryUrlBox box = new DataEntryUrlBox();
    box.setVersion(0);
    box.setFlags(1);
    try {
      box.setSize(box.getBoxSize() + box.location_.getBytes("UTF-8").length + 1);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      box.setSize(box.getBoxSize() + 1);
    }
    return box;
  }
}