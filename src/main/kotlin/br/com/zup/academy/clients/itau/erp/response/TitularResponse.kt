package br.com.zup.academy.clients.itau.erp.response

import br.com.zup.academy.conta.Titular
import java.util.*

data class TitularResponse(
    val nome: String,
    val cpf: String,
    val id: String,
) {
    fun toTitular(): Titular {
        return Titular(nome, cpf, UUID.fromString(id))
    }
}