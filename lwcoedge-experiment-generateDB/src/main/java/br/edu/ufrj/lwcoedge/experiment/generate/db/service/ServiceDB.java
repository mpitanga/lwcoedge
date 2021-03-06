package br.edu.ufrj.lwcoedge.experiment.generate.db.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ufrj.lwcoedge.core.model.Request;
import br.edu.ufrj.lwcoedge.experiment.generate.db.dao.RequestDAO;

@Service
public class ServiceDB {

	private Logger logger = LogManager.getLogger(getClass());

	@Autowired
	RequestDAO requestDAO;

	@Transactional(propagation=Propagation.REQUIRED)
	public void saveRequest(String experimentname, String experimentcode, int variation, String edgenode,
		boolean activatecollaboration, boolean activatedatasharing, int idxhost, int experimentvar, String experimentid, 
		String URL, Request request) throws Exception {
		logger.info("Saving request: {}",request.toString());
		requestDAO.save(experimentname, experimentcode, variation, edgenode,
				activatecollaboration, activatedatasharing, idxhost, experimentvar, experimentid,
				URL, request);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void clearRequest(String experimentname, String experimentcode) throws Exception {
		logger.info("Cleaning database for experiment [{} , {}]", experimentname, experimentcode);
		requestDAO.delete(experimentname, experimentcode);
	}

}
