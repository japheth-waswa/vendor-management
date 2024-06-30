package com.vendor.management.system.dataaccess.util;

import com.vendor.management.system.domain.valueobject.SortDirection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DataAccessHelper {
    public Pageable buildPageable(int pageNumber, int pageSize) {
        return buildPageable(pageNumber, pageSize, new ArrayList<>());
    }

    public Pageable buildPageable(int pageNumber, int pageSize, List<Sort.Order> orders) {
        if (!orders.isEmpty()) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
        } else {
            return PageRequest.of(pageNumber, pageSize);
        }
    }

    public Sort.Direction parseSortDirection(SortDirection sortDirection) {
        return sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

//    public  <T, U, V extends DomainException> List<T> parsePageableRecordsToList(Class<T> recordClass, Page<U> records, String recordsEmptyMessage) {
//        return parsePageableRecordsToList(recordClass, records, DomainException.class, recordsEmptyMessage);
//    }

//    public  <T, U, V extends DomainException> List<T> parsePageableRecordsToList(Class<T> recordClass, Page<U> records, Class<V> domainExceptionClass, String recordsEmptyMessage) {
//        if (records.isEmpty()) {
//            try {
//                throw domainExceptionClass.getConstructor(String.class).newInstance(recordsEmptyMessage);
//            } catch (Exception e) {
//                log.error("Error occurred while parsing records to list", e);
//                throw new DomainException("Error occurred while parsing records to list");
//            }
//        }
//        return records
//                .stream()
//                .map(record -> modelMapper.map(record, recordClass))
//                .toList();
//    }
}
