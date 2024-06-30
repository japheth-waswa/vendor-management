package com.vendor.management.system.domain.valueobject;

public class Names {
    private final String firstName;
    private final String lastName;
    private final String otherNames;

    public Names(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherNames = null;
    }


    public Names(String firstName, String lastName, String otherNames) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.otherNames = otherNames;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getOtherNames() {
        return otherNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Names names = (Names) o;
        return firstName.equals(names.firstName) && lastName.equals(names.lastName) && otherNames.equals(names.otherNames);
    }

    @Override
    public int hashCode() {
        int result = firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + otherNames.hashCode();
        return result;
    }
}
