package co.inter.piggies.service;

import co.inter.piggies.domain.operation.Operation;
import co.inter.piggies.domain.operation.OperationRequestDTO;
import co.inter.piggies.domain.operation.OperationResponseDTO;
import co.inter.piggies.domain.piggies.Pig;
import co.inter.piggies.domain.piggies.PigRequestDTO;
import co.inter.piggies.domain.piggies.PigResponseDTO;
import co.inter.piggies.kafka.events.OperationEventDTO;
import co.inter.piggies.kafka.producer.OperationProducer;
import co.inter.piggies.repository.OperationRepository;
import co.inter.piggies.repository.PigRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/*
 *-----------------------------------------------------------------------*
 * @Singleton registra a classe como um bean do Micronaut com uma única  *
 * instância compartilhada por toda a aplicação. É o que permite injetar *
 * este service no controller (via @Inject ou construtor). Por ser       *
 * compartilhado, não guarde estado de requisição em campos da classe.   *
 *-----------------------------------------------------------------------*
 */
@Singleton
public class PigService {

    /*
     *-------------------------------------------------------------------------*
     * @Inject: o Micronaut cria o repository e o injeta neste campo.          *
     * O campo não pode ser private nem final (o Micronaut evita reflection).  *
     * Alternativa: receber o repository pelo construtor (permite usar final). *
     *-------------------------------------------------------------------------*
     */
    @Inject
    PigRepository pigRepository;

    @Inject
    OperationRepository operationRepository;

    @Inject
    OperationProducer operationProducer;

    /*
     *--------------------------------------------------------------------*
     * Metodo: getPigById                                            *
     *--------------------------------------------------------------------*
     * Descrição: Função para buscar uma Pig pelo seu id se ele      *
     *            existir.                                                *
     *--------------------------------------------------------------------*
     * Params:  UUID id                                               *
     * Returns: PigResponseDTO                                       *
     *--------------------------------------------------------------------*
     */

    public PigResponseDTO getPigById(UUID id) {

        // Busca a entidade no banco de dados via id e caso a mesma não seja encontrada
        // retornará um Status 404 com a mensagem de dados nao encontrado para aquele id
        Pig pig = pigRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND,
                        "Dado não encontrado para o id: " + id));

        //Retorna a conversão da Entity em um ResponseDTO
        return new PigResponseDTO(
                pig.getId(),
                pig.getOwnerId(),
                pig.getName(),
                pig.getGoal(),
                pig.getBalance()
        );
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: getAllPig                                             *
     *--------------------------------------------------------------------*
     * Descrição: Função para buscar uma lista com todas as Pigs     *
     *            presentes no banco de dados.                            *
     *--------------------------------------------------------------------*
     * Params:  None                                                      *
     * Returns: List<PigResponseDTO>                                 *
     *--------------------------------------------------------------------*
     */

    public List<PigResponseDTO> getAllPig() {

        //Busca todas as Entities presentes no banco
        List<Pig> pigs = pigRepository.findAll();

        //Retorna a conversão da Lista de Entities em uma Lista de ResponseDTO
        return pigs.stream().map(
                pig -> new PigResponseDTO(
                        pig.getId(),
                        pig.getOwnerId(),
                        pig.getName(),
                        pig.getGoal(),
                        pig.getBalance()
                )).toList();
    }


    /*
     *--------------------------------------------------------------------*
     * Metodo: createPig                                             *
     *--------------------------------------------------------------------*
     * Descrição: Função para criar uma Pig com os dados passados    *
     *            via body da requisição                                  *
     *--------------------------------------------------------------------*
     * Params:  PigRequestDTO body                                   *
     * Returns: PigResponseDTO                                       *
     *--------------------------------------------------------------------*
     */

    public PigResponseDTO createPig(PigRequestDTO body) {

        if (body.goal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Informe uma meta maior que 0");
        }

        if (body.goal().scale() > 2) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, "Informe um valor de meta com até 2 casas decimais");
        }

        //Converte o Body que é um RequestDTO em uma Entity
        Pig pig = new Pig();

        pig.setBalance(BigDecimal.ZERO);
        pig.setGoal(body.goal());
        pig.setName(body.name());
        pig.setOwnerId(body.ownerId());


        // Salvando Entity no banco
        Pig pig2 = pigRepository.save(pig);

        //Retorna a conversão da RequestDTO em um ResponseDTO
        return new PigResponseDTO(
                pig2.getId(),
                pig2.getOwnerId(),
                pig2.getName(),
                pig2.getGoal(),
                pig2.getBalance()
        );
    }

    public OperationResponseDTO createOperation(UUID pigId, OperationRequestDTO body) {

        Pig pig = pigRepository.findById(pigId).orElseThrow(() -> new HttpStatusException(
                HttpStatus.NOT_FOUND, "Porquinho não encontrado"
        ));

        if ((!body.type().getDescription().equals("WITHDRAW")) &&
                (!body.type().getDescription().equals("DEPOSIT"))) {
            throw new HttpStatusException(HttpStatus.BAD_REQUEST,"Tipo de operação inválida");
        }

        if (body.amount().compareTo(BigDecimal.ZERO) <= 0
                ||  body.amount().scale() > 2){
            throw new HttpStatusException(HttpStatus.BAD_REQUEST,"Valor invalido da operação");
        }


        Operation operation = new Operation();

        operation.setAmount(body.amount());
        operation.setType(body.type());
        operation.setStatus("ACCEPTED");


        OperationEventDTO event = new OperationEventDTO(
                operation.getId(),
                pig.getId(),
                operation.getType(),
                operation.getAmount()
        );

        operationProducer.send(pig.getOwnerId(),event);

        return new OperationResponseDTO(
                operation.getId(),
                operation.getStatus()
        );
    }
}
