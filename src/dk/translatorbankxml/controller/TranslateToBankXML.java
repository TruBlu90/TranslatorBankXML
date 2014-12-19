/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.translatorbankxml.controller;

import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import dk.translatorbankxml.dto.LoanRequestDTO;
import dk.translatorbankxml.messaging.Receive;
import dk.translatorbankxml.messaging.Send;
import java.io.IOException;
import java.util.HashMap;
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
        
        LoanRequestDTO loanRequest;
        String loanRequestXML;
//        List<String> selectedBanks;
        
        while (true) 
        {
          QueueingConsumer.Delivery delivery = consumer.nextDelivery();
          String message = new String(delivery.getBody());
          
          AMQP.BasicProperties props = delivery.getProperties();
          AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder().correlationId(props.getCorrelationId()).replyTo(REPLY_QUEUE_NAME).build();
          
          String routingKey = delivery.getEnvelope().getRoutingKey();

          System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
          
          loanRequest = gson.fromJson(message, LoanRequestDTO.class);
          loanRequestXML = XMLConverter.convertToXML(loanRequest);
          /*
          StringBuilder sb = new StringBuilder(loanRequest.getSsn());
          sb.deleteCharAt(6);
          long ssnJSON = Long.parseLong(sb.toString());
          */
          
          
            System.out.println(loanRequestXML);
            Send.sendMessage(loanRequestXML, replyProps);
          
          channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

        }
        
    }
    

}
