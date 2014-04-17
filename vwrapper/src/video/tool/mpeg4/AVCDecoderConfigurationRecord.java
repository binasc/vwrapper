package video.tool.mpeg4;

import java.nio.ByteBuffer;

public class AVCDecoderConfigurationRecord
{
  private byte configurationVersion_ = 1;
  private byte AVCProfileIndication_;
  private byte profileCompatibility_;
  private byte AVCLevelIndication_;
  private byte lengthSizeMinusOne_ = -4;

  private byte numOfSequenceParameterSets_ = -32;
  SequenceParameterSet[] sequenceParameterSets_;
  byte numOfPictureParameterSets_;
  PictureParameterSet[] pictureParameterSets_;
  private byte chromaFormat_ = -4;

  private byte bitDepthLumaMinus8_ = -32;

  private byte bitDepthChromaMinus8_ = -32;
  byte numOfSequenceParameterSetExt_;
  SequenceParameterSetExt[] sequenceParameterSetsExt_;

  public AVCDecoderConfigurationRecord()
  {
  }

  public AVCDecoderConfigurationRecord(AVCDecoderConfigurationRecord config)
  {
    this.configurationVersion_ = config.configurationVersion_;
    this.AVCProfileIndication_ = config.AVCProfileIndication_;
    this.profileCompatibility_ = config.profileCompatibility_;
    this.AVCLevelIndication_ = config.AVCLevelIndication_;
    this.lengthSizeMinusOne_ = config.lengthSizeMinusOne_;
    this.numOfSequenceParameterSets_ = config.numOfSequenceParameterSets_;
    this.sequenceParameterSets_ = new SequenceParameterSet[config.sequenceParameterSets_.length];
    for (int i = 0; i < this.sequenceParameterSets_.length; i++) {
      this.sequenceParameterSets_[i] = new SequenceParameterSet();
      this.sequenceParameterSets_[i].sequenceParameterSetLength_ = config.sequenceParameterSets_[i].sequenceParameterSetLength_;
      this.sequenceParameterSets_[i].sequenceParameterSetNALUnit_ = new byte[this.sequenceParameterSets_[i].sequenceParameterSetLength_];
      for (int j = 0; j < this.sequenceParameterSets_[i].sequenceParameterSetNALUnit_.length; j++) {
        this.sequenceParameterSets_[i].sequenceParameterSetNALUnit_[j] = config.sequenceParameterSets_[i].sequenceParameterSetNALUnit_[j];
      }
    }
    this.numOfPictureParameterSets_ = config.numOfPictureParameterSets_;
    this.pictureParameterSets_ = new PictureParameterSet[config.pictureParameterSets_.length];
    for (int i = 0; i < this.pictureParameterSets_.length; i++) {
      this.pictureParameterSets_[i] = new PictureParameterSet();
      this.pictureParameterSets_[i].pictureParameterSetLength_ = config.pictureParameterSets_[i].pictureParameterSetLength_;
      this.pictureParameterSets_[i].pictureParameterSetNALUnit_ = new byte[this.pictureParameterSets_[i].pictureParameterSetLength_];
      for (int j = 0; j < this.pictureParameterSets_[i].pictureParameterSetNALUnit_.length; j++)
        this.pictureParameterSets_[i].pictureParameterSetNALUnit_[j] = config.pictureParameterSets_[i].pictureParameterSetNALUnit_[j];
    }
  }

  public String toString()
  {
    StringBuilder ret = new StringBuilder();
    ret.append("\nconfigurationVersion: ").append(this.configurationVersion_);
    ret.append("\nAVCProfileIndication: ").append(this.AVCProfileIndication_);
    ret.append("\nprofileCompatibility: ").append(this.profileCompatibility_);
    ret.append("\nAVCLevelIndication: ").append(this.AVCLevelIndication_);
    ret.append("\nlengthSize: ").append(getLengthSize());
    ret.append("\n<SequenceParameterSet>");
    for (SequenceParameterSet set : this.sequenceParameterSets_) {
      ret.append("\n length: ").append(set.sequenceParameterSetLength_).append("\n ");
      for (byte unit : set.sequenceParameterSetNALUnit_) {
        ret.append(unit).append(",");
      }
    }
    ret.append("\n</SequenceParameterSet>");
    ret.append("\n<PictureParameterSet>");
    for (PictureParameterSet set : this.pictureParameterSets_) {
      ret.append("\n length: ").append(set.pictureParameterSetLength_).append("\n ");
      for (byte unit : set.pictureParameterSetNALUnit_) {
        ret.append(unit).append(",");
      }
    }
    ret.append("\n</PictureParameterSet>");
    return ret.toString();
  }

  public byte getLengthSize() {
    return (byte)((this.lengthSizeMinusOne_ & 0x3) + 1);
  }

  public byte getNumOfSequenceParameterSets() {
    return (byte)(this.numOfSequenceParameterSets_ & 0x7);
  }

  public byte getNumOfPictureParameterSets() {
    return this.numOfPictureParameterSets_;
  }

  public byte getNumOfSequenceParameterSetsExt() {
    return this.numOfPictureParameterSets_;
  }

  public byte getChromaFormat() {
    return (byte)(this.chromaFormat_ & 0x3);
  }

  public byte getBitDepthLuma() {
    return (byte)((this.bitDepthLumaMinus8_ & 0x7) + 8);
  }

  public byte getBitDepthChroma() {
    return (byte)((this.bitDepthChromaMinus8_ & 0x7) + 8);
  }

  public void parse(ByteBuffer buff) {
    this.configurationVersion_ = buff.get();
    this.AVCProfileIndication_ = buff.get();
    this.profileCompatibility_ = buff.get();
    this.AVCLevelIndication_ = buff.get();
    this.lengthSizeMinusOne_ = buff.get();
    this.numOfSequenceParameterSets_ = buff.get();
    this.sequenceParameterSets_ = new SequenceParameterSet[getNumOfSequenceParameterSets()];
    for (int i = 0; i < this.sequenceParameterSets_.length; i++) {
      this.sequenceParameterSets_[i] = new SequenceParameterSet();
      this.sequenceParameterSets_[i].sequenceParameterSetLength_ = buff.getShort();
      this.sequenceParameterSets_[i].sequenceParameterSetNALUnit_ = new byte[this.sequenceParameterSets_[i].sequenceParameterSetLength_];
      buff.get(this.sequenceParameterSets_[i].sequenceParameterSetNALUnit_);
    }
    this.numOfPictureParameterSets_ = buff.get();
    this.pictureParameterSets_ = new PictureParameterSet[getNumOfSequenceParameterSets()];
    for (int i = 0; i < this.pictureParameterSets_.length; i++) {
      this.pictureParameterSets_[i] = new PictureParameterSet();
      this.pictureParameterSets_[i].pictureParameterSetLength_ = buff.getShort();
      this.pictureParameterSets_[i].pictureParameterSetNALUnit_ = new byte[this.pictureParameterSets_[i].pictureParameterSetLength_];
      buff.get(this.pictureParameterSets_[i].pictureParameterSetNALUnit_);
    }
    if (((buff.remaining() > 4) && (this.AVCProfileIndication_ == 100)) || (this.AVCProfileIndication_ == 110) || (this.AVCProfileIndication_ == 122) || (this.AVCProfileIndication_ == 144)) {
      this.chromaFormat_ = buff.get();
      this.bitDepthLumaMinus8_ = buff.get();
      this.bitDepthChromaMinus8_ = buff.get();
      this.numOfSequenceParameterSetExt_ = buff.get();
      this.sequenceParameterSetsExt_ = new SequenceParameterSetExt[getNumOfSequenceParameterSetsExt()];
      for (int i = 0; i < this.sequenceParameterSetsExt_.length; i++) {
        this.sequenceParameterSetsExt_[i] = new SequenceParameterSetExt();
        this.sequenceParameterSetsExt_[i].sequenceParameterSetExtLength_ = buff.getShort();
        this.sequenceParameterSetsExt_[i].sequenceParameterSetExtNALUnit_ = new byte[this.sequenceParameterSetsExt_[i].sequenceParameterSetExtLength_];
        buff.get(this.sequenceParameterSetsExt_[i].sequenceParameterSetExtNALUnit_);
      }
    }
  }

  class PictureParameterSet
  {
    short pictureParameterSetLength_;
    byte[] pictureParameterSetNALUnit_;

    PictureParameterSet()
    {
    }
  }

  class SequenceParameterSet
  {
    short sequenceParameterSetLength_;
    byte[] sequenceParameterSetNALUnit_;

    SequenceParameterSet()
    {
    }
  }

  class SequenceParameterSetExt
  {
    short sequenceParameterSetExtLength_;
    byte[] sequenceParameterSetExtNALUnit_;

    SequenceParameterSetExt()
    {
    }
  }
}