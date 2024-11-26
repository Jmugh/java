# java并发机制的底层实现原理

## volatile

volatile是轻量级的synchronized。。如果volatile变量修饰符使用恰当的话，它比synchronized的使用和执行成本更低，因为它不会引起线程上下文的切换和调度。

有volatile变量修饰的共享变量进行写操作的时候会多出lock前缀的汇编代码，Lock前缀的指令在多核处理器下会引发了两件事情：

1. 将当前处理器缓存行的数据写回到系统内存。 
2. 这个写回内存的操作会使在其他CPU里缓存了该内存地址的数据无效。 

## sychronized

### 基础

​		先来看下利用synchronized实现同步的基础：Java中的每一个对象都可以作为锁。具体表现为以下3种形式。 

- 对于普通同步方法，锁是当前实例对象。 
- 对于静态同步方法，锁是当前类的Class对象。 
- 对于同步方法块，锁是Synchonized括号里配置的对象。 

​		JVM基于进入和退出Monitor对 象来实现方法同步和代码块同步，但两者的实现细节不一样。代码块同步是使用monitorenter 和monitorexit指令实现的，而方法同步是使用另外一种方式实现的。

​		任何对象都有一个monitor与之关联，当且一个monitor被持有后，它将处于锁定状态。线程执行到monitorenter指令时，将会尝试获取对象所对应的monitor的所有权，即尝试获得对象的锁。

​		synchronized用的锁是存在Java对象头里的。Java对象头里的Mark Word里默认存储对象的HashCode、分代年龄和锁标记位。



### 锁的升级与对比 

锁一共有4种状态，级别从低到高依次是：无锁状态、偏向锁状态、轻量级锁状 态和重量级锁状态，这几个状态会随着竞争情况逐渐升级。锁可以升级但不能降级，

1. 偏向锁

   ```note
   大多数情况下，锁不仅不存在多线程竞争，而且总是由同 一线程多次获得，为了让线程获得锁的代价更低而引入了偏向锁。当一个线程访问同步块并 获取锁时，会在对象头和栈帧中的锁记录里存储锁偏向的线程ID，以后该线程在进入和退出 同步块时不需要进行CAS操作来加锁和解锁，只需简单地测试一下对象头的Mark Word里是否 存储着指向当前线程的偏向锁。如果测试成功，表示线程已经获得了锁。如果测试失败，则需 要再测试一下Mark Word中偏向锁的标识是否设置成1（表示当前是偏向锁）：如果没有设置，则 使用CAS竞争锁；如果设置了，则尝试使用CAS将对象头的偏向锁指向当前线程。
   ```

2. 轻量级锁

   ```note
   线程在执行同步块之前，JVM会先在当前线程的栈桢中创建用于存储锁记录的空间，并 将对象头中的Mark Word复制到锁记录中，官方称为Displaced Mark Word。然后线程尝试使用 CAS将对象头中的Mark Word替换为指向锁记录的指针。如果成功，当前线程获得锁，如果失 败，表示其他线程竞争锁，当前线程便尝试使用自旋来获取锁。
   ```

# java内存模型



## java内存模型的基础

![image-20240817142535045](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240817142535045.png)

## 重排序

在执行程序时，为了提高性能，编译器和处理器常常会对指令做重排序。重排序分3种类 型。 

- 编译器优化的重排序。编译器在不改变单线程程序语义的前提下，可以重新安排语句 的执行顺序。 

- 指令级并行的重排序。现代处理器采用了指令级并行技术（Instruction-Level Parallelism，ILP）来将多条指令重叠执行。如果不存在数据依赖性，处理器可以改变语句对应 机器指令的执行顺序。 

- 内存系统的重排序。由于处理器使用缓存和读/写缓冲区，这使得加载和存储操作看上 去可能是在乱序执行

​		上述的1属于编译器重排序，2和3属于处理器重排序。这些重排序可能会导致多线程程序出现内存可见性问题。对于编译器，JMM的编译器重排序规则会禁止特定类型的编译器重排序（不是所有的编译器重排序都要禁止）。对于处理器重排序，JMM的处理器重排序规则会要求Java编译器在生成指令序列时，插入特定类型的内存屏障（Memory Barriers，Intel称之为Memory Fence）指令，通过内存屏障指令来禁止特定类型的处理器重排序。 

## 顺序一致性





# java并发编程基础





# java中的锁

**队列同步器：AbstractQueuedSynchronizer.**  

同步器的设计是基于模板方法模式的，也就是说，使用者需要继承同步器并重写指定的方法，随后将同步器组合在自定义同步组件的实现中，并调用同步器提供的模板方法，而这些模板方法将会调用使用者重写的方法。 

重写同步器指定的方法时，需要使用同步器提供的如下3个方法来访问或修改同步状态。 

- getState()：获取当前同步状态。 

- setState(int newState)：设置当前同步状态。 

- compareAndSetState(int expect,int update)：使用CAS设置当前状态，该方法能够保证状态设置的原子性。 

 

**同步器可重写的方法**

![image-20240817155539178](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240817155539178.png)

**同步器提供的模板方法**

![image-20240817155631176](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240817155631176.png)







## 同步队列 

同步器依赖内部的同步队列（一个FIFO双向队列）来完成同步状态的管理，当前线程获取同步状态失败时，同步器会将当前线程以及等待状态等信息构造成为一个节点（Node）并将其加入同步队列，同时会阻塞当前线程，当同步状态释放时，会把首节点中的线程唤醒，使其再次尝试获取同步状态。 

同步队列中的节点（Node）用来保存获取同步状态失败的线程引用、等待状态以及前驱和后继节点,具体如下图：

![image-20240817160510319](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240817160510319.png)



**同步队列的基本结构**

![image-20240817160626016](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240817160626016.png)





<font color = 'red'>设置尾节点</font>

同步器包含了两个节点类型的引用，一个指向头节点，而另一个指向尾节点。 试想一下，当一个线程成功地获取了同步状态（或者锁），其他线程将无法获取到同步状态，转而被构造成为节点并加入到同步队列中，而这个加入队列的过程必须要保证线程安全，因此同步器提供了一个基于CAS的设置尾节点的方法：compareAndSetTail(Node expect,Node update)，它需要传递当前线程“认为”的尾节点和当前节点，只有设置成功后，当前节点才正式与之前的尾节点建立关联。 



<font color = 'red'>设置首节点</font>

同步队列遵循FIFO，首节点是获取同步状态成功的节点，首节点的线程在释放同步状态时，将会唤醒后继节点，而后继节点将会在获取同步状态成功时将自己设置为首节点。

设置首节点是通过获取同步状态成功的线程来完成的，由于只有一个线程能够成功获取到同步状态，因此设置头节点的方法并不需要使用CAS来保证，它只需要将首节点设置成为原首节点的后继节点并断开原首节点的next引用即可。 



![image-20240817160938987](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\image-20240817160938987.png)



acquire(int arg)

```java
 public final void acquire(int arg) {
     if (!tryAcquire(arg) && acquireQueued(addWaiter(Node.EXCLUSIVE), arg)) selfInterrupt();
 }
```

首先调用自定义同步器实现的tryAcquire(int arg)方法，该方法 保证线程安全的获取同步状态，如果同步状态获取失败，则构造同步节点（独占式Node.EXCLUSIVE，同一时刻只能有一个线程成功获取同步状态）并通过addWaiter(Node node) 方法将该节点加入到同步队列的尾部，最后调用acquireQueued(Node node,int arg)方法，使得该 节点以“死循环”的方式获取同步状态。如果获取不到则阻塞节点中的线程，而被阻塞线程的 唤醒主要依靠前驱节点的出队或阻塞线程被中断来实现。

 

addWaiter(Node mode)  &  enq(final Node node)

节点进入同步队列之后，就进入了一个自旋的过程，每个节点（或者说每个线程）都在自省地观察，当条件满足，获取到了同步状态，就可以从这个自旋过程中退出，否则依旧留在这个自旋过程中（并会阻塞节点的线程），







ReentrantLock

1. 可重入
2. 公平锁、非公平锁：公平锁与nonfairTryAcquire(int acquires)比较，唯一不同的位置为判断条件多了hasQueuedPredecessors()方法，即加入了同步队列中当前节点是否有前驱节点的判断，如果该 方法返回true，则表示有线程比当前线程更早地请求获取锁，因此需要等待前驱线程获取并释 放锁之后才能继续获取锁。



















# java并发容器和框架







# java中的并发工具类





# java中的线程池

## 线程池的监控

- taskCount：线程池需要执行的任务数量。 

- completedTaskCount：线程池在运行过程中已完成的任务数量，小于或等于taskCount。 

- largestPoolSize：线程池里曾经创建过的最大线程数量。通过这个数据可以知道线程池是否曾经满过。如该数值等于线程池的最大大小，则表示线程池曾经满过。 

- getPoolSize：线程池的线程数量。如果线程池不销毁的话，线程池里的线程不会自动销毁，所以这个大小只增不减。 

- getActiveCount：获取活动的线程数。

​		通过扩展线程池进行监控。可以通过继承线程池来自定义线程池，重写线程池的beforeExecute、afterExecute和terminated方法，也可以在任务执行前、执行后和线程池关闭前执行一些代码来进行监控。例如，监控任务的平均执行时间、最大执行时间和最小执行时间等。 

# Executor框架





# 并发编程实践





# 问题

synchronized怎么实现原子性、可见性、有序性的