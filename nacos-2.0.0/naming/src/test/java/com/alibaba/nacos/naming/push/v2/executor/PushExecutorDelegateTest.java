/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.nacos.naming.push.v2.executor;

import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.api.remote.PushCallBack;
import com.alibaba.nacos.naming.pojo.Subscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PushExecutorDelegateTest {
    
    private final String udpClientId = "1.1.1.1:60000";
    
    private final String rpcClientId = UUID.randomUUID().toString();
    
    @Mock
    private PushExecutorRpcImpl pushExecutorRpc;
    
    @Mock
    private PushExecutorUdpImpl pushExecutorUdp;
    
    @Mock
    private Subscriber subscriber;
    
    @Mock
    private PushCallBack pushCallBack;
    
    private ServiceInfo serviceInfo;
    
    private PushExecutorDelegate delegate;
    
    @Before
    public void setUp() throws Exception {
        serviceInfo = new ServiceInfo("G@@S");
        delegate = new PushExecutorDelegate(pushExecutorRpc, pushExecutorUdp);
    }
    
    @Test
    public void testDoPushForUdp() {
        delegate.doPush(udpClientId, subscriber, serviceInfo);
        verify(pushExecutorUdp).doPush(udpClientId, subscriber, serviceInfo);
    }
    
    @Test
    public void testDoPushForRpc() {
        delegate.doPush(rpcClientId, subscriber, serviceInfo);
        verify(pushExecutorRpc).doPush(rpcClientId, subscriber, serviceInfo);
    }
    
    @Test
    public void doPushWithCallbackForUdp() {
        delegate.doPushWithCallback(udpClientId, subscriber, serviceInfo, pushCallBack);
        verify(pushExecutorUdp).doPushWithCallback(udpClientId, subscriber, serviceInfo, pushCallBack);
    }
    
    @Test
    public void doPushWithCallbackForRpc() {
        delegate.doPushWithCallback(rpcClientId, subscriber, serviceInfo, pushCallBack);
        verify(pushExecutorRpc).doPushWithCallback(rpcClientId, subscriber, serviceInfo, pushCallBack);
    }
}
