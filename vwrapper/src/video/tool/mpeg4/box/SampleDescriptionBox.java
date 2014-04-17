package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import video.tool.mpeg4.AVCDecoderConfigurationRecord;

public class SampleDescriptionBox extends FullBox
{
  private List<Box> sampleEntries_ = new ArrayList();

  public void addSampleEntry(String type, Object data) {
    if (type.equals("avc1")) {
      AVCSampleEntry entry = new AVCSampleEntry((AVCDecoderConfigurationRecord)data);
      this.sampleEntries_.add(entry);
    }
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n<sample entries>");
    for (Box box : this.sampleEntries_) {
      ret.append("\n").append(box.toString());
    }
    ret.append("\n</sample entries>");
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
      this.sampleEntries_ = new ArrayList(count);
      int size = getBoxSize() + 4;

      for (int i = 0; i < count; i++) {
        Box box = Box.nextBox(in);
        box.setParent(this);
        this.sampleEntries_.add(box);
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