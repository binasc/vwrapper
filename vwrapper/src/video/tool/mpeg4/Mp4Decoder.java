package video.tool.mpeg4;

import java.io.InputStream;
import video.tool.mpeg4.box.Box;
import video.tool.mpeg4.box.BoxException;
import video.tool.mpeg4.box.FileTypeBox;
import video.tool.mpeg4.box.MediaDataBox;
import video.tool.mpeg4.box.MovieBox;

public class Mp4Decoder
{
  Mp4File file_ = new Mp4File();

  public Mp4File getMp4File() {
    return this.file_;
  }

  public Box nextBox(InputStream in) throws BoxException {
    Box box = Box.nextBox(in);
    if (box != null) {
      if (box.getType().equals("ftyp")) {
        this.file_.setFileTypeBox((FileTypeBox)box);
      }
      else if (box.getType().equals("moov")) {
        this.file_.setMovieBox((MovieBox)box);
      }
      else if (box.getType().equals("mdat")) {
        this.file_.setMediaDataBox((MediaDataBox)box);
      }
      else {
        this.file_.addOtherBox(box);
      }
    }
    return box;
  }
}