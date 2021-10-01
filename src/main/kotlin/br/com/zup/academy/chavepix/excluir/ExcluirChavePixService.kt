package br.com.zup.academy.chavepix.excluir

import br.com.zup.academy.chavepix.ChavePixRepository
import br.com.zup.academy.shared.validation.exceptions.ChavePixInexistenteException
import br.com.zup.academy.shared.validation.exceptions.PermissaoNegadaException
import br.com.zup.edu.shared.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.*
import javax.transaction.Transactional
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class ExcluirChavePixService(
    @Inject val chavePixRepository: ChavePixRepository

    ) {

    @Transactional
    fun excluirChavePix(
        @NotBlank @ValidUUID clienteIdRequest: String?,
        @NotBlank @ValidUUID pixIdRequest: String?,
    ){
        val uuidClienteId = UUID.fromString(clienteIdRequest)
        val pixId = UUID.fromString(pixIdRequest)
        val existeChave = chavePixRepository.findById(pixId)
        if (existeChave.isEmpty) throw ChavePixInexistenteException("Chave pix não encontrada pelo id: $pixId ")
        if (existeChave.get().conta.titular.id != uuidClienteId) throw PermissaoNegadaException("Permissão negada para o recurso.")
        chavePixRepository.delete(existeChave.get())
    }
}