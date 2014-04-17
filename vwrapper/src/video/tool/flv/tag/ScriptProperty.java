package video.tool.flv.tag;

public class ScriptProperty
{
  public String name;
  public ScriptValue value;

  public ScriptProperty()
  {
    this.name = null;
    this.value = null;
  }

  public ScriptProperty(String n, ScriptValue v) {
    this.name = n;
    this.value = v;
  }

  public ScriptProperty(ScriptProperty val) {
    this.name = null;
    if (val.name != null)
      this.name = new String(val.name);
    this.value = null;
    if (val.value != null)
      this.value = new ScriptValue(val.value);
  }
}