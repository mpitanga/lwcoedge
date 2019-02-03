package br.edu.ufrj.lwcoedge.experiment.generate.db.dao;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.experiment.generate.db.entities.Requests;

@Repository
public class RequestDAO {

	@Autowired
	private EntityManager em;

	public RequestDAO() {}
	
	public void save(String experimentname, String experimentcode, int variation, String edgenode,
			boolean activatecollaboration, boolean activatedatasharing, int idxhost, int experimentvar, String experimentid, 
			String URL, Request request) throws Exception {
		Requests reqDB = new Requests();
		reqDB.setExperimentname(experimentname);
		reqDB.setExperimentcode(experimentcode);
		reqDB.setVariation(variation);
		reqDB.setEdgenode(edgenode);
		reqDB.setActivatecollaboration(activatecollaboration);
		reqDB.setActivatedatasharing(activatedatasharing);
		reqDB.setIdxhost(idxhost);
		reqDB.setExperimentvar(experimentvar);
		reqDB.setExperimentid(experimentid);
		reqDB.setUrl(URL);
		reqDB.setRequest(request.toString());
		em.persist(reqDB);
	}

	public void delete(String experimentname, String experimentcode)  throws Exception {
		String sql = "delete from Requests r where (r.experimentname = :experimentname and r.experimentcode = :experimentcode)";
		em.createQuery(sql)
			.setParameter("experimentname", experimentname)
			.setParameter("experimentcode", experimentcode)
			.executeUpdate();
	}
}
