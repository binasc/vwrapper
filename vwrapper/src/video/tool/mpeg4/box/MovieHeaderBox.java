package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import video.tool.mpeg4.DateHelper;
import video.tool.mpeg4.NumericHelper;

public class MovieHeaderBox extends FullBox
{
  public static final int MovieHeaderBoxLengthV0 = 96;
  public static final int MovieHeaderBoxLengthV1 = 108;
  private long creationTime_;
  private long modificationTime_;
  private int timescale_ = 1000;
  private long duration_;
  private int rate_ = 65536;
  private short volume_ = 256;
  private short reserved_;
  private int reserved1_;
  private int reserved2_;
  private int[] matrix_ = { 65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824 };
  private int[] preDefined_ = new int[6];
  private int nextTrackID_ = 1;

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nCreation Time: ").append(getCreationTime().toString());
    ret.append("\nModification Time: ").append(getModificationTime().toString());
    ret.append("\nTimescale: ").append(getTimeScale());
    ret.append("\nDuration: ").append(getDuration());
    ret.append("\nRate: ").append(getRate());
    ret.append("\nVolume: ").append(getVolume());
    ret.append("\nMatrix: {");
    for (int i : this.matrix_) {
      ret.append("0x").append(Integer.toHexString(i)).append(",");
    }
    ret.append("}");
    ret.append("\nNext Track ID: ").append(getNextTrackID());
    return ret.toString();
  }

  public void setCreateTime(Date date) {
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
    this.duration_ = ((int)(duration * this.timescale_));
  }

  public double getDuration() {
    return this.duration_ / this.timescale_;
  }

  public double getRate() {
    return this.rate_ / 65536.0D;
  }

  public double getVolume() {
    return this.volume_ / 256.0D;
  }

  public int[] getMatrix() {
    return this.matrix_;
  }

  public void setNextTrackID(int id) {
    this.nextTrackID_ = id;
  }

  public int getNextTrackID() {
    return this.nextTrackID_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);

    ByteBuffer buff = ByteBuffer.allocate(getVersion() == 0 ? 96 : 108);
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
      this.rate_ = buff.getInt();
      this.volume_ = buff.getShort();
      this.reserved_ = buff.getShort();
      this.reserved1_ = buff.getInt();
      this.reserved2_ = buff.getInt();
      for (int i = 0; i < 9; i++) {
        this.matrix_[i] = buff.getInt();
      }
      for (int i = 0; i < 6; i++) {
        this.preDefined_[i] = buff.getInt();
      }
      this.nextTrackID_ = buff.getInt();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(getVersion() == 0 ? 96 : 108);
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

    buff.putInt(this.rate_);
    buff.putShort(this.volume_);
    buff.putShort(this.reserved_);
    buff.putInt(this.reserved1_);
    buff.putInt(this.reserved2_);
    for (int cell : this.matrix_) {
      buff.putInt(cell);
    }
    for (int v : this.preDefined_) {
      buff.putInt(v);
    }
    buff.putInt(this.nextTrackID_);
    try {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static MovieHeaderBox build() {
    MovieHeaderBox box = new MovieHeaderBox();
    box.setVersion(0);
    box.setSize(box.getBoxSize() + 96);
    return box;
  }
}