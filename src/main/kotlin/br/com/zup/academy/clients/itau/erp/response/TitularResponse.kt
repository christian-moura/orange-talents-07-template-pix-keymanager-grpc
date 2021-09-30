package br.com.zup.academy.clients.itau.erp.response

import br.com.zup.academy.conta.Titular

data class TitularResponse(
    val nome: String,
    val cpf: String
) {
    fun toTitular(): Titular {
        return Titular(nome, cpf)
    }
}