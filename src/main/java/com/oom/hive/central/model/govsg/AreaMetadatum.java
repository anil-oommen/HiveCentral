
package com.oom.hive.central.model.govsg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "label_location"
})
public class AreaMetadatum {

    @JsonProperty("name")
    private String name;
    @JsonProperty("label_location")
    private LabelLocation labelLocation;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("label_location")
    public LabelLocation getLabelLocation() {
        return labelLocation;
    }

    @JsonProperty("label_location")
    public void setLabelLocation(LabelLocation labelLocation) {
        this.labelLocation = labelLocation;
    }

}
