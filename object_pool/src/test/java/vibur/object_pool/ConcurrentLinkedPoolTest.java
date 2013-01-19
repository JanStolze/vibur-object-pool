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
public class ConcurrentLinkedPoolTest {

    private NonValidatingPoolService<Object> clp = null;

    @After
    public void tearDown() throws Exception {
        if (clp != null )
            clp.terminate();
        clp = null;
    }

    @Test
    public void testSimpleTakes() throws Exception {
        clp = new ConcurrentLinkedPool<Object>(
                new SimpleObjectFactory(), 1, 3, false, 0, null, null);

        Object obj1 = clp.take();
        Object obj2 = clp.take();
        Object obj3 = clp.take();
        Object obj4 = clp.tryTake();

        assertNotNull(obj1);
        assertNotNull(obj2);
        assertNotNull(obj3);
        assertNull(obj4);

        clp.restore(obj1);
        clp.restore(obj2);
        clp.restore(obj3);
    }

    @Test
    public void testSimpleMetrics() throws Exception {
        clp = new ConcurrentLinkedPool<Object>(
                new SimpleObjectFactory(), 1, 10, false, 0, null, null);

        // tests the initial pool state
        assertEquals(1, clp.initialSize());
        assertEquals(10, clp.maxSize());

        assertEquals(1, clp.createdTotal());
        assertEquals(1, clp.remainingCreated());
        assertEquals(10, clp.remainingCapacity());
        assertEquals(0, clp.taken());
        assertEquals(0, clp.takenCount());

        // takes one object and test
        Object obj1 = clp.take();
        assertNotNull(obj1);
        assertEquals(1, clp.createdTotal());
        assertEquals(0, clp.remainingCreated());
        assertEquals(9, clp.remainingCapacity());
        assertEquals(1, clp.taken());
        assertEquals(1, clp.takenCount());

        // restores one object and test
        clp.restore(obj1);
        assertEquals(1, clp.createdTotal());
        assertEquals(1, clp.remainingCreated());
        assertEquals(10, clp.remainingCapacity());
        assertEquals(0, clp.taken());
        assertEquals(1, clp.takenCount());

        // takes all objects and test
        Object[] objs = new Object[10];
        for (int i = 0; i < 10; i++) {
            objs[i] = clp.take();
            assertNotNull(objs[i]);
        }
        obj1 = clp.tryTake();
        assertNull(obj1);
        assertEquals(10, clp.createdTotal());
        assertEquals(0, clp.remainingCreated());
        assertEquals(0, clp.remainingCapacity());
        assertEquals(10, clp.taken());
        assertEquals(11, clp.takenCount());

        // restores the first 6 objects and test
        for (int i = 0; i < 6; i++) {
            clp.restore(objs[i]);
        }
        assertEquals(10, clp.createdTotal());
        assertEquals(6, clp.remainingCreated());
        assertEquals(6, clp.remainingCapacity());
        assertEquals(4, clp.taken());
        assertEquals(11, clp.takenCount());

        // restores the remaining 4 objects and test
        for (int i = 6; i < 10; i++) {
            clp.restore(objs[i]);
        }
        assertEquals(10, clp.createdTotal());
        assertEquals(10, clp.remainingCreated());
        assertEquals(10, clp.remainingCapacity());
        assertEquals(0, clp.taken());
        assertEquals(11, clp.takenCount());

        // terminates the pool and test
        clp.terminate();
        assertEquals(1, clp.initialSize());
        assertEquals(10, clp.maxSize());

        assertEquals(0, clp.createdTotal());
        assertEquals(0, clp.remainingCreated());
        assertEquals(0, clp.remainingCapacity());
        assertEquals(10, clp.taken());
        assertEquals(11, clp.takenCount());
    }

    @Test
    public void testPoolShrinking() throws Exception {
        // todo
    }
}
