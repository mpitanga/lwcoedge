package br.edu.ufrj.lwcoedge.context.broker.elements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EntityObject implements Serializable {

	private static final long serialVersionUID = 9036305201532591535L;

	private List<ContextElements> contextElements = new ArrayList<ContextElements>();
    private String updateAction;

    public EntityObject() {}
    
	/**
	 * @param contextElements
	 * @param updateAction
	 */
	public EntityObject(List<ContextElements> contextElements, String updateAction) {
		super();
		this.contextElements = contextElements;
		this.updateAction = updateAction;
	}


	/**
	 * @return the contextElements
	 */
	public List<ContextElements> getContextElements() {
		return contextElements;
	}


	/**
	 * @param contextElements the contextElements to set
	 */
	public void setContextElements(List<ContextElements> contextElements) {
		this.contextElements = contextElements;
	}


	/**
	 * @return the updateAction
	 */
	public String getUpdateAction() {
		return updateAction;
	}


	/**
	 * @param updateAction the updateAction to set
	 */
	public void setUpdateAction(String updateAction) {
		this.updateAction = updateAction;
	}
	
    @Override
    public String toString() {
        return "EntityObject [contextElements = "+contextElements+", updateAction = "+updateAction+"]";
    }

}
