package rpc.server;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rpc.common.RpcRequest;
import rpc.common.RpcResponse;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

public class BizTask implements Callable<Boolean> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BizTask.class);
    private RpcRequest request = null;
    private RpcResponse response = null;
    private Map<String, Object> handlerMap = null;

    public BizTask(RpcRequest request, RpcResponse response, Map<String, Object> handlerMap) {
        this.request = request;
        this.response = response;
        this.handlerMap = handlerMap;
    }

    @Override
    public Boolean call() throws Exception {
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            if (result != null) {
                response.setResult(result);
                response.setError("");
            } else {
                response.setResult(null);
                response.setError("error");
            }
            return Boolean.TRUE;
        } catch (Throwable t) {
            response.setError(t.toString());
            LOGGER.error("biz deal get error");
            return Boolean.FALSE;
        }
    }

    private Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = handlerMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        // JDK reflect
       /* Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);*/

        // Cglib reflect
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}