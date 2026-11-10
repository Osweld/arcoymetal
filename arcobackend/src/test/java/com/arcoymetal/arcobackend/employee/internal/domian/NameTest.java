package com.arcoymetal.arcobackend.employee.internal.domian;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

import org.junit.jupiter.api.Test;

import com.arcoymetal.arcobackend.employee.internal.domain.Name;

public class NameTest {

     @Test
    void shouldCreateNameWhenValuesAreValid() {
        Name name = new Name("Oswald", "Hernández");

        assertThat(name.firstName()).isEqualTo("Oswald");
        assertThat(name.lastName()).isEqualTo("Hernández");
    }

    @Test
    void shouldRemoveLeadingAndTrailingWhitespace() {
        Name name = new Name("  Oswald  ", "  Hernández  ");

        assertThat(name.firstName()).isEqualTo("Oswald");
        assertThat(name.lastName()).isEqualTo("Hernández");
    }

    @Test
    void shouldBuildFullName() {
        Name name = new Name("Oswald", "Hernández");

        assertThat(name.fullName()).isEqualTo("Oswald Hernández");
    }

    @Test
    void shouldAcceptNamesWithAccentsAndSpanishCharacters() {
        Name name = new Name("José", "Muñoz Álvarez");

        assertThat(name.firstName()).isEqualTo("José");
        assertThat(name.lastName()).isEqualTo("Muñoz Álvarez");
    }

    @Test
    void shouldAcceptNamesWithHyphensAndApostrophes() {
        Name name = new Name("Jean-Pierre", "O'Connor");

        assertThat(name.firstName()).isEqualTo("Jean-Pierre");
        assertThat(name.lastName()).isEqualTo("O'Connor");
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> new Name(null, "Hernández"))
            .withMessage("firstName cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsNull() {
        assertThatNullPointerException()
            .isThrownBy(() -> new Name("Oswald", null))
            .withMessage("lastName cannot be null");
    }

    @Test
    void shouldThrowExceptionWhenFirstNameIsBlank() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Name("   ", "Hernández"))
            .withMessage(
                "firstName must be between 1 and 100 characters"
            );
    }

    @Test
    void shouldThrowExceptionWhenLastNameIsBlank() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Name("Oswald", "   "))
            .withMessage(
                "lastName must be between 1 and 100 characters"
            );
    }

    @Test
    void shouldThrowExceptionWhenFirstNameExceedsMaxLength() {
        String firstName = "A".repeat(101);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Name(firstName, "Hernández"))
            .withMessage(
                "firstName must be between 1 and 100 characters"
            );
    }

    @Test
    void shouldThrowExceptionWhenLastNameExceedsMaxLength() {
        String lastName = "B".repeat(101);

        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Name("Oswald", lastName))
            .withMessage(
                "lastName must be between 1 and 100 characters"
            );
    }

    @Test
    void shouldAcceptNamesAtMaxLength() {
        String firstName = "A".repeat(100);
        String lastName = "B".repeat(100);

        Name name = new Name(firstName, lastName);

        assertThat(name.firstName()).hasSize(100);
        assertThat(name.lastName()).hasSize(100);
    }

    @Test
    void shouldThrowExceptionWhenFirstNameContainsInvalidCharacters() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Name("Oswald123", "Hernández"))
            .withMessage("firstName contains invalid characters");
    }

    @Test
    void shouldThrowExceptionWhenLastNameContainsInvalidCharacters() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Name("Oswald", "Hernández@"))
            .withMessage("lastName contains invalid characters");
    }

    @Test
    void shouldBeEqualWhenValuesAreEqual() {
        Name first = new Name("Oswald", "Hernández");
        Name second = new Name("Oswald", "Hernández");

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).hasSameHashCodeAs(second.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenValuesAreDifferent() {
        Name first = new Name("Oswald", "Hernández");
        Name second = new Name("Oswaldo", "Hernández");

        assertThat(first).isNotEqualTo(second);
    }
}
