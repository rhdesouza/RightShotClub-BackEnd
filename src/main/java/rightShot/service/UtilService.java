package rightShot.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

@Service
public class UtilService {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Retorna a quantidade de registros da Query
	 * 
	 * @param query
	 * @param qryParams
	 * @return
	 */
	public Long CountQuery(javax.persistence.Query query, Map<String, Object> qryParams) {
		if (query.toString().isEmpty())
			return 0L;

		final javax.persistence.Query newQuery = entityManager.createNativeQuery(
				"select count(*) from ( " + query.unwrap(Query.class).getQueryString().toString() + " ) A");

		if (!qryParams.isEmpty()) {
			qryParams.entrySet().stream().forEach((param) -> {
				newQuery.setParameter(param.getKey(), param.getValue());
			});
		}
		Long total = Long.parseLong(newQuery.getSingleResult().toString());
		return total == null ? 0L : total;
	}

	/**
	 * Gera o sql Where para filtro, utilizado em paginação
	 * 
	 * @param filtro
	 * @return
	 */
	public String gerarWhereParaFiltro(Object filtro) {
		try {
			StringBuilder where = new StringBuilder();
			where.append(" WHERE ");
			LinkedHashMap<String, String> filtro2 = (LinkedHashMap<String, String>) filtro;
			filtro2.forEach((key, value) -> {
				if (value != null && value != "")
					where.append(key + " LIKE '%" + value + "%' AND ");
			});

			if (where.toString().equals(" WHERE "))
				return "";

			return where.toString().substring(0, where.toString().length() - 4);
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Formata o valor para monetário para exibição
	 * 
	 * @param valor
	 * @return
	 */
	public String formataValorMonetarioParaExibicao(BigDecimal valor) {
		if (valor == null)
			return "R$ 0,00";
		NumberFormat dinheiro = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
		return dinheiro.format(valor);
	}

	public String formataCpfCnpjParaExibicao(String cpfCnpj) {
		try {
			if (cpfCnpj.length() == 11) {
				return (cpfCnpj.substring(0, 3) + "." + cpfCnpj.substring(3, 6) + "." + cpfCnpj.substring(6, 9) + "-"
						+ cpfCnpj.substring(9, 11));

			} else {
				return (cpfCnpj.substring(0, 2) + "." + cpfCnpj.substring(2, 5) + "." + cpfCnpj.substring(5, 8) + "."
						+ cpfCnpj.substring(8, 12) + "-" + cpfCnpj.substring(12, 14));
			}
		} catch (Exception e) {
			return cpfCnpj;
		}
	}
	
	public String formataDataHoraParaExibicao(LocalDateTime dataHora) {
		String dados = dataHora.toString();
		StringBuilder novosDados = new StringBuilder();
		novosDados.append(dados.split("T")[0].split("-")[2]);
		novosDados.append("/");
		novosDados.append(dados.split("T")[0].split("-")[1]);
		novosDados.append("/");
		novosDados.append(dados.split("T")[0].split("-")[0]);
		novosDados.append(" - ");
		novosDados.append(dados.split("T")[1].split(":")[0]);
		novosDados.append(":");
		novosDados.append(dados.split("T")[1].split(":")[1]);
		
		return novosDados.toString();
	}

}
