package video.tool.flv.tag;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import video.tool.mpeg4.AVCDecoderConfigurationRecord;

public class VideoTag extends InnerTag
{
  public byte firstByte;
  public byte AVCPacketType;
  public int compositionTime;
  public AVCDecoderConfigurationRecord record;

  public Frame getFrameType()
  {
    int frameType = this.firstByte >>> 4 & 0xF;

    switch (frameType) {
    case 1:
      return Frame.KeyFrame;
    case 2:
      return Frame.InterFrame;
    case 3:
      return Frame.DisposableInterFrame;
    case 4:
      return Frame.GeneratedKeyFrame;
    case 5:
      return Frame.CommandFrame;
    }

    return Frame.Unknown;
  }

  public Codec getCodecId() {
    int codecId = this.firstByte & 0xF;

    switch (codecId) {
    case 2:
      return Codec.SorensonH263;
    case 3:
      return Codec.ScreenVideo;
    case 4:
      return Codec.On2VP6;
    case 5:
      return Codec.On2VP6WithAC;
    case 6:
      return Codec.ScreenVideoV2;
    case 7:
      return Codec.AVC;
    }

    return Codec.Unknown;
  }

  public static VideoTag parseVideoTag(InputStream in) throws TagException {
    VideoTag tag = new VideoTag();
    try {
      byte[] buff = new byte[4];
      int ret = in.read(buff, 0, 1);
      if (ret != 1) {
        throw new TagException();
      }
      tag.firstByte = buff[0];

      if (tag.getCodecId() == Codec.AVC) {
        ret = in.read(buff, 0, 4);
        if (ret != 4) {
          throw new TagException();
        }
        tag.AVCPacketType = buff[0];
        buff[0] = 0;
        tag.compositionTime = ByteBuffer.wrap(buff, 0, 4).getInt();
      }

      return tag;
    } catch (IOException e) {
      e.printStackTrace();
    }throw new TagException();
  }

  public int size()
  {
    if (getCodecId() == Codec.AVC) {
      return 5;
    }
    return 1;
  }

  public static enum Codec
  {
    Unknown, SorensonH263, ScreenVideo, On2VP6, On2VP6WithAC, ScreenVideoV2, AVC;
  }

  public static enum Frame
  {
    Unknown, KeyFrame, InterFrame, DisposableInterFrame, GeneratedKeyFrame, CommandFrame;
  }
}