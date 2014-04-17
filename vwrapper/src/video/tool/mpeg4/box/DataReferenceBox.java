package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class DataReferenceBox extends FullBox
{
  private List<Box> dataEntryBoxes_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n<references>");
    for (Box box : this.dataEntryBoxes_) {
      ret.append("\n").append(box.toString());
    }
    ret.append("\n</references>");
    return ret.toString();
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);
    ByteBuffer buff = ByteBuffer.allocate(4);
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      int count = buff.getInt();
      this.dataEntryBoxes_ = new ArrayList(count);

      int size = getBoxSize() + 4;
      for (int i = 0; i < count; i++) {
        Box box = Box.nextBox(in);
        this.dataEntryBoxes_.add(box);
        size = (int)(size + box.getSize());
        if (size > getSize()) {
          throw new BoxException();
        }
        if (size == getSize())
          break;
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