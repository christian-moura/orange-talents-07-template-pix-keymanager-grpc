package br.com.zup.academy.conta

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface InstituicaoRepository:JpaRepository<Instituicao,String> {
}