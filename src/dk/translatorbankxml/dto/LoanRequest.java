/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.translatorbankxml.dto;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder={"ssn", "creditScore", "loanAmount", "loanDuration"})
public class LoanRequest 
{
     long ssn;
     int creditScore;
     double loanAmount;
     String loanDuration;

    public long getSsn() {
        return ssn;
    }
    @XmlElement
    public void setSsn(long ssn) {
        this.ssn = ssn;
    }

    public int getCreditScore() {
        return creditScore;
    }
    @XmlElement
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public double getLoanAmount() {
        return loanAmount;
    }
    @XmlElement
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanDuration() {
        return loanDuration;
    }
    @XmlElement
    public void setLoanDuration(String loanDuration) {
        this.loanDuration = loanDuration;
    }
     
}
