package com.softwareag.core.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class FunctionalUtilTest {

    @Test
    void asStream_createsStream_ifIteratorIsGiven() {
        final List<String> list = Arrays.asList("a", "b");
        assertThat(FunctionalUtil.asStream(list.iterator()).count()).isEqualTo(2);
    }

    @Test
    void asStream_createsStream_ifIterableIsGiven() {
        final List<String> list = Arrays.asList("a", "b");
        assertThat(FunctionalUtil.asStream(list).count()).isEqualTo(2);
    }

}
