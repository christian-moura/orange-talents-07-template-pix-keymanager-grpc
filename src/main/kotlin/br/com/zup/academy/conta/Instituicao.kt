package br.com.zup.academy.conta

import javax.persistence.*

@Entity
class Instituicao(
    @Column(nullable = false) val nome: String,
    @Id val ispb: String,
) {
    @OneToMany(mappedBy = "instituicao", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    var contas: List<Conta>? = null

    override fun toString(): String {
        return "Instituicao(nome='$nome', ispb='$ispb', contas=$contas)"
    }

}
