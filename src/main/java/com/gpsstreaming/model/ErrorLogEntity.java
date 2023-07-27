package com.gpsstreaming.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "gps_error_logs")
public class ErrorLogEntity implements Serializable {
    public String DateTime;
    public String IMEINo;
    public String Note;
}
