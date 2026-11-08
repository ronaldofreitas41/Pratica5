package co.inter.piggies.repository;

import co.inter.piggies.domain.piggies.Pig;
import io.micronaut.data.jpa.repository.JpaRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;

import java.math.BigDecimal;
import java.util.UUID;


/*
 *--------------------------------------------------------------------------*
 * @Repository: marca a interface como um repository do Micronaut Data.     *
 * A implementação é gerada em tempo de compilação, então basta declarar    *
 * a interface. Os métodos do JpaRepository (save, findById, findAll,       *
 * update, delete...) já vêm prontos, e métodos novos são criados pelo nome *
 * (ex.: findByNome(String nome) gera a consulta sozinho).                  *
 *--------------------------------------------------------------------------*
 */
@Repository
public interface PigRepository extends JpaRepository<Pig, UUID> {

    @Query("UPDATE Pig p SET p.balance = p.balance - :amount " +
            "WHERE p.id = :id AND p.balance >= :amount")
    int debit(UUID id, BigDecimal amount);

    @Query("UPDATE Pig p SET p.balance = p.balance + :amount WHERE p.id = :id")
    int credit(UUID id, BigDecimal amount);
}
