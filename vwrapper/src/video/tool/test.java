package video.tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import video.tool.flv.FlvDecoder;
import video.tool.flv.tag.Tag;
import video.tool.flv.tag.TagException;
import video.tool.flv.tag.VideoTag;

public class test {

	public static void test_decoder_basic(String[] args) throws TagException, IOException {

      FlvDecoder decoder = new FlvDecoder();

      File file = new File("/Users/jeff/mp4_tools/110171/1/flv/0.flv");
      BufferedInputStream fin = new BufferedInputStream(new FileInputStream(file));

      decoder.beginDecode(fin);
      Tag tag;
      while ((tag = decoder.nextTag(fin)) != null)
      {
        if ((tag.getType() == Tag.Type.VIDEO) && (tag.getVideoTag().getFrameType() == VideoTag.Frame.KeyFrame)) {
        	System.out.println("keyframe");
        }
      }

      fin.close();
	}

	public static void main(String[] args) {
		try {
			test_decoder_basic(args);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
