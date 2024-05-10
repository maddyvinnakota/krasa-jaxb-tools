/*
 * Copyright 2024 Francesco Illuminati <fillumina@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sun.tools.xjc.addon.krasa.validations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsOptionsTest {

    @Test
    public void shouldGetDafaults() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder().build();

        assertTrue(options.isSinglePattern());
        assertFalse(options.isJsr349());
        assertFalse(options.isVerbose());
        assertFalse(options.isJpaAnnotations());
        assertFalse(options.isGenerateStringListAnnotations());
        assertEquals(JaxbValidationsAnnotation.JAVAX, options.getAnnotationFactory());
    }

    @Test
    public void shouldSetTargetNamespace() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .targetNamespace("something.there")
                .build();

        assertEquals("something.there", options.getTargetNamespace());
    }

    @Test
    public void shouldSetSinglePatternTrue() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .singlePattern(true)
                .build();

        assertTrue(options.isSinglePattern());
    }

    @Test
    public void shouldSetSinglePatternFalse() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .singlePattern(false)
                .build();

        assertFalse(options.isSinglePattern());
    }

    @Test
    public void shouldSetJsr349True() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .jsr349(true)
                .build();

        assertTrue(options.isJsr349());
    }

    @Test
    public void shouldSetJsr349False() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .jsr349(false)
                .build();

        assertFalse(options.isJsr349());
    }

    @Test
    public void shouldSetVerboseTrue() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .verbose(true)
                .build();

        assertTrue(options.isVerbose());
    }

    @Test
    public void shouldSetVerboseFalse() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .verbose(false)
                .build();

        assertFalse(options.isVerbose());
    }

    @Test
    public void shouldSetJpaAnnotationsTrue() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .jpaAnnotations(true)
                .build();

        assertTrue(options.isJpaAnnotations());
    }

    @Test
    public void shouldSetJpaAnnotationsFalse() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .jpaAnnotations(false)
                .build();

        assertFalse(options.isJpaAnnotations());
    }

    @Test
    public void shouldSetGenerateStringListAnnotationsTrue() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .generateStringListAnnotations(true)
                .build();

        assertTrue(options.isGenerateStringListAnnotations());
    }

    @Test
    public void shouldSetGenerateStringListAnnotationsFalse() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .generateStringListAnnotations(false)
                .build();

        assertFalse(options.isGenerateStringListAnnotations());
    }

    @Test
    public void shouldSetAnnotationFactoryTrue() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .annotationFactory(JaxbValidationsAnnotation.JAVAX)
                .build();

        assertEquals(JaxbValidationsAnnotation.JAVAX, options.getAnnotationFactory());
    }

    @Test
    public void shouldSetAnnotationFactoryFalse() {
        JaxbValidationsOptions options = JaxbValidationsOptions.builder()
                .annotationFactory(JaxbValidationsAnnotation.JAKARTA)
                .build();

        assertEquals(JaxbValidationsAnnotation.JAKARTA, options.getAnnotationFactory());
    }

}
