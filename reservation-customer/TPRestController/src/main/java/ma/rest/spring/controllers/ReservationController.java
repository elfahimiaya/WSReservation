package ma.rest.spring.controllers;


import ma.rest.spring.mapper.CustomerMapper;
import ma.rest.spring.model.Customer;
import ma.rest.spring.stub.CustomerServiceGrpc;
import ma.rest.spring.stub.CustomerServiceOuterClass;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.graphql.client.HttpGraphQlClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservationService")
public class ReservationController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CustomerSoapService customerSoapService;

    @Autowired
    private CustomerMapper customerMapper;

    @GrpcClient(value = "customerService")
    private CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub;


    // CREATE: (Placeholder for external service integration)
    @PostMapping("/customers")
    public ResponseEntity<String> createCustomer(@RequestBody Customer customer) {
        return ResponseEntity.ok("External service integration not implemented.");
    }

    // READ: Get all customers from the external service
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            Customer[] customers = restTemplate.getForObject("http://localhost:8083/customers", Customer[].class);
            return ResponseEntity.ok(Arrays.asList(customers));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // READ: Get a customer by ID from the external service
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        try {
            Customer customer = restTemplate.getForObject("http://localhost:8083/customers/" + id, Customer.class);
            return customer != null ? ResponseEntity.ok(customer) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    // UPDATE: (Placeholder for external service integration)
    @PutMapping("/customers/{id}")
    public ResponseEntity<String> updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return ResponseEntity.ok("External service integration not implemented.");
    }

    // DELETE: (Placeholder for external service integration)
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id) {
        return ResponseEntity.ok("External service integration not implemented.");
    }


    //Client non bloquant
    @GetMapping("/customers/v2")
    public Flux<Customer> listCustomersV2(){
        //RestTemplate restTemplate=new RestTemplate();
        WebClient webClient= WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
        Flux<Customer> customerFlux = webClient.get()
                .uri("/customers")
                .retrieve().bodyToFlux(Customer.class);
        return customerFlux;
    }

    @GetMapping("/customers/v2/{id}")
    public Mono<Customer> customerByIdV2(@PathVariable Long id){
        WebClient webClient= WebClient.builder()
                .baseUrl("http://localhost:8083")
                .build();
        Mono<Customer> customerMono = webClient.get()
                .uri("/customers/{id}",id)
                .retrieve().bodyToMono(Customer.class);
        return customerMono;
    }

    // graphql


    @GetMapping("/gql/customers/{id}")
    public Mono<Customer> customerByIdGql(@PathVariable Long id){
        HttpGraphQlClient graphQlClient=HttpGraphQlClient.builder()
                .url("http://localhost:8083/graphql")
                .build();
        var httpRequestDocument= """
                 query($id:Int) {
                    customerById(id:$id){
                      id, name, email
                    }
                  }
                """;
        Mono<Customer> customerById = graphQlClient.document(httpRequestDocument)
                .variable("id",id)
                .retrieve("customerById")
                .toEntity(Customer.class);
        return customerById;
    }

    @GetMapping("/gql/customers")
    public Mono<List<Customer>> customerListGql(){
        HttpGraphQlClient graphQlClient=HttpGraphQlClient.builder()
                .url("http://localhost:8083/graphql")
                .build();
        var httpRequestDocument= """
                 query {
                     allCustomers{
                       name,email, id
                     }
                   }
                """;
        Mono<List<Customer>> customers = graphQlClient.document(httpRequestDocument)
                .retrieve("allCustomers")
                .toEntityList(Customer.class);
        return customers;
    }

    //soap

    @GetMapping("/soap/customers")
    public List<Customer> soapCustomers(){
        List<ma.rest.spring.controllers.Customer> soapCustomers = customerSoapService.customerList();
        return soapCustomers.stream().map(customerMapper::fromSoapCustomer).collect(Collectors.toList());
    }


    @GetMapping("/soap/customerById/{id}")
    public Customer customerByIdSoap(@PathVariable Long id){
        ma.rest.spring.controllers.Customer soapCustomer = customerSoapService.customerById(id);
        return customerMapper.fromSoapCustomer(soapCustomer);
    }

    //grpc

    @GetMapping("/grpc/customers")
    public List<Customer> grpcCustomers(){
        CustomerServiceOuterClass.GetAllCustomersRequest request =
                CustomerServiceOuterClass.GetAllCustomersRequest.newBuilder().build();
        CustomerServiceOuterClass.GetCustomersResponse response =
                customerServiceBlockingStub.getAllCustomers(request);
        List<CustomerServiceOuterClass.Customer> customersList = response.getCustomersList();
        List<Customer> customerList =
                customersList.stream().map(customerMapper::fromGrpcCustomer).collect(Collectors.toList());
        return customerList;
    }
    @GetMapping("/grpc/customers/{id}")
    public Customer grpcCustomerById(@PathVariable Long id){
        CustomerServiceOuterClass.GetCustomerByIdRequest request =
                CustomerServiceOuterClass.GetCustomerByIdRequest.newBuilder()
                        .setCustomerId(id)
                        .build();
        CustomerServiceOuterClass.GetCustomerByIdResponse response =
                customerServiceBlockingStub.getCustomerById(request);
        return customerMapper.fromGrpcCustomer(response.getCustomer());
    }




}
