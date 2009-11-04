############################
# CRONOS-WebService README #
############################

WSDL for WebService is available at:
-----------------------------------

http://mips.helmholtz-muenchen.de/CronosWSService/CronosWS?WSDL


Supported Methods:
------------------

public boolean isinRedList(String name,String organism3Letter)
returns true if name is an ambiguous gene or protein name, false otherwise.

public String cronosWS(String inputId, String organism3Letter, int queryIntId, int targetIntId)
returns the corresponding entry for 'inputId' in output type 'targetIntId' 

Parameters:
-----------

name: gene or protein name 
organism3Letter: Abbreviation for Organism

		organism		| organism3Letter
		------------------------------------------
		Homo sapiens		| hsa
		Mus musculus		| mmu
		Rattus norvegicus	| rno
		Bos taurus		| bta
		Canis familiaris	| cfa
		Drosophila melanogaster	| dme


inputId: EntryID of a particular Database (NM_12345, ENSMUST00004567, …)
queryIntId: Type of the input-ID as integer value (for translation of input Types to integer values see tables below) (Example your inputId is NM_12345 then the queryIntId is 3)
targetIntId: Type of the targeted Database-Type as integer value (e.g. translation into Ensembl Gene ID is wanted then targetIntId is 5) 


Translation Tables for Query- and Target Database IDs
-----------------------------------------------------

1. Values for QueryId and TargetId: mostly used

|-----------------------------------------------|
| Integer value	| Query/Target-Type	        | 
|-----------------------------------------------|
|   1		| Gene Name			|
|   2		| Protein Name			|	
|   3		| RefSeq			|
|   4		| UniProt			|
|   5		| Ensembl/FlyBase Gene ID 	|
|   6		| Ensembl/FlyBase Transcript ID	|
|   7		| Ensembl/FlyBase Protein ID	|
|   8		| GI				|
|   9		| GeneID			|
|  10		| EMBL				|
|  11		| PIR				|
|  12		| DBSNP				|
|  13		| UniSTS			|
|  14		| HGNC				|
|  17		| MfunGD			|
|  18		| MGI				|
-------------------------------------------------

2. Values for QueryId and TargetId: Expression-Analysis, human and mouse

|-------------------------------------------------------------------------------|
| Integer value	| Query/Target-Type	| Integer value	| Query/Target-Type	|
|-------------------------------------------------------------------------------|
| 200		| affy_hc_g110		| 500		| affy_mg_u74a		|
| 210		| affy_hg_u133_plus_2	| 510		| affy_mg_u74av2	|
| 220		| affy_hg_u133a_2	| 520		| affy_mg_u74b		|
| 230		| affy_hg_u133a		| 530		| affy_mg_u74bv2	|
| 240		| affy_hg_u133b		| 540		| affy_mg_u74c		|
| 250		| affy_u133_x3p		| 550		| affy_mg_u74cv2	|
| 260		| affy_hg_u95a		| 560		| affy_moe430a		|
| 270		| affy_hg_u95av2	| 570		| affy_moe430b		|
| 280		| affy_hg_u95b		| 580		| affy_mouse430_2	|
| 290		| affy_hg_u95c		| 590		| affy_mouse430a_2	|
| 300		| affy_hg_u95d		| 600		| affy_mu11ksuba	|
| 310		| affy_hg_u95e		| 610		| agilentprobe		|
| 320		| affy_hg_focus		|---------------------------------------|	
| 330		| affy_hugenefl		|	
| 		| 			|		
| 350		| agilentcgh		|	
| 360		| agilentprobe		|	
----------------------------------------|

3. Values for QueryId and TargetId: Expression-Analysis, rat, cow and dog

|-------------------------------------------------------------------------------|
| Integer value	| Query/Target-Type	| Integer value	| Query/Target-Type	|
|-------------------------------------------------------------------------------|
| 700		| affy_rg_u34a		| 800		| affy_bovine		|
| 710		| affy_rg_u34b		|---------------------------------------| 			
| 720		| affy_rg_u34c		| 900		| affy_canine		|
| 730		| affy_rat230_2		|---------------------------------------|			
| 740		| affy_rae230a		|			
| 750		| affy_rae230b		|	
| 760		| affy_rn_u34		|	
| 770		| affy_rt_u34		|	
| 780		| agilentprobe		|
|---------------------------------------|	

3. Values for QueryId and TargetId: Expression-Analysis, fruit fly

|---------------------------------------|
| Integer value	| Query/Target-Type	|
|---------------------------------------|
| 1000		| affy_drosgenome1	|
| 1010		| affy_drosophila2	|			
| 1020		| BDGP_insitu_expr	|
| 1030		| DEBb			|		
|---------------------------------------|	


Example for calling the WebService in Java
------------------------------------------
package call;

/**
 * @author brigitte
 */

public class Call
{
	public static void main(String []args)
	{
		try 
		{ 	// Call Web Service Operation
			cronos.webservice.CronosWSService service = new cronos.webservice.CronosWSService();
			cronos.webservice.CronosWS port = service.getCronosWSPort();
   	 		// TODO initialize WS operation arguments here
			java.lang.String inputId = "eno1";
			java.lang.String organism3Letter = "hsa";
			int queryIntId = 1; //gene name
			int targetIntId = 2;//protein name
   			// TODO process result here
   			java.lang.String result = port.cronosWS(inputId, organism3Letter, queryIntId, targetIntId);
   			System.out.println("Result = "+result);
		} 
		catch (Exception ex)
		{
	   		System.out.println(ex);
		}

	}
}



