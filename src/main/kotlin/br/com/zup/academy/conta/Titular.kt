package br.com.zup.academy.conta

import java.util.*
import javax.persistence.*

@Entity
class Titular(
    @Column(nullable = false) val nome: String,
    @Column(nullable = false) val cpf: String,
    @Id val id: UUID,
) {
    @OneToMany(mappedBy = "titular", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var contas: List<Conta>? = null

    override fun toString(): String {
        return "Titular(nome='$nome', cpf='$cpf', id=$id)"
    }


}
