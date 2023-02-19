import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 可重入锁  ReentrantLock
 */
public class ReentrantLock implements Lock, java.io.Serializable {
    // 序列号
    private static final long serialVersionUID = 7373984872572414699L;
    /** Sync 内部类的对象实例 */
    private final Sync sync;

    /**
     继承了AbstractQueuedSynchronizer 类，   FairSync 和NonfairSync的父类  用于实现非公平锁和公平锁
     */
    abstract static class Sync extends AbstractQueuedSynchronizer {
        // 序列号
        private static final long serialVersionUID = -5179523762034025860L;
        /**
         抽象方法，其子类公平锁和非公平锁的实现不一样，需要重写
         */
        abstract void lock();

        /**
         * 非公平锁的TryAcquire()方法
         */
        final boolean nonfairTryAcquire(int acquires) {
            /** 多线程情况下，只能有一个线程进入lock.lock()方法，就是这个线程获取了对资源的锁，
             * 那么可以通过Thread.currentThread获取当前的锁
             * */
            final Thread current = Thread.currentThread();
            /**
             * getState是AQS的方法，获取的是资源的状态，有没有锁。1表示有锁，在被线程占用  0表示没有。
             */
            int c = getState();
            if (c == 0) {
                /**
                 *  如果c==0,  资源的锁没有被占用，那么当前线程 通过AQS的compareAndSetState，通过 CAS 获取资源的锁，  底层调用unsafe.compareAndSwapInt
                 */
                if (compareAndSetState(0, acquires)) {

                    /**
                     * setExclusiveOwnerThread是AQS的一个方法，设置当前线程为Thread exclusiveOwnerThread=current  排他锁，
                     */
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            /**
             * 否则，获取当前排他锁 current  =  exclusiveOwnerThread
             */
            else if (current == getExclusiveOwnerThread()) {
                /** 设置state的状态增加acquires   */
                int nextc = c + acquires;
                if (nextc < 0) // overflow
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
        /**
         * 尝试释放锁，公平锁和非公平锁的类  没有进行重写，所以两种锁的release没有区别的，
         * unlock方法调用AQS的release方法  release 方法还是调用的Sync的tryRelease方法。
         */
        protected final boolean tryRelease(int releases) {
            int c = getState() - releases;//状态-releases
            if (Thread.currentThread() != getExclusiveOwnerThread())
                throw new IllegalMonitorStateException();
            boolean free = false;
            if (c == 0) {//如果资源的锁没有被线程使用了，设置排他锁=null
                free = true;
                setExclusiveOwnerThread(null);
            }
            setState(c);//资源状态=新的状态（不一定是0）
            return free;
        }

        protected final boolean isHeldExclusively() {
            // While we must in general read state before owner,
            // we don't need to do so to check if current thread is owner
            return getExclusiveOwnerThread() == Thread.currentThread();
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        // Methods relayed from outer class

        final Thread getOwner() {
            return getState() == 0 ? null : getExclusiveOwnerThread();
        }

        final int getHoldCount() {
            return isHeldExclusively() ? getState() : 0;
        }

        final boolean isLocked() {
            return getState() != 0;
        }

        /**
         * Reconstitutes the instance from a stream (that is, deserializes it).
         */
        private void readObject(java.io.ObjectInputStream s)
                throws java.io.IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0); // reset to unlocked state
        }
    }

    /**
     * Sync object for non-fair locks
     */
    static final class NonfairSync extends Sync {
        private static final long serialVersionUID = 7316153563782823691L;

        /**
         * 非公平锁  使用CAS 获取锁，但是这里是if  不是while，  就是只抢一次
         */
        final void lock() {
            if (compareAndSetState(0, 1))
                setExclusiveOwnerThread(Thread.currentThread());
            else// if的抢占失败  也会触发else的执行
                /**
                 * 为AQS的方法
                 * 先执行对应的tryacquire方法，成功就获得锁，失败就加入到队列
                 *===========================================================================================
                 * 会调用到内部类Sync的Lock方法，由于Sync#lock是抽象方法，根据ReentrantLock初始化选择的公平锁和非公平锁，执行相关内部类的Lock方法，本质上都会执行AQS的Acquire方法。
                 * AQS的Acquire方法会执行tryAcquire方法，但是由于tryAcquire需要自定义同步器实现，因此执行了ReentrantLock中的tryAcquire方法，由于ReentrantLock是通过公平锁和非公平锁内部类实现的tryAcquire方法，因此会根据锁类型不同，执行不同的tryAcquire。
                 * ===========================================================================================
                 * 所以虽然公平锁和非公平锁都调用了acquire方法，但是 实际是调用了各自的tryAcquire方法，实现也是不一样的
                 * */
                acquire(1);
        }
        /** 调用父类 Sync的nonfairTryAcquire方法*/
        protected final boolean tryAcquire(int acquires) {
            return nonfairTryAcquire(acquires);
        }
    }

    /**
     * Sync object for fair locks
     */
    static final class FairSync extends Sync {
        private static final long serialVersionUID = -3000897897090466540L;

        final void lock() {
            /**
             * 为AQS的方法
             * 先执行对应的tryacquire方法，成功就获得锁，失败就加入到队列
             * */
            acquire(1);
        }

        /**
         * Fair version of tryAcquire.  Don't grant access unless
         * recursive call or no waiters or is first.
         */
        protected final boolean tryAcquire(int acquires) {
            final Thread current = Thread.currentThread();
            int c = getState();
            if (c == 0) {
                // 查询是否有线程在等待更长的时间  比当前线程。
                if (!hasQueuedPredecessors() &&// 与非公平锁的重要区别
                        compareAndSetState(0, acquires)) {
                    setExclusiveOwnerThread(current);
                    return true;
                }
            }
            else if (current == getExclusiveOwnerThread()) {
                int nextc = c + acquires;
                if (nextc < 0)
                    throw new Error("Maximum lock count exceeded");
                setState(nextc);
                return true;
            }
            return false;
        }
    }
    public ReentrantLock() {
        sync = new NonfairSync();
    }
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
    public void lock() {
        sync.lock();
    }

    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock() {
        return sync.nonfairTryAcquire(1);
    }


    public boolean tryLock(long timeout, TimeUnit unit)
            throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }


    public void unlock() {
        sync.release(1);
    }

    public Condition newCondition() {
        return sync.newCondition();
    }


    public int getHoldCount() {
        return sync.getHoldCount();
    }

    public boolean isHeldByCurrentThread() {
        return sync.isHeldExclusively();
    }


    public boolean isLocked() {
        return sync.isLocked();
    }

    public final boolean isFair() {
        return sync instanceof FairSync;
    }


    protected Thread getOwner() {
        return sync.getOwner();
    }

    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }


    public final boolean hasQueuedThread(Thread thread) {
        return sync.isQueued(thread);
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

    public boolean hasWaiters(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.hasWaiters((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    public int getWaitQueueLength(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitQueueLength((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    protected Collection<Thread> getWaitingThreads(Condition condition) {
        if (condition == null)
            throw new NullPointerException();
        if (!(condition instanceof AbstractQueuedSynchronizer.ConditionObject))
            throw new IllegalArgumentException("not owner");
        return sync.getWaitingThreads((AbstractQueuedSynchronizer.ConditionObject)condition);
    }

    public String toString() {
        Thread o = sync.getOwner();
        return super.toString() + ((o == null) ?
                "[Unlocked]" :
                "[Locked by thread " + o.getName() + "]");
    }
}
