package dev.nilsschlegel.kernel

class DomainException(
    override val message: String,
) : RuntimeException(message)
