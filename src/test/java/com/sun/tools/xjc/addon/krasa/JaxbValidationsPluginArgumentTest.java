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

import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Francesco Illuminati <fillumina@gmail.com>
 */
public class JaxbValidationsPluginArgumentTest {
    private JaxbValidationsPlugin plugin;
    private Options opt;

    public JaxbValidationsPluginArgumentTest() {
        plugin = new JaxbValidationsPlugin();
        opt = new Options();
    }

    @Test
    public void shouldSetVersbose() throws BadCommandLineException, IOException {
        assertFalse(plugin.verbose);
        setArguments(Argument.verbose.withValue("true"));
        assertTrue(plugin.verbose);
    }

    @Test
    public void shouldSetVersboseWithCapital() throws BadCommandLineException, IOException {
        assertFalse(plugin.verbose);
        setArguments(Argument.verbose.withValue("TRue"));
        assertTrue(plugin.verbose);
    }

    @Test(expected = BadCommandLineException.class)
    public void shouldNotSetErroneousVerbose() throws BadCommandLineException, IOException {
        setArguments(Argument.verbose.withValue("ERROR"));
    }

    @Test
    public void shouldSetTargetNamespace() throws BadCommandLineException, IOException {
        assertNull(plugin.targetNamespace);
        String ns = "target.namespace";
        setArguments(Argument.targetNamespace.withValue(ns));
        assertEquals(plugin.targetNamespace, ns);
    }

    private void setArguments(String... args) throws BadCommandLineException, IOException {
        for (int i=0; i<args.length; i++) {
            plugin.parseArgument(opt , args, i);
        }
    }

}
