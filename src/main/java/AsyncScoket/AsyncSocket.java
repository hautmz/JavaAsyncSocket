package AsyncScoket;

import IAsyncSocketBuffer.IAsyncSocketBuffer;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Created by Administrator on 2015/10/19.
 */
public class AsyncSocket<TPackage> {

    protected SocketChannel mSocket;

    protected IAsyncSocketBuffer<TPackage> mSocketBuffer;

    private PackageHander mReceivePackageHander;


    public AsyncSocket(SocketChannel socketChannel){
        this.mSocket=socketChannel;

    }

    public void setReceivePackageHander(PackageHander receivePackageHander){
        this.mReceivePackageHander=receivePackageHander;
    }

    public void starReceive(){
        if(mSocket!=null) try {
            int readCount = mSocket.read(mSocketBuffer.getBuffer());
            if(readCount>0){
                mSocketBuffer.refresh(readCount);
            }
            if (mSocketBuffer.commpleted()){
                TPackage dataPackage=  mSocketBuffer.getReceivePackage();
                if (dataPackage!=null){
                    onReceivePackage(dataPackage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
