/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import java.util.concurrent.TimeUnit;

public class RetrieveLostFuture {

    public static void main(String[] args) throws Exception {
        Hazelcast.newHazelcastInstance();

        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        IScheduledExecutorService scheduler = client.getScheduledExecutorService("scheduler");
        IScheduledFuture future = scheduler.schedule(new EchoTask("My Task"), 5, TimeUnit.SECONDS);

        ScheduledTaskHandler handler = future.getHandler();

        client.shutdown();

        HazelcastInstance newClient = HazelcastClient.newHazelcastClient();
        IScheduledExecutorService newScheduler = newClient.getScheduledExecutorService("scheduler");
        IScheduledFuture newFuture = newScheduler.getScheduledFuture(handler);

        Object result = newFuture.get();
        System.out.println("Result: " + result);

        HazelcastClient.shutdownAll();
        Hazelcast.shutdownAll();
    }
}
