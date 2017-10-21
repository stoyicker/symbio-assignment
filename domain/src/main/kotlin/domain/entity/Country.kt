package domain.entity

/**
 * Models the relevant information about a post, but in a way that modules other than data can
 * see it without knowing about how it is retrieved (deserialized).
 */
class Country(val name: String)
