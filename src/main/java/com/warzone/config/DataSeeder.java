package com.warzone.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warzone.base.MilitaryBase;
import com.warzone.base.BaseRepository;
import com.warzone.conflict.Conflict;
import com.warzone.conflict.ConflictRepository;
import com.warzone.military.MilitaryPower;
import com.warzone.military.MilitaryRepository;
import com.warzone.nuclear.NuclearArsenal;
import com.warzone.nuclear.NuclearRepository;
import com.warzone.timeline.TimelineEvent;
import com.warzone.timeline.TimelineRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final MilitaryRepository militaryRepo;
    private final NuclearRepository nuclearRepo;
    private final BaseRepository baseRepo;
    private final ConflictRepository conflictRepo;
    private final TimelineRepository timelineRepo;
    private final ObjectMapper mapper;

    public DataSeeder(MilitaryRepository militaryRepo, NuclearRepository nuclearRepo,
                      BaseRepository baseRepo, ConflictRepository conflictRepo,
                      TimelineRepository timelineRepo, ObjectMapper mapper) {
        this.militaryRepo = militaryRepo;
        this.nuclearRepo = nuclearRepo;
        this.baseRepo = baseRepo;
        this.conflictRepo = conflictRepo;
        this.timelineRepo = timelineRepo;
        this.mapper = mapper;
    }

    @Override
    public void run(String... args) {
        loadJsonData("data/military-power.json", militaryRepo, new TypeReference<List<MilitaryPower>>() {}, "military");
        loadJsonData("data/nuclear-arsenals.json", nuclearRepo, new TypeReference<List<NuclearArsenal>>() {}, "nuclear");
        loadJsonData("data/military-bases.json", baseRepo, new TypeReference<List<MilitaryBase>>() {}, "bases");
        seedConflicts();
        seedTimeline();
        log.info("Database seeding complete");
    }

    private <T> void loadJsonData(String path, org.springframework.data.jpa.repository.JpaRepository<T, ?> repo,
                                   TypeReference<List<T>> typeRef, String label) {
        if (repo.count() > 0) {
            log.info("{} data already present, skipping", label);
            return;
        }
        try {
            InputStream stream = new ClassPathResource(path).getInputStream();
            List<T> items = mapper.readValue(stream, typeRef);
            repo.saveAll(items);
            log.info("Loaded {} {} records", items.size(), label);
        } catch (Exception e) {
            log.error("Failed to load {}: {}", label, e.getMessage());
        }
    }

    private void seedConflicts() {
        if (conflictRepo.count() > 0) return;

        List<Conflict> conflicts = new ArrayList<>();
        conflicts.add(conflict("Russia-Ukraine War", "Full-scale Russian invasion of Ukraine. Largest land war in Europe since WW2.",
                "RU", "UA", "Eastern Europe", 48.38, 31.17, "CRITICAL", "ACTIVE_WAR", "INTERSTATE", 500000, 8000000, "2022-02-24"));
        conflicts.add(conflict("Israel-Hamas War", "Israeli military operation in Gaza following October 7 attack.",
                "IL", "PS", "Middle East", 31.35, 34.30, "CRITICAL", "ACTIVE_WAR", "INTERSTATE", 45000, 1900000, "2023-10-07"));
        conflicts.add(conflict("Sudan Civil War", "Armed conflict between SAF and RSF paramilitary forces.",
                "SD", "SD", "East Africa", 15.50, 32.55, "HIGH", "ACTIVE_WAR", "CIVIL", 15000, 9000000, "2023-04-15"));
        conflicts.add(conflict("Myanmar Civil War", "Military junta vs National Unity Government and ethnic armed groups.",
                "MM", "MM", "Southeast Asia", 19.76, 96.07, "HIGH", "ACTIVE_WAR", "CIVIL", 50000, 2600000, "2021-02-01"));
        conflicts.add(conflict("China-Taiwan Strait Tensions", "Increasing military pressure and air defense zone incursions.",
                "CN", "TW", "East Asia", 23.69, 120.96, "HIGH", "TENSION", "TERRITORIAL", 0, 0, "2022-08-01"));
        conflicts.add(conflict("India-Pakistan Kashmir Dispute", "Ongoing territorial dispute over Kashmir region.",
                "IN", "PK", "South Asia", 34.08, 74.79, "MEDIUM", "TENSION", "BORDER", 0, 0, "1947-10-22"));
        conflicts.add(conflict("North Korea Nuclear Crisis", "Nuclear weapons program and ballistic missile testing.",
                "KP", "KR", "East Asia", 38.34, 127.13, "HIGH", "TENSION", "INTERSTATE", 0, 0, "2006-10-09"));
        conflicts.add(conflict("Yemen Civil War", "Houthi rebels vs Saudi-backed government forces.",
                "YE", "YE", "Middle East", 15.55, 48.52, "HIGH", "ACTIVE_WAR", "CIVIL", 377000, 4000000, "2014-09-21"));
        conflicts.add(conflict("Sahel Insurgency", "Jihadist groups across Mali, Burkina Faso, and Niger.",
                "ML", "BF", "West Africa", 14.50, -1.50, "HIGH", "ACTIVE_WAR", "INSURGENCY", 12000, 2500000, "2012-01-17"));
        conflicts.add(conflict("Somalia Al-Shabaab Insurgency", "Al-Shabaab militant group attacks.",
                "SO", "SO", "East Africa", 2.04, 45.31, "MEDIUM", "ACTIVE_WAR", "INSURGENCY", 20000, 3000000, "2006-01-01"));
        conflicts.add(conflict("Syria Post-War Instability", "Ongoing instability and territorial fragmentation.",
                "SY", "SY", "Middle East", 34.80, 38.99, "MEDIUM", "ACTIVE_WAR", "CIVIL", 500000, 6700000, "2011-03-15"));
        conflicts.add(conflict("DR Congo Eastern Conflict", "M23 rebel group and various armed factions.",
                "CD", "CD", "Central Africa", -1.65, 29.22, "HIGH", "ACTIVE_WAR", "CIVIL", 6000, 5800000, "2022-03-01"));
        conflicts.add(conflict("Ethiopia Tigray Aftermath", "Post-ceasefire tensions and Amhara conflict.",
                "ET", "ET", "East Africa", 13.50, 39.47, "MEDIUM", "CEASEFIRE", "CIVIL", 600000, 2000000, "2020-11-04"));
        conflicts.add(conflict("Iran-Israel Shadow War", "Proxy conflicts, cyberattacks, and direct military strikes.",
                "IR", "IL", "Middle East", 32.43, 53.69, "HIGH", "TENSION", "PROXY", 0, 0, "2020-01-01"));
        conflicts.add(conflict("South China Sea Disputes", "Territorial claims involving China, Philippines, Vietnam.",
                "CN", "PH", "Southeast Asia", 12.00, 114.00, "MEDIUM", "TENSION", "TERRITORIAL", 0, 0, "2012-04-01"));

        conflictRepo.saveAll(conflicts);
        log.info("Loaded {} conflict records", conflicts.size());
    }

    private Conflict conflict(String title, String desc, String a, String b, String region,
                               double lat, double lng, String sev, String status, String type,
                               int casualties, int displaced, String startDate) {
        Conflict c = new Conflict();
        c.setTitle(title);
        c.setDescription(desc);
        c.setCountryA(a);
        c.setCountryB(b);
        c.setRegion(region);
        c.setLat(lat);
        c.setLng(lng);
        c.setSeverity(sev);
        c.setStatus(status);
        c.setConflictType(type);
        c.setCasualties(casualties);
        c.setDisplaced(displaced);
        c.setStartedAt(LocalDate.parse(startDate));
        c.setSource("SEED");
        return c;
    }

    private void seedTimeline() {
        if (timelineRepo.count() > 0) return;

        List<TimelineEvent> events = new ArrayList<>();
        events.add(event("Russia launches full invasion of Ukraine", "CONFLICT", "CRITICAL", "RU", 48.38, 31.17, "2022-02-24T05:00:00"));
        events.add(event("Hamas attacks Israel, October 7 massacre", "CONFLICT", "CRITICAL", "IL", 31.35, 34.30, "2023-10-07T06:30:00"));
        events.add(event("North Korea tests Hwasong-18 ICBM", "NUCLEAR", "HIGH", "KP", 40.34, 127.51, "2023-07-12T08:00:00"));
        events.add(event("China conducts largest Taiwan Strait exercises", "MILITARY", "HIGH", "CN", 23.69, 120.96, "2022-08-04T00:00:00"));
        events.add(event("Sudan RSF launches coup attempt", "CONFLICT", "HIGH", "SD", 15.50, 32.55, "2023-04-15T09:00:00"));
        events.add(event("Iran launches drones and missiles at Israel", "CONFLICT", "CRITICAL", "IR", 31.05, 34.85, "2024-04-13T21:00:00"));
        events.add(event("NATO expands: Finland joins alliance", "DIPLOMATIC", "MEDIUM", "FI", 61.92, 25.75, "2023-04-04T12:00:00"));
        events.add(event("Wagner Group rebellion in Russia", "CONFLICT", "HIGH", "RU", 47.24, 39.71, "2023-06-24T03:00:00"));
        events.add(event("Houthi Red Sea shipping attacks begin", "CONFLICT", "HIGH", "YE", 13.00, 43.00, "2023-11-19T14:00:00"));
        events.add(event("Ukraine sinks Russian cruiser Moskva", "MILITARY", "HIGH", "UA", 45.30, 30.50, "2022-04-14T18:00:00"));

        timelineRepo.saveAll(events);
        log.info("Loaded {} timeline events", events.size());
    }

    private TimelineEvent event(String title, String type, String severity, String country,
                                 double lat, double lng, String time) {
        TimelineEvent e = new TimelineEvent();
        e.setTitle(title);
        e.setEventType(type);
        e.setSeverity(severity);
        e.setCountryCode(country);
        e.setLat(lat);
        e.setLng(lng);
        e.setEventTime(LocalDateTime.parse(time));
        e.setSource("SEED");
        return e;
    }
}
