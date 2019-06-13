/**
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
 *
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 */
package org.thingml.compilers.java;

import org.thingml.compilers.Context;
import org.thingml.compilers.ThingMLCompiler;
import org.thingml.xtext.thingML.Configuration;

/**
 * Created by ffl on 25.11.14.
 */
public class GraalCompiler extends JavaCompiler {

    public GraalCompiler() {
        super(new JavaThingActionCompiler(), new JavaThingApiCompiler(), new JavaCfgMainGenerator(),
                new GraalCfgBuildCompiler(), new JavaThingImplCompiler());
    }

    @Override
    public ThingMLCompiler clone() {
        return new GraalCompiler();
    }

    @Override
    public String getID() {
        return "graal";
    }

    @Override
    public String getName() {
        return "GraalVM for Java";
    }

    public String getDescription() {
        return "Generates native apps from plain Java code.";
    }

}
