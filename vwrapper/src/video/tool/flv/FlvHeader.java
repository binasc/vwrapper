package video.tool.flv;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import video.tool.flv.tag.TagException;

public class FlvHeader
{
  static final int defaultHeaderLength = 9;
  public String signature = new String("FLV");
  public byte version = 1;
  public byte typeFlags = 0;
  public int dataOffset = 9;

  public void setHasAudio() {
    this.typeFlags = ((byte)(this.typeFlags | 0x4));
  }

  public void setHasVideo() {
    this.typeFlags = ((byte)(this.typeFlags | 0x1));
  }

  public byte[] toByteArray() {
    int pos = 0;
    byte[] buff = new byte[this.dataOffset];
    try
    {
      buff[pos] = this.signature.getBytes("US-ASCII")[0];
      pos++;
      buff[pos] = this.signature.getBytes("US-ASCII")[1];
      pos++;
      buff[pos] = this.signature.getBytes("US-ASCII")[2];
      pos++;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      buff[0] = 70;
      buff[1] = 76;
      buff[2] = 86;
      pos = 3;
    }
    buff[pos] = this.version;
    pos++;
    buff[pos] = this.typeFlags;
    pos++;
    ByteBuffer.wrap(buff, pos, 4).putInt(this.dataOffset);

    return buff;
  }

  public static FlvHeader parseFlvHeader(InputStream in) throws TagException {
    FlvHeader header = new FlvHeader();
    byte[] buff = new byte[16];
    try
    {
      int ret = in.read(buff, 0, 9);
      if (ret != 9) {
        throw new TagException();
      }
      header.signature = new String(buff, 0, 3, "US-ASCII");
      if (!header.signature.equals("FLV")) {
        throw new TagException(0, "not a FLV file");
      }
      header.version = buff[3];
      if (header.version != 1) {
        throw new TagException();
      }
      header.typeFlags = buff[4];
      header.dataOffset = ByteBuffer.wrap(buff, 5, 4).getInt();
      int remainOffset = header.dataOffset - 9;
      if (remainOffset < 0) {
        throw new TagException();
      }
      if (remainOffset > 0) {
        ret = in.read(buff, 9, remainOffset);
        if (ret != remainOffset) {
          throw new TagException();
        }
      }
      ret = in.read(buff, 0, 4);
      if ((ret != 4) || (ByteBuffer.wrap(buff, 0, 4).getInt() != 0)) {
        throw new TagException();
      }

      return header;
    } catch (IOException e) {
      e.printStackTrace();
    }throw new TagException();
  }
}