package com.kotei.sun.client;

import com.kotei.common.entity.RestfulResult;
import com.kotei.sun.entity.ServiceInfo;
import org.springframework.stereotype.Component;

/**
 * 服务的降级
 */
@Component
public class ServiceFallback implements ServiceFeignClient {

    @Override
    public RestfulResult hello(ServiceInfo serviceInfo) {
        RestfulResult result = new RestfulResult();
        result.setData("服务调用失败");
        return result;
    }
}
