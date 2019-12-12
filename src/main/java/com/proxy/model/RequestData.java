
package com.proxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.util.LinkedMultiValueMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "headers",
        "data"
})
public class RequestData {

    @JsonProperty("headers")
    private LinkedMultiValueMap<String, String> headers;
    @JsonProperty("data")
    private String data;
    @JsonProperty("method")
    private String method;
    @JsonProperty("url")
    private String url;

    @JsonProperty("headers")
    public LinkedMultiValueMap<String, String> getHeaders() {
        return headers;
    }

    @JsonProperty("headers")
    public void setHeaders(LinkedMultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    @JsonProperty("data")
    public String getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(String data) {
        this.data = data;
    }

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
    }

    @JsonProperty("url")
    public String getURL() {
        return url;
    }

    @JsonProperty("url")
    public void setURL(String url) {
        this.url = url;
    }
}