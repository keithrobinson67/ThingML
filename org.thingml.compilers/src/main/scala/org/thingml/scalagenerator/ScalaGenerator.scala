/**
 * Copyright (C) 2011 SINTEF <franck.fleurey@sintef.no>
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
/**
 * This code generator targets the SMAc Framework
 * see https://github.com/brice-morin/SMAc
 * @author: Brice MORIN <brice.morin@sintef.no>
 */
package org.thingml.scalagenerator

import org.thingml.scalagenerator.ScalaGenerator._
import org.sintef.thingml.constraints.ThingMLHelpers
import org.thingml.model.scalaimpl.ThingMLScalaImpl._
import org.sintef.thingml.resource.thingml.analysis.helper.CharacterEscaper
import scala.collection.JavaConversions._
import sun.applet.resources.MsgAppletViewer
import org.eclipse.emf.ecore.xml.`type`.internal.RegEx.Match
import java.util.{ArrayList, Hashtable}
import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile
import java.lang.StringBuilder
import org.sintef.thingml._

//TODO: better handle naming conventions to avoid duplicating code and ease the maintenance

object ScalaGenerator {
  
  var debug = true
  
  val keywords = scala.List("implicit","match","requires","type","var","abstract","do","finally","import","object","throw","val","case","else","for","lazy","override","return","trait","catch","extends","forSome","match","package","sealed","try","while","class","false","if","new","private","super","true","with","def","final","implicit","null","protected","this","yield","_",":","=","=>","<-","<:","<%",">:","#","@")
  val badChar = scala.List("_")
  def protectScalaKeyword(value : String) : String = {
    var returnString  = new StringBuilder
    if(/*badChar.exists({p=>e.contains(p)}) || */keywords.exists(p => p.equals(value))){
      returnString append "`"+value+"`"
    } else {
      returnString append value
    }
    
    return returnString.toString
  }

  def firstToUpper(value : String) : String = {
    var result = ""
    if (value.size > 0)
      result += value(0).toUpperCase 
    if (value.size > 1)
      result += value.substring(1, value.length)
    return result
  }

  def compileAllJava(model: ThingMLModel, pack : String): Hashtable[Configuration, String] = {
    val result = new Hashtable[Configuration, String]()
    compileAll(model, pack).foreach{entry =>
      result.put(entry._1, entry._2)
    }
    result
  }
  
  def compileAll(model: ThingMLModel, pack : String): Map[Configuration, String] = {
    var result = Map[Configuration, String]()
    model.allConfigurations.foreach {
      t => result += (t -> compile(t, pack))
    }
    result
  }

  def compile(t: Configuration, pack : String) = {
    var builder = new StringBuilder()
    generateHeader(builder, pack)
    t.generateScala(builder)
    builder.toString
  }
  
  def generateHeader(builder : StringBuilder, pack : String) = {
    builder append "/**\n"
    builder append " * File generated by the ThingML IDE\n"
    builder append " * /!\\Do not edit this file/!\\\n"
    builder append " * In case of a bug in the generated code,\n"
    builder append " * please submit an issue on our GitHub\n"
    builder append " **/\n\n"

    builder append "package " + pack + "\n"
    builder append "import org.sintef.smac._\n"
    builder append "import org.thingml.devices._\n"
  }

  implicit def scalaGeneratorAspect(self: Thing): ThingScalaGenerator = ThingScalaGenerator(self)

  implicit def scalaGeneratorAspect(self: Configuration): ConfigurationScalaGenerator = ConfigurationScalaGenerator(self)
  implicit def scalaGeneratorAspect(self: Instance): InstanceScalaGenerator = InstanceScalaGenerator(self)
  implicit def scalaGeneratorAspect(self: Connector): ConnectorScalaGenerator = ConnectorScalaGenerator(self)

  implicit def scalaGeneratorAspect(self: EnumerationLiteral): EnumerationLiteralScalaGenerator = EnumerationLiteralScalaGenerator(self)

  implicit def scalaGeneratorAspect(self: Variable): VariableScalaGenerator = VariableScalaGenerator(self)

  implicit def scalaGeneratorAspect(self: Type) = self match {
    case t: PrimitiveType => PrimitiveTypeScalaGenerator(t)
    case t: Enumeration => EnumerationScalaGenerator(t)
    case _ => TypeScalaGenerator(self)
  }
  
  implicit def scalaGeneratorAspect(self: TypedElement) = self match {
    case t: Function => FunctionScalaGenerator(t)
    case _ => TypedElementScalaGenerator(self)
  } 

  implicit def scalaGeneratorAspect(self: Handler) = self match {
    case h: Transition => TransitionScalaGenerator(h)
    case h: InternalTransition => InternalTransitionScalaGenerator(h)
  }  
    
  implicit def scalaGeneratorAspect(self: State) = self match {
    case s: StateMachine => StateMachineScalaGenerator(s)
    case s: CompositeState => CompositeStateScalaGenerator(s)
    case s: State => StateScalaGenerator(s)
  }
  
  implicit def scalaGeneratorAspect(self: Action) = self match {
    case a: SendAction => SendActionScalaGenerator(a)
    case a: VariableAssignment => VariableAssignmentScalaGenerator(a)
    case a: ActionBlock => ActionBlockScalaGenerator(a)
    case a: ExternStatement => ExternStatementScalaGenerator(a)
    case a: ConditionalAction => ConditionalActionScalaGenerator(a)
    case a: LoopAction => LoopActionScalaGenerator(a)
    case a: PrintAction => PrintActionScalaGenerator(a)
    case a: ErrorAction => ErrorActionScalaGenerator(a)
    case _ => ActionScalaGenerator(self)
  }

  implicit def scalaGeneratorAspect(self: Expression) = self match {
    case exp: OrExpression => OrExpressionScalaGenerator(exp)
    case exp: AndExpression => AndExpressionScalaGenerator(exp)
    case exp: LowerExpression => LowerExpressionScalaGenerator(exp)
    case exp: GreaterExpression => GreaterExpressionScalaGenerator(exp)
    case exp: EqualsExpression => EqualsExpressionScalaGenerator(exp)
    case exp: PlusExpression => PlusExpressionScalaGenerator(exp)
    case exp: MinusExpression => MinusExpressionScalaGenerator(exp)
    case exp: TimesExpression => TimesExpressionScalaGenerator(exp)
    case exp: DivExpression => DivExpressionScalaGenerator(exp)
    case exp: ModExpression => ModExpressionScalaGenerator(exp)
    case exp: UnaryMinus => UnaryMinusScalaGenerator(exp)
    case exp: NotExpression => NotExpressionScalaGenerator(exp)
    case exp: EventReference => EventReferenceScalaGenerator(exp)
    case exp: ExpressionGroup => ExpressionGroupScalaGenerator(exp)
    case exp: PropertyReference => PropertyReferenceScalaGenerator(exp)
    case exp: IntegerLiteral => IntegerLiteralScalaGenerator(exp)
    case exp: StringLiteral => StringLiteralScalaGenerator(exp)
    case exp: BooleanLiteral => BooleanLiteralScalaGenerator(exp)
    case exp: EnumLiteralRef => EnumLiteralRefScalaGenerator(exp)
    case exp: ExternExpression => ExternExpressionScalaGenerator(exp)
    case _ => ExpressionScalaGenerator(self)
  }
}

case class ThingMLScalaGenerator(self: ThingMLElement) {
  def generateScala(builder: StringBuilder) {
    // Implemented in the sub-classes
  }
}


case class ConfigurationScalaGenerator(override val self: Configuration) extends ThingMLScalaGenerator(self) {

  override def generateScala(builder: StringBuilder) {

    val model = ThingMLHelpers.findContainingModel(self)

    model.allSimpleTypes.filter{ t => t.isInstanceOf[Enumeration] }.foreach{ e =>
      e.generateScala(builder)
    }

    // Generate code for things which appear in the configuration
    
    model.allMessages.foreach{m =>
      builder append "object " + firstToUpper(m.getName) + "{ def getName = \"" + m.getName + "\" }\n" 
      builder append "case class " 
      messageDeclaration(m, builder)
      builder append " extends Event(name)\n"
    }
    
    
    self.allThings.foreach { thing =>
      thing.generateScala(builder)
    }

    builder append "\n"
    builder append "// Initialize instance variables and states\n"
    // Generate code to initialize variable for instances
    self.allInstances.foreach { inst =>
      inst.generateScala(builder)
    }

    generateScalaMain(builder);
  }
  
  def messageDeclaration(m : Message, builder : StringBuilder) {
    val nameParam = "override val name : String = " + firstToUpper(m.getName) + ".getName"
    val params = m.getParameters.collect{ case p => ScalaGenerator.protectScalaKeyword(p.getName) + " : " + p.getType.scala_type} += nameParam
    builder append firstToUpper(m.getName) + "("
    builder append params.mkString(", ")
    builder append ")"
  }

  def generateScalaMain(builder : StringBuilder) {
    builder append "object Main {\n\n"
    builder append "def main(args: Array[String]): Unit = {\n"
      
    builder append "//Connectors\n"
    var i : Int = 0
    self.allConnectors.foreach{ c =>
      builder append "val " + c.getName + "_" + i + " = new Channel\n"
      builder append c.getName + "_" + i + ".start\n"
      i = i + 1
    }
      
    builder append "//Things\n"
    self.allInstances.foreach{ i =>
      builder append "val " + i.getType.getName + "_" + i.getName + " = new " + firstToUpper(i.getType.getName) + "("
      var j = 0
      self.initExpressionsForInstance(i).foreach{p =>         
        if (j > 0)
          builder append ", "
        var result = p._1.scala_var_name + " = "
        if (p._2 != null) {
          var tempBuilder = new StringBuilder()
          p._2.generateScala(tempBuilder, i.getType)
          result = result + tempBuilder.toString
        } else {
          result = result + "null.asInstanceOf[" + p._1.getType.scala_type + "]"
        }
        builder append result
        j = j + 1
      }
      builder append ")\n"
    }
    
    builder append "//Bindings\n"
    i = 0
    self.allConnectors.foreach{ c =>
      c.getCli.getInstance().getType.allStateMachines.foreach{sm1 =>
        c.getSrv.getInstance().getType.allStateMachines.foreach{sm2 =>
          builder append c.getName + "_" + i + ".connect(\n" 
          builder append c.getCli.getInstance.getType.getName + "_" + c.getCli.getInstance.getName + ".getPort(\"" + c.getRequired.getName + "\").get,\n"
          builder append c.getSrv.getInstance.getType.getName + "_" + c.getSrv.getInstance.getName + ".getPort(\"" + c.getProvided.getName + "\").get\n"
          builder append")\n"
          builder append c.getName + "_" + i + ".connect(\n" 
          builder append c.getSrv.getInstance.getType.getName + "_" + c.getSrv.getInstance.getName + ".getPort(\"" + c.getProvided.getName + "\").get,\n"
          builder append c.getCli.getInstance.getType.getName + "_" + c.getCli.getInstance.getName + ".getPort(\"" + c.getRequired.getName + "\").get\n"
          builder append")\n\n"
        }
      }
      i = i + 1
    }
    
    builder append "//Starting Things\n"
    self.allInstances.foreach{ i =>
      builder append i.getType.getName + "_" + i.getName + ".asInstanceOf[Component].start\n"
    }
    
    builder append "}\n\n"
    builder append "}\n"
  }
}

case class InstanceScalaGenerator(override val self: Instance) extends ThingMLScalaGenerator(self) {

}

case class ConnectorScalaGenerator(override val self: Connector) extends ThingMLScalaGenerator(self) {

  override def generateScala(builder: StringBuilder) {
    // connect the handlers for messages with the sender
    // sender_listener = reveive_handler;
    self.getProvided.getSends.filter{m => self.getRequired.getReceives.contains(m)}.foreach { m =>

    }

    self.getRequired.getSends.filter{m => self.getProvided.getReceives.contains(m)}.foreach { m =>

    }
  }
}


case class ThingScalaGenerator(override val self: Thing) extends ThingMLScalaGenerator(self) {

  override def generateScala(builder: StringBuilder) {
    builder append "\n/**\n"
    builder append " * Definitions for type : " + self.getName + "\n"
    builder append " **/\n"
    
    var traits = ""
    if (self.hasAnnotation("scala_trait")) {
      traits = "with " + self.annotation("scala_trait")
    } else if (self.hasAnnotation("java_interface")) {
      traits = "with " + self.annotation("java_interface")
    }

    builder append "class " + firstToUpper(self.getName) + "("
    generateProperties(builder)
    builder append ") extends Component " + traits + "{\n\n"
    
    builder append "//Companion object\n"
    builder append "object " + firstToUpper(self.getName) + "{\n"
    self.allPorts.foreach{ p => 
      builder append "object " + p.getName + "Port{\n"
      builder append "def getName = \"" + p.getName + "\"\n"
      builder append "object in {\n" 
      p.getReceives.foreach{r =>
        builder append "val " + r.getName + " = " + firstToUpper(r.getName) + ".getName\n"
      }
      builder append "}\n"
      builder append "object out {\n" 
      p.getSends.foreach{s =>
        builder append "val " + s.getName + " = " + firstToUpper(s.getName) + ".getName\n"
      }
      builder append "}\n"
      builder append "}\n\n"
    }
    builder append "}\n\n"
    
       
    generatePortDef(builder)
    
    
    self.allFunctions.foreach{
      f => f.generateScala(builder, self)
    }
    
    if(self.hasAnnotation("scala_def")) {
      builder append self.annotation("scala_def")
    }    

    self.allStateMachines.foreach{b => 
      builder append "this.behavior ++= List("
      val hist = if (b.isHistory) "true" else "false"
      builder append "new " + firstToUpper(b.getName) + "StateMachine(" + hist + ", this).getBehavior)\n"
    }
    
    self.allStateMachines.foreach{b => 
      b.asInstanceOf[StateMachine].generateScala(builder, self)
    }
    
    builder append "}\n"
  }

  def generatePortDef(builder : StringBuilder) {
    self.allPorts.foreach{ p => 
      builder append "new Port(" + firstToUpper(self.getName) + "." + p.getName + "Port.getName, List(" + p.getReceives.collect{case r => firstToUpper(self.getName) + "." + p.getName + "Port.in." + r.getName}.mkString(", ").toString + "), List(" + p.getSends.collect{case s => firstToUpper(self.getName) + "." + p.getName + "Port.out." + s.getName}.mkString(", ").toString + "), this).start\n"
    }
  }
  
  def generateProperties(builder: StringBuilder) {
    builder append self.allPropertiesInDepth.collect{case p =>
        (if (p.isChangeable) "val " else "var ") + p.scala_var_name + " : " + p.getType.scala_type
    }.mkString(", ")
  }
}

case class VariableScalaGenerator(override val self: Variable) extends ThingMLScalaGenerator(self) {
  def scala_var_name = {
    self.qname("_") + "_var"
  }
}

case class EnumerationLiteralScalaGenerator(override val self: EnumerationLiteral) extends ThingMLScalaGenerator(self) {

  def enum_val: String = {
    self.getAnnotations.filter {
      a => a.getName == "enum_val"
    }.headOption match {
      case Some(a) => return a.asInstanceOf[PlatformAnnotation].getValue
      case None => {
          println("Warning: Missing annotation enum_val on litteral " + self.getName + " in enum " + self.eContainer().asInstanceOf[ThingMLElement].getName + ", will use default value 0.")
          return "0"
        }
    }
  }

  def scala_name = {
    self.eContainer().asInstanceOf[ThingMLElement].getName.toUpperCase + "_" + self.getName.toUpperCase
  }
}

case class HandlerScalaGenerator(override val self: Handler) extends ThingMLScalaGenerator(self) {
  
  def generateScala(builder: StringBuilder, thing : Thing){}
  
  def printGuard(builder : StringBuilder, thing : Thing) {
    if(self.getGuard != null){
      builder append "override def checkGuard() : Boolean = {\n"
      builder append "try {\n"
      self.getGuard.generateScala(builder, thing)
      builder append "}\ncatch {\n"
      builder append "case nse : java.util.NoSuchElementException => return false\n"
      builder append "case e : Exception => return false\n"
      builder append"}\n"
      builder append "\n}\n"
    }
  }
  
  def printAction(builder : StringBuilder, thing : Thing) {
    builder append "override def executeActions() = {\n"
    if (debug)
      builder append "println(this + \".executeActions\")\n"
    Option(self.getAction) match {
      case Some(a) =>
        self.getAction.generateScala(builder, thing)
      case None =>
        builder append "//No action defined for this transition\n"
        println("INFO: no action for transition "+self)
    }
    builder append "}\n\n"
  }
}

case class TransitionScalaGenerator(override val self: Transition) extends HandlerScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "case class Transition" + self.getSource.getName+"2"+self.getTarget.getName + "_" + self.hashCode + " extends TransitionAction {\n"
    
    printGuard(builder, thing)
    
    Option(self.getBefore) match {
      case Some(a) =>
        builder append "override def executeBeforeActions() = {\n"
        if (debug)
          builder append "println(this + \".executeBeforeActions\")\n"
        self.getBefore.generateScala(builder, thing)
        builder append "}\n\n"
      case None =>
        println("INFO: no before action for transition "+self)
    }
    printAction(builder, thing)
    Option(self.getAfter) match {
      case Some(a) =>
        builder append "override def executeAfterActions() = {\n"
        if (debug)
          builder append "println(this + \".executeAfterActions\")\n"
        self.getAfter.generateScala(builder, thing)
        builder append "}\n\n"
      case None =>
        println("INFO: no after action for transition "+self)
    }
    
    builder append "}\n"
  }
}

case class InternalTransitionScalaGenerator(override val self: InternalTransition) extends HandlerScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "case class InternalTransition" + self.hashCode + " extends InternalTransitionAction {\n"
    printGuard(builder, thing)
    printAction(builder, thing)
    builder append "}\n"
  }
}

//TODO avoid code duplication with CompositeState
case class StateMachineScalaGenerator(override val self: StateMachine) extends CompositeStateScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "case class " + firstToUpper(self.getName) + "StateMachine(keepHistory : Boolean, root : Component) extends StateAction {\n"
   
    builder append "def getBehavior = parent\n"
    builder append "val parent : StateMachine = new StateMachine(this, keepHistory, root)\n"
    
    generateActions(builder, thing)
    self.getInternal.foreach{t =>
      generateInternalTransitions(builder, t, thing)
    }
    generateInternalTransitions(builder, thing)
    generateSub(builder, thing, self)
    
    self.getRegion.foreach{r =>
      generateRegion(builder, r, thing)
    }
    
    builder append "}\n"
  }
}

case class StateScalaGenerator(override val self: State) extends ThingMLScalaGenerator(self) {
    
  def generateActions(builder : StringBuilder, thing : Thing) {
    builder append "override def onEntry() = {\n"
    if (debug)
      builder append "println(this + \".onEntry\")\n"
    Option(self.getEntry) match {
      case Some(a) =>  
        self.getEntry.generateScala(builder, thing)
      case None =>
        builder append "//No entry action defined for this state\n"
        println("INFO: no onEntry action for state "+self)
    }
    builder append "}\n\n"
    
    builder append "override def onExit() = {\n"
    if (debug)
      builder append "println(this + \".onExit\")\n"
    Option(self.getExit) match {
      case Some(a) =>  
        self.getExit.generateScala(builder, thing)
      case None =>
        builder append "//No exit action defined for this state\n"
        println("INFO: no onExit action for state "+self)
    }
    builder append "}\n\n"
  }
  
  def generateHandler(h : Handler, thing : Thing) : String = {
    var tempBuilder = new StringBuilder()
    tempBuilder append "List("
    tempBuilder append h.allTriggeringPorts.collect{case pair =>
        "(" + thing.getName + "." + pair._1.getName + "Port.getName, " + thing.getName + "." + pair._1.getName + "Port.in." + pair._2.getMessage.getName + ")"
    }.mkString(", ")
    tempBuilder append ")"
    return tempBuilder.toString
  }
  
  def generateInternalTransitions(builder : StringBuilder, t : InternalTransition, thing : Thing){
    builder append "val t_self_" + t.hashCode  + " = new InternalTransition(getBehavior, " + "new InternalTransition" + t.hashCode + "(), " + generateHandler(t, thing) + ")\n"
  }
  
  def generateInternalTransitions(builder : StringBuilder, thing : Thing) {
    self.getInternal.foreach{ t => 
      t.generateScala(builder, thing)
    }
  }
  
  def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "case class " + firstToUpper(self.getName) + "State extends StateAction {\n"
    
    generateActions(builder, thing)
    self.getInternal.foreach{t =>
      generateInternalTransitions(builder, t, thing)
    }
    generateInternalTransitions(builder, thing)

    builder append "}\n\n"
  }
}

case class CompositeStateScalaGenerator(override val self: CompositeState) extends StateScalaGenerator(self) {  
  
  def generateRegion(builder : StringBuilder, r : ParallelRegion, thing : Thing) {
    val history = if(r.isHistory) "true" else "false"
    builder append "parent.addRegion(new " + firstToUpper(r.getName) + "Region(" + history + ")" + ".getBehavior)\n"
    builder append "case class " + firstToUpper(r.getName) + "Region(keepHistory : Boolean) extends EmptyStateAction{\n"
   
    builder append "def getBehavior = parent\n"
    builder append "val parent : CompositeState = new CompositeState(this, keepHistory, root)\n"
    
    //r.getSubstate.foreach{sub => sub.generateScala(builder, thing)}
    
    generateSub(builder, thing, r)
    
    builder append "}\n"
  }
  
  def generateSub(builder : StringBuilder, thing : Thing, r : Region) {
    if (r.getSubstate.size > 0)
      builder append "//create sub-states\n"
    r.getSubstate.foreach{ sub =>  
      sub match {
        case cs : CompositeState =>  
          val history = if(cs.isHistory) "true" else "false"
          builder append "val " + cs.getName + "_state = new " + firstToUpper(cs.getName) + "State(" + history + ", root).getBehavior\n"
        case s : State =>
          builder append "val " + s.getName + "_state = new State(" + firstToUpper(s.getName) + "State(), root)\n"
        case _ => 
          builder append "//Warning: Unknown type of State... "+sub
          println("Warning: Unknown type of State... "+sub)
      }
      builder append "parent.addSubState(" + sub.getName + "_state" + ")\n"
      
      /*sub.getInternal.foreach{t => 
       generateInternalTransitions(builder, t, thing)
       }*/
      sub.generateScala(builder, thing)
    }
    builder append "parent.setInitial(" + r.getInitial.getName + "_state" + ")\n\n"
    
    if (r.getSubstate.size > 0)
      builder append "//create transitions among sub-states\n"
    
    r.getSubstate.foreach{sub => sub.getOutgoing.foreach{ t => 
        builder append "val t_" + t.getSource.getName+"2"+t.getTarget.getName + "_" + t.hashCode  + " = new Transition(" + t.getSource.getName + "_state, " + t.getTarget.getName + "_state, " + "Transition" + t.getSource.getName+"2"+t.getTarget.getName + "_" + t.hashCode + "(), " + generateHandler(t, thing) + ")\n"
        //printEvents(t, builder)
        builder append "parent.addTransition(t_" + t.getSource.getName+"2"+t.getTarget.getName+ "_" + t.hashCode + ")\n"
      }
    }
    
    r.getSubstate.foreach{sub => 
      //sub.generateInternalTransitions(builder, thing)
      sub.getOutgoing.foreach{ t => 
        t.generateScala(builder, thing)
      }
    }
  }
  
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "case class " + firstToUpper(self.getName) + "State(keepHistory : Boolean, root : Component) extends StateAction {\n"
    
    builder append "def getBehavior = parent\n"
    builder append "val parent : CompositeState = new CompositeState(this, keepHistory, root)\n"
    
    generateActions(builder, thing)
    self.getInternal.foreach{t =>
      generateInternalTransitions(builder, t, thing)
    }
    generateInternalTransitions(builder, thing)
    generateSub(builder, thing, self)
    //generateInternalTransitions(builder)
    self.getRegion.foreach{r =>
      generateRegion(builder, r, thing)
    }

    builder append "}\n\n"
  }
}

case class TypedElementScalaGenerator(val self: TypedElement) /*extends ThingMLScalaGenerator(self)*/ {
  def generateScala(builder: StringBuilder, thing : Thing) {
    // Implemented in the sub-classes
  }
}
  
  

case class FunctionScalaGenerator(override val self: Function) extends TypedElementScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getAnnotations.filter {
      a => a.getName == "override"
    }.headOption match {
      case Some(a) => 
        builder append "override "
      case None =>
    }
      
    builder append "def " + self.getName + "(" + self.getParameters.collect{ case p => ScalaGenerator.protectScalaKeyword(p.scala_var_name) + " : " + p.getType.scala_type}.mkString(", ") + ") : " + self.getType.scala_type + " = {\n"
    builder append "val handler = this\n" 
    self.getBody.generateScala(builder, thing)
    builder append "}\n"
  }
}
  
    
/**
 * Type abstract class
 */

case class TypeScalaGenerator(override val self: Type) extends ThingMLScalaGenerator(self) {
  override def generateScala(builder: StringBuilder) {
    // Implemented in the sub-classes
  }

  def generateScala_TypeRef(builder: StringBuilder) = {
    scala_type
  }

  def scala_type(): String = {
    self.getAnnotations.filter {
      a => a.getName == "scala_type"
    }.headOption match {
      case Some(a) => 
        var res : String = a.asInstanceOf[PlatformAnnotation].getValue
        //res = res(0).toUpperCase + res.substring(1, res.length)
        return res
      case None => 
        self.getAnnotations.filter {
          a => a.getName == "java_type"
        }.headOption match {
          case Some(a) => 
            var res : String = a.asInstanceOf[PlatformAnnotation].getValue
            //res = res(0).toUpperCase + res.substring(1, res.length)
            return res
          case None =>
            println("Warning: Missing annotation java_type or scala_type for type " + self.getName + ", using " + self.getName + " as the Java/Scala type.")
            var res : String = self.getName
            res = res(0).toUpperCase + res.substring(1, res.length)
            return res
        }
        
    }
  }
}

/**
 * code generation for the definition of ThingML Types
 */

case class PrimitiveTypeScalaGenerator(override val self: PrimitiveType) extends TypeScalaGenerator(self) {
  override def generateScala(builder: StringBuilder) {
    builder append "// ThingML type " + self.getName + " is mapped to " + scala_type + "\n"
  }
}

case class EnumerationScalaGenerator(override val self: Enumeration) extends TypeScalaGenerator(self) {
  override def generateScala(builder: StringBuilder) {
    builder append "// Definition of Enumeration  " + self.getName + "\n"
    builder append "object " + firstToUpper(self.getName) + "_ENUM extends Enumeration {\n"
    builder append "\ttype " + firstToUpper(self.getName) + "_ENUM = " + scala_type + "\n"
    self.getLiterals.foreach {
      l => builder append "val " + l.scala_name + " : " + scala_type + " = " + l.enum_val +"\n"
    }
    builder append "}\n"
  }
}

/**
 * Action abstract class
 */
case class ActionScalaGenerator(val self: Action) /*extends ThingMLScalaGenerator(self)*/ {
  def generateScala(builder: StringBuilder, thing : Thing) {
    // Implemented in the sub-classes
  }
}

/**
 * All Action concrete classes
 */

case class SendActionScalaGenerator(override val self: SendAction) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "handler.getPort(\"" + self.getPort.getName + "\") match{\n"
    builder append "case Some(p) => p.send("
    concreteMsg(builder, thing)
    builder append ")\n"
    builder append "case None => println(\"Warning: no port " + self.getPort.getName + " You may consider revising your ThingML model.\")\n"
    builder append "}\n"
  }
  
  def concreteMsg(builder : StringBuilder, thing : Thing) {
    builder append "new " + firstToUpper(self.getMessage.getName) + "("
    var i = 0
    self.getParameters.foreach{ p =>
      if (i > 0)
        builder append ", "
      p.generateScala(builder, thing)
      i = i+1
    }
    builder append ")"
  }
}

case class VariableAssignmentScalaGenerator(override val self: VariableAssignment) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append(self.getProperty.scala_var_name)
    builder append " = "
    self.getExpression.generateScala(builder, thing)
    builder append "\n"
  }
}

case class ActionBlockScalaGenerator(override val self: ActionBlock) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    //builder append "{\n"
    self.getActions.foreach {
      a => a.generateScala(builder, thing)
      //builder append "\n"
    }
    //builder append "}\n"
  }
}

case class ExternStatementScalaGenerator(override val self: ExternStatement) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    //builder append "/*"
    builder.append(self.getStatement)
    self.getSegments.foreach {
      e => e.generateScala(builder, thing)
    }
    //builder append "*/"
    builder append "\n"
  }
}

case class ConditionalActionScalaGenerator(override val self: ConditionalAction) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "if("
    self.getCondition.generateScala(builder, thing)
    builder append ") {\n"
    self.getAction.generateScala(builder, thing)
    builder append "\n}\n"
  }
}

case class LoopActionScalaGenerator(override val self: LoopAction) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "while("
    self.getCondition.generateScala(builder, thing)
    builder append ") "
    self.getAction.generateScala(builder, thing)
  }
}

case class PrintActionScalaGenerator(override val self: PrintAction) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "println( "
    self.getMsg.generateScala(builder, thing)
    builder append ")\n"
  }
}

case class ErrorActionScalaGenerator(override val self: ErrorAction) extends ActionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "println(ERROR: "
    self.getMsg.generateScala(builder, thing)
    builder append ")\n"
  }
}

/**
 * Expression abstract classes
 */

case class ExpressionScalaGenerator(val self: Expression) /*extends ThingMLScalaGenerator(self)*/ {
  def generateScala(builder: StringBuilder, thing : Thing) {
    // Implemented in the sub-classes
  }
}

/**
 * All Expression concrete classes
 */

case class OrExpressionScalaGenerator(override val self: OrExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " || "
    self.getRhs.generateScala(builder, thing)
  }
}

case class AndExpressionScalaGenerator(override val self: AndExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " && "
    self.getRhs.generateScala(builder, thing)
  }
}

case class LowerExpressionScalaGenerator(override val self: LowerExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " < "
    self.getRhs.generateScala(builder, thing)
  }
}

case class GreaterExpressionScalaGenerator(override val self: GreaterExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " > "
    self.getRhs.generateScala(builder, thing)
  }
}

case class EqualsExpressionScalaGenerator(override val self: EqualsExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " == "
    self.getRhs.generateScala(builder, thing)
  }
}

case class PlusExpressionScalaGenerator(override val self: PlusExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " + "
    self.getRhs.generateScala(builder, thing)
  }
}

case class MinusExpressionScalaGenerator(override val self: MinusExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " - "
    self.getRhs.generateScala(builder, thing)
  }
}

case class TimesExpressionScalaGenerator(override val self: TimesExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " * "
    self.getRhs.generateScala(builder, thing)
  }
}

case class DivExpressionScalaGenerator(override val self: DivExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " / "
    self.getRhs.generateScala(builder, thing)
  }
}

case class ModExpressionScalaGenerator(override val self: ModExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    self.getLhs.generateScala(builder, thing)
    builder append " % "
    self.getRhs.generateScala(builder, thing)
  }
}

case class UnaryMinusScalaGenerator(override val self: UnaryMinus) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append " -"
    self.getTerm.generateScala(builder, thing)
  }
}

case class NotExpressionScalaGenerator(override val self: NotExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append " !"
    self.getTerm.generateScala(builder, thing)
  }
}

case class EventReferenceScalaGenerator(override val self: EventReference) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    //TODO: this could cause a null pointer if trying to get an event that does not exists... but this should be checked in the model ;-)
    //if not, it would be possible to generate a match Some/None to properly handle this...
    builder append "getEvent(" 
    builder append firstToUpper(thing.getName) + "." + self.getMsgRef.getPort.getName + "Port.in." + self.getMsgRef.getMessage.getName + ", " + thing.getName + "." + self.getMsgRef.getPort.getName + "Port.getName).get.asInstanceOf[" + firstToUpper(self.getMsgRef.getMessage.getName) + "]." + protectScalaKeyword(self.getParamRef.getName)
  }
}

case class ExpressionGroupScalaGenerator(override val self: ExpressionGroup) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder append "("
    self.getExp.generateScala(builder, thing)
    builder append ")"
  }
}

case class PropertyReferenceScalaGenerator(override val self: PropertyReference) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append(self.getProperty.qname("_") + "_var")
  }
}

case class IntegerLiteralScalaGenerator(override val self: IntegerLiteral) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append(self.getIntValue.toString)
  }
}

case class StringLiteralScalaGenerator(override val self: StringLiteral) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append("\"" + CharacterEscaper.escapeEscapedCharacters(self.getStringValue) + "\"")
  }
}

case class BooleanLiteralScalaGenerator(override val self: BooleanLiteral) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append(if (self.isBoolValue) "true" else "false")
  }
}

case class EnumLiteralRefScalaGenerator(override val self: EnumLiteralRef) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append(firstToUpper(self.getEnum.getName) + "_ENUM." + self.getLiteral.scala_name)
  }
}

case class ExternExpressionScalaGenerator(override val self: ExternExpression) extends ExpressionScalaGenerator(self) {
  override def generateScala(builder: StringBuilder, thing : Thing) {
    builder.append(self.getExpression)
    self.getSegments.foreach {
      e => e.generateScala(builder, thing)
    }
  }
}

