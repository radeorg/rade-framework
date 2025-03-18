package org.dows.rade.ddl.store;

import org.dows.rade.ddl.api.ddl.*;
import org.dows.rade.ddl.model.DbWrappers;

public final class DdlWrappersRepository {
    public DbWrappers<Alter> alter() {
        return new DbWrappers(Alter.class);
    }

    public DbWrappers<Create> create() {
        return new DbWrappers(Create.class);
    }

    public DbWrappers<Snapshot> snapshot() {
        return new DbWrappers(Snapshot.class);
    }

    public DbWrappers<Drop> drop() {
        return new DbWrappers(Drop.class);
    }

    public DbWrappers<Truncate> truncate() {
        return new DbWrappers(Truncate.class);
    }
}
