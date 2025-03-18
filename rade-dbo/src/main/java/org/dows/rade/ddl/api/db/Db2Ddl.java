package org.dows.rade.ddl.api.db;

import org.dows.rade.ddl.api.AbstractDdl;
import org.dows.rade.ddl.api.ddl.*;
import org.dows.rade.ddl.init.FactoryBuilder;

public class Db2Ddl extends AbstractDdl {
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
