package com.magedalaa.optlink.config;

import com.magedalaa.optlink.model.AccessLog;
import com.magedalaa.optlink.model.Company;
import com.magedalaa.optlink.model.Consumer;
import com.magedalaa.optlink.repository.AccessLogRepository;
import com.magedalaa.optlink.repository.CompanyRepository;
import com.magedalaa.optlink.repository.ConsumerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Loads fake demo data on startup so the app isn't empty on first run.
 * Every name, email, and phone number below is invented - no real
 * personal data is ever processed here.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final ConsumerRepository consumerRepository;
    private final CompanyRepository companyRepository;
    private final AccessLogRepository accessLogRepository;

    public DataSeeder(ConsumerRepository consumerRepository,
                       CompanyRepository companyRepository,
                       AccessLogRepository accessLogRepository) {
        this.consumerRepository = consumerRepository;
        this.companyRepository = companyRepository;
        this.accessLogRepository = accessLogRepository;
    }

    @Override
    public void run(String... args) {
        // Guard against double-seeding, in case this ever runs against a
        // persistent database instead of the in-memory one.
        if (consumerRepository.count() > 0) {
            return;
        }

        Consumer amira = seedConsumer("Amira Hassan", "amira.hassan@example.com", "+20 100 111 2222", "Cairo", "Fitness");
        Consumer omar = seedConsumer("Omar El-Sayed", "omar.elsayed@example.com", "+20 100 222 3333", "Giza", "Finance");
        Consumer nourhan = seedConsumer("Nourhan Adel", "nourhan.adel@example.com", "+20 100 333 4444", "Alexandria", "Retail");
        Consumer youssef = seedConsumer("Youssef Ibrahim", "youssef.ibrahim@example.com", "+20 100 444 5555", "Cairo", "Tech");
        Consumer salma = seedConsumer("Salma Tarek", "salma.tarek@example.com", "+20 100 555 6666", "Mansoura", "Food");
        Consumer karim = seedConsumer("Karim Fathy", "karim.fathy@example.com", "+20 100 666 7777", "Cairo", "Retail");
        Consumer laila = seedConsumer("Laila Mostafa", "laila.mostafa@example.com", "+20 100 777 8888", "Tanta", "Fitness");
        Consumer hossam = seedConsumer("Hossam Khaled", "hossam.khaled@example.com", "+20 100 888 9999", "Giza", "Finance");

        // One consumer who withdrew consent after signing up, so the search
        // endpoint has a real case to correctly exclude - proving
        // withdrawal actually removes someone from results, not just in
        // theory.
        Consumer withdrawn = seedConsumer("Mariam Sabry", "mariam.sabry@example.com", "+20 100 999 0000", "Cairo", "Retail");
        withdrawn.withdrawConsent();
        consumerRepository.save(withdrawn);

        Company activeSubscriber = new Company("Cairo Fit Gyms", "partnerships@cairofitgyms.example.com");
        activeSubscriber.activateSubscription();
        companyRepository.save(activeSubscriber);

        Company browsingOnly = new Company("Delta Retail Group", "leads@deltaretail.example.com");
        companyRepository.save(browsingOnly);

        // Seed a couple of AccessLog rows for the subscribed company, so
        // its /access-log endpoint has real history to show on first run
        // instead of an empty list.
        accessLogRepository.save(new AccessLog(activeSubscriber, amira));
        accessLogRepository.save(new AccessLog(activeSubscriber, laila));
    }

    private Consumer seedConsumer(String fullName, String email, String phone, String city, String interestCategory) {
        Consumer consumer = new Consumer(fullName, email, phone, city, interestCategory);
        consumer.giveConsent();
        return consumerRepository.save(consumer);
    }
}
