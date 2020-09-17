package com.androidlec.icontact.Dto;

public class AddressDto {

    private int aSeqno;
    private String aName;
    private String aMobile;
    private String aEmail;
    private String aCompany;
    private String aDepartment;
    private String aJob;
    private String aTel;
    private String aAddress;
    private String aImage1;
    private String kmail;


    // 메인 리스트
    public AddressDto(int aSeqno, String aName, String aCompany, String aDepartment, String aJob, String aImage1, String aTel) {
        this.aSeqno = aSeqno;
        this.aName = aName;
        this.aCompany = aCompany;
        this.aDepartment = aDepartment;
        this.aJob = aJob;
        this.aImage1 = aImage1;
        this.aTel = aTel;
    }


    public AddressDto(String aName, String aMobile, String aEmail, String aCompany, String aDepartment, String aJob, String aTel, String aAddress) {
        this.aName = aName;
        this.aMobile = aMobile;
        this.aEmail = aEmail;
        this.aCompany = aCompany;
        this.aDepartment = aDepartment;
        this.aJob = aJob;
        this.aTel = aTel;
        this.aAddress = aAddress;
    }

    // 명함 상세
    public AddressDto(int aSeqno, String aName, String aMobile, String aEmail, String aCompany, String aDepartment, String aJob, String aTel, String aAddress, String aImage1) {
        this.aSeqno = aSeqno;
        this.aName = aName;
        this.aMobile = aMobile;
        this.aEmail = aEmail;
        this.aCompany = aCompany;
        this.aDepartment = aDepartment;
        this.aJob = aJob;
        this.aTel = aTel;
        this.aAddress = aAddress;
        this.aImage1 = aImage1;
    }

    public String getKemail() {
        return kmail;
    }

    public void setKemail(String kemail) {
        this.kmail = kemail;
    }

    public int getaSeqno() {
        return aSeqno;
    }

    public void setaSeqno(int aSeqno) {
        this.aSeqno = aSeqno;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaMobile() {
        return aMobile;
    }

    public void setaMobile(String aMobile) {
        this.aMobile = aMobile;
    }

    public String getaEmail() {
        return aEmail;
    }

    public void setaEmail(String aEmail) {
        this.aEmail = aEmail;
    }

    public String getaCompany() {
        return aCompany;
    }

    public void setaCompany(String aCompany) {
        this.aCompany = aCompany;
    }

    public String getaDepartment() {
        return aDepartment;
    }

    public void setaDepartment(String aDepartment) {
        this.aDepartment = aDepartment;
    }

    public String getaJob() {
        return aJob;
    }

    public void setaJob(String aJob) {
        this.aJob = aJob;
    }

    public String getaTel() {
        return aTel;
    }

    public void setaTel(String aTel) {
        this.aTel = aTel;
    }

    public String getaAddress() {
        return aAddress;
    }

    public void setaAddress(String aAddress) {
        this.aAddress = aAddress;
    }

    public String getaImage1() {
        return aImage1;
    }

    public void setaImage1(String aImage1) {
        this.aImage1 = aImage1;
    }

}
