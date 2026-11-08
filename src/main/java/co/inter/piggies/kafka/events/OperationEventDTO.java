package co.inter.piggies.kafka.events;

import co.inter.piggies.domain.operation.OperationType;
import io.micronaut.serde.annotation.Serdeable;

import java.math.BigDecimal;
import java.util.UUID;

/*
 *-------------------------------------------------------------------------*
 * @Serdeable: o Micronaut Serialization gera, em tempo de compilação, o   *
 * código que converte JSON em objeto (e vice-versa). Sem ele, o Micronaut *
 * não consegue ler o body da requisição nem devolver este DTO como JSON.  *
 *-------------------------------------------------------------------------*
 */

@Serdeable
public record OperationEventDTO(
        UUID operationId,
        UUID pigId,
        OperationType type,
        BigDecimal amount
) {}
