# Tomcat

## 准备工作

### tomcat目录结构

![image-20230106145556872](images/image-20230106145556872.png)

### tomcat源码运行

#### 在源码目录下编写 pom.xml 文件

```java
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
   
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>Tomcat8.5</artifactId>
    <name>Tomcat8.5</name>
    <version>8.5</version>
   
    <build>
        <finalName>Tomcat8.5</finalName>
        <sourceDirectory>java</sourceDirectory>
        <testSourceDirectory>test</testSourceDirectory>
        <resources>
            <resource>
                <directory>java</directory>
            </resource>
        </resources>
        <testResources>
           <testResource>
                <directory>test</directory>
           </testResource>
        </testResources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.4</version>
                <configuration>
                    <encoding>UTF-8</encoding>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
   
    <dependencies>
        <dependency>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>2.4</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.easymock</groupId>
            <artifactId>easymock</artifactId>
            <version>3.4</version>
        </dependency>
        <dependency>
            <groupId>ant</groupId>
            <artifactId>ant</artifactId>
            <version>1.7.0</version>
        </dependency>
        <dependency>
            <groupId>wsdl4j</groupId>
            <artifactId>wsdl4j</artifactId>
            <version>1.6.2</version>
        </dependency>
        <dependency>
            <groupId>javax.xml</groupId>
            <artifactId>jaxrpc</artifactId>
            <version>1.1</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jdt.core.compiler</groupId>
            <artifactId>ecj</artifactId>
            <version>4.5.1</version>
        </dependency>
         
    </dependencies>
</project>
```

#### 启动参数

Main class设置为org.apache.catalina.startup.Bootstrap 添加VM options

```java
-Dcatalina.home=D:/MyJava/tomcat_note/apache-tomcat-8.5.42-src/home
-Dcatalina.base=D:/MyJava/tomcat_note/apache-tomcat-8.5.42-src/home
-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
-Djava.util.logging.config.file=D:/MyJava/tomcat_note/apache-tomcat-8.5.42-src/home/conf/logging.properties
-Duser.language=en
-Duser.region=US
-Dfile.encoding=UTF-8
```

#### 修改访问主页问题

在tomcat的源码org.apache.catalina.startup.ContextConfig中的configureStart函数中手动将JSP解析器初始化

```java
webConfig();
//加上下面这一行
context.addServletContainerInitializer(new JasperInitializer(), null);
```





## Tomcat架构

### Http工作原理

HTTP协议是浏览器与服务器之间的数据传送协议。作为应用层协议，HTTP是基于TCP/IP 协议来传递数据的（HTML文件、图片、查询结果等），HTTP协议不涉及数据包 （Packet）传输，主要规定了客户端和服务器之间的通信格式。

<img src="images/image-20230106150341955.png" alt="image-20230106150341955" style="zoom:50%;" />

从图上你可以看到，这个过程是：

1） 用户通过浏览器进行了一个操作，比如输入网址并回车，或者是点击链接，接着浏览器获取了这个事件。 

2） 浏览器向服务端发出TCP连接请求。 

3） 服务程序接受浏览器的连接请求，并经过TCP三次握手建立连接。 

4） 浏览器将请求数据打包成一个HTTP协议格式的数据包。 

5） 浏览器将该数据包推入网络，数据包经过网络传输，最终达到端服务程序。 

6） 服务端程序拿到这个数据包后，同样以HTTP协议格式解包，获取到客户端的意图。 

7） 得知客户端意图后进行处理，比如提供静态文件或者调用服务端程序获得动态结果。 

8） 服务器将响应结果（可能是HTML或者图片等）按照HTTP协议格式打包。 

9） 服务器将响应数据包推入网络，数据包经过网络传输最终达到到浏览器。 

10） 浏览器拿到数据包后，以HTTP协议的格式解包，然后解析数据，假设这里的数据是HTML。 

11） 浏览器将HTML文件展示在页面上。 

那我们想要探究的Tomcat作为一个HTTP服务器，在这个过程中都做了些什么事情呢？主要是接受连接、解析请求数据、处理请求和发送响应这几个步骤。 

### Tomcat整体架构

#### Http服务器请求处理

浏览器发给服务端的是一个HTTP格式的请求，HTTP服务器收到这个请求后，需要调用服 务端程序来处理，所谓的服务端程序就是你写的Java类，一般来说不同的请求需要由不同 的Java类来处理。

<img src="images/image-20230106150250921.png" alt="image-20230106150250921" style="zoom:50%;" />

1） 图1，表示HTTP服务器直接调用具体业务类，它们是紧耦合的。 

2） 图2，HTTP服务器不直接调用业务类，而是把请求交给容器来处理，容器通过Servlet接口调用业务类。因此Servlet接口和Servlet容器的出现，达到了HTTP服务器与业务类解耦的目的。而Servlet接口和Servlet容器这一整套规范叫作Servlet规范。Tomcat按照Servlet规范的要求实现了Servlet容器，同时它们也具有HTTP服务器的功能。作为Java程序员，如果我们要实现新的业务功能，只需要实现一个Servlet，并把它注册到Tomcat（Servlet容器）中，剩下的事情就由Tomcat帮我们处理了。



#### **Servlet**容器工作流程 

为了解耦，HTTP服务器不直接调用Servlet，而是把请求交给Servlet容器来处理，那Servlet容器又是怎么工作的呢？

当客户请求某个资源时，HTTP服务器会用一个ServletRequest对象把客户的请求信息封装起来，然后调用Servlet容器的service方法，Servlet容器拿到请求后，根据请求的URL和Servlet的映射关系，找到相应的Servlet，如果Servlet还没有被加载，就用反射机制创建这个Servlet，并调用Servlet的init方法来完成初始化，接着调用Servlet的service方法来处理请求，把ServletResponse对象返回给HTTP服务器，HTTP服务器会把响应发送给客户端。

<img src="images/image-20230106150658333.png" alt="image-20230106150658333" style="zoom: 50%;" />

#### **Tomcat**整体架构 

我们知道如果要设计一个系统，首先是要了解需求，我们已经了解了Tomcat要实现两个核心功能： 

1） 处理Socket连接，负责网络字节流与Request和Response对象的转化。 

2） 加载和管理Servlet，以及具体处理Request请求。 

因此Tomcat设计了两个核心组件连接器（Connector）和容器（Container）来分别做这两件事情。连接器负责对外交流，容器负责内部处理。

<img src="images/image-20230106150758856.png" alt="image-20230106150758856" style="zoom: 50%;" align="center" />

### 连接器 **- Coyote** 

- Coyote 是Tomcat的连接器框架的名称 , 是Tomcat服务器提供的供客户端访问的外部接口。客户端通过Coyote与服务器建立连接、发送请求并接受响应 。 
- Coyote 封装了底层的网络通信（Socket 请求及响应处理），为Catalina 容器提供了统一的接口，使Catalina 容器与具体的请求协议及IO操作方式完全解耦。
- Coyote 将Socket 输入转换封装为 Request 对象，交由Catalina 容器进行处理，处理请求完成后, Catalina 通过Coyote 提供的Response 对象将结果写入输出流。 
- Coyote 作为独立的模块，只负责具体协议和IO的相关操作， 与Servlet 规范实现没有直接关系，因此即便是 Request 和 Response 对象也并未实现Servlet规范对应的接口， 而是在Catalina 中将他们进一步封装为ServletRequest 和 ServletResponse 。 

![image-20230106150933840](images/image-20230106150933840.png)

### **IO**模型与协议 

在Coyote中 ， Tomcat支持的多种I/O模型和应用层协议，具体包含哪些IO模型和应用层协议，请看下表： 

Tomcat 支持的IO模型（自8.5/9.0 版本起，Tomcat 移除了 对 BIO 的支持）：

![image-20230106151031052](images/image-20230106151031052.png)

Tomcat 支持的应用层协议：

<img src="images/image-20230106151049251.png" alt="image-20230106151049251" style="zoom:67%;" />

协议分层 ： 

<img src="images/image-20230106151107040.png" alt="image-20230106151107040" style="zoom:67%;" />

在 8.0 之前 ， Tomcat 默认采用的I/O方式为 BIO ， 之后改为 NIO。 无论 NIO、NIO2 还是 APR， 在性能方面均优于以往的BIO。 如果采用APR， 甚至可以达到 Apache HTTP Server 的影响性能。

Tomcat为了实现支持多种I/O模型和应用层协议，**一个容器可能对接多个连接器**，就好比一个房间有多个门。**但是单独的连接器或者容器都不能对外提供服务，需要把它们组装起来才能工作，组装后这个整体叫作Service组件。**这里请你注意，Service本身没有做什么重要的事情，只是在连接器和容器外面多包了一层，把它们组装在一起。Tomcat内可能有多个Service，这样的设计也是出于灵活性的考虑。通过在Tomcat中配置多个Service，可以实现通过不同的端口号来访问同一台机器上部署的不同应用。

### 连接器组件 

![image-20230106160317728](images/image-20230106160317728.png)

连接器中的各个组件的作用如下： 

#### EndPoint 

1） EndPoint ： Coyote 通信端点，即通信监听的接口，是具体Socket接收和发送处理器，是对传输层的抽象，因此EndPoint用来实现TCP/IP协议的。 

2） Tomcat 并没有EndPoint 接口，而是提供了一个抽象类AbstractEndpoint ， 里面定义了两个内部类：Acceptor和SocketProcessor。Acceptor用于监听Socket连接请求。 SocketProcessor用于处理接收到的Socket请求，它实现Runnable接口，在Run方法里调用协议处理组件Processor进行处理。为了提高处理能力，SocketProcessor被提交到线程池来执行。而这个线程池叫作执行器（Executor)，我在后面的专栏会详细介绍Tomcat如何扩展原生的Java线程池。 

#### Processor

Processor ： Coyote 协议处理接口 ，如果说EndPoint是用来实现TCP/IP协议的，那么Processor用来实现HTTP协议，Processor接收来自EndPoint的Socket，读取字节流解析成Tomcat Request和Response对象，并通过Adapter将其提交到容器处理，Processor是对应用层协议的抽象。 

#### ProtocolHandler

ProtocolHandler： Coyote 协议接口， 通过Endpoint 和 Processor ， 实现针对具体协议的处理能力。Tomcat 按照协议和I/O 提供了6个实现类 ： AjpNioProtocol ，AjpAprProtocol， AjpNio2Protocol ， Http11NioProtocol ，Http11Nio2Protocol ， Http11AprProtocol。我们在配置tomcat/conf/server.xml 时 ， 至少要指定具体的ProtocolHandler , 当然也可以指定协议名称 ， 如 ： HTTP/1.1 ，如果安装了APR，那么将使用Http11AprProtocol ， 否则使用 Http11NioProtocol 。 

#### Adapter

由于协议不同，客户端发过来的请求信息也不尽相同，Tomcat定义了自己的Request类来“存放”这些请求信息。ProtocolHandler接口负责解析请求并生成Tomcat Request类。但是这个Request对象不是标准的ServletRequest，也就意味着，不能用TomcatRequest作为参数来调用容器。Tomcat设计者的解决方案是引入CoyoteAdapter，这是适配器模式的经典运用，连接器调用CoyoteAdapter的Sevice方法，传入的是TomcatRequest对象，CoyoteAdapter负责将Tomcat Request转成ServletRequest，再调用容器的Service方法。 

### 容器 - Catalina 

Tomcat是一个由一系列可配置的组件构成的Web容器，而Catalina是Tomcat的servlet容器。Catalina 是Servlet 容器实现，包含了之前讲到的所有的容器组件，以及后续章节涉及到的安全、会话、集群、管理等Servlet 容器架构的各个方面。它通过松耦合的方式集成Coyote，以完成按照请求协议进行数据读写。同时，它还包括我们的启动入口、Shell程序等。 

#### Tomcat 的模块分层结构图

<img src="images/image-20230106161827670.png" alt="image-20230106161827670" style="zoom:50%;" />

Tomcat 本质上就是一款 Servlet 容器， 因此Catalina 才是 Tomcat 的核心 ， 其他模块都是为Catalina 提供支撑的。 比如 ： 通过Coyote 模块提供链接通信Jasper 模块提供JSP引擎，Naming 提供JNDI 服务，Juli 提供日志服务。

#### Catalina 的主要组件结构

<img src="images/image-20230106161924189.png" alt="image-20230106161924189" style="zoom:50%;" />

如上图所示，Catalina负责管理Server，而Server表示着整个服务器。Server下面有多个服务Service，每个服务都包含着多个连接器组件Connector（Coyote 实现）和一个容器组件Container。在Tomcat 启动的时候， 会初始化一个Catalina的实例。 

Catalina 各个组件的职责:

<img src="images/image-20230106162020538.png" alt="image-20230106162020538" style="zoom:50%;" />

#### Container 结构

Tomcat设计了4种容器，分别是Engine、Host、Context和Wrapper。这4种容器不是平行关系，而是父子关系。， Tomcat通过一种分层的架构，使得Servlet容器具有很好的灵活性。 

<img src="images/image-20230106162443148.png" alt="image-20230106162443148" style="zoom:50%;" />

各个组件的含义 ：

<img src="images/image-20230106162508104.png" alt="image-20230106162508104" style="zoom:50%;" />

我们也可以再通过Tomcat的server.xml配置文件来加深对Tomcat容器的理解。Tomcat 采用了组件化的设计，它的构成组件都是可配置的，其中最外层的是Server，其他组件按照一定的格式要求配置在这个顶层容器中。 

<img src="images/image-20230106162631412.png" alt="image-20230106162631412" style="zoom:50%;" />

那么，Tomcat是怎么管理这些容器的呢？你会发现这些容器具有父子关系，形成一个树形结构，你可能马上就想到了设计模式中的组合模式。没错，Tomcat就是用组合模式来管理这些容器的。具体实现方法是，所有容器组件都实现了Container接口，因此组合模式可以使得用户对单容器对象和组合容器对象的使用具有一致性。这里单容器对象指的是最底层的Wrapper，组合容器对象指的是上面的Context、Host或者Engine。

<img src="images/image-20230106162710785.png" alt="image-20230106162710785" style="zoom:50%;" />

Container 接口中提供了以下方法（截图中知识一部分方法） ：

<img src="images/image-20230106162742663.png" alt="image-20230106162742663" style="zoom:50%;" />

Container接口扩展了LifeCycle接口，LifeCycle接口用来统一管理各组件的生命周期，后面我也用专门的篇幅去详细介绍.





## **Tomcat** 启动流程 

### 启动流程

![image-20230106162859381](images/image-20230106162859381.png)

步骤 : 

1） 启动tomcat ， 需要调用 bin/startup.bat (在linux 目录下 , 需要调用 bin/startup.sh) ， 在startup.bat 脚本中, 调用了catalina.bat。 

2） 在catalina.bat 脚本文件中，调用了BootStrap 中的main方法。 

3）在BootStrap 的main 方法中调用了 init 方法 ， 来创建Catalina 及 初始化类加载器。 

4）在BootStrap 的main 方法中调用了 load 方法 ， 在其中又调用了Catalina的load方法。

5）在Catalina 的load 方法中 , 需要进行一些初始化的工作, 并需要构造Digester 对象, 用于解析 XML。 

6） 然后在调用后续组件的初始化操作 。。。 

加载Tomcat的配置文件，初始化容器组件 ，监听对应的端口号， 准备接受客户端请求 。

### 源码解析 

#### Lifecycle

由于所有的组件均存在初始化、启动、停止等生命周期方法，拥有生命周期管理的特性， 所以Tomcat在设计的时候， 基于生命周期管理抽象成了一个接口 Lifecycle ，而组件 Server、Service、Container、Executor、Connector 组件 ， 都实现了一个生命周期的接口，从而具有了以下生命周期中的核心方法：

- init（）：初始化组件 
- start（）：启动组件 
- stop（）：停止组件 
- destroy（）：销毁组件 

<img src="images/image-20230106163137990.png" alt="image-20230106163137990" style="zoom:50%;" />

#### 各组件的默认实现 

上面我们提到的Server、Service、Engine、Host、Context都是接口， 下图中罗列了这些接口的默认实现类。当前对于 Endpoint组件来说，在Tomcat中没有对应的Endpoint接口， 但是有一个抽象类 AbstractEndpoint ，其下有三个实现类： NioEndpoint、Nio2Endpoint、AprEndpoint ， 这三个实现类，分别对应于前面讲解链接器 Coyote时， 提到的链接器支持的三种IO模型：NIO，NIO2，APR ， Tomcat8.5版本中，默认采用的是 NioEndpoint。

<img src="images/image-20230106163227179.png" alt="image-20230106163227179" style="zoom:50%;" />

ProtocolHandler ： Coyote协议接口，通过封装Endpoint和Processor ， 实现针对具体协议的处理功能。Tomcat按照协议和IO提供了6个实现类。 

AJP协议： 

1） AjpNioProtocol ：采用NIO的IO模型。 

2） AjpNio2Protocol：采用NIO2的IO模型。 

3） AjpAprProtocol ：采用APR的IO模型，需要依赖于APR库。 

HTTP协议： 

1） Http11NioProtocol ：采用NIO的IO模型，默认使用的协议（如果服务器没有安装APR）。 

2） Http11Nio2Protocol：采用NIO2的IO模型。 

3） Http11AprProtocol ：采用APR的IO模型，需要依赖于APR库

<img src="images/image-20230106163312307.png" alt="image-20230106163312307" style="zoom:50%;" />

## Tomcat请求处理流程 

### 请求流程 

设计了这么多层次的容器，Tomcat是怎么确定每一个请求应该由哪个Wrapper容器里的Servlet来处理的呢？答案是，Tomcat是用Mapper组件来完成这个任务的。 

Mapper组件的功能就是将用户请求的URL定位到一个Servlet，它的工作原理是：  

Mapper组件里保存了Web应用的配置信息，其实就是容器组件与访问路径的映射关系，比如Host容器里配置的域名、Context容器里的Web应用路径，以及Wrapper容器里Servlet映射的路径，你可以想象这些配置信息就是一个多层次的Map。当一个请求到来时，Mapper组件通过解析请求URL里的域名和路径，再到自己保存的Map里去查找，就能定位到一个Servlet。请你注意，一个请求URL最后只会定位到一个Wrapper容器，也就是一个Servlet。下面的示意图中 ， 就描述了 当用户请求链接 http://www.itcast.cn/bbs/findAll 之后, 是如何找到最终处理业务逻辑的servlet 。 

![image-20230106163428893](images/image-20230106163428893.png)

那上面这幅图只是描述了根据请求的URL如何查找到需要执行的Servlet ， 那么下面我们再来解析一下 ， 从Tomcat的设计架构层面来分析Tomcat的请求处理。 

![image-20230106163450097](images/image-20230106163450097.png)

步骤如下: 

1. Connector组件Endpoint中的Acceptor监听客户端套接字连接并接收Socket。 
2. 将连接交给线程池Executor处理，开始执行请求响应任务。 
3. Processor组件读取消息报文，解析请求行、请求体、请求头，封装成Request对象。 
4. Mapper组件根据请求行的URL值和请求头的Host值匹配由哪个Host容器、Context容器、Wrapper容器处理请求。 
5.  CoyoteAdaptor组件负责将Connector组件和Engine容器关联起来，把生成的Request对象和响应对象Response传递到Engine容器中，调用 Pipeline。 

6) Engine容器的管道开始处理，管道中包含若干个Valve、每个Valve负责部分处理逻辑。执行完Valve后会执行基础的 Valve--StandardEngineValve，负责调用Host容器的Pipeline。 
7) Host容器的管道开始处理，流程类似，最后执行 Context容器的Pipeline。 
8) Context容器的管道开始处理，流程类似，最后执行 Wrapper容器的Pipeline。 
9) Wrapper容器的管道开始处理，流程类似，最后执行 Wrapper容器对应的Servlet对象的 处理方法

### 请求流程源码解析 

<img src="images/image-20230106163657900.png" alt="image-20230106163657900" style="zoom:50%;" />

在前面所讲解的Tomcat的整体架构中，我们发现Tomcat中的各个组件各司其职，组件之间松耦合，确保了整体架构的可伸缩性和可拓展性，那么在组件内部，如何增强组件的灵活性和拓展性呢？ 在Tomcat中，每个Container组件采用责任链模式来完成具体的请求处理。 

在Tomcat中定义了Pipeline 和 Valve 两个接口，Pipeline 用于构建责任链， 后者代表责任链上的每个处理器。Pipeline 中维护了一个基础的Valve，它始终位于Pipeline的末端（最后执行），封装了具体的请求处理和输出响应的过程。当然，我们也可以调用addValve()方法， 为Pipeline 添加其他的Valve， 后添加的Valve 位于基础的Valve之前，并按照添加顺序执行。Pipiline通过获得首个Valve来启动整合链条的执行 。 