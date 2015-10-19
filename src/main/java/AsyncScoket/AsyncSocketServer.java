package AsyncScoket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Created by Administrator on 2015/10/19.
 */
public class AsyncSocketServer<TPackage> {
    protected int mPort=198110;
    protected Selector mSelector;

    protected ServerSocketChannel mServer;

    protected boolean mStarted=false;

    protected boolean mInited=false;

    protected Map<String,AsyncSocket<TPackage>> mAsySocketMap=new HashMap<String,AsyncSocket<TPackage>>();





    public AsyncSocketServer(int prot){
        this.mPort=prot;
    }

    public void init(){
        if (!mInited){
            try {
                this.mServer= ServerSocketChannel.open();
                this.mSelector=Selector.open();
                this.mServer.socket().bind(new InetSocketAddress(this.mPort));
                this.mServer.configureBlocking(false);
                this.mServer.register(this.mSelector, SelectionKey.OP_ACCEPT);
                this.mInited=true;
            } catch (IOException e) {
                e.printStackTrace();
                onError();
            }
        }
    }

    public void start() {
        this.mStarted=true;
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
                process(key);
            }
        }

    }

    protected void process(SelectionKey key){
        if (key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = null;
            try {
                channel = server.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (channel!=null){
                AsyncSocket asyncSocket= onConnectedServer(channel);
                if (asyncSocket!=null){
                    mAsySocketMap.put(asyncSocket.getmId(), asyncSocket);
                }

            }

        }
    }

    protected AsyncSocket onConnectedServer(SocketChannel channel){
        if (channel!=null){
            AsyncSocket<TPackage> asyncSocket=AsyncSocket.createAsyncSocket(channel);
            if (asyncSocket!=null){

                asyncSocket.starReceive();
                return  asyncSocket;
            }

        }
        return null;

    }





    protected void onError(){

    }
}
