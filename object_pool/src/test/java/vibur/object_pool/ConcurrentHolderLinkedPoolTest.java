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

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Simeon Malchev
 */
public class ConcurrentHolderLinkedPoolTest {

    private HolderValidatingPoolService<Object> chlp = null;

    @After
    public void tearDown() throws Exception {
        if (chlp != null )
            chlp.terminate();
        chlp = null;
    }

    @Test
    public void testSimpleTakes() throws Exception {
        chlp = new ConcurrentHolderLinkedPool<Object>(
                new SimpleObjectFactory(), 1, 3, false);

        Holder<Object> hobj1 = chlp.take();
        Holder<Object> hobj2 = chlp.take();
        Holder<Object> hobj3 = chlp.take();
        Holder<Object> hobj4 = chlp.tryTake();

        assertNotNull(hobj1.getTarget());
        assertNotNull(hobj2.getTarget());
        assertNotNull(hobj3.getTarget());
        assertNull(hobj4);

        assertTrue(chlp.restore(hobj1));
        assertTrue(chlp.restore(hobj2));
        assertTrue(chlp.restore(hobj3));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleMetrics() throws Exception {
        chlp = new ConcurrentHolderLinkedPool<Object>(
                new SimpleObjectFactory(), 1, 10, false);

        // tests the initial pool state
        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(1, chlp.createdTotal());
        assertEquals(1, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(0, chlp.takenCount());

        // takes one object and test
        Holder<Object> hobj1 = chlp.take();
        assertNotNull(hobj1.getTarget());
        assertEquals(1, chlp.createdTotal());
        assertEquals(0, chlp.remainingCreated());
        assertEquals(9, chlp.remainingCapacity());
        assertEquals(1, chlp.taken());
        assertEquals(1, chlp.takenCount());

        // restores one object and test
        assertTrue(chlp.restore(hobj1));
        assertEquals(1, chlp.createdTotal());
        assertEquals(1, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(1, chlp.takenCount());

        // takes all objects and test
        Object[] hobjs = new Object[10];
        for (int i = 0; i < 10; i++) {
            hobjs[i] = chlp.take();
            assertNotNull(((Holder<Object>) hobjs[i]).getTarget());
        }
        hobj1 = chlp.tryTake();
        assertNull(hobj1);
        assertEquals(10, chlp.createdTotal());
        assertEquals(0, chlp.remainingCreated());
        assertEquals(0, chlp.remainingCapacity());
        assertEquals(10, chlp.taken());
        assertEquals(11, chlp.takenCount());

        // restores the first 6 objects and test
        for (int i = 0; i < 6; i++) {
            assertTrue(chlp.restore((Holder<Object>) hobjs[i]));
        }
        assertEquals(10, chlp.createdTotal());
        assertEquals(6, chlp.remainingCreated());
        assertEquals(6, chlp.remainingCapacity());
        assertEquals(4, chlp.taken());
        assertEquals(11, chlp.takenCount());

        // restores the remaining 4 objects and test
        for (int i = 6; i < 10; i++) {
            assertTrue(chlp.restore((Holder<Object>) hobjs[i]));
        }
        assertEquals(10, chlp.createdTotal());
        assertEquals(10, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(11, chlp.takenCount());

        // terminates the pool and test
        chlp.terminate();
        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(0, chlp.createdTotal());
        assertEquals(0, chlp.remainingCreated());
        assertEquals(0, chlp.remainingCapacity());
        assertEquals(10, chlp.taken());
        assertEquals(11, chlp.takenCount());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPoolReductions() throws Exception {
        chlp = new ConcurrentHolderLinkedPool<Object>(
                new SimpleObjectFactory(), 1, 10, false);

        // takes all objects and test
        Object[] hobjs = new Object[10];
        for (int i = 0; i < 10; i++) {
            hobjs[i] = chlp.take();
            assertNotNull(((Holder<Object>) hobjs[i]).getTarget());
        }
        Object obj1 = chlp.tryTake();
        assertNull(obj1);

        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(10, chlp.createdTotal());
        assertEquals(0, chlp.remainingCreated());
        assertEquals(0, chlp.remainingCapacity());
        assertEquals(10, chlp.taken());
        assertEquals(10, chlp.takenCount());

        // restores all objects and test
        for (int i = 0; i < 10; i++) {
            assertTrue(chlp.restore((Holder<Object>) hobjs[i]));
        }
        assertEquals(10, chlp.createdTotal());
        assertEquals(10, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(10, chlp.takenCount());

        // reduce the number of created objects in the pool by 5 and test
        int reduction = chlp.reduceCreated(5);
        assertEquals(5, reduction);

        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(5, chlp.createdTotal());
        assertEquals(5, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(10, chlp.takenCount());

        // now takes again all objects
        for (int i = 0; i < 10; i++) {
            hobjs[i] = chlp.take();
            assertNotNull(((Holder<Object>) hobjs[i]).getTarget());
        }
        obj1 = chlp.tryTake();
        assertNull(obj1);
        // then restores again all objects and test
        for (int i = 0; i < 10; i++) {
            assertTrue(chlp.restore((Holder<Object>) hobjs[i]));
        }

        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(10, chlp.createdTotal());
        assertEquals(10, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(20, chlp.takenCount());

        // drain all created objects from the pool and test
        int drained = chlp.drainCreated();
        assertEquals(9, drained);

        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(1, chlp.createdTotal());
        assertEquals(1, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(20, chlp.takenCount());

        // now takes 5 objects and test
        for (int i = 0; i < 5; i++) {
            hobjs[i] = chlp.take();
            assertNotNull(((Holder<Object>) hobjs[i]).getTarget());
        }
        assertEquals(1, chlp.initialSize());
        assertEquals(10, chlp.maxSize());

        assertEquals(5, chlp.createdTotal());
        assertEquals(0, chlp.remainingCreated());
        assertEquals(5, chlp.remainingCapacity());
        assertEquals(5, chlp.taken());
        assertEquals(25, chlp.takenCount());
    }

    @Test
    public void testValidations() throws Exception {
        // todo
    }

    @Test
    public void testTakenHolders() throws Exception {
        // todo - including stack traces
    }

    // todo refactor this after moving the reducer to external class
    @Test
    @SuppressWarnings("unchecked")
    public void testPoolAutoShrinking() throws Exception {
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch reductionLatch = new CountDownLatch(3);
        chlp = new ConcurrentHolderLinkedPool<Object>(
                new SimpleObjectFactory(), 10, 100, false, 100, TimeUnit.MILLISECONDS,
                new SynchronizedDefaultPoolReducer(startLatch, reductionLatch));

        // tests the initial pool state
        assertEquals(10, chlp.initialSize());
        assertEquals(100, chlp.maxSize());

        assertEquals(10, chlp.createdTotal());
        assertEquals(10, chlp.remainingCreated());
        assertEquals(100, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(0, chlp.takenCount());

        // takes 90 objects and test
        Object[] hobjs = new Object[90];
        for (int i = 0; i < 90; i++) {
            hobjs[i] = chlp.take();
            assertNotNull(((Holder<Object>) hobjs[i]).getTarget());
        }
        assertEquals(90, chlp.createdTotal());
        assertEquals(0, chlp.remainingCreated());
        assertEquals(10, chlp.remainingCapacity());
        assertEquals(90, chlp.taken());
        assertEquals(90, chlp.takenCount());

        // restores 90 objects and test
        for (int i = 0; i < 90; i++) {
            assertTrue(chlp.restore((Holder<Object>) hobjs[i]));
        }
        assertEquals(90, chlp.createdTotal());
        assertEquals(90, chlp.remainingCreated());
        assertEquals(100, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(90, chlp.takenCount());

        // enable the pool reducer
        startLatch.countDown();
        // await for 3 calls to the pool reducer to be done:
        // on the first call no reduction will happen
        // on the second call a reduction of 9 objects should happen
        // on the third call a reduction of 8 objects should happen
        // the total reduction should be 17, that's why the created total will drop from 90 to 73.
        reductionLatch.await();

        // tests the pool metrics after the reducer was called 3 times
        assertEquals(73, chlp.createdTotal());
        assertEquals(73, chlp.remainingCreated());
        assertEquals(100, chlp.remainingCapacity());
        assertEquals(0, chlp.taken());
        assertEquals(90, chlp.takenCount());
    }
}
