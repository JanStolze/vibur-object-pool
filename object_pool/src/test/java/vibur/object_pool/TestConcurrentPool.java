/**
 * Copyright 2013 Simeon Malchev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vibur.object_pool;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Simeon Malchev
 */
public class TestConcurrentPool {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        final AtomicInteger totalSleep = new AtomicInteger(0);
//        final BasicObjectPool2<Object> bop = new BasicObjectPool2<Object>(new SimpleObjectFactory(), 20, 20);
        final NonValidatingPoolService<Object> bop = new ConcurrentLinkedPool<Object>(
                new SimpleObjectFactory(), 10, 20, false);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    Object obj = bop.tryTake(5000, TimeUnit.MILLISECONDS);
//                    int sleep = (int) (Math.random() * 20);
//                    totalSleep.addAndGet(sleep);
//                    System.out.println(Thread.currentThread().getName() + ", obj = " + obj + ", id = " + id[0] + ", sleep = " + sleep);
//                    Thread.sleep(sleep);
                    bop.restore(obj);
                }
            }
        };

        int threadNum = 200;
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new Thread(r);
            threads[i].start();
        }
        for (int i = 0; i < threadNum; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("millis = " + (System.currentTimeMillis() - start) + ", totalSleep = " + totalSleep.get());
    }
}
