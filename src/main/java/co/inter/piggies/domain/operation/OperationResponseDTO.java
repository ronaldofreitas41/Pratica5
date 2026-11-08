package co.inter.piggies.domain.operation;

import io.micronaut.serde.annotation.Serdeable;

import java.util.UUID;

/*
 *-------------------------------------------------------------------------*
 * @Serdeable: o Micronaut Serialization gera, em tempo de compilação, o   *
 * código que converte JSON em objeto (e vice-versa). Sem ele, o Micronaut *
 * não consegue ler o body da requisição nem devolver este DTO como JSON.  *
 *-------------------------------------------------------------------------*
 */
@Serdeable
public record OperationResponseDTO(
       UUID operationID,
       String status
) {
}
