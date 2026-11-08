package co.inter.piggies.controller;

import co.inter.piggies.domain.client.ClientRequestDTO;
import co.inter.piggies.domain.client.ClientResponseDTO;
import co.inter.piggies.service.ClientService;
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

@Controller("/api/client")
@ExecuteOn(TaskExecutors.BLOCKING)
public class ClientController {

    /*
     *-----------------------------------------------------------------------------*
     * @Inject: o Micronaut cria o service e o injeta neste campo automaticamente. *
     * O campo não pode ser private nem final (o Micronaut evita usar reflection). *
     * Alternativa: receber o service pelo construtor (permite usar final).        *
     *-----------------------------------------------------------------------------*
     */

    @Inject
    ClientService clientService;

    /*
     *----------------------------------------------------------------------*
     * Metodo: GET                                                          *
     * Path: /api/client                                            *
     *----------------------------------------------------------------------*
     * Descrição: Função para buscar todos os Clients presentes no banco. *
     *----------------------------------------------------------------------*
     * Params:  None                                                        *
     * Returns: List<ClientResponseDTO>                                   *
     *----------------------------------------------------------------------*
     */

    @Get
    public HttpResponse<List<ClientResponseDTO>> getAllClient() {
        return HttpResponse.ok(clientService.getAllClient());
    }

    /*
     *-----------------------------------------------------------------------------------*
     * Metodo: GET                                                                       *
     * Path: /api/client/{id}                                                    *
     *-----------------------------------------------------------------------------------*
     * Descrição: Função para buscar no banco o registro que tenha o id passado no Path. *
     *-----------------------------------------------------------------------------------*
     * Params:  UUID id                                                              *
     * Returns: ClientResponseDTO                                                      *
     *-----------------------------------------------------------------------------------*
     */

    @Get("/{id}")
    public HttpResponse<ClientResponseDTO> getClientById(@PathVariable UUID id) {
        return HttpResponse.ok(clientService.getClientById(id));
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: POST                                                       *
     * Path: /api/client                                          *
     *--------------------------------------------------------------------*
     * Descrição: Criar um registro novo referente a Entidade Client.   *
     *--------------------------------------------------------------------*
     * Params:  ClientRequestDTO body                                   *
     * Returns: ClientResponseDTO                                       *
     *--------------------------------------------------------------------*
     */

    @Post
    public HttpResponse<ClientResponseDTO> createClient(@Valid @Body ClientRequestDTO body) {
        return HttpResponse.created(clientService.createClient(body));
    }

    /*
     *-----------------------------------------------------------------------------------*
     * Metodo: PUT                                                                       *
     * Path: /api/client/{id}                                                    *
     *-----------------------------------------------------------------------------------*
     * Descrição: Função para buscar no banco o registro que tenha o id passado no Path, *
     *            e editá-lo conforme o body passado.                                    *
     *-----------------------------------------------------------------------------------*
     * Params:  UUID id, ClientRequestDTO body                                     *
     * Returns: ClientResponseDTO                                                      *
     *-----------------------------------------------------------------------------------*
     */
    @Put("/{id}")
    public HttpResponse<ClientResponseDTO> updateClient(@PathVariable UUID id, @Valid @Body ClientRequestDTO body) {
        return HttpResponse.ok(clientService.updateClient(id, body));
    }

    /*
     *-----------------------------------------------------------------------------------*
     * Metodo: DELETE                                                                    *
     * Path: /api/client/{id}                                                    *
     *-----------------------------------------------------------------------------------*
     * Descrição: Função para buscar no banco o registro que tenha o id passado no Path, *
     *            e excluir o mesmo                                                      *
     *-----------------------------------------------------------------------------------*
     * Params:  UUID id                                                              *
     * Returns: apenas o status                                                          *
     *-----------------------------------------------------------------------------------*
     */
    @Delete("/{id}")
    public HttpResponse<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return HttpResponse.noContent();
    }
}
