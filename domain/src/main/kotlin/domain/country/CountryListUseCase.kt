package domain.country

import domain.Domain
import domain.interactor.PostExecutionThread
import domain.interactor.SingleDisposableUseCase
import kotlin.properties.Delegates

/**
 * A use case for loading countries from the given API.
 * @param page The page to load, 0-indexed. Pagination is only a thing at the client-level since
 * the API is missing it, which means we don't save bandwidth, but we do save memory by avoiding
 * dumping all the results returned by the API into the UI at once.
 * @param postExecutionThread A representation of thread to receive the results of the execution.
 */
abstract class CountryListUseCase(page: Int, postExecutionThread: PostExecutionThread)
    : SingleDisposableUseCase<List<Country>>(
        asyncExecutionScheduler = Domain.useCaseScheduler,
        postExecutionScheduler = postExecutionThread.scheduler()) {
    // This makes sure we do not try to request negative pages
    private var safePage by Delegates.vetoable(0, { _, _, new -> new >= 0 })

    init {
        safePage = page
    }

    /**
     * Description of a factory that creates instances of implementations of this use case.
     */
    interface Factory {
        /**
         * Factory method for the 'fetch' use case.
         */
        fun newFetch(page: Int, postExecutionThread: PostExecutionThread): CountryListUseCase

        /**
         * Factory method for the 'get' use case.
         */
        fun newGet(page: Int, postExecutionThread: PostExecutionThread): CountryListUseCase
    }
}
