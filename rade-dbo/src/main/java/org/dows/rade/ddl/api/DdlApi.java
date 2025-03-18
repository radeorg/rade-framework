package org.dows.rade.ddl.api;

import org.dows.rade.ddl.api.ddl.*;

public interface DdlApi {
    Alter builderAlter();

    Create builderCreate();

    Snapshot buildSnapshot();

    Drop builderDrop();

    Truncate builderTruncate();
}
