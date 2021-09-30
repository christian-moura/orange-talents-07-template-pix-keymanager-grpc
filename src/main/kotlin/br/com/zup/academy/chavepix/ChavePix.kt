package br.com.zup.academy.chavepix

import br.com.zup.academy.conta.Conta
import java.util.*
import javax.persistence.*

@Entity
class ChavePix(
    @ManyToOne(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val conta: Conta,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false) val tipoChave: TipoChave,
    @Column(nullable = false) val valorChave: String,
) {
    @Id @GeneratedValue
    var id: UUID? = null

    override fun toString(): String {
        return "ChavePix(conta=$conta, tipoChave=$tipoChave, valorChave='$valorChave', id=$id)"
    }


}