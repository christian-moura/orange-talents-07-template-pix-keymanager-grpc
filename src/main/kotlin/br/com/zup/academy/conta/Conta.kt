package br.com.zup.academy.conta

import br.com.zup.academy.chavepix.ChavePix
import javax.persistence.*

@Entity
class Conta(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) val tipo: TipoConta,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val instituicao: Instituicao,
    @Column(nullable = false) val agencia: String,
    @Column(nullable = false) val numero: String,
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    val titular: Titular
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @OneToMany(mappedBy = "conta", fetch = FetchType.LAZY)
    var chaves: List<ChavePix>? = null

}