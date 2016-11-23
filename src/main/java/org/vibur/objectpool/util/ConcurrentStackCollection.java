/**
 * Copyright 2016 Simeon Malchev
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

package org.vibur.objectpool.util;

import com.conversantmedia.util.collection.Stack;
import com.conversantmedia.util.concurrent.ConcurrentStack;


/**
 * A {@link ConcurrentStack} based implementation of {@link ConcurrentCollection}.
 *
 * @author Simeon Malchev
 * @param <T> the type of objects held in this {@code ConcurrentCollection}
 */
public class ConcurrentStackCollection<T> implements ConcurrentCollection<T> {

    private final Stack<T> stack;

    public ConcurrentStackCollection(int capacity) {
        stack = new ConcurrentStack<>(capacity);
    }

    @Override
    public void offerFirst(T object) {
        stack.push(object);
    }

    @Override
    public void offerLast(T object) {
        offerFirst(object);
    }

    @Override
    public T pollFirst() {
        return stack.pop();
    }

    @Override
    public T pollLast() {
        return pollFirst();
    }
}
