package br.com.zup.academy.chavepix.cadastro

import br.com.zup.academy.*
import br.com.zup.academy.chavepix.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Singleton
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

@MicronautTest(transactional = false)
internal class CadastroChavePixEndpointTest(
    val chavePixRepository: ChavePixRepository,
    val grpcClient: CadastrarChavePixServiceGrpc.CadastrarChavePixServiceBlockingStub

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
    fun `deve cadastrar chave pix com sucesso com os dados atendendo os requisitos`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        Assertions.assertNotNull(cadastroResponse.idPix)
    }

    @Test
    fun `nao deve cadastrar chave pix ja cadastrada e deve retornar status ALREADY_EXISTS`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        grpcClient.cadastrar(chavePixRequest)
        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        }
        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, this.status.code)
            assertEquals("Chave Pix indisponível para cadastro: ${chavePixRequest.valorChave}", this.status.description)

        }
    }

    @Test
    fun `nao deve cadastrar chave pix com clientId inexistente no ERP Itau e deve retornar status NOT_FOUND`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157891")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        }
        with(error) {
            assertEquals(Status.NOT_FOUND.code, this.status.code)
            assertEquals("cliente não encontrado pelo id: ${chavePixRequest.idCliente}", this.status.description)

        }
    }
    @Test
    fun `nao deve cadastrar chave pix com clientId em formato incorreto e deve retornar status INVALID_ARGUMENT`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("sas6s1a1")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("02467781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        }
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            //assertEquals("cliente não encontrado pelo id: ${chavePixRequest.idCliente}", this.status.description)

        }
    }
    @Test
    fun `nao deve cadastrar chave pix tipo CPF invalida  e deve retornar status INVALID_ARGUMENT`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CPF)
            .setValorChave("0246sd7781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        }
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            println(this.status.description)
            assertEquals(": chave PIX inválida", this.status.description)

        }
    }

    @Test
    fun `nao deve cadastrar chave pix tipo EMAIL invalida  e deve retornar status INVALID_ARGUMENT`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.EMAIL)
            .setValorChave("0246sd7781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        }
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            println(this.status.description)
            assertEquals(": chave PIX inválida", this.status.description)

        }
    }

    @Test
    fun `nao deve cadastrar chave pix tipo CELULAR invalida  e deve retornar status INVALID_ARGUMENT`() {

        val chavePixRequest = CadastroChavePixRequest.newBuilder()
            .setIdCliente("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoChave(TipoChave.CELULAR)
            .setValorChave("0246sd7781054")
            .setTipoConta(TipoConta.CONTA_CORRENTE)
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            val cadastroResponse = grpcClient.cadastrar(chavePixRequest)
        }
        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            println(this.status.description)
            assertEquals(": chave PIX inválida", this.status.description)

        }
    }

    @Factory
    class ClientsFactory {
        @Singleton
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): CadastrarChavePixServiceGrpc.CadastrarChavePixServiceBlockingStub {
            return CadastrarChavePixServiceGrpc.newBlockingStub(channel)
        }
    }
}