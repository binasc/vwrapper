package video.tool.mpeg4.box;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class SampleTableBox extends Box
{
  SampleDescriptionBox stsd_;
  TimeToSampleBox stts_;
  SampleToChunkBox stsc_;
  SampleSizeBox stsz_;
  ChunkOffsetBox stco_;
  private List<Box> oths_ = new LinkedList();

  public TimeToSampleBox getTimeToSampleBox()
  {
    return this.stts_;
  }

  public SampleToChunkBox getSampleToChunkBox() {
    return this.stsc_;
  }

  public SampleSizeBox getSampleSizeBox() {
    return this.stsz_;
  }

  public ChunkOffsetBox getChunkOffsetBox() {
    return this.stco_;
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.stsd_.toString());
    ret.append("\n").append(this.stts_.toString());
    ret.append("\n").append(this.stsc_.toString());
    ret.append("\n").append(this.stsz_.toString());
    ret.append("\n").append(this.stco_.toString());
    if (this.oths_.size() > 0) {
      ret.append("\n<others>");
      for (Box box : this.oths_) {
        ret.append("\n").append(box.toString());
      }
      ret.append("\n</others>");
    }
    return ret.toString();
  }

  public SampleDescriptionBox getSampleDescriptionBox() {
    return this.stsd_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("stsd")) {
        this.stsd_ = ((SampleDescriptionBox)box);
      }
      else if (box.getType().equals("stts")) {
        this.stts_ = ((TimeToSampleBox)box);
      }
      else if (box.getType().equals("stsc")) {
        this.stsc_ = ((SampleToChunkBox)box);
      }
      else if (box.getType().equals("stsz")) {
        this.stsz_ = ((SampleSizeBox)box);
      }
      else if (box.getType().equals("stco")) {
        this.stco_ = ((ChunkOffsetBox)box);
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
    this.stsd_.write(out);
    this.stts_.write(out);
    this.stsc_.write(out);
    this.stsz_.write(out);
    this.stco_.write(out);
  }

  public static SampleTableBox build() {
    SampleTableBox box = new SampleTableBox();
    box.stsd_ = new SampleDescriptionBox();
    box.stsd_.setParent(box);
    box.stts_ = new TimeToSampleBox();
    box.stts_.setParent(box);
    box.stsc_ = new SampleToChunkBox();
    box.stsc_.setParent(box);
    box.stsz_ = new SampleSizeBox();
    box.stsz_.setParent(box);
    box.stco_ = new ChunkOffsetBox();
    box.stco_.setParent(box);
    box.setSize(
      (int)(box.getBoxSize() + 
      box.stsd_.getSize() + 
      box.stts_.getSize() + 
      box.stsc_.getSize() + 
      box.stsz_.getSize() + 
      box.stco_.getSize()));
    return box;
  }
}