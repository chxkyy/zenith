package com.zenith.admin.api.system;

import java.util.List;

public interface DataPermissionService {

    DataScopeInfo getDataScope(Long userId);

    List<Long> getAccessibleOrgIds(Long userId);

    boolean hasFullAccess(Long userId);

    class DataScopeInfo {
        private int dataScope;
        private List<Long> orgIds;

        public DataScopeInfo(int dataScope, List<Long> orgIds) {
            this.dataScope = dataScope;
            this.orgIds = orgIds;
        }

        public int getDataScope() {
            return dataScope;
        }

        public List<Long> getOrgIds() {
            return orgIds;
        }
    }
}
