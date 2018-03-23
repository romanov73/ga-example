package ru.ulstu.diser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    public static final String EMPLOYEE_TEMPLATE = "Сорудник %s";

    // сколько сотрудников в компании?
    public static final int MAX_EMPLOYEES = 10000;

    // сколько вакансий нужно включить в проект?
    public static final int MAX_VACANCIES = 10;

    // сколько максимально компетенций может быть у сотрудника
    public static final int MAX_EMPLOYEE_COMPETENCIES = 10;

    //GA parameters
    // ограничение на количество итераци алгоритма (если решения долго не удается найти)
    private static final int MAX_EPOCH = 1000000;
    // ораничение на значение фитнесс-функции для отсеивания между поколениями
    private static final double MAX_ELIT_TRESHOLD = 0.9;

    // вероятность кроссовера (как количество вакансий подлежащих кроссоверу)
    private static final int CROSSOVER_SIZE = MAX_VACANCIES / 3;
    // пороговое значение для фитнес-функции, при котором решение считается найденным
    private static final double MAX_FITNESS = 0.9;

    private List<Vacancy> vacancies = new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List<Competence> competencies = new ArrayList<>();
    private List<Gen> currentGenotype = new ArrayList<>();

    public static void main(String[] args) {
        Main m = new Main();

        // инициируетм список компетенций
        m.generateCompetence();

        // генерируем список вакансий на проект
        // из статичного списка  смещаемся на offset позиций от начала списка, выбираем MAX_VACANCIES
        m.generateVacancy(MAX_VACANCIES,  0);

        // инициируем список персонала (с компетенциями)
        for (int i = 0; i < MAX_EMPLOYEES; i++) {
            m.employees.add(m.generateEmployee(i));
        }

        int currentEpoch = 0;

        // пока не достигнуто ограничение на количество итераций цикла или вакансии не заполнены
        while (currentEpoch < MAX_EPOCH && !m.isVacanciesFull()) {
            System.out.println("Epoch № " + currentEpoch + " starts");
            //фильтруем элитарных особей
            m.removeNonElit();
            // дополняем популяцию
            m.generateGenotype();
            // кроссовер
            m.crossover();
            // вычисляем фитнес-функцию
            m.calcFitness();
            currentEpoch++;
        }

        if (m.isVacanciesFull()) {
            System.out.println("Decision: " + m.currentGenotype);
        } else {
            // решение могло быть не найдено
            System.out.println("Decision not found ");
            System.out.println("Decision: " + m.currentGenotype);
        }
    }

    private void crossover() {
        for (int i = 0; i < CROSSOVER_SIZE; i++) {
            Collections.shuffle(currentGenotype);
            Employee swapEmployee = currentGenotype.get(0).getEmployee();
            currentGenotype.get(0).setEmployee(currentGenotype.get(1).getEmployee());
            currentGenotype.get(1).setEmployee(swapEmployee);
        }
    }

    private void removeNonElit() {
        currentGenotype = currentGenotype.stream()
                .sorted((g1, g2) -> Double.compare(g2.getFitness(), g2.getFitness()))
                .filter(gen -> gen.getFitness() > MAX_ELIT_TRESHOLD)
                .collect(Collectors.toList());
    }

    private void calcFitness() {
        for (Gen gen : currentGenotype) {
            int competencesMatchCount = 0;
            for (Competence vacancyCompetency : gen.getVacancy().getCompetencies()) {
                if (gen.getEmployee().getCompetencies().contains(vacancyCompetency)) {
                    competencesMatchCount++;
                }
            }
            gen.setFitness(competencesMatchCount / gen.getVacancy().getCompetencies().size());
        }
    }

    private boolean isVacanciesFull() {
        Set<Vacancy> vacanciesToAssign = getVacanciesToAssign();
        System.out.println("Need to fill " + vacanciesToAssign.size() + " vacancies");
        return vacanciesToAssign.isEmpty();
    }

    private void generateGenotype() {
        List<Vacancy> vacanciesToAssign = getVacanciesToAssign().stream().collect(Collectors.toList());

        vacanciesToAssign = removeEquals(vacanciesToAssign);

        int i = 0;
        for (Employee employee : getRandomEmployees(employees, vacanciesToAssign.size())) {
            currentGenotype.add(new Gen(employee, vacanciesToAssign.get(i++)));
        }
    }

    private List<Vacancy> removeEquals(List<Vacancy> vacanciesToAssign) {
        for (int j = 0; j < currentGenotype.size(); j++) {
            vacanciesToAssign.remove(currentGenotype.get(j).getVacancy());
        }
        return vacanciesToAssign;
    }

    private Set<Vacancy> getVacanciesToAssign() {
        return vacancies.stream().filter(vacancy -> {
            for (Gen gen : currentGenotype) {
                if (gen.getVacancy().equals(vacancy) && gen.getFitness() >= MAX_FITNESS) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toSet());
    }

    private Employee generateEmployee(int i) {
        while (i >= Employee.names.size()) {
            i = i - 100;
        }
        Employee e = new Employee(Employee.names.get(i));
        setCompetences(e, MAX_EMPLOYEE_COMPETENCIES);
        return e;
    }

    private void generateVacancy(int max, int offset) {
        vacancies.add(new Vacancy("1 Руководитель проекта",
                "1С:Руководитель проекта",
                "1С:Специалист-консультант по внедрению подсистем управленческого учета в программе 1С:ERP Управление предприятием 2"));
        vacancies.add(new Vacancy("2 Консультант",
                "1С:Специалист-консультант по внедрению прикладного решения 1С:Зарплата и управление персоналом 8",
                "1С:Профессионал '1С:Зарплата и управление персоналом 8'"));
        vacancies.add(new Vacancy("3 Консультант", "1С:Специалист-консультант по внедрению прикладного решения 1С:Бухгалтерия 8",
                "1С:Профессионал '1С:Бухгалтерия 8'"));
        vacancies.add(new Vacancy("4 Консультант",
                "1С:Специалист-консультант по внедрению прикладного решения 1С:Управление торговлей 8"
        ));
        vacancies.add(new Vacancy("5 Преподаватель",
                "1С:Преподаватель пользовательские режимы 'Зарплата и управление кадрами'",
                "1С:Профессионал '1С:Зарплата и управление персоналом 8'"
        ));
        vacancies.add(new Vacancy("6 Преподаватель",
                "1С:Преподаватель пользоватеьские режимы Бухгалтерия предприятия"));

        vacancies.add(new Vacancy("7 Программист",
                "1С:Специалист по конфигурированию платформы 1С:Предприятие 8",
                "1С:Специалист-консультант по внедрению подсистем управленческого учета в программе 1С:ERP Управление предприятием 2"
                ));

        vacancies.add(new Vacancy("8 Программист",
                "1С:Специалист по конфигурированию платформы 1С:Предприятие 8",
                "1С:Специалист-консультант по внедрению подсистем Управление производством и организация ремонтов в программе 1С:ERP Управление предприятием 2"
        ));

        vacancies.add(new Vacancy("9 Программист",
                "1С:Специалист по конфигурированию подсистем расчета зарплаты и управления персоналом в прикладных решениях 1С:Предприятие 8"
        ));

        vacancies.add(new Vacancy("10 Программист",
                "1С:Специалист по конфигурированию платформы 1С:Предприятие 8",
                "1С:Эксперт по технологическим вопросам"
        ));
        Collections.shuffle(vacancies);
        // отбрасываем лишние вакансии, если превыашет ограничение по максимуму
        vacancies = vacancies.stream().skip(offset).limit(max).collect(Collectors.toList());
    }

    private void generateCompetence() {
        competencies.add(new Competence("1С:Преподаватель пользовательские режимы 'Зарплата и управление кадрами'"));
        competencies.add(new Competence("1С:Преподаватель пользоватеьские режимы Бухгалтерия предприятия"));
        competencies.add(new Competence("1С:Преподаватель пользоватеьские режимы Управление торговлей"));
        competencies.add(new Competence("1С:Профессионал '1С:ERP Управление предприятием 2'"));
        competencies.add(new Competence("1С:Профессионал '1С:Бухгалтерия 8'"));
        competencies.add(new Competence("1С:Профессионал '1С:Документооборот 8'"));
        competencies.add(new Competence("1С:Профессионал '1С:Зарплата и управление персоналом 8'"));
        competencies.add(new Competence("1С:Профессионал '1С:Управление нашей фирмой 8'"));
        competencies.add(new Competence("1С:Профессионал '1С:Управление производственным предприятием 8'"));
        competencies.add(new Competence("1С:Профессионал '1С:Управление торговлей 8'"));
        competencies.add(new Competence("1С:Профессионал на знание основных механизмов и возможностей платформы 1С:Предприятия 8"));
        competencies.add(new Competence("1С:Профессионал по технологическим вопросам"));
        competencies.add(new Competence("1С:Руководитель проекта"));
        competencies.add(new Competence("1С:Специалист по конфигурированию в программе  1С:Зарплата и кадры государственного учреждения 8 "));
        competencies.add(new Competence("1С:Специалист по конфигурированию и внедрению бухгалтерской подсистемы в прикладных решениях 1С:Предприятие 8"));
        competencies.add(new Competence("1С:Специалист по конфигурированию платформы 1С:Предприятие 8"));
        competencies.add(new Competence("1С:Специалист по конфигурированию подсистем расчета зарплаты и управления персоналом в прикладных решениях 1С:Предприятие 8"));
        competencies.add(new Competence("1С:Специалист по конфигурированию торговых решений в системе программ 1С:Предприятие 8"));
        competencies.add(new Competence("1С:Специалист по методологии подсистемы Управление производством в прикладных решениях на платформе 1С:Предприятие 8"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению подсистем Управление производством и организация ремонтов в программе 1С:ERP Управление предприятием 2"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению подсистем управленческого учета в программе 1С:ERP Управление предприятием 2"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению подсистемы Бюджетирование в 1С:ERP Управление предприятием 2"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению прикладного решения 1C:Зарплата и кадры государственного учреждения 8"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению прикладного решения 1С:Бухгалтерия 8"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению прикладного решения 1С:Зарплата и управление персоналом 8"));
        competencies.add(new Competence("1С:Специалист-консультант по внедрению прикладного решения 1С:Управление торговлей 8"));
        competencies.add(new Competence("1С:Специалист-консультант по настройке и администрированию 1С:Документооборота"));
        competencies.add(new Competence("1С:Специалист-консультант по прикладному решению 1С:Бухгалтерия государственного учреждения 8"));
        competencies.add(new Competence("1С:Эксперт по технологическим вопросам"));
    }

    private List<Employee> getRandomEmployees(List<Employee> employees, int max) {
        Collections.shuffle(employees);
        return employees.stream().limit(max).collect(Collectors.toList());
    }

    private void setCompetences(CompetenceContainer container, long max) {
        Collections.shuffle(competencies);
        for (int i = 0; i < max; i++) {
            container.getCompetencies().add(competencies.get(i));
        }
    }
}

