package net.thucydides.core.matchers;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class WhenMatchingBeanUniqueness {

    public class Person {
        private final String firstName;
        private final String lastName;

        Person(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    Person billoddie = new Person("Bill", "Oddie");
    Person billkidd = new Person("Bill", "Kidd");

    @Test
    public void should_detect_unique_field_values() {
        
        List<Person> people = Arrays.asList(billkidd,billoddie);

        BeanCollectionMatcher matcher = new BeanUniquenessMatcher("lastName");

        assertThat(matcher.matches(people)).isTrue();
    }

    @Test
    public void should_detect_duplicated_field_values() {

        List<Person> people = Arrays.asList(billoddie,billoddie);

        BeanCollectionMatcher matcher = new BeanUniquenessMatcher("lastName");

        assertThat(matcher.matches(people)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_check_for_valid_field_name() {

        List<Person> people = Arrays.asList(billkidd,billoddie);

        BeanCollectionMatcher matcher = new BeanUniquenessMatcher("doesNotExist");

        assertThat(matcher.matches(people)).isTrue();
    }
}
