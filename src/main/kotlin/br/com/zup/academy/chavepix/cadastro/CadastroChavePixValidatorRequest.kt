package br.com.zup.academy.chavepix.cadastro

import br.com.zup.academy.chavepix.ChavePix
import br.com.zup.academy.chavepix.TipoChave
import br.com.zup.academy.conta.Conta
import br.com.zup.academy.conta.TipoConta
import br.com.zup.academy.shared.validation.ValidPixKey
import br.com.zup.edu.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@ValidPixKey
@Introspected
data class CadastroChavePixValidatorRequest(
    @field:ValidUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipo: TipoChave,
    @field:Size(max = 77)
    val chave: String?,
    @field:NotNull
    val tipoDeConta: TipoConta
) {

    fun toChavePix(conta: Conta): ChavePix {
        return ChavePix(
            conta,
            this.tipo,
            if (this.tipo == TipoChave.ALEATORIA) UUID.randomUUID().toString() else this.chave!!
        )
    }
}