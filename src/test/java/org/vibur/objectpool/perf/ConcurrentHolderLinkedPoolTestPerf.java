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

package org.vibur.objectpool.perf;

import org.vibur.objectpool.ConcurrentHolderLinkedPool;
import org.vibur.objectpool.Holder;
import org.vibur.objectpool.HolderValidatingPoolService;
import org.vibur.objectpool.SimpleObjectFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Simeon Malchev
 */
public class ConcurrentHolderLinkedPoolTestPerf {

    private static final int INITIAL_SIZE = 10;
    private static final int MAX_SIZE = 100;
    private static final int ITERATIONS = 50000;
    private static final int TIMEOUT = 5000;
    private static final int THREADS_COUNT = 100;
    private static final boolean FAIR = true;

    public static void main(String[] args) {

        // Creates a pool with INITIAL_SIZE and MAX_SIZE, and starts THREADS_COUNT threads where each thread
        // executes ITERATIONS times take and then immediately restore operation on an object from the pool.
        // Each take call has TIMEOUT in ms and the number of unsuccessful calls is recorded.
        // Measures and reports the total time taken by the test in ms.

        final HolderValidatingPoolService<Object> pool = new ConcurrentHolderLinkedPool<Object>(
                new SimpleObjectFactory(), INITIAL_SIZE, MAX_SIZE, FAIR);

        long start = System.currentTimeMillis();
        final AtomicInteger unsuccessful = new AtomicInteger(0);
        Runnable r = new Runnable() {
            public void run() {
                for (int i = 0; i < ITERATIONS; i++) {
                    Holder<Object> hObj = pool.tryTake(TIMEOUT, TimeUnit.MILLISECONDS);
                    if (hObj != null)
                        pool.restore(hObj);
                    else
                        unsuccessful.incrementAndGet();
                }
            }
        };

        int threadsCount = THREADS_COUNT;
        Thread[] threads = new Thread[threadsCount];
        for (int i = 0; i < threadsCount; i++) {
            threads[i] = new Thread(r);
            threads[i].start();
        }
        for (int i = 0; i < threadsCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(String.format("Total execution time %dms, unsuccessful takes %d.",
            (System.currentTimeMillis() - start), unsuccessful.get()));

        pool.terminate();
    }
}
