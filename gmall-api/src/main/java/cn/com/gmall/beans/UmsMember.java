package cn.com.gmall.beans;

import javax.persistence.Column;
import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class UmsMember implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private java.lang.Long id;
    @Column
    private java.lang.Long memberLevelId;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String nickname;
    @Column
    private String phone;
    @Column
    private java.lang.Integer status;
    @Column
    private java.sql.Timestamp createTime;
    @Column
    private String icon;
    @Column
    private java.lang.Integer gender;
    @Column
    private java.sql.Date birthday;
    @Column
    private String city;
    @Column
    private String job;
    @Column
    private String personalizedSignature;
    @Column
    private java.lang.Integer sourceType;
    @Column
    private java.lang.Integer integration;
    @Column
    private java.lang.Integer growth;
    @Column
    private java.lang.Integer luckeyCount;
    @Column
    private java.lang.Integer historyIntegration;
    @Column
    private String accessToken;
    @Column
    private String socialCode;
    @Column
    private java.lang.Long sourceUid;
    @Column
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public java.lang.Long getId() {
        return id;
    }

    public void setId(java.lang.Long id) {
        this.id = id;
    }


    public java.lang.Long getMemberLevelId() {
        return memberLevelId;
    }

    public void setMemberLevelId(java.lang.Long memberLevelId) {
        this.memberLevelId = memberLevelId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public java.lang.Integer getStatus() {
        return status;
    }

    public void setStatus(java.lang.Integer status) {
        this.status = status;
    }


    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public java.lang.Integer getGender() {
        return gender;
    }

    public void setGender(java.lang.Integer gender) {
        this.gender = gender;
    }


    public java.sql.Date getBirthday() {
        return birthday;
    }

    public void setBirthday(java.sql.Date birthday) {
        this.birthday = birthday;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


    public String getPersonalizedSignature() {
        return personalizedSignature;
    }

    public void setPersonalizedSignature(String personalizedSignature) {
        this.personalizedSignature = personalizedSignature;
    }


    public java.lang.Integer getSourceType() {
        return sourceType;
    }

    public void setSourceType(java.lang.Integer sourceType) {
        this.sourceType = sourceType;
    }


    public java.lang.Integer getIntegration() {
        return integration;
    }

    public void setIntegration(java.lang.Integer integration) {
        this.integration = integration;
    }


    public java.lang.Integer getGrowth() {
        return growth;
    }

    public void setGrowth(java.lang.Integer growth) {
        this.growth = growth;
    }


    public java.lang.Integer getLuckeyCount() {
        return luckeyCount;
    }

    public void setLuckeyCount(java.lang.Integer luckeyCount) {
        this.luckeyCount = luckeyCount;
    }


    public java.lang.Integer getHistoryIntegration() {
        return historyIntegration;
    }

    public void setHistoryIntegration(java.lang.Integer historyIntegration) {
        this.historyIntegration = historyIntegration;
    }


    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }


    public String getSocialCode() {
        return socialCode;
    }

    public void setSocialCode(String socialCode) {
        this.socialCode = socialCode;
    }


    public java.lang.Long getSourceUid() {
        return sourceUid;
    }

    public void setSourceUid(java.lang.Long sourceUid) {
        this.sourceUid = sourceUid;
    }

}
