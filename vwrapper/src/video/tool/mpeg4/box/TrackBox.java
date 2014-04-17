package video.tool.mpeg4.box;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import video.tool.mpeg4.AVCDecoderConfigurationRecord;

public class TrackBox extends Box
{
  private TrackHeaderBox tkhd_;
  private Box tref_;
  private MediaBox mdia_;
  private List<Box> oths_ = new LinkedList();

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.tkhd_.toString());
    if (this.tref_ != null) {
      ret.append("\n").append(this.tref_.toString());
    }
    ret.append("\n").append(this.mdia_.toString());
    if (this.oths_.size() > 0) {
      ret.append("\n<others>");
      for (Box box : this.oths_) {
        ret.append("\n").append(box.toString());
      }
      ret.append("\n</others>");
    }
    return ret.toString();
  }

  public TrackHeaderBox getTrackHeaderBox() {
    return this.tkhd_;
  }

  public MediaBox getMediaBox() {
    return this.mdia_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("tkhd")) {
        this.tkhd_ = ((TrackHeaderBox)box);
      }
      else if (box.getType().equals("tref")) {
        this.tref_ = box;
      }
      else if (box.getType().equals("mdia")) {
        this.mdia_ = ((MediaBox)box);
      }
      else {
        this.oths_.add(box);
      }
      size = (int)(size + box.getSize());
      if (size > getSize()) {
        throw new BoxException();
      }
      if (size == getSize())
        break;
    }
  }

  public void write(OutputStream out)
  {
    super.write(out);
    this.tkhd_.write(out);
    this.mdia_.write(out);
  }

  public void addSample(byte[] sample, int off, int len, boolean isKeyframe, int duration, int compositionTimeOffset) {
    this.mdia_.getMediaInformationBox().getSampleTableBox().getTimeToSampleBox().addDelta(duration);
    this.mdia_.getMediaInformationBox().getSampleTableBox().getSampleToChunkBox().addSample(false);
    this.mdia_.getMediaInformationBox().getSampleTableBox().getSampleSizeBox().addSampleSize(len);
    this.tkhd_.setDuration((this.tkhd_.getDuration() + duration) / 1000.0D);
    MovieBox moov = (MovieBox)getParent();
    if (moov.getMovieHeaderBox().getDuration() < getTrackHeaderBox().getDuration()) {
      moov.getMovieHeaderBox().setDuration(getTrackHeaderBox().getDuration());
    }
    setSize((int)(getBoxSize() + this.tkhd_.getSize() + this.mdia_.getSize()));
  }

  public static TrackBox build(Type type) {
    TrackBox box = new TrackBox();
    box.tkhd_ = TrackHeaderBox.build(type);
    box.tkhd_.setParent(box);
    box.mdia_ = MediaBox.build(type);
    box.mdia_.setParent(box);
    box.setSize((int)(box.getBoxSize() + box.tkhd_.getSize() + box.mdia_.getSize()));
    return box;
  }

  public static TrackBox buildVideoTrackBox(double width, double height, AVCDecoderConfigurationRecord record) {
    TrackBox box = build(Type.VIDEO);
    box.tkhd_.setCreationTime(new Date());
    box.tkhd_.setModificationTime(new Date());
    box.tkhd_.setWidth(width);
    box.tkhd_.setHeight(height);
    box.mdia_.getMediaDataBox().setCreationTime(new Date());
    box.mdia_.getMediaDataBox().setModificationTime(new Date());
    box.mdia_.getMediaInformationBox().getSampleTableBox().getSampleDescriptionBox().addSampleEntry("avc1", record);

    return box;
  }

  static enum Type
  {
    VIDEO, AUDIO;
  }
}