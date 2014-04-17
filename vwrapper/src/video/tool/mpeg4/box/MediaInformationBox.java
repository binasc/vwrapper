package video.tool.mpeg4.box;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class MediaInformationBox extends Box
{
  SampleTableBox stbl_;
  DataInformationBox dinf_;
  Box xmhd_;
  private List<Box> oths_ = new LinkedList();

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.stbl_.toString());
    ret.append("\n").append(this.dinf_.toString());
    ret.append("\n").append(this.xmhd_.toString());
    if (this.oths_.size() > 0) {
      ret.append("\n<others>");
      for (Box box : this.oths_) {
        ret.append("\n").append(box.toString());
      }
      ret.append("\n</others>");
    }
    return ret.toString();
  }

  public SampleTableBox getSampleTableBox() {
    return this.stbl_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("stbl")) {
        this.stbl_ = ((SampleTableBox)box);
      }
      else if (box.getType().equals("dinf")) {
        this.dinf_ = ((DataInformationBox)box);
      }
      else if ((box.getType().equals("vmhd")) || 
        (box.getType().equals("smhd")) || 
        (box.getType().equals("hmhd")) || 
        (box.getType().equals("nmhd"))) {
        this.xmhd_ = box;
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
    this.stbl_.write(out);
    this.dinf_.write(out);
    this.xmhd_.write(out);
  }

  public static MediaInformationBox build(TrackBox.Type type) {
    MediaInformationBox box = new MediaInformationBox();
    box.stbl_ = SampleTableBox.build();
    box.stbl_.setParent(box);
    box.dinf_ = DataInformationBox.build();
    box.dinf_.setParent(box);
    switch (type) {
    case AUDIO:
      box.xmhd_ = VideoMediaHeaderBox.build();
      break;
    case VIDEO:
      box.xmhd_ = SoundMediaHeaderBox.build();
    }
    box.xmhd_
      .setParent(box);
    box.setSize((int)(box.getBoxSize() + box.stbl_.getSize() + box.dinf_.getSize() + box.xmhd_.getSize()));
    return box;
  }
}