package rpc.client.example;

import rpc.client.RpcClient;
import rpc.facade.Hello;


public class ClientBoot {
  /*  public static void main(String[] args) throws Exception {
        final RpcClient client = new RpcClient("127.0.0.1:8000");
        final int threadNum = 1;
        final int requestNum = 100000;
        long startTime = System.currentTimeMillis();
        Thread[] threads = new Thread[threadNum];
        for (int j = 0; j < threadNum; j++) {
            threads[j] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        final Hello syncClient = client.execute(Hello.class);
                        String result = syncClient.sayHello(String.valueOf(i));
                        //System.out.println(result);
                    }
                }
            });
            threads[j].start();

        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        long timeCost = (System.currentTimeMillis() - startTime);
        String msg = String.format("total request:%s, total time:%sms, req/s=%s", requestNum, timeCost, ((double) (requestNum)) / timeCost * 1000);
        System.out.println(msg);

        RpcClient.stop();
    }*/
}
