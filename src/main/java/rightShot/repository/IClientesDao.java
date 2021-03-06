package rightShot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rightShot.entity.Cliente;

@Repository
public interface IClientesDao extends JpaRepository<Cliente, Long> {

}
