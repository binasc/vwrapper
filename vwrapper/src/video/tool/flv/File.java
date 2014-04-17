package video.tool.flv;

import java.util.List;
import video.tool.flv.tag.Tag;

public class File
{
	public Header header;
	public Tag onMetaDataTag;
	public Tag AVCDecoderConfigurationRecordTag;
	public List<Tag> tags;
}