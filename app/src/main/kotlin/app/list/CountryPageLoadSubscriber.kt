package app.list

import domain.entity.Country
import io.reactivex.observers.DisposableSingleObserver

/**
 * The subscriber that will react to the outcome of the associated use case and request the
 * viewConfig to update itself.
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
                page++
                // * is the spread operator. We use it to build an immutable list.
                view.updateContent(listOf(
                        *payload.map { entityMapper.transform(it) }.toTypedArray()))
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
}
