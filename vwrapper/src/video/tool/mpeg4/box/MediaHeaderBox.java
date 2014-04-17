package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;
import video.tool.mpeg4.DateHelper;
import video.tool.mpeg4.NumericHelper;

public class MediaHeaderBox extends FullBox
{
  public static final int MediaHeaderBoxLengthV0 = 20;
  public static final int MediaHeaderBoxLengthV1 = 32;
  private long creationTime_;
  private long modificationTime_;
  private int timescale_ = 1000;
  private long duration_;
  private short language_;
  private short preDefined_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nCreation Time: ").append(getCreationTime().toString());
    ret.append("\nModification Time: ").append(getModificationTime().toString());
    ret.append("\nTimescale: ").append(getTimeScale());
    ret.append("\nDuration: ").append(getDuration());
    ret.append("\nLanguage: ").append(getLanguage());
    return ret.toString();
  }

  public void setCreationTime(Date date) {
    this.creationTime_ = DateHelper.convert(date);
  }

  public Date getCreationTime() {
    return DateHelper.convert(this.creationTime_);
  }

  public void setModificationTime(Date date) {
    this.modificationTime_ = DateHelper.convert(date);
  }

  public Date getModificationTime() {
    return DateHelper.convert(this.modificationTime_);
  }

  public int getTimeScale() {
    return this.timescale_;
  }

  public void setDuration(double duration) {
    this.duration_ = ((long) (duration * this.timescale_));
  }

  public double getDuration() {
    return this.duration_ / this.timescale_;
  }

  public void setLanguage(String lang)
  {
    try {
      byte[] l = lang.getBytes("US-ASCII");
      if (l.length != 3) {
        setLanguage("und");
      }
      this.language_ = ((short)((l[0] - 96 << 10) + (l[1] - 96 << 5) + (l[2] - 96)));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();

      setLanguage("und");
    }
  }

  public String getLanguage() {
    byte[] lang = new byte[3];
    lang[0] = ((byte)((this.language_ >>> 10 & 0x1F) + 96));
    lang[1] = ((byte)((this.language_ >>> 5 & 0x1F) + 96));
    lang[2] = ((byte)((this.language_ & 0x1F) + 96));
    return new String(lang);
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);

    ByteBuffer buff = ByteBuffer.allocate(getVersion() == 0 ? 20 : 32);
    try {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      if (getVersion() == 0) {
        this.creationTime_ = NumericHelper.getLongFromInt(buff.getInt());
        this.modificationTime_ = NumericHelper.getLongFromInt(buff.getInt());
        this.timescale_ = buff.getInt();
        this.duration_ = NumericHelper.getLongFromInt(buff.getInt());
      }
      else {
        this.creationTime_ = buff.getLong();
        this.modificationTime_ = buff.getLong();
        this.timescale_ = buff.getInt();
        this.duration_ = buff.getLong();
      }
      this.language_ = buff.getShort();
      this.preDefined_ = buff.getShort();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(getVersion() == 0 ? 20 : 32);
    if (getVersion() == 0) {
      buff.putInt((int)this.creationTime_);
      buff.putInt((int)this.modificationTime_);
      buff.putInt(this.timescale_);
      buff.putInt((int)this.duration_);
    }
    else {
      buff.putLong(this.creationTime_);
      buff.putLong(this.modificationTime_);
      buff.putInt(this.timescale_);
      buff.putLong(this.duration_);
    }

    buff.putShort(this.language_);
    buff.putShort(this.preDefined_);
    try {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static MediaHeaderBox build() {
    MediaHeaderBox box = new MediaHeaderBox();
    box.setVersion(0);
    box.setSize(box.getBoxSize() + 20);
    box.setLanguage("und");
    return box;
  }
}