package domain.interactor

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver

abstract class SingleDisposableUseCase<T> protected constructor(
        /**
         * Send null for in-place synchronous execution
         */
        private val asyncExecutionScheduler: Scheduler,
        private val postExecutionScheduler: Scheduler)
    : DisposableUseCase(), UseCase<Single<T>> {
    fun execute(subscriber: DisposableSingleObserver<T>) {
        assembledSubscriber = buildUseCase()
                .subscribeOn(asyncExecutionScheduler)
                .observeOn(postExecutionScheduler)
                .subscribeWith(subscriber)
    }
}
