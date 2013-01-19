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

import static org.junit.Assert.*;

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
                new SimpleObjectFactory(), 1, 3, false, 0, null, null);

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
                new SimpleObjectFactory(), 1, 10, false, 0, null, null);

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
            chlp.restore((Holder<Object>) hobjs[i]);
        }
        assertEquals(10, chlp.createdTotal());
        assertEquals(6, chlp.remainingCreated());
        assertEquals(6, chlp.remainingCapacity());
        assertEquals(4, chlp.taken());
        assertEquals(11, chlp.takenCount());

        // restores the remaining 4 objects and test
        for (int i = 6; i < 10; i++) {
            chlp.restore((Holder<Object>) hobjs[i]);
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
    public void testValidations() throws Exception {
        // todo
    }

    @Test
    public void testTakenHolders() throws Exception {
        // todo - including stack traces
    }

    @Test
    public void testPoolShrinking() throws Exception {
        // todo
    }
}
