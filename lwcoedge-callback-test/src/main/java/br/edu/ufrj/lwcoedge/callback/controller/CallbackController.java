package br.edu.ufrj.lwcoedge.callback.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/myapp/callback")
public class CallbackController {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@PostMapping("/result")
	public void callBackResult(@RequestBody String body, HttpServletRequest httpRequest) {
		logger.info(body);
//		logger.info("ContentLenght="+String.valueOf(httpRequest.getContentLengthLong()));
	}
}
