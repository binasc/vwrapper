package video.tool.flv.tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ScriptDataTag extends InnerTag
{
  private ScriptValue name_;
  private ScriptValue value_;

  public ScriptDataTag()
  {
    this.name_ = null;
    this.value_ = null;
  }

  public int size()
  {
    return this.name_.toByteArray().length + this.value_.toByteArray().length;
  }

  public void setName(String name) {
    this.name_ = new ScriptValue(name);
  }

  public String getName() {
    return this.name_.getString();
  }

  public void setProperties(List<ScriptProperty> properties) {
    this.value_ = new ScriptValue(true, properties);
  }

  public List<ScriptProperty> getProperties() {
    return this.value_.getValueAsECMAArray();
  }

  public byte[] toByteArray() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      out.write(this.name_.toByteArray());
      out.write(this.value_.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toByteArray();
  }

  private List<ScriptValue> nextScriptStrictArray(InputStream in, int len) throws TagException, IOException {
    List list = new LinkedList();
    for (int i = 0; i < len; i++) {
      ScriptValue value = nextScriptValue(in);
      if (value == null) {
        throw new TagException();
      }
      list.add(value);
    }
    return list;
  }

  private List<ScriptValue> nextScriptStrictArray(InputStream in) throws TagException, IOException
  {
    byte[] buff = new byte[4];
    int ret = in.read(buff, 0, 4);
    if (ret != 4) {
      throw new TagException();
    }
    int len = ByteBuffer.wrap(buff, 0, 4).getInt();
    return nextScriptStrictArray(in, len);
  }

  private String nextScriptString(InputStream in) throws TagException, IOException
  {
    byte[] buff = new byte[2];

    int ret = in.read(buff, 0, 2);

    if (ret == -1) {
      return null;
    }
    if (ret != 2) {
      throw new TagException();
    }
    int len = ByteBuffer.wrap(buff, 0, 2).getShort();
    if (len == 0) {
      return new String("");
    }
    byte[] string = new byte[len];
    ret = in.read(string, 0, len);
    if (ret != len) {
      throw new TagException();
    }
    return new String(string, 0, len, "US-ASCII");
  }

  private List<ScriptProperty> nextScriptObject(InputStream in) throws TagException, IOException
  {
    byte[] buff = new byte[1];
    List list = new LinkedList();
    while (true) {
      String str = nextScriptString(in);

      if (str == null) {
        break;
      }
      if (str.equals("")) {
        int ret = in.read(buff, 0, 1);
        if (ret != 1) {
          throw new TagException();
        }
        if (buff[0] != ScriptValue.Type.OEM.value()) {
          break;
        }
        if (buff[0] != ScriptValue.Type.Undefined.value())
        {
          break;
        }

        throw new TagException();
      }

      ScriptProperty property = new ScriptProperty();
      property.name = str;
      property.value = nextScriptValue(in);
      if (property.value == null) {
        throw new TagException();
      }
      list.add(property);
    }
    return list;
  }

  private List<ScriptProperty> nextScriptECMAArray(InputStream in) throws TagException, IOException
  {
    byte[] buff = new byte[4];
    int ret = in.read(buff, 0, 4);
    if (ret != 4) {
      throw new TagException();
    }

    int len = ByteBuffer.wrap(buff, 0, 4).getInt();
    return nextScriptObject(in);
  }

  private ScriptValue nextScriptValue(InputStream in) throws TagException, IOException
  {
    byte[] buff = new byte[8];

    int ret = in.read(buff, 0, 1);
    if (ret != 1) {
      throw new TagException();
    }
    ScriptValue value = new ScriptValue(buff[0]);

    switch (value.getType()) {
    case Number:
      ret = in.read(buff, 0, 8);
      if (ret != 8) {
        throw new TagException();
      }
      value.setValue(Double.valueOf(ByteBuffer.wrap(buff, 0, 8).getDouble()));
      break;
    case Boolean:
      ret = in.read(buff, 0, 1);
      if (ret != 1) {
        throw new TagException();
      }
      value.setValue(Boolean.valueOf(buff[0] != 0));
      break;
    case String:
      value.setValue(nextScriptString(in));
      if (value.getString() == null) {
        throw new TagException();
      }
      break;
    case Object:
      value.setValue(nextScriptObject(in));
      break;
    case ECMAArray:
      value.setValue(nextScriptECMAArray(in));
      break;
    case StrictArray:
      value.setValue(nextScriptStrictArray(in));
      break;
    default:
      System.out.println("unsupport");
    }

    return value;
  }

  public String toString() {
    return this.name_.toString() + this.value_.toString();
  }

  public static ScriptDataTag parseScriptDataTag(InputStream in) throws TagException {
    ScriptDataTag tag = new ScriptDataTag();
    try {
      tag.name_ = tag.nextScriptValue(in);
      if (tag.name_.getType() != ScriptValue.Type.String) {
        throw new TagException();
      }
      tag.value_ = tag.nextScriptValue(in);
      if (tag.value_.getType() != ScriptValue.Type.ECMAArray) {
        throw new TagException();
      }

      return tag;
    } catch (IOException e) {
      e.printStackTrace();
    }throw new TagException();
  }
}