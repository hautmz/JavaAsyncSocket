package IAsyncSocketBuffer;

import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2015/10/19.
 */
public interface IAsyncSocketBuffer<TPackage> {
    TPackage getReceivePackage();
    ByteBuffer getBuffer();
    void setEndFlag(byte[] endFlag);
    void refresh(int count);
    boolean commpleted();
}
