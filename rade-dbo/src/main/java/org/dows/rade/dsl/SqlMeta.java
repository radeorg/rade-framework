package org.dows.rade.dsl;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class SqlMeta {

    String sql;
    List<Object> jdbcParamValues;
}
