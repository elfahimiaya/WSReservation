package ma.rest.spring.mapper;

import ma.rest.spring.model.Customer;
import ma.rest.spring.stub.CustomerServiceOuterClass;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    private ModelMapper modelMapper=new ModelMapper();
    public Customer fromSoapCustomer(ma.rest.spring.controllers.Customer soapCustomer){
        return modelMapper.map(soapCustomer,Customer.class);
    }

    public Customer fromGrpcCustomer(CustomerServiceOuterClass.Customer grpcCustomer){
        return modelMapper.map(grpcCustomer, Customer.class);
    }
}