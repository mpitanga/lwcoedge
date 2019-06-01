package br.edu.ufrj.lwcoedge.context.broker.test;

import br.edu.ufrj.lwcoedge.context.broker.OrionBroker;
import br.edu.ufrj.lwcoedge.context.broker.elements.ContextBrokerResponse;

public class OrionBrokerTest {

	public static void main(String[] args) {
/*		OrionBroker ob = new OrionBroker("192.168.36.130","1026","lw_cos", "/");
		Long id = ob.getNumber("VN_MONITOR");
		System.out.println(id);
		
		EntityObject entity = new EntityObject();
*/
		try {
/*			Attributes att = new Attributes("host", "localhost", "string");		
			ContextElements contextElements = new ContextElements();
			contextElements.setId("ID"+id);
			contextElements.setType("VN_MONITOR"); 
			contextElements.getAttributes().add(att);			
			entity.setUpdateAction(EntityAction.APPEND.toString());
			entity.getContextElements().add(contextElements);
			ContextBrokerResponse response = ob.updateContext(entity);
*/			
			//smartown /gardens
			OrionBroker ob = new OrionBroker("192.168.36.130","1026","smartown", "/gardens");
			ContextBrokerResponse response = ob.getEntityById("WeatherStation1", "Device");
			System.out.println(response);
			response = ob.getEntityByType("ALL");
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
