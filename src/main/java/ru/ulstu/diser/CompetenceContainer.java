package ru.ulstu.diser;

import java.util.Set;

public interface CompetenceContainer {
    Set<Competence> getCompetencies();

    void setCompetencies(Set<Competence> competences);
}
