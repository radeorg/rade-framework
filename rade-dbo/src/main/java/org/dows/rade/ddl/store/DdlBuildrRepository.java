package org.dows.rade.ddl.store;

import org.dows.rade.ddl.api.DdlApi;
import org.dows.rade.ddl.enums.DbTypeEnum;

public class DdlBuildrRepository {

    public DdlApi mysql() {
        return DbTypeEnum.MYSQL.getDb();
    }

    public DdlApi oracle() {
        return DbTypeEnum.ORAClE.getDb();
    }

    public DdlApi db2() {
        return DbTypeEnum.DB2.getDb();
    }

    public DdlApi postgre() {
        return DbTypeEnum.POSTGRESQL.getDb();
    }

}
