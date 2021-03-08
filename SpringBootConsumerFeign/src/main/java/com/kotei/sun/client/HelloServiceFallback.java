package com.kotei.sun.client;

import com.kotei.common.entity.RestfulResult;
import com.kotei.sun.entity.ServiceInfo;
import org.springframework.stereotype.Component;

/**
 * 服务的降级
 */
@Component
public class HelloServiceFallback implements ServiceFeignClient {

    @Override
    public RestfulResult hello(ServiceInfo serviceInfo) {
        RestfulResult restfulResult = new RestfulResult();
        restfulResult.setResult("consumerServiceRibbon异常，Name=" + serviceInfo.getName());
        return restfulResult;
    }

}
