package video.tool.mpeg4.box;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class DataInformationBox extends Box
{
  Box dataReferenceBox_;
  private List<Box> oths_ = new LinkedList();

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.dataReferenceBox_.toString());
    if (this.oths_.size() > 0) {
      ret.append("\n<others>");
      for (Box box : this.oths_) {
        ret.append("\n").append(box.toString());
      }
      ret.append("\n</others>");
    }
    return ret.toString();
  }

  protected void parseBox(InputStream in) throws BoxException {
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("url ")) {
        this.dataReferenceBox_ = box;
      }
      else if (box.getType().equals("urn ")) {
        this.dataReferenceBox_ = box;
      }
      else if (box.getType().equals("dref")) {
        this.dataReferenceBox_ = box;
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
    this.dataReferenceBox_.write(out);
  }

  public static DataInformationBox build() {
    DataInformationBox box = new DataInformationBox();
    box.dataReferenceBox_ = DataEntryUrlBox.build();
    box.dataReferenceBox_.setParent(box);
    box.setSize((int)(box.getBoxSize() + box.dataReferenceBox_.getSize()));
    return box;
  }
}