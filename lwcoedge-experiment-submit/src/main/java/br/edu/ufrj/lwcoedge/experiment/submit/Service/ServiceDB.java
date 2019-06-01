package br.edu.ufrj.lwcoedge.experiment.submit.Service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufrj.lwcoedge.experiment.submit.db.dao.RequestDAO;
import br.edu.ufrj.lwcoedge.experiment.submit.db.entities.Requests;

@Service
public class ServiceDB {

	private Logger logger = LogManager.getLogger(getClass());
			//Logger.getLogger(this.getClass().getName());

	@Autowired
	RequestDAO requestDAO;

	@Transactional(propagation=Propagation.REQUIRED)
	public List<Requests> getRequests(String experimentname) throws Exception {
		logger.info("Loading database for experiment [{}]", experimentname);
		return requestDAO.getRequests(experimentname);
	}

}
