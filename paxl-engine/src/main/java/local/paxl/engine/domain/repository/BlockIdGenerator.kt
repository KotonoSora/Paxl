package local.paxl.engine.domain.repository

/**
 * Identity-generation port so the domain model stays deterministic and free
 * of `UUID`/platform concerns. Infrastructure provides the implementation.
 */
fun interface BlockIdGenerator {
    fun generate(): String
}
