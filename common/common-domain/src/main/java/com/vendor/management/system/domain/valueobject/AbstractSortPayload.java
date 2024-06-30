package com.vendor.management.system.domain.valueobject;

public class AbstractSortPayload<T extends SortDirection,U> {
    private final T sortDirection;
    private final U sortField;

    public AbstractSortPayload(T sortDirection, U sortField) {
        this.sortDirection = sortDirection;
        this.sortField = sortField;
    }

    public T getSortDirection() {
        return sortDirection;
    }

    public U getSortField() {
        return sortField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractSortPayload<?, ?> that = (AbstractSortPayload<?, ?>) o;
        return sortDirection.equals(that.sortDirection) && sortField.equals(that.sortField);
    }

    @Override
    public int hashCode() {
        int result = sortDirection.hashCode();
        result = 31 * result + sortField.hashCode();
        return result;
    }
}
