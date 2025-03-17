package org.dows.dbo.ddl.api.db;

import org.dows.dbo.ddl.api.AbstractDdl;
import org.dows.dbo.ddl.api.ddl.*;
import org.dows.dbo.ddl.init.FactoryBuilder;

public class MysqlDdl extends AbstractDdl {
    @Override
    public Alter builderAlter() {
        return FactoryBuilder.Builder$Alter.getBean(this);
    }

    @Override
    public Create builderCreate() {
        return FactoryBuilder.Builder$Create.getBean(this);
    }

    @Override
    public Snapshot buildSnapshot() {
        return FactoryBuilder.Builder$Snapshot.getBean(this);
    }

    @Override
    public Drop builderDrop() {
        return FactoryBuilder.Builder$Drop.getBean(this);
    }

    @Override
    public Truncate builderTruncate() {
        return FactoryBuilder.Builder$Truncate.getBean(this);
    }
}
