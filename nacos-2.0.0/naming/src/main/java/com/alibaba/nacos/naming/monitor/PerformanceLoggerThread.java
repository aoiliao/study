/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

package com.alibaba.nacos.naming.monitor;

import com.alibaba.nacos.naming.consistency.persistent.ClusterVersionJudgement;
import com.alibaba.nacos.naming.consistency.persistent.raft.RaftCore;
import com.alibaba.nacos.naming.consistency.persistent.raft.RaftPeer;
import com.alibaba.nacos.naming.core.ServiceManager;
import com.alibaba.nacos.naming.misc.GlobalExecutor;
import com.alibaba.nacos.naming.misc.Loggers;
import com.alibaba.nacos.naming.misc.NamingExecuteTaskDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * Logger thread for print performance.
 *
 * @author nacos
 */

@Component
public class PerformanceLoggerThread {
    
    @Autowired
    private ServiceManager serviceManager;
    
    @Autowired
    private RaftCore raftCore;
    
    @Autowired
    private ClusterVersionJudgement versionJudgement;
    
    private static final long PERIOD = 60;
    
    @PostConstruct
    public void init() {
        start();
    }
    
    private void start() {
        PerformanceLogTask task = new PerformanceLogTask();
        GlobalExecutor.schedulePerformanceLogger(task, 30, PERIOD, TimeUnit.SECONDS);
    }
    
    /**
     * Refresh metrics.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshMetrics() {
        MetricsMonitor.resetAll();
    }
    
    /**
     * collect metrics.
     */
    @Scheduled(cron = "0/15 * * * * ?")
    public void collectMetrics() {
        MetricsMonitor.getDomCountMonitor().set(com.alibaba.nacos.naming.core.v2.ServiceManager.getInstance().size());
        MetricsMonitor.getAvgPushCostMonitor().set(getAvgPushCost());
        metricsRaftLeader();
    }
    
    /**
     * Will deprecated after v1.4.x
     */
    @Deprecated
    private void metricsRaftLeader() {
        if (!versionJudgement.allMemberIsNewVersion()) {
            if (raftCore.isLeader()) {
                MetricsMonitor.getLeaderStatusMonitor().set(1);
            } else if (raftCore.getPeerSet().local().state == RaftPeer.State.FOLLOWER) {
                MetricsMonitor.getLeaderStatusMonitor().set(0);
            } else {
                MetricsMonitor.getLeaderStatusMonitor().set(2);
            }
        }
    }
    
    class PerformanceLogTask implements Runnable {
        
        private int logCount = 0;
        
        @Override
        public void run() {
            try {
                logCount %= 10;
                if (logCount == 0) {
                    Loggers.PERFORMANCE_LOG
                            .info("PERFORMANCE:|serviceCount|ipCount|maxPushCost|avgPushCost|totalPushCount|failPushCount");
                }
                int serviceCount = com.alibaba.nacos.naming.core.v2.ServiceManager.getInstance().size();
                int ipCount = MetricsMonitor.getIpCountMonitor().get();
                long maxPushCost = MetricsMonitor.getMaxPushCostMonitor().get();
                long avgPushCost = getAvgPushCost();
                long totalPushCount = MetricsMonitor.getTotalPushMonitor().longValue();
                long failPushCount = MetricsMonitor.getFailedPushMonitor().longValue();
                Loggers.PERFORMANCE_LOG
                        .info("PERFORMANCE:|{}|{}|{}|{}|{}|{}", serviceCount, ipCount, maxPushCost, avgPushCost,
                                totalPushCount, failPushCount);
                Loggers.PERFORMANCE_LOG
                        .info("Task worker status: \n" + NamingExecuteTaskDispatcher.getInstance().workersStatus());
                logCount++;
                MetricsMonitor.getTotalPushCountForAvg().set(0);
                MetricsMonitor.getTotalPushCostForAvg().set(0);
                MetricsMonitor.getMaxPushCostMonitor().set(-1);
            } catch (Exception e) {
                Loggers.SRV_LOG.warn("[PERFORMANCE] Exception while print performance log.", e);
            }
            
        }
    }
    
    private long getAvgPushCost() {
        int size = MetricsMonitor.getTotalPushCountForAvg().get();
        long totalCost = MetricsMonitor.getTotalPushCostForAvg().get();
        return (size > 0 && totalCost > 0) ? totalCost / size : -1;
    }
}
