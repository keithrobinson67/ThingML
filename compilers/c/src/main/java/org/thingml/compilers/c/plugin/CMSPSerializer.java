/**
 * Copyright (C) 2014 SINTEF <franck.fleurey@sintef.no>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingml.compilers.c.plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.sintef.thingml.Configuration;
import org.sintef.thingml.ExternalConnector;
import org.sintef.thingml.Message;
import org.sintef.thingml.Parameter;
import org.sintef.thingml.Port;
import org.sintef.thingml.Thing;
import org.thingml.compilers.c.CCompilerContext;
import org.thingml.compilers.c.CMessageSerializer;

/**
 *
 * @author sintef
 */
public class CMSPSerializer extends CMessageSerializer {

    public CMSPSerializer(CCompilerContext ctx, Configuration cfg) {
        super(ctx, cfg);
    }

    @Override
    public int generateMessageSerialzer(ExternalConnector eco, Message m, StringBuilder builder, String BufferName, List<Parameter> IgnoreList) {
        builder.append("byte " + BufferName + "[" + ctx.getMessageSerializationSize(m)+ "];\n");

        int HandlerCode = ctx.getHandlerCode(cfg, m);

        builder.append(BufferName + "[0] = '<';\n");
        builder.append(BufferName + "[1] = " + (ctx.getMessageSerializationSize(m) - 4) + ";\n");
        builder.append(BufferName + "[2] = " + HandlerCode + " & 0xFF;\n\n");

        int j = 3;

        for (Parameter pt : m.getParameters()) {
            builder.append("\n// parameter " + pt.getName() + "\n");
            int i = ctx.getCByteSize(pt.getType(), 0);
            String v = pt.getName();
            if (ctx.isPointer(pt.getType())) {
                // This should not happen and should be checked before.
                throw new Error("ERROR: Attempting to deserialize a pointer (for message " + m.getName() + "). This is not allowed.");
            } else {
                if(!ctx.containsParam(IgnoreList, pt)) {
                    builder.append("union u_" + v + "_t {\n");
                    builder.append(ctx.getCType(pt.getType()) + " p;\n");
                    builder.append("byte bytebuffer[" + ctx.getCByteSize(pt.getType(), 0) + "];\n");
                    builder.append("} u_" + v + ";\n");
                    builder.append("u_" + v + ".p = " + v + ";\n");

                    while (i > 0) {
                        i = i - 1;
                        builder.append(BufferName + "[" + j + "] = (u_" + v + ".bytebuffer[" + i + "] & 0xFF);\n");
                        j++;
                    }
                }
            }
        }
        
        builder.append("byte crc = 0;\n");
        for(int k = 1; k < (ctx.getMessageSerializationSize(m) - 1); k++) {
            builder.append("crc ^= " + BufferName + "[" + k + "];\n");
        }
        builder.append(BufferName + "[" + j + "] = crc;\n");
        j++;
        return j;
    }

    @Override
    public void generateMessageParser(ExternalConnector eco, StringBuilder builder) {
        builder.append("void " + eco.getProtocol().getName() + "_parser(byte * msg, int size, int listener_id) {\n");
        builder.append("    byte msg_buf[size-1];\n");
        builder.append("    msg_buf[0] = 1;\n");
        builder.append("    uint16_t i;\n");
        builder.append("    for(i = 1; i < (size-1); i++) {\n");
        builder.append("        msg_buf[i] = msg[i];\n");
        builder.append("    }\n");
        builder.append("    externalMessageEnqueue((uint8_t *) msg_buf, size, listener_id);\n");
        builder.append("}\n");
    }
    
}
