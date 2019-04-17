package br.edu.ufrj.lwcoedge.context.broker.elements;

public enum EntityAction {
    APPEND ("APPEND");

    private final String name;
    private EntityAction(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        // (otherName == null) check is not needed because name.equals(null) returns false 
        return name.equals(otherName);
    }

    public String toString() {
       return this.name;
    }
}
