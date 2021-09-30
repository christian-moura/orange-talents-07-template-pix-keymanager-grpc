package br.com.zup.academy.conta

import javax.persistence.*

@Entity
class Titular(
    @Column(nullable = false) val nome: String,
    @Id val cpf: String,
) {
    @OneToMany(mappedBy = "titular", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var contas: List<Conta>? = null

    override fun toString(): String {
        return "Titular(nome='$nome', cpf='$cpf', contas=$contas)"
    }


}
