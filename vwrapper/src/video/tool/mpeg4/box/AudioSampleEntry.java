package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioSampleEntry extends SampleEntry
{
  public static final int AudioSampleEntryHeaderSize = 20;
  private int[] reserved_ = new int[2];
  private int channelcount_ = 2;
  private int samplesize_ = 16;

  private short preDefined_ = 0;

  private short reserved1_ = 0;
  private int samplerate_;
  private byte[] data_;

  protected void parseBox(InputStream in)
    throws BoxException
  {
    super.parseBox(in);
    ByteBuffer buff = ByteBuffer.allocate(20);
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      this.reserved_[0] = buff.getInt();
      this.reserved_[1] = buff.getInt();
      this.samplesize_ = buff.getInt();
      this.preDefined_ = buff.getShort();
      this.reserved1_ = buff.getShort();
      this.samplerate_ = buff.getInt();

      this.data_ = new byte[(int)getSize() - getBoxSize() - 20];
      ret = in.read(this.data_);
      if (ret != this.data_.length)
        throw new BoxException();
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}