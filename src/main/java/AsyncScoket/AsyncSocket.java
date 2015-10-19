package AsyncScoket;

import IAsyncSocketBuffer.IAsyncSocketBuffer;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Administrator on 2015/10/19.
 */
public class AsyncSocket<TPackage> {

    protected SocketChannel mSocket;

    protected IAsyncSocketBuffer<TPackage> mSocketBuffer;

    protected Selector mSelector;

    protected String mId;

    public String getmId() {
        return mId;
    }

    private PackageHander mReceivePackageHander;

    public static AsyncSocket createAsyncSocket(SocketChannel channel){
        if (channel!=null){
            try {
                Selector selector=Selector.open();

                channel.configureBlocking(false);
                channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                AsyncSocket asyncSocket=new AsyncSocket(channel, UUID.randomUUID().toString(),selector);
                return  asyncSocket;
            } catch (IOException e) {

            }
        }
        return null;
    }

    public AsyncSocket(SocketChannel socketChannel,String id,Selector selector){
        this.mSocket=socketChannel;
        this.mId=id;
        this.mSelector=selector;
    }

    public void setReceivePackageHander(PackageHander receivePackageHander){
        this.mReceivePackageHander=receivePackageHander;
    }

    public void starReceive(){

        for (;;){
            try {
                this.mSelector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Iterator iter = mSelector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = (SelectionKey) iter.next();
                iter.remove();
                receiveData(key);
            }
        }


    }

    public void setSocketBuffer(IAsyncSocketBuffer<TPackage> buffer){
        this.mSocketBuffer=buffer;
    }

    protected void receiveData(SelectionKey key){
        if (key.isReadable()){
            SocketChannel selectableChannel = (SocketChannel)key.channel();
            if(selectableChannel!=null) try {
                if (this.mSocketBuffer!=null){
                    int readCount = selectableChannel.read(mSocketBuffer.getBuffer());
                    if(readCount>0){
                        mSocketBuffer.refresh(readCount);
                    }
                    if (mSocketBuffer.commpleted()){
                        TPackage dataPackage=  mSocketBuffer.getReceivePackage();
                        if (dataPackage!=null){
                            onReceivePackage(dataPackage);
                        }
                    }
                }
                else {
                    throw new NullPointerException("please set socketbuffer");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    protected void onReceivePackage(TPackage tPackage) {
        if (mReceivePackageHander!=null){
            mReceivePackageHander.onReceivePackage(tPackage);
        }
    }

    protected void onError(){

    }
}
