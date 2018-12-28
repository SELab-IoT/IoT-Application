package kr.ac.hanyang.selab.iot_application.domain;

public class PEPGroup {
    private long id;
    private String name;

    public PEPGroup(long id, String name){
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
