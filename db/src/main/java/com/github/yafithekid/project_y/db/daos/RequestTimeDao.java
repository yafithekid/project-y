package com.github.yafithekid.project_y.db.daos;

import com.github.yafithekid.project_y.db.models.RequestTime;

public interface RequestTimeDao {
    void save(RequestTime requestTime);

    RequestTime findEqualTimestamp(long timestamp);
}
