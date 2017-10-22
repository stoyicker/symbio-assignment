package app.detail

internal interface DetailView<in T : Any?> {
    /**
     * Called to notify the implementation that the content should be updated.
     */
    fun updateContent(item: T)
}
