import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

//https://oauth.vk.com/authorize?client_id=7153000&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=token&v=5.101&state=123456

public class VkTest {

    JsonUtility jsonUtility = new JsonUtility();


    private String rulePic = "/response/items/0/attachments/0/photo/sizes/6/url";
    private String ruleItem = "/response/items/0/text";

    private String token = "";

    public void setToken(String token) {
        this.token = token;
    }


    public String linkConstructor(String groupName, int offset, int count) {

        return "https://api.vk.com/method/wall.get?domain=" +
                groupName +
                "&offset=" +
                offset +
                "&count=" +
                count +
                "&access_token=" + token + "&v=5.101";
    }


    public String getStringResponse(String linkConstructor) {
        String s = "null";
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(new HttpGet(linkConstructor))) {
            HttpEntity entity = response.getEntity();
            s = IOUtils.toString(entity.getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String getPicLink(String resp) {
        return jsonUtility.readJson(resp).at(rulePic).asText();
    }

    public String getItemLink(String resp) {
        String postText = jsonUtility.readJson(resp).at(ruleItem).asText();
        int index = postText.indexOf("http");
        String s1 = postText.substring(index, index + 21);
        return s1;
    }

    public String getLinkForEpn(String link) throws IOException, URISyntaxException {
        String s = "null";
        RequestConfig requestConfig = RequestConfig.custom()
                .setCircularRedirectsAllowed(true)
                .build();

        CloseableHttpClient httpClient = HttpClients.custom().setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig)
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) " +
                        "AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11")
                .build();

        HttpClientContext context = HttpClientContext.create();
        HttpGet httpGet = new HttpGet(link);
//        System.out.println("Executing request " + httpGet.getRequestLine());
//        System.out.println("----------------------------------------");

        httpClient.execute(httpGet, context);
        HttpHost target = context.getTargetHost();
        List<URI> redirectLocations = context.getRedirectLocations();
        URI location = URIUtils.resolve(httpGet.getURI(), target, redirectLocations);
//        System.out.println("Final HTTP location: " + location.toASCIIString());

        s = location.toASCIIString();
        String linkForEpn = s.substring(0, s.indexOf("?")).replace("ru", "com");

        return linkForEpn;
    }
}
