package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import video.tool.mpeg4.AVCDecoderConfigurationRecord;

public class AVCConfigurationBox extends Box
{
  AVCDecoderConfigurationRecord AVCConfig_;

  public AVCConfigurationBox()
  {
    this.AVCConfig_ = new AVCDecoderConfigurationRecord();
  }

  public AVCConfigurationBox(AVCDecoderConfigurationRecord record) {
    this.AVCConfig_ = new AVCDecoderConfigurationRecord(record);
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append(this.AVCConfig_.toString());
    return ret.toString();
  }

  public byte getLengthSize() {
    return this.AVCConfig_.getLengthSize();
  }

  public byte getNumOfSequenceParameterSets() {
    return this.AVCConfig_.getNumOfSequenceParameterSets();
  }

  public byte getNumOfPictureParameterSets() {
    return this.AVCConfig_.getNumOfPictureParameterSets();
  }

  protected void parseBox(InputStream in) throws BoxException {
    super.parseBox(in);
    ByteBuffer buff = ByteBuffer.allocate((int)getSize() - getBoxSize());
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      this.AVCConfig_.parse(buff);
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}