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
package org.thingml.compilers.c.posix;

import org.thingml.compilers.Context;
import org.thingml.compilers.c.CCompilerContext;
import org.thingml.compilers.c.CThingActionCompiler;
import org.thingml.xtext.thingML.ADCCommand;
import org.thingml.xtext.thingML.ErrorAction;
import org.thingml.xtext.thingML.Expression;
import org.thingml.xtext.thingML.GPIOCommand;
import org.thingml.xtext.thingML.MCUExpression;
import org.thingml.xtext.thingML.PWMCommand;
import org.thingml.xtext.thingML.PrintAction;
import org.thingml.xtext.thingML.Type;
import org.thingml.xtext.validation.TypeChecker;

/**
 * Created by ffl on 11.06.15.
 */
public class CThingActionCompilerPosix extends CThingActionCompiler {
	@Override
	public void generate(ErrorAction action, StringBuilder builder, Context ctx) {

		for (Expression msg : action.getMsg()) {
		Type actual = TypeChecker.computeTypeOf(msg).getType();
		final StringBuilder b = new StringBuilder();
		generate(msg, b, ctx);
		if (actual != null) {
			if (actual.getName().equals("Integer") || actual.getName().equals("Byte")) {
				builder.append("fprintf(stderr, \"%i\"," + b.toString() + ");\n");
			} else if (actual.getName().equals("Character")) {
				builder.append("fprintf(stderr, \"%c\"," + b.toString() + ");\n");
			} else if (actual.getName().equals("String")) {
				builder.append("fprintf(stderr, " + b.toString() + ");\n");
			} else if (actual.getName().equals("Real")) {
				builder.append("fprintf(stderr, \"%f\"," + b.toString() + ");\n");
			} else if (actual.getName().equals("Boolean")) {
				builder.append("fprintf(stderr, \"%s\", (" + b.toString() + ") ? \"true\" : \"false\");\n");
			} else {
				builder.append("//Type " + actual.getName() + " is not handled in print action\n");
			}
		} else {
			builder.append("//Error in type detection\n");
		}
		}
		if (action.isLine()) builder.append("fprintf(stderr, \"\\n\");\n");
	}

	@Override
	public void generate(PrintAction action, StringBuilder builder, Context ctx) {
		for (Expression msg : action.getMsg()) {
			Type actual = TypeChecker.computeTypeOf(msg).getType();
			final StringBuilder b = new StringBuilder();
			generate(msg, b, ctx);
			if (actual != null) {
				if (actual.getName().equals("Integer") || actual.getName().equals("Byte")) {
					builder.append("fprintf(stdout, \"%i\"," + b.toString() + ");\n");
				} else if (actual.getName().equals("Character")) {
					builder.append("fprintf(stdout, \"%c\"," + b.toString() + ");\n");
				} else if (actual.getName().equals("String")) {
					builder.append("fprintf(stdout, " + b.toString() + ");\n");
				} else if (actual.getName().equals("Real")) {
					builder.append("fprintf(stdout, \"%f\"," + b.toString() + ");\n");
				} else if (actual.getName().equals("Boolean")) {
					builder.append("fprintf(stdout, \"%s\", (" + b.toString() + ") ? \"true\" : \"false\");\n");
				} else {
					builder.append("//Type " + actual.getName() + " is not handled in print action\n");
				}
			} else {
				builder.append("//Error in type detection\n");
			}
		}
		if (action.isLine()) builder.append("fprintf(stdout, \"\\n\");\n");
	}
	
    @Override
    public void generate(MCUExpression expression, StringBuilder builder, Context ctx) {
        CCompilerContext context = (CCompilerContext) ctx;
        if (expression.getCommand() instanceof PWMCommand) {
        	PWMCommand com = (PWMCommand)expression.getCommand();
        	final StringBuilder port = new StringBuilder();
        	final StringBuilder pin = new StringBuilder();
        	generate(com.getDev().getPort(), port, ctx);
        	generate(com.getDev().getPin(), pin, ctx);
        	if (com.getOp().getType().equals("start")) {
            	final StringBuilder duty = new StringBuilder();
            	final StringBuilder frequency = new StringBuilder();
            	generate(com.getOp().getDuty(), duty, ctx);
            	generate(com.getOp().getFrequency(), frequency, ctx);
        		builder.append("pwm_start(" + port.toString() + "," + pin.toString() + "," + duty.toString() + ")");
        	} else if (com.getOp().getType().equals("stop")) {
        		builder.append("pwm_stop(" + port.toString() + "," + pin.toString() + ")");
        	}
        } else if (expression.getCommand() instanceof GPIOCommand) {
        	GPIOCommand com = (GPIOCommand)expression.getCommand();
        	final StringBuilder port = new StringBuilder();
        	final StringBuilder pin = new StringBuilder();
        	generate(com.getDev().getPort(), port, ctx);
        	generate(com.getDev().getPin(), pin, ctx);
        	if (com.getOp().getType().equals("setmode")) {
        		if (com.getOp().getDir().equals("in")) {
        			builder.append("gpio_setmode(" + port.toString() + "," + pin.toString() + ",GPIO_DIR_IN)");
        		} else if (com.getOp().getDir().equals("out")) {
        			builder.append("gpio_setmode(" + port.toString() + "," + pin.toString() + ",GPIO_DIR_OUT)");
        		}
        	} else if (com.getOp().getType().equals("read")) {
        		builder.append("gpio_read(" + port.toString() + "," + pin.toString() + ")");
        	} else if (com.getOp().getType().equals("write")) {
        		final StringBuilder val = new StringBuilder();
            	generate(com.getOp().getValue(), val, ctx);
        		builder.append("gpio_write(" + port.toString() + "," + pin.toString() + "," + val.toString() + ")");
        	}
        } else if (expression.getCommand() instanceof ADCCommand) {
        	ADCCommand com = (ADCCommand)expression.getCommand();
        	final StringBuilder port = new StringBuilder();
        	final StringBuilder pin = new StringBuilder();
        	generate(com.getDev().getPort(), port, ctx);
        	generate(com.getDev().getPin(), pin, ctx);
        	if (com.getOp().getType().equals("read")) {
        		builder.append("adc_read(" + port.toString() + "," + pin.toString() + ")");
        	} else if (com.getOp().getType().equals("setref")) {
        		builder.append("adc_setref(ADC_REF_VDD)");
        	} 
        } else  {
            builder.append("/* unrecognised MCU expression here */");
        }
    }

}
