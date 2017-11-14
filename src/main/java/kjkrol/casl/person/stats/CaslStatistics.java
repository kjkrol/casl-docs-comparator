package kjkrol.casl.person.stats;

import com.mongodb.client.MongoCursor;
import kjkrol.casl.person.stats.model.PersonCmrStat;
import kjkrol.casl.person.stats.model.PersonContextCmrStat;
import kjkrol.casl.person.stats.model.PersonIds;
import kjkrol.casl.person.stats.model.PersonPcStat;
import kjkrol.casl.person.stats.model.repository.PersonCmrStatRepository;
import kjkrol.casl.person.stats.model.repository.PersonContextCmrStatRepository;
import kjkrol.casl.person.stats.model.repository.PersonIdsRepository;
import kjkrol.casl.person.stats.model.repository.PersonPcStatRepository;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ShellComponent
public class CaslStatistics {

    private final ContentRecords contentRecords;

    private final PersonCmrStatRepository personCmrStatRepository;
    private final PersonPcStatRepository personPcStatRepository;
    private final PersonContextCmrStatRepository personContextCmrStatRepository;
    private final PersonIdsRepository personIdsRepository;

    public CaslStatistics(ContentRecords contentRecords, PersonCmrStatRepository personCmrStatRepository,
            PersonPcStatRepository personPcStatRepository, PersonContextCmrStatRepository personContextCmrStatRepository,
            PersonIdsRepository personIdsRepository) {
        this.contentRecords = contentRecords;
        this.personCmrStatRepository = personCmrStatRepository;
        this.personPcStatRepository = personPcStatRepository;
        this.personContextCmrStatRepository = personContextCmrStatRepository;
        this.personIdsRepository = personIdsRepository;
    }

    @ShellMethod("Count PersonContexts for each Person in a specific Mongo collection.")
    @Transactional
    public void countPCs() {
        MongoCursor<Document> mongoCursor = contentRecords.cursorOfCountedPersonContexts();
        List<PersonPcStat> temporary = new ArrayList<>();
        int counter = 0;
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            ArrayList<UUID> list = document.get("_id", ArrayList.class);
            String id = list.stream()
                    .map(UUID::toString)
                    .collect(Collectors.joining(", "));
            int count = document.getInteger("count");
            personPcStatRepository.save(new PersonPcStat(id, count));
        }
        mongoCursor.close();
    }

    @ShellMethod("Count PersonCMR for each Person in a specific Mongo collection.")
    @Transactional
    public void countCMRs() {
        MongoCursor<Document> mongoCursor = contentRecords.cursorOfCountedPersonCmrs();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            UUID id = document.get("_id", UUID.class);
            int count = document.getInteger("count");
            personCmrStatRepository.save(new PersonCmrStat(id.toString(), count));
        }
        mongoCursor.close();
    }

    @ShellMethod("Count PersonContextCMRs for each PersonContext in a specific Mongo collection.")
    @Transactional
    public void countPcCMRs() {
        MongoCursor<Document> mongoCursor = contentRecords.cursorOfCountedPersonContextCmrs();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            UUID id = document.get("_id", UUID.class);
            String strId = id == null ? "null_id" : id.toString();
            int count = document.getInteger("count");
            personContextCmrStatRepository.save(new PersonContextCmrStat(strId, count));
        }
        mongoCursor.close();
    }

    @ShellMethod("Get all Persons ids.")
    @Transactional
    public void getPersonsIds() {
        MongoCursor<Document> mongoCursor = contentRecords.findAllPersonsIds();
        while (mongoCursor.hasNext()) {
            Document document = mongoCursor.next();
            UUID id = document.get("_id", UUID.class);
            personIdsRepository.save(new PersonIds(id.toString()));
        }
        mongoCursor.close();
    }

    @ShellMethod("Get all missing Persons ids.")
    @Transactional
    public void getMissingPersonsIds() {
        Pageable pageable = PageRequest.of(0, 1000);
        Page<String> page = personPcStatRepository.findMissingPersons(pageable);
        handlePage(page);
        while (page.hasNext()) {
            pageable = page.nextPageable();
            page = personPcStatRepository.findMissingPersons(pageable);
            handlePage(page);
        }
    }

    private void handlePage(Page<String> page) {
        System.out.println("page = " + page.toString());
        List<UUID> uids = page.getContent().stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        System.out.println("ids size() = " + uids.size());
        MongoCursor<Document> mongoCursor = contentRecords.cursorOfContentTypeByIds(uids);
        while (mongoCursor.hasNext()) {
            // simply print if found anything
            Document document = mongoCursor.next();
            System.out.println(document);
        }
        mongoCursor.close();
    }

    @ShellMethod("Find content by id.")
    @Transactional
    public void findContentById(String id) {
        Document document = contentRecords.findContent(UUID.fromString(id));
        System.out.println(document);
    }

}
