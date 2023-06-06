package challenge18.hotdeal.common.config.rds;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RoutingDatasource extends AbstractRoutingDataSource {
    private final DataSourceKey dataSourceKey;
    private final ReadOnlyDataSourceCycle<String> readOnlyDataSourceCycle = new ReadOnlyDataSourceCycle<>();

    @Override
    public void setTargetDataSources(Map<Object, Object> targetDataSources) {
        super.setTargetDataSources(targetDataSources);

        List<String> readOnlyDataSourceLookupKeys = targetDataSources.keySet()
                .stream()
                .map(String::valueOf)
                .filter(lookupKey -> lookupKey.contains("slave")).collect(Collectors.toList());

        readOnlyDataSourceCycle.setReadOnlyDataSourceLookupKeys(readOnlyDataSourceLookupKeys);
    }

    @Override
    protected Object determineCurrentLookupKey() {
//        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
//        if (isReadOnly) {
////            logger.info("Connection Slave");
//            return dataSourceKey.getDefaultSlaveKey();
//        } else {
////            logger.info("Connection Master");
//            return dataSourceKey.getMasterKey();
//        }

        return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? readOnlyDataSourceCycle.getReadOnlyDataSourceLookupKey()
                : dataSourceKey.getMasterKey();
    }
}
