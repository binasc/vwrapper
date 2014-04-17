package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class CleanApertureBox extends Box
{
  private int cleanApertureWidthN_;
  private int cleanApertureWidthD_;
  private int cleanApertureHeightN_;
  private int cleanApertureHeightD_;
  private int horizOffN_;
  private int horizOffD_;
  private int vertOffN_;
  private int vertOffD_;

  public String toString()
  {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nWidthN: ").append(this.cleanApertureWidthN_);
    ret.append("\nWidthD: ").append(this.cleanApertureWidthD_);
    ret.append("\nHeightN: ").append(this.cleanApertureHeightN_);
    ret.append("\nHeightD: ").append(this.cleanApertureHeightD_);
    ret.append("\nhorizOffN: ").append(this.horizOffN_);
    ret.append("\nhorizOffD: ").append(this.horizOffD_);
    ret.append("\nvertOffN: ").append(this.vertOffN_);
    ret.append("\nvertOffD: ").append(this.vertOffD_);
    return ret.toString();
  }

  protected void parseBox(InputStream in)
    throws BoxException
  {
    ByteBuffer buff = ByteBuffer.allocate(32);
    try
    {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      this.cleanApertureWidthN_ = buff.getInt();
      this.cleanApertureWidthD_ = buff.getInt();

      this.cleanApertureHeightN_ = buff.getInt();
      this.cleanApertureHeightD_ = buff.getInt();

      this.horizOffN_ = buff.getInt();
      this.horizOffD_ = buff.getInt();

      this.vertOffN_ = buff.getInt();
      this.vertOffD_ = buff.getInt();
    } catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }
    int ret;
  }
}