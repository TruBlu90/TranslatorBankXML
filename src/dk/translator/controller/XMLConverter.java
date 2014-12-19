/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.translator.controller;

import dk.translator.dto.LoanRequest;
import dk.translator.dto.LoanRequestDTO;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;



/**
 *
 * @author Jon
 */
public class XMLConverter 
{
    public static String convertToXML(LoanRequestDTO loanRequestdto) throws JAXBException
    {
        String loanDuration = convertLoanDuration(loanRequestdto.getLoanDuration());
        StringBuilder sb = new StringBuilder(loanRequestdto.getSsn());
        sb.deleteCharAt(6);
        long ssnXML = Long.parseLong(sb.toString());
        
        LoanRequest loanRequestXML = new LoanRequest();
        loanRequestXML.setSsn(ssnXML);
        loanRequestXML.setLoanAmount(loanRequestdto.getLoanAmount());
        loanRequestXML.setLoanDuration(loanDuration);
        loanRequestXML.setCreditScore(loanRequestdto.getCreditScore());
        
        JAXBContext jaxbcontext = JAXBContext.newInstance(LoanRequest.class);
        Marshaller marchaller = jaxbcontext.createMarshaller(); 
        
        marchaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        StringWriter sw = new StringWriter();
        
        marchaller.marshal(loanRequestXML, sw);
        
        return sw.toString();
    }
    
    public static String convertLoanDuration(int loanDuration)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
        Calendar calendar = new GregorianCalendar(1970, 0, 1, 1, 0,0);
        TimeZone tz = TimeZone.getTimeZone("CET");
        calendar.setTimeZone(tz);

        calendar.add(Calendar.YEAR, loanDuration);
        String date = sdf.format(calendar.getTime());
        
        return date;
    }
}
