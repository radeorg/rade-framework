package org.dows.rade.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class DdlGenerator {

    public static void main(String[] args) {
        String jsonFilePath = "ddl.json";
        ClassPathResource resource = new ClassPathResource(jsonFilePath);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DdlConfig ddlConfig = objectMapper.readValue(resource.getFile(), DdlConfig.class);
            generateCreateTableStatements(ddlConfig);
        } catch (IOException e) {
            log.error("Error reading or parsing JSON file", e);
        }
    }

    private static void generateCreateTableStatements(DdlConfig ddlConfig) {
        for (Ddl ddl : ddlConfig.getDows().getDdl()) {
            for (Table table : ddl.getTables()) {
                StringBuilder createTableSql = new StringBuilder();
                createTableSql.append("CREATE TABLE ").append(table.getTable()).append(" (");

                List<Column> columns = table.getColumns();
                List<String> primaryKeyColumns = columns.stream()
                        .filter(col -> col.getConstraints().getPrimaryKey() != null && col.getConstraints().getPrimaryKey())
                        .map(Column::getColumn)
                        .collect(Collectors.toList());

                List<String> indexColumns = columns.stream()
                        .filter(col -> col.getConstraints().getIndex() != null && col.getConstraints().getIndex())
                        .sorted((col1, col2) -> Integer.compare(col1.getConstraints().getSeq(), col2.getConstraints().getSeq()))
                        .map(Column::getColumn)
                        .collect(Collectors.toList());

                Map<String, List<String>> indexTypeColumnsMap = columns.stream()
                        .filter(col -> col.getConstraints().getIndexType() != null && !col.getConstraints().getIndexType().isEmpty())
                        .collect(Collectors.groupingBy(
                                col -> col.getConstraints().getIndexType(),
                                Collectors.mapping(Column::getColumn, Collectors.toList())
                        ));

                for (int i = 0; i < columns.size(); i++) {
                    Column col = columns.get(i);
                    createTableSql.append(col.getColumn()).append(" ").append(col.getType());

                    Constraints constraints = col.getConstraints();
                    if (constraints.getPrimaryKey() != null && constraints.getPrimaryKey()) {
                        createTableSql.append(" PRIMARY KEY");
                    }
                    if (constraints.getAutoIncrement() != null && constraints.getAutoIncrement()) {
                        createTableSql.append(" AUTO_INCREMENT");
                    }
                    if (constraints.getNotNull() != null && constraints.getNotNull()) {
                        createTableSql.append(" NOT NULL");
                    }
                    if (constraints.getUnique() != null && constraints.getUnique()) {
                        createTableSql.append(" UNIQUE");
                    }
                    if (constraints.getDefaultValue() != null) {
                        createTableSql.append(" DEFAULT ").append(constraints.getDefaultValue());
                    }
                    if (constraints.getComment() != null && !constraints.getComment().isEmpty()) {
                        createTableSql.append(" COMMENT '").append(constraints.getComment()).append("'");
                    }

                    if (i < columns.size() - 1) {
                        createTableSql.append(", ");
                    }
                }

                if (!primaryKeyColumns.isEmpty()) {
                    createTableSql.append(", PRIMARY KEY (").append(String.join(", ", primaryKeyColumns)).append(")");
                }

                createTableSql.append(");");

                log.info(createTableSql.toString());

                // 添加索引
                if (!indexColumns.isEmpty()) {
                    StringBuilder indexSql = new StringBuilder();
                    indexSql.append("CREATE INDEX idx_").append(table.getTable()).append(" ON ").append(table.getTable()).append(" (");
                    indexSql.append(String.join(", ", indexColumns)).append(");");
                    log.info(indexSql.toString());
                }

                // 添加分组索引
                for (Map.Entry<String, List<String>> entry : indexTypeColumnsMap.entrySet()) {
                    String indexType = entry.getKey();
                    List<String> indexedColumns = entry.getValue();
                    StringBuilder indexSql = new StringBuilder();
                    indexSql.append("CREATE ").append(indexType).append(" INDEX idx_").append(table.getTable()).append("_").append(indexType.toLowerCase()).append(" ON ").append(table.getTable()).append(" (");
                    indexSql.append(String.join(", ", indexedColumns)).append(");");
                    log.info(indexSql.toString());
                }
            }
        }
    }
}
