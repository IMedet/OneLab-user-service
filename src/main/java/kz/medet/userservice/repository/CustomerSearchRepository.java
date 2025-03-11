package kz.medet.userservice.repository;

import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.entity.CustomerDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSearchRepository extends ElasticsearchRepository<CustomerDocument,String> {
    List<CustomerDocument> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

}
