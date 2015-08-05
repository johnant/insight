package offline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

import models.Document;
import models.Paragraph;

/**
 * @description Class that populates the document repository with web documents according to a set of queries or url links
 * 
 * @author Ioannis Antoniadis
 * @institution	Aristotle University of Thessaloniki
 * @department Electrical and Computer Engineering
 * @year 2015
 * 
 */

public class WebParser {

	List<LanguageProfile> languageProfiles;
	LanguageDetector languageDetector;
	TextObjectFactory textObjectFactory;
	
	public WebParser(){
		
		/*Language detector configuration*/		
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
		} catch (IOException e) {
			e.printStackTrace();
		}
		languageDetector = LanguageDetectorBuilder
				.create(NgramExtractors.standard())
		        .withProfiles(languageProfiles)
		        .build();
		textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();	
	}
	
	public void parseWebDocuments(String query){	
		
		System.out.println("Searching the web for documents");
		IndexManager indexManager = new IndexManager();
		
		Properties prop = indexManager.readProperties("C:\\Users\\John\\Documents\\Eclipse\\insight\\online.properties");
		int N = Integer.parseInt(prop.getProperty("N"));
		
		/* Documents by api search*/
		Document doc;
		int docCounter = indexManager.getDatasetDocumentsCount();
		System.out.println("Currently "+docCounter+" documents in dataset");
		JsonObject jobject;
		String url;
		
		JsonArray jarray = getBingSearchResults(query, N);
		StringBuilder textBuilder, analyzedTextBuilder;
		
		/* Parse html pages indicated by the urls to create news Document objects */
		for (JsonElement jelement: jarray){
			jobject = (JsonObject) jelement;
			url = jobject.get("Url").toString().replace("\"", "");
			if(!indexManager.checkDocumentExistance(url)){
				if(checkConnection(url)){
					doc = new Document();
					doc.setParagraphList(parseHtmlDocuments(url, languageDetector, textObjectFactory));
					if(doc.getParagraphList().size()>0 && doc.getParagraphList().size()<100){
						System.out.println("Creating document, id: "+docCounter+", paragraphs: "+doc.getParagraphList().size());
						doc.setId(docCounter++);
						doc.setUrl(url);
						doc.setTitle(jobject.get("Title").toString());
						textBuilder = new StringBuilder();
						analyzedTextBuilder = new StringBuilder();
						for(Paragraph par: doc.getParagraphList()){
							par.setParentId(doc.getId());
							par.setAnalyzedText(indexManager.analyze(par.getText()).trim());
							textBuilder.append(par.getText()+" "); //concatenation of paragraphs
							analyzedTextBuilder.append(par.getAnalyzedText()+" "); //concatenation of analyzed paragraphs
						}				
						doc.setText(textBuilder.toString().trim());
						doc.setAnalyzedText(analyzedTextBuilder.toString().trim());
						indexManager.indexDocument(doc);
					}
					else{
						System.out.println("Document discarded");
					}
				}	
			}
			else{
				System.out.println("Document already exists");
			}
		}
		System.out.println("Current dataset population: "+docCounter+" documents");
	}
	
	public JsonArray getBingSearchResults(String query, int n){
		
		String accountKey = "yAPsRY5LTphSQ0Qm3oTJJQj948xZQOidvSANQtDxmuA";
		
		query = query.replaceAll(" ", "%20");
		
		Client client = ClientBuilder.newClient();

		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(accountKey, accountKey);

		Response r = client.target("https://api.datamarket.azure.com/Bing/Search/Web?Query=%27" + query + "%27&$top="+n)
				.register(feature)
				.request()
				.accept("application/json")
				.get();
		
		if (r.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "+ r.getStatus());
		}
		
		String br = r.readEntity(String.class);
		
		/* Parse the result set to obtain the urls of the news articles */
		JsonElement jelement = new JsonParser().parse(br);
		JsonObject jobject = jelement.getAsJsonObject();
		jobject = jobject.getAsJsonObject("d");
		JsonArray jarray = jobject.getAsJsonArray("results");
		
		return jarray;
	}
	
	private Boolean checkConnection(String url){
		
		try {	            	 
			Connection.Response response = Jsoup
					.connect(url)
					.timeout(3*1000).execute();       		 
			return response.statusCode() == 200;  	 
		}	catch (Exception ex) {        		 
			System.out.print("\nCould not connect to: "+url+"\n");   		 
			return false; 
		}
	}

	private List<Paragraph> parseHtmlDocuments(String url, LanguageDetector languageDetector, TextObjectFactory textObjectFactory){
		
		org.jsoup.nodes.Document doc = null;
		List<Paragraph> ownParagraphList = new ArrayList<Paragraph>();
		
		try {
			doc = Jsoup.connect(url.replace("\"", ""))
					.userAgent("Chrome")
					.referrer("http://www.google.com") 
					.timeout(30*1000)
					.get();
		
		Elements paragraphs = doc.select("p");
		TextObject textObject;
		Optional<LdLocale> lang;
		Paragraph par;
		
		for (Element p: paragraphs){
			if(p.text().length()>200){
				textObject = textObjectFactory.forText(p.text());
				lang = languageDetector.detect(textObject);
				if(lang.isPresent()){
					if(lang.get().toString().equals("en")){
						par = new Paragraph();
						par.setText(p.text().replace("  ", " ").trim());
						par.setUrl(url);
						ownParagraphList.add(par);
					}
				}
			}
		}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ownParagraphList;
	}

}

