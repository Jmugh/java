/*多个工厂方法模式，是对普通工厂方法模式的改进，
在普通工厂方法模式中，如果传递的字符串出错，则不能正确创建对象，
而多个工厂方法模式是提供多个工厂方法，分别创建对象
*/
interface Sender {							
    public void Send(); 
}						。
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
   public Sender produceMail(){  
        return new MailSender();  
    }  
      
    public Sender produceSms(){  
        return new SmsSender();  
    }  
}	
public class FactoryTest2 {  
  
    public static void main(String[] args) {  
        SendFactory factory = new SendFactory();  
        Sender sender = factory.produceMail();  
        sender.Send();  
    }  
} 