package cn.com.gmall.beans;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

public class OmsCompanyAddress implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String addressName;
    @Column
    private Long sendStatus;
    @Column
    private Long receiveStatus;
    @Column
    private String name;
    @Column
    private String phone;
    @Column
    private String province;
    @Column
    private String city;
    @Column
    private String region;
    @Column
    private String detailAddress;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }


    public Long getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Long sendStatus) {
        this.sendStatus = sendStatus;
    }


    public Long getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(Long receiveStatus) {
        this.receiveStatus = receiveStatus;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }


    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

}
