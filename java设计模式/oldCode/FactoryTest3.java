interface Sender {//静态工厂方法模式将上面的多个工厂方法模式里的方法置为静态的，
				  //不需要创建实例，直接调用即可
    public void Send();  
}  
class MailSender implements Sender {  
    @Override  
    public void Send() {  
        System.out.println("this is mailsender!");  
    }  
}  
class SmsSender implements Sender {  
  
    @Override  
    public void Send() {  
        System.out.println("this is sms sender!");  
    }  
} 
class SendFactory {  
      
    public static Sender produceMail(){  
        return new MailSender();  
    }  
      
    public static Sender produceSms(){  
        return new SmsSender();  
    }  
}  

public class FactoryTest3 {  
  
    public static void main(String[] args) {      
        Sender sender = SendFactory.produceMail();  
        sender.Send();  
    }  
}

/*
总体来说，工厂模式适合：凡是出现了大量的产品需要创建，并且具有共同的接口时，
可以通过工厂方法模式进行创建。在以上的三种模式中，第一种如果传入的字符串有误，
不能正确创建对象，第三种相对于第二种，不需要实例化工厂类，
所以，大多数情况下，我们会选用第三种——静态工厂方法模式。
*/