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

package vibur.object_pool.util;

import vibur.object_pool.BasePoolService;

/**
 * The automated shrinking (reduction) of a {@link vibur.object_pool.BasePoolService} is provided via this interface.
 *
 * @author Simeon Malchev
 */
public interface Reducer {

    /**
     * Returns the number of elements by which this object pool needs to be reduced (shrinked).
     *
     * @param poolService       the object pool service which is to be reduced by this reducer
     * @return see above
     */
    int reduceBy(BasePoolService poolService);
}
