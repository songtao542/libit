package cn.lolii.cropper.exif;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

class CountedDataInputStream extends FilterInputStream {

    private int mCount = 0;

    // allocate a byte buffer for a long value;
    private final byte[] mByteArray = new byte[8];
    private final ByteBuffer mByteBuffer = ByteBuffer.wrap(mByteArray);

    protected CountedDataInputStream(InputStream in) {
        super(in);
    }

    public int getReadByteCount() {
        return mCount;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int r = in.read(b);
        mCount += (r >= 0) ? r : 0;
        return r;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = in.read(b, off, len);
        mCount += (r >= 0) ? r : 0;
        return r;
    }

    @Override
    public int read() throws IOException {
        int r = in.read();
        mCount += (r >= 0) ? 1 : 0;
        return r;
    }

    @Override
    public long skip(long length) throws IOException {
        long skip = in.skip(length);
        mCount += skip;
        return skip;
    }

    public void skipOrThrow(long length) throws IOException {
        if (skip(length) != length) throw new EOFException();
    }

    public void skipTo(long target) throws IOException {
        long cur = mCount;
        long diff = target - cur;
        assert(diff >= 0);
        skipOrThrow(diff);
    }

    public void readOrThrow(byte[] b, int off, int len) throws IOException {
        int r = read(b, off, len);
        if (r != len) throw new EOFException();
    }

    public void readOrThrow(byte[] b) throws IOException {
        readOrThrow(b, 0, b.length);
    }

    public void setByteOrder(ByteOrder order) {
        mByteBuffer.order(order);
    }

    public ByteOrder getByteOrder() {
        return mByteBuffer.order();
    }

    public short readShort() throws IOException {
        readOrThrow(mByteArray, 0 ,2);
        mByteBuffer.rewind();
        return mByteBuffer.getShort();
    }

    public int readUnsignedShort() throws IOException {
        return readShort() & 0xffff;
    }

    public int readInt() throws IOException {
        readOrThrow(mByteArray, 0 , 4);
        mByteBuffer.rewind();
        return mByteBuffer.getInt();
    }

    public long readUnsignedInt() throws IOException {
        return readInt() & 0xffffffffL;
    }

    public String readString(int n, Charset charset) throws IOException {
        byte[] buf = new byte[n];
        readOrThrow(buf);
        return new String(buf, charset);
    }
}