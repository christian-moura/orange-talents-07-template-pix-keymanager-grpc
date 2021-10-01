package br.com.zup.academy.chavepix.cadastro


import br.com.zup.academy.CadastrarChavePixServiceGrpc
import br.com.zup.academy.CadastroChavePixRequest
import br.com.zup.academy.CadastroChavePixResponse

import br.com.zup.academy.chavepix.ChavePixRepository
import br.com.zup.academy.chavepix.TipoChave
import br.com.zup.academy.clients.itau.erp.ErpItauClient
import br.com.zup.academy.conta.ContaRepository
import br.com.zup.academy.conta.InstituicaoRepository
import br.com.zup.academy.conta.TipoConta
import br.com.zup.academy.conta.TitularRepository
import br.com.zup.edu.pix.ChavePixExistenteException
import br.com.zup.edu.pix.ClienteNaoEncontradoException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.validation.validator.Validator
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.transaction.Transactional
import javax.validation.ConstraintViolationException

@Singleton
open class CadastroChavePixEndpoint(
    @Inject val validator: Validator,
    @Inject val itauClient: ErpItauClient,
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val instituicaoRepository: InstituicaoRepository,
    @Inject val titularRepository: TitularRepository,
    @Inject val contaRepository: ContaRepository
) : CadastrarChavePixServiceGrpc.CadastrarChavePixServiceImplBase() {

    @Transactional
    override fun cadastrar(request: CadastroChavePixRequest, responseObserver: StreamObserver<CadastroChavePixResponse>) {

        try {
            val chavePixRequest = CadastroChavePixValidatorRequest(
                request.idCliente,
                TipoChave.valueOf(request.tipoChave.name),
                request.valorChave,
                TipoConta.valueOf(request.tipoConta.name)
            )
            val listaViolacoes = validator.validate(chavePixRequest)
            if (listaViolacoes.isNotEmpty()) throw ConstraintViolationException(listaViolacoes)

            val existeChavePix = chavePixRepository.findByValorChave(request.valorChave)
            if (existeChavePix.isPresent && chavePixRequest.tipo != TipoChave.ALEATORIA) throw ChavePixExistenteException(
                "Chave Pix indisponível para cadastro: ${request.valorChave}"
            )

            val contaItauResponse =
                itauClient.buscarConta(chavePixRequest.clienteId!!, chavePixRequest.tipoDeConta.toString())

            if (contaItauResponse.code() == 404) throw ClienteNaoEncontradoException("cliente não encontrado pelo id: ${request.idCliente}")

            var conta = contaItauResponse.body()!!.toConta(instituicaoRepository, titularRepository)

            val existeConta = contaRepository.findByTipoAndAgenciaAndNumero(conta.tipo, conta.agencia, conta.numero)
            conta =
                if (existeConta.isPresent) existeConta.get() else contaRepository.saveAndFlush(conta)

            var chavePix = chavePixRequest.toChavePix(conta)
            chavePix = chavePixRepository.saveAndFlush(chavePix)


            val response = CadastroChavePixResponse.newBuilder()
                .setIdPix(chavePix.id.toString())
                .setIdCliente(request.idCliente)
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()

        } catch (constraintViolationException: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(constraintViolationException.message)
                    .asRuntimeException()
            )
            return
        }catch (clienteNaoEncontradoException: ClienteNaoEncontradoException) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(clienteNaoEncontradoException.message)
                    .asRuntimeException()
            )
            return
        } catch (chavePixExistenteException: ChavePixExistenteException) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                    .withDescription(
                        chavePixExistenteException.message
                    )
                    .asRuntimeException()
            )
            return
        }
    }
}