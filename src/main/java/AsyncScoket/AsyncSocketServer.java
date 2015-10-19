package AsyncScoket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2015/10/19.
 */
public class AsyncSocketServer<TPackage> {
    protected int mPort=198110;
    protected Selector mSelector;

    protected ServerSocketChannel mServer;

    protected boolean mStarted=false;

    protected boolean mInited=false;

    protected List<AsyncSocket<TPackage>> mAsySocketList=new ArrayList<AsyncSocket<TPackage>>();

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
            } catch (IOException e) {
                e.printStackTrace();
                onError();
            }
        }
    }

    public void start() throws IOException {
        for (;;){
            this.mSelector.select();
            Iterator iter = mSelector.selectedKeys().iterator();
            while (iter.hasNext()){
                SelectionKey key = (SelectionKey) iter.next();
                iter.remove();
                process(key);
            }
        }

    }

    protected void process(SelectionKey key) throws IOException{
        if (key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel) key.channel();
            SocketChannel channel = server.accept();
            //设置非阻塞模式
            channel.configureBlocking(false);
            channel.register(this.mSelector, SelectionKey.OP_READ);
            AsyncSocket<TPackage> asyncSocket=new AsyncSocket(channel);
            mAsySocketList.add(asyncSocket);
            asyncSocket.starReceive();
        }
    }

    protected void onError(){

    }
}
