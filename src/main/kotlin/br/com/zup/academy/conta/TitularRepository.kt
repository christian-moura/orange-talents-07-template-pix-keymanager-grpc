package br.com.zup.academy.conta

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface TitularRepository :JpaRepository<Titular,UUID>{
}