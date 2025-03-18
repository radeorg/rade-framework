package org.dows.rade.ddl.enums;

import org.dows.rade.ddl.api.AbstractDdl;
import org.dows.rade.ddl.api.DdlApi;
import org.dows.rade.ddl.api.db.Db2Ddl;
import org.dows.rade.ddl.api.db.MysqlDdl;
import org.dows.rade.ddl.api.db.OracleDdl;
import org.dows.rade.ddl.api.db.PostgreSQLDdl;

import java.util.function.Supplier;

public enum DbTypeEnum {
    DB2(Db2Ddl::new),
    MYSQL(MysqlDdl::new),
    ORAClE(OracleDdl::new),
    POSTGRESQL(PostgreSQLDdl::new);

    private final DdlApi db;

    DbTypeEnum(final Supplier<DdlApi> supplier) {
        this.db = supplier.get();
    }

    public DdlApi getDb() {
        return db;
    }

    public AbstractDdl getDbBase() {
        return (AbstractDdl) db;
    }


}
