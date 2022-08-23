package rightshot.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import rightshot.dto.EstoqueDTO;
import rightshot.entity.Estoque;
import rightshot.repository.EstoqueRepository;
import rightshot.vo.PageVO;

@Slf4j
@Service
public class EstoqueService {

    private static final String ESTOQUE_SINTETICO = "EstoqueSintetico";

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private UtilService utilService;

    public List<EstoqueDTO> getAllEstoqueSintetico() {
        try {
            return entityManager.createNamedQuery(ESTOQUE_SINTETICO, EstoqueDTO.class)
                    .getResultList();
        } catch (Exception e) {
            log.error(e.toString());
            return Collections.emptyList();
        }
    }

    public List<Estoque> getAllEstoque() {
        try {
            return estoqueRepository.findAll();
        } catch (Exception e) {
            log.error(e.toString());
            return Collections.emptyList();
        }
    }

    public PageVO<EstoqueDTO> getAllEstoqueSintetivoPageable(PageVO<EstoqueDTO> pageVO) {
        try {
            final TypedQuery<EstoqueDTO> query = entityManager.createNamedQuery(ESTOQUE_SINTETICO, EstoqueDTO.class);
            String where = "";

            String sqlQuery = query.unwrap(Query.class).getQueryString();
            sqlQuery = sqlQuery.concat(" ORDER BY " + pageVO.getSort() + " " + pageVO.getSortDirection());

            if (pageVO.getFilterForm() != null) {
                where = this.utilService.gerarWhereParaFiltro(pageVO.getFilterForm());
            }
            sqlQuery = sqlQuery.replace("HashWhereFilter", where);

            final javax.persistence.Query newQuery = entityManager.createNativeQuery(sqlQuery,
                    "EstoqueSinteticoResultMapping");

            if (pageVO.isChangedQuery())
                pageVO.setTotalElements(this.utilService.CountQuery(newQuery, new HashMap<>()));

            newQuery.setFirstResult(pageVO.getPageIndex() * pageVO.getPageSize());
            newQuery.setMaxResults(pageVO.getPageSize());

            List<EstoqueDTO> retorno = Collections.checkedList(newQuery.getResultList(), EstoqueDTO.class);
            pageVO.setContent(retorno);

            return pageVO;
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        }

    }

    public boolean validaEstoquePorProduto(Long idProduto, Long qtd) {
        List<EstoqueDTO> estoque = entityManager.createNamedQuery(ESTOQUE_SINTETICO, EstoqueDTO.class)
                .getResultList();

        return estoque.stream().anyMatch(item -> item.getId_produto().equals(idProduto) && item.getQtd_est_real() >= qtd);
    }

}