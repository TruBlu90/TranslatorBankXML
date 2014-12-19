/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package translatorbankxml;

import dk.translatorbankxml.controller.TranslateToBankXML;
import java.io.IOException;
import javax.xml.bind.JAXBException;

;

public class TranslatorBankXML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException, JAXBException 
    {
        TranslateToBankXML.receiveMessages();
    }
    
}
