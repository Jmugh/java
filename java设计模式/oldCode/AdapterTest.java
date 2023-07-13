//适配器模式将某个类的接口转换成客户端期望的另一个接口表示，
//目的是消除由于接口不匹配所造成的类的兼容性问题。
//主要分为三类：类的适配器模式、对象的适配器模式、接口的适配器模式。

class Source {  
  
    public void method1() {  
        System.out.println("this is original method!");  
    }  
}  

interface Targetable {  
  
    /* 与原类中的方法相同 */  
    public void method1();  
  
    /* 新类的方法 */  
    public void method2();  
}  

class Adapter extends Source implements Targetable {  
  
    @Override  
    public void method2() {  
        System.out.println("this is the targetable method!");  
    }  
}  
//Adapter类继承Source类，实现Targetable接口，下面是测试类：

public class AdapterTest {  
  
    public static void main(String[] args) {  
        Targetable target = new Adapter();  
        target.method1();  
        target.method2();  
    }  
}  




//对象适配器
/*
public class Wrapper implements Targetable {  
  
    private Source source;  
      
    public Wrapper(Source source){  
        super();  
        this.source = source;  
    }  
    @Override  
    public void method2() {  
        System.out.println("this is the targetable method!");  
    }  
  
    @Override  
    public void method1() {  
        source.method1();  
    }  
}  

public class AdapterTest {  
  
    public static void main(String[] args) {  
        Source source = new Source();  
        Targetable target = new Wrapper(source);  
        target.method1();  
        target.method2();  
    }  
} 


*/





//接口适配器
/*
public interface Sourceable {  
      
    public void method1();  
    public void method2();  
}  
抽象类Wrapper2：
[java] view plaincopy
public abstract class Wrapper2 implements Sourceable{  
      
    public void method1(){}  
    public void method2(){}  
}  
[java] view plaincopy
public class SourceSub1 extends Wrapper2 {  
    public void method1(){  
        System.out.println("the sourceable interface's first Sub1!");  
    }  
}  
[java] view plaincopy
public class SourceSub2 extends Wrapper2 {  
    public void method2(){  
        System.out.println("the sourceable interface's second Sub2!");  
    }  
}  
[java] view plaincopy
public class WrapperTest {  
  
    public static void main(String[] args) {  
        Sourceable source1 = new SourceSub1();  
        Sourceable source2 = new SourceSub2();  
          
        source1.method1();  
        source1.method2();  
        source2.method1();  
        source2.method2();  
    }  
} 
*/