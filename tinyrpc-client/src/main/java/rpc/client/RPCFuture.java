package rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;

import java.util.concurrent.*;

public class RPCFuture implements Future<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPCFuture.class);

    private RpcRequest request;
    private RpcResponse response;
    private long startTime;
    private CountDownLatch latch = new CountDownLatch(1);

    private long responseTimeThreshold = 5000;

    public RPCFuture(RpcRequest request) {
        this.request = request;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isDone() {
        if (response != null) {
            return true;
        }
        return false;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        latch.await();
        if (this.response != null) {
            return this.response.getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = latch.await(timeout, unit);
        if (success) {
            if (this.response != null) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.request.getRequestId()
                    + ". Request class name: " + this.request.getClassName()
                    + ". Request method: " + this.request.getMethodName());
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(RpcResponse reponse) {
        this.response = reponse;
        latch.countDown();
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            LOGGER.warn("Service response time is too slow. Request id = " + reponse.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }
}