/**
 * Copyright 2015-2016 Red Hat, Inc, and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wildfly.swarm.cdi;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.swarm.Swarm;
import org.wildfly.swarm.arquillian.CreateSwarm;
import org.wildfly.swarm.spi.api.JARArchive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Martin Kouba
 */
@RunWith(Arquillian.class)
@Ignore
public class ConfigValueProducerTest {

    @Deployment
    public static Archive<?> createDeployment() {
        JARArchive archive = ShrinkWrap.create(JARArchive.class)
                .add(new FileAsset(new File("src/test/resources/beans.xml")),
                        "META-INF/beans.xml")
                .add(new FileAsset(
                                new File("src/test/resources/project-stages.yml")),
                        "project-stages.yml")
                .addClass(ConfigAwareBean.class);
        return archive;
    }

    @CreateSwarm
    public static Swarm newContainer() throws Exception {
        return new Swarm()
                .withStageConfig(ConfigValueProducerTest.class.getClassLoader()
                        .getResource("project-stages.yml"))
                //.fraction(LoggingFraction.createDebugLoggingFraction())
                .fraction(new CDIFraction());
    }

    @Test
    public void testInjection(ConfigAwareBean configAwareBean) {
        assertNotNull(configAwareBean);

        assertEquals(Integer.valueOf(10), configAwareBean.getPortOffset());
        assertEquals("DEBUG", configAwareBean.getLogLevel());
        assertEquals(Integer.valueOf(10), configAwareBean
                .getPortOffsetResolver().as(Integer.class).getValue());
    }

}
