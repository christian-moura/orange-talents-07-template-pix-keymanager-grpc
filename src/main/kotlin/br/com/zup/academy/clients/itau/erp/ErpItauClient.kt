package br.com.zup.academy.clients.itau.erp

import br.com.zup.academy.clients.itau.erp.response.DetalhesContaErpItauResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client

@Client("\${itau.contas.url}")
interface ErpItauClient {

    @Get("/clientes/{clienteId}/contas{?tipo}")
    fun buscarConta(@PathVariable clienteId: String, @QueryValue tipo: String): HttpResponse<DetalhesContaErpItauResponse>
}