package node.builder

import node.builder.metrics.MetricsTestHelper
import org.junit.After
import org.junit.Before
import org.junit.Test



/**
 * Copyright 2013 AirGap, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class MetricServiceTests {
    def metricService

    @Before
    public void setUp(){
        MetricsTestHelper.resetMetrics()
        MetricsTestHelper.seedTestData()
        metricService = new MetricService()
    }

    @Test
    void shouldGenerateMetricsPerProject(){
        def metrics = metricService.metricsForProject("test")
        assert metrics.averageDuration == "00:00:00.151"
        assert metrics.workflows.size() == 2
    }

    @Test
    void shouldGenerateMetricsForTheApplication(){
        def metrics = metricService.metricsForApplication()
        assert metrics.upTime.toString().matches(/.*\d\.\d{3}\sseconds/)
    }

    @After
    public void tearDown(){
        MetricsTestHelper.resetMetrics()
    }
}
