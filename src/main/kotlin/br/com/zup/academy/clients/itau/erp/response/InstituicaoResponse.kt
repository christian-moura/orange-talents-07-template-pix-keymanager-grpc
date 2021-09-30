package br.com.zup.academy.clients.itau.erp.response

import br.com.zup.academy.conta.Instituicao

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
) {
    fun toInstituicao():Instituicao{
        return Instituicao(nome, ispb)
    }
}