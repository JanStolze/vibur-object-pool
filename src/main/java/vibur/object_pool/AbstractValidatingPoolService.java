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
 * Base abstract class which is to be extended and which provides common functionality for
 * validating object pool's implementations. Implemented via composition with a non-validating
 * object pool on top of which the validation will be implemented.
 *
 * @author Simeon Malchev
 * @param <T> the type of objects held in this object pool
 */
public abstract class AbstractValidatingPoolService<T> extends AbstractBasePoolService
        implements BasePoolService {

    final NonValidatingPoolService<T> nonValidatingPoolService;

    /**
     * Default visibility constructor. Sets the non-validating object pool instance, on top of which
     * the validation will be implemented.
     *
     * @param nonValidatingPoolService the non-validating object pool instance
     */
    AbstractValidatingPoolService(NonValidatingPoolService<T> nonValidatingPoolService) {
        this.nonValidatingPoolService = nonValidatingPoolService;
    }


    /** {@inheritDoc} */
    @Override
    public int createdTotal() {
        return nonValidatingPoolService.createdTotal();
    }

    /** {@inheritDoc} */
    @Override
    public int remainingCapacity() {
        return nonValidatingPoolService.remainingCapacity();
    }

    /** {@inheritDoc} */
    @Override
    public int initialSize() {
        return nonValidatingPoolService.initialSize();
    }

    /** {@inheritDoc} */
    @Override
    public int maxSize() {
        return nonValidatingPoolService.maxSize();
    }


    /** {@inheritDoc} */
    @Override
    public int reduceCreated(int reduction) {
        return nonValidatingPoolService.reduceCreated(reduction);
    }


    /** {@inheritDoc} */
    @Override
    public void terminate() {
        nonValidatingPoolService.terminate();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isTerminated() {
        return nonValidatingPoolService.isTerminated();
    }


    /** {@inheritDoc} */
    @Override
    public boolean isFair() {
        return nonValidatingPoolService.isFair();
    }

    /** {@inheritDoc} */
    @Override
    public long takenCount() {
        return nonValidatingPoolService.takenCount();
    }
}
