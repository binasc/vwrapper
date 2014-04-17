package video.tool.mpeg4;

import java.util.LinkedList;
import java.util.List;
import video.tool.mpeg4.box.Box;
import video.tool.mpeg4.box.FileTypeBox;
import video.tool.mpeg4.box.MediaDataBox;
import video.tool.mpeg4.box.MovieBox;

public class Mp4File
{
  private FileTypeBox ftyp_;
  private MovieBox moov_;
  private MediaDataBox mdat_;
  private List<Box> oths_ = new LinkedList();

  public FileTypeBox getFileTypeBox() {
    return this.ftyp_;
  }

  public void setFileTypeBox(FileTypeBox ftyp) {
    this.ftyp_ = ftyp;
  }

  public MovieBox getMovieBox() {
    return this.moov_;
  }

  public void setMovieBox(MovieBox moov) {
    this.moov_ = moov;
  }

  public MediaDataBox getMediaDataBox() {
    return this.mdat_;
  }

  public void setMediaDataBox(MediaDataBox mdat) {
    this.mdat_ = mdat;
  }

  public List<Box> getOtherBoxes() {
    return this.oths_;
  }

  public void addOtherBox(Box box) {
    this.oths_.add(box);
  }

  public static Mp4File build() {
    Mp4File file = new Mp4File();
    file.ftyp_ = FileTypeBox.build();
    file.ftyp_.setMajorBrand("mp42");
    file.ftyp_.addCompatiblesBrand("isom");
    file.ftyp_.addCompatiblesBrand("mp42");

    file.moov_ = MovieBox.build();

    return file;
  }
}