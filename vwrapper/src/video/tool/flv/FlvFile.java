package video.tool.flv;

import java.util.List;
import video.tool.flv.tag.Tag;

public class FlvFile
{
  public FlvHeader header;
  public Tag onMetaDataTag;
  public Tag AVCDecoderConfigurationRecordTag;
  public List<Tag> tags;
}