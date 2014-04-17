package video.tool.flv.tag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ScriptValue
{
  private int type_;
  private Object value_;

  public ScriptValue(ScriptValue val)
  {
    this.type_ = val.type_;
    switch (type_) {
    case 0:
      this.value_ = new Double(val.getDouble().doubleValue());
      break;
    case 1:
      this.value_ = new Boolean(val.getBoolean().booleanValue());
      break;
    case 2:
      this.value_ = new String(val.getString());
      break;
    case 3:
    case 8: {
      List<ScriptProperty> list = val.getValueAsECMAArray();
      if (list != null)
      {
        this.value_ = new LinkedList<ScriptProperty>();
        for (ScriptProperty sp : list)
          getValueAsECMAArray().add(new ScriptProperty(sp));
      }
      break;
    }
    case 10: {
      List<ScriptValue> list = val.getValueAsStrictArray();
      if (list != null)
      {
        this.value_ = new LinkedList<ScriptValue>();
        for (ScriptValue sv : list)
          getValueAsStrictArray().add(new ScriptValue(sv));
      }
      break;
    }
    default:
      System.out.println("unsupport");
    }
  }

  public ScriptValue()
  {
    this.type_ = Type.Unknown.value();
    this.value_ = null;
  }

  public ScriptValue(int type) {
    this.type_ = type;
    this.value_ = null;
  }

  public ScriptValue(double d) {
    this.type_ = Type.Number.value();
    this.value_ = Double.valueOf(d);
  }

  public ScriptValue(String s) {
    this.type_ = Type.String.value();
    this.value_ = s;
  }

  public ScriptValue(List<ScriptValue> list) {
    this.type_ = Type.StrictArray.value();
    this.value_ = list;
  }

  public ScriptValue(boolean isArray, List<ScriptProperty> list) {
    this.type_ = (isArray ? Type.ECMAArray.value() : Type.Object.value());
    this.value_ = list;
  }

  public Type getType() {
    return Type.valueOf(this.type_);
  }

  public void setValue(Object val) {
    this.value_ = val;
  }

  public Object getValue() {
    return this.value_;
  }

  public Double getDouble() {
    if (this.type_ == Type.Number.value()) {
      return (Double)this.value_;
    }
    return null;
  }

  public Boolean getBoolean() {
    if (this.type_ == Type.Boolean.value()) {
      return (Boolean)this.value_;
    }
    return null;
  }

  public String getString() {
    if (this.type_ == Type.String.value()) {
      return (String)this.value_;
    }
    return null;
  }

  public List<ScriptProperty> getValueAsECMAArray()
  {
    if ((this.type_ == Type.ECMAArray.value()) || (this.type_ == Type.Object.value())) {
      return (List)getValue();
    }
    return null;
  }

  public List<ScriptValue> getValueAsStrictArray()
  {
    if (this.type_ == Type.StrictArray.value()) {
      return (List)getValue();
    }
    return null;
  }

  private void toByteArray(ByteArrayOutputStream out) throws IOException {
    byte[] buff = new byte[3];
    buff[0] = ((byte)this.type_);
    out.write(buff, 0, 1);

    switch (getType()) {
    case Number:
      out.write(ByteBuffer.allocate(8).putDouble(((Double)this.value_).doubleValue()).array());
      break;
    case Boolean:
      buff[0] = ((byte)(((Boolean)this.value_).booleanValue() ? 1 : 0));
      out.write(buff, 0, 1);
      break;
    case String:
      int len = ((String)this.value_).length();
      out.write(ByteBuffer.allocate(2).putShort((short)len).array());
      out.write(((String)this.value_).getBytes("US-ASCII"));
      break;
    case ECMAArray: {
      List<ScriptProperty> list = (List<ScriptProperty>) this.value_;
      out.write(ByteBuffer.allocate(4).putInt(list.size()).array());
    }
    case Object: {
      List<ScriptProperty> list = (List<ScriptProperty>) this.value_;
      for (ScriptProperty property : list) {
        out.write(ByteBuffer.allocate(2).putShort((short)property.name.length()).array());
        out.write(property.name.getBytes("US-ASCII"));
        property.value.toByteArray(out);
      }
      buff[0] = 0;
      buff[1] = 0;
      buff[2] = ((byte)Type.OEM.value());
      out.write(buff, 0, 3);
      break;
    }
    case StrictArray: {
      List<ScriptValue> list = (List<ScriptValue>) this.value_;
      out.write(ByteBuffer.allocate(4).putInt(list.size()).array());
      for (ScriptValue value : list) {
        value.toByteArray(out);
      }
      break;
    }
    default:
      System.out.println("unsupport");
    }
  }

  public byte[] toByteArray()
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      toByteArray(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return out.toByteArray();
  }

  public String toString() {
	  switch (getType()) {
    case Number:
      return ((Double)this.value_).toString();
    case Boolean:
      return ((Boolean)this.value_).toString();
    case String:
      return (String)this.value_;
    case Object:
    case ECMAArray: {
      String ret = getType() == Type.Object ? "<Object> " : "<ECMAArray> ";

      List<ScriptProperty> list = (List)this.value_;
      for (ScriptProperty property : list) {
        ret = ret + property.name + ": ";
        if ((property.value.type_ != Type.Object.value()) && 
          (property.value.type_ != Type.ECMAArray.value()) && 
          (property.value.type_ != Type.StrictArray.value())) {
          ret = ret + property.value.toString() + "\n";
        }
        else {
          ret = ret + "\n" + property.value.toString();
        }
      }
      return ret;
	  }
    case StrictArray:
      List<ScriptValue> list = (List)this.value_;
      String ret = "<Array> Length: " + String.valueOf(list.size()) + "\n";
      for (ScriptValue value : list) {
        ret = ret + value.toString();
        if ((value.type_ != Type.Object.value()) && 
          (value.type_ != Type.ECMAArray.value()) && 
          (value.type_ != Type.StrictArray.value())) {
          ret = ret + "\n";
        }
      }
      return ret;
    }

    System.out.println("unsupport");

    return null;
  }

  static enum Type
  {
    Unknown(-1), 
    Number(0), Boolean(1), String(2), Object(3), MovieClip(4), 
    Null(5), Undefined(6), Reference(7), ECMAArray(8), 
    OEM(9), StrictArray(10), Date(11), LongString(12);

    private int value_ = -1;

    private Type(int value) {
      if ((value < 0) || (value > 12)) {
        this.value_ = -1;
      }
      else
        this.value_ = value;
    }

    public static Type valueOf(int value)
    {
      switch (value) {
      case 0:
        return Number;
      case 1:
        return Boolean;
      case 2:
        return String;
      case 3:
        return Object;
      case 4:
        return MovieClip;
      case 5:
        return Null;
      case 6:
        return Undefined;
      case 7:
        return Reference;
      case 8:
        return ECMAArray;
      case 9:
        return OEM;
      case 10:
        return StrictArray;
      case 11:
        return Date;
      case 12:
        return LongString;
      }
      return Unknown;
    }

    public int value() {
      return this.value_;
    }
  }
}