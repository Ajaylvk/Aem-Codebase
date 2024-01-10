package com.softwareag.core.services.parsys.impl;

import com.softwareag.core.services.parsys.ParsysItemWrapper;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParsysItemWrapperServiceImplTest {

    @Mock
    private Resource resource;
    @Mock
    private ParsysItemWrapper parsysItemWrapper;

    @InjectMocks
    private ParsysItemWrapperServiceImpl underTest;

    /**
     * Asserts, if null returned for a .
     */
    @Test
    void testFindParsysItemWrapperWithoutWrapper() {
        assertNull(underTest.findParsysItemWrapper(resource, "testPath", "testType"));
    }

    @Test
    void testFindParsysItemWrapperWithoutMatchingWrapper() {
        underTest.bindParsysItemWrapper(parsysItemWrapper, Collections.emptyMap());
        when(parsysItemWrapper.matches(any(Resource.class), any(String.class), any(String.class))).thenReturn(false);

        assertNull(underTest.findParsysItemWrapper(resource, "testPath", "testType"));
    }

    @Test
    void testFindParsysItemWrapperWithWrapper() {
        underTest.bindParsysItemWrapper(parsysItemWrapper, Collections.emptyMap());
        when(parsysItemWrapper.matches(any(Resource.class), any(String.class), any(String.class))).thenReturn(true);

        assertEquals(parsysItemWrapper, underTest.findParsysItemWrapper(resource, "testPath", "testType"));
    }

}
