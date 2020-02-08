import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EPN {
    JsonUtility jsonUtility = new JsonUtility();
    CloseableHttpClient httpclient = HttpClients.createDefault();

    private String error = "Unauthorized: invalid access token or incorrect token payload";

    private String regMethod = "https://app.epn.bz/auth/social/google?client_id=Uym179IeibNjJVHshF0gPMAd3kZYfLWv&role=user&redirect_after_auth_url=https%3A%2F%2Fepn.bz%2Fapp-auth";

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    private String access_token = "";

    @SneakyThrows
    public String getLongLinkForTG(String linkFromVk) {
        HttpPost httpPost = new HttpPost("https://app.epn.bz/creative/create");
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("access_token", access_token));
        nvps.add(new BasicNameValuePair("link", linkFromVk));
        nvps.add(new BasicNameValuePair("offerId", "1"));
        nvps.add(new BasicNameValuePair("description", "test"));
        nvps.add(new BasicNameValuePair("type", "link"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        CloseableHttpResponse response2 = httpclient.execute(httpPost);
        HttpEntity entity2 = response2.getEntity();
        String resp = IOUtils.toString(entity2.getContent());
        EntityUtils.consume(entity2);
        response2.close();
        if (jsonUtility.readJson(resp).at("/errors/0/error_description").asText().equals(error)) {
            System.out.println(resp);
        }
        return jsonUtility.readJson(resp).at("/data/attributes/code").asText();
    }

    public String getCutLinkForTG(String linkFromVk) {
        HttpPost httpPost = new HttpPost("https://app.epn.bz/link-reduction?access_token=" + access_token);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        String json = "{\"urlContainer\": [{\"url\":\"" + linkFromVk + "\", \"id\":1}],\"domainCutter\":\"ali.pub\"}";

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(json);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = null;
        String resp = "";
        try {
            response = httpclient.execute(httpPost);
            HttpEntity entity2 = response.getEntity();
            resp = IOUtils.toString(entity2.getContent());
            EntityUtils.consume(entity2);
            response.close();
            System.out.println(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonUtility.readJson(resp).at("/data/attributes").get(0).get("result").asText();
    }
}
