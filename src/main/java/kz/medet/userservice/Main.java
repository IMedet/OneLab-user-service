//package kz.medet.userservice;
//
//import kz.medet.userservice.config.Configs;
//import kz.medet.userservice.dto.OrderResponse;
//import kz.medet.userservice.entity.CustomerDocument;
//import kz.medet.userservice.exceptions.CustomException;
//import kz.medet.userservice.service.impl.CustomerService;
//import kz.medet.userservice.service.impl.UserService;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//
//import java.util.List;
//import java.util.Scanner;
//import java.util.logging.Logger;
//
//public class Main {
//    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
//
//    public static void main(String[] args) {
//        ApplicationContext context = new AnnotationConfigApplicationContext(Configs.class);
//        UserService services = context.getBean(UserService.class);
//        CustomerService customerService = context.getBean(CustomerService.class);
//
//        Scanner in = new Scanner(System.in);
//        boolean running = true;
//
//
//        System.out.println("Добро пожаловать! Войдите в систему или зарегистрируйтесь.");
//
//        String username;
//        while (true) {
//            System.out.println("""
//                    1 - Войти в систему
//                    2 - Зарегистрироваться
//                    """);
//            System.out.print("Выберите действие: ");
//            int authChoice = in.nextInt();
//            in.nextLine();
//
//            if (authChoice == 1) {
//                System.out.print("Введите имя пользователя: ");
//                username = in.nextLine();
//                System.out.print("Введите пароль: ");
//                String password = in.nextLine();
//
//                if (services.login(username, password)) {
//                    System.out.println("Вход выполнен успешно.");
//                    break;
//                } else {
//                    System.out.println("Ошибка входа! Попробуйте снова.");
//                }
//            } else if (authChoice == 2) {
//                System.out.print("Введите имя пользователя: ");
//                username = in.nextLine();
//                System.out.print("Введите пароль: ");
//                String password = in.nextLine();
//                System.out.print("Введите ИИН: ");
//                String iin = in.nextLine();
//                System.out.print("Введите номер телефона: ");
//                String phoneNumber = in.nextLine();
//                System.out.print("Введите ФИО: ");
//                String fio = in.nextLine();
//
//                services.register(username, password, iin, phoneNumber, fio);
//                System.out.println("Регистрация завершена! Вход в систему...");
//                break;
//            } else {
//                System.out.println("Некорректный ввод, попробуйте снова.");
//            }
//        }
//
//
//        while (running) {
//            System.out.println("--------\n" +
//                    "What you wanna ?\n" +
//                    "1 - Show All Customers\n" +
//                    "2 - Add Customer\n" +
//                    "3 - Add Order To Customer\n" +
//                    "4 - Show Order of Customer\n" +
//                    "5 - Add Product To Order\n" +
//                    "6 - Show Product of Order\n" +
//                    "7 - Search By FirstName or LastName\n" +
//                    "8 - Exit\n\n");
//
//            System.out.println("Enter your choice below: ");
//            int choice = in.nextInt();
//
//            switch (choice) {
//                case 1:
//                    System.out.println("Customers: \n");
//                    customerService.getAllCustomers().stream().forEach(customerDto ->
//                            System.out.println("Id: " + customerDto.getId() + ", "
//                                    + "FirstName: " + customerDto.getFirstName() + ", "
//                                    + "LastName: " + customerDto.getLastName()));
//                    break;
//                case 2:
//                    in.nextLine();
//                    System.out.println("Enter customer's firstName: ");
//                    String firstName = in.nextLine();
//
//                    System.out.println("Enter customer's lastName: ");
//                    String lastName = in.nextLine();
//
//                    customerService.addCustomer(firstName, lastName);
//
//                    System.out.println("Customer successfully added");
//                    break;
//                case 3:
//                    System.out.println("Enter Id which customer you want to add Order: ");
//                    Long customerId = in.nextLong();
//
//                    try {
//                        customerService.addOrderToCustomer(customerId);
//                    } catch (CustomException exception) {
//                        LOGGER.warning(exception.getMessage());
//                    }
//
//                    System.out.println("Order was created for Customer Id: " + customerId);
//                    break;
//                case 4:
//                    System.out.println("Enter Id of Customer whom orders you wanna see: ");
//                    Long customer_Id = in.nextLong();
//                    try {
//                        OrderResponse orderResponse = customerService.getCustomerOrders(customer_Id);
//                        System.out.println(orderResponse.toString());
//
//                    } catch (CustomException exception) {
//                        LOGGER.warning(exception.getMessage());
//                    }
//                    break;
//                case 5:
//                    System.out.println("Enter Order Id to add Product");
//                    Long order_Id = in.nextLong();
//
//                    in.nextLine();
//
//                    System.out.println("Enter name of Product: ");
//                    String name = in.nextLine();
//
//                    System.out.println("Enter price of Product: ");
//                    double price = in.nextDouble();
//
//                    try {
//                        String productId = customerService.addProductToOrder(order_Id, name, price);
//                        System.out.println(productId);
//                    } catch (Exception exception) {
//                        LOGGER.warning("ResourceNotFoundException: " + exception.getMessage());
//                    }
//
//                    break;
////                case 6:
////                    in.nextLine();
////
////                    System.out.println("Enter Order Id to see its Product List: ");
////                    Long orderId = in.nextLong();
////
////                    try {
////                        customerService.getAllProductsOfOrder(orderId).stream().forEach(
////                                productDto -> System.out.println(productDto.toString()));
////                    } catch (ResourceNotFoundException exception) {
////                        LOGGER.warning(exception.getMessage());
////                    }
////                    break;
//                case 7:
//                    in.nextLine();
//
//                    System.out.println("Enter FirstName or LastName to find: ");
//                    String query = in.nextLine();
//                    List<CustomerDocument> customerDocuments = customerService.searchCustomers(query);
//                    if (customerDocuments.isEmpty()) {
//                        System.out.println("Customers not found.");
//                    } else {
//                        customerDocuments.forEach(customer ->
//                                System.out.println("Customer foun: " + customer.getFirstName() + " " + customer.getLastName()));
//                    }
//                    break;
//                case 8:
//                    System.out.println("Existing the App!");
//                    running = false;
//                    break;
//                default:
//                    System.out.println("No such choice, try again ");
//            }
//        }
//
//        in.close();
//        System.exit(0);
//    }
//}
