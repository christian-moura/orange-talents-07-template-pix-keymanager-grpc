package br.com.zup.academy.chavepix.excluir

import br.com.zup.academy.ExcluirChavePixRequest
import br.com.zup.academy.ExcluirChavePixResponse
import br.com.zup.academy.ExcluirChavePixServiceGrpc
import br.com.zup.academy.shared.validation.exceptions.ChavePixInexistenteException
import br.com.zup.academy.shared.validation.exceptions.PermissaoNegadaException
import io.grpc.Status
import io.grpc.stub.StreamObserver
import jakarta.inject.Inject
import jakarta.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class ExcluirChavePixEndpoint(
    @Inject val excluirChavePixService: ExcluirChavePixService
) : ExcluirChavePixServiceGrpc.ExcluirChavePixServiceImplBase() {

    override fun excluir(request: ExcluirChavePixRequest, responseObserver: StreamObserver<ExcluirChavePixResponse>) {

        try {
            excluirChavePixService.excluirChavePix(request.idCliente, request.idPix)
            val response = ExcluirChavePixResponse.newBuilder()
                .setIdPix(request.idPix)
                .setIdCliente(request.idCliente)
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (chavePixInexistenteException: ChavePixInexistenteException) {
            responseObserver.onError(
                Status.NOT_FOUND
                    .withDescription(chavePixInexistenteException.message)
                    .asRuntimeException()
            )
            return
        } catch (permissaoNegadaException: PermissaoNegadaException) {
            responseObserver.onError(
                Status.PERMISSION_DENIED
                    .withDescription(permissaoNegadaException.message)
                    .asRuntimeException()
            )
            return
        } catch (constraintViolationException: ConstraintViolationException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription(constraintViolationException.message)
                    .asRuntimeException()
            )
            return
        }
    }
}