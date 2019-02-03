package br.edu.ufrj.lwcoedge.experiment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.edu.ufrj.lwcoedge.experiment.request.Experiment0;

@SpringBootApplication
public class LwcoedgeExperiment0Application {

	@Autowired
	Experiment0 service;
	
	public static void main(String[] args) {
		SpringApplication.run(LwcoedgeExperiment0Application.class, args);
	}

}
