package video.tool.mpeg4;

public class NumericHelper
{
  public static long getLongFromInt(int i)
  {
    if (i < 0) {
      return (i & 0x7FFFFFFF) + 2147483648L;
    }

    return i;
  }
}