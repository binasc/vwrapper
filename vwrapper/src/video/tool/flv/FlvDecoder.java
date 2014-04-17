package video.tool.flv;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import video.tool.flv.tag.ScriptDataTag;
import video.tool.flv.tag.Tag;
import video.tool.flv.tag.Tag.Type;
import video.tool.flv.tag.TagException;
import video.tool.flv.tag.VideoTag;
import video.tool.flv.tag.VideoTag.Codec;

public class FlvDecoder
{
  public FlvFile flvFile;

  public int getDuration()
  {
    return ((Tag)this.flvFile.tags.get(this.flvFile.tags.size() - 1)).getTimestamp();
  }

  public Tag nextTag(InputStream in) throws TagException {
    Tag tag = Tag.parseFlvTag(in);
    if (tag == null) {
      return null;
    }
    if ((tag.getType() == Tag.Type.SCRIPT) && (tag.getScriptDataTag().getName().equals("onMetaData"))) {
      this.flvFile.onMetaDataTag = tag;
    }
    else if ((tag.getType() == Tag.Type.VIDEO) && (tag.getVideoTag().getCodecId() == VideoTag.Codec.AVC) && (tag.getVideoTag().AVCPacketType == 0)) {
      this.flvFile.AVCDecoderConfigurationRecordTag = tag;
    }
    else {
      this.flvFile.tags.add(tag);
    }

    return tag;
  }

  public void beginDecode(InputStream in) throws TagException {
    this.flvFile = new FlvFile();
    this.flvFile.header = FlvHeader.parseFlvHeader(in);
    this.flvFile.tags = new LinkedList();
  }
}