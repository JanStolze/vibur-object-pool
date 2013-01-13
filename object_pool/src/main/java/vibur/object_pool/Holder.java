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


/**
 * An interface which is to be implemented by the wrapper class enclosing the taken
 * from this object pool objects.
 *
 * @param <T> the type of objects held in this object pool
 */
public interface Holder<T> {

    /**
     * Returns the underlying object which has been taken from this object pool.
     *
     * @return the underlying object
     */
    T getTarget();
}
