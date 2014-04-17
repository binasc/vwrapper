package video.tool.flv;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import video.tool.flv.tag.TagException;

public class Header
{
	static final int defaultHeaderLength = 9;
	private byte[] signature_ = new byte[] { 'F', 'L', 'V' };
	public byte version = 1;
	public byte typeFlags = 0;
	public int dataOffset = 9;

	public void setHasAudio() {
		this.typeFlags = ((byte)(this.typeFlags | 0x4));
	}

	public void setHasVideo() {
		this.typeFlags = ((byte)(this.typeFlags | 0x1));
	}

	public byte[] toByteArray() {
		int pos = 0;
		byte[] buff = new byte[this.dataOffset];
		pos += 3;
		ByteBuffer.wrap(buff, pos, 3).put(signature_);
		buff[pos] = this.version;
		pos++;
		buff[pos] = this.typeFlags;
		pos++;
		ByteBuffer.wrap(buff, pos, 4).putInt(this.dataOffset);

		return buff;
	}

	public static Header fromInputStream(InputStream in) throws TagException {
		Header header = new Header();
		byte[] buff = new byte[defaultHeaderLength];
		try
		{
			int ret = in.read(buff, 0, defaultHeaderLength);
			if (ret != defaultHeaderLength) {
				throw new TagException();
			}
			ByteBuffer.wrap(header.signature_).put(buff, 0, 3);
			if (!new String(header.signature_).equals("FLV")) {
				throw new TagException("not a FLV file");
			}
			header.version = buff[3];
			if (header.version != 1) {
				throw new TagException("unsupported version");
			}
			header.typeFlags = buff[4];
			header.dataOffset = ByteBuffer.wrap(buff, 5, 4).getInt();
			int remain = header.dataOffset - defaultHeaderLength;
			if (remain < 0) {
				throw new TagException("invalid header length");
			}
			while (remain > 0) {
				ret = (int) in.skip(remain);
				if (ret <= 0 && remain > 0) {
					throw new TagException();
				}
				remain -= ret;
			}
			// read first tag size, it should always be 0
			ret = in.read(buff, 0, 4);
			if ((ret != 4) || (ByteBuffer.wrap(buff, 0, 4).getInt() != 0)) {
				throw new TagException();
			}

			return header;
		} catch (IOException e) {
			e.printStackTrace();
			throw new TagException("io error:w");
		}
	}
}