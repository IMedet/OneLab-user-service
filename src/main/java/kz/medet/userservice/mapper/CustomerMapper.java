package kz.medet.userservice.mapper;

import kz.medet.userservice.dto.CustomerResponseDto;
import kz.medet.userservice.entity.Customer;
import kz.medet.userservice.entity.CustomerDocument;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerResponseDto toResponseDto(Customer customer);

    CustomerResponseDto toResponseDto(CustomerDocument customerDocument);
}
