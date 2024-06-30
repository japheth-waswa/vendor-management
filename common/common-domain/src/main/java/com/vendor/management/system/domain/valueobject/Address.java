package com.vendor.management.system.domain.valueobject;

public class Address {
    private final String city;
    private final String street;
    private final String phone;
    private final String email;

    public Address(String city, String street, String phone, String email) {
        this.city = city;
        this.street = street;
        this.phone = phone;
        this.email = email;
    }


    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;
        return city.equals(address.city) && street.equals(address.street) && phone.equals(address.phone) && email.equals(address.email);
    }

    @Override
    public int hashCode() {
        int result = city.hashCode();
        result = 31 * result + street.hashCode();
        result = 31 * result + phone.hashCode();
        result = 31 * result + email.hashCode();
        return result;
    }
}
