package co.inter.piggies.controller;

import co.inter.piggies.domain.operation.OperationRequestDTO;
import co.inter.piggies.domain.operation.OperationResponseDTO;
import co.inter.piggies.domain.piggies.PigRequestDTO;
import co.inter.piggies.domain.piggies.PigResponseDTO;
import co.inter.piggies.service.PigService;
import io.micronaut.http.HttpStatus;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;

import java.util.List;
import java.util.UUID;

/*
 *-------------------------------------------------------------------------*
 * @Controller marca a classe como um controller HTTP (bean do Micronaut). *
 * O valor passado (ex.: "/api/produtos") é o caminho base de todos os     *
 * endpoints da classe. O path de cada método (ex.: @Get("/{id}")) é       *
 * somado a ele, resultando em /api/produtos/{id}.                         *
 *-------------------------------------------------------------------------*
 * @ExecuteOn(TaskExecutors.BLOCKING) faz os métodos deste controller      *
 * rodarem no pool de threads para tarefas bloqueantes, e não no event     *
 * loop do Netty (padrão do Micronaut).                                    *
 *-------------------------------------------------------------------------*
 * Importância: o event loop usa poucas threads, que atendem todas as      *
 * requisições. Se uma delas ficar travada esperando banco (JDBC/JPA),     *
 * arquivo ou outra API, o servidor inteiro fica lento ou para.            *
 * Com o BLOCKING, essas chamadas rodam em outro pool e o event loop       *
 * continua livre para receber novas requisições.                          *
 *-------------------------------------------------------------------------*
 * Usar sempre que: O controller fizer chamadas bloqueantes (o caso comum  *
 * com repositories). Não é necessário em métodos reativos/assíncronos.    *
 *-------------------------------------------------------------------------*
 */

@Controller("/api/pig")
@ExecuteOn(TaskExecutors.BLOCKING)
public class PigController {

    /*
     *-----------------------------------------------------------------------------*
     * @Inject: o Micronaut cria o service e o injeta neste campo automaticamente. *
     * O campo não pode ser private nem final (o Micronaut evita usar reflection). *
     * Alternativa: receber o service pelo construtor (permite usar final).        *
     *-----------------------------------------------------------------------------*
     */

    @Inject
    PigService pigService;

    /*
     *----------------------------------------------------------------------*
     * Metodo: GET                                                          *
     * Path: /api/pig                                            *
     *----------------------------------------------------------------------*
     * Descrição: Função para buscar todos os Pigs presentes no banco. *
     *----------------------------------------------------------------------*
     * Params:  None                                                        *
     * Returns: List<PigResponseDTO>                                   *
     *----------------------------------------------------------------------*
     */

    @Get
    public HttpResponse<List<PigResponseDTO>> getAllPig() {
        return HttpResponse.ok(pigService.getAllPig());
    }

    /*
     *-----------------------------------------------------------------------------------*
     * Metodo: GET                                                                       *
     * Path: /api/pig/{id}                                                    *
     *-----------------------------------------------------------------------------------*
     * Descrição: Função para buscar no banco o registro que tenha o id passado no Path. *
     *-----------------------------------------------------------------------------------*
     * Params:  UUID id                                                              *
     * Returns: PigResponseDTO                                                      *
     *-----------------------------------------------------------------------------------*
     */

    @Get("/{id}")
    public HttpResponse<PigResponseDTO> getPigById(@PathVariable UUID id) {
        return HttpResponse.ok(pigService.getPigById(id));
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: POST                                                       *
     * Path: /api/pig                                                     *
     *--------------------------------------------------------------------*
     * Descrição: Criar um registro novo referente a Entidade Pig.        *
     *--------------------------------------------------------------------*
     * Params:  PigRequestDTO body                                        *
     * Returns: PigResponseDTO                                            *
     *--------------------------------------------------------------------*
     */

    @Post
    public HttpResponse<PigResponseDTO> createPig(@Valid @Body PigRequestDTO body) {
        return HttpResponse.created(pigService.createPig(body));
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: POST                                                       *
     * Path: /api/pig/{pigId}/operation                                   *
     *--------------------------------------------------------------------*
     * Descrição: Criar um registro novo referente a Entidade Pig.        *
     *--------------------------------------------------------------------*
     * Params:  PigRequestDTO body                                        *
     * Returns: PigResponseDTO                                            *
     *--------------------------------------------------------------------*
     */

    @Post("/{id}/operation")
    public HttpResponse<OperationResponseDTO> createOperation (@PathVariable UUID id, @Valid @Body OperationRequestDTO body) {
            System.out.println("Aqui");
            return HttpResponse.status(HttpStatus.ACCEPTED).body(pigService.createOperation(id,body));
    }


}
