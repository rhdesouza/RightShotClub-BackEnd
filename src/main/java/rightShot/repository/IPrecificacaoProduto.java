package rightShot.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import rightShot.entity.PrecificacaoProduto;

public interface IPrecificacaoProduto extends CrudRepository<PrecificacaoProduto, Long> {

	@Query("select pp from PrecificacaoProduto pp where pp.produto.id = :idProduto")
	PrecificacaoProduto PrecificacaoProdutoPorIdProduto(@Param(value = "idProduto") Long idProduto);


}
