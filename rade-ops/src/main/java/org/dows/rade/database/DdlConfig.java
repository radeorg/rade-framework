package org.dows.rade.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DdlConfig {
    private Dows dows;
}

@Data
class Dows {
    private List<Ddl> ddl;
}

@Data
class Ddl {
    private String database;
    private String username;
    private String password;
    private String url;
    @JsonProperty("driver-class-name")
    private String driverClassName;
    private String type;
    private List<Table> tables;
}

@Data
class Table {
    private String table;
    private List<Column> columns;
}

@Data
class Column {
    private String column;
    private String type;
    private Constraints constraints;
}

@Data
class Constraints {
    private Boolean primaryKey;
    private Boolean autoIncrement;
    private Boolean notNull;
    private Boolean unique;
    private Integer defaultValue;
    private String comment;
    private Boolean index;
    private Integer seq;
    private String indexType;
}
