package ru.ulstu.diser;

public class Gen {
    private Employee employee;
    private Vacancy vacancy;
    private double fitness = 0;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Vacancy getVacancy() {
        return vacancy;
    }

    public void setVacancy(Vacancy vacancy) {
        this.vacancy = vacancy;
    }

    public Gen(Employee employee, Vacancy vacancy) {

        this.employee = employee;
        this.vacancy = vacancy;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    @Override
    public String toString() {
        return "Gen{" +
                "employee=" + employee +
                ", vacancy=" + vacancy +
                ", fitness=" + fitness +
                '}';
    }
}
