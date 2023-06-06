package challenge18.hotdeal.common.config.rds;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ReadOnlyDataSourceCycle<T> {
    private List<T> readOnlyDataSourceLookupKeys;
    private int index = 0;

    public void setReadOnlyDataSourceLookupKeys(List<T> readOnlyDataSourceLookupKeys) {
        this.readOnlyDataSourceLookupKeys = readOnlyDataSourceLookupKeys;
    }

    public T getReadOnlyDataSourceLookupKey() {
        if (index + 1 >= readOnlyDataSourceLookupKeys.size()) {
            index = -1;
        }

        return readOnlyDataSourceLookupKeys.get(++index);
    }
}
