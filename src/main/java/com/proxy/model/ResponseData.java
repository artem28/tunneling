
package com.proxy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.util.LinkedMultiValueMap;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "headers",
        "data",
        "status"
})
public class ResponseData {

    @JsonProperty("headers")
    private LinkedMultiValueMap<String, String> headers;

    @JsonProperty("data")
    private String data;

    @JsonProperty("status")
    private int status = 200;

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

    @JsonProperty("status")
    public int getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(int status) {
        this.status = status;
    }
}