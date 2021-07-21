package cn.com.gmall.beans;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class WmsWareOrderTaskDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long skuId;
    @Column
    private String skuName;
    @Column
    private Long skuNums;
    @Column
    private Long taskId;
    @Column
    private Long skuNum;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }


    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }


    public Long getSkuNums() {
        return skuNums;
    }

    public void setSkuNums(Long skuNums) {
        this.skuNums = skuNums;
    }


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }


    public Long getSkuNum() {
        return skuNum;
    }

    public void setSkuNum(Long skuNum) {
        this.skuNum = skuNum;
    }

}
