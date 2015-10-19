package AsyncScoket;

/**
 * Created by Administrator on 2015/10/19.
 */
public interface PackageHander<TPackage> {
    void onReceivePackage(TPackage tPackage);
}
