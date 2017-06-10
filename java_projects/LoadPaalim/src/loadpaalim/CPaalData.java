/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadpaalim;

/**
 *
 * @author vyrovoy
 * Includes all data that ifrom webpage regading one verb
 */
public class CPaalData {
    
    public String sVerbType;
    public String sRoot;
    public String sComments;
    
    public String sTranslation;
    public String sInfinitive;

    // Past
    public String sPastFirstPersonSingle;
    public String sPastFirstPersonPlural;

    public String sPastSecondPersonMaleSingular;
    public String sPastSecondPersonMalePlural;
    public String sPastSecondPersonFemaleSingular;
    public String sPastSecondPersonFemalePlural;
    
    public String sPastThirdPersonMaleSingular;
    public String sPastThirdPersonFemaleSingular;
    public String sPastThirdPersonPlural;    
    
    // Present
    public String sPresentMaleSingular;
    public String sPresentMalePlural;
    public String sPresentFemaleSingular;
    public String sPresentFemalePlural;    
    
    // Future
    public String sFutureFirstPersonSingle;
    public String sFutureFirstPersonPlural;

    public String sFutureSecondPersonMaleSingular;
    public String sFutureSecondPersonMalePlural;
    public String sFutureSecondPersonFemaleSingular;
    public String sFutureSecondPersonFemalePlural;
    
    public String sFutureThirdPersonMaleSingular;
    public String sFutureThirdPersonMalePlural;
    public String sFutureThirdPersonFemaleSingular;
    public String sFutureThirdPersonFemalePlural;        

    // Imperative
    public String sImperativeMaleSingular;
    public String sImperativeMalePlural;
    public String sImperativeFemaleSingular;
    public String sImperativeFemalePlural;
    
    public CPaalData(){}
    
    public CPaalData(CPaalData data){
    
        this.sVerbType = data.sVerbType;
        this.sRoot = data.sRoot;
        this.sComments = data.sComments;

        this.sTranslation = data.sTranslation;
        this.sInfinitive = data.sInfinitive;

        // Past
        this.sPastFirstPersonSingle = data.sPastFirstPersonSingle;
        this.sPastFirstPersonPlural = data.sPastFirstPersonPlural;

        this.sPastSecondPersonMaleSingular = data.sPastSecondPersonMaleSingular;
        this.sPastSecondPersonMalePlural = data.sPastSecondPersonMalePlural;
        this.sPastSecondPersonFemaleSingular = data.sPastSecondPersonFemaleSingular;
        this.sPastSecondPersonFemalePlural = data.sPastSecondPersonFemalePlural;

        this.sPastThirdPersonMaleSingular = data.sPastThirdPersonMaleSingular;
        this.sPastThirdPersonFemaleSingular = data.sPastThirdPersonFemaleSingular;
        this.sPastThirdPersonPlural = data.sPastThirdPersonPlural;    

        // Present
        this.sPresentMaleSingular = data.sPresentMaleSingular;
        this.sPresentMalePlural = data.sPresentMalePlural;
        this.sPresentFemaleSingular = data.sPresentFemaleSingular;
        this.sPresentFemalePlural = data.sPresentFemalePlural;    

        // Future
        this.sFutureFirstPersonSingle = data.sFutureFirstPersonSingle;
        this.sFutureFirstPersonPlural = data.sFutureFirstPersonPlural;

        this.sFutureSecondPersonMaleSingular = data.sFutureSecondPersonMaleSingular;
        this.sFutureSecondPersonMalePlural = data.sFutureSecondPersonMalePlural;
        this.sFutureSecondPersonFemaleSingular = data.sFutureSecondPersonFemaleSingular;
        this.sFutureSecondPersonFemalePlural = data.sFutureSecondPersonFemalePlural;

        this.sFutureThirdPersonMaleSingular = data.sFutureThirdPersonMaleSingular;
        this.sFutureThirdPersonMalePlural = data.sFutureThirdPersonMalePlural;
        this.sFutureThirdPersonFemaleSingular = data.sFutureThirdPersonFemaleSingular;
        this.sFutureThirdPersonFemalePlural = data.sFutureThirdPersonFemalePlural;        

        // Imperative
        this.sImperativeMaleSingular = data.sImperativeMaleSingular;
        this.sImperativeMalePlural = data.sImperativeMalePlural;
        this.sImperativeFemaleSingular = data.sImperativeFemaleSingular;
        this.sImperativeFemalePlural = data.sImperativeFemalePlural;
    
    }
    
}
