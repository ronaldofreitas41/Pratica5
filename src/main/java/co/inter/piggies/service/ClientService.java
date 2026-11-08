package co.inter.piggies.service;

import co.inter.piggies.domain.client.Client;
import co.inter.piggies.domain.client.ClientRequestDTO;
import co.inter.piggies.domain.client.ClientResponseDTO;
import co.inter.piggies.repository.ClientRepository;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
public class ClientService {

    /*
     *-------------------------------------------------------------------------*
     * @Inject: o Micronaut cria o repository e o injeta neste campo.          *
     * O campo não pode ser private nem final (o Micronaut evita reflection).  *
     * Alternativa: receber o repository pelo construtor (permite usar final). *
     *-------------------------------------------------------------------------*
     */
    @Inject
    ClientRepository clientRepository;

    /*
     *--------------------------------------------------------------------*
     * Metodo: getClientById                                            *
     *--------------------------------------------------------------------*
     * Descrição: Função para buscar uma Client pelo seu id se ele      *
     *            existir.                                                *
     *--------------------------------------------------------------------*
     * Params:  UUID id                                               *
     * Returns: ClientResponseDTO                                       *
     *--------------------------------------------------------------------*
     */

    public ClientResponseDTO getClientById(UUID id) {

        // Busca a entidade no banco de dados via id e caso a mesma não seja encontrada
        // retornará um Status 404 com a mensagem de dados nao encontrado para aquele id
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND,
                        "Dado não encontrado para o id: " + id));

        //Retorna a conversão da Entity em um ResponseDTO
        return new ClientResponseDTO(
                client.getId(),
                client.getName()
        );
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: getAllClient                                             *
     *--------------------------------------------------------------------*
     * Descrição: Função para buscar uma lista com todas as Clients     *
     *            presentes no banco de dados.                            *
     *--------------------------------------------------------------------*
     * Params:  None                                                      *
     * Returns: List<ClientResponseDTO>                                 *
     *--------------------------------------------------------------------*
     */

    public List<ClientResponseDTO> getAllClient() {

        //Busca todas as Entities presentes no banco
        List<Client> clients = clientRepository.findAll();

        //Retorna a conversão da Lista de Entities em uma Lista de ResponseDTO
        return clients.stream().map(
                client -> new ClientResponseDTO(
                        client.getId(),
                        client.getName()
                )).toList();
    }


    /*
     *--------------------------------------------------------------------*
     * Metodo: createClient                                             *
     *--------------------------------------------------------------------*
     * Descrição: Função para criar uma Client com os dados passados    *
     *            via body da requisição                                  *
     *--------------------------------------------------------------------*
     * Params:  ClientRequestDTO body                                   *
     * Returns: ClientResponseDTO                                       *
     *--------------------------------------------------------------------*
     */

    public ClientResponseDTO createClient(ClientRequestDTO body) {

        //Converte o Body que é um RequestDTO em uma Entity
        Client client = new Client();

        //Setando Entity
        client.setName(body.name());

        // Salvando Entity no banco
        Client clientSaved = clientRepository.save(client);

        //Retorna a conversão da RequestDTO em um ResponseDTO
        return new ClientResponseDTO(
                clientSaved.getId(),
                clientSaved.getName()
        );
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: updateClient                                             *
     *--------------------------------------------------------------------*
     * Descrição: Função para buscar uma Client pelo seu id se ele      *
     *            existir. E se ela existir editar com os dados passados  *
     *            via body da requisição                                  *
     *--------------------------------------------------------------------*
     * Params:  UUID id, ClientRequestDTO body                      *
     * Returns: ClientResponseDTO                                       *
     *--------------------------------------------------------------------*
     */

    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO body) {

        // Busca a entidade no banco de dados via id e caso a mesma não seja encontrada
        // retornará um Status 404 com a mensagem de dados nao encontrado para aquele id
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND,
                        "Dado não encontrado para o id: " + id));

        client.setName(body.name());

        // Atualizando Entity referente ao id passado no banco
        clientRepository.update(client);

        //Retorna a conversão da RequestDTO em um ResponseDTO
        return new ClientResponseDTO(
                client.getId(),
                client.getName()
        );
    }

    /*
     *--------------------------------------------------------------------*
     * Metodo: deleteClient                                             *
     *--------------------------------------------------------------------*
     * Descrição: Função para buscar uma Client pelo seu id se ele      *
     *            existir. E se ela existir ela será excluída             *
     *--------------------------------------------------------------------*
     * Params:  UUID id                                               *
     * Returns: None                                                      *
     *--------------------------------------------------------------------*
     */

    public void deleteClient(UUID id) {

        // Busca a entidade no banco de dados via id e caso a mesma não seja encontrada
        // retornará um Status 404 com a mensagem de dados nao encontrado para aquele id
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new HttpStatusException(HttpStatus.NOT_FOUND,
                        "Dado não encontrado para o id: " + id));

        // Excluindo Entity referente ao id passado no banco se ela existir
        clientRepository.delete(client);
    }
}
