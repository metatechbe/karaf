/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.features.command;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.command.completers.AvailableFeatureCompleter;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Completion;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Command(scope = "feature", name = "stop", description = "Start features with the specified name and version.")
@Service
public class StopFeaturesCommand extends FeaturesCommandSupport {

    @Argument(index = 0, name = "feature", description = "The name and version of the features to install. A feature id looks like name/version. The version is optional.", required = true, multiValued = true)
    @Completion(AvailableFeatureCompleter.class)
    List<String> features;

    @Option(name = "-v", aliases = "--verbose", description = "Explain what is being done", required = false, multiValued = false)
    boolean verbose;

    @Option(name = "-t", aliases = "--simulate", description = "Perform a simulation only", required = false, multiValued = false)
    boolean simulate;

    @Option(name = "-g", aliases = "--region", description = "Region to install to")
    String region;

    protected void doExecute(FeaturesService admin) throws Exception {
        EnumSet<FeaturesService.Option> options = EnumSet.noneOf(FeaturesService.Option.class);
        if (simulate) {
            options.add(FeaturesService.Option.Simulate);
        }
        if (verbose) {
            options.add(FeaturesService.Option.Verbose);
        }
        Map<String, Map<String, FeaturesService.RequestedState>> stateChanges = new HashMap<>();
        if (region == null) {
            region = FeaturesService.ROOT_REGION;
        }
        Map<String, FeaturesService.RequestedState> regionChanges = new HashMap<>();
        for (String feature : features) {
            regionChanges.put(feature, FeaturesService.RequestedState.Resolved);
        }
        stateChanges.put(region, regionChanges);
        admin.updateFeaturesState(stateChanges, options);
    }
}
