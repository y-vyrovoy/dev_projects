/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadpaalim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;



/**
 *
 * @author vyrovoy
 */
public class CHtmlParseProcessor {

    public static final String SITE_ADDRESS = "http://www.pealim.com";
    
    public static Map<String, String> ParseWebPage(String sURL, iLogger logger){
        
        try{
            
            CPaalData pdReturn = new CPaalData();
            
            Map<String, String> mapResult = new HashMap<>();
            
            
            Document doc = Jsoup.connect(sURL).get();

            // looking for <div class="footer-wrap"> section
            Element eleFooterWrap = doc.getElementsByClass("footer-wrap").first();
            if(eleFooterWrap == null){
                logger.AddErrorMessage("Cannot load: " + sURL);
                return null;
            }
            
            // looking for <div class="container"> section inside <div class="footer-wrap">
            
            Element eleContainer = null;
            for( Node e : eleFooterWrap.childNodes() ){
            
                if( ((Element)e).className().equals("container") ){
                    eleContainer = ((Element)e);
                    break;
                }
            }
            if(eleContainer == null){
                logger.AddErrorMessage("Cannot load: " + sURL);
                return null;
            }
            
            // Reading data
            Elements eleP = eleContainer.getElementsByTag("p");
            
            if( (eleP != null) && (eleP.isEmpty() == false)){
                
                Iterator<Element> itP = eleP.iterator();
                
                pdReturn.sVerbType = itP.next().getElementsByTag("b").text();
                mapResult.put("verb_type", pdReturn.sVerbType);
                
                if(itP.hasNext())
                {
                    pdReturn.sRoot = itP.next().getElementsByTag("span").text();
                    mapResult.put("verb_root", pdReturn.sRoot);
                }

                if(itP.hasNext())
                {
                    pdReturn.sComments = itP.next().text();
                }

                if(itP.hasNext())
                {
                    pdReturn.sComments += itP.next().text();
                    mapResult.put("comments", pdReturn.sComments);
                }
            }
            
            
            // looking for <div class="lead">
            
            Element elePageLead = null;
            
            for( Node e : eleContainer.childNodes() ){
            
                if( ((Element)e).className().equals("lead") ){
                    elePageLead = ((Element)e);
                    break;
                }
            }
            
            if(elePageLead != null){
                pdReturn.sTranslation = elePageLead.text();
                mapResult.put("verb_translation", pdReturn.sTranslation);
            }
            
            // parsing table with the verb forms
          
            Element eleHScrollWraper = null;
            for( Node e : eleContainer.childNodes() ){
            
                if( ((Element)e).className().equals("horiz-scroll-wrapper") ){
                    eleHScrollWraper = ((Element)e);
                    break;
                }
            }
            
            if(eleHScrollWraper == null){
                logger.AddErrorMessage("Loaded but not full: " + sURL);
                return mapResult;
            }

            mapResult.put("verb_infinitive", eleHScrollWraper.getElementsByAttributeValue("id","INF-L").first().getElementsByClass("menukad").text());

            mapResult.put("present_ms", eleHScrollWraper.getElementsByAttributeValue("id","AP-ms").first().getElementsByClass("menukad").text());
            mapResult.put("present_mp", eleHScrollWraper.getElementsByAttributeValue("id","AP-mp").first().getElementsByClass("menukad").text());
            mapResult.put("present_fs", eleHScrollWraper.getElementsByAttributeValue("id","AP-fs").first().getElementsByClass("menukad").text());
            mapResult.put("present_fp", eleHScrollWraper.getElementsByAttributeValue("id","AP-fp").first().getElementsByClass("menukad").text());            
            
            mapResult.put("past_1s", eleHScrollWraper.getElementsByAttributeValue("id","PERF-1s").first().getElementsByClass("menukad").text());
            mapResult.put("past_1p", eleHScrollWraper.getElementsByAttributeValue("id","PERF-1p").first().getElementsByClass("menukad").text());
            
            mapResult.put("past_2ms", eleHScrollWraper.getElementsByAttributeValue("id","PERF-2ms").first().getElementsByClass("menukad").text());
            mapResult.put("past_2mp", eleHScrollWraper.getElementsByAttributeValue("id","PERF-2mp").first().getElementsByClass("menukad").text());
            mapResult.put("past_2fs", eleHScrollWraper.getElementsByAttributeValue("id","PERF-2fs").first().getElementsByClass("menukad").text());
            mapResult.put("past_2fp", eleHScrollWraper.getElementsByAttributeValue("id","PERF-2fp").first().getElementsByClass("menukad").text());
            
            mapResult.put("past_3ms", eleHScrollWraper.getElementsByAttributeValue("id","PERF-3ms").first().getElementsByClass("menukad").text());
            mapResult.put("past_3fs", eleHScrollWraper.getElementsByAttributeValue("id","PERF-3fs").first().getElementsByClass("menukad").text());
            mapResult.put("past_3p", eleHScrollWraper.getElementsByAttributeValue("id","PERF-3p").first().getElementsByClass("menukad").text());
            

            mapResult.put("future_1s", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-1s").first().getElementsByClass("menukad").text());
            mapResult.put("future_1p", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-1p").first().getElementsByClass("menukad").text());
            
            mapResult.put("future_2ms", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-2ms").first().getElementsByClass("menukad").text());
            mapResult.put("future_2mp", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-2mp").first().getElementsByClass("menukad").text());
            mapResult.put("future_2fs", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-2fs").first().getElementsByClass("menukad").text());
            mapResult.put("future_2fp", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-2fp").first().getElementsByClass("menukad").text());
            
            mapResult.put("future_3ms", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-3ms").first().getElementsByClass("menukad").text());
            mapResult.put("future_3mp", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-3mp").first().getElementsByClass("menukad").text());
            mapResult.put("future_3fs", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-3fs").first().getElementsByClass("menukad").text());
            mapResult.put("future_3fp", eleHScrollWraper.getElementsByAttributeValue("id","IMPF-3fp").first().getElementsByClass("menukad").text());
            
            mapResult.put("imperative_ms", eleHScrollWraper.getElementsByAttributeValue("id","IMP-2ms").first().getElementsByClass("menukad").text());
            mapResult.put("imperative_mp", eleHScrollWraper.getElementsByAttributeValue("id","IMP-2mp").first().getElementsByClass("menukad").text());
            mapResult.put("imperative_fs", eleHScrollWraper.getElementsByAttributeValue("id","IMP-2fs").first().getElementsByClass("menukad").text());
            mapResult.put("imperative_fp", eleHScrollWraper.getElementsByAttributeValue("id","IMP-2fp").first().getElementsByClass("menukad").text());            
            

            System.out.println("Page [" + sURL + "] parsed. " + pdReturn.sTranslation + " : " + pdReturn.sInfinitive);
            logger.SetCurrentStatus("Page [" + sURL + "] parsed. " + pdReturn.sTranslation + " : " + pdReturn.sInfinitive);
            return mapResult;            
            
        }catch(Exception ex){
            System.out.println(ex.getLocalizedMessage());
            logger.AddErrorMessage("Cannot load: " + sURL);
            return null;
        }
    }
    
    public static String SearchForWordAtSite(String sWord, iLogger logger){
        
        String sQuery = "http://www.pealim.com/search/?q=" + sWord;
        
        try{
            Document doc = Jsoup.connect(sQuery).get();
            
            // looking for <div class="results-by-verb"> section
            Element eleResultByVerb = doc.getElementsByClass("results-by-verb").first();
            if(eleResultByVerb == null){
                logger.AddErrorMessage("Cannot load: " + sWord + ". No  <div class=\"results-by-verb\">");
                return null;
            }

            Element eleButton = eleResultByVerb.getElementsByAttributeValue("class", "btn btn-primary").first();

            if(eleButton == null){
                logger.AddErrorMessage("Cannot load: " + sWord + ". No  <a class=\"btn btn-primary\">");
                return null;
            }
            
            String sHref = SITE_ADDRESS + eleButton.attr("href");
            
            logger.SetCurrentStatus(sWord + " -> " + sHref);
            System.out.println(sWord + " -> " + sHref);
            
            return sHref;
            
        }catch(Exception ex){
            System.out.println(ex.getLocalizedMessage());
            logger.AddErrorMessage("Cannot load: " + sWord);
            return null;
        }
        
    }
}
