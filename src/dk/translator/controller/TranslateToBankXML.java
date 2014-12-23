/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.translator.controller;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.thoughtworks.xstream.XStream;
import dk.translator.dto.ConvertedLoanRequestDTO;
import dk.translator.dto.LoanRequestDTO;
import dk.translator.messaging.Receive;
import dk.translator.messaging.Send;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Jon
 */
public class TranslateToBankXML 
{
     private static Gson gson;
    private static final String REPLY_QUEUE_NAME = "queue_normalizerBankXML";
    
    public static void receiveMessages() throws IOException,InterruptedException, JAXBException
    {
        gson = new Gson();
        
        HashMap<String,Object> objects = Receive.setUpReceiver();
        
        QueueingConsumer consumer = (QueueingConsumer) objects.get("consumer");
        Channel channel = (Channel) objects.get("channel");
        
        LoanRequestDTO loanRequestDTO;
        ConvertedLoanRequestDTO convertedDTO;
        
        XStream xstream = new XStream();
        
        while (true) 
        {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());

            AMQP.BasicProperties props = delivery.getProperties();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId()).replyTo(REPLY_QUEUE_NAME).build();

            String routingKey = delivery.getEnvelope().getRoutingKey();

            System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");

            loanRequestDTO = gson.fromJson(message, LoanRequestDTO.class);
            
            StringBuilder sb = new StringBuilder(loanRequestDTO.getSsn());
            sb.deleteCharAt(6);
            long convertedSsn = Long.parseLong(sb.toString());
            
            convertedDTO = new ConvertedLoanRequestDTO(convertedSsn, loanRequestDTO.getCreditScore(), loanRequestDTO.getLoanAmount(), convertLoanDuration(loanRequestDTO.getLoanDuration()));
            
            sendMessage(convertedDTO, props);
            
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        }
        
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
    
    private static void sendMessage(ConvertedLoanRequestDTO dto, AMQP.BasicProperties props) throws IOException
    {
        XStream xstream = new XStream();
        
        xstream.alias("LoanRequest", ConvertedLoanRequestDTO.class);
        
        
        
        String message = xstream.toXML(dto);
        
        
        Send.sendMessage(message, props);
    }
    

}
