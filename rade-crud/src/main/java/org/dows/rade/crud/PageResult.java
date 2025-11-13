package org.dows.rade.crud;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PageResult {
    private List<?> list;
    private Map<String, Object> pagination;
}