package video.tool.mpeg4.box;

import java.io.InputStream;
import video.tool.mpeg4.AVCDecoderConfigurationRecord;

public class AVCSampleEntry extends VisualSampleEntry
{
  private AVCConfigurationBox avcC_;
  private MPEG4BitRateBox btrt_;
  private MPEG4ExtensionDescriptorsBox m4ds_;

  public AVCSampleEntry()
  {
  }

  public AVCSampleEntry(AVCDecoderConfigurationRecord record)
  {
    this.avcC_ = new AVCConfigurationBox(record);
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.avcC_.toString());
    if (this.btrt_ != null) {
      ret.append("\n").append(this.btrt_.toString());
    }
    if (this.m4ds_ != null) {
      ret.append("\n").append(this.m4ds_.toString());
    }
    return ret.toString();
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("avcC")) {
        this.avcC_ = ((AVCConfigurationBox)box);
      }
      else if (box.getType().equals("btrt")) {
        this.btrt_ = ((MPEG4BitRateBox)box);
      }
      else if (box.getType().equals("m4ds")) {
        this.m4ds_ = ((MPEG4ExtensionDescriptorsBox)box);
      }
      else {
        throw new BoxException();
      }
      size = (int)(size + box.getSize());
      if (size > getSize()) {
        throw new BoxException();
      }
      if (size == getSize())
        break;
    }
  }
}