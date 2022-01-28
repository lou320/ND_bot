import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.ArrayList;




public class WebReader {
    private final String url;
    Document document;
    WebReader(String url){
        this.url = url;
    }

    // getting html file
    Document getDocument() throws HttpStatusException{
        Document document = null;
        try {
            // get html file from url
            document = Jsoup.connect(url).get();
        } catch (Exception e){
            throw new HttpStatusException("can't find match gallery", 404, url);
        }
        this.document = document;
        return document;
    }

    // get how many pages are there in gallery
    int getPages(){
        int pages = Integer.parseInt(this.document.select("div.field-name.tag-container:nth-of-type(8) span.name").text());
        return pages;

    }

    // get gallery name
    String getName(){
        String name = (this.document.select("h1.title span.pretty").text()+ " " + this.document.select("h1.title span.after").text());
        return name;
    }
    
    String getGalleryCode(){
        String code = (this.document.select("h3#gallery_id").text().replace("#", ""));
        return code;
    }

    //get images links from the gallery
    ArrayList<String> getImgLinks(){
        ArrayList<String> links = new ArrayList<String>();
        for(Element src : this.document.select("a.gallerythumb img[src]")){
            StringBuffer str1 = new StringBuffer(src.attr("data-src"));
            if(str1.isEmpty()){
                continue;
            } else{
                StringBuffer str2 = new StringBuffer(str1.reverse().toString().replaceFirst("[t]",""));
                String link = str2.reverse().toString().replaceFirst("t.nhentai.net", "i.nhentai.net");
                links.add(link);
            }
        }
        return links;
    }
}
