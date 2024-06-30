package com.vendor.management.system.domain.valueobject;

public abstract class AbstractValueObject<T> {
    public abstract T getValue();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractValueObject<?> that = (AbstractValueObject<?>) o;
        return getValue().equals(that.getValue());
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

}
