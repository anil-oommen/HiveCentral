package com.oom.hive.central.model.govsg;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "area_metadata",
        "items",
        "api_info"
})
public class WeatherForecast {
    @JsonProperty("area_metadata")
    private List<AreaMetadatum> areaMetadata = null;
    @JsonProperty("items")
    private List<Item> items = null;
    @JsonProperty("api_info")
    private ApiInfo apiInfo;
    @JsonProperty("area_metadata")
    public List<AreaMetadatum> getAreaMetadata() {
        return areaMetadata;
    }
    @JsonProperty("area_metadata")
    public void setAreaMetadata(List<AreaMetadatum> areaMetadata) {
        this.areaMetadata = areaMetadata;
    }
    @JsonProperty("items")
    public List<Item> getItems() {
        return items;
    }
    @JsonProperty("items")
    public void setItems(List<Item> items) {
        this.items = items;
    }
    @JsonProperty("api_info")
    public ApiInfo getApiInfo() {
        return apiInfo;
    }
    @JsonProperty("api_info")
    public void setApiInfo(ApiInfo apiInfo) {
        this.apiInfo = apiInfo;
    }
}