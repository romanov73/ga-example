package ru.ulstu.diser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Vacancy implements CompetenceContainer {
    private String name;

    private Set<Competence> competencies = new HashSet<>();

    public Vacancy(String name, String...  competencies) {
        this.name = name;
        this.competencies = Arrays.asList(competencies).stream().map(s -> new Competence(s)).collect(Collectors.toSet());
    }

    public Set<Competence> getCompetencies() {
        return competencies;
    }

    public void setCompetencies(Set<Competence> competencies) {
        this.competencies = competencies;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Vacancy{" +
                "name='" + name + '\'' +
                ", competencies=" + competencies +
                "}\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vacancy vacancy = (Vacancy) o;

        if (!name.equals(vacancy.name)) return false;
        return competencies.equals(vacancy.competencies);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + competencies.hashCode();
        return result;
    }
}
