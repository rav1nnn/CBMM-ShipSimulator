package com.cbmm.shipsimulator.util

/**
 * Classe selada que representa o resultado de uma operação de rede.
 * Pode ser um sucesso, erro ou carregamento.
 */
sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val isFromCache: Boolean = false
) {
    class Success<T>(data: T, isFromCache: Boolean = false) : NetworkResult<T>(data, null, isFromCache)
    class Error<T>(message: String, data: T? = null) : NetworkResult<T>(data, message)
    class Loading<T> : NetworkResult<T>()
}
