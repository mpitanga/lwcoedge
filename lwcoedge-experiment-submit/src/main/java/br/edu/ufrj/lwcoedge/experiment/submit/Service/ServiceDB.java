package br.edu.ufrj.lwcoedge.experiment.submit.Service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufrj.lwcoedge.core.util.Util;
import br.edu.ufrj.lwcoedge.experiment.submit.db.dao.RequestDAO;
import br.edu.ufrj.lwcoedge.experiment.submit.db.entities.Requests;

@Service
public class ServiceDB {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	RequestDAO requestDAO;

	@Transactional(propagation=Propagation.REQUIRED)
	public List<Requests> getRequests(String experimentname) throws Exception {
		logger.info( Util.msg("Loading database for experiment [", experimentname, "]"));
		return requestDAO.getRequests(experimentname);
	}

}
