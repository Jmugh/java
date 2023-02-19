package thread;

public abstract class AbstractOwnableSynchronizer
        implements java.io.Serializable {
    // 版本序列号
    private static final long serialVersionUID = 3737899427754241961L;
    // 构造方法
    protected AbstractOwnableSynchronizer() { }
    // 独占模式下的线程
    private transient Thread exclusiveOwnerThread;

    /**
     * AbstractOwnableSynchronizer抽象类中，可以设置独占资源线程和获取独占资源线程。
     * 分别为setExclusiveOwnerThread与getExclusiveOwnerThread方法，这两个方法会被子类调用。
     */

    // 设置独占线程
    protected final void setExclusiveOwnerThread(Thread thread) {
        exclusiveOwnerThread = thread;
    }
    // 获取独占线程
    protected final Thread getExclusiveOwnerThread() {
        return exclusiveOwnerThread;
    }
}
