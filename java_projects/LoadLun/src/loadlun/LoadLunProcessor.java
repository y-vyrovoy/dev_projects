/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadlun;


import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 *
 * @author vyrovoy
 */
public class LoadLunProcessor {
    
    public static LoadLunProcessor GetNewProcessor(){
        return new LoadLunProcessor();
    }
    
    private LoadLunProcessor(){

    }
    
    public void StartLoading(){
        
        String sURL = "https://www.lun.ua/%D0%BF%D1%80%D0%BE%D0%B4%D0%B0%D0%B6%D0%B0-%D0%BA%D0%B2%D0%B0%D1%80%D1%82%D0%B8%D1%80-%D0%BA%D0%B8%D0%B5%D0%B2?page=1";
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        
        try{
            
            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document docResult = builder.newDocument();
            
            // creating root element of the result document
            org.w3c.dom.Element eleRoot = docResult.createElement("lun_ads");
            
            
            docResult.appendChild(eleRoot);

            // iterating pages
            ParseOnePage(sURL, eleRoot);
            //eleRoot.appendChild();
            
            
            org.w3c.dom.Document  d = (org.w3c.dom.Document)eleRoot.getParentNode();

            System.out.println(d == docResult);
            
        
            
            
            // saving ready document
            SaveResultXML(docResult);
 
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    private void ParseOnePage(String sURL, org.w3c.dom.Element eleRoot){
    
        try{
            
            org.w3c.dom.Document w3cDoc = (org.w3c.dom.Document) eleRoot.getParentNode();
            
            Document jsoupDoc = Jsoup.connect(sURL).get();

            // looking for <div class="obj-left-flat-box"> section
            Element eleLeftColumn = jsoupDoc.getElementsByAttributeValue("class", "obj-left-flat-box").first();
            if(eleLeftColumn == null){
                return;
            }
                   
            
            // eleLeftColumn is the root node for ad's
            Elements eleSrcLeftColumnList = eleLeftColumn.children();
            
            

            // listing ads
            for(Element ele:eleSrcLeftColumnList){
                
                Element eleLeft = ele.getElementsByAttributeValue("class", "obj-left").first();
                Element eleRight = ele.getElementsByAttributeValue("class", "obj-right").first();
                
                if((eleLeft != null) || (eleRight != null)){

                    org.w3c.dom.Element eleNext = w3cDoc.createElement("ad_item");

                    // parsing left part of ad
                    if(eleLeft != null){
                    
                        // retrieving title
                        org.w3c.dom.Element eleTitle = w3cDoc.createElement("title");
                        eleTitle.setTextContent(ele.getElementsByAttributeValue("class", "obj-title").text());
                        eleNext.appendChild(eleTitle);
                        
                        System.out.println(ele.getElementsByAttributeValue("class", "obj-title").text());
                        
                        // retrieving "locality"
                        org.w3c.dom.Element eleLocality = w3cDoc.createElement("locality");
                        eleLocality.setTextContent(ele.getElementsByAttributeValue("class", "obj-locality").text());
                        eleNext.appendChild(eleLocality);

                        // retrieving details (params)
                        org.w3c.dom.Element eleParams = w3cDoc.createElement("params");
                        eleParams.setTextContent(ele.getElementsByAttributeValue("class", "obj-params").text());
                        eleNext.appendChild(eleParams);

                        
                        // retrieving details (wrap mt-10)
                        org.w3c.dom.Element eleMt10 = w3cDoc.createElement("wrap_mt-10");
                        eleMt10.setTextContent(ele.getElementsByAttributeValue("class", "wrap mt-10").text());
                        eleNext.appendChild(eleMt10);

                        // retrieving link (wrap mt-10) 
                        org.w3c.dom.Element eleMt10Link = w3cDoc.createElement("wrap_mt-10_link");
                        
                        if( (ele.getElementsByAttributeValue("class", "wrap mt-10").isEmpty() == false) && 
                            (ele.getElementsByAttributeValue("class", "wrap mt-10").first().getElementsByTag("a").isEmpty() == false) 
                            ){

                            eleMt10Link.setTextContent(ele.getElementsByAttributeValue("class", "wrap mt-10").first()
                                                                                    .getElementsByTag("a").first().attr("href"));

                        }        
                        eleNext.appendChild(eleMt10Link);
                        
                        // retrieving link (wrap) 
                        org.w3c.dom.Element eleWrapLink = w3cDoc.createElement("wrap_link");
                        
                        // does <div class="obj-source"> exist?
                        
                        if( (ele.getElementsByAttributeValue("class", "obj-source") != null) && 
                                (ele.getElementsByAttributeValue("class", "obj-source").isEmpty() == false) ){
                                
                            
                        
                            if( (ele.getElementsByAttributeValue("class", "obj-source").first()
                                    .getElementsByAttributeValue("class", "wrap").isEmpty() == false) && 
                            (ele.getElementsByAttributeValue("class", "wrap").first().getElementsByTag("a").isEmpty() == false) 
                            ){
                        
                                eleWrapLink.setTextContent(ele.getElementsByAttributeValue("class", "obj-source").first()
                                                                .getElementsByAttributeValue("class", "wrap").first()
                                                                .getElementsByTag("a").first().attr("href"));
                            }
                        
                        }                        
                        eleNext.appendChild(eleWrapLink);

                    
                    }                    
                    
                    
                    // parsing right part of ad
                    if(eleRight != null){
                    
                        // retrieving id
                        org.w3c.dom.Element eleID = w3cDoc.createElement("id");
                        eleID.setTextContent(ele.attr("data-jss"));
                        eleNext.appendChild(eleID);

                        // retrieving price
                        org.w3c.dom.Element elePrice = w3cDoc.createElement("price");
                        elePrice.setTextContent(ele.getElementsByAttributeValue("class", "obj-price").text());
                        eleNext.appendChild(elePrice);

                        // retrieving price-ded
                        org.w3c.dom.Element elePriceDed = w3cDoc.createElement("price-ded");
                        elePriceDed.setTextContent(ele.getElementsByAttributeValue("class", "obj-price-ded").text());
                        eleNext.appendChild(elePriceDed);

                        // retrieving seller under
                        org.w3c.dom.Element eleSellerUnder = w3cDoc.createElement("seller_under");
                        eleSellerUnder.setTextContent(ele.getElementsByAttributeValue("class", "obj-seller under").text());
                        eleNext.appendChild(eleSellerUnder);
                    }

                    
                    eleRoot.appendChild(eleNext);
                }
            }
            
            
            //return eleNext;
            
        }
        catch(Exception ex){
            ex.printStackTrace();
            //return null;
        }
    
    }
    
    private boolean SaveResultXML(org.w3c.dom.Document docResult){
        
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            tr.transform(new DOMSource(docResult), 
                            new StreamResult(new FileOutputStream("c:\\temp\\lun.xml")));

            return true;
                    
        } catch (TransformerException te) {
            System.out.println(te.getMessage());
            return false;
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
            return false;
        }     

    }
            
}

     
