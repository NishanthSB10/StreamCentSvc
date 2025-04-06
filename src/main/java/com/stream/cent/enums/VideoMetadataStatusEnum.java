package com.stream.cent.enums;


import com.stream.cent.enumeration.IEnum;


public enum VideoMetadataStatusEnum implements IEnum {
    PROCESSING("PROCESSING", "Processing"),
    FAILED("FAILED", "Failed"),
    SUCCESS("SUCCESS", "Success");

    VideoMetadataStatusEnum(String p0, String p1)
    {
        value = p0;
        description = p1;
    }

    protected String value;
    protected String description;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
