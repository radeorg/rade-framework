package org.dows.dbo.ddl.api;

import org.dows.dbo.ddl.api.ddl.*;

public interface DdlApi {
    Alter builderAlter();

    Create builderCreate();

    Snapshot buildSnapshot();

    Drop builderDrop();

    Truncate builderTruncate();
}
