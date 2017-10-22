package app.list

import domain.country.Country
import io.reactivex.observers.DisposableSingleObserver

/**
 * The subscriber that will react to the outcome of the associated use case and request the
 * view to update itself.
 */
internal open class CountryPageLoadSubscriber(
        private val coordinator: CountryListCoordinator,
        private val entityMapper: PresentationCountryEntityMapper)
    : DisposableSingleObserver<List<Country>>() {
    override fun onStart() {
        coordinator.view.apply {
            showLoadingLayout()
            hideContentLayout()
            hideErrorLayout()
        }
    }

    override fun onSuccess(payload: List<Country>) {
        coordinator.apply {
            if (!payload.none()) {
                if (page * PAGE_SIZE >= payload.size) {
                    view.updateContent(emptyList())
                } else {
                    // * is the spread operator
                    view.updateContent(listOf(
                            *payload.subList(
                                    page * PAGE_SIZE,
                                    Math.min((page + 1) * PAGE_SIZE, payload.size))
                                    .map { entityMapper.transform(it) }.toTypedArray()))
                }
                page++
            }
            view.apply {
                hideLoadingLayout()
                hideErrorLayout()
            }
        }
    }

    override fun onError(throwable: Throwable) {
        coordinator.view.apply {
            showErrorLayout()
            hideLoadingLayout()
            hideContentLayout()
        }
    }

    /**
     * Description of a factory that creates page load subscribers.
     */
    internal interface Factory {
        fun newSubscriber(coordinator: CountryListCoordinator)
                : DisposableSingleObserver<List<Country>>
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
