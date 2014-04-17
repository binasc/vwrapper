package video.tool.flv.tag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import video.tool.mpeg4.AVCDecoderConfigurationRecord;

public class Tag
{
  public static int firstPartLength = 11;
  public int offset;
  private byte firstByte_ = 0;
  private int datasize_ = 0;
  private int timestamp_ = 0;
  private byte timestampExtend_ = 0;
  private int streamId_ = 0;
  private InnerTag innerTag_ = null;

  static int lastTS = 0;

  public String toString() { StringBuilder ret = new StringBuilder();
    ret.append("size: ").append(this.datasize_);
    ret.append("\ntimestamp: ").append(getTimestamp());
    ret.append(" ").append(getTimestamp() - lastTS);
    lastTS = getTimestamp();
    return ret.toString(); }

  public void setFilter(boolean hasFilter)
  {
    if (hasFilter)
      this.firstByte_ = ((byte)(this.firstByte_ | 0x20));
    else
      this.firstByte_ = ((byte)(this.firstByte_ & 0xDF));
  }

  public boolean getFilter() {
    return (this.firstByte_ & 0x20) != 0;
  }

  public void setType(Type type) {
    this.firstByte_ = ((byte)(this.firstByte_ & 0xE0));
    switch (type) {
    case SCRIPT:
      this.firstByte_ = ((byte)(this.firstByte_ | 0x8));
      break;
    case UNKNOWN:
      this.firstByte_ = ((byte)(this.firstByte_ | 0x9));
      break;
    case VIDEO:
      this.firstByte_ = ((byte)(this.firstByte_ | 0x12));
      break;
    }
  }

  public Type getType()
  {
    int type = this.firstByte_ & 0x1F;
    switch (type) {
    case 8:
      return Type.AUDIO;
    case 9:
      return Type.VIDEO;
    case 18:
      return Type.SCRIPT;
    }
    return Type.UNKNOWN;
  }

  public void setDatasize(int datasize) {
    this.datasize_ = datasize;
  }

  public int getDatasize() {
    return this.datasize_;
  }

  public int getTimestamp() {
    return this.timestamp_ + (this.timestampExtend_ << 24 & 0xFF000000);
  }

  public void setTimestamp(int ts) {
    this.timestamp_ = (ts & 0xFFFFFFFF);
    this.timestampExtend_ = ((byte)(ts >>> 24 & 0xFF));
  }

  public void setInnerTag(InnerTag tag) {
    this.innerTag_ = tag;
  }

  public VideoTag getVideoTag() {
    if (getType() == Type.VIDEO)
      return (VideoTag)this.innerTag_;
    return null;
  }

  public ScriptDataTag getScriptDataTag() {
    if (getType() == Type.SCRIPT)
      return (ScriptDataTag)this.innerTag_;
    return null;
  }

  public byte[] toByteArray() {
    int pos = 0;
    byte[] buff = new byte[firstPartLength];

    buff[pos] = this.firstByte_;
    pos++;
    buff[pos] = ((byte)(this.datasize_ >>> 16 & 0xFF));
    pos++;
    buff[pos] = ((byte)(this.datasize_ >>> 8 & 0xFF));
    pos++;
    buff[pos] = ((byte)(this.datasize_ & 0xFF));
    pos++;

    buff[pos] = ((byte)(this.timestamp_ >>> 16 & 0xFF));
    pos++;
    buff[pos] = ((byte)(this.timestamp_ >>> 8 & 0xFF));
    pos++;
    buff[pos] = ((byte)(this.timestamp_ & 0xFF));
    pos++;
    buff[pos] = this.timestampExtend_;
    pos++;
    buff[pos] = ((byte)(this.streamId_ >>> 16 & 0xFF));
    pos++;
    buff[pos] = ((byte)(this.streamId_ >>> 8 & 0xFF));
    pos++;
    buff[pos] = ((byte)(this.streamId_ & 0xFF));

    return buff;
  }

  public static Tag parseFlvTag(InputStream in) throws TagException {
    Tag tag = new Tag();
    byte[] buff = new byte[16];
    try {
      int pos = 0;

      int ret = in.read(buff, 0, firstPartLength);

      if (ret == -1) {
        return null;
      }
      if (ret != firstPartLength) {
        throw new TagException();
      }
      tag.firstByte_ = buff[pos];
      pos++;
      buff[(pos - 1)] = 0;
      tag.datasize_ = ByteBuffer.wrap(buff, pos - 1, 4).getInt();
      pos += 3;
      buff[(pos - 1)] = 0;
      tag.timestamp_ = ByteBuffer.wrap(buff, pos - 1, 4).getInt();
      pos += 3;
      tag.timestampExtend_ = buff[pos];
      pos++;
      buff[(pos - 1)] = 0;
      tag.streamId_ = ByteBuffer.wrap(buff, pos - 1, 4).getInt();
      if (tag.streamId_ != 0) {
        throw new TagException();
      }

      int remainSize = 0;
      switch (tag.getType()) {
      case VIDEO:
        tag.innerTag_ = VideoTag.parseVideoTag(in);
        remainSize = tag.datasize_ - tag.innerTag_.size();
        if (((VideoTag)tag.innerTag_).AVCPacketType == 0) {
          AVCDecoderConfigurationRecord record = new AVCDecoderConfigurationRecord();
          ByteBuffer buf = ByteBuffer.allocate(remainSize);
          ret = in.read(buf.array());
          if (ret != buf.capacity()) {
            throw new TagException();
          }
          record.parse(buf);
          ((VideoTag)tag.innerTag_).record = record;
          remainSize = 0;
        }

        break;
      case SCRIPT:
        byte[] script = new byte[tag.datasize_];
        ret = in.read(script);
        if (ret != tag.datasize_) {
          throw new TagException();
        }
        ByteArrayInputStream bin = new ByteArrayInputStream(script);
        tag.innerTag_ = ScriptDataTag.parseScriptDataTag(bin);
        remainSize = 0;

        break;
      default:
        remainSize = tag.datasize_;
      }

      while (remainSize > 0) {
        ret = (int)in.skip(remainSize);
        if (ret == 0) {
          throw new TagException();
        }
        remainSize -= ret;
      }
      if (tag.innerTag_ != null) {
        tag.innerTag_.setParent(tag);
      }

      ret = in.read(buff, 0, 4);

      if (ret == -1) {
        return tag;
      }
      if ((ret != 4) || (ByteBuffer.wrap(buff, 0, 4).getInt() != tag.datasize_ + firstPartLength)) {
        throw new TagException();
      }

      return tag;
    } catch (IOException e) {
      e.printStackTrace();
    }throw new TagException();
  }

  public static enum Type
  {
    UNKNOWN, AUDIO, VIDEO, SCRIPT;
  }
}