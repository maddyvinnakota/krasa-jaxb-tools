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
package com.sun.tools.xjc.addon.krasa;

import java.math.BigDecimal;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class TypeHelperTest {

    @Test
    public void testIsNumber() throws Exception {
        assertFalse(TypeHelper.isNumber(String.class));
        assertFalse(TypeHelper.isNumber(IllegalStateException.class));
        assertTrue(TypeHelper.isNumber(BigDecimal.class));
    }

    @Test
    public void isFieldTypeNameNumber() {
        assertTrue(TypeHelper.isFieldTypeNameNumber(BigDecimal.class.getSimpleName()));
        assertTrue(TypeHelper.isFieldTypeNameNumber(Long.class.getSimpleName()));
        assertTrue(TypeHelper.isFieldTypeNameNumber(Short.class.getSimpleName()));
        assertTrue(TypeHelper.isFieldTypeNameNumber(Integer.class.getSimpleName()));

        assertFalse(TypeHelper.isFieldTypeNameNumber(String.class.getSimpleName()));
        assertFalse(TypeHelper.isFieldTypeNameNumber(Boolean.class.getSimpleName()));
    }

    @Test
    public void isFieldTypeFullNameNumber() {
        assertTrue(TypeHelper.isFieldTypeFullNameNumber(BigDecimal.class.getCanonicalName()));
        assertTrue(TypeHelper.isFieldTypeFullNameNumber(Long.class.getCanonicalName()));
        assertTrue(TypeHelper.isFieldTypeFullNameNumber(Short.class.getCanonicalName()));
        assertTrue(TypeHelper.isFieldTypeFullNameNumber(Integer.class.getCanonicalName()));

        assertFalse(TypeHelper.isFieldTypeFullNameNumber(String.class.getCanonicalName()));
        assertFalse(TypeHelper.isFieldTypeFullNameNumber(Boolean.class.getCanonicalName()));
    }

}
