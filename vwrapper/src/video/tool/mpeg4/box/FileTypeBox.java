package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class FileTypeBox extends Box
{
  private int majorBrand_;
  private int minorVersion_;
  private List<Integer> compatibleBrands_;

  public void setMajorBrand(String brand)
  {
    this.majorBrand_ = Box.getTypeFromString(brand);
  }

  public void setMinorVersion(int version) {
    this.minorVersion_ = version;
  }

  public void addCompatiblesBrand(String brand) {
    if (this.compatibleBrands_ == null) {
      this.compatibleBrands_ = new LinkedList();
    }
    this.compatibleBrands_.add(Integer.valueOf(Box.getTypeFromString(brand)));
    setSize((int)(getSize() + 4L));
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\nMajor Brand: ").append(Box.getTypeString(this.majorBrand_));
    ret.append("\nMinor Version: ").append(Integer.toString(this.minorVersion_));
    ret.append("\nCompatible Brands: ");
    if (this.compatibleBrands_ != null) {
      for (Iterator localIterator = this.compatibleBrands_.iterator(); localIterator.hasNext(); ) { int brand = ((Integer)localIterator.next()).intValue();
        ret.append(Box.getTypeString(brand)).append(";");
      }
    }
    return ret.toString();
  }

  protected void parseBox(InputStream in) throws BoxException
  {
    ByteBuffer buff = ByteBuffer.allocate((int)getSize() - super.getBoxSize());
    try {
      int ret = in.read(buff.array());
      if (ret != buff.capacity()) {
        throw new BoxException();
      }
      this.majorBrand_ = buff.getInt();
      this.minorVersion_ = buff.getInt();
      this.compatibleBrands_ = new ArrayList((ret - 8) / 4);
      for (int i = 0; i < (ret - 8) / 4; i++)
        this.compatibleBrands_.add(Integer.valueOf(buff.getInt()));
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new BoxException();
    }int ret;
  }

  public void write(OutputStream out) {
    super.write(out);
    ByteBuffer buff = ByteBuffer.allocate(8 + 4 * this.compatibleBrands_.size());
    buff.putInt(this.majorBrand_);
    buff.putInt(this.minorVersion_);
    for (Integer brand : this.compatibleBrands_)
      buff.putInt(brand.intValue());
    try
    {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static FileTypeBox build() {
    FileTypeBox box = new FileTypeBox();
    box.setSize(box.getBoxSize() + 8);
    return box;
  }
}