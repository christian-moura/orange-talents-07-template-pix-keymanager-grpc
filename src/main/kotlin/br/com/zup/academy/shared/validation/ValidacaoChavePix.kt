package br.com.zup.academy.shared.validation


import br.com.zup.academy.chavepix.cadastro.CadastroChavePixValidatorRequest

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import jakarta.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass
import br.com.zup.academy.chavepix.*

@MustBeDocumented
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidPixKeyValidator::class])

annotation class ValidPixKey(
    val message: String = "chave PIX inválida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = [],
)

@Singleton
class ValidPixKeyValidator : ConstraintValidator <ValidPixKey, CadastroChavePixValidatorRequest>  {

    override fun isValid(
        value: CadastroChavePixValidatorRequest,
        annotationMetadata: AnnotationValue<ValidPixKey>,
        context: ConstraintValidatorContext
    ): Boolean {

        when(value.tipo) {

            TipoChave.CELULAR -> {
                context.messageTemplate("chave aleatória não é número celular válido.")
                return "^\\+[1-9][0-9]\\d{1,14}\$".toRegex().matches(value.chave!!)
            }
            TipoChave.CPF -> {
                context.messageTemplate("chave aleatória não é um cpf válido.")
                return "^[0-9]{11}".toRegex().matches(value.chave!!)
            }
            TipoChave.EMAIL -> {
                context.messageTemplate("chave aleatória não é email válido.")
                return "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$".toRegex().matches(value.chave!!)
            }
            TipoChave.ALEATORIA -> {

                return true
            }
            else -> return false
        }

    }
}