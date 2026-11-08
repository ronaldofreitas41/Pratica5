package co.inter.piggies.domain.operation;

import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;

/*
 *-------------------------------------------------------------------------*
 * @Serdeable: o Micronaut Serialization gera, em tempo de compilação, o   *
 * código que converte JSON em objeto (e vice-versa). Sem ele, o Micronaut *
 * não consegue ler o body da requisição nem devolver este DTO como JSON.  *
 *-------------------------------------------------------------------------*
 */

@Serdeable
public record OperationRequestDTO(
        OperationType type,
        BigDecimal amount
) {
}
