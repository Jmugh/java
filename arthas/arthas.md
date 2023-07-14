



# 入门

## 启动 arthas

```bash
curl -O https://arthas.aliyun.com/arthas-boot.jar
java -jar arthas-boot.jar
```



```linux
$ java -jar arthas-boot.jar
* [1]: 35542
  [2]: 71560 math-game.jar
```



## 查看 dashboard

dashboard

## 通过 thread 命令来获取到`math-game`进程的 Main Class

```bash
thread 1 | grep 'main('
```



## jad 反编译

```java
jad demo.MathGame
```

## 

## 通过[watch](https://arthas.aliyun.com/doc/watch.html)命令来查看`demo.MathGame#primeFactors`函数的返回值：

```bash
watch demo.MathGame primeFactors returnObj
```

## 退出 arthas

如果只是退出当前的连接，可以用`quit`或者`exit`命令。Attach 到目标进程上的 arthas 还会继续运行，端口会保持开放，下次连接时可以直接连接上。

如果想完全退出 arthas，可以执行`stop`命令。



## retransform热部署





# Arthas 后台异步任务

```bash
trace Test t &    # 没效果
```

## 通过 jobs 查看任务

如果希望查看当前有哪些 arthas 任务在执行，可以执行 jobs 命令，执行结果如下



```bash
$ jobs
[10]*
       Stopped           watch com.taobao.container.Test test "params[0].{? #this.name == null }" -x 2
       execution count : 19
       start time      : Fri Sep 22 09:59:55 CST 2017
       timeout date    : Sat Sep 23 09:59:55 CST 2017
       session         : 3648e874-5e69-473f-9eed-7f89660b079b (current)
```



- job id 是 10, `*` 表示此 job 是当前 session 创建
- 状态是 Stopped
- execution count 是执行次数，从启动开始已经执行了 19 次
- timeout date 是超时的时间，到这个时间，任务将会自动超时退出

## 任务暂停和取消

当任务正在前台执行，比如直接调用命令`trace Test t`或者调用后台执行命令`trace Test t &`后又通过`fg`命令将任务转到前台。这时 console 中无法继续执行命令，但是可以接收并处理以下事件：

- ‘ctrl + z’：将任务暂停。通过`jbos`查看任务状态将会变为 Stopped，通过`bg <job-id>`或者`fg <job-id>`可让任务重新开始执行
- ‘ctrl + c’：停止任务
- ‘ctrl + d’：按照 linux 语义应当是退出终端，目前 arthas 中是空实现，不处理

## fg、bg 命令，将命令转到前台、后台继续执行

- 任务在后台执行或者暂停状态（`ctrl + z`暂停任务）时，执行`fg <job-id>`将可以把对应的任务转到前台继续执行。在前台执行时，无法在 console 中执行其他命令
- 当任务处于暂停状态时（`ctrl + z`暂停任务），执行`bg <job-id>`将可以把对应的任务在后台继续执行
- 非当前 session 创建的 job，只能由当前 session fg 到前台执行

## 任务输出重定向

可通过`>`或者`>>`将任务输出结果输出到指定的文件中，可以和`&`一起使用，实现 arthas 命令的后台异步任务。比如：



```bash
$ trace Test t >> test.out &
```

这时 trace 命令会在后台执行，并且把结果输出到应用`工作目录`下面的`test.out`文件。可继续执行其他命令。并可查看文件中的命令执行结果。可以执行`pwd`命令查看当前应用的`工作目录`。



```bash
$ cat test.out
```

如果没有指定重定向文件，则会把结果输出到`~/logs/arthas-cache/`目录下，比如：



```bash
$ trace Test t >>  &
job id  : 2
cache location  : /Users/admin/logs/arthas-cache/28198/2
```

此时命令会在后台异步执行，并将结果异步保存在文件（`~/logs/arthas-cache/${PID}/${JobId}`）中；

- 此时任务的执行不受 session 断开的影响；任务默认超时时间是 1 天，可以通过全局 `options` 命令修改默认超时时间；
- 此命令的结果将异步输出到  文件中；此时不管 `save-result` 是否为 true，都不会再往`~/logs/arthas-cache/result.log` 中异步写结果。

- 默认情况下，该功能是关闭的，如果需要开启，请执行以下命令：



```bash
$ options save-result true
 NAME         BEFORE-VALUE  AFTER-VALUE
----------------------------------------
 save-result  false         true
Affect(row-cnt:1) cost in 3 ms.
```





# 批处理功能

## 第一步： 创建你的批处理脚本

这里我们新建了一个`test.as`脚本，为了规范，我们采用了.as 后缀名，但事实上任意的文本文件都 ok。

提示

- 目前需要每个命令占一行
- dashboard 务必指定执行次数(`-n`)，否则会导致批处理脚本无法终止
- watch/tt/trace/monitor/stack 等命令务必指定执行次数(`-n`)，否则会导致批处理脚本无法终止
- 可以使用异步后台任务，如 `watch c.t.X test returnObj > &`，让命令一直在后台运行，通过日志获取结果，[获取更多异步任务的信息](https://arthas.aliyun.com/doc/async.html)

```text
➜  arthas git:(develop) cat /var/tmp/test.as
help
dashboard -n 1
session
thread
sc -d org.apache.commons.lang.StringUtils
```

## 第二步： 运行你的批处理脚本

通过`-f`执行脚本文件， 批处理脚本默认会输出到标准输出中，可以将结果重定向到文件中。

```bash
./as.sh -f /var/tmp/test.as <pid> > test.out # pid 可以通过 jps 命令查看
```

也可以通过 `-c` 来指定指行的命令，比如

```bash
./as.sh -c 'sysprop; thread' <pid> > test.out # pid 可以通过 jps 命令查看
```

## 第三步： 查看运行结果

```bash
cat test.out
```



# 活用ognl表达式

## 前言

Arthas 3.0中使用ognl表达式替换了groovy来实现表达式的求值功能，解决了groovy潜在会出现内存泄露的问题。灵活运用ognl表达式，能够极大提升问题排查的效率。

ognl官方文档：https://commons.apache.org/proper/commons-ognl/language-guide.html

## 一个测试应用

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author zhuyong on 2017/9/13.
 */
public class Test {

    public static final Map m = new HashMap<>();
    public static final Map n = new HashMap<>();

    static {
        m.put("a", "aaa");
        m.put("b", "bbb");

        n.put(Type.RUN, "aaa");
        n.put(Type.STOP, "bbb");
    }

    public static void main(String[] args) throws InterruptedException {
        List<Pojo> list = new ArrayList<>();

        for (int i = 0; i < 40; i ++) {
            Pojo pojo = new Pojo();
            pojo.setName("name " + i);
            pojo.setAge(i + 2);

            list.add(pojo);
        }

        while (true) {
            int random = new Random().nextInt(40);

            String name = list.get(random).getName();
            list.get(random).setName(null);

            test(list);

            list.get(random).setName(name);

            Thread.sleep(1000l);
        }
    }

    public static void test(List<Pojo> list) {

    }

    public static void invoke(String a) {
        System.out.println(a);
    }

    static class Pojo {
        String name;
        int age;
        String hobby;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getHobby() {
            return hobby;
        }

        public void setHobby(String hobby) {
            this.hobby = hobby;
        }
    }
}

public enum Type {
    RUN, STOP;
}
```

## 查看第一个参数

params是参数列表，是一个数组，可以直接通过下标方式访问

```linux
$ watch Test test params[0] -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 26 ms.
@ArrayList[
    @Pojo[Test$Pojo@6e2c634b],
    @Pojo[Test$Pojo@37a71e93],
    @Pojo[Test$Pojo@7e6cbb7a],
    ...
]
```

> 这里的-n表示只输出一次

## 查看数组中的元素

第一个参数是一个List，想要看List中第一个Pojo对象，可以通过**下标方式**，也可以通过**List的get方法访问**。

```arthas
$ watch Test test params[0][0] -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 14 ms.
@Pojo[
    name=@String[name 0],
    age=@Integer[2],
    hobby=null,
]

$ watch Test test params[0].get(0) -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 14 ms.
@Pojo[
    name=@String[name 0],
    age=@Integer[2],
    hobby=null,
]
```

## 查看Pojo的属性

拿到这个Pojo可以，直接访问Pojo的属性，如age

```sh
$ watch Test test params[0].get(0).age -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 21 ms.
@Integer[2]
```

还可以通过下标的方式访问`params[0][0]["age"]`，这个写法等效于`params[0][0].age`：

```sh
$ watch Test test params[0][0]["name"] -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 53 ms.
watch failed, condition is: null, express is: params[0][0][age], ognl.NoSuchPropertyException: com.taobao.arthas.core.advisor.Advice.age, visit /Users/wangtao/logs/arthas/arthas.log for more details.
```

但这样会报错，这时候需要再加一个引号

```
$ watch Test test 'params[0][0]["age"]' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 25 ms.
@Integer[2]
```

## 集合投影

有时候我们只需要抽取对象数组中的某一个属性，这种情况可以通过投影来实现，比如要将Pojo对象列表中的name属性单独抽出来，可以通过`params[0].{name}`这个表达式来实现。 ognl会便利params[0]这个List取出每个对象的name属性，重新组装成一个新的数组。用法相当于Java stream中的map函数。

```
$ watch Test test params[0].{name} -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 56 ms.
@ArrayList[
    @String[name 0],
    @String[name 1],
    @String[name 2],
    @String[name 3],
    null,
    @String[name 5],
    @String[name 6],
    @String[name 7],
    @String[name 8],
    @String[name 9],
]
```

## 集合过滤

有时候还需要针对集合对象按某种条件进行过滤，比如想找出所有age大于5的Pojo的name，可以这样写

```
$ watch Test test "params[0].{? #this.age > 5}.{name}" -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 25 ms.
@ArrayList[
    @String[name 4],
    @String[name 5],
    @String[name 6],
    null,
    @String[name 8],
    @String[name 9],
]
```

其中`{? #this.age > 5}` 相当于stream里面的filter，后面的`name`相当于stream里面的map

那如果要找到第一个age大于5的Pojo的name，怎么办呢？可以用`^`或`$`来进行第一个或最后一个的匹配，像下面这样:

```
$ watch Test test "params[0].{^ #this.age > 5}.{name}" -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 24 ms.
@ArrayList[
    @String[name 4],
]
Command hit execution time limit 1, therefore will be aborted.
$ watch Test test "params[0].{$ #this.age > 5}.{name}" -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 43 ms.
@ArrayList[
    @String[name 9],
]
```

## 多行表达式

有些表达式一行之内无法表达，需要多行才能表达，应该怎么写的？比如，假设我们要把所有Pojo的name拿出来，再往里面新加一个新的元素，在返回新的列表，应该如何写？可以通过中括号将多个表达式串联起来，**最后一个表达式的返回值代表整个表达式的最终结果**。临时变量可以用`#`来表示。

```
$ watch Test test '(#test=params[0].{name}, #test.add("abc"), #test)' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 28 ms.
@ArrayList[
    @String[name 0],
    @String[name 1],
    @String[name 2],
    @String[name 3],
    @String[name 4],
    @String[name 5],
    @String[name 6],
    @String[name 7],
    @String[name 8],
    null,
    @String[abc],
]
```

## 调用构造函数

调用构造函数(这里是指ArrayList这个类的构造函数)，**必须要指定要创建的类的`全类名`**。比如下面的例子中，创建一个新的list，然后添加一个新的元素，然后返回添加后的list。

```
$ watch Test test '(#test=new java.util.ArrayList(), #test.add("abc"), #test)' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 37 ms.
@ArrayList[
    @String[abc],
]
```

## 访问静态变量

可以通过`@class@filed`方式访问，注意需要填写全类名

```
$ watch Test test '@Test@m' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 35 ms.
@HashMap[
    @String[a]:@String[aaa],
    @String[b]:@String[bbb],
]
```

## 调用静态方法

可以通过`@class@method(args)`方式访问，**注意需要填写全类名**

```
$ watch Test test '@java.lang.System@getProperty("java.version")' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 42 ms.
@String[1.8.0_51]
```

静态方法和非静态方法结合，例如想要获取当前方法调用的TCCL，可以像下面这样写：

```
$ watch Test test '@java.lang.Thread@currentThread().getContextClassLoader()' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 84 ms.
@AppClassLoader[
    ucp=@URLClassPath[sun.misc.URLClassPath@4cdbe50f],
    $assertionsDisabled=@Boolean[true],
]
```

## 访问Map中的元素

Test.n是一个HashMap，假设要获取这个Map的所有key，ongl针对Map接口提供了`keys`, `values`这两个虚拟属性，可以像普通属性一样访问。

```
$ watch Test test '@Test@n.keys' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 57 ms.
@KeySet[
    @Type[RUN],
    @Type[STOP],
]
```

因为这个Map的Key是一个Enum，假设要把key为RUN这个值的value取出来应该怎么写呢？可以通过Enum的`valueOf`方法来创建一个Enum，然后get出来，比如下面一样

```
$ watch Test test '@Test@n.get(@Type@valueOf("RUN"))' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 168 ms.
@String[aaa]
```

或者是下面这样，通过迭代器+过滤的方式：

```
$ watch Test test '@Test@n.entrySet().iterator.{? #this.key.name() == "RUN"}' -n 1
Press Ctrl+C to abort.
Affect(class-cnt:1 , method-cnt:1) cost in 72 ms.
@ArrayList[
    @Node[RUN=aaa],
]
```



## ognl的特殊用法

https://github.com/alibaba/arthas/issues/71







# 经验

有时候不知道为什么，arthas会断开或者链接容器断开，重新执行java -jar arthas-boot.jar，进入选进程之后提示端口被占用。这是因为上一次的进程没有正确退出导致的（应该执行stop退出），想要杀掉之前的进程使用如下命令：

```shell
java -jar arthas-client.jar 127.0.0.1 3658 -c "stop"  # 停掉之前的arthas进程
```

3658是上次选进程后面的端口。









