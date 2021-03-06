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

package org.apache.samza.job.local
import org.apache.samza.config.TaskConfig._
import org.apache.samza.config.Config
import org.apache.samza.config.SystemConfig._
import org.apache.samza.config.StreamConfig._
import org.apache.samza.config.ShellCommandConfig._
import org.apache.samza.job.CommandBuilder
import org.apache.samza.job.StreamJob
import org.apache.samza.job.StreamJobFactory
import scala.collection.JavaConversions._
import org.apache.samza.Partition
import grizzled.slf4j.Logging
import org.apache.samza.SamzaException
import org.apache.samza.container.SamzaContainer
import org.apache.samza.util.Util
import org.apache.samza.job.ShellCommandBuilder

class LocalJobFactory extends StreamJobFactory with Logging {
  def getJob(config: Config): StreamJob = {
    val taskName = "local-task"
    val partitions = Util.getMaxInputStreamPartitions(config)

    info("got partitions for job %s" format partitions)

    if (partitions.size <= 0) {
      throw new SamzaException("No partitions were detected for your input streams. It's likely that the system(s) specified don't know about the input streams: %s" format config.getInputStreams)
    }

    config.getCommandClass match {
      case Some(cmdBuilderClassName) => {
        // A command class was specified, so we need to use a process job to
        // execute the command in its own process.
        val cmdBuilder = Class.forName(cmdBuilderClassName).newInstance.asInstanceOf[CommandBuilder]

        cmdBuilder
          .setConfig(config)
          .setName(taskName)
          .setPartitions(partitions)

        val processBuilder = new ProcessBuilder(cmdBuilder.buildCommand.split(" ").toList)

        processBuilder
          .environment
          .putAll(cmdBuilder.buildEnvironment)

        new ProcessJob(processBuilder)
      }
      case _ => {
        info("No config specified for %s. Defaulting to ThreadJob, which is only meant for debugging." format COMMAND_BUILDER)

        // Give developers a nice friendly warning if they've specified task.opts and are using a threaded job.
        config.getTaskOpts match {
          case Some(taskOpts) => warn("%s was specified in config, but is not being used because job is being executed with ThreadJob. You probably want to run %s=%s." format (TASK_JVM_OPTS, COMMAND_BUILDER, classOf[ShellCommandBuilder].getName))
          case _ => None
        }

        // No command class was specified, so execute the job in this process
        // using a threaded job.
        new ThreadJob(SamzaContainer(taskName, partitions, config))
      }
    }
  }
}
