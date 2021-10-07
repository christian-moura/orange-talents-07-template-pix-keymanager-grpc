package br.com.zup.academy.chavepix.excluir

import br.com.zup.academy.chavepix.ChavePix
import br.com.zup.academy.chavepix.TipoChave
import br.com.zup.academy.conta.Conta
import br.com.zup.academy.conta.TipoConta
import java.time.LocalDateTime
import java.util.*

data class ChavePixInformacao (
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipo: TipoChave,
    val chave: String,
    val tipoDeConta: TipoConta,
    val registradaEm: LocalDateTime = LocalDateTime.now()
    ) {

        companion object {
            fun of(chave: ChavePix): ChavePixInformacao {
                return ChavePixInformacao(
                    pixId = chave.id,
                    clienteId = chave.conta.titular.id,
                    tipo = chave.tipoChave,
                    chave = chave.valorChave,
                    tipoDeConta = chave.conta.tipo,
                )
            }
        }
    }