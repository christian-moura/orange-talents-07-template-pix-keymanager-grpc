package br.com.zup.academy.clients.itau.erp.response

import br.com.zup.academy.conta.Conta
import br.com.zup.academy.conta.InstituicaoRepository
import br.com.zup.academy.conta.TipoConta
import br.com.zup.academy.conta.TitularRepository
import java.util.*

data class DetalhesContaErpItauResponse(
    val tipo: String,
    var instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    var titular: TitularResponse
) {

//    fun toConta(): Conta {
//        return Conta(
//            TipoConta.valueOf(this.tipo),
//            this.instituicao.toInstituicao(),
//            this.agencia,
//            this.numero,
//            this.titular.toTitular()
//        )
//    }

    fun toConta(instituicaoRepository: InstituicaoRepository, titularRepository: TitularRepository): Conta {
        val existeIntituicao = instituicaoRepository.findById(this.instituicao.ispb)
        val existeTitular = titularRepository.findById(UUID.fromString(this.titular.id))
        val instituicao =
            if (existeIntituicao.isPresent) existeIntituicao.get() else instituicaoRepository.saveAndFlush(this.instituicao.toInstituicao())
        val titular =
            if (existeTitular.isPresent) existeTitular.get() else titularRepository.saveAndFlush(this.titular.toTitular())

        return Conta(
            TipoConta.valueOf(this.tipo),
            instituicao,
            this.agencia,
            this.numero,
            titular
        )
    }
}