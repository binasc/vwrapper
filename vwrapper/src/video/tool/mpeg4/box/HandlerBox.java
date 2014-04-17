package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class HandlerBox extends FullBox
{
  public static final int HandlerBoxLengthV0 = 20;
  private int preDefined_;
  private int handlerType_;
  private int[] reserved_ = new int[3];
  private String name_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nHandler Type: ").append(getHandlerType());
    ret.append("\nName: ").append(getName());
    return ret.toString();
  }

  public String getHandlerType() {
    return Box.getTypeString(this.handlerType_);
  }

  public String getName() {
    return this.name_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);

    ByteBuffer buff = ByteBuffer.allocate(20);
    try {
      int ret = in.read(buff.array());
      if (ret != buff.capacity())
        throw new BoxException();
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    this.preDefined_ = buff.getInt();
    this.handlerType_ = buff.getInt();
    for (int i = 0; i < 3; i++) {
      this.reserved_[i] = buff.getInt();
    }
    byte[] name = new byte[(int)getSize() - getBoxSize() - 20];
    try {
      int ret = in.read(name);
      if (ret != name.length)
        throw new BoxException();
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    try {
      this.name_ = new String(name, 0, name.length, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      this.name_ = new String(name);
    }
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(20);
    buff.putInt(this.preDefined_);
    buff.putInt(this.handlerType_);
    for (int i = 0; i < 3; i++) {
      buff.putInt(this.reserved_[i]);
    }
    try
    {
      out.write(buff.array());
      out.write(this.name_.getBytes("UTF-8"));
      out.write(0);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static HandlerBox build(TrackBox.Type type) {
    HandlerBox box = new HandlerBox();
    box.setVersion(0);
    switch (type) {
    case AUDIO:
      box.handlerType_ = Box.getTypeFromString("vide");
      break;
    case VIDEO:
      box.handlerType_ = Box.getTypeFromString("soun");
    }

    box.name_ = "GPAC ISO Video Handler";
    try {
      box.setSize(box.getBoxSize() + 20 + box.name_.getBytes("UTF-8").length + 1);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      box.setSize(box.getBoxSize() + 20 + 1);
    }
    return box;
  }
}