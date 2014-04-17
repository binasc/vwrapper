package video.tool.mpeg4.box;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class Box
{
  private Box parent_;
  private int size_;
  private int type_;
  private long largeSize_;
  private static Map<Class<?>, String> reverseTypeMap_ = null;

  private static final Map<String, Class<?>> typeMap_ = new HashMap() { } ;

  public Box()
  {
    if (reverseTypeMap_ == null) {
      reverseTypeMap_ = new HashMap();
      for (String type : typeMap_.keySet()) {
        reverseTypeMap_.put((Class)typeMap_.get(type), type);
      }
    }
    this.type_ = getTypeFromString((String)reverseTypeMap_.get(getClass()));
    this.size_ = 8;
  }

  public static int getTypeFromString(String type) {
    if (type == null)
      return 0;
    try
    {
      return ByteBuffer.wrap(type.getBytes("US-ASCII"), 0, 4).getInt();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }return 0;
  }

  public static String getTypeString(int type)
  {
    try
    {
      return new String(ByteBuffer.allocate(4).putInt(type).array(), 0, 4, "US-ASCII");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }return "";
  }

  protected void setBox(Box box)
  {
    this.size_ = box.size_;
    this.type_ = box.type_;
    this.largeSize_ = box.largeSize_;
  }

  public Box getParent() {
    return this.parent_;
  }

  public void setParent(Box box) {
    this.parent_ = box;
  }

  public int getBoxSize() {
    return this.size_ == 1 ? 16 : 8;
  }

  public void setSize(int size) {
    this.size_ = size;
  }

  public long getSize()
  {
    if (this.size_ == 0) {
      return 0L;
    }
    if (this.size_ == 1) {
      return this.largeSize_;
    }
    if (this.size_ < 0) {
      return (this.size_ & 0x7FFFFFFF) + 2147483648L;
    }
    return this.size_;
  }

  public String getType() {
    return getTypeString(this.type_);
  }

  public String toString() {
    return "<" + getType() + ": 0x" + Long.toHexString(getSize()) + ">";
  }

  private int parseBasicBox(InputStream in)
    throws BoxException
  {
    ByteBuffer buff = ByteBuffer.allocate(8);
    try {
      int ret = in.read(buff.array());
      if (ret == -1) {
        return -1;
      }
      if (ret != 8) {
        throw new BoxException();
      }
      this.size_ = buff.getInt();
      this.type_ = buff.getInt();
      if (this.size_ == 1) {
        buff.rewind();
        ret = in.read(buff.array());
        if (ret != 8) {
          throw new BoxException();
        }
        this.largeSize_ = buff.getLong();
      }

      return 0;
    } catch (IOException e) {
      e.printStackTrace();
    }throw new BoxException();
  }

  void parseBox(InputStream in)
    throws BoxException
  {
  }

  public static Box nextBox(InputStream in)
    throws BoxException
  {
    Box box = null; Box basicBox = new Box();
    if (basicBox.parseBasicBox(in) == -1) {
      return null;
    }

    if (typeMap_.containsKey(basicBox.getType())) {
      Class clz = (Class)typeMap_.get(basicBox.getType());
      try {
        box = (Box)clz.newInstance();

        box.setBox(basicBox);
        box.parseBox(in);
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
    else {
      box = new UnknownBox();
      box.setBox(basicBox);
      box.parseBox(in);
    }

    return box;
  }

  public void write(OutputStream out) {
    ByteBuffer buff = ByteBuffer.allocate(this.size_ == 1 ? 16 : 8);
    buff.putInt(this.size_);
    buff.putInt(this.type_);
    if (this.size_ == 1)
      buff.putLong(this.largeSize_);
    try
    {
      out.write(buff.array());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}