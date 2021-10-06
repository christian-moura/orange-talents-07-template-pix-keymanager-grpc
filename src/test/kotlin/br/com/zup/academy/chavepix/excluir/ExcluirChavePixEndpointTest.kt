package br.com.zup.academy.chavepix.excluir

import br.com.zup.academy.ExcluirChavePixRequest
import br.com.zup.academy.ExcluirChavePixServiceGrpc
import br.com.zup.academy.chavepix.ChavePix
import br.com.zup.academy.chavepix.ChavePixRepository
import br.com.zup.academy.conta.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*
import javax.transaction.Transactional

@MicronautTest(transactional = false)
internal class ExcluirChavePixEndpointTest(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val instituicaoRepository: InstituicaoRepository,
    @Inject val titularRepository: TitularRepository,
    @Inject val contaRepository: ContaRepository,
    val grpcExcluirClient: ExcluirChavePixServiceGrpc.ExcluirChavePixServiceBlockingStub,
) {

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    @AfterEach
    internal fun tearDown() {
        chavePixRepository.deleteAll()
    }

    @Test
    @Transactional
    fun `deve excluir chave pix com sucesso com os dados atendendo os requisitos`() {

        val instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
        val titular = Titular(
            "Rafael M C Ponte",
            "02467781054",
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890")
        )

        val conta = Conta(
            br.com.zup.academy.conta.TipoConta.CONTA_CORRENTE,
            instituicao,
            "0001",
            "291900",
            titular
        )

        val chavePix = ChavePix(
            conta,
            br.com.zup.academy.chavepix.TipoChave.CPF,
            "02467781054"
        )
        val cadastroResponse = chavePixRepository.saveAndFlush(chavePix)


        val exclusaoChavePixRequest = ExcluirChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setIdPix(cadastroResponse.id.toString())
            .build()

        val exclusaoResponse = grpcExcluirClient.excluir(exclusaoChavePixRequest)
        assertEquals(exclusaoChavePixRequest.idPix, exclusaoResponse.idPix)
        assertEquals(exclusaoChavePixRequest.idCliente, exclusaoResponse.idCliente)
    }


    @Test

    fun ` não deve excluir chave pix com ID pix inválido e deve retornar status INVALID_ARGUMENT`() {
        val exclusaoChavePixRequest = ExcluirChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setIdPix("02467781054")
            .build()

        val error = assertThrows<StatusRuntimeException> {
            val exclusaoResponse = grpcExcluirClient.excluir(exclusaoChavePixRequest)
        }
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("excluirChavePix.pixIdRequest: não é um formato válido de UUID", this.status.description)
        }
    }

    @Test
    @Transactional
    fun ` não deve excluir chave pix inexistente e deve retornar status NOT_FOUND`() {
        val exclusaoChavePixRequest = ExcluirChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setIdPix("5260263c-a3c1-4727-ae32-3bdb2538841b")
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val exclusaoResponse = grpcExcluirClient.excluir(exclusaoChavePixRequest)
        }
        with(error) {
            assertEquals(Status.NOT_FOUND.code, this.status.code)
            assertEquals("Chave pix não encontrada pelo id: ${exclusaoChavePixRequest.idPix} ", this.status.description)
        }
    }
    fun `não deve excluir chave pix com clientId diferente do titular e deve retornar status PERMISSION_DENIED`() {

        val instituicao = Instituicao("ITAÚ UNIBANCO S.A.", "60701190")
        val titular = Titular(
            "Rafael M C Ponte",
            "02467781054",
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890")
        )

        val conta = Conta(
            br.com.zup.academy.conta.TipoConta.CONTA_CORRENTE,
            instituicao,
            "0001",
            "291900",
            titular
        )

        val chavePix = ChavePix(
            conta,
            br.com.zup.academy.chavepix.TipoChave.CPF,
            "02467781054"
        )
        val cadastroResponse = chavePixRepository.saveAndFlush(chavePix)


        val exclusaoChavePixRequest = ExcluirChavePixRequest.newBuilder()
            .setIdCliente(UUID.randomUUID().toString())
            .setIdPix(cadastroResponse.id.toString())
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val exclusaoResponse = grpcExcluirClient.excluir(exclusaoChavePixRequest)
        }
        with(error) {
            assertEquals(Status.PERMISSION_DENIED.code, this.status.code)
            assertEquals("Permissão negada para o recurso.", this.status.description)
        }

    }

    @Factory
    class ClientsFactory {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ExcluirChavePixServiceGrpc.ExcluirChavePixServiceBlockingStub {
            return ExcluirChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}