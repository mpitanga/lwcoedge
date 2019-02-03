package br.edu.ufrj.lwcoedge.experiment.submit.db.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.edu.ufrj.lwcoedge.experiment.submit.db.entities.Requests;

@Repository
public class RequestDAO {

	@Autowired
	private EntityManager em;

	public RequestDAO() {}

	@SuppressWarnings("unchecked")
	public List<Requests> getRequests(String experimentname) throws Exception {
		String sql = "from Requests r where (r.experimentname = :experimentname) order by r.id";
		List<Requests> requests = 
				em.createQuery(sql)
				.setParameter("experimentname", experimentname)
				.getResultList();
		return requests;
	}
	
	public void delete(String experimentname, String experimentcode) throws Exception {
		String sql = "delete from Requests r where (r.experimentname = :experimentname and r.experimentcode = :experimentcode)";
		em.createQuery(sql)
			.setParameter("experimentname", experimentname)
			.setParameter("experimentcode", experimentcode)
			.executeUpdate();
	}
}
