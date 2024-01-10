package com.softwareag.core.models;

import com.softwareag.core.services.language.LanguageService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
class DateTimeModelTest {

    private static final String INPUT_FIELD = "input";
    private static final String FORMAT_FIELD = "format";

    private static final String VALID_DATE_INPUT = "2020-10-13";
    private static final String VALID_DATE_FORMAT = "MMM dd, yyyy";
    private static final String VALID_DATE_OUTPUT = "Oct 13, 2020";

    private static final String VALID_DATETIME_INPUT = "2020-01-20T11:00:00";
    private static final String VALID_DATETIME_FORMAT = "MMM dd, yyyy h:mm a";
    private static final String VALID_DATETIME_OUTPUT = "Jan 20, 2020 11:00 AM";

    private static final String INVALID_FORMAT = "invalid-format";
    private static final String INVALID_DATE = "invalid-date";
    private static final String INVALID_DATETIME = "invalid-datetime";

    private final AemContext context = new AemContext();

    @Mock
    private LanguageService languageService;

    @BeforeEach
    public void setUp() {
        context.registerService(languageService);
        context.addModelsForClasses("com.softwareag.core.models.DateTimeModel");
    }

    @Test
    void testGetDateOutput_ValidInputAndFormat() {
        context.request().setAttribute(INPUT_FIELD, VALID_DATE_INPUT);
        context.request().setAttribute(FORMAT_FIELD, VALID_DATE_FORMAT);

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals(VALID_DATE_OUTPUT, dateTimeModel.getOutput());
    }

    @Test
    void testGetDateOutput_ValidInputAndNoFormat() {
        context.request().setAttribute(INPUT_FIELD, VALID_DATE_INPUT);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals(VALID_DATE_INPUT, dateTimeModel.getOutput());
    }

    @Test
    void testGetDateOutput_ValidInputAndInvalidFormat() {
        context.request().setAttribute(INPUT_FIELD, VALID_DATE_INPUT);
        context.request().setAttribute(FORMAT_FIELD, VALID_DATETIME_FORMAT);

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateOutput_InvalidInputAndValidFormat() {
        context.request().setAttribute(INPUT_FIELD, INVALID_DATE);
        context.request().setAttribute(FORMAT_FIELD, VALID_DATE_FORMAT);

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateOutput_InvalidInputAndNoFormat() {
        context.request().setAttribute(INPUT_FIELD, INVALID_DATE);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateOutput_InvalidInputAndFormat() {
        context.request().setAttribute(INPUT_FIELD, INVALID_DATE);
        context.request().setAttribute(FORMAT_FIELD, INVALID_FORMAT);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateTimeOutput_ValidInputAndFormat() {
        context.request().setAttribute(INPUT_FIELD, VALID_DATETIME_INPUT);
        context.request().setAttribute(FORMAT_FIELD, VALID_DATETIME_FORMAT);

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals(VALID_DATETIME_OUTPUT, dateTimeModel.getOutput());
    }

    @Test
    void testGetDateTimeOutput_ValidInputAndNoFormat() {
        context.request().setAttribute(INPUT_FIELD, VALID_DATETIME_INPUT);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertEquals(VALID_DATETIME_INPUT, dateTimeModel.getOutput());
    }

    @Test
    void testGetDateTimeOutput_ValidInputAndInvalidFormat() {
        context.request().setAttribute(INPUT_FIELD, VALID_DATETIME_INPUT);
        context.request().setAttribute(FORMAT_FIELD, INVALID_FORMAT);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateTimeOutput_InvalidInputAndValidFormat() {
        context.request().setAttribute(INPUT_FIELD, INVALID_DATETIME);
        context.request().setAttribute(FORMAT_FIELD, VALID_DATETIME_FORMAT);

        when(languageService.getLocale(context.currentResource(), Locale.US)).thenReturn(Locale.US);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateTimeOutput_InvalidInputAndNoFormat() {
        context.request().setAttribute(INPUT_FIELD, INVALID_DATETIME);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetDateTimeOutput_InvalidInputAndFormat() {
        context.request().setAttribute(INPUT_FIELD, INVALID_DATETIME);
        context.request().setAttribute(FORMAT_FIELD, INVALID_FORMAT);

        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

    @Test
    void testGetOutput_EmptyInput() {
        context.request().setAttribute(INPUT_FIELD, StringUtils.EMPTY);
        DateTimeModel dateTimeModel = Objects.requireNonNull(context.request()).adaptTo(DateTimeModel.class);

        assertNotNull(dateTimeModel);
        assertNull(dateTimeModel.getOutput());
    }

}
