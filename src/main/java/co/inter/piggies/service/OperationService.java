package co.inter.piggies.service;

import co.inter.piggies.domain.operation.Operation;
import co.inter.piggies.domain.piggies.Pig;
import co.inter.piggies.kafka.events.OperationEventDTO;
import co.inter.piggies.repository.OperationRepository;
import co.inter.piggies.repository.PigRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/*
 *-----------------------------------------------------------------------*
 * @Singleton registra a classe como um bean do Micronaut com uma única  *
 * instância compartilhada por toda a aplicação. É o que permite injetar *
 * este service no controller (via @Inject ou construtor). Por ser       *
 * compartilhado, não guarde estado de requisição em campos da classe.   *
 *-----------------------------------------------------------------------*
 */
@Singleton
public class OperationService {

    /*
     *-------------------------------------------------------------------------*
     * @Inject: o Micronaut cria o repository e o injeta neste campo.          *
     * O campo não pode ser private nem final (o Micronaut evita reflection).  *
     * Alternativa: receber o repository pelo construtor (permite usar final). *
     *-------------------------------------------------------------------------*
     */
    @Inject
    OperationRepository operationRepository;

    @Inject
    PigRepository pigRepository;

    @Inject
    Logger LOG = LoggerFactory.getLogger(OperationService.class);

    @Transactional
    public void processBalance(OperationEventDTO event){

        if (event.pigId() == null){
            LOG.warn("Pig Id vazio favor passar um ID valido");
            return;
        }

        Pig pig = pigRepository.findById(event.pigId()).
                orElse(null);

        Operation operation = operationRepository.findById(event.operationId())
                .orElse(null);

        if(operation != null){
            LOG.info("Operação já processada anteriormente ignorando a mesma de ser executada novamente");
            return;
        }

        if (event.type().getDescription().equals("WITHDRAW")){
           int debbited =  pigRepository.debit(event.pigId(),event.amount());
           if (debbited == 0) {
                LOG.warn("Saldo insuficiente para o saque");
                return;
            }
        } else if (event.type().getDescription().equals("DEPOSIT")) {
            pigRepository.credit(event.pigId(),event.amount());
        }

        Operation operation1 = new Operation(event.operationId(),"SUCCESS",event.amount(),event.type());

        //Salvando Operações Realizadas
        Operation savedOperation = operationRepository.save(operation1);


    }


}
