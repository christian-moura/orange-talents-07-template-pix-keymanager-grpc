package br.com.zup.academy.conta

import br.com.zup.academy.chavepix.ChavePix
import javax.persistence.*

@Entity
class Conta(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) val tipo: TipoConta,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    val instituicao: Instituicao,
    @Column(nullable = false) val agencia: String,
    @Column(nullable = false) val numero: String,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    val titular: Titular
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null


    @OneToMany(mappedBy = "conta", cascade = [CascadeType.MERGE], fetch = FetchType.LAZY)
    var chaves: List<ChavePix>? = null

    override fun toString(): String {
        return "Conta(tipo=$tipo, instituicao=$instituicao, agencia='$agencia', numero='$numero', titular=$titular, id=$id, chaves=$chaves)"
    }


}