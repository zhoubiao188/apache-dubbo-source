package cn.ityoudream.dubbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Application {
    public static void main(String[] args) {
        //加载Dubbo的配置文件
        ClassPathXmlApplicationContext run = new ClassPathXmlApplicationContext("consumer.xml");
        run.start();
        //获取我们的的第一个接口
        DemoService demoService = run.getBean("demoService", DemoService.class);
        //获取第二个接口
        GreetingService greetingService = run.getBean("greetingService", GreetingService.class);

        new Thread(() -> {
            boolean flag = true;
            while (flag) {
                //调用我们的hello方法
                String greetings = greetingService.hello();
                System.out.println("greetings:" + greetings + "from thread");
                flag = false;
                try {
                    Thread.sleep(100);
                }catch (InterruptedException ex) {
                }
            }
        }).start();

        //调用sayHelloAsync
        boolean flag = true;
        while (flag) {
            try {
                CompletableFuture<String> hello = demoService.sayHelloAsync("world");
                System.out.println(hello.get());
                //得到调用结果
                String greetings = greetingService.hello();
                System.out.println("result:" + greetings);
                flag = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}
