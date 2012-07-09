package org.bridgedb.result;

/**
 *
 * @author Christian
 */
public class ResultBase {

    protected String errorMessage;

    protected ResultBase(){
        this.errorMessage = "";
    }

    protected ResultBase(String errorMessage){
        this.errorMessage = errorMessage;
    }
    
    protected ResultBase(Exception ex, String safeMessage){
        ex.printStackTrace();
        this.errorMessage = ex.toString();
    }
    
    protected ResultBase(Exception ex, String query, String safeMessage){
        System.err.println("query: " + query);
        ex.printStackTrace();
        this.errorMessage = ex.toString() + "query " + query;
    }
    
    public boolean isValid(){
        return errorMessage.isEmpty();
    }
    
    protected boolean sameError(ResultBase other){
        if (errorMessage.isEmpty()) return false;
        return errorMessage.equals(other.errorMessage);
    }

}
