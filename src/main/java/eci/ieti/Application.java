package eci.ieti;

import eci.ieti.data.CustomerRepository;
import eci.ieti.data.ProductRepository;
import eci.ieti.data.TodoRepository;
import eci.ieti.data.UserRepository;
import eci.ieti.data.model.Customer;
import eci.ieti.data.model.Product;

import eci.ieti.data.model.Todo;
import eci.ieti.data.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.List;


@SpringBootApplication
public class Application implements CommandLineRunner {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
        MongoOperations mongoOperation = (MongoOperations) applicationContext.getBean("mongoTemplate");

        userRepository.deleteAll();

        for(int i=0;i<10;i++){
            userRepository.save(new User("user"+i, "user"+i+"mail.com"));
        }

        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");

        userRepository.findAll().stream().forEach(System.out::println);
        System.out.println();

        todoRepository.deleteAll();

        for(int i=0;i<25;i++){
            Integer date = (i%21)+10;
            todoRepository.save(new Todo("hola",i,"2021-03-"+date,"user"+i%10+"@mail.com","hola"));
        }

        System.out.println();

        LocalDate date = LocalDate.now();
        Query query = new Query();
        query.addCriteria(Criteria.where("dueDate").lt(date.toString()));

        List<Todo> todos = mongoOperation.find(query,Todo.class);
        List<Todo> todosd=todoRepository.findByDueDateBefore(date.toString());
        System.out.println(todos.size());
        System.out.println(todosd.size());

        query = new Query();
        query.addCriteria(Criteria.where("responsible").is("user1@mail.com")
                .and("priority").gte(5));

        List<Todo> todos1 = mongoOperation.find(query,Todo.class);
        List<Todo> todosd1=todoRepository.findByResponsibleEqualsAndPriorityIsGreaterThanEqual("user1@mail.com",5);
        System.out.println(todos1.size());
        System.out.println(todosd1.size());

        todoRepository.save(new Todo("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",1,"2021-03-22","user1@mail.com","hola"));
        query = new Query();
        query.addCriteria(Criteria.where("description").regex("^.{30,}$"));
        List<Todo> todos2 = mongoOperation.find(query,Todo.class);
        List<Todo> todosd2 = todoRepository.findByDescriptionMatchesRegex("^.{30,}$");
        System.out.println(todos2.size());
        System.out.println(todosd2.size());


    }

}