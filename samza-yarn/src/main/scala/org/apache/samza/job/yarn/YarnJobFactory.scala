/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.samza.job.yarn
import org.apache.samza.job.StreamJobFactory
import org.apache.hadoop.yarn.conf.YarnConfiguration
import org.apache.samza.config.Config

class YarnJobFactory extends StreamJobFactory {
  def getJob(config: Config) = {
    // TODO fix this. needed to support http package locations.
    val hConfig = new YarnConfiguration
    hConfig.set("fs.http.impl", "samza.util.hadoop.HttpFileSystem")

    new YarnJob(config, hConfig)
  }
}
