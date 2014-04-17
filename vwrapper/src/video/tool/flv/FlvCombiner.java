package video.tool.flv;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import video.tool.flv.tag.ScriptDataTag;
import video.tool.flv.tag.ScriptProperty;
import video.tool.flv.tag.ScriptValue;
import video.tool.flv.tag.Tag;
import video.tool.flv.tag.Tag.Type;
import video.tool.flv.tag.TagException;
import video.tool.flv.tag.VideoTag;
import video.tool.flv.tag.VideoTag.Frame;

public class FlvCombiner
{
  private List<String> ins_ = new LinkedList();
  private List<FlvDecoder> decoders_ = new LinkedList();
  private int keyFramesCount_ = 0;

  public int getKeyFramesCount() {
    return this.keyFramesCount_;
  }

  public boolean addFileToCombine(String in) throws FileNotFoundException {
    long last = System.currentTimeMillis();
    try
    {
      FlvDecoder decoder = new FlvDecoder();

      BufferedInputStream fin = new BufferedInputStream(new FileInputStream(in));

      decoder.beginDecode(fin);
      Tag tag;
      while ((tag = decoder.nextTag(fin)) != null)
      {
        if ((tag.getType() == Tag.Type.VIDEO) && (tag.getVideoTag().getFrameType() == VideoTag.Frame.KeyFrame)) {
          this.keyFramesCount_ += 1;
        }
      }

      fin.close();
      this.ins_.add(in);
      this.decoders_.add(decoder);

      System.out.println("add: " + String.valueOf(System.currentTimeMillis() - last));

      return true;
    } catch (TagException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
    }return false;
  }

  public boolean addFilesToCombine(String[] ins) throws FileNotFoundException
  {
    for (String in : ins) {
      if (!addFileToCombine(in))
        return false;
    }
    return true;
  }

  private void flushByteBuffer(OutputStream out, ByteBuffer buff) throws IOException {
    out.write(buff.array(), 0, buff.position());
    buff.rewind();
  }

  private int writeWithByteBuffer(OutputStream out, ByteBuffer buff, InputStream in, int len) throws IOException {
    int byteCount = len;
    while (byteCount > 0) {
      int toWrite = buff.remaining();
      if (byteCount < toWrite)
        toWrite = byteCount;
      int ret = in.read(buff.array(), buff.position(), toWrite);
      if (ret != toWrite) {
        throw new IOException();
      }
      buff.position(buff.position() + toWrite);
      byteCount -= toWrite;
      if (buff.remaining() == 0) {
        out.write(buff.array());
        buff.rewind();
      }
    }

    return len;
  }

  private int writeWithByteBuffer(OutputStream out, ByteBuffer buff, byte[] src) throws IOException {
    int srcOffset = 0;
    int byteCount = src.length;
    while (byteCount > 0) {
      int toWrite = buff.remaining();
      if (byteCount < toWrite)
        toWrite = byteCount;
      buff.put(src, srcOffset, toWrite);
      byteCount -= toWrite;
      srcOffset += toWrite;
      if (buff.remaining() == 0) {
        out.write(buff.array());
        buff.rewind();
      }
    }

    return src.length;
  }

  public boolean writeToSingleFile(String out, Notifier n) throws FileNotFoundException {
    long last = System.currentTimeMillis();

    Header newHeader = new Header();
    newHeader.setHasAudio();
    newHeader.setHasVideo();

    if (((FlvDecoder)this.decoders_.get(0)).flvFile.onMetaDataTag == null) {
      return false;
    }
    Tag newMetaDataTag = new Tag();
    newMetaDataTag.setFilter(false);
    newMetaDataTag.setType(Tag.Type.SCRIPT);
    newMetaDataTag.setInnerTag(new ScriptDataTag());
    newMetaDataTag.getScriptDataTag().setName("onMetaData");
    newMetaDataTag.getScriptDataTag().setProperties(new LinkedList());

    ScriptValue svDuration = new ScriptValue(0.0D);
    ScriptValue filepositions = new ScriptValue(new LinkedList());
    ScriptValue times = new ScriptValue(new LinkedList());

    for (ScriptProperty sp : ((FlvDecoder)this.decoders_.get(0)).flvFile.onMetaDataTag.getScriptDataTag().getProperties())
    {
      ScriptProperty newSp;
      if (sp.name.equals("metadatacreator")) {
        newSp = new ScriptProperty();
        newSp.name = "metadatacreator";
        newSp.value = new ScriptValue("bilibili helper");
      }
      else if (sp.name.equals("duration")) {
        newSp = new ScriptProperty();
        newSp.name = "duration";
        newSp.value = svDuration;
      }
      else if (sp.name.equals("keyframes")) {
        newSp = new ScriptProperty();
        newSp.name = "keyframes";
        newSp.value = new ScriptValue(false, new LinkedList());
        for (int i = 0; i < this.keyFramesCount_; i++) {
          filepositions.getValueAsStrictArray().add(new ScriptValue(0.0D));
          times.getValueAsStrictArray().add(new ScriptValue(0.0D));
        }
        newSp.value.getValueAsECMAArray().add(new ScriptProperty("filepositions", filepositions));
        newSp.value.getValueAsECMAArray().add(new ScriptProperty("times", times));
      }
      else {
        newSp = new ScriptProperty(sp);
      }
      newMetaDataTag.getScriptDataTag().getProperties().add(newSp);
    }

    System.out.println("prepare: " + String.valueOf(System.currentTimeMillis() - last));
    last = System.currentTimeMillis();
    try
    {
      FileOutputStream fOut = new FileOutputStream(out);

      int filePosition = 0;
      byte[] intByte = new byte[4];
      ByteBuffer buff = ByteBuffer.allocate(1048576);

      filePosition += writeWithByteBuffer(fOut, buff, newHeader.toByteArray());
      filePosition += writeWithByteBuffer(fOut, buff, ByteBuffer.wrap(intByte).putInt(0).array());

      byte[] newScriptDataTag = newMetaDataTag.getScriptDataTag().toByteArray();
      newMetaDataTag.setDatasize(newScriptDataTag.length);
      filePosition += writeWithByteBuffer(fOut, buff, newMetaDataTag.toByteArray());
      filePosition += writeWithByteBuffer(fOut, buff, newScriptDataTag);
      filePosition += writeWithByteBuffer(fOut, buff, ByteBuffer.wrap(intByte).putInt(Tag.firstPartLength + newMetaDataTag.getDatasize()).array());

      filepositions.getValueAsStrictArray().clear();
      times.getValueAsStrictArray().clear();

      int lastTimestamp = 0; int timePast = 0;
      int i = 0; int keyFramesCount = 0;
      for (FlvDecoder decoder : this.decoders_)
      {
        FileInputStream fIn = new FileInputStream((String)this.ins_.get(i));
        fIn.skip(decoder.flvFile.header.dataOffset + 4);

        for (Tag tag : decoder.flvFile.tags) {
          if (tag.getType() == Tag.Type.SCRIPT) {
            fIn.skip(Tag.firstPartLength + tag.getDatasize() + 4);
          }
          else
          {
            if (tag.getTimestamp() - lastTimestamp < 0)
              tag.setTimestamp(tag.getTimestamp() + timePast);
            if ((tag.getType() == Tag.Type.VIDEO) && (tag.getVideoTag().getFrameType() == VideoTag.Frame.KeyFrame)) {
              if (n != null) {
                n.notifyProgress(keyFramesCount, getKeyFramesCount());
              }
              filepositions.getValueAsStrictArray().add(new ScriptValue(filePosition));
              times.getValueAsStrictArray().add(new ScriptValue(tag.getTimestamp() / 1000.0D));
              keyFramesCount++;
            }
            filePosition += writeWithByteBuffer(fOut, buff, tag.toByteArray());
            fIn.skip(Tag.firstPartLength);

            filePosition += writeWithByteBuffer(fOut, buff, fIn, tag.getDatasize() + 4);
            lastTimestamp = tag.getTimestamp();
          }
        }
        timePast = decoder.getDuration();
        decoder.flvFile.tags = null;

        fIn.close();
        i++;
      }
      flushByteBuffer(fOut, buff);

      FileChannel fcOut = fOut.getChannel();
      svDuration.setValue(Double.valueOf(timePast / 1000.0D));
      fcOut.position(newHeader.dataOffset + 4);
      fcOut.write(ByteBuffer.wrap(newMetaDataTag.toByteArray()));
      fcOut.write(ByteBuffer.wrap(newMetaDataTag.getScriptDataTag().toByteArray()));

      fcOut.close();
      fOut.close();

      System.out.println("copy: " + String.valueOf(System.currentTimeMillis() - last));
      last = System.currentTimeMillis();

      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }return false;
  }
}