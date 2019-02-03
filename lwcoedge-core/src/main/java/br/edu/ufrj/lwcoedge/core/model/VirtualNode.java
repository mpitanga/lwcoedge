package br.edu.ufrj.lwcoedge.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.edu.ufrj.lwcoedge.core.util.Util;

/**
 * @author mpalv
 *
 */
public class VirtualNode implements Serializable {

	private static final long serialVersionUID = -3831689358755291766L;

    private String id; // Virtual node ID => Hostname.DatatypeID, EN1.UFRJ.UbicompLab.temperature
    private Descriptor datatype; // data type id => where.who.what <=> UFRJ.UbicompLab.temperature

    // End device, list of data
    private ConcurrentHashMap <String, ArrayList<Data>> data = new ConcurrentHashMap <String, ArrayList<Data>>();

    private List<String> neighbors = new ArrayList<String>();
    private Integer port; // communication port

	/**
	 * Default constructor.
	 */
	public VirtualNode() {}

	/**
	 * @param id The Virtual node identification => EDGENODE.DatatypeID, e.g. EN1.UFRJ.UbicompLab.temperature
	 * @param datatype The data type descriptor
	 */
	public VirtualNode(String id, Descriptor datatype, Integer port) {
		super();
		this.id = id;
		this.datatype = datatype;
		this.port = port;
	}

	/**
	 * @param id The Virtual node identification => EDGENODE.DatatypeID, EN1.UFRJ.UbicompLab.temperature
	 * @param datatype The data type descriptor
	 * @param data The data stream
	 * @param neighbors The virtual node neighbors.
	 */
	public VirtualNode(String id, Descriptor datatype, Integer port, ConcurrentHashMap<String, ArrayList<Data>> data, List<String> neighbors) {
		this.id = id;
		this.datatype = datatype;
		this.data = data;
		this.neighbors = neighbors;
		this.port = port;
	}

	@JsonIgnore
	public String getHostName() {
		String[] host = getId().split("\\.");
		return host[0];
	}

	@JsonIgnore
	public ArrayList<Data> getData(String element) {
		return data.get(element);
	}

	/**
	 * @param element The id
	 * @param data the data to set
	 */
	public void setData(String element, ArrayList<Data> data) {
		this.data.put(element, data);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the data
	 */
	public ConcurrentHashMap<String, ArrayList<Data>> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(ConcurrentHashMap<String, ArrayList<Data>> data) {
		this.data = data;
	}

	/**
	 * @return the neighbors
	 */
	public List<String> getNeighbors() {
		return neighbors;
	}

	/**
	 * @param neighbors the neighbors to set
	 */
	public void setNeighbors(List<String> neighbors) {
		this.neighbors = neighbors;
	}

	/**
	 * @return the datatype
	 */
	public Descriptor getDatatype() {
		return datatype;
	}

	/**
	 * @param datatype the datatype to set
	 */
	public void setDatatype(Descriptor datatype) {
		this.datatype = datatype;
	}

	/**
	 * @return the port
	 */
//	@JsonIgnore
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Util.obj2json(this);
	}

/*
    public static void main(String[] args) {
        ConcurrentHashMap <String, ArrayList<Data>> VNdata = new ConcurrentHashMap <String, ArrayList<Data>>();
		ArrayList<Data> freshData = new ArrayList<Data>();
		Data d = new Data();
		d.setValue(30);
		String dh = LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		d.setAcquisitiondatetime(dh);
		freshData.add(d);
		
		VNdata.put("DeviceID1", freshData);
		String jsonVN = Util.obj2json(VNdata);		
		String jsonDV = Util.obj2json(d);

		System.out.println("Device");
		System.out.println(jsonDV);
		System.out.println(jsonDV.length());

		System.out.println("Virtual Node");
		System.out.println(jsonVN);
		System.out.println(jsonVN.length());
	}
*/
}
