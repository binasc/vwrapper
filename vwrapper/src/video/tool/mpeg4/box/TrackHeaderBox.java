package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import video.tool.mpeg4.DateHelper;
import video.tool.mpeg4.NumericHelper;

public class TrackHeaderBox extends FullBox
{
  public static final int FlagTrackEnable = 1;
  public static final int FlagTrackInMovie = 2;
  public static final int FlagTrackInPreview = 4;
  public static final int TrackHeaderBoxLengthV0 = 80;
  public static final int TrackHeaderBoxLengthV1 = 92;
  private long creationTime_;
  private long modificationTime_;
  private int trackId_;
  private int reserved_;
  private long duration_;
  private int reserved1_;
  private int reserved2_;
  private short layer_ = 0;
  private short alternateGroup_ = 0;
  private short volume_ = 0;
  private short reserved3_;
  private int[] matrix_ = { 65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824 };
  private int width_;
  private int height_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nCreation Time: ").append(getCreationTime().toString());
    ret.append("\nModification Time: ").append(getModificationTime().toString());
    ret.append("\nTrack ID: ").append(getTrackID());
    ret.append("\nDuration: ").append(getDuration());
    ret.append("\nLayer: ").append(getLayer());
    ret.append("\nAlternate Group: ").append(getAlternateGroup());
    ret.append("\nVolume: ").append(getVolume());
    ret.append("\nMatrix: {");
    for (int i : this.matrix_) {
      ret.append("0x").append(Integer.toHexString(i)).append(",");
    }
    ret.append("}");
    ret.append("\nWidth: ").append(getWidth());
    ret.append("\nHeight: ").append(getHeight());
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

  public void setTrackID(int id) {
    this.trackId_ = id;
  }

  public int getTrackID() {
    return this.trackId_;
  }

  public void setDuration(double duration) {
    MovieBox moov = (MovieBox)getParent().getParent();
    this.duration_ = ((long) (duration * moov.getMovieHeaderBox().getTimeScale()));
  }

  public double getDuration() {
    MovieBox moov = (MovieBox)getParent().getParent();
    return this.duration_ / moov.getMovieHeaderBox().getTimeScale();
  }

  public short getLayer() {
    return this.layer_;
  }

  public short getAlternateGroup() {
    return this.alternateGroup_;
  }

  public double getVolume() {
    return this.volume_ / 256.0D;
  }

  public int[] getMatrix() {
    return this.matrix_;
  }

  public void setWidth(double width) {
    this.width_ = ((int)(width * 65536.0D));
  }

  public double getWidth() {
    return this.width_ / 65536.0D;
  }

  public void setHeight(double height) {
    this.height_ = ((int)(height * 65536.0D));
  }

  public double getHeight() {
    return this.height_ / 65536.0D;
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);

    ByteBuffer buff = ByteBuffer.allocate(getVersion() == 0 ? 80 : 92);
    try {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      if (getVersion() == 0) {
        this.creationTime_ = NumericHelper.getLongFromInt(buff.getInt());
        this.modificationTime_ = NumericHelper.getLongFromInt(buff.getInt());
        this.trackId_ = buff.getInt();
        this.reserved_ = buff.getInt();
        this.duration_ = NumericHelper.getLongFromInt(buff.getInt());
      }
      else {
        this.creationTime_ = buff.getInt();
        this.modificationTime_ = buff.getInt();
        this.trackId_ = buff.getInt();
        this.reserved_ = buff.getInt();
        this.duration_ = buff.getInt();
      }
      this.reserved1_ = buff.getInt();
      this.reserved2_ = buff.getInt();
      this.layer_ = buff.getShort();
      this.alternateGroup_ = buff.getShort();
      this.volume_ = buff.getShort();
      this.reserved3_ = buff.getShort();
      for (int i = 0; i < 9; i++) {
        this.matrix_[i] = buff.getInt();
      }
      this.width_ = buff.getInt();
      this.height_ = buff.getInt();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(getVersion() == 0 ? 80 : 92);
    if (getVersion() == 0) {
      buff.putInt((int)this.creationTime_);
      buff.putInt((int)this.modificationTime_);
      buff.putInt(this.trackId_);
      buff.putInt(this.reserved_);
      buff.putInt((int)this.duration_);
    }
    else {
      buff.putLong(this.creationTime_);
      buff.putLong(this.modificationTime_);
      buff.putInt(this.trackId_);
      buff.putInt(this.reserved_);
      buff.putLong(this.duration_);
    }
    buff.putInt(this.reserved1_);
    buff.putInt(this.reserved2_);
    buff.putShort(this.layer_);
    buff.putShort(this.alternateGroup_);
    buff.putShort(this.volume_);
    buff.putShort(this.reserved3_);
    for (int cell : this.matrix_) {
      buff.putInt(cell);
    }
    buff.putInt(this.width_);
    buff.putInt(this.height_);
    try {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static TrackHeaderBox build(TrackBox.Type type) {
    TrackHeaderBox box = new TrackHeaderBox();
    box.setVersion(0);
    box.setSize(box.getBoxSize() + 80);
    box.setFlags(1);
    if (type == TrackBox.Type.AUDIO) {
      box.volume_ = 256;
    }
    return box;
  }
}