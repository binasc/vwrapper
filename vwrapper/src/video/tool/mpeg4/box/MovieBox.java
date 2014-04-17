package video.tool.mpeg4.box;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MovieBox extends Box
{
  private MovieHeaderBox mvhd_;
  private List<TrackBox> traks_ = new LinkedList();
  private List<Box> oths_ = new LinkedList();

  public void addTrack(TrackBox track)
  {
    track.setParent(this);
    track.getTrackHeaderBox().setTrackID(this.mvhd_.getNextTrackID());
    this.mvhd_.setNextTrackID(this.mvhd_.getNextTrackID() + 1);
    this.traks_.add(track);
    setSize((int)(getSize() + track.getSize()));
  }

  public String toString() {
    StringBuilder ret = new StringBuilder(super.toString());
    ret.append("\n").append(this.mvhd_.toString());
    ret.append("\n<tracks>");
    for (TrackBox box : this.traks_) {
      ret.append("\n").append(box.toString());
    }
    ret.append("\n</tracks>");
    if (this.oths_.size() > 0) {
      ret.append("\n<others>");
      for (Box box : this.oths_) {
        ret.append("\n").append(box.toString());
      }
      ret.append("\n</others>");
    }
    return ret.toString();
  }

  public MovieHeaderBox getMovieHeaderBox() {
    return this.mvhd_;
  }

  protected void parseBox(InputStream in) throws BoxException {
    int size = getBoxSize();
    Box box;
    while ((box = Box.nextBox(in)) != null)
    {
      box.setParent(this);
      if (box.getType().equals("mvhd")) {
        this.mvhd_ = ((MovieHeaderBox)box);
      }
      else if (box.getType().equals("trak")) {
        this.traks_.add((TrackBox)box);
      }
      else {
        this.oths_.add(box);
      }
      size = (int)(size + box.getSize());
      if (size > getSize()) {
        throw new BoxException();
      }
      if (size == getSize())
        break;
    }
  }

  public void write(OutputStream out)
  {
    super.write(out);
    this.mvhd_.write(out);
    for (TrackBox track : this.traks_)
      track.write(out);
  }

  public static MovieBox build()
  {
    MovieBox box = new MovieBox();
    box.mvhd_ = MovieHeaderBox.build();
    box.mvhd_.setParent(box);
    box.mvhd_.setCreateTime(new Date());
    box.mvhd_.setModificationTime(new Date());
    box.setSize((int)(box.getBoxSize() + box.mvhd_.getSize()));

    return box;
  }
}