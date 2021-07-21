package cn.com.gmall.beans;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

public class PmsBaseCatalog2 implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private Long catalog1Id;

    @Transient
    private List<PmsBaseCatalog3> pmsBaseCatalog3List;

    public List<PmsBaseCatalog3> getPmsBaseCatalog3List() {
        return pmsBaseCatalog3List;
    }

    public void setPmsBaseCatalog3List(List<PmsBaseCatalog3> pmsBaseCatalog3List) {
        this.pmsBaseCatalog3List = pmsBaseCatalog3List;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(Long catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

}
