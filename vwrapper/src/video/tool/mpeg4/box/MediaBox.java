package video.tool.mpeg4.box;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class MediaBox extends Box
{
  private MediaHeaderBox mdhd_;
  private HandlerBox hdlr_;
  private MediaInformationBox minf_;
  private List<Box> oths_ = new LinkedList();

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.mdhd_.toString());
    ret.append("\n").append(this.hdlr_.toString());
    ret.append("\n").append(this.minf_.toString());
    if (this.oths_.size() > 0) {
      ret.append("\n<others>");
      for (Box box : this.oths_) {
        ret.append("\n").append(box.toString());
      }
      ret.append("\n</others>");
    }
    return ret.toString();
  }

  public MediaHeaderBox getMediaDataBox() {
    return this.mdhd_;
  }

  public HandlerBox getHandlerBox() {
    return this.hdlr_;
  }

  public MediaInformationBox getMediaInformationBox() {
    return this.minf_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("mdhd")) {
        this.mdhd_ = ((MediaHeaderBox)box);
      }
      else if (box.getType().equals("hdlr")) {
        this.hdlr_ = ((HandlerBox)box);
      }
      else if (box.getType().equals("minf")) {
        this.minf_ = ((MediaInformationBox)box);
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
    this.mdhd_.write(out);
    this.hdlr_.write(out);
    this.minf_.write(out);
  }

  public static MediaBox build(TrackBox.Type type) {
    MediaBox box = new MediaBox();
    box.mdhd_ = MediaHeaderBox.build();
    box.mdhd_.setParent(box);
    box.hdlr_ = HandlerBox.build(type);
    box.hdlr_.setParent(box);
    box.minf_ = MediaInformationBox.build(type);
    box.minf_.setParent(box);
    box.setSize((int)(box.getBoxSize() + box.mdhd_.getSize() + box.hdlr_.getSize() + box.minf_.getSize()));
    return box;
  }
}